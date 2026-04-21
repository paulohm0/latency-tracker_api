package paulodev.latencytracker_api.reader.factory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import paulodev.latencytracker_api.reader.LogFileReader;
import paulodev.latencytracker_api.reader.impl.TextLogFileReader;
import paulodev.latencytracker_api.reader.impl.XlsxLogFileReader;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LogFileReaderFactoryTest {

    @Autowired
    private LogFileReaderFactory factory;

    @Autowired
    private XlsxLogFileReader xlsxReader;

    @Autowired
    private TextLogFileReader textReader;

    @Test
    void testGetReaderForXlsx() {
        LogFileReader reader = factory.getReader("test.xlsx");
        assertEquals(xlsxReader, reader);
    }

    @Test
    void testGetReaderForCsv() {
        LogFileReader reader = factory.getReader("test.csv");
        assertEquals(textReader, reader);
    }

    @Test
    void testGetReaderForTxt() {
        LogFileReader reader = factory.getReader("test.txt");
        assertEquals(textReader, reader);
    }

    @Test
    void testGetReaderUnsupported() {
        assertThrows(IllegalArgumentException.class, () -> factory.getReader("test.pdf"));
    }
}
