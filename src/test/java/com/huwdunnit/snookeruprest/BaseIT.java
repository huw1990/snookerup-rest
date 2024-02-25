package com.huwdunnit.snookeruprest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huwdunnit.snookeruprest.db.RoutineRepository;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.db.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Base abstract class for all integration tests. Creates a MongoDB container using Testcontainers, to manage data used
 * in the tests.
 *
 * @author Huwdunnit
 */
@Testcontainers
@DirtiesContext
public abstract class BaseIT {

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
                .build();
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        routineRepository.deleteAll();
        scoreRepository.deleteAll();
    }
}
