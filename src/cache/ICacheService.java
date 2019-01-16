package cache;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Class that allow to save different types of data offline in files
 */
public interface ICacheService {
    /**
     * Save given data under the key
     *
     * @param key key value
     * @param data data to save
     */
    void save(String key, Object data);

    /**
     * Read data which was previously saved with given key
     *
     * @param key key that data was saved with
     * @return readed data
     */
    Optional<Object> read(String key);

    /**
     * Check when data with given key was modified last time
     * @param key key of data
     * @return date of last file modification
     */
    LocalDateTime getLasyModificationOfFile(String key);
}
