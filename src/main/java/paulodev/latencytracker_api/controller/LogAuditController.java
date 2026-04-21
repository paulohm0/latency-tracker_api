package paulodev.latencytracker_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import paulodev.latencytracker_api.dto.AuditReportDTO;
import paulodev.latencytracker_api.dto.BottleneckDTO;
import paulodev.latencytracker_api.enums.BottleneckCriticalityLevel;
import paulodev.latencytracker_api.service.LogAuditService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class LogAuditController {

    private final LogAuditService logAuditService;

    @GetMapping("/audit")
    public AuditReportDTO getLogsBottlenecks(
            @RequestParam(required = false) BottleneckCriticalityLevel filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size)
    {
        return logAuditService.getCachedBottlenecks(filter, page, size);
    }
}
