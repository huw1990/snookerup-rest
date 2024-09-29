package com.huwdunnit.snookeruprest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huwdunnit.snookeruprest.db.RoutineRepository;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.db.UserRepository;
import com.huwdunnit.snookeruprest.model.Balls;
import com.huwdunnit.snookeruprest.model.Routine;
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

import java.util.List;
import java.util.stream.IntStream;

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
    private static final String RONNIE_PASSWORD = "ronnie-pw";

    private static final String HENDRY_EMAIL = "hendry@example.com";
    private static final String HENDRY_FIRST_NAME = "Stephen";

    private static final String HENDRY_LAST_NAME = "Hendry";
    private static final String HENDRY_PASSWORD = "hendry-pw";

    private static final String WILLO_EMAIL = "willo@example.com";
    private static final String WILLO_FIRST_NAME = "Mark";

    private static final String WILLO_LAST_NAME = "Williams";
    private static final String WILLO_PASSWORD = "mark-pw";

    private static final String LINEUP_TITLE = "The Line Up";
    private static final String LINEUP_DESC_LINE_1 = "Arrange all reds in a line up the middle of the table, in line with the blue, pink, and black spots.";
    private static final String LINEUP_DESC_LINE_2 = "Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.";
    private static final String LINEUP_DESC_LINE_3 = "Can you clear the table?";

    private static final String T_LINEUP_TITLE = "The T Line Up";
    private static final String T_LINEUP_DESC_LINE_1 = "Arrange the reds in three lines of five reds, first between pink and black, then either side of the pink, to form a \"T\" shape.";
    private static final String T_LINEUP_DESC_LINE_2 = "Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.";
    private static final String T_LINEUP_DESC_LINE_3 = "In this routine, all reds are nearer the pink and black, so this replicates what you might see in a match, more than the Line Up would.";
    private static final String T_LINEUP_DESC_LINE_4 = "Can you clear the table?";

    private static final String CLEARING_COLOURS_TITLE = "Clearing the Colours";
    private static final String CLEARING_COLOURS_DESC = "Put all colours on their spots, then try to clear them in order, i.e. yellow, green, brown, blue, pink, black.";
    private static final String TAG_BREAK_BUILDING = "break-building";
    private static final String TAG_POSITION = "positional-play";
    private static final String TAG_CUSTOM = "custom-tag-1";
    private static final String REDS_UNIT = "reds";
    private static final String ALL_COLOURS = "all";
    private static final String JUST_BLACK_COLOUR = "black";
    private static final String JUST_PINK_COLOUR = "pink";
    private static final String PINK_AND_BLACK_COLOURS = "pink,black";
    private static final String IMAGE_PATH_1 = "/path/to/image/1";
    private static final String IMAGE_PATH_2 = "/path/to/image/2";

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
        return createUser(RONNIE_FIRST_NAME, RONNIE_LAST_NAME, RONNIE_EMAIL, RONNIE_PASSWORD);
    }

    protected User getHendryUser() {
        return createUser(HENDRY_FIRST_NAME, HENDRY_LAST_NAME, HENDRY_EMAIL, HENDRY_PASSWORD);
    }

    protected User getWilloUser() {
        return createUser(WILLO_FIRST_NAME, WILLO_LAST_NAME, WILLO_EMAIL, WILLO_PASSWORD);
    }

    private User createUser(String firstName, String lastName, String email, String password) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
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

    protected Routine getLineUpRoutine() {
        return Routine.builder()
                .title(LINEUP_TITLE)
                .description(List.of(LINEUP_DESC_LINE_1, LINEUP_DESC_LINE_2, LINEUP_DESC_LINE_3))
                .tags(List.of(TAG_BREAK_BUILDING, TAG_POSITION))
                .balls(Balls.builder()
                        .options(IntStream.rangeClosed(1, 15).boxed().toList())
                        .unit(REDS_UNIT)
                        .build())
                .cushionLimits(List.of(0, 3, 5, 7))
                .colours(List.of(ALL_COLOURS, JUST_BLACK_COLOUR))
                .images(List.of(IMAGE_PATH_1, IMAGE_PATH_2))
                .build();
    }

    protected Routine getTLineUpRoutine() {
        return Routine.builder()
                .title(T_LINEUP_TITLE)
                .description(List.of(T_LINEUP_DESC_LINE_1, T_LINEUP_DESC_LINE_2, T_LINEUP_DESC_LINE_3, T_LINEUP_DESC_LINE_4))
                .tags(List.of(TAG_BREAK_BUILDING, TAG_POSITION))
                .balls(Balls.builder()
                        .options(IntStream.rangeClosed(3, 15).boxed().toList())
                        .unit(REDS_UNIT)
                        .build())
                .cushionLimits(List.of(0, 3, 5, 7))
                .colours(List.of(ALL_COLOURS, JUST_BLACK_COLOUR, JUST_PINK_COLOUR, PINK_AND_BLACK_COLOURS))
                .images(List.of(IMAGE_PATH_1, IMAGE_PATH_2))
                .build();
    }

    protected Routine getClearingTheColoursRoutine() {
        return Routine.builder()
                .title(CLEARING_COLOURS_TITLE)
                .description(List.of(CLEARING_COLOURS_DESC))
                .tags(List.of(TAG_CUSTOM, TAG_POSITION))
                .cushionLimits(List.of(0, 3, 5, 7))
                .images(List.of(IMAGE_PATH_1))
                .canLoop(true)
                .build();
    }
}
