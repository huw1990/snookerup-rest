package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.BaseIT;
import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.model.Balls;
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
import java.util.stream.IntStream;

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
                        jsonPath("$.description.length()").value(3),
                        jsonPath("$.description[0]").value(routineToAdd.getDescription().get(0)),
                        jsonPath("$.description[1]").value(routineToAdd.getDescription().get(1)),
                        jsonPath("$.description[2]").value(routineToAdd.getDescription().get(2)),
                        jsonPath("$.balls.options.length()").value(15),
                        jsonPath("$.balls.options[0]").value(routineToAdd.getBalls().getOptions().get(0)),
                        jsonPath("$.balls.options[1]").value(routineToAdd.getBalls().getOptions().get(1)),
                        jsonPath("$.balls.options[2]").value(routineToAdd.getBalls().getOptions().get(2)),
                        jsonPath("$.balls.options[3]").value(routineToAdd.getBalls().getOptions().get(3)),
                        jsonPath("$.balls.options[4]").value(routineToAdd.getBalls().getOptions().get(4)),
                        jsonPath("$.balls.options[5]").value(routineToAdd.getBalls().getOptions().get(5)),
                        jsonPath("$.balls.options[6]").value(routineToAdd.getBalls().getOptions().get(6)),
                        jsonPath("$.balls.options[7]").value(routineToAdd.getBalls().getOptions().get(7)),
                        jsonPath("$.balls.options[8]").value(routineToAdd.getBalls().getOptions().get(8)),
                        jsonPath("$.balls.options[9]").value(routineToAdd.getBalls().getOptions().get(9)),
                        jsonPath("$.balls.options[10]").value(routineToAdd.getBalls().getOptions().get(10)),
                        jsonPath("$.balls.options[11]").value(routineToAdd.getBalls().getOptions().get(11)),
                        jsonPath("$.balls.options[12]").value(routineToAdd.getBalls().getOptions().get(12)),
                        jsonPath("$.balls.options[13]").value(routineToAdd.getBalls().getOptions().get(13)),
                        jsonPath("$.balls.options[14]").value(routineToAdd.getBalls().getOptions().get(14)),
                        jsonPath("$.balls.unit").value(routineToAdd.getBalls().getUnit()),
                        jsonPath("$.cushionLimits.length()").value(4),
                        jsonPath("$.cushionLimits[0]").value(routineToAdd.getCushionLimits().get(0)),
                        jsonPath("$.cushionLimits[1]").value(routineToAdd.getCushionLimits().get(1)),
                        jsonPath("$.cushionLimits[2]").value(routineToAdd.getCushionLimits().get(2)),
                        jsonPath("$.cushionLimits[3]").value(routineToAdd.getCushionLimits().get(3)),
                        jsonPath("$.colours.length()").value(2),
                        jsonPath("$.colours[0]").value(routineToAdd.getColours().get(0)),
                        jsonPath("$.colours[1]").value(routineToAdd.getColours().get(1)),
                        jsonPath("$.images.length()").value(2),
                        jsonPath("$.images[0]").value(routineToAdd.getImages().get(0)),
                        jsonPath("$.images[1]").value(routineToAdd.getImages().get(1)),
                        jsonPath("$.tags.length()").value(2),
                        jsonPath("$.tags[0]").value(routineToAdd.getTags().get(0)),
                        jsonPath("$.tags[1]").value(routineToAdd.getTags().get(1)))
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
                        jsonPath("$.routines[0].description.length()").value(3),
                        jsonPath("$.routines[0].description[0]").value(lineUpInDb.getDescription().get(0)),
                        jsonPath("$.routines[0].description[1]").value(lineUpInDb.getDescription().get(1)),
                        jsonPath("$.routines[0].description[2]").value(lineUpInDb.getDescription().get(2)),
                        jsonPath("$.routines[0].balls.options.length()").value(15),
                        jsonPath("$.routines[0].balls.options[0]").value(lineUpInDb.getBalls().getOptions().get(0)),
                        jsonPath("$.routines[0].balls.options[1]").value(lineUpInDb.getBalls().getOptions().get(1)),
                        jsonPath("$.routines[0].balls.options[2]").value(lineUpInDb.getBalls().getOptions().get(2)),
                        jsonPath("$.routines[0].balls.options[3]").value(lineUpInDb.getBalls().getOptions().get(3)),
                        jsonPath("$.routines[0].balls.options[4]").value(lineUpInDb.getBalls().getOptions().get(4)),
                        jsonPath("$.routines[0].balls.options[5]").value(lineUpInDb.getBalls().getOptions().get(5)),
                        jsonPath("$.routines[0].balls.options[6]").value(lineUpInDb.getBalls().getOptions().get(6)),
                        jsonPath("$.routines[0].balls.options[7]").value(lineUpInDb.getBalls().getOptions().get(7)),
                        jsonPath("$.routines[0].balls.options[8]").value(lineUpInDb.getBalls().getOptions().get(8)),
                        jsonPath("$.routines[0].balls.options[9]").value(lineUpInDb.getBalls().getOptions().get(9)),
                        jsonPath("$.routines[0].balls.options[10]").value(lineUpInDb.getBalls().getOptions().get(10)),
                        jsonPath("$.routines[0].balls.options[11]").value(lineUpInDb.getBalls().getOptions().get(11)),
                        jsonPath("$.routines[0].balls.options[12]").value(lineUpInDb.getBalls().getOptions().get(12)),
                        jsonPath("$.routines[0].balls.options[13]").value(lineUpInDb.getBalls().getOptions().get(13)),
                        jsonPath("$.routines[0].balls.options[14]").value(lineUpInDb.getBalls().getOptions().get(14)),
                        jsonPath("$.routines[0].balls.unit").value(lineUpInDb.getBalls().getUnit()),
                        jsonPath("$.routines[0].cushionLimits.length()").value(4),
                        jsonPath("$.routines[0].cushionLimits[0]").value(lineUpInDb.getCushionLimits().get(0)),
                        jsonPath("$.routines[0].cushionLimits[1]").value(lineUpInDb.getCushionLimits().get(1)),
                        jsonPath("$.routines[0].cushionLimits[2]").value(lineUpInDb.getCushionLimits().get(2)),
                        jsonPath("$.routines[0].cushionLimits[3]").value(lineUpInDb.getCushionLimits().get(3)),
                        jsonPath("$.routines[0].colours.length()").value(2),
                        jsonPath("$.routines[0].colours[0]").value(lineUpInDb.getColours().get(0)),
                        jsonPath("$.routines[0].colours[1]").value(lineUpInDb.getColours().get(1)),
                        jsonPath("$.routines[0].images.length()").value(2),
                        jsonPath("$.routines[0].images[0]").value(lineUpInDb.getImages().get(0)),
                        jsonPath("$.routines[0].images[1]").value(lineUpInDb.getImages().get(1)),
                        jsonPath("$.routines[0].tags.length()").value(2),
                        jsonPath("$.routines[0].tags[0]").value(lineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[0].tags[1]").value(lineUpInDb.getTags().get(1)))
                .andExpectAll(
                        jsonPath("$.routines[1].id").value(tLineUpInDb.getId()),
                        jsonPath("$.routines[1].title").value(tLineUpInDb.getTitle()),
                        jsonPath("$.routines[1].description.length()").value(4),
                        jsonPath("$.routines[1].description[0]").value(tLineUpInDb.getDescription().get(0)),
                        jsonPath("$.routines[1].description[1]").value(tLineUpInDb.getDescription().get(1)),
                        jsonPath("$.routines[1].description[2]").value(tLineUpInDb.getDescription().get(2)),
                        jsonPath("$.routines[1].description[3]").value(tLineUpInDb.getDescription().get(3)),
                        jsonPath("$.routines[1].balls.options.length()").value(13),
                        jsonPath("$.routines[1].balls.options[0]").value(tLineUpInDb.getBalls().getOptions().get(0)),
                        jsonPath("$.routines[1].balls.options[1]").value(tLineUpInDb.getBalls().getOptions().get(1)),
                        jsonPath("$.routines[1].balls.options[2]").value(tLineUpInDb.getBalls().getOptions().get(2)),
                        jsonPath("$.routines[1].balls.options[3]").value(tLineUpInDb.getBalls().getOptions().get(3)),
                        jsonPath("$.routines[1].balls.options[4]").value(tLineUpInDb.getBalls().getOptions().get(4)),
                        jsonPath("$.routines[1].balls.options[5]").value(tLineUpInDb.getBalls().getOptions().get(5)),
                        jsonPath("$.routines[1].balls.options[6]").value(tLineUpInDb.getBalls().getOptions().get(6)),
                        jsonPath("$.routines[1].balls.options[7]").value(tLineUpInDb.getBalls().getOptions().get(7)),
                        jsonPath("$.routines[1].balls.options[8]").value(tLineUpInDb.getBalls().getOptions().get(8)),
                        jsonPath("$.routines[1].balls.options[9]").value(tLineUpInDb.getBalls().getOptions().get(9)),
                        jsonPath("$.routines[1].balls.options[10]").value(tLineUpInDb.getBalls().getOptions().get(10)),
                        jsonPath("$.routines[1].balls.options[11]").value(tLineUpInDb.getBalls().getOptions().get(11)),
                        jsonPath("$.routines[1].balls.options[12]").value(tLineUpInDb.getBalls().getOptions().get(12)),
                        jsonPath("$.routines[1].balls.unit").value(tLineUpInDb.getBalls().getUnit()),
                        jsonPath("$.routines[1].cushionLimits.length()").value(4),
                        jsonPath("$.routines[1].cushionLimits[0]").value(tLineUpInDb.getCushionLimits().get(0)),
                        jsonPath("$.routines[1].cushionLimits[1]").value(tLineUpInDb.getCushionLimits().get(1)),
                        jsonPath("$.routines[1].cushionLimits[2]").value(tLineUpInDb.getCushionLimits().get(2)),
                        jsonPath("$.routines[1].cushionLimits[3]").value(tLineUpInDb.getCushionLimits().get(3)),
                        jsonPath("$.routines[1].colours.length()").value(4),
                        jsonPath("$.routines[1].colours[0]").value(tLineUpInDb.getColours().get(0)),
                        jsonPath("$.routines[1].colours[1]").value(tLineUpInDb.getColours().get(1)),
                        jsonPath("$.routines[1].colours[2]").value(tLineUpInDb.getColours().get(2)),
                        jsonPath("$.routines[1].colours[3]").value(tLineUpInDb.getColours().get(3)),
                        jsonPath("$.routines[1].images.length()").value(2),
                        jsonPath("$.routines[1].images[0]").value(tLineUpInDb.getImages().get(0)),
                        jsonPath("$.routines[1].images[1]").value(tLineUpInDb.getImages().get(1)),
                        jsonPath("$.routines[1].tags.length()").value(2),
                        jsonPath("$.routines[1].tags[0]").value(tLineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[1].tags[1]").value(tLineUpInDb.getTags().get(1)))
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
                        jsonPath("$.routines[0].description.length()").value(3),
                        jsonPath("$.routines[0].description[0]").value(lineUpInDb.getDescription().get(0)),
                        jsonPath("$.routines[0].description[1]").value(lineUpInDb.getDescription().get(1)),
                        jsonPath("$.routines[0].description[2]").value(lineUpInDb.getDescription().get(2)),
                        jsonPath("$.routines[0].balls.options.length()").value(15),
                        jsonPath("$.routines[0].balls.options[0]").value(lineUpInDb.getBalls().getOptions().get(0)),
                        jsonPath("$.routines[0].balls.options[1]").value(lineUpInDb.getBalls().getOptions().get(1)),
                        jsonPath("$.routines[0].balls.options[2]").value(lineUpInDb.getBalls().getOptions().get(2)),
                        jsonPath("$.routines[0].balls.options[3]").value(lineUpInDb.getBalls().getOptions().get(3)),
                        jsonPath("$.routines[0].balls.options[4]").value(lineUpInDb.getBalls().getOptions().get(4)),
                        jsonPath("$.routines[0].balls.options[5]").value(lineUpInDb.getBalls().getOptions().get(5)),
                        jsonPath("$.routines[0].balls.options[6]").value(lineUpInDb.getBalls().getOptions().get(6)),
                        jsonPath("$.routines[0].balls.options[7]").value(lineUpInDb.getBalls().getOptions().get(7)),
                        jsonPath("$.routines[0].balls.options[8]").value(lineUpInDb.getBalls().getOptions().get(8)),
                        jsonPath("$.routines[0].balls.options[9]").value(lineUpInDb.getBalls().getOptions().get(9)),
                        jsonPath("$.routines[0].balls.options[10]").value(lineUpInDb.getBalls().getOptions().get(10)),
                        jsonPath("$.routines[0].balls.options[11]").value(lineUpInDb.getBalls().getOptions().get(11)),
                        jsonPath("$.routines[0].balls.options[12]").value(lineUpInDb.getBalls().getOptions().get(12)),
                        jsonPath("$.routines[0].balls.options[13]").value(lineUpInDb.getBalls().getOptions().get(13)),
                        jsonPath("$.routines[0].balls.options[14]").value(lineUpInDb.getBalls().getOptions().get(14)),
                        jsonPath("$.routines[0].balls.unit").value(lineUpInDb.getBalls().getUnit()),
                        jsonPath("$.routines[0].cushionLimits.length()").value(4),
                        jsonPath("$.routines[0].cushionLimits[0]").value(lineUpInDb.getCushionLimits().get(0)),
                        jsonPath("$.routines[0].cushionLimits[1]").value(lineUpInDb.getCushionLimits().get(1)),
                        jsonPath("$.routines[0].cushionLimits[2]").value(lineUpInDb.getCushionLimits().get(2)),
                        jsonPath("$.routines[0].cushionLimits[3]").value(lineUpInDb.getCushionLimits().get(3)),
                        jsonPath("$.routines[0].colours.length()").value(2),
                        jsonPath("$.routines[0].colours[0]").value(lineUpInDb.getColours().get(0)),
                        jsonPath("$.routines[0].colours[1]").value(lineUpInDb.getColours().get(1)),
                        jsonPath("$.routines[0].images.length()").value(2),
                        jsonPath("$.routines[0].images[0]").value(lineUpInDb.getImages().get(0)),
                        jsonPath("$.routines[0].images[1]").value(lineUpInDb.getImages().get(1)),
                        jsonPath("$.routines[0].tags.length()").value(2),
                        jsonPath("$.routines[0].tags[0]").value(lineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[0].tags[1]").value(lineUpInDb.getTags().get(1)))
                .andExpectAll(
                        jsonPath("$.routines[1].id").value(tLineUpInDb.getId()),
                        jsonPath("$.routines[1].title").value(tLineUpInDb.getTitle()),
                        jsonPath("$.routines[1].description.length()").value(4),
                        jsonPath("$.routines[1].description[0]").value(tLineUpInDb.getDescription().get(0)),
                        jsonPath("$.routines[1].description[1]").value(tLineUpInDb.getDescription().get(1)),
                        jsonPath("$.routines[1].description[2]").value(tLineUpInDb.getDescription().get(2)),
                        jsonPath("$.routines[1].description[3]").value(tLineUpInDb.getDescription().get(3)),
                        jsonPath("$.routines[1].balls.options.length()").value(13),
                        jsonPath("$.routines[1].balls.options[0]").value(tLineUpInDb.getBalls().getOptions().get(0)),
                        jsonPath("$.routines[1].balls.options[1]").value(tLineUpInDb.getBalls().getOptions().get(1)),
                        jsonPath("$.routines[1].balls.options[2]").value(tLineUpInDb.getBalls().getOptions().get(2)),
                        jsonPath("$.routines[1].balls.options[3]").value(tLineUpInDb.getBalls().getOptions().get(3)),
                        jsonPath("$.routines[1].balls.options[4]").value(tLineUpInDb.getBalls().getOptions().get(4)),
                        jsonPath("$.routines[1].balls.options[5]").value(tLineUpInDb.getBalls().getOptions().get(5)),
                        jsonPath("$.routines[1].balls.options[6]").value(tLineUpInDb.getBalls().getOptions().get(6)),
                        jsonPath("$.routines[1].balls.options[7]").value(tLineUpInDb.getBalls().getOptions().get(7)),
                        jsonPath("$.routines[1].balls.options[8]").value(tLineUpInDb.getBalls().getOptions().get(8)),
                        jsonPath("$.routines[1].balls.options[9]").value(tLineUpInDb.getBalls().getOptions().get(9)),
                        jsonPath("$.routines[1].balls.options[10]").value(tLineUpInDb.getBalls().getOptions().get(10)),
                        jsonPath("$.routines[1].balls.options[11]").value(tLineUpInDb.getBalls().getOptions().get(11)),
                        jsonPath("$.routines[1].balls.options[12]").value(tLineUpInDb.getBalls().getOptions().get(12)),
                        jsonPath("$.routines[1].balls.unit").value(tLineUpInDb.getBalls().getUnit()),
                        jsonPath("$.routines[1].cushionLimits.length()").value(4),
                        jsonPath("$.routines[1].cushionLimits[0]").value(tLineUpInDb.getCushionLimits().get(0)),
                        jsonPath("$.routines[1].cushionLimits[1]").value(tLineUpInDb.getCushionLimits().get(1)),
                        jsonPath("$.routines[1].cushionLimits[2]").value(tLineUpInDb.getCushionLimits().get(2)),
                        jsonPath("$.routines[1].cushionLimits[3]").value(tLineUpInDb.getCushionLimits().get(3)),
                        jsonPath("$.routines[1].colours.length()").value(4),
                        jsonPath("$.routines[1].colours[0]").value(tLineUpInDb.getColours().get(0)),
                        jsonPath("$.routines[1].colours[1]").value(tLineUpInDb.getColours().get(1)),
                        jsonPath("$.routines[1].colours[2]").value(tLineUpInDb.getColours().get(2)),
                        jsonPath("$.routines[1].colours[3]").value(tLineUpInDb.getColours().get(3)),
                        jsonPath("$.routines[1].images.length()").value(2),
                        jsonPath("$.routines[1].images[0]").value(tLineUpInDb.getImages().get(0)),
                        jsonPath("$.routines[1].images[1]").value(tLineUpInDb.getImages().get(1)),
                        jsonPath("$.routines[1].tags.length()").value(2),
                        jsonPath("$.routines[1].tags[0]").value(tLineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[1].tags[1]").value(tLineUpInDb.getTags().get(1)))
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
                        jsonPath("$.routines[0].description.length()").value(1),
                        jsonPath("$.routines[0].description[0]").value(clearingColoursInDb.getDescription().get(0)),
                        jsonPath("$.routines[0].cushionLimits.length()").value(4),
                        jsonPath("$.routines[0].cushionLimits[0]").value(clearingColoursInDb.getCushionLimits().get(0)),
                        jsonPath("$.routines[0].cushionLimits[1]").value(clearingColoursInDb.getCushionLimits().get(1)),
                        jsonPath("$.routines[0].cushionLimits[2]").value(clearingColoursInDb.getCushionLimits().get(2)),
                        jsonPath("$.routines[0].cushionLimits[3]").value(clearingColoursInDb.getCushionLimits().get(3)),
                        jsonPath("$.routines[0].canLoop").value(clearingColoursInDb.isCanLoop()),
                        jsonPath("$.routines[0].images.length()").value(1),
                        jsonPath("$.routines[0].images[0]").value(clearingColoursInDb.getImages().get(0)),
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
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}&tags=custom-tag-1",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(clearingColoursInDb.getId()),
                        jsonPath("$.routines[0].title").value(clearingColoursInDb.getTitle()),
                        jsonPath("$.routines[0].description.length()").value(1),
                        jsonPath("$.routines[0].description[0]").value(clearingColoursInDb.getDescription().get(0)),
                        jsonPath("$.routines[0].cushionLimits.length()").value(4),
                        jsonPath("$.routines[0].cushionLimits[0]").value(clearingColoursInDb.getCushionLimits().get(0)),
                        jsonPath("$.routines[0].cushionLimits[1]").value(clearingColoursInDb.getCushionLimits().get(1)),
                        jsonPath("$.routines[0].cushionLimits[2]").value(clearingColoursInDb.getCushionLimits().get(2)),
                        jsonPath("$.routines[0].cushionLimits[3]").value(clearingColoursInDb.getCushionLimits().get(3)),
                        jsonPath("$.routines[0].canLoop").value(clearingColoursInDb.isCanLoop()),
                        jsonPath("$.routines[0].images.length()").value(1),
                        jsonPath("$.routines[0].images[0]").value(clearingColoursInDb.getImages().get(0)),
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
        mockMvc.perform(get("/api/v1/routines?pageSize={page-size}&pageNumber={page-number}&tags=custom-tag-1,break-building",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.routines[0].id").value(lineUpInDb.getId()),
                        jsonPath("$.routines[0].title").value(lineUpInDb.getTitle()),
                        jsonPath("$.routines[0].description.length()").value(3),
                        jsonPath("$.routines[0].description[0]").value(lineUpInDb.getDescription().get(0)),
                        jsonPath("$.routines[0].description[1]").value(lineUpInDb.getDescription().get(1)),
                        jsonPath("$.routines[0].description[2]").value(lineUpInDb.getDescription().get(2)),
                        jsonPath("$.routines[0].balls.options.length()").value(15),
                        jsonPath("$.routines[0].balls.options[0]").value(lineUpInDb.getBalls().getOptions().get(0)),
                        jsonPath("$.routines[0].balls.options[1]").value(lineUpInDb.getBalls().getOptions().get(1)),
                        jsonPath("$.routines[0].balls.options[2]").value(lineUpInDb.getBalls().getOptions().get(2)),
                        jsonPath("$.routines[0].balls.options[3]").value(lineUpInDb.getBalls().getOptions().get(3)),
                        jsonPath("$.routines[0].balls.options[4]").value(lineUpInDb.getBalls().getOptions().get(4)),
                        jsonPath("$.routines[0].balls.options[5]").value(lineUpInDb.getBalls().getOptions().get(5)),
                        jsonPath("$.routines[0].balls.options[6]").value(lineUpInDb.getBalls().getOptions().get(6)),
                        jsonPath("$.routines[0].balls.options[7]").value(lineUpInDb.getBalls().getOptions().get(7)),
                        jsonPath("$.routines[0].balls.options[8]").value(lineUpInDb.getBalls().getOptions().get(8)),
                        jsonPath("$.routines[0].balls.options[9]").value(lineUpInDb.getBalls().getOptions().get(9)),
                        jsonPath("$.routines[0].balls.options[10]").value(lineUpInDb.getBalls().getOptions().get(10)),
                        jsonPath("$.routines[0].balls.options[11]").value(lineUpInDb.getBalls().getOptions().get(11)),
                        jsonPath("$.routines[0].balls.options[12]").value(lineUpInDb.getBalls().getOptions().get(12)),
                        jsonPath("$.routines[0].balls.options[13]").value(lineUpInDb.getBalls().getOptions().get(13)),
                        jsonPath("$.routines[0].balls.options[14]").value(lineUpInDb.getBalls().getOptions().get(14)),
                        jsonPath("$.routines[0].balls.unit").value(lineUpInDb.getBalls().getUnit()),
                        jsonPath("$.routines[0].cushionLimits.length()").value(4),
                        jsonPath("$.routines[0].cushionLimits[0]").value(lineUpInDb.getCushionLimits().get(0)),
                        jsonPath("$.routines[0].cushionLimits[1]").value(lineUpInDb.getCushionLimits().get(1)),
                        jsonPath("$.routines[0].cushionLimits[2]").value(lineUpInDb.getCushionLimits().get(2)),
                        jsonPath("$.routines[0].cushionLimits[3]").value(lineUpInDb.getCushionLimits().get(3)),
                        jsonPath("$.routines[0].colours.length()").value(2),
                        jsonPath("$.routines[0].colours[0]").value(lineUpInDb.getColours().get(0)),
                        jsonPath("$.routines[0].colours[1]").value(lineUpInDb.getColours().get(1)),
                        jsonPath("$.routines[0].images.length()").value(2),
                        jsonPath("$.routines[0].images[0]").value(lineUpInDb.getImages().get(0)),
                        jsonPath("$.routines[0].images[1]").value(lineUpInDb.getImages().get(1)),
                        jsonPath("$.routines[0].tags.length()").value(2),
                        jsonPath("$.routines[0].tags[0]").value(lineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[0].tags[1]").value(lineUpInDb.getTags().get(1)))
                .andExpectAll(
                        jsonPath("$.routines[1].id").value(tLineUpInDb.getId()),
                        jsonPath("$.routines[1].title").value(tLineUpInDb.getTitle()),
                        jsonPath("$.routines[1].description.length()").value(4),
                        jsonPath("$.routines[1].description[0]").value(tLineUpInDb.getDescription().get(0)),
                        jsonPath("$.routines[1].description[1]").value(tLineUpInDb.getDescription().get(1)),
                        jsonPath("$.routines[1].description[2]").value(tLineUpInDb.getDescription().get(2)),
                        jsonPath("$.routines[1].description[3]").value(tLineUpInDb.getDescription().get(3)),
                        jsonPath("$.routines[1].balls.options.length()").value(13),
                        jsonPath("$.routines[1].balls.options[0]").value(tLineUpInDb.getBalls().getOptions().get(0)),
                        jsonPath("$.routines[1].balls.options[1]").value(tLineUpInDb.getBalls().getOptions().get(1)),
                        jsonPath("$.routines[1].balls.options[2]").value(tLineUpInDb.getBalls().getOptions().get(2)),
                        jsonPath("$.routines[1].balls.options[3]").value(tLineUpInDb.getBalls().getOptions().get(3)),
                        jsonPath("$.routines[1].balls.options[4]").value(tLineUpInDb.getBalls().getOptions().get(4)),
                        jsonPath("$.routines[1].balls.options[5]").value(tLineUpInDb.getBalls().getOptions().get(5)),
                        jsonPath("$.routines[1].balls.options[6]").value(tLineUpInDb.getBalls().getOptions().get(6)),
                        jsonPath("$.routines[1].balls.options[7]").value(tLineUpInDb.getBalls().getOptions().get(7)),
                        jsonPath("$.routines[1].balls.options[8]").value(tLineUpInDb.getBalls().getOptions().get(8)),
                        jsonPath("$.routines[1].balls.options[9]").value(tLineUpInDb.getBalls().getOptions().get(9)),
                        jsonPath("$.routines[1].balls.options[10]").value(tLineUpInDb.getBalls().getOptions().get(10)),
                        jsonPath("$.routines[1].balls.options[11]").value(tLineUpInDb.getBalls().getOptions().get(11)),
                        jsonPath("$.routines[1].balls.options[12]").value(tLineUpInDb.getBalls().getOptions().get(12)),
                        jsonPath("$.routines[1].balls.unit").value(tLineUpInDb.getBalls().getUnit()),
                        jsonPath("$.routines[1].cushionLimits.length()").value(4),
                        jsonPath("$.routines[1].cushionLimits[0]").value(tLineUpInDb.getCushionLimits().get(0)),
                        jsonPath("$.routines[1].cushionLimits[1]").value(tLineUpInDb.getCushionLimits().get(1)),
                        jsonPath("$.routines[1].cushionLimits[2]").value(tLineUpInDb.getCushionLimits().get(2)),
                        jsonPath("$.routines[1].cushionLimits[3]").value(tLineUpInDb.getCushionLimits().get(3)),
                        jsonPath("$.routines[1].colours.length()").value(4),
                        jsonPath("$.routines[1].colours[0]").value(tLineUpInDb.getColours().get(0)),
                        jsonPath("$.routines[1].colours[1]").value(tLineUpInDb.getColours().get(1)),
                        jsonPath("$.routines[1].colours[2]").value(tLineUpInDb.getColours().get(2)),
                        jsonPath("$.routines[1].colours[3]").value(tLineUpInDb.getColours().get(3)),
                        jsonPath("$.routines[1].images.length()").value(2),
                        jsonPath("$.routines[1].images[0]").value(tLineUpInDb.getImages().get(0)),
                        jsonPath("$.routines[1].images[1]").value(tLineUpInDb.getImages().get(1)),
                        jsonPath("$.routines[1].tags.length()").value(2),
                        jsonPath("$.routines[1].tags[0]").value(tLineUpInDb.getTags().get(0)),
                        jsonPath("$.routines[1].tags[1]").value(tLineUpInDb.getTags().get(1)))
                .andExpectAll(
                        jsonPath("$.routines[2].id").value(clearingColoursInDb.getId()),
                        jsonPath("$.routines[2].title").value(clearingColoursInDb.getTitle()),
                        jsonPath("$.routines[2].description.length()").value(1),
                        jsonPath("$.routines[2].description[0]").value(clearingColoursInDb.getDescription().get(0)),
                        jsonPath("$.routines[2].cushionLimits.length()").value(4),
                        jsonPath("$.routines[2].cushionLimits[0]").value(clearingColoursInDb.getCushionLimits().get(0)),
                        jsonPath("$.routines[2].cushionLimits[1]").value(clearingColoursInDb.getCushionLimits().get(1)),
                        jsonPath("$.routines[2].cushionLimits[2]").value(clearingColoursInDb.getCushionLimits().get(2)),
                        jsonPath("$.routines[2].cushionLimits[3]").value(clearingColoursInDb.getCushionLimits().get(3)),
                        jsonPath("$.routines[2].canLoop").value(clearingColoursInDb.isCanLoop()),
                        jsonPath("$.routines[2].images.length()").value(1),
                        jsonPath("$.routines[2].images[0]").value(clearingColoursInDb.getImages().get(0)),
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
                        jsonPath("$.description.length()").value(3),
                        jsonPath("$.description[0]").value(lineUpInDb.getDescription().get(0)),
                        jsonPath("$.description[1]").value(lineUpInDb.getDescription().get(1)),
                        jsonPath("$.description[2]").value(lineUpInDb.getDescription().get(2)),
                        jsonPath("$.balls.options.length()").value(15),
                        jsonPath("$.balls.options[0]").value(lineUpInDb.getBalls().getOptions().get(0)),
                        jsonPath("$.balls.options[1]").value(lineUpInDb.getBalls().getOptions().get(1)),
                        jsonPath("$.balls.options[2]").value(lineUpInDb.getBalls().getOptions().get(2)),
                        jsonPath("$.balls.options[3]").value(lineUpInDb.getBalls().getOptions().get(3)),
                        jsonPath("$.balls.options[4]").value(lineUpInDb.getBalls().getOptions().get(4)),
                        jsonPath("$.balls.options[5]").value(lineUpInDb.getBalls().getOptions().get(5)),
                        jsonPath("$.balls.options[6]").value(lineUpInDb.getBalls().getOptions().get(6)),
                        jsonPath("$.balls.options[7]").value(lineUpInDb.getBalls().getOptions().get(7)),
                        jsonPath("$.balls.options[8]").value(lineUpInDb.getBalls().getOptions().get(8)),
                        jsonPath("$.balls.options[9]").value(lineUpInDb.getBalls().getOptions().get(9)),
                        jsonPath("$.balls.options[10]").value(lineUpInDb.getBalls().getOptions().get(10)),
                        jsonPath("$.balls.options[11]").value(lineUpInDb.getBalls().getOptions().get(11)),
                        jsonPath("$.balls.options[12]").value(lineUpInDb.getBalls().getOptions().get(12)),
                        jsonPath("$.balls.options[13]").value(lineUpInDb.getBalls().getOptions().get(13)),
                        jsonPath("$.balls.options[14]").value(lineUpInDb.getBalls().getOptions().get(14)),
                        jsonPath("$.balls.unit").value(lineUpInDb.getBalls().getUnit()),
                        jsonPath("$.cushionLimits.length()").value(4),
                        jsonPath("$.cushionLimits[0]").value(lineUpInDb.getCushionLimits().get(0)),
                        jsonPath("$.cushionLimits[1]").value(lineUpInDb.getCushionLimits().get(1)),
                        jsonPath("$.cushionLimits[2]").value(lineUpInDb.getCushionLimits().get(2)),
                        jsonPath("$.cushionLimits[3]").value(lineUpInDb.getCushionLimits().get(3)),
                        jsonPath("$.colours.length()").value(2),
                        jsonPath("$.colours[0]").value(lineUpInDb.getColours().get(0)),
                        jsonPath("$.colours[1]").value(lineUpInDb.getColours().get(1)),
                        jsonPath("$.images.length()").value(2),
                        jsonPath("$.images[0]").value(lineUpInDb.getImages().get(0)),
                        jsonPath("$.images[1]").value(lineUpInDb.getImages().get(1)),
                        jsonPath("$.tags.length()").value(2),
                        jsonPath("$.tags[0]").value(lineUpInDb.getTags().get(0)),
                        jsonPath("$.tags[1]").value(lineUpInDb.getTags().get(1)));
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

    private Routine getTLineUpRoutine() {
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

    private Routine getClearingTheColoursRoutine() {
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
