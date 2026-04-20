package paulodev.latencytracker_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import paulodev.latencytracker_api.dto.AuditReportDTO;
import paulodev.latencytracker_api.dto.BottleneckDTO;
import paulodev.latencytracker_api.dto.LogEntryDTO;
import paulodev.latencytracker_api.enums.BottleneckCriticalityLevel;
import paulodev.latencytracker_api.reader.LogFileReader;
import paulodev.latencytracker_api.reader.factory.LogFileReaderFactory;
import paulodev.latencytracker_api.reader.impl.XlsxLogFileReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LogAuditService {

    private final LogFileReaderFactory logFileReaderFactory;

    private volatile List<BottleneckDTO> cachedBottlenecks = new ArrayList<>();
    private volatile int totalLogsProcessed = 0;

    public void processAudit(String filePath) {
        LogFileReader reader = logFileReaderFactory.getReader(filePath);
        List<LogEntryDTO> rawLogs = reader.readLogs(filePath);
        List<BottleneckDTO> processedBottlenecks = new ArrayList<>();

        for (LogEntryDTO log : rawLogs) {
            BottleneckCriticalityLevel criticalityLevel = BottleneckCriticalityLevel.fromResponseTime(log.responseTimeMs());
            if (criticalityLevel == BottleneckCriticalityLevel.NORMAL) continue;
            processedBottlenecks.add(new BottleneckDTO(
                    log.endpoint(),
                    log.serviceName(),
                    log.responseTimeMs(),
                    criticalityLevel));
        }
        this.totalLogsProcessed = rawLogs.size();
        this.cachedBottlenecks = processedBottlenecks;

    }

    public AuditReportDTO getCachedBottlenecks(BottleneckCriticalityLevel filter) {

        Stream<BottleneckDTO> stream = cachedBottlenecks.stream();

        if (filter != null) {
            stream = stream.filter(b -> b.criticalityLevel().equals(filter));
        }
        List<BottleneckDTO> processedList = stream
                .sorted(Comparator.comparingInt(BottleneckDTO::responseTimeMs).reversed())
                .toList();

        BottleneckDTO worstBottleneck = processedList.isEmpty() ? null : processedList.get(0);

        int maxLatencyMs = (worstBottleneck != null) ? worstBottleneck.responseTimeMs() : 0;
        String slowestEndpoint = (worstBottleneck != null) ? worstBottleneck.endpoint() : null;
        String slowestService = (worstBottleneck != null) ? worstBottleneck.serviceName() : null;

        return new AuditReportDTO(
                totalLogsProcessed,
                (filter != null) ? processedList.size() : null,
                (filter != null) ? filter.name() : null,
                maxLatencyMs,
                slowestEndpoint,
                slowestService,
                processedList
        );
    }
}