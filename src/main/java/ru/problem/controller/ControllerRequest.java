package ru.problem.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControllerRequest {
    private Duration sleep;
    @Builder.Default
    private boolean withNative = true;
    @Builder.Default
    private long bId = 1;
    @Builder.Default
    private boolean flushInsideB = false;

    @SuppressWarnings("WeakerAccess")
    public Map<String, List<String>> toParams() {
        HashMap<String, List<String>> params = new HashMap<>();
        if (getSleep() != null) {
            params.put("sleep", singletonList(getSleep().toString()));
        }
        params.put("withNative", singletonList(String.valueOf(isWithNative())));
        params.put("bId", singletonList(String.valueOf(getBId())));
        params.put("flushInsideB", singletonList(String.valueOf(isFlushInsideB())));
        return params;
    }
}
