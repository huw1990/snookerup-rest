package com.huwdunnit.snookeruprest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Models the various options for permutations of balls in a routine. Allows for flexibility of routines, e.g. some
 * routines have the "balls" option as "reds" (e.g. "The line up", where the changing factor is number of REDS)
 * whereas others have "balls" (e.g. "Long pots along blue spot", where the changing factor is number of BALLS).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balls {

    private List<Integer> options;

    private String unit;
}
