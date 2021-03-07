package com.gamefreak.huskycratesextension.huskycratesextension.config;

import java.io.IOException;

import org.spongepowered.api.entity.living.player.Player;

import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Messages {
	
	public static String distanceOpenInvalidKey;
	public static String distanceOpenDuplicate;
	public static String distanceOpenCooldown;
	
	public static String convertToVirtualSuccess;
	public static String convertToVirtualDuplicate;
	public static String convertToVirtualNoKey;
	
	public static String convertToPhysicalNegative;
	public static String convertToPhysicalNotEnoughKeys;
	public static String convertToPhysicalSuccess;
	
	public static String giveVirtualKeyTargetEmpty;
	public static String giveVirtualKeySelfTarget;
	public static String giveVirtualKeyEmptyKey;
	public static String giveVirtualKeyNegativeAmount;
	public static String giveVirtualKeyNotEnoughKey;
	public static String giveVirtualKeySender;
	public static String giveVirtualKeyReceiver;
	
	public static String manageRightClickRemove;
	public static String manageRightClickAdd;
	public static String manageRightClickInvalidOption;
	
	public static String crateUntestable;
	public static String noMemoryCrate;
	public static String memoryOutOfMoves;
	
	public static String notEnoughKeys;

	public Messages() {
		
		//Placeholders: 
		//%player -> player
		//%crate -> crate
		//%time -> time
		//%target -> target
		//%amount -> amount
		//%keys -> key / keys
		CommentedConfigurationNode nodes;
		try {
			nodes = HuskycratesExtension.instance.keyConfig.load();
			ConfigurationNode node = nodes.getNode("keys").getNode("messages");

			Messages.distanceOpenInvalidKey = !checkMessage(node.getNode("distanceOpenInvalidKey").getString())?
					"you can not open this crate from distance, please use this key on crate.":node.getNode("distanceOpenInvalidKey").getString();
			Messages.distanceOpenDuplicate = checkMessage(node.getNode("distanceOpenDuplicate").getString())?
					"This key is a duplicate and can not be consumed!":node.getNode("distanceOpenDuplicate").getString();
			Messages.distanceOpenCooldown = !checkMessage(node.getNode("distanceOpenCooldown").getString())?
					"time untill next opening by right click: %time seconds":node.getNode("distanceOpenCooldown").getString();
			
			Messages.convertToVirtualSuccess = !checkMessage(node.getNode("convertToVirtualSuccess").getString())?
				"You converted %amount %crate %keys to virtual keys":node.getNode((convertToVirtualSuccess)).getString();
			Messages.convertToVirtualDuplicate = !checkMessage(node.getNode("convertToVirtualDuplicate").getString())?
					"your hand contains duplicated keys and can not be consumed!, remove them and try again!":node.getNode("convertToVirtualDuplicate").getString();
			Messages.convertToVirtualNoKey = !checkMessage(node.getNode("convertToVirtualNoKey").getString())?
					"Main hand doesn't contain a key":node.getNode("convertToVirtualNoKey").getString();
			
			Messages.convertToPhysicalNegative = !checkMessage(node.getNode("convertToPhysicalNegative").getString())?
					"You can't convert a negative amount of keys":node.getNode("convertToPhysicalNegative").getString();
			Messages.convertToPhysicalSuccess = !checkMessage(node.getNode("convertToPhysicalSuccess").getString())?
					"You converted %amount %crate %keys":node.getNode("convertToPhysicalSuccess").getString();
			Messages.convertToPhysicalNotEnoughKeys = !checkMessage(node.getNode("convertToPhysicalNotEnoughKeys").getString())?
					"You don't have enough keys to convert them":node.getNode("convertToPhysicalNotEnoughKeys").getString();
			
			Messages.giveVirtualKeyTargetEmpty= !checkMessage(node.getNode("giveVirtualKeyTargetEmpty").getString())?
					"target player can not be empty":node.getNode("giveVirtualKeyTargetEmpty").getString();
			Messages.giveVirtualKeySelfTarget = !checkMessage(node.getNode("giveVirtualKeySelfTarget").getString())?
					"You can't send a key to yourself":node.getNode("giveVirtualKeySelfTarget").getString();
			Messages.giveVirtualKeyEmptyKey = !checkMessage(node.getNode("giveVirtualKeyEmptyKey").getString())?
					"Key can not be empty":node.getNode("giveVirtualKeyEmptyKey").getString();
			Messages.giveVirtualKeyNegativeAmount = !checkMessage(node.getNode("giveVirtualKeyNegativeAmount").getString())?
					"Amount need to be higher then 0":node.getNode("giveVirtualKeyNegativeAmount").getString();
			Messages.giveVirtualKeyNotEnoughKey = !checkMessage(node.getNode("giveVirtualKeyNotEnoughKey").getString())?
					"You don't have enough keys":node.getNode("giveVirtualKeyNoKey").getString();
			Messages.giveVirtualKeySender = !checkMessage(node.getNode("giveVirtualKeySender").getString())?
					"You send %amount %crate %keys to %target":node.getNode("giveVirtualKeySender").getString();
			Messages.giveVirtualKeyReceiver = !checkMessage(node.getNode("giveVirtualKeyReceiver").getString())?
					"You received %amount %crate %keys from %player":node.getNode("giveVirtualKeyReceiver").getString();
			
			Messages.manageRightClickRemove = !checkMessage(node.getNode("manageRightClickRemove").getString())?
					"Crate can no longer be claimed by right clicking":node.getNode("manageRightClickRemove").getString();
			Messages.manageRightClickInvalidOption = !checkMessage(node.getNode("manageRightClickInvalidOption").getString())?
					"Invalid right click option":node.getNode("manageRightClickInvalidOption").getString();
			Messages.manageRightClickAdd = !checkMessage(node.getNode("manageRightClickAdd").getString())?
					"Crate can now be claimed by right clicking": node.getNode("manageRightClickAdd").getString();
			
			Messages.crateUntestable = !checkMessage(node.getNode("crateUntestable").getString())?
					"%c crate can not be tested": node.getNode("crateUntestable").getString();
			
			Messages.noMemoryCrate = !checkMessage(node.getNode("noMemoryCrate").getString())?
					"%c can not be executed as a memory game": node.getNode("noMemoryCrate").getString();
			Messages.memoryOutOfMoves = !checkMessage(node.getNode("memoryOutOfMoves").getString())?
					"You are out of moves": node.getNode("memoryOutOfMoves").getString();
			
			Messages.notEnoughKeys = !checkMessage(node.getNode("notEnoughKeys").getString())?
					"You don't gave enough keys": node.getNode("notEnoughKeys").getString();
			
			node.getNode("distanceOpenInvalidKey").setValue(distanceOpenInvalidKey);

			node.getNode("distanceOpenCooldown").setValue(distanceOpenCooldown);
			node.getNode("distanceOpenDuplicate").setValue(distanceOpenDuplicate);
			
			node.getNode("convertToPhysicalNegative").setValue(convertToPhysicalNegative);
			node.getNode("convertToPhysicalNotEnoughKeys").setValue(convertToPhysicalNotEnoughKeys);
			node.getNode("convertToPhysicalSuccess").setValue(convertToPhysicalSuccess);
			
			node.getNode("convertToVirtualDuplicate").setValue(convertToVirtualDuplicate);
			node.getNode("convertToVirtualNoKey").setValue(convertToVirtualNoKey);
			node.getNode("convertToVirtualSuccess").setValue(convertToVirtualSuccess);
			
			node.getNode("giveVirtualKeyEmptyKey").setValue(giveVirtualKeyEmptyKey);
			node.getNode("giveVirtualKeyNegativeAmount").setValue(giveVirtualKeyNegativeAmount);
			node.getNode("giveVirtualKeyNotEnoughKey").setValue(giveVirtualKeyNotEnoughKey);
			node.getNode("giveVirtualKeyReceiver").setValue(giveVirtualKeyReceiver);
			node.getNode("giveVirtualKeySelfTarget").setValue(giveVirtualKeySelfTarget);
			node.getNode("giveVirtualKeySender").setValue(giveVirtualKeySender);
			node.getNode("giveVirtualKeyTargetEmpty").setValue(giveVirtualKeyTargetEmpty);
			
			node.getNode("manageRightClickAdd").setValue(manageRightClickAdd);
			node.getNode("manageRightClickInvalidOption").setValue(manageRightClickInvalidOption);
			node.getNode("manageRightClickRemove").setValue(manageRightClickRemove);
			
			node.getNode("crateUntestable").setValue(crateUntestable);
			node.getNode("noMemoryCrate").setValue(noMemoryCrate);
			node.getNode("memoryOutOfMoves").setValue(memoryOutOfMoves);

			node.getNode("notEnoughKeys").setValue(notEnoughKeys);

			
			HuskycratesExtension.instance.keyConfig.save(nodes);
			} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean checkMessage(String message) {
		
		return message != null && !message.trim().equals("");
	}
	


	public static String replaceText(String text,int time) {
		
		return text.replace("%time", String.format("%d", time));
	}
	
	public static String replaceText(String text,String crate, int amount, Player target,Player player) {
		
		String fulltext = text;
		if(crate !=null && !crate.trim().equalsIgnoreCase(""))
			fulltext = fulltext.replace("%crate", crate);
		if(player != null)
			fulltext = fulltext.replace("%amount", String.format("%d", amount));
		if(amount != -1)
			fulltext = fulltext.replace("%amount", String.format("%d", amount)).replace("%keys", amount !=1?"keys":"key");
		if(target != null)
			fulltext = fulltext.replace("%target", target.getName());
		if(player != null)
			fulltext = fulltext.replace("%player", player.getName());
		return fulltext;
				
	}
	

	
	
}
