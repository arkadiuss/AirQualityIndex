package cache

import java.time.LocalDateTime
import java.util.*

/**
 * Class that allow to save different types of data offline in files
 */
interface ICacheService {
    /**
     * Save given data under the key
     *
     * @param key key value
     * @param data data to save
     */
    fun save(key: String, data: Any)

    /**
     * Read data which was previously saved with given key
     *
     * @param key key that data was saved with
     * @return readed data
     */
    fun read(key: String): Optional<Any>

    /**
     * Check when data with given key was modified last time
     * @param key key of data
     * @return date of last file modification
     */
    fun getLasyModificationOfFile(key: String): LocalDateTime
}
