package paulodev.latencytracker_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import paulodev.latencytracker_api.dto.ApiResponseDTO;
import paulodev.latencytracker_api.dto.LogEntryDTO;
import paulodev.latencytracker_api.enums.BottleneckCriticalityLevel;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogAuditService {

    private final LogReaderService logReaderService;

    private List<ApiResponseDTO> cachedBottlenecks = new ArrayList<>();

    public void processAudit(String filePath) {
        List<LogEntryDTO> rawLogs = logReaderService.readLogsFromFile(filePath);
        List<ApiResponseDTO> processedBottlenecks = new ArrayList<>();

        for (LogEntryDTO log : rawLogs) {
            BottleneckCriticalityLevel criticalityLevel = BottleneckCriticalityLevel.fromResponseTime(log.responseTimeMs());
            if (criticalityLevel == BottleneckCriticalityLevel.NORMAL) continue;
            processedBottlenecks.add(new ApiResponseDTO(
                    log.endpoint(),
                    log.serviceName(),
                    log.responseTimeMs(),
                    criticalityLevel));
        }
        this.cachedBottlenecks = processedBottlenecks;

    }

    public List<ApiResponseDTO> getCachedBottlenecks() {
        return this.cachedBottlenecks;
    }
}