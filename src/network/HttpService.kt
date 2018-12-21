package network

import com.fasterxml.jackson.databind.ObjectMapper
import network.model.StationGIONResponse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

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

fun <T> httpGetAsync(executorService: ExecutorService, url: String, responseClass: Class<T>): Future<T?> {
    return executorService.submit{ httpGet(url, responseClass) } as Future<T?>
}