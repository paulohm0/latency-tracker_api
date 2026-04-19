package paulodev.latencytracker_api.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import paulodev.latencytracker_api.service.LogAuditService;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogReaderScheduler {

    @Value("${logs.file.path}")
    private String filePath;

    private final LogAuditService logAuditService;

    // Padrão Cron: Segundo(0) Minuto(0) Hora(7) Dia(*) Mês(*) DiaDaSemana(*)
    @Scheduled(cron = "0 * * * * *")
    public void runAudit() {
        log.info("Leitura de Logs iniciada!");
        try {
            logAuditService.processAudit(filePath);
        } catch (Exception e) {
            log.info("Falha ao executar a leitura de logs!");
        }
    }

}
