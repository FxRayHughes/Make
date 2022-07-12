package ray.mintcat.make.data

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ray.mintcat.make.info
import ray.mintcat.make.serializable.UUIDSerializable
import taboolib.platform.util.giveItem
import java.util.LinkedList
import java.util.UUID

@Serializable
class MakeQueue(
    @Serializable(with = UUIDSerializable::class)
    val uuid: UUID,
    var task: Int = 7,
    val makes: MutableList<MakeTask> = mutableListOf(),
    val finished: MutableList<MakeTask> = mutableListOf(),
    var last: Long = System.currentTimeMillis()
) {

    val player: Player?
        get() = Bukkit.getPlayer(uuid)

    fun getTasks(): MutableList<MakeTask> {
        return mutableListOf<MakeTask>().apply {
            addAll(finished)
            addAll(makes)
        }
    }

    fun join(task: MakeTask) {
        if (makes.isEmpty()) {
            last = System.currentTimeMillis()
        }
        val data = task.getStack() ?: return
        makes.add(task.apply {
            makeTime = data.time
        })
        refresh()
    }

    fun back(task: MakeTask) {
        player ?: return
        task.getStack()!!.replace.forEach { it.back(player!!) }
        remove(task)
    }

    fun remove(task: MakeTask) {
        makes.remove(task)
        refresh()
    }

    fun getFinished(task: MakeTask) {
        player ?: return
        task.getStack()?.eval(player!!)
        finished.remove(task)
        refresh()
    }

    fun jump(task: MakeTask) {
        makes.remove(task)
        val first = makes.getOrNull(0)
        if (first == null) {
            makes.add(0, task)
            return
        }
        first.makeTime = first.getStack()!!.time
        makes.add(0, task)
    }

    fun getTaskSize(): Int {
        return (makes.size + finished.size)
    }

    fun isFull(): Boolean {
        return getTaskSize() < task
    }

    fun refresh() {
        if (makes.isEmpty()) {
            return
        }
        var value = System.currentTimeMillis() - last
        last = System.currentTimeMillis()
        while (value > 0) {
            if (makes.isEmpty()) {
                break
            }
            val make = makes[0]
            if (value - make.makeTime < 0) {
                make.makeTime -= value
                break
            }
            value -= make.makeTime
            finished.add(make.apply {
                state = true
                makeTime = -1
            })
            if (player != null && player?.isOnline == true) {
                player!!.info("队列中的 &f${make.stack} &7已制作完成!")
            }
            makes.remove(make)
        }
    }

}