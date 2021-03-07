package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.virtual.Crate;
import com.codehusky.huskycrates.crate.virtual.Item;
import com.codehusky.huskycrates.crate.virtual.Slot.RewardType;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class AddRewardCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Optional<Crate> cr = args.getOne("crate");
		Optional<RewardType> t = args.getOne("type");
		Optional<String> d = args.getOne("data");

		
		Crate crate = null;
		RewardType type = t.orElse(RewardType.ITEM);
		if (src instanceof Player) {
			Player player = (Player) src;

			if (!player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
				player.sendMessage(Text.of("Your hand can not be empty"));
				return CommandResult.success();
			}


			if (cr.isPresent())
				crate = cr.get();
			if(type != RewardType.ITEM && !d.isPresent()) {
				player.sendMessage(Text.of("Data can not be empty"));
				return CommandResult.success();
			}
			
			try {
				Path newCratePath = HuskyCrates.instance.crateDirectoryPath.resolve(crate.getId() + ".crate");
				ConfigurationLoader<CommentedConfigurationNode> generatedCrateConfig;
				generatedCrateConfig = HoconConfigurationLoader.builder().setPath(newCratePath.toAbsolutePath())
						.build();
				Item item = Item.fromItemStack(player.getItemInHand(HandTypes.MAIN_HAND).get());

				CommentedConfigurationNode root;

				root = generatedCrateConfig.load();

				CommentedConfigurationNode n = root.getNode(crate.getId());


				ConfigurationNode slot = n.getNode("slots").getAppendedNode();
				
				slot.getNode("chance").setValue(10);
				
				ConfigurationNode display =  slot.getNode("displayItem");
				
				display.getNode("id").setValue(item.getItemType().getId());
				display.getNode("name").setValue(item.getName());
				display.getNode("count").setValue(item.getCount());
				display.getNode("damage").setValue(item.getDamage());
				display.getNode("durability").setValue(item.getDurability());
				display.getNode("nbt").setValue(item.getNBT());
				display.getNode("lore").setValue(item.getLore());
				display.getNode("enchantments").setValue(item.getEnchantments());

				ConfigurationNode rewards = slot.getNode("rewards");
				ConfigurationNode rewards2 = rewards.getAppendedNode();
				
				rewards2.getNode("type").setValue(type.toString().toLowerCase());
				if(type != RewardType.ITEM) {
					if(d.isPresent()) {
						rewards2.getNode("data").setValue(d.get());
					}else {
						player.sendMessage(Text.of(TextColors.RED,"Data can not be empty"));
						return CommandResult.success();
					}
				}


				generatedCrateConfig.save(root);


			} catch (IOException e) {
				e.printStackTrace();
			}
			src.sendMessage(Text.of(TextColors.GREEN,"Reward added succesfully"));
			return CommandResult.success();
		} else {
			src.sendMessage(Text.of("This command can only be executed as a command"));
			return CommandResult.success();
		}
	}

}
