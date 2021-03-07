package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.security.SecureRandom;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.crate.virtual.Crate;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Slot;

public class GiveRandomReward implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		Optional<Crate> cr = args.getOne("crate");
		Optional<Player> target = args.getOne("player");
		
		if(!cr.isPresent()) {
			src.sendMessage(Text.of(TextColors.RED, "Crate can not be empty!"));
			return CommandResult.success();
			
		}
		
		Crate crate = cr.get();
		com.gamefreak.huskycratesextension.huskycratesextension.objects.Crate crate2 = HuskycratesExtension.registry.getCrate(crate.getId().toLowerCase());
		
		if(crate2 == null) {
			src.sendMessage(Text.of(TextColors.RED,"Something went wrong, use /hc reload and try again"));
			return CommandResult.success();
		}
		
		SecureRandom random = new SecureRandom();
		int number = random.nextInt(crate2.getSlots().size());
		
		Slot slot = crate2.getSlot(number);
		
		if(!target.isPresent()) {
			if(src instanceof Player) {
				Player player = (Player)src;
				
				HuskycratesExtension.registry.execute(slot, crate, player);
				return CommandResult.success();
			}else {
				src.sendMessage(Text.of("Target can not be empty when using console"));
				return CommandResult.success();
			}
		}else {
			
			HuskycratesExtension.registry.execute(slot, crate, target.get());
			return CommandResult.success();
		}
	}

}
