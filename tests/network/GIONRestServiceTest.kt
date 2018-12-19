package network

import network.model.StationGIONResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class GIONRestServiceTest {

    private val gionRestService: GIONRestService = GIONRestService()

    @Test
    fun getStations() {
        Mockito
            .`when`(httpGet(gionRestService.url, Array<StationGIONResponse>::class.java))
            .thenReturn(
                arrayOf(StationGIONResponse(0,"Cracow"), StationGIONResponse(1,"Warsaw")))
        val stations = gionRestService.getStations()
        assertEquals(2, stations?.size)
        assertEquals(0, stations?.get(0)?.id)
    }
}