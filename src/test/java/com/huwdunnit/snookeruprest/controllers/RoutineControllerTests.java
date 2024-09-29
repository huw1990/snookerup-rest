package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.RoutineRepository;
import com.huwdunnit.snookeruprest.exceptions.RoutineNotFoundException;
import com.huwdunnit.snookeruprest.model.Balls;
import com.huwdunnit.snookeruprest.model.Routine;
import com.huwdunnit.snookeruprest.model.RoutineListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the RoutineController class.
 *
 * @author Huwdunnit
 */
public class RoutineControllerTests {

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
    private static final String REDS_UNIT = "reds";
    private static final String ALL_COLOURS = "all";
    private static final String JUST_BLACK_COLOUR = "black";
    private static final String JUST_PINK_COLOUR = "pink";
    private static final String PINK_AND_BLACK_COLOURS = "pink,black";
    private static final String IMAGE_PATH_1 = "/path/to/image/1";
    private static final String IMAGE_PATH_2 = "/path/to/image/2";

    private RoutineRepository mockRoutineRepository;

    private RoutineController routineController;

    @BeforeEach
    public void beforeEach() {
        mockRoutineRepository = mock(RoutineRepository.class);

        routineController = new RoutineController(mockRoutineRepository);
    }

    @Test
    public void addRoutine_Should_AddRoutineAndReturnWithId() {
        // Define variables
        Routine routineToAdd = getLineUpRoutine();
        Routine expectedRoutine = getLineUpRoutine();
        expectedRoutine.setId(IdGenerator.createNewId());

        // Set mock expectations
        when(mockRoutineRepository.insert(any(Routine.class))).thenReturn(expectedRoutine);

        // Execute method under test
        Routine addedRoutine = routineController.addRoutine(routineToAdd);

        // Verify
        assertNotNull(addedRoutine);
        assertEquals(expectedRoutine, addedRoutine);

        verify(mockRoutineRepository).insert(any(Routine.class));
    }

    @Test
    public void addRoutine_Should_ThrowException_When_UserWithDuplicateEmailAdded() {
        // Define variables
        Routine routineToAdd = getLineUpRoutine();

        // Set mock expectations
        when(mockRoutineRepository.insert(any(Routine.class))).thenThrow(new DuplicateKeyException("Duplicate title"));

        // Execute method under test
        try {
            routineController.addRoutine(routineToAdd);
            fail("Expected DuplicateKeyException to be thrown");
        } catch (DuplicateKeyException ex) {
            // Exception expected, test passed
        }

        // Verify
    }

    @Test
    public void getRoutines_Should_RespondWithTwoRoutinesAndNoFurtherPages_When_OnlyTwoRoutinesInDb() {
        // Define variables
        Routine lineUpRoutine = getLineUpRoutine();
        lineUpRoutine.setId(IdGenerator.createNewId());
        Routine tLineUpRoutine = getTLineUpRoutine();
        tLineUpRoutine.setId(IdGenerator.createNewId());
        Page<Routine> mockRoutinesPage = mock(Page.class);

        // Set mock expectations
        when(mockRoutineRepository.findAll(any(Pageable.class))).thenReturn(mockRoutinesPage);
        when(mockRoutinesPage.getContent()).thenReturn(List.of(lineUpRoutine, tLineUpRoutine));
        when(mockRoutinesPage.getNumber()).thenReturn(0);
        when(mockRoutinesPage.getSize()).thenReturn(2);
        when(mockRoutinesPage.getTotalPages()).thenReturn(1);
        when(mockRoutinesPage.getTotalElements()).thenReturn(2L);

        // Execute method under test
        RoutineListResponse routinesResponse = routineController.getRoutines(0, 50, Optional.empty());

        // Verify
        assertEquals(2, routinesResponse.getRoutines().size());
        assertEquals(lineUpRoutine, routinesResponse.getRoutines().get(0));
        assertEquals(tLineUpRoutine, routinesResponse.getRoutines().get(1));
        assertEquals(0, routinesResponse.getPageNumber());
        assertEquals(2, routinesResponse.getPageSize());
        assertEquals(1, routinesResponse.getTotalPages());
        assertEquals(2L, routinesResponse.getTotalItems());
    }

    @Test
    public void getRoutines_Should_RespondWithEmptyList_When_NoRoutinesInDb() {
        // Define variables
        Page<Routine> mockRoutinesPage = mock(Page.class);

        // Set mock expectations
        when(mockRoutineRepository.findAll(any(Pageable.class))).thenReturn(mockRoutinesPage);
        when(mockRoutinesPage.getContent()).thenReturn(List.of());
        when(mockRoutinesPage.getNumber()).thenReturn(0);
        when(mockRoutinesPage.getSize()).thenReturn(0);
        when(mockRoutinesPage.getTotalPages()).thenReturn(1);
        when(mockRoutinesPage.getTotalElements()).thenReturn(0L);

        // Execute method under test
        RoutineListResponse routinesResponse = routineController.getRoutines(0, 50, Optional.empty());

        // Verify
        assertEquals(0, routinesResponse.getRoutines().size());
        assertEquals(0, routinesResponse.getPageNumber());
        assertEquals(0, routinesResponse.getPageSize());
        assertEquals(1, routinesResponse.getTotalPages());
        assertEquals(0L, routinesResponse.getTotalItems());
    }

