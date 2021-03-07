package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.Util;
import com.codehusky.huskycrates.crate.virtual.Crate;
import com.codehusky.huskycrates.crate.virtual.Key;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;

public class GiveKeyNoAutocompletionCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {


		Optional<Crate> cr = args.getOne("crate");
		Optional<Key> key = args.getOne("key");
		Optional<String> playerString = args.getOne("player");
		int amount = (Integer)args.getOne("amount").orElse(1);
		Optional<String> virtual = args.getOne("virtual");
		
		if(!playerString.isPresent()) {
			src.sendMessage(Text.of("Player can not be empty"));
			return CommandResult.success();
		}
		String st = playerString.get();
		Player player = Sponge.getServer().getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(st)).findFirst().orElse(null);
		
		if(player == null ) {
			src.sendMessage(Text.of(TextColors.RED, playerString.get() + " is an invalid playername"));
			return CommandResult.success();
		}
		if(amount <1) {
			src.sendMessage(Text.of(TextColors.RED,"You must atleast give 1 key"));
			return CommandResult.success();
		}
		
        Key workingWith = null;

        
        if(cr.isPresent()){
            if(cr.get().hasLocalKey()){
                workingWith = cr.get().getLocalKey();
            }
        }else if(key.isPresent()){
            workingWith = key.get();
        }
        if(workingWith == null){
            src.sendMessage(HuskyCrates.keyCommandMessages.getCrateNoLocalKey());
            return CommandResult.success();
        }
        
		if(!cr.isPresent() && !key.isPresent()) {
			src.sendMessage(Text.of("key can not be empty"));
			return CommandResult.success();
		}else {
			
			if(virtual.isPresent()) {
				HuskyCrates.registry.addVirtualKeys(player.getUniqueId(), workingWith.getId(), amount);
				player.sendMessage(Text.of(TextColors.GREEN, String.format("You received %d %s %s", amount, workingWith.getId().replace("LOCALKEY_", ""), amount !=1?"keys":"key")));
				HuskycratesExtension.instance.logger.info(String.format("%s received %d %s %s",player.getName(), amount, workingWith.getId().replace("LOCALKEY_", ""), amount !=1?"keys":"key"));
				return CommandResult.success();
			}else {
				Util.getHotbarFirst(player.getInventory()).offer(workingWith.getKeyItemStack(amount));
				HuskycratesExtension.instance.logger.info(String.format("%s received %d %s %s",player.getName(), amount, workingWith.getId().replace("LOCALKEY_", ""), amount !=1?"keys":"key"));
				player.sendMessage(Text.of(TextColors.GREEN,String.format("You received %d %s %s", amount, workingWith.getId().replace("LOCALKEY_", ""), amount !=1?"keys":"key")));
				return CommandResult.success();
			}
		}
	}	
	
	
}
