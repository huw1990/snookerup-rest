package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.exceptions.ScoreNotFoundException;
import com.huwdunnit.snookeruprest.model.Score;
import com.huwdunnit.snookeruprest.model.ScoreListResponse;
import com.huwdunnit.snookeruprest.model.User;
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
        scoreToAdd.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));
        Score expectedScore = getScoreToAddWithoutDateTimeSet();
        expectedScore.setDateTime(LocalDateTime.parse(DATE_STRING_1, DATE_FORMATTER));

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
        expectedScore.setDateTime(LocalDateTime.now());

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
    public void getScoresForUser_Should_RespondWithOneScore_When_OnlyOneScoreInDbForUser() {
        // Define variables
        Score scoreTwo = getScoreTwo();
        scoreTwo.setId(IdGenerator.createNewId());
        Page<Score> mockScoresPage = mock(Page.class);
        String toDateString = "27/2/2024-00:00";
        LocalDateTime toDate = LocalDateTime.parse(toDateString, DATE_FORMATTER);

        // Set mock expectations
        when(mockScoreRepository.findByUserId(any(Pageable.class), eq(PLAYER_ID_2))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScoresForUser(0, 50, Optional.empty(), Optional.empty(), PLAYER_ID_2, Optional.empty());

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
        when(mockScoreRepository.findByUserIdAndRoutineId(any(Pageable.class), eq(PLAYER_ID_2), eq(ROUTINE_ID_2))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScoresForUser(0, 50, Optional.empty(), Optional.empty(), PLAYER_ID_2, Optional.of(ROUTINE_ID_2));

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
        when(mockScoreRepository.findByUserIdAndRoutineId(any(Pageable.class), eq(PLAYER_ID_2), eq(ROUTINE_ID_1))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of());
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(0);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(0L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScoresForUser(0, 50, Optional.empty(), Optional.empty(), PLAYER_ID_2, Optional.of(ROUTINE_ID_1));

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
        when(mockScoreRepository.findAll(any(Pageable.class))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne, scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(2);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(2L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(0, 50, Optional.empty(), Optional.empty(), Optional.empty());

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
        when(mockScoreRepository.findByRoutineId(any(Pageable.class), eq(ROUTINE_ID_2))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(0, 50, Optional.empty(), Optional.empty(), Optional.of(ROUTINE_ID_2));

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
        when(mockScoreRepository.findAll(any(Pageable.class))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of());
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(0);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(0L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(0, 50, Optional.empty(), Optional.empty(), Optional.empty());

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
        when(mockScoreRepository.findAll(any(Pageable.class))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne, scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(2);
        when(mockScoresPage.getTotalPages()).thenReturn(2);
        when(mockScoresPage.getTotalElements()).thenReturn(3L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(0, 2, Optional.empty(), Optional.empty(), Optional.empty());

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
        when(mockScoreRepository.findAll(any(Pageable.class))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne, scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(2);
        when(mockScoresPage.getTotalPages()).thenReturn(2);
        when(mockScoresPage.getTotalElements()).thenReturn(3L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(0, 2, Optional.empty(), Optional.empty(), Optional.empty());

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
        when(mockScoreRepository.findByDateTimeBefore(any(Pageable.class), eq(toDate))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreOne));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(0, 2, Optional.empty(), Optional.of(toDate), Optional.empty());

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
        when(mockScoreRepository.findByDateTimeAfter(any(Pageable.class), eq(fromDate))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(0, 2, Optional.of(fromDate), Optional.empty(), Optional.empty());

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
        when(mockScoreRepository.findByDateTimeBetween(any(Pageable.class), eq(fromDate), eq(toDate))).thenReturn(mockScoresPage);
        when(mockScoresPage.getContent()).thenReturn(List.of(scoreTwo));
        when(mockScoresPage.getNumber()).thenReturn(0);
        when(mockScoresPage.getSize()).thenReturn(1);
        when(mockScoresPage.getTotalPages()).thenReturn(1);
        when(mockScoresPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        ScoreListResponse scoresResponse = scoreController.getScores(0, 50, Optional.of(fromDate), Optional.of(toDate), Optional.empty());

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
