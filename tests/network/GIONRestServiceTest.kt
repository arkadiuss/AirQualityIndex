package network

import network.model.StationGIONResponse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

internal class GIONRestServiceTest {

    private val gionRestService: GIONRestService = GIONRestService()

    @Test
    fun getStations() {
        val stations = gionRestService.getStations()
        assertTrue(2 < stations?.size?:-1)
    }
}