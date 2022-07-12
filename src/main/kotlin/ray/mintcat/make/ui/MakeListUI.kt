package ray.mintcat.make.ui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import ray.mintcat.make.MakeManager
import ray.mintcat.make.color
import ray.mintcat.make.data.MakeStack
import ray.mintcat.make.data.Time
import ray.mintcat.make.error
import ray.mintcat.make.inits
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.colored
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.inventoryCenterSlots
import taboolib.platform.util.modifyLore
import taboolib.platform.util.modifyMeta
import java.util.*

object MakeListUI {


    fun typeUI(player: Player) {
        player.openMenu<Linked<String>>("制作") {
            rows(6)
            inits()
            slots(inventoryCenterSlots)
            if (player.isOp) {
                set(49, buildItem(XMaterial.ANVIL) {
                    name = "&e创建物品"
                    colored()
                }) {
                    isCancelled = true
                    player.closeInventory()
                    submit(delay = 1) {
                        player.inputSign(arrayOf("", "", "第一行输入配方名", "点击确认创建")) { len ->
                            val name = len[0] + len[1]
                            if (name.isEmpty()) {
                                submit(delay = 1) {
                                    player.error("错误的名字 已取消创建")
                                    typeUI(player)
                                }
                                return@inputSign
                            }
                            val data = MakeManager.getStack(name)
                            MakeCreateUI.open(player, data)
                        }
                    }
                }
            }
            elements {
                MakeManager.stacks.map { it.type }.toSortedSet().toList()
            }
            onGenerate { player, element, index, slot ->
                return@onGenerate buildItem(XMaterial.PAPER) {
                    name = "&f${element}"
                    colored()
                }
            }
            onClick { event, element ->
                player.closeInventory()
                submit(delay = 1) {
                    open(player, element)
                }
            }
        }
    }

    fun open(player: Player, type: String) {
        player.openMenu<Linked<MakeStack>>("${type}类 制作") {
            rows(6)
            inits()
            slots(inventoryCenterSlots)
            elements {
                if (player.isOp) {
                    MakeManager.stacks.filter { it.type == type }
                } else {
                    MakeManager.stacks.filter { it.type == type && !it.builder }
                }
            }
            if (player.isOp) {
                set(49, buildItem(XMaterial.ANVIL) {
                    name = "&e在该类创建物品"
                    colored()
                }) {
                    isCancelled = true
                    player.closeInventory()
                    submit(delay = 1) {
                        player.inputSign(arrayOf("", "", "第一行输入配方名", "点击确认创建")) { len ->
                            val name = len[0] + len[1]
                            if (name.isEmpty()) {
                                submit(delay = 1) {
                                    player.error("错误的名字 已取消创建")
                                    open(player, type)
                                }
                                return@inputSign
                            }
                            val data = MakeManager.getStack(name).apply {
                                this.type = type
                            }
                            MakeCreateUI.open(player, data)
                        }
                    }
                }
            }
            onGenerate { player, element, index, slot ->
                return@onGenerate element.getShowItem(player).clone().apply {
                    modifyLore {
                        add(" ")
                        add("&7耗时: &f${Time(element.time)}".color())
                        add("&7点击制作".color())
                    }
                    if (element.builder) {
                        modifyMeta<ItemMeta> {
                            displayName = "$displayName &e(编辑中)".color()
                        }
                    }
                    if (player.isOp) {
                        modifyLore {
                            add("&e右键进入编辑页面".color())
                        }
                    }
                }
            }
            onClick { event, element ->
                player.closeInventory()
                if (player.isOp) {
                    if (event.clickEvent().isRightClick) {
                        submit(delay = 1) {
                            MakeCreateUI.open(player, element)
                        }
                    } else {
                        submit(delay = 1) {
                            MakeUI.open(player, element)
                        }
                    }
                } else {
                    submit(delay = 1) {
                        MakeUI.open(player, element)
                    }
                }
            }
        }
    }

}