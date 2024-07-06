package com.huwdunnit.snookeruprest.db;

import com.huwdunnit.snookeruprest.model.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the Score collection.
 *
 * @author Huwdunnit
 */
@Repository
public interface ScoreRepository extends MongoRepository<Score, String> {

    /**
     * Delete the score from the database with the provided ID, but only if the provided user ID also matches.
     * @param id The score ID to delete
     * @param userId The user ID that must also be present on the score in order for the delete to occur
     */
    void deleteByIdAndUserId(String id, String userId);

    /**
     * Get the score from the database with the provided ID, but only if the provided user ID also matches.
     * @param id The ID of the score to get
     * @param userId The user ID that must also be present on the score in order for the retrieval to succeed
     * @return An Optional containing the returned score
     */
    Optional<Score> findByIdAndUserId(String id, String userId);

    /**
     * Get all scores for the provided user ID.
     * @param pageConstraints Constraints for paging
     * @param userId The user ID to get scores for
     * @return All scores for the provided user
     */
    Page<Score> findByUserId(Pageable pageConstraints, String userId);

    /**
     * Get all scores for the provided routine ID.
     * @param pageConstraints Constraints for paging
     * @param routineId The routine ID to get scores for
     * @return All scores for the provided routine
     */
    Page<Score> findByRoutineId(Pageable pageConstraints, String routineId);

    /**
     * Get all scores for the provided user ID and routine ID.
     * @param pageConstraints Constraints for paging
     * @param userId The user ID to get scores for
     * @param routineId The routine ID to get scores for
     * @return All scores for the provided user and routine
     */
    Page<Score> findByUserIdAndRoutineId(Pageable pageConstraints, String userId, String routineId);

    /**
     * Get all scores for a user where the date is between the "from" and "to" dates.
     * @param pageConstraints Constraints for paging
     * @param userId The user ID to get scores for
     * @param from From date
     * @param to To date
     * @return All scores for the provided user within the provided dates
     */
    Page<Score> findByUserIdAndDateTimeBetween(Pageable pageConstraints, String userId, LocalDateTime from, LocalDateTime to);

    /**
     * Get all scores for a user and routine where the date is between the "from" and "to" dates.
     * @param pageConstraints Constraints for paging
     * @param userId The user ID to get scores for
     * @param routineId The routine ID to get scores for
     * @param from From date
     * @param to To date
     * @return All scores for the provided user and routine within the provided dates
     */
    Page<Score> findByUserIdAndRoutineIdAndDateTimeBetween(Pageable pageConstraints, String userId, String routineId, LocalDateTime from, LocalDateTime to);

    /**
     * Get all scores for a user where the date is after the "from" date.
     * @param pageConstraints Constraints for paging
     * @param userId The user ID to get scores for
     * @param from From date
     * @return All scores for the provided user after the provided date
     */
    Page<Score> findByUserIdAndDateTimeAfter(Pageable pageConstraints, String userId, LocalDateTime from);

    /**
     * Get all scores for a user and routine where the date is after the "from" date.
     * @param pageConstraints Constraints for paging
     * @param userId The user ID to get scores for
     * @param routineId The routine ID to get scores for
     * @param from From date
     * @return All scores for the provided user and routine after the provided date
     */
    Page<Score> findByUserIdAndRoutineIdAndDateTimeAfter(Pageable pageConstraints, String userId, String routineId, LocalDateTime from);

    /**
     * Get all scores for a user where the date is before the "to" date.
     * @param pageConstraints Constraints for paging
     * @param userId The user ID to get scores for
     * @param to To date
     * @return All scores for the provided user before the provided date
     */
    Page<Score> findByUserIdAndDateTimeBefore(Pageable pageConstraints, String userId, LocalDateTime to);

    /**
     * Get all scores for a user and routine where the date is before the "to" date.
     * @param pageConstraints Constraints for paging
     * @param userId The user ID to get scores for
     * @param routineId The routine ID to get scores for
     * @param to To date
     * @return All scores for the provided user and routine before the provided date
     */
    Page<Score> findByUserIdAndRoutineIdAndDateTimeBefore(Pageable pageConstraints, String userId, String routineId, LocalDateTime to);

    /**
     * Get all scores where the date is between the "from" and "to" dates.
     * @param pageConstraints Constraints for paging
     * @param from From date
     * @param to To date
     * @return All scores within the provided dates
     */
    Page<Score> findByDateTimeBetween(Pageable pageConstraints, LocalDateTime from, LocalDateTime to);

    /**
     * Get all scores where the date is between the "from" and "to" dates.
     * @param pageConstraints Constraints for paging
     * @param from From date
     * @param to To date
     * @return All scores within the provided dates
     */
    Page<Score> findByRoutineIdAndDateTimeBetween(Pageable pageConstraints, String routineId, LocalDateTime from, LocalDateTime to);

    /**
     * Get all scores where the date is after the "from" date.
     * @param pageConstraints Constraints for paging
     * @param from From date
     * @return All scores after the provided date
     */
    Page<Score> findByDateTimeAfter(Pageable pageConstraints, LocalDateTime from);

    /**
     * Get all scores where the date is after the "from" date.
     * @param pageConstraints Constraints for paging
     * @param from From date
     * @return All scores after the provided date
     */
    Page<Score> findByRoutineIdAndDateTimeAfter(Pageable pageConstraints, String routineId, LocalDateTime from);

    /**
     * Get all scores where the date is before the "to" date.
     * @param pageConstraints Constraints for paging
     * @param to To date
     * @return All scores before the provided date
     */
    Page<Score> findByDateTimeBefore(Pageable pageConstraints, LocalDateTime to);

    /**
     * Get all scores where the date is before the "to" date.
     * @param pageConstraints Constraints for paging
     * @param to To date
     * @return All scores before the provided date
     */
    Page<Score> findByRoutineIdAndDateTimeBefore(Pageable pageConstraints, String routineId, LocalDateTime to);
}
