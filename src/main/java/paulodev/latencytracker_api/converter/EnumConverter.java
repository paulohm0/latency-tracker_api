package paulodev.latencytracker_api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import paulodev.latencytracker_api.enums.BottleneckCriticalityLevel;

@Component
public class EnumConverter implements Converter<String, BottleneckCriticalityLevel> {

    @Override
    public BottleneckCriticalityLevel convert(String source) {
        try {
            return BottleneckCriticalityLevel.valueOf(source.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Filtro inválido! Os valores aceitos são: NORMAL, WARNING, HIGH, CRITICAL."
            );
        }
    }
}
