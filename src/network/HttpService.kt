package network

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private fun getConnection(url: String): HttpURLConnection{
    val url: URL = URL(url)
    return url.openConnection() as HttpURLConnection
}


//TODO: Error handling
fun <T> httpGet(url: String, responseClass: Class<T>): T? {
    val con: HttpURLConnection = getConnection(url)
    con.requestMethod = "GET"
    if(con.responseCode == HttpURLConnection.HTTP_OK){
        val reader = BufferedReader(InputStreamReader(con.inputStream))
        val stringBuffer = StringBuffer()
        for(line in reader.lines()){
            stringBuffer.append(line)
        }
        val objectMapper = ObjectMapper()
        return objectMapper.readValue(stringBuffer.toString(), responseClass)
    }
    return null
}

fun <T> httpGetAsync(url: String, responseClass: Class<T>): Deferred<T?> {
    return GlobalScope.async {
        httpGet(url, responseClass)
    }
}

suspend fun <T,V> Deferred<T>.map(f: (T) -> V): V {
    await()
    return f(getCompleted())
}