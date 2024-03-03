package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.BaseIT;
import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.model.Score;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the ScoreController class.
 *
 * @author Huwdunnit
 */
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class ScoreControllerTestsIT extends BaseIT {

    private static final String PLAYER_ID_1 = IdGenerator.createNewId();

    private static final String PLAYER_ID_2 = IdGenerator.createNewId();

    private static final String ROUTINE_ID_1 = IdGenerator.createNewId();

    private static final String ROUTINE_ID_2 = IdGenerator.createNewId();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/M/yyyy HH:mm");

    private static final String DATE_STRING_1 = "25/2/2024 15:00";

    private static final String DATE_STRING_2 = "01/3/2024 19:10";

    private static final String DATE_STRING_3 = "01/3/2024 19:25";

    @Test
    void addScore_Should_Return201ResponseWithAddedScore_When_DateIncludedInReq() throws Exception {
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateAndTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        String requestBody = objectMapper.writeValueAsString(scoreToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.value").value(scoreToAdd.getValue()),
                        jsonPath("$.userId").value(scoreToAdd.getUserId()),
                        jsonPath("$.routineId").value(scoreToAdd.getRoutineId()),
                        jsonPath("$.dateAndTime").value(scoreToAdd.getDateAndTime().format(DATE_FORMATTER)))
                .andReturn();

        // Get the score's ID so we can check it exists in the DB
        Score scoreInResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Score.class);
        String addedScoreId = scoreInResponse.getId();

        // Get the user by ID from the DB.
        Optional<Score> opt = scoreRepository.findById(addedScoreId);

        opt.ifPresentOrElse(
                (scoreInDb) -> assertEquals(scoreInResponse, scoreInDb, "Score returned in response is different to score in DB"),
                () -> fail("Score with ID from response not found in the DB")
        );
    }

    @Test
    void addScore_Should_Return201ResponseWithAddedScore_When_DateNotIncludedInReq() throws Exception {
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        String requestBody = objectMapper.writeValueAsString(scoreToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.value").value(scoreToAdd.getValue()),
                        jsonPath("$.userId").value(scoreToAdd.getUserId()),
                        jsonPath("$.routineId").value(scoreToAdd.getRoutineId()),
                        jsonPath("$.dateAndTime").exists())
                .andReturn();

        // Get the score's ID so we can check it exists in the DB
        Score scoreInResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Score.class);
        String addedScoreId = scoreInResponse.getId();

        // Get the user by ID from the DB.
        Optional<Score> opt = scoreRepository.findById(addedScoreId);

        opt.ifPresentOrElse(
                (scoreInDb) -> assertEquals(scoreInResponse, scoreInDb, "Score returned in response is different to score in DB"),
                () -> fail("Score with ID from response not found in the DB")
        );
    }

    @Test
    void getAllScores_Should_EmptyScoresPage_When_NoScoresInDb() throws Exception {
        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 0;
        int expectedTotalItems = 0;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores").isEmpty())
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getAllScores_Should_ScoresInOnePage_When_OnlyTwoScoresInDb() throws Exception {
        // Add scores to DB before running test
        Score scoreOneInDb = getScoreOne();
        scoreOneInDb.setId(IdGenerator.createNewId());
        scoreRepository.insert(scoreOneInDb);
        Score scoreTwoInDb = getScoreTwo();
        scoreTwoInDb.setId(IdGenerator.createNewId());
        scoreRepository.insert(scoreTwoInDb);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 2;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreOneInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreOneInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreOneInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreOneInDb.getUserId()),
                        jsonPath("$.scores[0].dateAndTime").value(scoreOneInDb.getDateAndTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.scores[1].id").value(scoreTwoInDb.getId()),
                        jsonPath("$.scores[1].value").value(scoreTwoInDb.getValue()),
                        jsonPath("$.scores[1].routineId").value(scoreTwoInDb.getRoutineId()),
                        jsonPath("$.scores[1].userId").value(scoreTwoInDb.getUserId()),
                        jsonPath("$.scores[1].dateAndTime").value(scoreTwoInDb.getDateAndTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getAllRoutines_Should_RoutinesInTwoPages_When_RequestedPagesOfTwoButThreeRoutinesInDb() throws Exception {
        // Add scores to DB before running test
        Score scoreOneInDb = getScoreOne();
        scoreOneInDb.setId(IdGenerator.createNewId());
        scoreRepository.insert(scoreOneInDb);
        Score scoreTwoInDb = getScoreTwo();
        scoreTwoInDb.setId(IdGenerator.createNewId());
        scoreRepository.insert(scoreTwoInDb);
        Score scoreThreeInDb = getScoreThree();
        scoreThreeInDb.setId(IdGenerator.createNewId());
        scoreRepository.insert(scoreThreeInDb);

        int pageSize = 2;
        int pageToGet = 0;
        int expectedNumberOfPages = 2;
        int expectedTotalItems = 3;

        // Get the first page of users
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreOneInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreOneInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreOneInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreOneInDb.getUserId()),
                        jsonPath("$.scores[0].dateAndTime").value(scoreOneInDb.getDateAndTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.scores[1].id").value(scoreTwoInDb.getId()),
                        jsonPath("$.scores[1].value").value(scoreTwoInDb.getValue()),
                        jsonPath("$.scores[1].routineId").value(scoreTwoInDb.getRoutineId()),
                        jsonPath("$.scores[1].userId").value(scoreTwoInDb.getUserId()),
                        jsonPath("$.scores[1].dateAndTime").value(scoreTwoInDb.getDateAndTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));


        pageToGet = 1;
        // Get the second page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreThreeInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreThreeInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreThreeInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreThreeInDb.getUserId()),
                        jsonPath("$.scores[0].dateAndTime").value(scoreThreeInDb.getDateAndTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    private Score getScoreOne() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(100);
        scoreToAdd.setUserId(PLAYER_ID_1);
        scoreToAdd.setRoutineId(ROUTINE_ID_1);
        scoreToAdd.setDateAndTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        return scoreToAdd;
    }

    private Score getScoreTwo() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(110);
        scoreToAdd.setUserId(PLAYER_ID_2);
        scoreToAdd.setRoutineId(ROUTINE_ID_2);
        scoreToAdd.setDateAndTime(LocalDateTime.parse(DATE_STRING_2, DATE_FORMATTER));
        return scoreToAdd;
    }

    private Score getScoreThree() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(130);
        scoreToAdd.setUserId(PLAYER_ID_1);
        scoreToAdd.setRoutineId(ROUTINE_ID_1);
        scoreToAdd.setDateAndTime(LocalDateTime.parse(DATE_STRING_3, DATE_FORMATTER));
        return scoreToAdd;
    }

    private Score getScoreToAddWithoutDateTimeSet() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(100);
        scoreToAdd.setUserId(PLAYER_ID_1);
        scoreToAdd.setRoutineId(ROUTINE_ID_1);
        return scoreToAdd;
    }
}
