package paulodev.latencytracker_api.dto;

import paulodev.latencytracker_api.enums.BottleneckCriticalityLevel;

public record BottleneckDTO(
        String endpoint,
        String serviceName,
        int responseTimeMs,
        BottleneckCriticalityLevel criticalityLevel
) { }
