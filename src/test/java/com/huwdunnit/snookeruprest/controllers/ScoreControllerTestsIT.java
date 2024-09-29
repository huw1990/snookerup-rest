package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.BaseIT;
import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.model.Routine;
import com.huwdunnit.snookeruprest.model.Score;
import com.huwdunnit.snookeruprest.model.User;
import com.huwdunnit.snookeruprest.security.Roles;
import com.huwdunnit.snookeruprest.security.UserPrincipal;
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

    private static final String DATE_STRING_4 = "02/3/2024-19:30";

    @Test
    void addScore_Should_Return400BadRequest_When_ScoreIncludesInvalidRoutineId() throws Exception {
        // No routines in DB, so routine ID in score won't exist

        User userForScore = this.getHendryUser();
        userForScore.setId(PLAYER_ID_1);
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        String requestBody = objectMapper.writeValueAsString(scoreToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user(getPrincipalForUser(userForScore))))
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Invalid routine ID"))
                .andReturn();
    }

    @Test
    void addScore_Should_Return400BadRequest_When_ScoreIncludesCushionLimitParamNotAllowedByRoutine() throws Exception {
        // Add routine to DB before score that references it
        Routine routineInDb = this.getLineUpRoutine();
        routineInDb.setCushionLimits(null);
        routineInDb.setId(ROUTINE_ID_1);
        routineRepository.insert(routineInDb);

        User userForScore = this.getHendryUser();
        userForScore.setId(PLAYER_ID_1);
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setCushionLimit(3);
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        String requestBody = objectMapper.writeValueAsString(scoreToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user(getPrincipalForUser(userForScore))))
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Invalid field for routine"),
                        jsonPath("$.context.field").value("cushionLimit"))
                .andReturn();
    }

    @Test
    void addScore_Should_Return400BadRequest_When_ScoreIncludesColoursParamNotAllowedByRoutine() throws Exception {
        // Add routine to DB before score that references it
        Routine routineInDb = this.getLineUpRoutine();
        routineInDb.setColours(null);
        routineInDb.setId(ROUTINE_ID_1);
        routineRepository.insert(routineInDb);

        User userForScore = this.getHendryUser();
        userForScore.setId(PLAYER_ID_1);
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setColours("all");
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        String requestBody = objectMapper.writeValueAsString(scoreToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user(getPrincipalForUser(userForScore))))
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Invalid field for routine"),
                        jsonPath("$.context.field").value("colours"))
                .andReturn();
    }

    @Test
    void addScore_Should_Return400BadRequest_When_ScoreIncludesNumBallsParamNotAllowedByRoutine() throws Exception {
        // Add routine to DB before score that references it
        Routine routineInDb = this.getLineUpRoutine();
        routineInDb.setBalls(null);
        routineInDb.setId(ROUTINE_ID_1);
        routineRepository.insert(routineInDb);

        User userForScore = this.getHendryUser();
        userForScore.setId(PLAYER_ID_1);
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setNumBalls(10);
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        String requestBody = objectMapper.writeValueAsString(scoreToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user(getPrincipalForUser(userForScore))))
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Invalid field for routine"),
                        jsonPath("$.context.field").value("numBalls"))
                .andReturn();
    }

    @Test
    void addScore_Should_Return400BadRequest_When_ScoreIncludesLoopParamNotAllowedByRoutine() throws Exception {
        // Add routine to DB before score that references it
        Routine routineInDb = this.getLineUpRoutine();
        routineInDb.setCanLoop(false);
        routineInDb.setId(ROUTINE_ID_1);
        routineRepository.insert(routineInDb);

        User userForScore = this.getHendryUser();
        userForScore.setId(PLAYER_ID_1);
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setLoop(true);
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        String requestBody = objectMapper.writeValueAsString(scoreToAdd);

        MvcResult result = mockMvc.perform(post("/api/v1/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user(getPrincipalForUser(userForScore))))
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Invalid field for routine"),
                        jsonPath("$.context.field").value("loop"))
                .andReturn();
    }

    @Test
    void addScore_Should_Return201ResponseWithAddedScore_When_ExtraParamsProvidedAndAllAllowedByRoutine() throws Exception {
        // Add routine to DB before score that references it
        Routine routineInDb = this.getLineUpRoutine();
        routineInDb.setId(ROUTINE_ID_1);
        routineRepository.insert(routineInDb);

        int numBalls = 8;
        int cushionLimit = 5;
        String colours = "black";
        User userForScore = this.getHendryUser();
        userForScore.setId(PLAYER_ID_1);
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setNumBalls(numBalls);
        scoreToAdd.setCushionLimit(cushionLimit);
        scoreToAdd.setColours(colours);
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
                        jsonPath("$.dateTime").value(scoreToAdd.getDateTime().format(DATE_FORMATTER)),
                        jsonPath("$.cushionLimit").value(cushionLimit),
                        jsonPath("$.colours").value(colours),
                        jsonPath("$.numBalls").value(numBalls))
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
    void addScore_Should_Return201ResponseWithAddedScore_When_DateIncludedInReqAndScoreIsForCurrentUser() throws Exception {
        // Add routine to DB before score that references it
        Routine routineInDb = this.getLineUpRoutine();
        routineInDb.setId(ROUTINE_ID_1);
        routineRepository.insert(routineInDb);

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

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void addScore_Should_Return201ResponseWithAddedScore_When_DateIncludedInReqAndScoreIsForOtherUserButReqFromAdmin() throws Exception {
        // Add routine to DB before score that references it
        Routine routineInDb = this.getLineUpRoutine();
        routineInDb.setId(ROUTINE_ID_1);
        routineRepository.insert(routineInDb);

        User userForScore = this.getHendryUser();
        userForScore.setId(PLAYER_ID_1);
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
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
    void addScore_Should_Return201ResponseWithAddedScore_When_DateNotIncludedInReqAndScoreIsForCurrentUser() throws Exception {
        // Add routine to DB before score that references it
        Routine routineInDb = this.getLineUpRoutine();
        routineInDb.setId(ROUTINE_ID_1);
        routineRepository.insert(routineInDb);

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

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScoresForUser_Should_ReturnOneScore_WhenReqContainsCushionLimitParamAndOnlyOneScoreWithThisProvided() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        int cushionLimit = 5;
        scoreFourInDb.setCushionLimit(cushionLimit);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/users/{userId}/scores?pageSize={page-size}&pageNumber={page-number}&cushionLimit={cushion-limit}",
                        PLAYER_ID_2, pageSize, pageToGet, cushionLimit))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScoresForUser_Should_ReturnOneScore_WhenReqContainsColoursParamAndOnlyOneScoreWithThisProvided() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        String colours = "black";
        scoreFourInDb.setColours(colours);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/users/{userId}/scores?pageSize={page-size}&pageNumber={page-number}&colours={colours}",
                        PLAYER_ID_2, pageSize, pageToGet, colours))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScoresForUser_Should_ReturnOneScore_WhenReqContainsNumBallsParamAndOnlyOneScoreWithThisProvided() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        int numBalls = 8;
        scoreFourInDb.setNumBalls(numBalls);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/users/{userId}/scores?pageSize={page-size}&pageNumber={page-number}&numBalls={num-balls}",
                        PLAYER_ID_2, pageSize, pageToGet, numBalls))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScoresForUser_Should_ReturnOneScore_WhenReqContainsLoopParamAndOnlyOneScoreWithThisProvided() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        boolean loop = true;
        scoreFourInDb.setLoop(loop);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/users/{userId}/scores?pageSize={page-size}&pageNumber={page-number}&loop={loop}",
                        PLAYER_ID_2, pageSize, pageToGet, loop))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScoresForUser_Should_ReturnOneScore_WhenReqContainsMultipleParamsAndOnlyOneScoreWithAllProvided() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        String colours = "black";
        scoreFourInDb.setColours(colours);
        int cushionLimit = 0;
        scoreFourInDb.setCushionLimit(cushionLimit);
        int numBalls = 8;
        scoreFourInDb.setNumBalls(numBalls);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/users/{userId}/scores?pageSize={page-size}&pageNumber={page-number}" +
                                "&colours={colours}&cushionLimit={cushion-limit}&numBalls={num-balls}",
                        PLAYER_ID_2, pageSize, pageToGet, colours, cushionLimit, numBalls))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScoresForUser_Should_ReturnJustOneScore_When_OnlyOneOutOfThreeDbScoresForProvidedUserAndReqFromAdmin() throws Exception {
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
                        PLAYER_ID_2, pageSize, pageToGet))
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
    void getScores_Should_ReturnOneScore_WhenReqContainsCushionLimitParamAndOnlyOneScoreWithThisProvidedAndReqByAdmin() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        int cushionLimit = 5;
        scoreFourInDb.setCushionLimit(cushionLimit);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}&cushionLimit={cushion-limit}",
                        pageSize, pageToGet, cushionLimit))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_ReturnOneScore_WhenReqContainsColoursParamAndOnlyOneScoreWithThisProvidedAndReqByAdmin() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        String colours = "black";
        scoreFourInDb.setColours(colours);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}&colours={colours}",
                        pageSize, pageToGet, colours))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_ReturnOneScore_WhenReqContainsNumBallsParamAndOnlyOneScoreWithThisProvidedAndReqByAdmin() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        int numBalls = 8;
        scoreFourInDb.setNumBalls(numBalls);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}&numBalls={num-balls}",
                        pageSize, pageToGet, numBalls))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_ReturnOneScore_WhenReqContainsLoopParamAndOnlyOneScoreWithThisProvidedAndReqByAdmin() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        boolean loop = true;
        scoreFourInDb.setLoop(loop);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}&loop={loop}",
                        pageSize, pageToGet, loop))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_ReturnOneScore_WhenReqContainsMultipleParamsAndOnlyOneScoreWithAllProvidedAndReqByAdmin() throws Exception {
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
        Score scoreFourInDb = getScoreFour();
        scoreFourInDb.setId(IdGenerator.createNewId());
        int cushionLimit = 5;
        scoreFourInDb.setCushionLimit(cushionLimit);
        String colours = "black";
        scoreFourInDb.setColours(colours);
        int numBalls = 8;
        scoreFourInDb.setNumBalls(numBalls);
        scoreRepository.insert(scoreFourInDb);
        // Get user for score
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_2);

        int pageSize = 50;
        int pageToGet = 0;
        int expectedNumberOfPages = 1;
        int expectedTotalItems = 1;

        // Get the first page of scores
        mockMvc.perform(get("/api/v1/scores?pageSize={page-size}&pageNumber={page-number}" +
                                "&cushionLimit={cushion-limit}&colours={colours}&numBalls={num-balls}",
                        pageSize, pageToGet, cushionLimit, colours, numBalls))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.scores[0].id").value(scoreFourInDb.getId()),
                        jsonPath("$.scores[0].value").value(scoreFourInDb.getValue()),
                        jsonPath("$.scores[0].routineId").value(scoreFourInDb.getRoutineId()),
                        jsonPath("$.scores[0].userId").value(scoreFourInDb.getUserId()),
                        jsonPath("$.scores[0].dateTime").value(scoreFourInDb.getDateTime().format(DATE_FORMATTER)))
                .andExpectAll(
                        jsonPath("$.pageSize").value(pageSize),
                        jsonPath("$.pageNumber").value(pageToGet),
                        jsonPath("$.totalPages").value(expectedNumberOfPages),
                        jsonPath("$.totalItems").value(expectedTotalItems));
    }

    @WithMockUser(authorities = Roles.ADMIN)
    @Test
    void getScores_Should_EmptyScoresPage_When_NoScoresInDbAndReqByAdmin() throws Exception {
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
    void getScores_Should_ReturnScoresInOnePage_When_OnlyTwoScoresInDbAndReqByAdmin() throws Exception {
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
    void getScores_Should_ReturnScoresInOnePage_When_OnlyOneScoreOutOfTwoInDbMatchingRoutineIdAndReqByAdmin() throws Exception {
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
    void getScores_Should_ReturnJustOneScore_When_OnlyOneOutOfThreeDbScoresWithDateBeforeToAndReqByAdmin() throws Exception {
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
    void getScores_Should_ReturnJustTwoScores_When_OnlyTwoOutOfThreeDbScoresWithDateAfterFromAndReqByAdmin() throws Exception {
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
    void getScores_Should_ReturnJustOneScore_When_OnlyOneOutOfThreeDbScoresWithDateInRangeAndReqByAdmin() throws Exception {
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
    void getScores_Should_RoutinesInTwoPages_When_RequestedPagesOfTwoButThreeRoutinesInDbAndReqByAdmin() throws Exception {
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

    @Test
    void getScoreById_Should_Return200ResponseWithScore_When_ScoreExistsAndAdminUser() throws Exception {
        String scoreId = IdGenerator.createNewId();
        Score scoreOneInDb = getScoreOne();
        scoreOneInDb.setId(scoreId);
        scoreRepository.insert(scoreOneInDb);
        User adminUser = new User();
        adminUser.setId(IdGenerator.createNewId());
        adminUser.setAdmin(true);
        UserPrincipal userPrincipal = new UserPrincipal(adminUser);

        mockMvc.perform(get("/api/v1/scores/{score-id}", scoreId)
                        .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(scoreOneInDb.getId()),
                        jsonPath("$.value").value(scoreOneInDb.getValue()),
                        jsonPath("$.routineId").value(scoreOneInDb.getRoutineId()),
                        jsonPath("$.userId").value(scoreOneInDb.getUserId()),
                        jsonPath("$.dateTime").value(scoreOneInDb.getDateTime().format(DATE_FORMATTER)));
    }

    @Test
    void getScoreById_Should_Return200ResponseWithScore_When_ScoreExistsAndOwnedByUser() throws Exception {
        String scoreId = IdGenerator.createNewId();
        Score scoreOneInDb = getScoreOne();
        scoreOneInDb.setId(scoreId);
        scoreRepository.insert(scoreOneInDb);
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_1);
        UserPrincipal userPrincipal = new UserPrincipal(hendryUser);

        mockMvc.perform(get("/api/v1/scores/{score-id}", scoreId)
                        .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(scoreOneInDb.getId()),
                        jsonPath("$.value").value(scoreOneInDb.getValue()),
                        jsonPath("$.routineId").value(scoreOneInDb.getRoutineId()),
                        jsonPath("$.userId").value(scoreOneInDb.getUserId()),
                        jsonPath("$.dateTime").value(scoreOneInDb.getDateTime().format(DATE_FORMATTER)));
    }

    @Test
    void getScoreById_Should_Return404Response_When_ScoreNotOwnedByUser() throws Exception {
        String scoreId = IdGenerator.createNewId();
        Score scoreOneInDb = getScoreOne();
        scoreOneInDb.setId(scoreId);
        scoreRepository.insert(scoreOneInDb);
        User hendryUser = getHendryUser();
        hendryUser.setId(IdGenerator.createNewId());
        UserPrincipal userPrincipal = new UserPrincipal(hendryUser);

        mockMvc.perform(get("/api/v1/scores/{score-id}", scoreId)
                        .with(user(userPrincipal)))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Score not found"));
    }

    @Test
    void getScoreById_Should_Return404Response_When_ScoreNotFound() throws Exception {
        String invalidScoreId = "1234";
        User adminUser = new User();
        adminUser.setId(IdGenerator.createNewId());
        adminUser.setAdmin(true);
        UserPrincipal userPrincipal = new UserPrincipal(adminUser);

        mockMvc.perform(get("/api/v1/scores/{score-id}", invalidScoreId)
                        .with(user(userPrincipal)))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.errorMessage").value("Score not found"));
    }

    @Test
    void deleteScoreById_Should_Return204Response_When_ScoreExistedAndAdminUser() throws Exception {
        String scoreId = IdGenerator.createNewId();
        Score scoreOneInDb = getScoreOne();
        scoreOneInDb.setId(scoreId);
        scoreRepository.insert(scoreOneInDb);
        User adminUser = new User();
        adminUser.setId(IdGenerator.createNewId());
        adminUser.setAdmin(true);
        UserPrincipal userPrincipal = new UserPrincipal(adminUser);

        mockMvc.perform(delete("/api/v1/scores/{score-id}", scoreId)
                        .with(user(userPrincipal)))
                .andExpect(status().isNoContent());

        // Verify the score has been deleted from the DB
        Optional<Score> opt = scoreRepository.findById(scoreId);
        assertTrue(opt.isEmpty());
    }

    @WithMockUser(authorities = {Roles.ADMIN, Roles.USER})
    @Test
    void deleteScoreById_Should_Return204Response_When_ScoreExistedAndOwnedByUser() throws Exception {
        String scoreId = IdGenerator.createNewId();
        Score scoreOneInDb = getScoreOne();
        scoreOneInDb.setId(scoreId);
        scoreRepository.insert(scoreOneInDb);
        User hendryUser = getHendryUser();
        hendryUser.setId(PLAYER_ID_1);
        UserPrincipal userPrincipal = new UserPrincipal(hendryUser);

        mockMvc.perform(delete("/api/v1/scores/{score-id}", scoreId)
                        .with(user(userPrincipal)))
                .andExpect(status().isNoContent());

        // Verify the score has been deleted from the DB
        Optional<Score> opt = scoreRepository.findById(scoreId);
        assertTrue(opt.isEmpty());
    }

    @Test
    void deleteScoreById_Should_Return204Response_When_ScoreDidntExistAndAdminUser() throws Exception {
        String invalidScoreId = "1234";
        User adminUser = new User();
        adminUser.setId(IdGenerator.createNewId());
        adminUser.setAdmin(true);
        UserPrincipal userPrincipal = new UserPrincipal(adminUser);

        mockMvc.perform(delete("/api/v1/scores/{score-id}", invalidScoreId)
                        .with(user(userPrincipal)))
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

    private Score getScoreFour() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(130);
        scoreToAdd.setUserId(PLAYER_ID_2);
        scoreToAdd.setRoutineId(ROUTINE_ID_2);
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_4, DATE_FORMATTER));
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
