package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.crate.virtual.Crate;
import com.codehusky.huskycrates.crate.virtual.Key;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;



public class ManageRightClickCrateCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		
		String option = (String)args.getOne(Text.of("option")).get().toString().toLowerCase();
		Optional<Crate> crate = args.getOne("crate");
		Optional<Key> key = args.getOne("key");
		
		Crate crate2 = null;
		Key key2 = null;
		
		if(crate.isPresent())
			crate2 = crate.get();
		if( key.isPresent())
			key2 = key.get();
		if(option == null) {
			src.sendMessage(Text.of(TextColors.RED,"Please use add/remove"));
			return CommandResult.success();
		}
		
		if(crate2 == null && key2 == null) {
			src.sendMessage(Text.of(TextColors.RED,"The crate/key can not be empty"));
			return CommandResult.success();
		}
		
		
		
		switch(option) {
		case "add": 
			
			if(crate2 != null) {
				HuskycratesExtension.registry.addCrateWithRightClick(crate2.getName());
				src.sendMessage(Text.of(TextColors.GREEN,
						"Key can now be consumed by right clicking"
						));
				HuskycratesExtension.instance.logger.info(String.format("%s has added %s to the rightclick whitelist", src.getName(),crate2.getName()));
				return CommandResult.success();
			}
			
			break;
		case "remove":
			
			if(crate2 != null) {
				HuskycratesExtension.registry.removeCrateWithRightClick(crate2.getName());
				HuskycratesExtension.instance.logger.info(String.format("%s has removed %s to the whitelist", src.getName(),crate2.getName()));
				src.sendMessage(Text.of(TextColors.GREEN,
						"Key can no longer be used from a distance"
						));
				return CommandResult.success();
			}
			break;
		default:
			src.sendMessage(Text.of(TextColors.RED,
					"Invalid option"
					));
			return CommandResult.success();
		}
		
		return CommandResult.success();
	}

}
