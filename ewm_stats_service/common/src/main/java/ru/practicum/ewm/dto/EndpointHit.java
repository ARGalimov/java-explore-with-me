package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EndpointHit {
    private Integer id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
