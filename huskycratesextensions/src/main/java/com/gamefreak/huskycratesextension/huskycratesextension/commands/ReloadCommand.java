package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;


public class ReloadCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		HuskycratesExtension ext = HuskycratesExtension.instance;
		src.sendMessage(Text.of("HuskycratesExtension reloaded"));
		ext.reloadConfig();
		return CommandResult.success();
	}

	
	
}
