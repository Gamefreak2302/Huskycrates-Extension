package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.Util;
import com.codehusky.huskycrates.crate.virtual.Crate;
import com.codehusky.huskycrates.crate.virtual.Key;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.config.Messages;

public class ConvertCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		// TODO Auto-generated method stub

		Optional<Crate> cr = args.getOne("crate"); // crate
		Optional<Key> k = args.getOne("key"); // key
		Optional<Integer> am = args.getOne("amount"); // amount
		Key key = null;

		int amount = am.orElse(1);
		@SuppressWarnings("unused")
		InventoryTransactionResult result = null;

		if (src instanceof Player) {
			Player player = (Player) src;
			if(!player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
				player.sendMessage(Text.of(TextColors.RED, "Your hand can not be empty"));
				return CommandResult.success();
			}
			ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();
			Map<String, Integer> virtualBalance = HuskyCrates.registry.getVirtualKeyBalances(player.getUniqueId());

			if (!k.isPresent() && !cr.isPresent()) { //key to virtual
				try {
					Optional<Object> keyobject = item.toContainer().get(DataQuery.of("UnsafeData", "HCKEYID"));
					String keyId = null;
					if (keyobject.isPresent()) {
						keyId = keyobject.get().toString();
					}
					if (keyId != null) {
						if (HuskyCrates.registry.isKey(keyId)) {
							int amount2 = item.getQuantity();

							if (!HuskyCrates.KEY_SECURITY || HuskyCrates.registry.validateSecureKey(item, 1)) {
								HuskyCrates.registry.addVirtualKeys(player.getUniqueId(), keyId, amount2);
								player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.empty());
								player.sendMessage(Text.of(TextColors.GREEN,
										String.format(
												Messages.replaceText(Messages.convertToVirtualSuccess,HuskycratesExtension.registry.getStringWithoutColor( keyId.replace("LOCALKEY_", "")), amount2, null, player)

										)));
								HuskycratesExtension.instance.logger.info(String.format(
										"%s has converted %d %s %s to virtual keys", player.getName(), amount2,
										HuskycratesExtension.registry.getStringWithoutColor( keyId.replace("LOCALKEY_", "")), amount2 != 1 ? "keys" : "key"));
								return CommandResult.success();

							} else {
								player.sendMessage(
										Text.of(TextColors.RED, 
												Messages.convertToVirtualDuplicate
												));
								HuskycratesExtension.instance.logger
										.info(player.getName() + " tried to use a duplicated key!");
								return CommandResult.success();
							}

						} else {
							player.sendMessage(Text.of(TextColors.RED, 
									Messages.convertToVirtualNoKey
									));
							return CommandResult.success();
						}
					} else {
						player.sendMessage(Text.of(TextColors.RED, 
								Messages.convertToVirtualNoKey
								));
						return CommandResult.success();
					}
				} catch (NoSuchElementException ex) {
					player.sendMessage(Text.of(TextColors.RED, 
							Messages.convertToVirtualNoKey
							));
					return CommandResult.success();
				}

			} else { // virtual to key

				key = cr.isPresent() ? cr.get().getLocalKey() : k.get();
				
				
				if (key == null) {
					player.sendMessage(Text.of(TextColors.RED,"The key seems to be empty"));
					return CommandResult.success();
				}
				if (amount < 0) {
					src.sendMessage(Text.of(TextColors.RED, 
							
							Messages.convertToPhysicalNegative
							));
					return CommandResult.success();
				}

				if (virtualBalance.containsKey(key.getId())) {
					if (virtualBalance.get(key.getId()) >= amount) { 
						ItemStack stack = key.getKeyItemStack(amount);
						if(player.getInventory().first().canFit(stack)) {
							HuskyCrates.registry.removeVirtualKeys(player.getUniqueId(), key.getId(), amount);
							result = Util.getHotbarFirst(player.getInventory()).offer(stack);
							src.sendMessage(Text.of(TextColors.GREEN, 
									Messages.replaceText(Messages.convertToPhysicalSuccess, HuskycratesExtension.registry.getStringWithoutColor( key.getId().replace("LOCALKEY_","")), amount, null, player)
									));
							HuskycratesExtension.instance.logger.error(String.format("%s converted %d %s %s", player.getName(), amount,
									 HuskycratesExtension.registry.getStringWithoutColor( key.getId().replace("LOCALKEY_","")),  amount != 1 ? "keys" : "key")
									);
							return CommandResult.success();
						}else {
							player.sendMessage(Text.of(TextColors.RED,"You don't have eough space in your inventory to convert keys"));
							return CommandResult.success();
						}

					}else {
						player.sendMessage(Text.of(TextColors.RED,
								Messages.replaceText(Messages.convertToPhysicalNotEnoughKeys,  HuskycratesExtension.registry.getStringWithoutColor( key.getId().replace("LOCALKEY_","")), amount, null, player)
								));
						return CommandResult.success();

					}
				} else {
					src.sendMessage(Text.of(
							Messages.replaceText(Messages.convertToPhysicalNotEnoughKeys, "none", amount, null, player)
							));
					return CommandResult.success();
				}
			}
		}

		src.sendMessage(Text.of(TextColors.RED, "This command can only be executed as a player"));
		return CommandResult.success();
	}

}
