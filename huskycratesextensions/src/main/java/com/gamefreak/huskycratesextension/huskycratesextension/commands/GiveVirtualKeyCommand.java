package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.virtual.Crate;
import com.codehusky.huskycrates.crate.virtual.Key;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;

public class GiveVirtualKeyCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Player player = null;

		if (src instanceof Player) {
			player = (Player) src;

			Player target = (Player) args.getOne("player").get();
			Optional<Crate> crate = args.getOne("crate");
			Optional<Key> key = args.getOne("key");

			HuskycratesExtension.instance.logger.info(target.toString());
			int amount = (Integer) args.getOne("amount").orElse(1);

			Key key2 = crate.isPresent()?
					crate.get().hasLocalKey()? crate.get().getLocalKey():null
							:key.orElse(null);
			
//			String keyName = (crate.isPresent()) ? crate.get().getName() : key.get().getName();
//			String keyID = key.get().getId();
			
			if (key2==null) {
				src.sendMessage(Text.of(TextColors.RED, "Could not recognize key"));
				return CommandResult.success();
			}

			String keyID = key2.getId();

			if (target.getName().equals(player.getName())) {
				src.sendMessage(Text.of(TextColors.RED,
						"You can't give yourself a virtual key"));
				return CommandResult.success();
			}

			if (amount <= 0) {
				src.sendMessage(Text.of(TextColors.RED,
						"You can not give less then 1 key"
				));
				return CommandResult.success();
			}

			Map<String, Integer> virtualBalance = HuskyCrates.registry.getVirtualKeyBalances(player.getUniqueId());

			
			if (virtualBalance == null) {
				player.sendMessage(Text.of(TextColors.RED,
						"You don't have enough "+ key2.getName() + " keys"));
				return CommandResult.success();
			}
			if (!virtualBalance.containsKey(keyID)) {
				src.sendMessage(Text.of(TextColors.RED,
						"You don't have enough "+ key2.getName() + " keys"));
				return CommandResult.success();
			}
			
			if(virtualBalance.get(keyID) == null) {
				src.sendMessage(Text.of(TextColors.RED,
						"You don't have enough "+ key2.getName() + " keys"));
				return CommandResult.success();
			}
			
			if(virtualBalance.get(keyID) < 0) {
				while(virtualBalance.get(keyID) < 0) {
					HuskyCrates.registry.addVirtualKeys(player.getUniqueId(), keyID, 1);
				}
			}

			if (virtualBalance.get(keyID) < amount) {
				
				src.sendMessage(Text.of(TextColors.RED,

					"You don't have enough "+ key2.getName() + " keys"));
				return CommandResult.success();
			}

			/**
			 * Remove key from player
			 */
			HuskyCrates.registry.removeVirtualKeys(player.getUniqueId(), keyID, amount);
			player.sendMessage(Text.of(TextColors.GREEN,

					"You send "+amount + " " + key2.getName() + " to " + target.getName()
			));
			
			/**
			 * Add key to target
			 */
			HuskyCrates.registry.addVirtualKeys(target.getUniqueId(), keyID, amount);
			target.sendMessage(Text.of(TextColors.GREEN,

					"You received " + amount + " " +  key2.getName() + " " + (amount!=1?"keys":"key") + " from " + player.getName()
					//Messages.replaceText(Messages.giveVirtualKeyReceiver, keyName, amount, target, player)

			));

		} else {
			src.sendMessage(Text.of(TextColors.RED, "This command can only be executed by a player"));
		}

		return CommandResult.success();
	}

}
