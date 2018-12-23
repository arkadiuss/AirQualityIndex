package service

import de.jodamob.kotlin.testrunner.KotlinTestRunner
import network.GIONRestService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(KotlinTestRunner::class)
internal class GIONAirQualityServiceTest{

    private val gionRestService: GIONRestService = Mockito.mock(GIONRestService::class.java)

    @Test
    fun getStations() {
        Mockito
            .`when`(gionRestService.getStations{})
            .then {
//                val stations = gionRestService.getStations()
//                Assert.assertEquals(2, stations?.size)
//                Assert.assertEquals(0L, stations?.get(0)?.id)

            }
    }

}