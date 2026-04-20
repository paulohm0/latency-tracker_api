package paulodev.latencytracker_api.reader;

import paulodev.latencytracker_api.dto.LogEntryDTO;

import java.util.List;

public interface LogFileReader {

    List<LogEntryDTO> readLogs(String filePath);
}
