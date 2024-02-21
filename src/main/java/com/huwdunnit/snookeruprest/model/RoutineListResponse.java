package com.huwdunnit.snookeruprest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Models a pageable list of routines.
 *
 * @author Huwdunnit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineListResponse {

    private List<Routine> routines;

    private Integer pageSize;

    private Integer pageNumber;

    private Integer totalPages;

    private Long totalItems;

    public RoutineListResponse(Page<Routine> pageOfRoutines) {
        this.routines = pageOfRoutines.getContent();
        this.pageSize = pageOfRoutines.getSize();
        this.pageNumber = pageOfRoutines.getNumber();
        this.totalPages = pageOfRoutines.getTotalPages();
        this.totalItems = pageOfRoutines.getTotalElements();
    }
}
