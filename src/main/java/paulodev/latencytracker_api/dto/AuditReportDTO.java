package paulodev.latencytracker_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuditReportDTO(
        Integer totalLogsAnalyzed,
        Integer totalFiltered,
        String filterApplied,
        int maxLatencyMs,
        String slowestEndpoint,
        String slowestService,
        PaginationDTO paginationDTO,
        List<BottleneckDTO> bottleneckList
) { }
