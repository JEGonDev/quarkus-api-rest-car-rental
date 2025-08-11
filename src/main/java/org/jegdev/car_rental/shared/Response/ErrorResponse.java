package org.jegdev.car_rental.shared.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;
    private final Instant timestamp;
    private final String path;
    private final List<String> details;
}
