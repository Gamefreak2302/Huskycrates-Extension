package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.virtual.Key;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;

public class Balance implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		CommandResult success = CommandResult.success();

		User player = (User) args.getOne("player").orElse(null);
		Text header;
		if (player == null) {
			header = Text.of(TextColors.YELLOW, " === Balance ===");
			if (src instanceof Player) {
				player = (Player) src;
			} else {
				src.sendMessage(Text.of(TextColors.RED, "Only players can execute this command"));
				return success;
			}
		} else if (!src.hasPermission("huskycratesextension.admin.checkbalanceothers")) {
			header = Text.of(TextColors.YELLOW, " === Balance ===");
			if (src instanceof Player) {
				player = (Player) src;
			} else {
				src.sendMessage(Text.of(TextColors.RED, "permission required"));
				return success;
			}
		} else {
			header = Text.of(TextColors.YELLOW, "=== Balance of " + player.getName() + " ===");
		}

		Map<String, Integer> balances = HuskyCrates.registry.getVirtualKeyBalances(player.getUniqueId());
		src.sendMessage(header);
		for (Entry<String, Integer> entry : balances.entrySet()) {
			Key key = HuskyCrates.registry.getKey(entry.getKey());
			if (entry.getValue() > 0) {
				if (src instanceof Player && (Player) src == player) {
					Player pl = (Player) src;
					Text text = Text
							.of(TextSerializers.FORMATTING_CODE.deserialize(
									"&6- " + key.getName().replace("LOCALKEY_", "") + " &6: " + entry.getValue()))
							.toBuilder().onClick(TextActions.executeCallback(r -> {
								if (balances.get(entry.getKey()) > 0) {

									if (pl.getInventory().first().canFit(key.getKeyItemStack())) {
										HuskyCrates.registry.removeVirtualKeys(pl.getUniqueId(), entry.getKey(), 1);
										ItemStack stack = key.getKeyItemStack(1);
										pl.getInventory().first().offer(stack);
										src.sendMessage(Text.of(TextColors.GREEN,
												"You converted a "
														+ HuskycratesExtension.registry.getStringWithoutColor(
																key.getName().replace("LOCALKEY_", ""))));
										Sponge.getCommandManager().process(src, "hce bal");
									} else {
										src.sendMessage(Text.of(TextColors.RED,
												"Not enough space in your inventory to convert a key."));
									}
								} else {
									src.sendMessage(Text.of(TextColors.RED, "You don't have enough keys"));
								}

							}))
							.onHover(
									TextActions.showText(Text.of(TextColors.GREEN,
											"Click to convert 1 " + HuskycratesExtension.registry
													.getStringWithoutColor(key.getName().replace("LOCALKEY_", "")))))
							.build();
					src.sendMessage(text);
				} else {
					src.sendMessage(Text
							.of(TextSerializers.FORMATTING_CODE.deserialize(
									"&6- " + key.getName().replace("LOCALKEY_", "") + " &6: " + entry.getValue())));

				}
			}
		}

		return success;
	}

}
