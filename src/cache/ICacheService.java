package cache;

import java.io.Serializable;

public interface ICacheService {
    void save(String key, Object data);
    Object read(String key);
}
