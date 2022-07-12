package ray.mintcat.make.data

import kotlinx.serialization.Serializable
import ray.mintcat.make.MakeManager

@Serializable
class MakeTask(
    val stack: String,
    var makeTime: Long = 0L,
    var state: Boolean = false
) {

    fun getStack(): MakeStack? {
        return MakeManager.stacks.firstOrNull { it.name == stack }
    }

}