package com.huwdunnit.snookeruprest.db;

import com.huwdunnit.snookeruprest.model.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Spring Data MongoDB repository for the Score collection.
 *
 * @author Huwdunnit
 */
@Repository
public interface ScoreRepository extends MongoRepository<Score, String> {

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
}
