package com.fawkes.api.DTOs.Request;

import lombok.Data;

@Data
public class SelectWinnerRequest {
    private Long proposalId;
    private String justification;
}
