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
import com.codehusky.huskycrates.crate.virtual.Key;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.config.MemoryConfig;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.views.MemoryView;

public class MemoryCrateCommand implements CommandExecutor {

	private Player player;
	private Crate crate;

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Optional<Crate> cr = args.getOne("crate");
		Optional<Key> k = args.getOne("key");
		Key key = null;
		if (src instanceof Player) {
			player = (Player) src;
			
			if (!cr.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "Crate can not be empty"));
				return CommandResult.success();
			} else {
				crate = cr.get();

			}

			if (crate != null) {
				key = crate.hasLocalKey()?crate.getLocalKey():k.isPresent()?k.get():null;
				if(!MemoryConfig.isMemoryCrate(crate.getId())) {
					src.sendMessage(Text.of(TextColors.RED,
							"Can not use this crate as a memory game"));
					return CommandResult.success();
				}
				if(crate.getSlots().size() < 18) {
					src.sendMessage(Text.of(TextColors.RED,
							"Can not use this crate as a memory game"));
					return CommandResult.success();
				}

				Map<String, Integer> virtualBalances = HuskyCrates.registry.getVirtualKeyBalances(player.getUniqueId());

				if (virtualBalances.get("LOCALKEY_" + crate.getId()) != null
						&& virtualBalances.get("LOCALKEY_" + crate.getId()) > 0) {

					HuskyCrates.registry.removeVirtualKeys(player.getUniqueId(),  key.getId() , 1);
					new MemoryView(crate, player);
					HuskycratesExtension.instance.logger.info(String
							.format("%s has opened a memory game for the %s crate", player.getName(), crate.getId()));

					return CommandResult.success();
				} else if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()
						&& HuskycratesExtension.registry.isKey(player.getItemInHand(HandTypes.MAIN_HAND).get())) {
					ItemStack st = player.getItemInHand(HandTypes.MAIN_HAND).get();
					st.setQuantity(st.getQuantity() - 1);
					new MemoryView(crate, player);
					HuskycratesExtension.instance.logger.info(String
							.format("%s has opened a memory game for the %s crate", player.getName(), crate.getId()));
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
