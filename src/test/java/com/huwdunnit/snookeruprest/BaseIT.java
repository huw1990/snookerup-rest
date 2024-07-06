package com.huwdunnit.snookeruprest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huwdunnit.snookeruprest.db.RoutineRepository;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.db.UserRepository;
import com.huwdunnit.snookeruprest.model.User;
import com.huwdunnit.snookeruprest.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Base abstract class for all integration tests. Creates a MongoDB container using Testcontainers, to manage data used
 * in the tests.
 *
 * @author Huwdunnit
 */
@Testcontainers
@DirtiesContext
public abstract class BaseIT {

    private static final String RONNIE_EMAIL = "ronnieo@example.com";
    private static final String RONNIE_FIRST_NAME = "Ronnie";

    private static final String RONNIE_LAST_NAME = "O'Sullivan";

    private static final String HENDRY_EMAIL = "hendry@example.com";
    private static final String HENDRY_FIRST_NAME = "Stephen";

    private static final String HENDRY_LAST_NAME = "Hendry";

    private static final String WILLO_EMAIL = "willo@example.com";
    private static final String WILLO_FIRST_NAME = "Mark";

    private static final String WILLO_LAST_NAME = "Williams";

    @Container
    protected static GenericContainer<?> MONGODB_CONTAINER = new GenericContainer<>(DockerImageName.parse("mongo"))
            .withExposedPorts(27017)
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "testing")
            .withEnv("MONGO_INITDB_DATABASE", "snookerup");

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoutineRepository routineRepository;

    @Autowired
    protected ScoreRepository scoreRepository;

    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        // Testcontainers randomly selects ports, so get these on the running container and update app properties accordingly.
        registry.add("spring.data.mongodb.host", MONGODB_CONTAINER::getHost);
        registry.add("spring.data.mongodb.port", MONGODB_CONTAINER::getFirstMappedPort);
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        routineRepository.deleteAll();
        scoreRepository.deleteAll();
    }

    protected User getRonnieUser() {
        return createUser(RONNIE_FIRST_NAME, RONNIE_LAST_NAME, RONNIE_EMAIL);
    }

    protected User getHendryUser() {
        return createUser(HENDRY_FIRST_NAME, HENDRY_LAST_NAME, HENDRY_EMAIL);
    }

    protected User getWilloUser() {
        return createUser(WILLO_FIRST_NAME, WILLO_LAST_NAME, WILLO_EMAIL);
    }

    private User createUser(String firstName, String lastName, String email) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        return user;
    }

    /**
     * Convert a user from the DB into a Spring Scurity user, which we can use to auth with "with(user(X))" on WebMvc
     * invocations.
     * @param user The DB user
     * @return The converted UserPrincipal with values from the DB user
     */
    protected UserPrincipal getPrincipalForUser(User user) {
        return new UserPrincipal(user);
    }
}
