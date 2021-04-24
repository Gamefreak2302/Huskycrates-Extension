package com.gamefreak.huskycratesextension.huskycratesextension.commands;

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

public class TestAllRewardCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		
		Optional<Crate> cr = args.getOne("crate");
		
		com.gamefreak.huskycratesextension.huskycratesextension.objects.Crate crate2 = null;
		Player player = null;
		Crate crate = null;
		if (src instanceof Player) {
			player = (Player) src;
			if (!cr.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "Crate can not be empty"));
				return CommandResult.success();
			} else {
				crate = cr.get();

			}

			if (crate != null) {

				crate2 = HuskycratesExtension.registry.getCrates().get(crate.getId().toLowerCase());

				if(HuskycratesExtension.registry.getUntestableCrates().contains(crate2)) {
					player.sendMessage(Text.of(TextColors.RED,"You can not test this crate"));
					return CommandResult.success();
				}

				String log = "";
				for(Slot slot :crate2.getSlots()) {

					HuskycratesExtension.registry.execute(slot, crate,player);

				}
				log = String.format("%s tested ALL slots from the %s crate", player.getName(), crate.getId());
				HuskycratesExtension.registry.SendPlayersNoteOfTest("huskycratesextension.message.test", log);
				HuskycratesExtension.instance.logger.info(log);
				return CommandResult.success();

			} else {
				src.sendMessage(Text.of("Command must be executed as a player!"));
				return CommandResult.success();

			}
		}
		

		return CommandResult.success();

	}
	
}
