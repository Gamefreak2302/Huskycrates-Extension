package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Map;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.virtual.Crate;
import com.codehusky.huskycrates.crate.virtual.Key;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;

public class ConvertCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		// TODO Auto-generated method stub

		Crate crate = (Crate)args.getOne("crate").orElse(null); // crate
		Key k = (Key)args.getOne("key").orElse(null); // key
		int amount = (Integer)args.getOne("amount").orElse(1); // amount
		boolean tovirtual = (crate != null || k != null); // true if virtual to physical
		Player player = src instanceof Player?(Player)src:null;
		
		if(player == null) {
			src.sendMessage(Text.of(TextColors.RED,"Command can only be executed as a player"));
		}
		
		Map<String,Integer> balance = HuskyCrates.registry.getVirtualKeyBalances(player.getUniqueId());

		if(tovirtual) { // virtual to key
			Key key = crate!=null? crate.getLocalKey():k;
			if(balance.get(key.getId())!= null && balance.get(key.getId()) <=0) {
				HuskyCrates.registry.setVirtualKeys(player.getUniqueId(), key.getId(), 0);
				player.sendMessage(Text.of(TextColors.RED,"You don't have any " + HuskycratesExtension.registry.getStringWithoutColor(key.getName()) + "keys"));
				return CommandResult.success();
			}
			
			if(amount > balance.get(key.getId())) {
				player.sendMessage(Text.of(TextColors.RED,"You don't have enough " +  HuskycratesExtension.registry.getStringWithoutColor(key.getName()) + "keys"));
				return CommandResult.success();
			}
			
			ItemStack stack = key.getKeyItemStack(amount);
			if(player.getInventory().first().canFit(stack)) {
				HuskyCrates.registry.removeVirtualKeys(player.getUniqueId(), key.getId(), amount);
				player.getInventory().offer(stack);
				player.sendMessage(Text.of(TextColors.GREEN,"You converted " + amount + " " +  HuskycratesExtension.registry.getStringWithoutColor(key.getName()) + " " +  (amount!=1?"keys":"key")));
				HuskycratesExtension.instance.logger.info(player.getName() + "has converted " + amount + " " + HuskycratesExtension.registry.getStringWithoutColor(key.getName()) + (amount!=1?"keys":"key") + " to physical keys.");
				return CommandResult.success();
			}else {
				player.sendMessage(Text.of(TextColors.RED,"You don't have enough space in your inventory"));
				return CommandResult.success();
			}
			
		}else { //physical to virtual
			
			ItemStack stack = player.getItemInHand(HandTypes.MAIN_HAND).get();
			if(stack == null || stack.getType() == ItemTypes.AIR) {
				player.sendMessage(Text.of(TextColors.RED,"There is no item in your main hand"));
				return CommandResult.success();
			}
			
			if(!HuskycratesExtension.registry.isKey(stack)) {
				player.sendMessage(Text.of(TextColors.RED,"Item in main hand is no key"));
				return CommandResult.success();
			}
			Key key = HuskyCrates.registry.getKey(Key.extractKeyId(stack));
			amount = stack.getQuantity();
			if(!HuskyCrates.KEY_SECURITY || HuskyCrates.registry.validateSecureKey(stack, amount)) {
				HuskyCrates.registry.addVirtualKeys(player.getUniqueId(), key.getId(), amount);
				player.getItemInHand(HandTypes.MAIN_HAND).get().setQuantity(0);
				player.sendMessage(Text.of(TextColors.GREEN,"You converted " + amount + " " +  HuskycratesExtension.registry.getStringWithoutColor(key.getName()) + " " + (amount!=1?"keys":"key") + "." ));
				HuskycratesExtension.instance.logger.info(player.getName() + " converted " + amount + " " +  HuskycratesExtension.registry.getStringWithoutColor(key.getName()) + " " + (amount!=1?"keys":"key") + " to virtual keys.");
				return CommandResult.success();
			}else {
				player.sendMessage(Text.of(TextColors.RED, "Some of the keys in your hand are Duplicated"));
				HuskycratesExtension.instance.logger.info( player.getName() + "tried to convert duplicated keys");
				return CommandResult.success();
			}
		}
