package paulodev.latencytracker_api.dto;

import paulodev.latencytracker_api.enums.BottleneckCriticalityLevel;

public record ApiResponseDTO(
        String endpoint,
        String serviceName,
        int responseTimeMs,
        BottleneckCriticalityLevel criticalityLevel
) { }
