package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.BaseIT;
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

    private static final String PLAYER_ID = "1111";

    private static final String ROUTINE_ID = "2222";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/M/yyyy HH:mm");

    private static final String DATE_STRING = "25/2/2024 15:00";

    @Test
    void addScore_Should_Return201ResponseWithAddedScore_When_DateIncludedInReq() throws Exception {
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateAndTime(LocalDateTime.parse(DATE_STRING, DATE_FORMATTER));
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

    private Score getScoreToAddWithoutDateTimeSet() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(100);
        scoreToAdd.setUserId(PLAYER_ID);
        scoreToAdd.setRoutineId(ROUTINE_ID);
        return scoreToAdd;
    }
}
