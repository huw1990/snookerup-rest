package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.RoutineRepository;
import com.huwdunnit.snookeruprest.exceptions.RoutineNotFoundException;
import com.huwdunnit.snookeruprest.model.Routine;
import com.huwdunnit.snookeruprest.model.RoutineListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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
    private static final String LINEUP_DESC = """
            Arrange all reds in a line up the middle of the table, in line with the blue, pink, and black spots.
            
            Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.
            
            Can you clear the table?""";

    private static final String T_LINEUP_TITLE = "The T Line Up";
    private static final String T_LINEUP_DESC = """
            Arrange the reds in three lines of five reds, first between pink and black, then either side of the pink, to form a "T" shape.
            
            Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.
            
            In this routine, all reds are nearer the pink and black, so this replicates what you might see in a match, more than the Line Up would.
            
            Can you clear the table?""";

    private static final String CLEARING_COLOURS_TITLE = "Clearing the Colours";
    private static final String CLEARING_COLOURS_DESC = """
            Put all colours on their spots, then try to clear them in order, i.e. yellow, green, brown, blue, pink, black.""";
    private static final String TAG_BEGINNER = "beginner";
    private static final String TAG_INTER = "intermediate";
    private static final String TAG_BREAK_BUILDING = "break-building";
    private static final String TAG_POSITION = "positional-play";

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
        List<String> tagList = List.of(TAG_BEGINNER);

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
        return createRoutine(LINEUP_TITLE, LINEUP_DESC, List.of(TAG_INTER, TAG_BREAK_BUILDING, TAG_POSITION));
    }

    private Routine getTLineUpRoutine() {
        return createRoutine(T_LINEUP_TITLE, T_LINEUP_DESC, List.of(TAG_INTER, TAG_BREAK_BUILDING, TAG_POSITION));
    }

    private Routine getClearingTheColoursRoutine() {
        return createRoutine(CLEARING_COLOURS_TITLE, CLEARING_COLOURS_DESC, List.of(TAG_BEGINNER, TAG_POSITION));
    }

    private Routine createRoutine(String title, String description, List<String> tags) {
        Routine routine = new Routine();
        routine.setTitle(title);
        routine.setDescription(description);
        routine.setTags(tags);
        return routine;
    }
}
