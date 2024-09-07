package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.BaseIT;
import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.model.Routine;
import com.huwdunnit.snookeruprest.security.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the RoutineController class.
 *
 * @author Huwdunnit
 */
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class RoutineControllerTestsIT extends BaseIT {

    private static final String LINEUP_TITLE = "The Line Up";
    private static final String LINEUP_DESC = """
            Arrange all reds in a line up the middle of the table, in line with the blue, pink, and black spots.
            
            Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.
            
            Can you clear the table?""";

    private static final String T_LINEUP_TITLE = "The T Line Up";
    private static final String T_LINEUP_DESC = """
            Arrange the reds in three lines of five reds, first between pink and black, then either side of the pink, to form a "T" shape.
            
            Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.
            
            In this routine, all reds are nearer the pink and black, so this replicates what you might see in a match, more than the Line Up would.
            
            Can you clear the table?""";

    private static final String CLEARING_COLOURS_TITLE = "Clearing the Colours";
    private static final String CLEARING_COLOURS_DESC = """
            Put all colours on their spots, then try to clear them in order, i.e. yellow, green, brown, blue, pink, black.""";
    private static final String TAG_BEGINNER = "beginner";
    private static final String TAG_INTER = "intermediate";
    private static final String TAG_BREAK_BUILDING = "break-building";
    private static final String TAG_POSITION = "positional-play";

    @Test
    void addRoutine_Should_Return401_When_NoAuthProvided() throws Exception {
        Routine routineToAdd = getLineUpRoutine();
        String requestBody = objectMapper.writeValueAsString(routineToAdd);

        // Get the first page of users
        mockMvc.perform(post("/api/v1/routines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void addRoutine_Should_Return201ResponseWithAddedRoutine_When_ReqMadeByAdmin() throws Exception {
        Routine routineToAdd = getLineUpRoutine();
        String requestBody = objectMapper.writeValueAsString(routineToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/routines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.title").value(routineToAdd.getTitle()),
                        jsonPath("$.description").value(routineToAdd.getDescription()),
                        jsonPath("$.tags.length()").value(3),
                        jsonPath("$.tags[0]").value(routineToAdd.getTags().get(0)),
                        jsonPath("$.tags[1]").value(routineToAdd.getTags().get(1)),
                        jsonPath("$.tags[2]").value(routineToAdd.getTags().get(2)))
                .andReturn();

        // Get the routine's ID so we can check it exists in the DB
        Routine routineInResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Routine.class);
        String addedRoutineId = routineInResponse.getId();

        // Get the routine by ID from the DB.
        Optional<Routine> opt = routineRepository.findById(addedRoutineId);

        opt.ifPresentOrElse(
                (scoreInDb) -> assertEquals(routineInResponse, scoreInDb, "Routine returned in response is different to routine in DB"),
                () -> fail("Routine with ID from response not found in the DB")
        );
    }

    @Test
    void getRoutines_Should_EmptyRoutinesPage_When_NoRoutinesInDbAndNoAuth() throws Exception {
        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 0;
        int expectedTotalItems = 0;

        // Get the first page of routines
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines").isEmpty())
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getRoutines_Should_RoutinesInOnePage_When_OnlyTwoRoutinesInDbAndNoAuth() throws Exception {
        // Add routines to DB before running test
        Routine lineUpInDb = getLineUpRoutine();
        lineUpInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(lineUpInDb);
        Routine tLineUpInDb = getTLineUpRoutine();
        tLineUpInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(tLineUpInDb);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 2;

        // Get the first page of routines
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(lineUpInDb.getId()),
                        jsonPath("$.routines[0].title").value(lineUpInDb.getTitle()),
                        jsonPath("$.routines[0].description").value(lineUpInDb.getDescription()),
                        jsonPath("$.routines[0].tags.length()").value(3),
                        jsonPath("$.routines[0].tags[0]").value(lineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[0].tags[1]").value(lineUpInDb.getTags().get(1)),
                        jsonPath("$.routines[0].tags[2]").value(lineUpInDb.getTags().get(2)))
                .andExpectAll(
                        jsonPath("$.routines[1].id").value(tLineUpInDb.getId()),
                        jsonPath("$.routines[1].title").value(tLineUpInDb.getTitle()),
                        jsonPath("$.routines[1].description").value(tLineUpInDb.getDescription()),
                        jsonPath("$.routines[1].tags.length()").value(3),
                        jsonPath("$.routines[1].tags[0]").value(tLineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[1].tags[1]").value(tLineUpInDb.getTags().get(1)),
                        jsonPath("$.routines[1].tags[2]").value(tLineUpInDb.getTags().get(2)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getRoutines_Should_RoutinesInTwoPages_When_RequestedPagesOfTwoButThreeRoutinesInDbAndNoAuth() throws Exception {
        // Add routines to DB before running test
        Routine lineUpInDb = getLineUpRoutine();
        lineUpInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(lineUpInDb);
        Routine tLineUpInDb = getTLineUpRoutine();
        tLineUpInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(tLineUpInDb);
        Routine clearingColoursInDb = getClearingTheColoursRoutine();
        clearingColoursInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(clearingColoursInDb);

        int pageSize = 2;
        int pageToGet = 0;
        int expectedNumberOfPages = 2;
        int expectedTotalItems = 3;

        // Get the first page of routines
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(lineUpInDb.getId()),
                        jsonPath("$.routines[0].title").value(lineUpInDb.getTitle()),
                        jsonPath("$.routines[0].description").value(lineUpInDb.getDescription()),
                        jsonPath("$.routines[0].tags.length()").value(3),
                        jsonPath("$.routines[0].tags[0]").value(lineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[0].tags[1]").value(lineUpInDb.getTags().get(1)),
                        jsonPath("$.routines[0].tags[2]").value(lineUpInDb.getTags().get(2)))
                .andExpectAll(
                        jsonPath("$.routines[1].id").value(tLineUpInDb.getId()),
                        jsonPath("$.routines[1].title").value(tLineUpInDb.getTitle()),
                        jsonPath("$.routines[1].description").value(tLineUpInDb.getDescription()),
                        jsonPath("$.routines[1].tags.length()").value(3),
                        jsonPath("$.routines[1].tags[0]").value(tLineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[1].tags[1]").value(tLineUpInDb.getTags().get(1)),
                        jsonPath("$.routines[1].tags[2]").value(tLineUpInDb.getTags().get(2)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));


        pageToGet = 1;
        // Get the second page of routines
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(clearingColoursInDb.getId()),
                        jsonPath("$.routines[0].title").value(clearingColoursInDb.getTitle()),
                        jsonPath("$.routines[0].description").value(clearingColoursInDb.getDescription()),
                        jsonPath("$.routines[0].tags.length()").value(2),
                        jsonPath("$.routines[0].tags[0]").value(clearingColoursInDb.getTags().get(0)),
                        jsonPath("$.routines[0].tags[1]").value(clearingColoursInDb.getTags().get(1)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getRoutines_Should_ReturnOneRoutine_When_OnlyOneRoutineWithRequestedTagAndNoAuth() throws Exception {
        // Add routines to DB before running test
        Routine lineUpInDb = getLineUpRoutine();
        lineUpInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(lineUpInDb);
        Routine tLineUpInDb = getTLineUpRoutine();
        tLineUpInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(tLineUpInDb);
        Routine clearingColoursInDb = getClearingTheColoursRoutine();
        clearingColoursInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(clearingColoursInDb);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of routines
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}&tags=beginner",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(clearingColoursInDb.getId()),
                        jsonPath("$.routines[0].title").value(clearingColoursInDb.getTitle()),
                        jsonPath("$.routines[0].description").value(clearingColoursInDb.getDescription()),
                        jsonPath("$.routines[0].tags.length()").value(2),
                        jsonPath("$.routines[0].tags[0]").value(clearingColoursInDb.getTags().get(0)),
                        jsonPath("$.routines[0].tags[1]").value(clearingColoursInDb.getTags().get(1)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getRoutines_Should_ReturnThreeRoutines_When_MultipleTagsRequestedAndNoAuth() throws Exception {
        // Add routines to DB before running test
        Routine lineUpInDb = getLineUpRoutine();
        lineUpInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(lineUpInDb);
        Routine tLineUpInDb = getTLineUpRoutine();
        tLineUpInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(tLineUpInDb);
        Routine clearingColoursInDb = getClearingTheColoursRoutine();
        clearingColoursInDb.setId(IdGenerator.createNewId());
        routineRepository.insert(clearingColoursInDb);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 3;

        // Get the first page of routines
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}&tags=beginner,intermediate",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(lineUpInDb.getId()),
                        jsonPath("$.routines[0].title").value(lineUpInDb.getTitle()),
                        jsonPath("$.routines[0].description").value(lineUpInDb.getDescription()),
                        jsonPath("$.routines[0].tags.length()").value(3),
                        jsonPath("$.routines[0].tags[0]").value(lineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[0].tags[1]").value(lineUpInDb.getTags().get(1)),
                        jsonPath("$.routines[0].tags[2]").value(lineUpInDb.getTags().get(2)))
                .andExpectAll(
                        jsonPath("$.routines[1].id").value(tLineUpInDb.getId()),
                        jsonPath("$.routines[1].title").value(tLineUpInDb.getTitle()),
                        jsonPath("$.routines[1].description").value(tLineUpInDb.getDescription()),
                        jsonPath("$.routines[1].tags.length()").value(3),
                        jsonPath("$.routines[1].tags[0]").value(tLineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[1].tags[1]").value(tLineUpInDb.getTags().get(1)),
                        jsonPath("$.routines[1].tags[2]").value(tLineUpInDb.getTags().get(2)))
                .andExpectAll(
                        jsonPath("$.routines[2].id").value(clearingColoursInDb.getId()),
                        jsonPath("$.routines[2].title").value(clearingColoursInDb.getTitle()),
                        jsonPath("$.routines[2].description").value(clearingColoursInDb.getDescription()),
                        jsonPath("$.routines[2].tags.length()").value(2),
                        jsonPath("$.routines[2].tags[0]").value(clearingColoursInDb.getTags().get(0)),
                        jsonPath("$.routines[2].tags[1]").value(clearingColoursInDb.getTags().get(1)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getRoutineById_Should_Return200ResponseWithRoutine_When_RoutineExistsAndNoAuth() throws Exception {
        String routineId = "1234";
        Routine lineUpInDb = getLineUpRoutine();
        lineUpInDb.setId(routineId);
        routineRepository.insert(lineUpInDb);

        mockMvc.perform(get("/api/v1/routines/{routine-id}", routineId))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(lineUpInDb.getId()),
                        jsonPath("$.title").value(lineUpInDb.getTitle()),
                        jsonPath("$.description").value(lineUpInDb.getDescription()),
                        jsonPath("$.tags.length()").value(3),
                        jsonPath("$.tags[0]").value(lineUpInDb.getTags().get(0)),
                        jsonPath("$.tags[1]").value(lineUpInDb.getTags().get(1)),
                        jsonPath("$.tags[2]").value(lineUpInDb.getTags().get(2)));
    }

    @Test
    void getRoutineById_Should_Return404Response_When_RoutineNotFoundAndNoAuth() throws Exception {
        String invalidRoutineId = "1234";

        mockMvc.perform(get("/api/v1/routines/{routine-id}", invalidRoutineId))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Routine not found"));
    }

    private Routine getLineUpRoutine() {
        return createRoutine(LINEUP_TITLE, LINEUP_DESC, List.of(TAG_INTER, TAG_BREAK_BUILDING, TAG_POSITION));
    }

    private Routine getTLineUpRoutine() {
        return createRoutine(T_LINEUP_TITLE, T_LINEUP_DESC, List.of(TAG_INTER, TAG_BREAK_BUILDING, TAG_POSITION));
    }

    private Routine getClearingTheColoursRoutine() {
        return createRoutine(CLEARING_COLOURS_TITLE, CLEARING_COLOURS_DESC, List.of(TAG_BEGINNER, TAG_POSITION));
    }

    private Routine createRoutine(String title, String description, List<String> tags) {
        Routine routine = new Routine();
        routine.setTitle(title);
        routine.setDescription(description);
        routine.setTags(tags);
        return routine;
    }
}
