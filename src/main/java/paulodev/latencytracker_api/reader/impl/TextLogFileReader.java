package paulodev.latencytracker_api.reader.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import paulodev.latencytracker_api.dto.LogEntryDTO;
import paulodev.latencytracker_api.reader.LogFileReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Slf4j
public class TextLogFileReader implements LogFileReader {

    private static final int COLUMN_ENDPOINT = 3;
    private static final int COLUMN_SERVICE = 5;
    private static final int COLUMN_RESPONSE_TIME_MS = 6;

    @Override
    public List<LogEntryDTO> readLogs(String filePath) {
        List<LogEntryDTO> logEntries = new ArrayList<>();

        String delimiter = filePath.endsWith(".csv") ? "," : "\\s+";

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {

            List<String> linesList = lines.toList();

            IntStream.range(1, linesList.size())
                    .forEach(i -> {
                        String line = linesList.get(i);

                        try {
                            String[] columns = line.split(delimiter);

                            if (columns.length > COLUMN_RESPONSE_TIME_MS) {
                                String endpoint = columns[COLUMN_ENDPOINT];
                                String serviceName = columns[COLUMN_SERVICE];
                                int responseTime = Integer.parseInt(columns[COLUMN_RESPONSE_TIME_MS]);

                                logEntries.add(new LogEntryDTO(endpoint, serviceName, responseTime));
                            }
                        } catch (Exception e) {
                            log.warn("Falha no processamento dos dados da linha da linha " + i + ": " + line);
                        }
                    });
            log.info("Arquivo de logs lido com sucesso");

        } catch (Exception e) {
            log.error("Erro ao processar o arquivo: " + e.getMessage());
        }
        return logEntries;
    }
}
