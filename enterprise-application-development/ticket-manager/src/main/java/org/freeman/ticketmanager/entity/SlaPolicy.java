package org.freeman.ticketmanager.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SlaPolicy implements Serializable {
    private String priority;
    private Integer responseHours;
    private Integer resolveHours;
}
