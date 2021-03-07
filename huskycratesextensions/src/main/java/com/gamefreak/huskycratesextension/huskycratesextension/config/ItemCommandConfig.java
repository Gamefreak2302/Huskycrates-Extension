package com.gamefreak.huskycratesextension.huskycratesextension.config;

import java.io.IOException;

import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.CommandItem;

import ninja.leaping.configurate.ConfigurationNode;

public class ItemCommandConfig {

	
	public ItemCommandConfig() {
		
		
		try {
			ConfigurationNode nodes = HuskycratesExtension.instance.commandItemConfig.load();
			
			
			ConfigurationNode commandItems = nodes.getNode("commandItems");
			
			if(commandItems.hasListChildren()) {
				for(ConfigurationNode node : commandItems.getChildrenList()) {
					CommandItem item = new CommandItem(node);
					HuskycratesExtension.registry.addCommandItem(item);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	
	}
	

	
	
}
