package net.ccbluex.liquidbounce.features.module.modules.movement.noslow.modes.blocking

import net.ccbluex.liquidbounce.config.Choice
import net.ccbluex.liquidbounce.config.ChoiceConfigurable
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.events.PlayerNetworkMovementTickEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.modules.movement.noslow.modes.blocking.NoSlowBlock.blockingHand
import net.ccbluex.liquidbounce.features.module.modules.movement.noslow.modes.blocking.NoSlowBlock.modes
import net.ccbluex.liquidbounce.features.module.modules.movement.noslow.modes.blocking.NoSlowBlock.untracked
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

internal object NoSlowBlockingReuse : Choice("Reuse") {

    override val parent: ChoiceConfigurable<Choice>
        get() = modes

    @Suppress("unused")
    val onNetworkTick = handler<PlayerNetworkMovementTickEvent> { event ->
        if (blockingHand != null) {
            when (event.state) {
                EventState.PRE -> {
                    untracked {
                        PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN
                        )
                    }
                }

                EventState.POST -> {
                    untracked {
                        interaction.sendSequencedPacket(world) { sequence ->
                            PlayerInteractItemC2SPacket(blockingHand, sequence)
                        }
                    }
                }
            }
        }
    }


}