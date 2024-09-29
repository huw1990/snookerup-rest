package com.huwdunnit.snookeruprest.controllers;

import com.huwdunnit.snookeruprest.db.IdGenerator;
import com.huwdunnit.snookeruprest.db.RoutineRepository;
import com.huwdunnit.snookeruprest.db.ScoreRepository;
import com.huwdunnit.snookeruprest.exceptions.InvalidScoreFieldException;
import com.huwdunnit.snookeruprest.exceptions.RoutineForScoreNotFoundException;
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

    private final RoutineRepository routineRepository;

    @PostMapping(SCORES_URL)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('" + Roles.USER + "') && #scoreToAdd.getUserId() == principal.getId() || hasRole('" + Roles.ADMIN + "')")
    public Score addScore(@RequestBody Score scoreToAdd) {
        log.debug("addScore score={}", scoreToAdd);

        // Get the routine from the DB so we can validate input fields on the score against the routine
        String routineId = scoreToAdd.getRoutineId();
        Routine routine = routineRepository.findById(routineId).orElseThrow(
                () -> new RoutineForScoreNotFoundException("Invalid routine ID " + routineId, routineId));

        // Validate params used on the score are allowed on the routine
        if (scoreToAdd.getCushionLimit() != null
                && (routine.getCushionLimits() == null || !routine.getCushionLimits().contains(scoreToAdd.getCushionLimit()))) {
            throw new InvalidScoreFieldException("Field cushionLimit on score not allowed with selected routine", "cushionLimit");
        }
        if (scoreToAdd.getColours() != null
                && (routine.getColours() == null || !routine.getColours().contains(scoreToAdd.getColours()))) {
            throw new InvalidScoreFieldException("Field colours on score not allowed with selected routine", "colours");
        }
        if (scoreToAdd.getNumBalls() != null
                && (routine.getBalls() == null || !routine.getBalls().getOptions().contains(scoreToAdd.getNumBalls()))) {
            throw new InvalidScoreFieldException("Field numBalls on score not allowed with selected routine", "numBalls");
        }
        if (scoreToAdd.isLoop() && !routine.isCanLoop()) {
            throw new InvalidScoreFieldException("Field loop on score not allowed with selected routine", "loop");
        }

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
                                       @RequestParam(name = "from") @DateTimeFormat(pattern = Score.DATE_FORMAT) Optional<LocalDateTime> from,
                                       @RequestParam(name = "to") @DateTimeFormat(pattern = Score.DATE_FORMAT) Optional<LocalDateTime> to,
                                       @PathVariable(name = "userid") @NotBlank String userId,
                                       @RequestParam(name = "routineId") Optional<String> routineId,
                                       @RequestParam(name = "cushionLimit") Optional<Integer> cushionLimit,
                                       @RequestParam(name = "colours") Optional<String> colours,
                                       @RequestParam(name = "numBalls") Optional<Integer> numBalls,
                                       @RequestParam(name = "loop") Optional<Boolean> loop) {
        return getScoresCommon(pageNumber, pageSize, from, to, routineId, Optional.of(userId), cushionLimit, colours, numBalls, loop);
    }

    @GetMapping(SCORES_URL)
    @ResponseStatus(HttpStatus.OK)
    @AdminPermission
    public ScoreListResponse getScores(@RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
                                       @RequestParam(defaultValue = "50", name = "pageSize") int pageSize,
                                       @RequestParam(name = "from") @DateTimeFormat(pattern = Score.DATE_FORMAT) Optional<LocalDateTime> from,
                                       @RequestParam(name = "to") @DateTimeFormat(pattern = Score.DATE_FORMAT) Optional<LocalDateTime> to,
                                       @RequestParam(name = "routineId") Optional<String> routineId,
                                       @RequestParam(name = "cushionLimit") Optional<Integer> cushionLimit,
                                       @RequestParam(name = "colours") Optional<String> colours,
                                       @RequestParam(name = "numBalls") Optional<Integer> numBalls,
                                       @RequestParam(name = "loop") Optional<Boolean> loop) {
        return getScoresCommon(pageNumber, pageSize, from, to, routineId, Optional.empty(), cushionLimit, colours, numBalls, loop);
    }

    private ScoreListResponse getScoresCommon(int pageNumber,
                                              int pageSize,
                                              Optional<LocalDateTime> from,
                                              Optional<LocalDateTime> to,
                                              Optional<String> routineId,
                                              Optional<String> userId,
                                              Optional<Integer> cushionLimit,
                                              Optional<String> colours,
                                              Optional<Integer> numBalls,
                                              Optional<Boolean> loop) {
                log.debug("getScores pageNumber={}, pageSize={} from={} to={} routineId={} userId={} cushionLimit={} colours={} numBalls={} loop={}",
                pageNumber, pageSize, from, to, routineId, userId, cushionLimit, colours, numBalls, loop);


        Pageable pageConstraints = PageRequest.of(pageNumber, pageSize);
        Page<Score> scoresPage;

        if (from.isPresent() && to.isPresent()) {
            // Querying for scores between a date range
            scoresPage = scoreRepository.findBetweenDatesWithOptionalRoutineIdAndUserIdAndScoreParams(pageConstraints,
                    from.get(),
                    to.get(),
                    routineId,
                    userId,
                    cushionLimit,
                    colours,
                    numBalls,
                    loop);
        } else if (from.isPresent()) {
            // Querying for scores from a particular date
            scoresPage = scoreRepository.findFromDateWithOptionalRoutineIdAndUserIdAndScoreParams(pageConstraints,
                    from.get(),
                    routineId,
                    userId,
                    cushionLimit,
                    colours,
                    numBalls,
                    loop);
        } else if (to.isPresent()) {
            // Querying for scores up to a particular date
            scoresPage = scoreRepository.findToDateWithOptionalRoutineIdAndUserIdAndScoreParams(pageConstraints,
                    to.get(),
                    routineId,
                    userId,
                    cushionLimit,
                    colours,
                    numBalls,
                    loop);
        } else {
            // Querying for scores without a date range
            scoresPage = scoreRepository.findWithOptionalRoutineIdAndUserIdAndScoreParams(pageConstraints,
                    routineId,
                    userId,
                    cushionLimit,
                    colours,
                    numBalls,
                    loop);
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
