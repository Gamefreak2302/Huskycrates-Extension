package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Optional;

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

import com.codehusky.huskycrates.crate.virtual.Item;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.CommandItem;

public class AddCommandItem implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		Optional<String> id = args.getOne("id");
		Optional<String> cmd = args.getOne("command");
		
		if (src instanceof Player) {
			Player player = (Player)src;
			
			if(!id.isPresent()) {
				player.sendMessage(Text.of(TextColors.RED,"ID can not be empty"));
				return CommandResult.success();
			}
			
			if(HuskycratesExtension.registry.getCommandItems().keySet().contains(id.get())) {
				player.sendMessage(Text.of(TextColors.RED,"ID already exists"));
				return CommandResult.success();
			}
			
			if(!cmd.isPresent()) {
				player.sendMessage(Text.of(TextColors.RED,"Command can not be empty"));
				return CommandResult.success();
			}
			
			if(!player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
				player.sendMessage(Text.of(TextColors.RED, "Your mainhand does not have an item"));
				return CommandResult.success();
			}
			
			ItemStack st = player.getItemInHand(HandTypes.MAIN_HAND).get();

			if(st.getType() == ItemTypes.AIR) {
				player.sendMessage(Text.of(TextColors.RED, "Your mainhand does not have an item"));
				return CommandResult.success();
				}
			
			String command = cmd.get();
			if(command.startsWith("/")) {
				command.replaceFirst("/", "");
			}
			Item item = Item.fromItemStack(st);

			CommandItem ci = new CommandItem(id.get(), item,st, command);
			st.setQuantity(st.getQuantity() -1);
			HuskycratesExtension.registry.addCommandItem(ci);
			player.sendMessage(Text.of(TextColors.GREEN,"Command Item successfully added"));
			HuskycratesExtension.instance.logger.info("Command item successfully added!");
			return CommandResult.success();
		}
		
		return CommandResult.success();
	}

	
}
