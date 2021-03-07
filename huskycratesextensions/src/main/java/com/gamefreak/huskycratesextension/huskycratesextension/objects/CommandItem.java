package com.gamefreak.huskycratesextension.huskycratesextension.objects;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import com.codehusky.huskycrates.crate.virtual.Item;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;

import ninja.leaping.configurate.ConfigurationNode;

public class CommandItem {

	private String id;
	private String command;
	private Item item;

	public CommandItem(ConfigurationNode n) {


		this.id = n.getNode("commandItemId") != null ? n.getNode("commandItemId").getString()
				: null;

		this.command = n.getNode("command") != null ? n.getNode("command").getString() : null;		
		this.item = new Item(n.getNode("Item"));
	}

	public CommandItem(String id, Item item,ItemStack itemstack, String command) {

		try {
			ConfigurationNode nodes = HuskycratesExtension.instance.commandItemConfig.load();

			this.id = id;
			this.command = command;
			this.item = item;

			ConfigurationNode nd = nodes.getNode("commandItems").getAppendedNode();

			ItemToConfig(nd.getNode("Item"), item);
			nd.getNode("commandItemId").setValue(id);
			nd.getNode("command").setValue(command);

			HuskycratesExtension.instance.commandItemConfig.save(nodes);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public String getCommand() {
		return command;
	}

	public void execute(Player player) {
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
				this.command.replace("%p", player.getName()));
	}

	public void ItemToConfig(ConfigurationNode node, Item item) {
		node.getNode("id").setValue(item.getItemType().getId());
		node.getNode("name")
				.setValue(item.getName() != null ? item.getName() : item.getItemType().getId().split(":")[1]);
		node.getNode("count").setValue(item.getCount());
		node.getNode("damage").setValue(item.getDamage());
		node.getNode("durability").setValue(item.getDurability());
		node.getNode("lore").setValue(item.getLore());

        Map<String, Object> enchants = new HashMap<String, Object>();

		if (item.getEnchantments() != null && item.getEnchantments().size() > 0){
            item.getEnchantments().forEach(enchantment -> {
                enchants.put(enchantment.getType().getId(), enchantment.getLevel());
            });
        }
		node.getNode("enchantments")
		.setValue(enchants.size() > 0?enchants:null);
 
		node.getNode("nbt").setValue(item.getNBT() != null ? item.getNBT() : new HashMap<>());
		
	}

}
