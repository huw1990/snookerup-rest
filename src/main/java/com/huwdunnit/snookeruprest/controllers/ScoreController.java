package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.db.UserRepository;
import com.huwdunnit.snookeruprest.exceptions.UserNotFoundException;
import com.huwdunnit.snookeruprest.model.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * REST Controller for Score endpoints.
 *
 * @author Huwdunnit
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/scores")
public class ScoreController {

    private final ScoreRepository scoreRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Score addScore(@RequestBody Score scoreToAdd) {
        log.debug("addScore score={}", scoreToAdd);

        String generatedUserId = IdGenerator.createNewId();
        scoreToAdd.setId(generatedUserId);

        if (scoreToAdd.getDateAndTime() == null) {
            log.debug("Score to add didn't have date/time set, so adding it now");
            scoreToAdd.setDateAndTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        }

        Score addedScore = scoreRepository.insert(scoreToAdd);

        log.debug("Returning new score {}", addedScore);
        return addedScore;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ScoreListResponse getAllScores(@RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
                                              @RequestParam(defaultValue = "50", name = "pageSize") int pageSize) {
        log.debug("getAllScores pageNumber={}, pageSize={}", pageNumber, pageSize);

        Pageable pageConstraints = PageRequest.of(pageNumber, pageSize);
        Page<Score> scoresPage = scoreRepository.findAll(pageConstraints);
        ScoreListResponse scoreListResponse = new ScoreListResponse(scoresPage);

        log.debug("Returning score list={}", scoreListResponse);
        return scoreListResponse;
    }
}
