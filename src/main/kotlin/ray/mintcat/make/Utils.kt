package ray.mintcat.make

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.library.xseries.XItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import java.lang.StringBuilder
import java.util.concurrent.CompletableFuture

fun ItemStack.serializable(): MutableMap<String, Any> {
    return XItemStack.serialize(this)
}

class LoadTask(string: String)

fun createLoad(max: Double, now: Double, maxS: String, nowS: String): String {
    val a = (now / max)
    val string = StringBuilder()
    (1..10).forEach {
        if (a > it / 10.0) {
            string.append(maxS.reversed())
        } else {
            string.append(nowS.reversed())
        }
    }
    return string.reversed().toString()
}

fun <T> Linked<T>.inits() {
    this.setNextPage(51) { page, hasNextPage ->
        if (hasNextPage) {
            buildItem(XMaterial.SPECTRAL_ARROW) {
                name = "§f下一页"
            }
        } else {
            buildItem(XMaterial.ARROW) {
                name = "§7下一页"
            }
        }
    }
    this.setPreviousPage(47) { page, hasPreviousPage ->
        if (hasPreviousPage) {
            buildItem(XMaterial.SPECTRAL_ARROW) {
                name = "§f上一页"
            }
        } else {
            buildItem(XMaterial.ARROW) {
                name = "§7上一页"
            }
        }
    }
}

fun List<String>.eval(player: Player) {
    try {
        KetherShell.eval(this, sender = adaptPlayer(player))
    } catch (e: Throwable) {
        e.printKetherErrorMessage()
    }
}

fun showBoolean(boolean: Boolean): String {
    return if (boolean) {
        "&a√"
    } else {
        "&c×"
    }
}

fun List<String>.check(player: Player): CompletableFuture<Boolean> {
    return if (this.isEmpty()) {
        CompletableFuture.completedFuture(true)
    } else {
        try {
            KetherShell.eval(this, sender = adaptPlayer(player)).thenApply {
                Coerce.toBoolean(it)
            }
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            CompletableFuture.completedFuture(false)
        }
    }
}

fun Basic.set(int: Int, item: ItemStack, onClick: (ClickEvent) -> Unit) {
    set(int, item)
    onClick(int, onClick)
}