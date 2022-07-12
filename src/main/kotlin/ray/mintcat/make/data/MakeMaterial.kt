package ray.mintcat.make.data

import github.saukiya.sxitem.data.item.ItemManager
import io.lumine.xikage.mythicmobs.MythicMobs
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ray.mintcat.make.data.MakeMaterialType.*
import taboolib.platform.util.*

@Serializable
class MakeMaterial(
    val id: String,
    val type: MakeMaterialType = MINECRAFT,
    val amount: Int = 0,
    var excess: MutableList<String> = mutableListOf(),
) {

    fun back(player: Player) {
        val item = when (type) {
            MINECRAFT -> {
                buildItem(Material.valueOf(id))
            }

            MYTHIC -> {
                MythicMobs.inst().itemManager.getItemStack(id)
            }

            SXITEM -> {
                if (excess.isEmpty()) {
                    ItemManager().getItem(id, player, *excess.toTypedArray()) ?: ItemStack(Material.BARRIER)
                } else {
                    ItemManager().getItem(id, player) ?: ItemStack(Material.BARRIER)
                }
            }
        }
        player.giveItem(item, amount)
    }

    fun showItem(player: Player): ItemStack {
        val item = when (type) {
            MINECRAFT -> {
                buildItem(Material.valueOf(id))
            }

            MYTHIC -> {
                MythicMobs.inst().itemManager.getItemStack(id)
            }

            SXITEM -> {
                if (excess.isEmpty()) {
                    ItemManager().getItem(id, player, *excess.toTypedArray()) ?: ItemStack(Material.BARRIER)
                } else {
                    ItemManager().getItem(id, player) ?: ItemStack(Material.BARRIER)
                }
            }
        }
        return item.clone().apply {
            amount = if (this@MakeMaterial.amount >= item.maxStackSize) {
                item.maxStackSize
            } else {
                this@MakeMaterial.amount
            }
        }
    }

    fun showBoolean(player: Player): String {
        return if (hasItem(player)) {
            "&a√"
        } else {
            "&cX"
        }
    }

    fun getAmount(player: Player): Int {
        var i = 0
        player.inventory.takeItem(999) {
            if (when (type) {
                    MINECRAFT -> {
                        it.type.name.equals(id, true)
                    }

                    MYTHIC -> {
                        MythicMobs.inst().itemManager.getItemStack(id) == it
                    }

                    SXITEM -> {
                        val manager = ItemManager().getGenerator(it)
                        if (manager != null) {
                            manager.key == id
                        } else {
                            false
                        }
                    }
                }
            ) {
                i += it.amount
            }
            false
        }
        return i
    }

    fun takeItem(player: Player) {
        player.inventory.takeItem(amount) {
            when (type) {
                MINECRAFT -> {
                    it.type.name.equals(id, true)
                }

                MYTHIC -> {
                    MythicMobs.inst().itemManager.getItemStack(id) == it
                }

                SXITEM -> {
                    val manager = ItemManager().getGenerator(it)
                    if (manager != null) {
                        manager.key == id
                    } else {
                        false
                    }
                }
            }
        }
    }

    fun hasItem(player: Player): Boolean {
        return player.inventory.hasItem(amount) {
            when (type) {
                MINECRAFT -> {
                    it.type.name.equals(id, true)
                }

                MYTHIC -> {
                    MythicMobs.inst().itemManager.getItemStack(id) == it
                }

                SXITEM -> {
                    val manager = ItemManager().getGenerator(it)
                    if (manager != null) {
                        manager.key == id
                    } else {
                        false
                    }
                }
            }

        }
    }

}