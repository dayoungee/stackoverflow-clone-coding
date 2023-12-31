package com.rainbow.sof.domain.answer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AnswerVoteDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private int vote;
    }
}
