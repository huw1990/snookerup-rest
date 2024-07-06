package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.BaseIT;
import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.model.Score;
import com.huwdunnit.snookeruprest.model.User;
import com.huwdunnit.snookeruprest.security.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/M/yyyy-HH:mm");

    private static final String DATE_STRING_1 = "25/2/2024-15:00";

    private static final String DATE_STRING_2 = "01/3/2024-19:10";

    private static final String DATE_STRING_3 = "02/3/2024-19:25";

    @Test
    void addScore_Should_Return201ResponseWithAddedScore_When_DateIncludedInReq() throws Exception {
        User userForScore = this.getHendryUser();
        userForScore.setId(PLAYER_ID_1);
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        String requestBody = objectMapper.writeValueAsString(scoreToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user(getPrincipalForUser(userForScore))))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.value").value(scoreToAdd.getValue()),
                        jsonPath("$.userId").value(scoreToAdd.getUserId()),
                        jsonPath("$.routineId").value(scoreToAdd.getRoutineId()),
                        jsonPath("$.dateTime").value(scoreToAdd.getDateTime().format(DATE_FORMATTER)))
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
        User userForScore = this.getHendryUser();
        userForScore.setId(PLAYER_ID_1);
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        String requestBody = objectMapper.writeValueAsString(scoreToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user(getPrincipalForUser(userForScore))))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.value").value(scoreToAdd.getValue()),
                        jsonPath("$.userId").value(scoreToAdd.getUserId()),
                        jsonPath("$.routineId").value(scoreToAdd.getRoutineId()),
                        jsonPath("$.dateTime").exists())
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
    void getScoresForUser_Should_ReturnJustOneScore_When_OnlyOneOutOfThreeDbScoresForProvidedUser() throws Exception {
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
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/users/{userId}/scores?pageSize={page-size}&pageNumber={page-number}",
                        PLAYER_ID_2, pageSize, pageToGet)
                        .with(user(getPrincipalForUser(hendryUser))))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreTwoInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreTwoInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreTwoInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreTwoInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreTwoInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @Test
    void getScoresForUser_Should_ReturnJustOneScore_When_OnlyOneOutOfThreeDbScoresForProvidedUserAndRoutine() throws Exception {
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
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/users/{userId}/scores?pageSize={page-size}&pageNumber={page-number}&routineId={routine-id}",
                        PLAYER_ID_2, pageSize, pageToGet, ROUTINE_ID_2)
                        .with(user(getPrincipalForUser(hendryUser))))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreTwoInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreTwoInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreTwoInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreTwoInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreTwoInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_EmptyScoresPage_When_NoScoresInDb() throws Exception {
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

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_ReturnScoresInOnePage_When_OnlyTwoScoresInDb() throws Exception {
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
                        jsonPath("$.scores[0].dateTime").value(scoreOneInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.scores[1].id").value(scoreTwoInDb.getId()),
                        jsonPath("$.scores[1].value").value(scoreTwoInDb.getValue()),
                        jsonPath("$.scores[1].routineId").value(scoreTwoInDb.getRoutineId()),
                        jsonPath("$.scores[1].userId").value(scoreTwoInDb.getUserId()),
                        jsonPath("$.scores[1].dateTime").value(scoreTwoInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_ReturnScoresInOnePage_When_OnlyOneScoreOutOfTwoInDbMatchingRoutineId() throws Exception {
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
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}&routineId={routine-id}",
                        pageSize, pageToGet, ROUTINE_ID_1))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreOneInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreOneInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreOneInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreOneInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreOneInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_ReturnJustOneScore_When_OnlyOneOutOfThreeDbScoresWithDateBeforeTo() throws Exception {
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

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}&to=27/2/2024-00:00",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreOneInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreOneInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreOneInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreOneInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreOneInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_ReturnJustTwoScores_When_OnlyTwoOutOfThreeDbScoresWithDateAfterFrom() throws Exception {
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

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 2;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}&from=01/3/2024-00:00",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreTwoInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreTwoInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreTwoInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreTwoInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreTwoInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.scores[1].id").value(scoreThreeInDb.getId()),
                        jsonPath("$.scores[1].value").value(scoreThreeInDb.getValue()),
                        jsonPath("$.scores[1].routineId").value(scoreThreeInDb.getRoutineId()),
                        jsonPath("$.scores[1].userId").value(scoreThreeInDb.getUserId()),
                        jsonPath("$.scores[1].dateTime").value(scoreThreeInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_ReturnJustOneScore_When_OnlyOneOutOfThreeDbScoresWithDateInRange() throws Exception {
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

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}&from=01/3/2024-00:00&to=02/3/2024-00:00",
                        pageSize, pageToGet))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreTwoInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreTwoInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreTwoInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreTwoInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreTwoInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_RoutinesInTwoPages_When_RequestedPagesOfTwoButThreeRoutinesInDb() throws Exception {
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
                        jsonPath("$.scores[0].dateTime").value(scoreOneInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.scores[1].id").value(scoreTwoInDb.getId()),
                        jsonPath("$.scores[1].value").value(scoreTwoInDb.getValue()),
                        jsonPath("$.scores[1].routineId").value(scoreTwoInDb.getRoutineId()),
                        jsonPath("$.scores[1].userId").value(scoreTwoInDb.getUserId()),
                        jsonPath("$.scores[1].dateTime").value(scoreTwoInDb.getDateTime().format(DATE_FORMATTER)))
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
                        jsonPath("$.scores[0].dateTime").value(scoreThreeInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = {Roles.ADMIN, Roles.USER})
    @Test
    void getScoreById_Should_Return200ResponseWithScore_When_ScoreExists() throws Exception {
        String scoreId = IdGenerator.createNewId();
        Score scoreOneInDb = getScoreOne();
        scoreOneInDb.setId(scoreId);
        scoreRepository.insert(scoreOneInDb);

        mockMvc.perform(get("/api/v1/scores/{score-id}", scoreId))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(scoreOneInDb.getId()),
                        jsonPath("$.value").value(scoreOneInDb.getValue()),
                        jsonPath("$.routineId").value(scoreOneInDb.getRoutineId()),
                        jsonPath("$.userId").value(scoreOneInDb.getUserId()),
                        jsonPath("$.dateTime").value(scoreOneInDb.getDateTime().format(DATE_FORMATTER)));
    }

    @WithMockUser(authorities = {Roles.ADMIN, Roles.USER})
    @Test
    void getScoreById_Should_Return404Response_When_ScoreNotFound() throws Exception {
        String invalidScoreId = "1234";

        mockMvc.perform(get("/api/v1/scores/{score-id}", invalidScoreId))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Score not found"));
    }

    @WithMockUser(authorities = {Roles.ADMIN, Roles.USER})
    @Test
    void deleteScoreById_Should_Return204Response_When_ScoreExisted() throws Exception {
        String scoreId = IdGenerator.createNewId();
        Score scoreOneInDb = getScoreOne();
        scoreOneInDb.setId(scoreId);
        scoreRepository.insert(scoreOneInDb);

        mockMvc.perform(delete("/api/v1/scores/{score-id}", scoreId))
                .andExpect(status().isNoContent());

        // Verify the score has been deleted from the DB
        Optional<Score> opt = scoreRepository.findById(scoreId);
        assertTrue(opt.isEmpty());
    }

    @WithMockUser(authorities = {Roles.ADMIN, Roles.USER})
    @Test
    void getScoreById_Should_Return204Response_When_ScoreDidntExist() throws Exception {
        String invalidScoreId = "1234";

        mockMvc.perform(delete("/api/v1/scores/{score-id}", invalidScoreId))
                .andExpect(status().isNoContent());
    }

    private Score getScoreOne() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(100);
        scoreToAdd.setUserId(PLAYER_ID_1);
        scoreToAdd.setRoutineId(ROUTINE_ID_1);
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        return scoreToAdd;
    }

    private Score getScoreTwo() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(110);
        scoreToAdd.setUserId(PLAYER_ID_2);
        scoreToAdd.setRoutineId(ROUTINE_ID_2);
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_2, DATE_FORMATTER));
        return scoreToAdd;
    }

    private Score getScoreThree() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(130);
        scoreToAdd.setUserId(PLAYER_ID_1);
        scoreToAdd.setRoutineId(ROUTINE_ID_1);
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_3, DATE_FORMATTER));
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
