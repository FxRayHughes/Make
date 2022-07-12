package ray.mintcat.make

import org.bukkit.Material
import ray.mintcat.make.serializable.XItemStackSerializable
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.platform.util.buildItem

@RuntimeDependencies(
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2",
        relocate = ["!kotlin.", "!kotlin@kotlin_version_escape@."]
    ),
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2",
        relocate = ["!kotlin.", "!kotlin@kotlin_version_escape@."]
    ),
)
object Make : Plugin() {

    override fun onEnable() {
        val item = buildItem(Material.APPLE){
            name = "这是一个测试的苹果"
            lore.add("第一行lore")
        }
        println(MakeManager.json.encodeToString(XItemStackSerializable, item))
    }
}