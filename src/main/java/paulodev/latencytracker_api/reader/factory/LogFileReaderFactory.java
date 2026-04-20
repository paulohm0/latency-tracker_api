package paulodev.latencytracker_api.reader.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import paulodev.latencytracker_api.reader.LogFileReader;
import paulodev.latencytracker_api.reader.impl.TextLogFileReader;
import paulodev.latencytracker_api.reader.impl.XlsxLogFileReader;

@Component
@RequiredArgsConstructor
public class LogFileReaderFactory {

    private final XlsxLogFileReader xlsxReader;
    private final TextLogFileReader textReader;

    public LogFileReader getReader(String filePath) {
        String path = filePath.toLowerCase();

        if(path.endsWith(".xlsx")) return xlsxReader;
        if(path.endsWith(".csv") || path.endsWith(".txt")) return textReader;

        throw new IllegalArgumentException("Formato de arquivo não suportado pela aplicação: " + filePath);
    }
}
