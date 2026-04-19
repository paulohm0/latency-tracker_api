package paulodev.latencytracker_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paulodev.latencytracker_api.dto.ApiResponseDTO;
import paulodev.latencytracker_api.service.LogAuditService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class LogAuditController {

    private final LogAuditService logAuditService;

    @GetMapping("/logs-audit")
    public List<ApiResponseDTO> getLogsBottlenecks() {
        return logAuditService.getCachedBottlenecks();
    }
}
