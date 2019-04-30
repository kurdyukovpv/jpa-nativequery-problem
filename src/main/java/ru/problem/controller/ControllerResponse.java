package ru.problem.controller;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class ControllerResponse {
    private String result;
    private String thread;
    private Duration duration;
}
