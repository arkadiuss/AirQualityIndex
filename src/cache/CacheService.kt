package cache

import java.io.*

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
        println(file.absoluteFile)
        fw.writeObject(data)
        fw.close()
    }

    override fun read(key: String): Any {
        val file = getFile(key)
        val fw = ObjectInputStream(FileInputStream(file.absoluteFile))
        return fw.readObject()
    }

    private fun getFile(key: String): File{
        return File(directory.canonicalPath + "/" + key)
    }

}