//		Key key = null;
//
//		@SuppressWarnings("unused")
//		InventoryTransactionResult result = null;
//
//		if (src instanceof Player) {
//			Player player = (Player) src;
//			if(!player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
//				player.sendMessage(Text.of(TextColors.RED, "Your hand can not be empty"));
//				return CommandResult.success();
//			}
//			ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();
//			Map<String, Integer> virtualBalance = HuskyCrates.registry.getVirtualKeyBalances(player.getUniqueId());
//
//			if (!k.isPresent() && !cr.isPresent()) { //key to virtual
//				try {
//					Optional<Object> keyobject = item.toContainer().get(DataQuery.of("UnsafeData", "HCKEYID"));
//					String keyId = null;
//					if (keyobject.isPresent()) {
//						keyId = keyobject.get().toString();
//					}
//					if (keyId != null) {
//						if (HuskyCrates.registry.isKey(keyId)) {
//							Key key = HuskyCrates.registry.getKey(keyId);
//							int amount2 = item.getQuantity();
//							if(amount<=0) {
//								src.sendMessage(Text.of("You are not holding any keys"));
//								return CommandResult.success();
//							}
//							if (!HuskyCrates.KEY_SECURITY || HuskyCrates.registry.validateSecureKey(item, 1)) {
//								HuskyCrates.registry.addVirtualKeys(player.getUniqueId(), keyId, amount2);
//								player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.empty());
//								player.sendMessage(Text.of(TextColors.GREEN,
//										String.format(
//												Messages.replaceText(Messages.convertToVirtualSuccess,HuskycratesExtension.registry.getStringWithoutColor( keyId.replace("LOCALKEY_", "")), amount2, null, player)
//
//										)));
//								HuskycratesExtension.instance.logger.info(String.format(
//										"%s has converted %d %s %s to virtual keys", player.getName(), amount2,
//										HuskycratesExtension.registry.getStringWithoutColor( keyId.replace("LOCALKEY_", "")), amount2 != 1 ? "keys" : "key"));
//								return CommandResult.success();
//
//							} else {
//								player.sendMessage(
//										Text.of(TextColors.RED, 
//												Messages.convertToVirtualDuplicate
//												));
//								HuskycratesExtension.instance.logger
//										.info(player.getName() + " tried to use a duplicated key!");
//								return CommandResult.success();
//							}
//
//						} else {
//							player.sendMessage(Text.of(TextColors.RED, 
//									Messages.convertToVirtualNoKey
//									));
//							return CommandResult.success();
//						}
//					} else {
//						player.sendMessage(Text.of(TextColors.RED, 
//								Messages.convertToVirtualNoKey
//								));
//						return CommandResult.success();
//					}
//				} catch (NoSuchElementException ex) {
//					player.sendMessage(Text.of(TextColors.RED, 
//							Messages.convertToVirtualNoKey
//							));
//					return CommandResult.success();
//				}
//
//			} else { // virtual to key
//
//				key = cr.isPresent() ? cr.get().getLocalKey() : k.get();
//				
//				
//				
//				if (key == null) {
//					player.sendMessage(Text.of(TextColors.RED,"The key seems to be empty"));
//					return CommandResult.success();
//				}
//				if (amount < 0) {
//					src.sendMessage(Text.of(TextColors.RED, 
//							
//							Messages.convertToPhysicalNegative
//							));
//					return CommandResult.success();
//				}
//
//				if (virtualBalance.containsKey(key.getId())) {
//						ItemStack stack = key.getKeyItemStack(amount);
//						if(player.getInventory().first().canFit(stack)) {
//							HuskyCrates.registry.removeVirtualKeys(player.getUniqueId(), key.getId(), amount);
//							result = Util.getHotbarFirst(player.getInventory()).offer(stack);
//							src.sendMessage(Text.of(TextColors.GREEN, 
//									Messages.replaceText(Messages.convertToPhysicalSuccess, HuskycratesExtension.registry.getStringWithoutColor( key.getName()), amount, player, player)
//									));
//							HuskycratesExtension.instance.logger.error(String.format("%s converted %d %s %s", player.getName(), amount,
//									 HuskycratesExtension.registry.getStringWithoutColor( key.getName()),  amount != 1 ? "keys" : "key")
//									);
//							return CommandResult.success();
//						}else {
//							player.sendMessage(Text.of(TextColors.RED,"You don't have eough space in your inventory to convert keys"));
//							return CommandResult.success();
//						}
//
//					}else {
//						player.sendMessage(Text.of(TextColors.RED,
//								Messages.replaceText(Messages.convertToPhysicalNotEnoughKeys,  HuskycratesExtension.registry.getStringWithoutColor( key.getId().replace("LOCALKEY_","")), amount, null, player)
//								));
//						return CommandResult.success();
//
//					}
//				} else {
//					src.sendMessage(Text.of(
//							Messages.replaceText(Messages.convertToPhysicalNotEnoughKeys, "none", amount, null, player)
//							));
//					return CommandResult.success();
//				}
//			}
//		}
//
//		src.sendMessage(Text.of(TextColors.RED, "This command can only be executed as a player"));
//		return CommandResult.success();
	}

}
