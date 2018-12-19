package service

import de.jodamob.kotlin.testrunner.KotlinTestRunner
import network.GIONRestService
import network.model.StationGIONResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(KotlinTestRunner::class)
internal class GIONAirQualityServiceTest{

    private val gionRestService: GIONRestService = Mockito.mock(GIONRestService::class.java)

    @Test
    fun getStations() {
        Mockito
            .`when`(gionRestService.getStations())
            .thenReturn(
                listOf(StationGIONResponse(0,"Cracow"), StationGIONResponse(1,"Warsaw")))
        val stations = gionRestService.getStations()
        assertEquals(2, stations?.size)
        assertEquals(0L, stations?.get(0)?.id)
    }

}