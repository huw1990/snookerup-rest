package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.exceptions.ScoreNotFoundException;
import com.huwdunnit.snookeruprest.model.*;
import com.huwdunnit.snookeruprest.security.Roles;
import com.huwdunnit.snookeruprest.security.UserPrincipal;
import com.huwdunnit.snookeruprest.security.permissions.AdminPermission;
import com.huwdunnit.snookeruprest.security.permissions.UserOwnerOrAdminPermission;
import com.huwdunnit.snookeruprest.security.permissions.UserPermission;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.huwdunnit.snookeruprest.controllers.UserController.USERS_URL;

/**
 * REST Controller for Score endpoints.
 *
 * @author Huwdunnit
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ScoreController {

    private static final String SCORES_URL = "/api/v1/scores";

    private final ScoreRepository scoreRepository;

    @PostMapping(SCORES_URL)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('" + Roles.USER + "') && #scoreToAdd.getUserId() == principal.getId() || hasRole('" + Roles.ADMIN + "')")
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

    @GetMapping(USERS_URL + "/{userid}/scores")
    @ResponseStatus(HttpStatus.OK)
    @UserOwnerOrAdminPermission
    public ScoreListResponse getScoresForUser(@RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
                                       @RequestParam(defaultValue = "50", name = "pageSize") int pageSize,
                                       @RequestParam(name = "from") @DateTimeFormat(pattern = Score.DATE_FORMAT)
                                       Optional<LocalDateTime> from,
                                       @RequestParam(name = "to") @DateTimeFormat(pattern = Score.DATE_FORMAT)
                                       Optional<LocalDateTime> to, @PathVariable(name = "userid") @NotBlank String userId,
                                              @RequestParam(name = "routineId") Optional<String> routineId) {
        return getScoresCommon(pageNumber, pageSize, from, to, routineId, Optional.of(userId));
    }

    @GetMapping(SCORES_URL)
    @ResponseStatus(HttpStatus.OK)
    @AdminPermission
    public ScoreListResponse getScores(@RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
                                       @RequestParam(defaultValue = "50", name = "pageSize") int pageSize,
                                       @RequestParam(name = "from") @DateTimeFormat(pattern = Score.DATE_FORMAT)
                                           Optional<LocalDateTime> from,
                                       @RequestParam(name = "to") @DateTimeFormat(pattern = Score.DATE_FORMAT)
                                           Optional<LocalDateTime> to,
                                       @RequestParam(name = "routineId") Optional<String> routineId) {
        return getScoresCommon(pageNumber, pageSize, from, to, routineId, Optional.empty());
    }

    private ScoreListResponse getScoresCommon(int pageNumber, int pageSize, Optional<LocalDateTime> from, Optional<LocalDateTime> to,
                                              Optional<String> routineId, Optional<String> userId) {
                log.debug("getScores pageNumber={}, pageSize={} from={} to={} routineId={} userId={}",
                pageNumber, pageSize, from, to, routineId, userId);


        Pageable pageConstraints = PageRequest.of(pageNumber, pageSize);
        Page<Score> scoresPage;

        if (from.isPresent() && to.isPresent()) {
            // Querying for scores between a date range
            scoresPage = scoreRepository.findBetweenDatesWithOptionalRoutineIdAndUserId(pageConstraints, from.get(), to.get(), routineId, userId);
        } else if (from.isPresent()) {
            // Querying for scores from a particular date
            scoresPage = scoreRepository.findFromDateWithOptionalRoutineIdAndUserId(pageConstraints, from.get(), routineId, userId);
        } else if (to.isPresent()) {
            // Querying for scores up to a particular date
            scoresPage = scoreRepository.findToDateWithOptionalRoutineIdAndUserId(pageConstraints, to.get(), routineId, userId);
        } else {
            // Querying for scores without a date range
            scoresPage = scoreRepository.findWithOptionalRoutineIdAndUserId(pageConstraints, routineId, userId);
        }

        ScoreListResponse scoreListResponse = new ScoreListResponse(scoresPage);

        log.debug("Returning score list={}", scoreListResponse);
        return scoreListResponse;
    }

    @GetMapping(SCORES_URL + "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @UserPermission
    public Score getScoreById(@PathVariable(name = "id") @NotBlank String scoreId,
                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.debug("getScoreById scoreId={}", scoreId);

        Score scoreResponse;
        if (userPrincipal.isAdmin()) {
            // User is an admin, so just get the score by ID
            scoreResponse = scoreRepository.findById(scoreId).orElseThrow(
                    () -> new ScoreNotFoundException("Score not found, ID=" + scoreId, scoreId));
        } else {
            // User is not an admin, so only return the score if the user ID matches the principal's ID
            scoreResponse = scoreRepository.findByIdAndUserId(scoreId, userPrincipal.getId()).orElseThrow(
                    () -> new ScoreNotFoundException("Score not found, ID=" + scoreId, scoreId));
        }

        log.debug("Returning score={}", scoreResponse);
        return scoreResponse;
    }

    @DeleteMapping(SCORES_URL + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @UserPermission
    public void deleteScoreById(@PathVariable(name = "id") @NotBlank String scoreId,
                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.debug("deleteScoreById scoreId={}", scoreId);

        if (userPrincipal.isAdmin()) {
            // User is an admin, so just delete the score by ID
            scoreRepository.deleteById(scoreId);
        } else {
            // User is not an admin, so only delete the score if the user ID matches the principal's ID
            scoreRepository.deleteByIdAndUserId(scoreId, userPrincipal.getId());
        }
    }
}
