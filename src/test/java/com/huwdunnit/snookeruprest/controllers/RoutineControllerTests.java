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

    private RoutineRepository mockRoutineRepository;

    private RoutineController routineController;

    @BeforeEach
    public void beforeEach() {
        mockRoutineRepository = mock(RoutineRepository.class);

        routineController = new RoutineController(mockRoutineRepository);
    }

    @Test
    public void getAllRoutines_Should_RespondWithTwoRoutinesAndNoFurtherPages_When_OnlyTwoRoutinesInDb() {
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
        RoutineListResponse routinesResponse = routineController.getAllRoutines(0, 50);

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
    public void getAllRoutines_Should_RespondWithEmptyList_When_NoRoutinesInDb() {
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
        RoutineListResponse routinesResponse = routineController.getAllRoutines(0, 50);

        // Verify
        assertEquals(0, routinesResponse.getRoutines().size());
        assertEquals(0, routinesResponse.getPageNumber());
        assertEquals(0, routinesResponse.getPageSize());
        assertEquals(1, routinesResponse.getTotalPages());
        assertEquals(0L, routinesResponse.getTotalItems());
    }

    @Test
    public void getAllRoutines_Should_RespondWithTwoRoutinesAndOneFurtherPage_When_ThreeRoutinesInDb() {
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
        RoutineListResponse routinesResponse = routineController.getAllRoutines(0, 2);

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
        return createRoutine(LINEUP_TITLE, LINEUP_DESC);
    }

    private Routine getTLineUpRoutine() {
        return createRoutine(T_LINEUP_TITLE, T_LINEUP_DESC);
    }

    private Routine createRoutine(String title, String description) {
        Routine routine = new Routine();
        routine.setTitle(title);
        routine.setDescription(description);
        return routine;
    }
}
