package paulodev.latencytracker_api.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import paulodev.latencytracker_api.service.LogAuditService;

import java.io.File;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogReaderScheduler {

    @Value("${backlog.folder.path}")
    private String folderPath;

    private final LogAuditService logAuditService;

    @Scheduled(cron = "${scheduler.cron}")
    public void runAudit() {

        log.info("Procurando arquivo de logs...");
        File folder = new File(folderPath);
        File[] listFiles = folder.listFiles();

        if (listFiles == null || listFiles.length == 0) {
            log.warn("Arquivo de logs não encontrado.");
            return;
        }

        File arquivoParaProcessar = listFiles[0];
        String filePath = arquivoParaProcessar.getAbsolutePath();

        log.info("Arquivo de logs encontrado: " + arquivoParaProcessar.getName());

        try {
            logAuditService.processAudit(filePath);
        } catch (Exception e) {
            log.error("Falha ao executar a leitura do arquivo: " + arquivoParaProcessar.getName(), e);
        }
    }

}
