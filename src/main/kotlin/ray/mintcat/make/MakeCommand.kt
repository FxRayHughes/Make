package ray.mintcat.make

import org.bukkit.entity.Player
import ray.mintcat.make.ui.MakeCreateUI
import ray.mintcat.make.ui.MakeQueueUI
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

@CommandHeader(name = "make", aliases = ["mk"])
object MakeCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val open = subCommand {
        execute<Player> { sender, context, argument ->
            MakeQueueUI.open(sender)
        }
    }

    @CommandBody
    val create = subCommand {
        dynamic(commit = "配方名") {
            execute<Player> { sender, context, argument ->
                val data = MakeManager.getStack(context.argument(0))
                MakeCreateUI.open(sender, data)
            }
        }
    }


}