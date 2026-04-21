package paulodev.latencytracker_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import paulodev.latencytracker_api.dto.AuditReportDTO;
import paulodev.latencytracker_api.dto.LogEntryDTO;
import paulodev.latencytracker_api.enums.BottleneckCriticalityLevel;
import paulodev.latencytracker_api.reader.LogFileReader;
import paulodev.latencytracker_api.reader.factory.LogFileReaderFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogAuditServiceTest {

    @Mock
    private LogFileReaderFactory logFileReaderFactory;

    @Mock
    private LogFileReader logFileReader;

    @InjectMocks
    private LogAuditService logAuditService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessAudit() {
        // Arrange
        String filePath = "test.log";
        List<LogEntryDTO> mockLogs = List.of(
                new LogEntryDTO("/api/test", "service1", 500),
                new LogEntryDTO("/api/slow", "service2", 2500)
        );
        when(logFileReaderFactory.getReader(filePath)).thenReturn(logFileReader);
        when(logFileReader.readLogs(filePath)).thenReturn(mockLogs);

        // Act
        logAuditService.processAudit(filePath);

        // Assert
        verify(logFileReaderFactory).getReader(filePath);
        verify(logFileReader).readLogs(filePath);

        AuditReportDTO report = logAuditService.getCachedBottlenecks(null,0,20);
        assertEquals(2, report.totalLogsAnalyzed());
        assertEquals(1, report.bottleneckList().size());
        assertEquals(BottleneckCriticalityLevel.HIGH, report.bottleneckList().getFirst().criticalityLevel());
    }

    @Test
    void testGetCachedBottlenecksWithoutFilter() {
        // Arrange
        when(logFileReaderFactory.getReader(anyString())).thenReturn(logFileReader);
        when(logFileReader.readLogs(anyString())).thenReturn(List.of(
                new LogEntryDTO("/api/slow", "service1", 2500),
                new LogEntryDTO("/api/very-slow", "service2", 5000)
        ));
        logAuditService.processAudit("test");

        // Act
        AuditReportDTO report = logAuditService.getCachedBottlenecks(null,0,20);

        // Assert
        assertNotNull(report);
        assertEquals(2, report.totalLogsAnalyzed());
        assertNull(report.totalFiltered());
        assertEquals(5000, report.maxLatencyMs());
        assertEquals("/api/very-slow", report.slowestEndpoint());
        assertEquals(2, report.bottleneckList().size());
    }

    @Test
    void testGetCachedBottlenecksWithFilter() {

        when(logFileReaderFactory.getReader(anyString())).thenReturn(logFileReader);
        when(logFileReader.readLogs(anyString())).thenReturn(List.of(
                new LogEntryDTO("/api/slow", "service1", 2500),
                new LogEntryDTO("/api/very-slow", "service2", 5000)
        ));
        logAuditService.processAudit("test");

        // Act
        AuditReportDTO report = logAuditService.getCachedBottlenecks(BottleneckCriticalityLevel.CRITICAL, 0, 20);

        // Assert
        assertNotNull(report);
        assertEquals(2, report.totalLogsAnalyzed());
        assertEquals(1, report.totalFiltered());
        assertEquals("CRITICAL", report.filterApplied());
        assertEquals(1, report.bottleneckList().size());
        assertEquals(BottleneckCriticalityLevel.CRITICAL, report.bottleneckList().getFirst().criticalityLevel());
    }
}