    @Test
    public void getRoutines_Should_RespondWithTwoRoutinesAndOneFurtherPage_When_ThreeRoutinesInDb() {
        // Define variables
        Routine lineUpRoutine = getLineUpRoutine();
        lineUpRoutine.setId(IdGenerator.createNewId());
        Routine tLineUpRoutine = getTLineUpRoutine();
        tLineUpRoutine.setId(IdGenerator.createNewId());
        Page<Routine> mockRoutinesPage = mock(Page.class);

        // Set mock expectations
        when(mockRoutineRepository.findAll(any(Pageable.class))).thenReturn(mockRoutinesPage);
        when(mockRoutinesPage.getContent()).thenReturn(List.of(lineUpRoutine, tLineUpRoutine));
        when(mockRoutinesPage.getNumber()).thenReturn(0);
        when(mockRoutinesPage.getSize()).thenReturn(2);
        when(mockRoutinesPage.getTotalPages()).thenReturn(2);
        when(mockRoutinesPage.getTotalElements()).thenReturn(3L);

        // Execute method under test
        RoutineListResponse routinesResponse = routineController.getRoutines(0, 2, Optional.empty());

        // Verify
        assertEquals(2, routinesResponse.getRoutines().size());
        assertEquals(lineUpRoutine, routinesResponse.getRoutines().get(0));
        assertEquals(tLineUpRoutine, routinesResponse.getRoutines().get(1));
        assertEquals(0, routinesResponse.getPageNumber());
        assertEquals(2, routinesResponse.getPageSize());
        assertEquals(2, routinesResponse.getTotalPages());
        assertEquals(3L, routinesResponse.getTotalItems());
    }

    @Test
    public void getRoutines_Should_RespondWithOneRoutine_When_OnlyOneRoutineContainsRequestedTags() {
        // Define variables
        Routine clearingColoursRoutine = getClearingTheColoursRoutine();
        clearingColoursRoutine.setId(IdGenerator.createNewId());
        Page<Routine> mockRoutinesPage = mock(Page.class);
        List<String> tagList = List.of(TAG_BREAK_BUILDING);

        // Set mock expectations
        when(mockRoutineRepository.findByTagsIn(any(Pageable.class), eq(tagList))).thenReturn(mockRoutinesPage);
        when(mockRoutinesPage.getContent()).thenReturn(List.of(clearingColoursRoutine));
        when(mockRoutinesPage.getNumber()).thenReturn(0);
        when(mockRoutinesPage.getSize()).thenReturn(1);
        when(mockRoutinesPage.getTotalPages()).thenReturn(1);
        when(mockRoutinesPage.getTotalElements()).thenReturn(1L);

        // Execute method under test
        RoutineListResponse routinesResponse = routineController.getRoutines(0, 50, Optional.of(tagList));

        // Verify
        assertEquals(1, routinesResponse.getRoutines().size());
        assertEquals(clearingColoursRoutine, routinesResponse.getRoutines().get(0));
        assertEquals(0, routinesResponse.getPageNumber());
        assertEquals(1, routinesResponse.getPageSize());
        assertEquals(1, routinesResponse.getTotalPages());
        assertEquals(1L, routinesResponse.getTotalItems());
    }

    @Test
    public void getRoutineById_Should_ReturnRoutine_When_RoutineWithIdExists() {
        // Define variables
        String routineId = "1234";
        Routine lineUpRoutine = getLineUpRoutine();
        lineUpRoutine.setId(routineId);

        // Set mock expectations
        when(mockRoutineRepository.findById(routineId)).thenReturn(Optional.of(lineUpRoutine));

        // Execute method under test
        Routine returnedRoutine = routineController.getRoutineById(routineId);

        // Verify
        assertNotNull(returnedRoutine);
        assertEquals(lineUpRoutine, returnedRoutine);

        verify(mockRoutineRepository).findById(routineId);
    }

    @Test
    public void getRoutineById_Should_ThrowRoutineNotFoundException_When_RoutineNotFound() {
        // Define variables
        String routineId = "1234";

        // Set mock expectations
        when(mockRoutineRepository.findById(routineId)).thenReturn(Optional.empty());

        // Execute method under test
        Routine returnedRoutine = null;
        try {
            returnedRoutine = routineController.getRoutineById(routineId);
            fail("Expected RoutineNotFoundException");
        } catch (RoutineNotFoundException ex) {
            // Exception thrown as expected
        }

        // Verify
        assertNull(returnedRoutine);
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
                .tags(List.of(TAG_BREAK_BUILDING, TAG_POSITION))
                .cushionLimits(List.of(0, 3, 5, 7))
                .images(List.of(IMAGE_PATH_1))
                .canLoop(true)
                .build();
    }
}
