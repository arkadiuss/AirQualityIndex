package cache;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ICacheService {
    void save(String key, Object data);
    Optional<Object> read(String key);
    LocalDateTime getLasyModificationOfFile(String key);
}
