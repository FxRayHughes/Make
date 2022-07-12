package ray.mintcat.make

import kotlinx.serialization.json.Json
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import ray.mintcat.make.data.MakeQueue
import ray.mintcat.make.data.MakeStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.createLocal

object MakeManager {

    val playerData = ArrayList<MakeQueue>()
    val stacks = ArrayList<MakeStack>()

    val data by lazy {
        createLocal("data.yml")
    }
    val stack by lazy {
        createLocal("stack.yml")
    }

    val json = Json {
        coerceInputValues = true
    }

    @Schedule(period = 20)
    fun update() {
        playerData.forEach {
            it.refresh()
        }
    }

    fun getData(player: Player): MakeQueue {
        val data = playerData.firstOrNull { it.uuid == player.uniqueId }
        if (data == null) {
            playerData.add(MakeQueue(player.uniqueId))
        }
        return playerData.firstOrNull { it.uuid == player.uniqueId }!!
    }

    fun getStack(name: String): MakeStack {
        val data = stacks.firstOrNull { it.name == name }
        if (data == null) {
            stacks.add(MakeStack(name))
        }
        return stacks.firstOrNull { it.name == name }!!
    }

    @Awake(LifeCycle.ENABLE)
    fun playerDataLoad() {
        playerData.clear()
        data.getKeys(false).forEach {
            playerData.add(json.decodeFromString(MakeQueue.serializer(), data.getString(it)!!))
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun stacksLoad() {
        stacks.clear()
        stack.getKeys(false).forEach {
            stacks.add(json.decodeFromString(MakeStack.serializer(), stack.getString(it)!!))
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun save() {
        data.clear()
        playerData.forEach {
            data[it.uuid.toString()] = json.encodeToString(MakeQueue.serializer(), it)
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun stacksSave() {
        stack.clear()
        stacks.forEach {
            stack[it.name] = json.encodeToString(MakeStack.serializer(), it)
        }
    }

}