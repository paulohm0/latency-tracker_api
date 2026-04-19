package paulodev.latencytracker_api.service;

import com.github.pjfanning.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import paulodev.latencytracker_api.dto.LogEntryDTO;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LogReaderService {

    public List<LogEntryDTO> readLogsFromFile(String filePath) {
        List<LogEntryDTO> logEntries = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(new File(filePath));
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100)    // Mantém apenas 100 linhas na RAM por vez
                     .bufferSize(4096)     // Tamanho do buffer de leitura no disco
                     .open(file)) {        // Abre o arquivo

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                log.info("Erro ao ler arquivo: Aba de logs não localizada");
                return logEntries;
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String endpoint = row.getCell(2).getStringCellValue();
                String serviceName = row.getCell(4).getStringCellValue();
                int responseTimeMs = (int) row.getCell(5).getNumericCellValue();

                LogEntryDTO logEntry = new LogEntryDTO(endpoint, serviceName, responseTimeMs);
                logEntries.add(logEntry);
            }
            log.info("Arquivo de logs lido com éxito");
        } catch (Exception e) {
            log.info("Erro ao processar o arquivo Excel: " + e.getMessage());
        }
        return logEntries;
    }
}