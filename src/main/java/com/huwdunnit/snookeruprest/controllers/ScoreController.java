package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.exceptions.ScoreNotFoundException;
import com.huwdunnit.snookeruprest.model.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

        if (scoreToAdd.getDateTime() == null) {
            log.debug("Score to add didn't have date/time set, so adding it now");
            scoreToAdd.setDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        }

        Score addedScore = scoreRepository.insert(scoreToAdd);

        log.debug("Returning new score {}", addedScore);
        return addedScore;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ScoreListResponse getScores(@RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
                                       @RequestParam(defaultValue = "50", name = "pageSize") int pageSize,
                                       @RequestParam(name = "from") @DateTimeFormat(pattern = Score.DATE_FORMAT)
                                           Optional<LocalDateTime> from,
                                       @RequestParam(name = "to") @DateTimeFormat(pattern = Score.DATE_FORMAT)
                                           Optional<LocalDateTime> to) {
        log.debug("getScores pageNumber={}, pageSize={} startDate={} endDate={}",
                pageNumber, pageSize, from, to);


        Pageable pageConstraints = PageRequest.of(pageNumber, pageSize);
        Page<Score> scoresPage;
        if (from.isPresent() && to.isPresent()) {
            scoresPage = scoreRepository.findByDateTimeBetween(pageConstraints, from.get(), to.get());
        } else if (from.isPresent()) {
            scoresPage = scoreRepository.findByDateTimeAfter(pageConstraints, from.get());
        } else if (to.isPresent()) {
            scoresPage = scoreRepository.findByDateTimeBefore(pageConstraints, to.get());
        } else {
            scoresPage = scoreRepository.findAll(pageConstraints);
        }
        ScoreListResponse scoreListResponse = new ScoreListResponse(scoresPage);

        log.debug("Returning score list={}", scoreListResponse);
        return scoreListResponse;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Score getScoreById(@PathVariable(name = "id") @NotBlank String scoreId) {
        log.debug("getScoreById scoreId={}", scoreId);

        Score scoreResponse = scoreRepository.findById(scoreId).orElseThrow(
                () -> new ScoreNotFoundException("Score not found, ID=" + scoreId, scoreId));

        log.debug("Returning score={}", scoreResponse);
        return scoreResponse;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteScoreById(@PathVariable(name = "id") @NotBlank String scoreId) {
        log.debug("deleteScoreById scoreId={}", scoreId);

        scoreRepository.deleteById(scoreId);
    }
}
