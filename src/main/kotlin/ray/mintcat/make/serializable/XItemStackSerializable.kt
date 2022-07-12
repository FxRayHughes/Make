package ray.mintcat.make.serializable

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.getItemStack
import taboolib.library.xseries.setItemStack
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.platform.util.buildItem

object XItemStackSerializable : KSerializer<ItemStack> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("org.bukkit.inventory.ItemStack")

    override fun deserialize(decoder: Decoder): ItemStack {
        return Configuration.loadFromString(decoder.decodeString(), Type.FAST_JSON)
            .getItemStack("value") ?: buildItem(XMaterial.STONE)
    }

    override fun serialize(encoder: Encoder, value: ItemStack) {
        encoder.encodeString(Configuration.empty(type = Type.FAST_JSON).apply {
            setItemStack("value", value)
        }.saveToString())
    }
}