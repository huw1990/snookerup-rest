package com.huwdunnit.snookeruprest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Models a pageable list of scores.
 *
 * @author Huwdunnit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreListResponse {

    private List<Score> scores;

    private Integer pageSize;

    private Integer pageNumber;

    private Integer totalPages;

    private Long totalItems;

    public ScoreListResponse(Page<Score> pageOfScores) {
        this.scores = pageOfScores.getContent();
        this.pageSize = pageOfScores.getSize();
        this.pageNumber = pageOfScores.getNumber();
        this.totalPages = pageOfScores.getTotalPages();
        this.totalItems = pageOfScores.getTotalElements();
    }
}
