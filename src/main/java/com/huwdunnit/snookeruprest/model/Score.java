package com.huwdunnit.snookeruprest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Score {

    public static final String DATE_FORMAT = "dd/M/yyyy-HH:mm";

    @Id
    private String id;

    private int value;

    private String routineId;

    private String userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private LocalDateTime dateTime;

    /** The restriction on the number of cushions allowed to be hit on this routine attempt. */
    private Integer cushionLimit;

    /** The specific configuration of colours used on this routine attempt. */
    private String colours;

    /** The specific configuration of balls used on this routine attempt. */
    private Integer numBalls;

    /** Whether this score (and it's value) is the number of successful loops of a routine. */
    private boolean loop;
}
