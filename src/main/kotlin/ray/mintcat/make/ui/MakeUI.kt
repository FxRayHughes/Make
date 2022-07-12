package ray.mintcat.make.ui

import org.bukkit.entity.Player
import ray.mintcat.make.MakeManager
import ray.mintcat.make.data.MakeStack
import ray.mintcat.make.data.MakeTask
import ray.mintcat.make.data.Time
import ray.mintcat.make.error
import ray.mintcat.make.set
import ray.mintcat.make.showBoolean
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore

object MakeUI {

    val slotMap =
        listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T')

    fun open(player: Player, makeStack: MakeStack) {
        player.openMenu<Basic>("${makeStack.name}制作") {
            map(
                "#########",
                "###ABCDE#",
                "#@#FGHIJ#",
                "###KLMNO#",
                "#!#PQRST#",
                "=####~###",
            )
            set('#', buildItem(XMaterial.BLACK_STAINED_GLASS_PANE)) {
                it.isCancelled = true
            }
            set('@', makeStack.getShowItem(player)) {
                it.isCancelled = true
            }
            set('!', buildItem(XMaterial.OAK_SIGN) {
                name = "&f${makeStack.name}"
                lore.addAll(makeStack.info)
                colored()
            }) {
                it.isCancelled = true
            }
            set('=', buildItem(XMaterial.YELLOW_STAINED_GLASS_PANE) {
                name = "&e返回到上一级"
                colored()
            }) {
                player.closeInventory()
                submit(delay = 1) {
                    MakeListUI.open(player, makeStack.type)
                }
            }
            set('~', buildItem(XMaterial.LIME_STAINED_GLASS_PANE) {
                name = "&a开始制作 ${showBoolean(makeStack.canMake(player))}"
                lore.add(" ")
                lore.add("&7耗时: &f${Time(makeStack.time)}")
                colored()
            }) {
                it.isCancelled = true
                if (!makeStack.canMake(player)) {
                    player.error("条件不满足！无法制作！")
                    return@set
                }
                makeStack.make(player)
                player.closeInventory()
                submit(delay = 1) {
                    MakeQueueUI.open(player)
                }

            }
            slotMap.forEachIndexed { i, it ->
                val data = makeStack.replace.getOrNull(i)
                if (data != null) {
                    set(it, data.showItem(player).clone().apply {
                        modifyLore {
                            add("")
                            add("&f需求数量: &c${data.getAmount(player)}&8/&a${data.amount} ${data.showBoolean(player)}")
                        }
                    }) {
                        it.isCancelled = true
                    }
                }
            }
        }
    }

}