package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.CommandItem;

public class GiveCommandItem implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		Optional<Player> player = args.getOne("player");
		Optional<CommandItem> ci = args.getOne("commanditem");
		int amount = (Integer)args.getOne("amount").orElse(1);
		
		if(!ci.isPresent()) {
			src.sendMessage(Text.of(TextColors.RED,"Command item can not be empty"));
			return CommandResult.success();
		}
		
		if(amount < 1) {
			src.sendMessage(Text.of( TextColors.RED,"Amount can not be lower then 1"));
			return CommandResult.success();
		}
		
		if(!player.isPresent()) {
			src.sendMessage(Text.of(TextColors.RED,"Target player can not be empty"));
			return CommandResult.success();
		}
		
		if(ci.get() != null) {
			DataContainer dc = ci.get().getItem().toItemStack().toContainer().set(DataQuery.of("UnsafeData","HCE_CommandItem"), ci.get().getId());
			ItemStack item = ItemStack.builder().fromContainer(dc).build();
			item.setQuantity(amount);
			player.get().getInventory().offer(item);
			
			player.get().sendMessage(Text.of(TextColors.GREEN,String.format("You received a %s command item", ci.get().getItem().getName())));
			src.sendMessage(Text.of(TextColors.GREEN,String.format("You gave %s a command item with id %s", player.get().getName(),ci.get().getId())));

			HuskycratesExtension.instance.logger.info(String.format("%s has given %s a command item with id %s",src.getName(),player.get().getName(), ci.get().getId() ));
			return CommandResult.success();
		}
		

		return CommandResult.success();
	}

	
}
