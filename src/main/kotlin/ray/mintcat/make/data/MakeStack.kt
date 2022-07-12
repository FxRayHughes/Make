package ray.mintcat.make.data

import kotlinx.serialization.Serializable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ray.mintcat.make.MakeManager
import ray.mintcat.make.check
import ray.mintcat.make.eval
import ray.mintcat.make.info
import ray.mintcat.make.serializable.XItemStackSerializable
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import taboolib.platform.util.giveItem

@Serializable
class MakeStack(
    val name: String,
    var type: String = "其他",
    var time: Long = 0L,
    val replace: MutableList<MakeMaterial> = mutableListOf(),
    @Serializable(with = XItemStackSerializable::class)
    var show: ItemStack = buildItem(XMaterial.BARRIER) {
        this.name = "&f待配置"
        colored()
    },
    var give: Boolean = true,
    var action: MutableList<String> = mutableListOf(),
    var check: MutableList<String> = mutableListOf(),
    var builder: Boolean = true,
    var info: MutableList<String> = mutableListOf(),
    var source: MakeMaterial = MakeMaterial("AIR")
) {

    fun getShowItem(player: Player): ItemStack {
        return if (source.id != "AIR") {
            source.showItem(player)
        } else {
            show
        }
    }

    // true 可
    fun canMake(player: Player): Boolean {
        return !replace.map { it.hasItem(player) ?: false }.contains(false) && check.check(player).get() == true
    }

    fun make(player: Player) {
        val data = MakeManager.getData(player)
        data.join(MakeTask(name))
        replace.forEach { it.takeItem(player) }
    }

    fun eval(player: Player) {
        player.info("制作成功!")
        if (give) {
            player.giveItem(getShowItem(player))
        }
        action.eval(player)
    }

}