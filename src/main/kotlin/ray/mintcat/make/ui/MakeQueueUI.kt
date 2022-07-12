package ray.mintcat.make.ui

import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import ray.mintcat.make.*
import ray.mintcat.make.data.MakeTask
import ray.mintcat.make.data.Time
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore
import taboolib.platform.util.modifyMeta

object MakeQueueUI {

    fun open(player: Player) {
        val data = MakeManager.getData(player)
        data.refresh()
        player.openMenu<Basic>("制作队列 [${data.getTaskSize()}/${data.task}]") {
            map(
                "#########",
                "#0123456#",
                "#########"
            )
            set('#', buildItem(XMaterial.BLACK_STAINED_GLASS_PANE)) {
                it.isCancelled = true
                player.closeInventory()
                submit(delay = 1) {
                    open(player)
                }
            }
            var isNull = false
            listOf('0', '1', '2', '3', '4', '5', '6').forEachIndexed { i, it ->
                val task = MakeManager.getData(player).getTasks().getOrNull(i)
                if (isNull) {
                    return@forEachIndexed
                }
                if (task == null) {
                    isNull = true
                    set(it, buildItem(XMaterial.MAP) {
                        name = "&f点击创建任务"
                        colored()
                    }) {
                        player.closeInventory()
                        submit(delay = 1) {
                            MakeListUI.typeUI(player)
                        }
                    }
                    return@forEachIndexed
                } else {
                    val item = (task.getStack()?.getShowItem(player) ?: buildItem(XMaterial.BARRIER)).clone()
                    set(it, buildItem(item) {
                        lore.add(" ")
                        if (task.state) {
                            lore.add("&a 制作完成")
                            lore.add("&7 点击领取")
                            shiny()
                        } else if (task.makeTime == task.getStack()!!.time) {
                            lore.add("&a 等待中")
                            lore.add("&7 左键: 置顶任务")
                            lore.add("&7 右键: 取消任务")
                            lore.add("&c !置顶任务会重置当前工作中的任务! ")
                        } else {
                            lore.add("&e 制作中... ")
                            lore.add(
                                "&a ${
                                    createLoad(
                                        task.getStack()!!.time.toDouble(),
                                        task.makeTime.toDouble(),
                                        "&c■", "&a■"
                                    )
                                }"
                            )
                            lore.add("&8( &a${Time(task.makeTime)}&8 / &c${Time(task.getStack()!!.time)} &8)")
                            lore.add("&c 点击取消任务 ")
                        }
                        colored()
                    }) { e ->
                        e.isCancelled = true
                        if (task.state) {
                            data.getFinished(task)
                            player.closeInventory()
                            submit(delay = 1) {
                                open(player)
                            }
                        } else if (task.makeTime == task.getStack()!!.time) {
                            if (e.clickEvent().isLeftClick) {
                                data.jump(task)
                            } else {
                                data.back(task)
                            }
                            player.closeInventory()
                            submit(delay = 1) {
                                open(player)
                            }
                        } else {
                            data.back(task)
                            player.closeInventory()
                            submit(delay = 1) {
                                open(player)
                            }
                        }
                    }
                }
            }
        }
    }

}