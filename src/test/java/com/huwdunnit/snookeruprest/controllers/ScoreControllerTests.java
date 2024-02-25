package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.model.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * Tests for the ScoreController class.
 *
 * @author Huwdunnit
 */
public class ScoreControllerTests {

    private static final String PLAYER_ID = "1111";

    private static final String ROUTINE_ID = "2222";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/M/yyyy HH:mm");

    private static final String DATE_STRING = "25/2/2024 15:00";

    private ScoreRepository mockScoreRepository;

    private ScoreController scoreController;

    @BeforeEach
    public void beforeEach() {
        mockScoreRepository = mock(ScoreRepository.class);

        scoreController = new ScoreController(mockScoreRepository);
    }

    @Test
    public void addScore_Should_AddScoreAndReturnWithId_When_DateIncludedInRequest() {
        // Define variables
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateAndTime(LocalDateTime.parse(DATE_STRING, DATE_FORMATTER));
        Score expectedScore = getScoreToAddWithoutDateTimeSet();
        expectedScore.setDateAndTime(LocalDateTime.parse(DATE_STRING, DATE_FORMATTER));

        // Set mock expectations
        when(mockScoreRepository.insert(any(Score.class))).thenReturn(expectedScore);

        // Execute method under test
        Score addedScore = scoreController.addScore(scoreToAdd);

        // Verify
        assertNotNull(addedScore);
        assertEquals(expectedScore, addedScore);

        verify(mockScoreRepository).insert(any(Score.class));
    }

    @Test
    public void addScore_Should_AddScoreAndReturnWithId_When_DateNotIncludedInRequest() {
        // Define variables
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        Score expectedScore = getScoreToAddWithoutDateTimeSet();
        expectedScore.setDateAndTime(LocalDateTime.now());

        // Set mock expectations
        when(mockScoreRepository.insert(any(Score.class))).thenReturn(expectedScore);

        // Execute method under test
        Score addedScore = scoreController.addScore(scoreToAdd);

        // Verify
        assertNotNull(addedScore);
        assertEquals(expectedScore, addedScore);

        verify(mockScoreRepository).insert(any(Score.class));
    }

    private Score getScoreToAddWithoutDateTimeSet() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(100);
        scoreToAdd.setUserId(PLAYER_ID);
        scoreToAdd.setRoutineId(ROUTINE_ID);
        return scoreToAdd;
    }
}
