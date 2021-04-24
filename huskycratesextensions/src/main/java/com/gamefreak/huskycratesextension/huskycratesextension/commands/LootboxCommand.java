package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.virtual.Crate;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.views.LootboxView;

public class LootboxCommand implements CommandExecutor {

	private Player player;
	private Crate crate;


	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Optional<Crate> cr = args.getOne("crate");

		if (src instanceof Player) {
			player = (Player) src;
			if (!cr.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "Crate can not be empty"));
				return CommandResult.success();
			} else {
				crate = cr.get();

			}

			if (crate != null) {



				Map<String, Integer> virtualBalances = HuskyCrates.registry.getVirtualKeyBalances(player.getUniqueId());

				if (virtualBalances.get("LOCALKEY_" + crate.getId()) != null
						&& virtualBalances.get("LOCALKEY_" + crate.getId()) > 0) {

					HuskyCrates.registry.addVirtualKeys(player.getUniqueId(), "LOCALKEY_" + crate.getId(), -1);
					new LootboxView(crate, player);
					HuskycratesExtension.instance.logger.info(String
							.format("%s has opened a lootbox for the %s crate", player.getName(), crate.getId()));

					return CommandResult.success();
				} else if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()
						&& HuskycratesExtension.registry.isKey(player.getItemInHand(HandTypes.MAIN_HAND).get())) {
					ItemStack st = player.getItemInHand(HandTypes.MAIN_HAND).get();
					st.setQuantity(st.getQuantity() - 1);
					new LootboxView(crate, player);
					HuskycratesExtension.instance.logger.info(String
							.format("%s has opened a loot box for the %s crate", player.getName(), crate.getId()));
					return CommandResult.success();
				} else {
					player.sendMessage(Text.of(TextColors.RED,
							"You don't have enough keys"));
					return CommandResult.success();
				}

			} else {
				src.sendMessage(Text.of("Command must be executed as a player!"));
				return CommandResult.success();

			}
		}
		return CommandResult.success();
	}

}
