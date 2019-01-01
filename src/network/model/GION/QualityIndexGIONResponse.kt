package network.model.GION

import model.IMappable
import model.QualityIndex

class QualityIndexGIONResponse(
    val values: Map<String, String>) : IMappable<List<QualityIndex>> {

    override fun map(): List<QualityIndex> {
        values.forEach{
            println(it.key+" "+it.value)
        }
        return emptyList()
    }
}