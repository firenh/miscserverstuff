package fireopal.miscserverstuff.command;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BroadcastCommand {
    private enum BroadcastType {
        MESSAGE,
        TITLE,
        ACTIONBAR
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("broadcast")
            .then(
                CommandManager.literal("message")
                .then(
                    CommandManager.argument("players", EntityArgumentType.players())
                    .then(
                        CommandManager.argument("message", MessageArgumentType.message())
                        .executes(context -> execute(context, BroadcastType.MESSAGE))
                    )
                )
            )
            .then(
                CommandManager.literal("title")
                .then(
                    CommandManager.argument("players", EntityArgumentType.players())
                    .then(
                        CommandManager.argument("message", MessageArgumentType.message())
                        .executes(context -> execute(context, BroadcastType.TITLE))
                    )
                )
            )
            .then(
                CommandManager.literal("actionbar")
                .then(
                    CommandManager.argument("players", EntityArgumentType.players())
                    .then(
                        CommandManager.argument("message", MessageArgumentType.message())
                        .executes(context -> execute(context, BroadcastType.ACTIONBAR))
                    )
                )
            )
        );
    }

    public static int execute(CommandContext<ServerCommandSource> context, BroadcastType type) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
        MutableText senderName = MutableText.of(Text.of(context.getSource().getName()).getContent());
        MutableText message = MutableText.of(MessageArgumentType.getMessage(context, "message").getContent());
 
        for (ServerPlayerEntity player : players) {
            if (type == BroadcastType.MESSAGE) {
                player.sendMessage(
                    MutableText.of(Text.of("[").getContent())
                    .append(senderName.styled(style -> style.withColor(Formatting.GOLD)))
                    .append("] ")
                    .append(message.styled(style -> style.withColor(Formatting.RED)))
                );
            } else if (type == BroadcastType.TITLE) {
                player.networkHandler.sendPacket(new TitleS2CPacket(senderName.styled(style -> style.withColor(Formatting.GOLD))));
                player.networkHandler.sendPacket(new SubtitleS2CPacket(message.styled(style -> style.withColor(Formatting.RED))));
            } else {
                player.sendMessage(
                    MutableText.of(Text.of("[").getContent())
                    .append(senderName.styled(style -> style.withColor(Formatting.GOLD)))
                    .append("] ")
                    .append(message.styled(style -> style.withColor(Formatting.RED))),
                    true
                );
            }

            player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(SoundEvents.BLOCK_NOTE_BLOCK_PLING.getId(), SoundCategory.MASTER, player.getPos(), 1f, 1, 1));
        }

        if (players.size() == 0) {
            context.getSource().sendError(Text.of("Could not send message, no players targeted!"));
        }

        return players.size();
    }
}
