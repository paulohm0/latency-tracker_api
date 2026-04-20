package paulodev.latencytracker_api.reader.impl;

import com.github.pjfanning.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import paulodev.latencytracker_api.dto.LogEntryDTO;
import paulodev.latencytracker_api.reader.LogFileReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class XlsxLogFileReader implements LogFileReader {

    private static final int COLUMN_ENDPOINT = 2;
    private static final int COLUMN_SERVICE = 4;
    private static final int COLUMN_RESPONSE_TIME_MS = 5;

    @Override
    public List<LogEntryDTO> readLogs(String filePath) {
        List<LogEntryDTO> logEntries = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(new File(filePath));
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100)    // Mantém apenas 100 linhas na RAM por vez
                     .bufferSize(4096)     // Tamanho do buffer de leitura no disco
                     .open(file)) {        // Abre o arquivo

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                log.error("Erro ao ler arquivo: Aba de logs não localizada");
                return logEntries;
            }

            for (Row row : sheet) {
                try {
                    if (row.getRowNum() == 0) continue;
                    String endpoint = row.getCell(COLUMN_ENDPOINT).getStringCellValue();
                    String serviceName = row.getCell(COLUMN_SERVICE).getStringCellValue();
                    int responseTimeMs = (int) row.getCell(COLUMN_RESPONSE_TIME_MS).getNumericCellValue();

                    LogEntryDTO logEntry = new LogEntryDTO(endpoint, serviceName, responseTimeMs);
                    logEntries.add(logEntry);
                } catch (Exception e) {
                    log.warn("Falha no processamento dos dados da linha da linha "+ row.getRowNum());
                }
            }
            log.info("Arquivo de logs lido com éxito");
        } catch (Exception e) {
            log.error("Erro ao processar o arquivo Excel: " + e.getMessage());
        }
        return logEntries;
    }
}