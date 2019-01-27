package network

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

internal class GIONRestServiceTest {

    private val gionRestService: GIONRestService = GIONRestService()

    @Test
    fun getStations() {
        runBlocking {
            val stations = gionRestService.getStations().await()
            Assert.assertTrue(2 < stations.size)
        }
    }
}