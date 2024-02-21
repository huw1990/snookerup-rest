package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.RoutineRepository;
import com.huwdunnit.snookeruprest.model.Routine;
import com.huwdunnit.snookeruprest.model.RoutineListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Routine endpoints.
 *
 * @author Huwdunnit
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/routines")
public class RoutineController {

    private final RoutineRepository routineRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public RoutineListResponse getAllRoutines(@RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
                                        @RequestParam(defaultValue = "50", name = "pageSize") int pageSize) {
        log.debug("getAllRoutines pageNumber={}, pageSize={}", pageNumber, pageSize);

        Pageable pageConstraints = PageRequest.of(pageNumber, pageSize);
        Page<Routine> routinesPage = routineRepository.findAll(pageConstraints);
        RoutineListResponse routineListResponse = new RoutineListResponse(routinesPage);

        log.debug("Returning routine list={}", routineListResponse);
        return routineListResponse;
    }
}
