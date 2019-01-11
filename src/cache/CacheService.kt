package cache

import java.io.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class CacheService(storageName: String) : ICacheService{

    private val PATH = "cache/"
    private val directory: File = File("$PATH$storageName")

    init {
        if(!directory.exists()){
            directory.mkdirs()
        }
    }

    override fun save(key: String, data: Any) {
        val file = getFile(key)
        val fw = ObjectOutputStream(FileOutputStream(file.absoluteFile))
        fw.writeObject(data)
        fw.close()
    }

    override fun read(key: String): Optional<Any> {
        val file = getFile(key)
        if(!file.exists()) return Optional.empty()
        val fw = ObjectInputStream(FileInputStream(file.absoluteFile))
        return Optional.of(fw.readObject())
    }

    override fun getLasyModificationOfFile(key: String): LocalDateTime {
        val file = getFile(key)
        if(file.exists()){
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault())
        }
        return LocalDateTime.MIN
    }

    private fun getFile(key: String): File{
        return File(directory.canonicalPath + "/" + key)
    }

}
