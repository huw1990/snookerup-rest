package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.RoutineRepository;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.exceptions.InvalidScoreFieldException;
import com.huwdunnit.snookeruprest.exceptions.RoutineForScoreNotFoundException;
import com.huwdunnit.snookeruprest.exceptions.ScoreNotFoundException;
import com.huwdunnit.snookeruprest.model.*;
import com.huwdunnit.snookeruprest.security.UserPrincipal;
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/M/yyyy-HH:mm");

    private static final String DATE_STRING_1 = "25/2/2024-15:00";

    private static final String DATE_STRING_2 = "01/3/2024-19:10";

    private ScoreRepository mockScoreRepository;

    private RoutineRepository mockRoutineRepository;

    private Routine mockRoutine;

    private ScoreController scoreController;

    @BeforeEach
    public void beforeEach() {
        mockScoreRepository = mock(ScoreRepository.class);
        mockRoutineRepository = mock(RoutineRepository.class);
        mockRoutine = mock(Routine.class);

        scoreController = new ScoreController(mockScoreRepository, mockRoutineRepository);
    }

    @Test
    public void addScore_Should_ThrowException_When_InvalidRoutineIdProvided() {
        // Define variables
        String invalidRoutineId = "invalid_routine_id";
        Score scoreToAdd = getScoreOne();
        scoreToAdd.setRoutineId(invalidRoutineId);

        // Set mock expectations
        when(mockRoutineRepository.findById(invalidRoutineId)).thenReturn(Optional.empty());

        // Execute method under test
        try {
            Score addedScore = scoreController.addScore(scoreToAdd);
            fail("Expected RoutineForScoreNotFoundException");
        } catch (RoutineForScoreNotFoundException ex) {
            // Expected exception, i.e. test pass
        }

        // Verify
    }

    @Test
    public void addScore_Should_ThrowException_When_CushionLimitProvidedAndNotAllowedOnRoutine() {
        // Define variables
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        scoreToAdd.setCushionLimit(0);

        // Set mock expectations
        when(mockRoutineRepository.findById(ROUTINE_ID_1)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.getCushionLimits()).thenReturn(null);

        // Execute method under test
        try {
            Score addedScore = scoreController.addScore(scoreToAdd);
            fail("Expected InvalidScoreFieldException");
        } catch (InvalidScoreFieldException ex) {
            // Expected exception, i.e. test pass
        }

        // Verify
    }

    @Test
    public void addScore_Should_ThrowException_When_ColourRestrictionProvidedAndNotAllowedOnRoutine() {
        // Define variables
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        scoreToAdd.setColours("all");

        // Set mock expectations
        when(mockRoutineRepository.findById(ROUTINE_ID_1)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.getColours()).thenReturn(null);

        // Execute method under test
        try {
            Score addedScore = scoreController.addScore(scoreToAdd);
            fail("Expected InvalidScoreFieldException");
        } catch (InvalidScoreFieldException ex) {
            // Expected exception, i.e. test pass
        }

        // Verify
    }

    @Test
    public void addScore_Should_ThrowException_When_NumBallsProvidedAndNotAllowedOnRoutine() {
        // Define variables
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        scoreToAdd.setNumBalls(8);

        // Set mock expectations
        when(mockRoutineRepository.findById(ROUTINE_ID_1)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.getBalls()).thenReturn(null);

        // Execute method under test
        try {
            Score addedScore = scoreController.addScore(scoreToAdd);
            fail("Expected InvalidScoreFieldException");
        } catch (InvalidScoreFieldException ex) {
            // Expected exception, i.e. test pass
        }

        // Verify
    }

    @Test
    public void addScore_Should_ThrowException_When_LoopProvidedAndNotAllowedOnRoutine() {
        // Define variables
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        scoreToAdd.setLoop(true);

        // Set mock expectations
        when(mockRoutineRepository.findById(ROUTINE_ID_1)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.isCanLoop()).thenReturn(false);

        // Execute method under test
        try {
            Score addedScore = scoreController.addScore(scoreToAdd);
            fail("Expected InvalidScoreFieldException");
        } catch (InvalidScoreFieldException ex) {
            // Expected exception, i.e. test pass
        }

        // Verify
    }

    @Test
    public void addScore_Should_AddScoreAndReturnWithId_When_ScoreWithExtraFieldsProvided() {
        // Define variables
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        scoreToAdd.setCushionLimit(0);
        scoreToAdd.setColours("all");
        scoreToAdd.setNumBalls(10);
        scoreToAdd.setLoop(true);
        Score expectedScore = getScoreToAddWithoutDateTimeSet();
        expectedScore.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        expectedScore.setCushionLimit(0);
        expectedScore.setColours("all");
        expectedScore.setNumBalls(10);
        expectedScore.setLoop(true);

        // Set mock expectations
        when(mockRoutineRepository.findById(ROUTINE_ID_1)).thenReturn(Optional.of(mockRoutine));
        when(mockScoreRepository.insert(any(Score.class))).thenReturn(expectedScore);
        when(mockRoutine.getCushionLimits()).thenReturn(List.of(0, 3, 5, 7));
        when(mockRoutine.getColours()).thenReturn(List.of("all", "black", "pink,black"));
        when(mockRoutine.getBalls()).thenReturn(Balls.builder().unit("reds").options(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        when(mockRoutine.isCanLoop()).thenReturn(true);

        // Execute method under test
        Score addedScore = scoreController.addScore(scoreToAdd);

        // Verify
        assertNotNull(addedScore);
        assertEquals(expectedScore, addedScore);

        verify(mockScoreRepository).insert(any(Score.class));
    }

    @Test
    public void addScore_Should_AddScoreAndReturnWithId_When_DateIncludedInRequest() {
        // Define variables
        Score scoreToAdd = getScoreToAddWithoutDateTimeSet();
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        Score expectedScore = getScoreToAddWithoutDateTimeSet();
        expectedScore.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));

        // Set mock expectations
        when(mockRoutineRepository.findById(ROUTINE_ID_1)).thenReturn(Optional.of(mockRoutine));
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
        expectedScore.setDateTime(LocalDateTime.now());

        // Set mock expectations
        when(mockRoutineRepository.findById(ROUTINE_ID_1)).thenReturn(Optional.of(mockRoutine));
        when(mockScoreRepository.insert(any(Score.class))).thenReturn(expectedScore);

        // Execute method under test
        Score addedScore = scoreController.addScore(scoreToAdd);

        // Verify
        assertNotNull(addedScore);
        assertEquals(expectedScore, addedScore);

        verify(mockScoreRepository).insert(any(Score.class));
    }

    @Test
    public void getScoresForUser_Should_RespondWithOneScore_When_OnlyOneScoreInDbForUser() {
        // Define variables
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);
        String toDateString = "27/2/2024-00:00";
        LocalDateTime toDate = LocalDateTime.parse(toDateString, DATE_FORMATTER);

        // Set mock expectations
        when(mockScoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(Optional.empty()),
                    eq(Optional.of(PLAYER_ID_2)),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScoresForUser(
                0,
                50,
                Optional.empty(),
                Optional.empty(), PLAYER_ID_2,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Verify
        assertEquals(1, scoresResponse.getScores().size());
        assertEquals(scoreTwo, scoresResponse.getScores().get(0));
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(1, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(1L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScoresForUser_Should_RespondWithOneScore_When_RoutineIdProvidedAndOnlyOneMatchingScore() {
        // Define variables
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);
        String toDateString = "27/2/2024-00:00";
        LocalDateTime toDate = LocalDateTime.parse(toDateString, DATE_FORMATTER);

        // Set mock expectations
        when(mockScoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(Optional.of(ROUTINE_ID_2)),
                    eq(Optional.of(PLAYER_ID_2)),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScoresForUser(
                0,
                50,
                Optional.empty(),
                Optional.empty(),
                PLAYER_ID_2,
                Optional.of(ROUTINE_ID_2),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Verify
        assertEquals(1, scoresResponse.getScores().size());
        assertEquals(scoreTwo, scoresResponse.getScores().get(0));
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(1, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(1L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScoresForUser_Should_RespondWithNoScores_When_RoutineIdProvidedAndNoMatchingScores() {
        // Define variables
        Page<Score> mockScoresPage = mock(Page.class);

        // Set mock expectations
        when(mockScoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(Optional.of(ROUTINE_ID_1)),
                    eq(Optional.of(PLAYER_ID_2)),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of());
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(0);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(0L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScoresForUser(
                0,
                50,
                Optional.empty(),
                Optional.empty(),
                PLAYER_ID_2,
                Optional.of(ROUTINE_ID_1),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Verify
        assertEquals(0, scoresResponse.getScores().size());
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(0, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(0L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScoresForUser_Should_RespondWithOneScore_When_OnlyOneScoreInDateRangeWithExtraParamsProvidedAndRequestedScoresBetweenDates() {
        // Define variables
        Page<Score> mockScoresPage = mock(Page.class);
        int cushionLimit = 0;
        String colours = "all";
        int numBalls = 10;
        boolean loop = true;

        // Set mock expectations
        when(mockScoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(
                any(Pageable.class),
                eq(Optional.of(ROUTINE_ID_1)),
                eq(Optional.of(PLAYER_ID_2)),
                eq(Optional.of(cushionLimit)),
                eq(Optional.of(colours)),
                eq(Optional.of(numBalls)),
                eq(Optional.of(loop))
        )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of());
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(0);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(0L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScoresForUser(
                0,
                50,
                Optional.empty(),
                Optional.empty(),
                PLAYER_ID_2,
                Optional.of(ROUTINE_ID_1),
                Optional.of(cushionLimit),
                Optional.of(colours),
                Optional.of(numBalls),
                Optional.of(loop));

        // Verify
        assertEquals(0, scoresResponse.getScores().size());
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(0, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(0L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScores_Should_RespondWithTwoScoresAndNoFurtherPages_When_OnlyTwoScoresInDb() {
        // Define variables
        Score scoreOne = getScoreOne();
        scoreOne.setId(IdGenerator.createNewId());
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);

        // Set mock expectations
        when(mockScoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne, scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(2);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(2L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(
                0,
                50,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

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
    public void getScores_Should_RespondWithOneScoreAndNoFurtherPages_When_OnlyTwoScoresInDbButOneMatchingProvidedRoutineId() {
        // Define variables
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);

        // Set mock expectations
        when(mockScoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(Optional.of(ROUTINE_ID_2)),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(
                0,
                50,
                Optional.empty(),
                Optional.empty(),
                Optional.of(ROUTINE_ID_2),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Verify
        assertEquals(1, scoresResponse.getScores().size());
        assertEquals(scoreTwo, scoresResponse.getScores().get(0));
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(1, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(1L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScores_Should_RespondWithEmptyList_When_NoScoresInDb() {
        // Define variables
        Page<Score> mockScoresPage = mock(Page.class);

        // Set mock expectations
        when(mockScoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of());
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(0);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(0L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(
                0,
                50,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Verify
        assertEquals(0, scoresResponse.getScores().size());
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(0, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(0L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScores_Should_RespondWithTwoScoresAndOneFurtherPage_When_ThreeScoresInDb() {
        // Define variables
        Score scoreOne = getScoreOne();
        scoreOne.setId(IdGenerator.createNewId());
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);

        // Set mock expectations
        when(mockScoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne, scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(2);
        when(mockScoresPage.getTotalPages()).thenReturn(2);
        when(mockScoresPage.getTotalElements()).thenReturn(3L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(
                0,
                2,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

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
    public void getScores_Should_RespondWithTwoScores_When_ThreeScoresInDb() {
        // Define variables
        Score scoreOne = getScoreOne();
        scoreOne.setId(IdGenerator.createNewId());
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);

        // Set mock expectations
        when(mockScoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne, scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(2);
        when(mockScoresPage.getTotalPages()).thenReturn(2);
        when(mockScoresPage.getTotalElements()).thenReturn(3L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(
                0,
                2,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

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
    public void getScores_Should_RespondWithOneScore_When_OnlyOneScoreInDateRangeAndRequestedScoresToDate() {
        // Define variables
        Score scoreOne = getScoreOne();
        scoreOne.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);
        String toDateString = "27/2/2024-00:00";
        LocalDateTime toDate = LocalDateTime.parse(toDateString, DATE_FORMATTER);

        // Set mock expectations
        when(mockScoreRepository.findToDateWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(toDate), eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(
                0,
                2,
                Optional.empty(),
                Optional.of(toDate),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Verify
        assertEquals(1, scoresResponse.getScores().size());
        assertEquals(scoreOne, scoresResponse.getScores().get(0));
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(1, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(1L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScores_Should_RespondWithOneScore_When_OnlyOneScoreInDateRangeAndRequestedScoresFromDate() {
        // Define variables
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);
        String fromDateString = "01/3/2024-00:00";
        LocalDateTime fromDate = LocalDateTime.parse(fromDateString, DATE_FORMATTER);

        // Set mock expectations
        when(mockScoreRepository.findFromDateWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(fromDate),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(
                0,
                2,
                Optional.of(fromDate),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Verify
        assertEquals(1, scoresResponse.getScores().size());
        assertEquals(scoreTwo, scoresResponse.getScores().get(0));
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(1, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(1L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScores_Should_RespondWithOneScore_When_OnlyOneScoreInDateRangeAndRequestedScoresBetweenDates() {
        // Define variables
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);
        String fromDateString = "01/3/2024-00:00";
        LocalDateTime fromDate = LocalDateTime.parse(fromDateString, DATE_FORMATTER);
        String toDateString = "02/3/2024-00:00";
        LocalDateTime toDate = LocalDateTime.parse(toDateString, DATE_FORMATTER);

        // Set mock expectations
        when(mockScoreRepository.findBetweenDatesWithOptionalRoutineIdAndUserIdAndScoreParams(
                    any(Pageable.class),
                    eq(fromDate), eq(toDate),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty()),
                    eq(Optional.empty())
                )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(
                0,
                50,
                Optional.of(fromDate),
                Optional.of(toDate),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Verify
        assertEquals(1, scoresResponse.getScores().size());
        assertEquals(scoreTwo, scoresResponse.getScores().get(0));
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(1, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(1L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScores_Should_RespondWithOneScore_When_OnlyOneScoreInDateRangeWithExtraParamsProvidedAndRequestedScoresBetweenDates() {
        // Define variables
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);
        String fromDateString = "01/3/2024-00:00";
        LocalDateTime fromDate = LocalDateTime.parse(fromDateString, DATE_FORMATTER);
        String toDateString = "02/3/2024-00:00";
        LocalDateTime toDate = LocalDateTime.parse(toDateString, DATE_FORMATTER);
        int cushionLimit = 0;
        String colours = "all";
        int numBalls = 10;
        boolean loop = true;

        // Set mock expectations
        when(mockScoreRepository.findBetweenDatesWithOptionalRoutineIdAndUserIdAndScoreParams(
                any(Pageable.class),
                eq(fromDate), eq(toDate),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.of(cushionLimit)),
                eq(Optional.of(colours)),
                eq(Optional.of(numBalls)),
                eq(Optional.of(loop))
        )).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(
                0,
                50,
                Optional.of(fromDate),
                Optional.of(toDate),
                Optional.empty(),
                Optional.of(cushionLimit),
                Optional.of(colours),
                Optional.of(numBalls),
                Optional.of(loop));

        // Verify
        assertEquals(1, scoresResponse.getScores().size());
        assertEquals(scoreTwo, scoresResponse.getScores().get(0));
        assertEquals(0, scoresResponse.getPageNumber());
        assertEquals(1, scoresResponse.getPageSize());
        assertEquals(1, scoresResponse.getTotalPages());
        assertEquals(1L, scoresResponse.getTotalItems());
    }

    @Test
    public void getScoreById_Should_ReturnScore_When_ScoreWithIdExistsAndAdminUser() {
        // Define variables
        String scoreId = IdGenerator.createNewId();
        Score scoreOne = getScoreOne();
        scoreOne.setId(scoreId);
        User adminUser = new User();
        adminUser.setAdmin(true);
        UserPrincipal userPrincipal = new UserPrincipal(adminUser);

        // Set mock expectations
        when(mockScoreRepository.findById(scoreId)).thenReturn(Optional.of(scoreOne));

        // Execute method under test
        Score returnedScore = scoreController.getScoreById(scoreId, userPrincipal);

        // Verify
        assertNotNull(returnedScore);
        assertEquals(scoreOne, returnedScore);

        verify(mockScoreRepository).findById(scoreId);
    }

    @Test
    public void getScoreById_Should_ReturnScore_When_ScoreWithIdExistsAndIsOwnedByUser() {
        // Define variables
        String scoreId = IdGenerator.createNewId();
        Score scoreOne = getScoreOne();
        scoreOne.setId(scoreId);
        String userId = IdGenerator.createNewId();
        User user = new User();
        user.setId(userId);
        UserPrincipal userPrincipal = new UserPrincipal(user);

        // Set mock expectations
        when(mockScoreRepository.findByIdAndUserId(scoreId, userId)).thenReturn(Optional.of(scoreOne));

        // Execute method under test
        Score returnedScore = scoreController.getScoreById(scoreId, userPrincipal);

        // Verify
        assertNotNull(returnedScore);
        assertEquals(scoreOne, returnedScore);

        verify(mockScoreRepository).findByIdAndUserId(scoreId, userId);
    }

    @Test
    public void getScoreById_Should_ThrowScoreNotFoundException_When_ScoreNotFoundAndAdminUser() {
        // Define variables
        String scoreId = "1234";
        User adminUser = new User();
        adminUser.setAdmin(true);
        UserPrincipal userPrincipal = new UserPrincipal(adminUser);

        // Set mock expectations
        when(mockScoreRepository.findById(scoreId)).thenReturn(Optional.empty());

        // Execute method under test
        Score returnedScore = null;
        try {
            returnedScore = scoreController.getScoreById(scoreId, userPrincipal);
            fail("Expected ScoreNotFoundException");
        } catch (ScoreNotFoundException ex) {
            // Exception thrown as expected
        }

        // Verify
        assertNull(returnedScore);
    }

    @Test
    public void getScoreById_Should_ThrowScoreNotFoundException_When_ScoreForUserNotFound() {
        // Define variables
        String scoreId = "1234";
        User user = new User();
        String userId = "abcd";
        user.setId(userId);
        UserPrincipal userPrincipal = new UserPrincipal(user);

        // Set mock expectations
        when(mockScoreRepository.findByIdAndUserId(scoreId, userId)).thenReturn(Optional.empty());

        // Execute method under test
        Score returnedScore = null;
        try {
            returnedScore = scoreController.getScoreById(scoreId, userPrincipal);
            fail("Expected ScoreNotFoundException");
        } catch (ScoreNotFoundException ex) {
            // Exception thrown as expected
        }

        // Verify
        assertNull(returnedScore);
    }

    @Test
    public void deleteScoreById_Should_DeleteScore_When_ScoreWithIdExistsAndAdminUser() {
        // Define variables
        String scoreId = IdGenerator.createNewId();
        Score scoreOne = getScoreOne();
        scoreOne.setId(scoreId);
        User adminUser = new User();
        adminUser.setAdmin(true);
        UserPrincipal userPrincipal = new UserPrincipal(adminUser);

        // Set mock expectations

        // Execute method under test
        scoreController.deleteScoreById(scoreId, userPrincipal);

        // Verify
        verify(mockScoreRepository).deleteById(scoreId);
    }

    @Test
    public void deleteScoreById_Should_DeleteScore_When_ScoreWithIdExistsAndOwnedByUser() {
        // Define variables
        String scoreId = IdGenerator.createNewId();
        Score scoreOne = getScoreOne();
        scoreOne.setId(scoreId);
        String userId = IdGenerator.createNewId();
        User user = new User();
        user.setId(userId);
        UserPrincipal userPrincipal = new UserPrincipal(user);

        // Set mock expectations

        // Execute method under test
        scoreController.deleteScoreById(scoreId, userPrincipal);

        // Verify
        verify(mockScoreRepository).deleteByIdAndUserId(scoreId, userId);
    }

    @Test
    public void deleteScoreById_Should_DoNothing_When_ScoreNotFoundAndAdminUser() {
        // Define variables
        String scoreId = "1234";
        User adminUser = new User();
        adminUser.setAdmin(true);
        UserPrincipal userPrincipal = new UserPrincipal(adminUser);

        // Set mock expectations
        when(mockScoreRepository.findById(scoreId)).thenReturn(Optional.empty());

        // Execute method under test
        scoreController.deleteScoreById(scoreId, userPrincipal);

        // Verify
        verify(mockScoreRepository).deleteById(scoreId);
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

    private Score getScoreToAddWithoutDateTimeSet() {
        Score scoreToAdd = new Score();
        scoreToAdd.setValue(100);
        scoreToAdd.setUserId(PLAYER_ID_1);
        scoreToAdd.setRoutineId(ROUTINE_ID_1);
        return scoreToAdd;
    }
}
