package network

import org.junit.Assert
import org.junit.Test

internal class GIONRestServiceTest {

    private val gionRestService: GIONRestService = GIONRestService()

    @Test
    fun getStations() {
        val stations = gionRestService.getStations{
            Assert.assertTrue(2 < it?.size ?: -1)
        }
    }
}