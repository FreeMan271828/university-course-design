package org.freeman.ticketmanager.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private String content;
    private String type; // PUBLIC, INTERNAL
}