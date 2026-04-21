package paulodev.latencytracker_api.reader.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import paulodev.latencytracker_api.dto.LogEntryDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TextLogFileReaderTest {

    @Autowired
    private TextLogFileReader reader;

    @Test
    void testReadLogsCsv() throws IOException {
        // Create temp file
        Path tempFile = Files.createTempFile("test", ".csv");
        Files.writeString(tempFile, "header1,header2,header3,endpoint,service,header4,response\n");
        Files.writeString(tempFile, "data1,data2,data3,/api/test,data4,service1,1500\n", java.nio.file.StandardOpenOption.APPEND);

        List<LogEntryDTO> logs = reader.readLogs(tempFile.toString());

        assertEquals(1, logs.size());
        assertEquals("/api/test", logs.getFirst().endpoint());
        assertEquals("service1", logs.getFirst().serviceName());
        assertEquals(1500, logs.getFirst().responseTimeMs());

        Files.delete(tempFile);
    }

    @Test
    void testReadLogsTxt() throws IOException {
        // Similar for txt
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "header\n");
        Files.writeString(tempFile, "data1 data2 data3 /api/test data4 service1 2000\n", java.nio.file.StandardOpenOption.APPEND);

        List<LogEntryDTO> logs = reader.readLogs(tempFile.toString());

        assertEquals(1, logs.size());
        assertEquals("/api/test", logs.getFirst().endpoint());
        assertEquals("service1", logs.getFirst().serviceName());
        assertEquals(2000, logs.getFirst().responseTimeMs());

        Files.delete(tempFile);
    }
}
