package paulodev.latencytracker_api.dto;

public record LogEntryDTO(
        String endpoint,
        String serviceName,
        int responseTimeMs
) { }
