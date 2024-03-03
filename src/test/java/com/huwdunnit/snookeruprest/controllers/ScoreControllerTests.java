package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.exceptions.RoutineNotFoundException;
import com.huwdunnit.snookeruprest.exceptions.ScoreNotFoundException;
import com.huwdunnit.snookeruprest.model.Routine;
import com.huwdunnit.snookeruprest.model.Score;
import com.huwdunnit.snookeruprest.model.ScoreListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

    private static final String PLAYER_ID_1 = IdGenerator.createNewId();

    private static final String PLAYER_ID_2 = IdGenerator.createNewId();

    private static final String ROUTINE_ID_1 = IdGenerator.createNewId();

    private static final String ROUTINE_ID_2 = IdGenerator.createNewId();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/M/yyyy HH:mm");

    private static final String DATE_STRING_1 = "25/2/2024 15:00";

    private static final String DATE_STRING_2 = "01/3/2024 19:10";

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
        scoreToAdd.setDateAndTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        Score expectedScore = getScoreToAddWithoutDateTimeSet();
        expectedScore.setDateAndTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));

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

    @Test
    public void getAllScores_Should_RespondWithTwoScoresAndNoFurtherPages_When_OnlyTwoScoresInDb() {
        // Define variables
        Score scoreOne = getScoreOne();
        scoreOne.setId(IdGenerator.createNewId());
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);

        // Set mock expectations
        when(mockScoreRepository.findAll(any(Pageable.class))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne, scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(2);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(2L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getAllScores(0, 50);

        // Verify
        assertEquals(2, scoresResponse.getScores().size());
        assertEquals(scoreOne, scoresResponse.getScores().get(0));
        assertEquals(scoreTwo, scoresResponse.getScores().get(1));
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(2, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(2L, scoresResponse.getTotalItems());
    }

    @Test
    public void getAllScores_Should_RespondWithEmptyList_When_NoScoresInDb() {
        // Define variables
        Page<Score> mockScoresPage = mock(Page.class);

        // Set mock expectations
        when(mockScoreRepository.findAll(any(Pageable.class))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of());
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(0);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(0L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getAllScores(0, 50);

        // Verify
        assertEquals(0, scoresResponse.getScores().size());
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(0, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(0L, scoresResponse.getTotalItems());
    }

    @Test
    public void getAllScores_Should_RespondWithTwoScoresAndOneFurtherPage_When_ThreeScoresInDb() {
        // Define variables
        Score scoreOne = getScoreOne();
        scoreOne.setId(IdGenerator.createNewId());
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);

        // Set mock expectations
        when(mockScoreRepository.findAll(any(Pageable.class))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne, scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(2);
        when(mockScoresPage.getTotalPages()).thenReturn(2);
        when(mockScoresPage.getTotalElements()).thenReturn(3L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getAllScores(0, 2);

        // Verify
        assertEquals(2, scoresResponse.getScores().size());
        assertEquals(scoreOne, scoresResponse.getScores().get(0));
        assertEquals(scoreTwo, scoresResponse.getScores().get(1));
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(2, scoresResponse.getPageSize());
        assertEquals(2, scoresResponse.getTotalPages());
        assertEquals(3L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScoreById_Should_ReturnScore_When_ScoreWithIdExists() {
        // Define variables
        String scoreId = IdGenerator.createNewId();
        Score scoreOne = getScoreOne();
        scoreOne.setId(scoreId);

        // Set mock expectations
        when(mockScoreRepository.findById(scoreId)).thenReturn(Optional.of(scoreOne));

        // Execute method under test
        Score returnedScore = scoreController.getScoreById(scoreId);

        // Verify
        assertNotNull(returnedScore);
        assertEquals(scoreOne, returnedScore);

        verify(mockScoreRepository).findById(scoreId);
    }

    @Test
    public void getScoreById_Should_ThrowScoreNotFoundException_When_ScoreNotFound() {
        // Define variables
        String scoreId = "1234";

        // Set mock expectations
        when(mockScoreRepository.findById(scoreId)).thenReturn(Optional.empty());

        // Execute method under test
        Score returnedScore = null;
        try {
            returnedScore = scoreController.getScoreById(scoreId);
            fail("Expected ScoreNotFoundException");
        } catch (ScoreNotFoundException ex) {
            // Exception thrown as expected
        }

        // Verify
        assertNull(returnedScore);
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

    private Score getScoreToAddWithoutDateTimeSet() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(100);
        scoreToAdd.setUserId(PLAYER_ID_1);
        scoreToAdd.setRoutineId(ROUTINE_ID_1);
        return scoreToAdd;
    }
}
