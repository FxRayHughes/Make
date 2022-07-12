package ray.mintcat.make.data

import kotlinx.serialization.Serializable

@Serializable
enum class MakeMaterialType(val display: String) {
    MINECRAFT("原版"),
    MYTHIC("MM"),
    SXITEM("SX")
}