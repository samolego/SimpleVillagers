package org.samo_lego.simplevillagers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;
import static org.samo_lego.simplevillagers.SimpleVillagers.CONFIG;

public class SimpleVillagersCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        final LiteralCommandNode<CommandSourceStack> config = literal("config")
                .requires(s -> Permissions.check(s, "simplevillagers.command.config", s.hasPermission(3)))
                .then(literal("reload")
                        .requires(s -> Permissions.check(s, "simplevillagers.command.config.reload", s.hasPermission(3)))
                        .executes(SimpleVillagersCommand::reloadConfig)
                )
                .build();

        final LiteralCommandNode<CommandSourceStack> simplevillagers = literal("simplevillagers")
                .requires(s -> Permissions.check(s, "simplevillagers.command", s.hasPermission(3)))
                .build();

        final LiteralCommandNode<CommandSourceStack> edit = literal("edit")
                .requires(s -> Permissions.check(s, "simplevillagers.command.config.edit", s.hasPermission(3)))
                .build();


        CONFIG.generateCommand(edit);
        config.addChild(edit);
        simplevillagers.addChild(config);
        dispatcher.getRoot().addChild(simplevillagers);
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        CONFIG.reload();
        context.getSource().sendSuccess(Component.translatable("command.simplevilagers.config.reload.success"), true);
        return 1;
    }
}
