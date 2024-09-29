package com.huwdunnit.snookeruprest.db;

import com.huwdunnit.snookeruprest.model.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
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
     * Get all scores where the date is between the "from" and "to" dates.
     * @param pageConstraints Constraints for paging
     * @param from From date
     * @param to To date
     * @return All scores within the provided dates
     */
    Page<Score> findByDateTimeBetween(Pageable pageConstraints, LocalDateTime from, LocalDateTime to);

    /**
     * Get all scores where the date is after the "from" date.
     * @param pageConstraints Constraints for paging
     * @param from From date
     * @return All scores after the provided date
     */
    Page<Score> findByDateTimeAfter(Pageable pageConstraints, LocalDateTime from);

    /**
     * Get all scores where the date is before the "to" date.
     * @param pageConstraints Constraints for paging
     * @param to To date
     * @return All scores before the provided date
     */
    Page<Score> findByDateTimeBefore(Pageable pageConstraints, LocalDateTime to);

    /*
     * QUERIES WITH OPTIONAL PARAMETERS:
     * The below queries use raw MongoDB syntax to allow us to pass in optional parameters, which we can't do with
     * named queries (i.e. methods such as "findByFooAndBar(String foo, String bar)"). The MongoDB syntax looks
     * complicated, but it's actually just following the format:
     *
     * {
     *   $and: [
     *     {
     *       $or : [
     *         { $expr: { $eq: ['?0', 'null'] } } ,
     *         { foo : ?0 }
     *       ]
     *     },
     *     {
     *       $or : [
     *         { $expr: { $eq: ['?1', 'null'] } } ,
     *         { bar : ?1 }
     *       ]
     *     }
     *   ]
     * }
     *
     * i.e. either the provided param ("foo" or "bar") is null, OR match records that contain the provided value for
     * "foo" or "bar".
     *
     * There are multiple similar queries to account for a date range (i.e. without a date range, up to a particular
     * date, from a particular date, or between a particular date.
     *
     */

    /**
     * Find all scores with optional routine ID and user ID.
     * @param pageConstraints Constraints for paging
     * @param routineId Routine ID. Can be null.
     * @param userId User ID. Can be null.
     * @param cushionLimit The cushion limit on the score. Can be null.
     * @param colours Any colours restraints applied to the score. Can be null.
     * @param numBalls The configured number of balls applied to the routine. Can be null.
     * @param loop Specifies whether this score is with looping of the routine. Can be null.
     * @return Returns all scores where fields match provided parameters, and non-provided optional parameters are
     *         ignored.
     */
    @Query("{ $and: [ { $or : [ { $expr: { $eq: ['?0', 'null'] } } , { routineId : ?0 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?1', 'null'] } } , { userId : ?1 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?2', 'null'] } } , { cushionLimit : ?2 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?3', 'null'] } } , { colours : ?3 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?4', 'null'] } } , { numBalls : ?4 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?5', 'null'] } } , { loop : ?5 } ] } " +
                   "] } ")
    Page<Score> findWithOptionalRoutineIdAndUserIdAndScoreParams(Pageable pageConstraints,
                                                   Optional<String> routineId,
                                                   Optional<String> userId,
                                                   Optional<Integer> cushionLimit,
                                                   Optional<String> colours,
                                                   Optional<Integer> numBalls,
                                                   Optional<Boolean> loop);

    /**
     * Find all scores up to a provided date, with optional routine ID and user ID.
     * @param pageConstraints Constraints for paging
     * @param to Date/time to get scores up to
     * @param routineId Routine ID. Can be null.
     * @param userId User ID. Can be null.
     * @param cushionLimit The cushion limit on the score. Can be null.
     * @param colours Any colours restraints applied to the score. Can be null.
     * @param numBalls The configured number of balls applied to the routine. Can be null.
     * @param loop Specifies whether this score is with looping of the routine. Can be null.
     * @return Returns all scores up to provided date where fields match provided parameters, and non-provided optional
     *         parameters are ignored.
     */
    @Query("{ $and: [ { dateTime: { $lte: { $date: '?0' } } }," +
                     "{ $or : [ { $expr: { $eq: ['?1', 'null'] } } , { routineId : ?1 } ] }," +
                     "{ $or : [ { $expr: { $eq: ['?2', 'null'] } } , { userId : ?2 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?3', 'null'] } } , { cushionLimit : ?3 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?4', 'null'] } } , { colours : ?4 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?5', 'null'] } } , { numBalls : ?5 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?6', 'null'] } } , { loop : ?6 } ] } " +
                   "] }")
    Page<Score> findToDateWithOptionalRoutineIdAndUserIdAndScoreParams(Pageable pageConstraints,
                                                         LocalDateTime to,
                                                         Optional<String> routineId,
                                                         Optional<String> userId,
                                                         Optional<Integer> cushionLimit,
                                                         Optional<String> colours,
                                                         Optional<Integer> numBalls,
                                                         Optional<Boolean> loop);

    /**
     * Find all scores from a provided date, with optional routine ID and user ID.
     * @param pageConstraints Constraints for paging
     * @param from Date/time to get scores from
     * @param routineId Routine ID. Can be null.
     * @param userId User ID. Can be null.
     * @param cushionLimit The cushion limit on the score. Can be null.
     * @param colours Any colours restraints applied to the score. Can be null.
     * @param numBalls The configured number of balls applied to the routine. Can be null.
     * @param loop Specifies whether this score is with looping of the routine. Can be null.
     * @return Returns all scores from provided date where fields match provided parameters, and non-provided optional
     *         parameters are ignored.
     */
    @Query("{ $and: [ { dateTime: { $gte: { $date: '?0' } } }," +
                     "{ $or : [ { $expr: { $eq: ['?1', 'null'] } } , { routineId : ?1 } ] }," +
                     "{ $or : [ { $expr: { $eq: ['?2', 'null'] } } , { userId : ?2 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?3', 'null'] } } , { cushionLimit : ?3 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?4', 'null'] } } , { colours : ?4 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?5', 'null'] } } , { numBalls : ?5 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?6', 'null'] } } , { loop : ?6 } ] } " +
                   "] }")
    Page<Score> findFromDateWithOptionalRoutineIdAndUserIdAndScoreParams(Pageable pageConstraints,
                                                           LocalDateTime from,
                                                           Optional<String> routineId,
                                                           Optional<String> userId,
                                                           Optional<Integer> cushionLimit,
                                                           Optional<String> colours,
                                                           Optional<Integer> numBalls,
                                                           Optional<Boolean> loop);

    /**
     * Find all scores between two provided dates, with optional routine ID and user ID.
     * @param pageConstraints Constraints for paging
     * @param from Start point in date range to get scores between
     * @param to End point in date range to get scores between
     * @param routineId Routine ID. Can be null.
     * @param userId User ID. Can be null.
     * @param cushionLimit The cushion limit on the score. Can be null.
     * @param colours Any colours restraints applied to the score. Can be null.
     * @param numBalls The configured number of balls applied to the routine. Can be null.
     * @param loop Specifies whether this score is with looping of the routine. Can be null.
     * @return Returns all scores between provided dates where fields match provided parameters, and non-provided optional
     *         parameters are ignored.
     */
    @Query("{ $and: [ { dateTime: { $gte: { $date: '?0' }, $lte: { $date: '?1' } } }," +
                     "{ $or : [ { $expr: { $eq: ['?2', 'null'] } } , { routineId : ?2 } ] }," +
                     "{ $or : [ { $expr: { $eq: ['?3', 'null'] } } , { userId : ?3 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?4', 'null'] } } , { cushionLimit : ?4 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?5', 'null'] } } , { colours : ?5 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?6', 'null'] } } , { numBalls : ?6 } ] }, " +
                     "{ $or : [ { $expr: { $eq: ['?7', 'null'] } } , { loop : ?7 } ] } " +
                   "] }")
    Page<Score> findBetweenDatesWithOptionalRoutineIdAndUserIdAndScoreParams(Pageable pageConstraints,
                                                               LocalDateTime from,
                                                               LocalDateTime to,
                                                               Optional<String> routineId,
                                                               Optional<String> userId,
                                                               Optional<Integer> cushionLimit,
                                                               Optional<String> colours,
                                                               Optional<Integer> numBalls,
                                                               Optional<Boolean> loop);
}
