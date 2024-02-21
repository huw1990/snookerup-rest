package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.BaseIT;
import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.model.Routine;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void getAllRoutines_Should_EmptyUsersPage_When_NoRoutinesInDb() throws Exception {
        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 0;
        int expectedTotalItems = 0;

        // Get the first page of users
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
    void getAllRoutines_Should_RoutinesInOnePage_When_OnlyTwoRoutinesInDb() throws Exception {
        // Add users to DB before running test
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

        // Get the first page of users
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(lineUpInDb.getId()),
                        jsonPath("$.routines[0].title").value(lineUpInDb.getTitle()),
                        jsonPath("$.routines[0].description").value(lineUpInDb.getDescription()))
                .andExpectAll(
                        jsonPath("$.routines[1].id").value(tLineUpInDb.getId()),
                        jsonPath("$.routines[1].title").value(tLineUpInDb.getTitle()),
                        jsonPath("$.routines[1].description").value(tLineUpInDb.getDescription()))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getAllRoutines_Should_RoutinesInTwoPages_When_RequestedPagesOfTwoButThreeRoutinesInDb() throws Exception {
        // Add users to DB before running test
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

        // Get the first page of users
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(lineUpInDb.getId()),
                        jsonPath("$.routines[0].title").value(lineUpInDb.getTitle()),
                        jsonPath("$.routines[0].description").value(lineUpInDb.getDescription()))
                .andExpectAll(
                        jsonPath("$.routines[1].id").value(tLineUpInDb.getId()),
                        jsonPath("$.routines[1].title").value(tLineUpInDb.getTitle()),
                        jsonPath("$.routines[1].description").value(tLineUpInDb.getDescription()))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));


        pageToGet = 1;
        // Get the second page of users
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(clearingColoursInDb.getId()),
                        jsonPath("$.routines[0].title").value(clearingColoursInDb.getTitle()),
                        jsonPath("$.routines[0].description").value(clearingColoursInDb.getDescription()))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getRoutineById_Should_Return200ResponseWithRoutine_When_RoutineExists() throws Exception {
        String routineId = "1234";
        Routine lineUpInDb = getLineUpRoutine();
        lineUpInDb.setId(routineId);
        routineRepository.insert(lineUpInDb);

        mockMvc.perform(get("/api/v1/routines/{routine-id}", routineId))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(lineUpInDb.getId()),
                        jsonPath("$.title").value(lineUpInDb.getTitle()),
                        jsonPath("$.description").value(lineUpInDb.getDescription()));
    }

    @Test
    void getRoutineById_Should_Return404Response_When_RoutineNotFound() throws Exception {
        String invalidRoutineId = "1234";

        mockMvc.perform(get("/api/v1/routines/{routine-id}", invalidRoutineId))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Routine not found"));
    }

    private Routine getLineUpRoutine() {
        return createRoutine(LINEUP_TITLE, LINEUP_DESC);
    }

    private Routine getTLineUpRoutine() {
        return createRoutine(T_LINEUP_TITLE, T_LINEUP_DESC);
    }

    private Routine getClearingTheColoursRoutine() {
        return createRoutine(CLEARING_COLOURS_TITLE, CLEARING_COLOURS_DESC);
    }

    private Routine createRoutine(String title, String description) {
        Routine routine = new Routine();
        routine.setTitle(title);
        routine.setDescription(description);
        return routine;
    }
}
