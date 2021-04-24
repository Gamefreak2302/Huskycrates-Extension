package com.gamefreak.huskycratesextension.huskycratesextension;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.Util;
import com.codehusky.huskycrates.crate.virtual.Item;
import com.codehusky.huskycrates.exception.RewardDeliveryError;
import com.gamefreak.huskycratesextension.huskycratesextension.config.KeyConfig;
import com.gamefreak.huskycratesextension.huskycratesextension.config.TestableConfig;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.CommandItem;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Crate;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Reward;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Slot;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Registry {

	private Collection<String> cratesWithRightClick;
	private Map<Player, LocalDateTime> cooldownInteractEvent;
	private HashMap<String, Crate> crates = new HashMap<>();
	private Set<String> untestableCrates;
	private Map<String, CommandItem> commandItems;
	

	private int interactionCooldown;

	public Registry() {
		this.cratesWithRightClick = new ArrayList<String>();
		this.cooldownInteractEvent = new HashMap<Player, LocalDateTime>();
		this.crates = new HashMap<String, Crate>();
		this.untestableCrates = new HashSet<String>();
		this.commandItems = new HashMap<String, CommandItem>();

	}
	
	/**
	 * Adds a command item to the list with commandItem
	 * @param id id to get the commandItem
	 * @param commandItem object
	 */
	public void addCommandItem( CommandItem commandItem) {
			commandItems.put(commandItem.getId(), commandItem);
		
	}
	/**
	 * Removes commanditem from list
	 * @param id of command item to remove
	 */
	public void removeCommandItem(String id) {
		
		commandItems.remove(id);
	}
	
	/**
	 * Get the commanditem by id
	 * @param id of command item to get
	 * @return commanditem with id
	 */
	public CommandItem getCommandItem(String id) {
		return commandItems.get(id);
	}
	/**
	 * 
	 * @return List of crates being able to be opened by right-click
	 * */
	public Collection<String> getCratesWithRightClick() {
		return cratesWithRightClick;
	}
	

	/**
	 * 
	 * @param player is player who needs cooldown
	 * @param time is time it starts
	 * @param seconds is seconds cooldown 
	 * 
	 */
	public void addCooldownInteractEvent(Player player, LocalDateTime time) {
		cooldownInteractEvent.put(player, time.plusSeconds(KeyConfig.getCooldownRightClickSeconds()));
	}
	
	/**
	 * Turn string of crate to crate if exists
	 * @param crate String to find the crate
	 * @return Crate of huskycrates extension
	 */
	public Crate getCrate(String crate) {
		if(crates.containsKey(crate.toLowerCase()))
			return crates.get(crate.toLowerCase());
		return null;
	}

	/**
	 * 
	 * @param player is player's cooldown in the list
	 * @return returns if player can use the interaction (false) 
	 */
	public boolean isCooldownInteractEventValid(Player player) {
		if (!cooldownInteractEvent.containsKey(player)) {
			return false;
		}
		boolean check = cooldownInteractEvent.get(player).isAfter(LocalDateTime.now());
		if (check)
			player.sendMessage(Text.of(TextColors.RED, String.format(
					"time untill next opening by right click: %s seconds",ChronoUnit.SECONDS.between(LocalDateTime.now(), cooldownInteractEvent.get(player))
//					Messages.replaceText(Messages.distanceOpenCooldown, (int) ChronoUnit.SECONDS
//							.between(LocalDateTime.now(), cooldownInteractEvent.get(player)))
					)));
		return check;
	}

	/**
	 * Clears all crates, used for reloading all crates
	 */
	public void clearCrates() {
		cratesWithRightClick.clear();
		untestableCrates.clear();
		crates.clear();
		commandItems.clear();
		
	}
	
	public boolean isKey(ItemStack stack) {
		String keyId = null;
		Optional<Object> opt = stack.toContainer().get(DataQuery.of("UnsafeData", "HCKEYID"));
		if (opt.isPresent()) {
			keyId = stack.toContainer().get(DataQuery.of("UnsafeData", "HCKEYID")).get().toString();
		if (HuskyCrates.registry.isKey(keyId)) {
			return true;
		}

		}
		return false;

	}

	/**
	 * The key will be added to the config,
	 * to make it possible for the key to open crate by right clicking anywhere
	 * @param key is keyid of a crate
	 */
	public void addCrateWithRightClick(String key) {
		if (!cratesWithRightClick.contains(key.toLowerCase()) && key != null && !key.trim().isEmpty())
			cratesWithRightClick.add(key.toLowerCase());

		CommentedConfigurationNode nodes;
		try {
			nodes = HuskycratesExtension.instance.keyConfig.load();
			CommentedConfigurationNode keysNode = nodes.getNode("keys");
			ConfigurationNode whitelistedKeyNode = keysNode.getNode("whitelist");

			whitelistedKeyNode.setValue(cratesWithRightClick);
			HuskycratesExtension.instance.keyConfig.save(nodes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Gets crates out of huskycrates.
	 * Used to "replicate" the crates of huskycrates, to be able to get reward strings
	 * @throws IOException if file is not found
	 */
	public void loadHuskyCratesCrate() throws IOException {

		File folder = new File(HuskyCrates.instance.crateDirectoryPath.toString());
		File[] files = folder.listFiles();
		List<File> crateFiles = new ArrayList<>();

		for (File file : files) {
			if (file.getName().endsWith(".crate")) {
				crateFiles.add(file);
			}
		}
		for (File file : files) {
			if (file.isFile() && file.getPath().endsWith(".crate") && file.length() > 0) {
//            	HuskycratesExtension.instance.logger.info(file.getName());
				ConfigurationLoader<CommentedConfigurationNode> crateLoop = HoconConfigurationLoader.builder()
						.setPath(file.toPath()).build();

				CommentedConfigurationNode crateThing;
				crateThing = crateLoop.load();

				for (CommentedConfigurationNode node : crateThing.getChildrenMap().values()) {
					com.codehusky.huskycrates.crate.virtual.Crate cr = HuskyCrates.registry
							.getCrate(node.getKey().toString());
					Crate thisCrate = new Crate(node, cr);
					crates.put(thisCrate.getId().toLowerCase(), thisCrate);
				}

			}
		}

	}

/**
 * Removes crates being claimed by right click
 * @param key is crate id/key id to lowercase
 */
	public void removeCrateWithRightClick(String key) {
		if (cratesWithRightClick.contains(key.toLowerCase()) && key != null && !key.trim().isEmpty())
			cratesWithRightClick.remove(key.toLowerCase());

		CommentedConfigurationNode nodes;
		try {
			nodes = HuskycratesExtension.instance.keyConfig.load();
			CommentedConfigurationNode keysNode = nodes.getNode("keys");
			ConfigurationNode whitelistedKeyNode = keysNode.getNode("whitelist");
			whitelistedKeyNode.setValue(cratesWithRightClick);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * Get a map of crates as <CrateID,Crate>
	 * @return map of crates
	 */
	public HashMap<String, Crate> getCrates() {
		return crates;
	}
	/**
	 * Get a set of crate ID's that can not be tested
	 * @return set of untestable crateid's
	 */
	public Set<Crate> getUntestableCrates() {
		return TestableConfig.getTestableCrates();
	}

	/**Sends a message to all players with a certain permission
	 * @param permission is a permission to all players who will read the message.
	 * @param text is the text the players will read 
	 */
	public void SendPlayersNoteOfTest(String permission, String text) {
		for(Player player: Sponge.getServer().getOnlinePlayers()) {
			if(player.hasPermission(permission)) {
				player.sendMessage(Text.of(TextColors.GOLD,text));
			}
		}
	}
	/**
	 * 
	 * Enum containing all possible reward types
	 *
	 */
	public enum RewardType {
		USERCOMMAND, SERVERCOMMAND,
		USERMESSAGE, SERVERMESSAGE,
		ITEM,
		EFFECT,
		KEY
	}
	
	/**
	 * This will replace a certain text in the data string of a command or message
	 * @param player will be the player executing a command
	 * @param text is the full message string
	 * @param slot is the slot of the crate
	 * @param crate is the huskycrates crate which is checked
	 * @return
	 */
	private String replaceCommand(Player player, String text, Slot slot, com.codehusky.huskycrates.crate.virtual.Crate crate) {
		ArrayList<String> vowels = new ArrayList<>(Arrays.asList("a", "e", "i", "o", "u"));
		Item displayItem = slot.getDisplayItem();

		String pP = text.replace("%p", player.getName()).replace("%P", player.getUniqueId().toString())
				.replace("%pxi", player.getLocation().getBlockX() + "")
				.replace("%pyi", player.getLocation().getBlockY() + "")
				.replace("%pzi", player.getLocation().getBlockZ() + "")
				.replace("%pxd", player.getLocation().getX() + "").replace("%pyd", player.getLocation().getY() + "")
				.replace("%pzd", player.getLocation().getZ() + "")
				.replace("%R",
						(displayItem.getName() != null) ? displayItem.getName()
								: displayItem.toItemStack().getTranslation().get())
				.replace("%a",
						(displayItem.getName() != null && vowels.indexOf(displayItem.getName().substring(0, 1)) == 0)
								? "an"
								: "a")
				.replace("%C", (crate.getName()));

		/*
		 * if(Sponge.getPluginManager().isLoaded("placeholderapi")) { return
		 * TextSerializers.FORMATTING_CODE.serialize(PlaceholderServiceImpl.get().
		 * replacePlaceholders(pP, player, null)); }
		 */
		return pP;
	}

	/**
	 * 
	 * @param slot is slot that will be executed
	 * @param crate is the huskycrates crate with the rewards
	 * @param player is the player on who the rewards will be executed
	 */
	public void execute(Slot slot, com.codehusky.huskycrates.crate.virtual.Crate crate,Player player) {

		for (Reward rew2 : slot.getRewards()) {
			switch (rew2.getRewardType()) {

			case ITEM:
				InventoryTransactionResult result = Util.getHotbarFirst(player.getInventory())
						.offer(slot.getDisplayItem().toItemStack());
				if (result.getType() != InventoryTransactionResult.Type.SUCCESS) {
					throw new RewardDeliveryError("Failed to deliver item to " + player.getName() + " from reward.");
				}
				break;
			case SERVERMESSAGE:
				Sponge.getServer().getBroadcastChannel().send(TextSerializers.FORMATTING_CODE
						.deserialize(replaceCommand(player, rew2.getRewardString(), slot, crate)));
				break;
			case USERMESSAGE:
				player.sendMessage(TextSerializers.FORMATTING_CODE
						.deserialize(replaceCommand(player, rew2.getRewardString(), slot, crate)));
				break;
			case SERVERCOMMAND:
				Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
						replaceCommand(player, rew2.getRewardString(), slot, crate));
				break;
			case USERCOMMAND:
				Sponge.getCommandManager().process(player, replaceCommand(player, rew2.getRewardString(), slot, crate));
				break;
			default:
				break;
			}
		}

	}
	

	/**
	 * return cooldown for right click
	 * @return
	 */
	public int getInteractionCooldown() {
		return interactionCooldown;
	}

	
	
	/**
	 * Tales 18 random different slots from a crate, doubles them, shuffles and
	 * returns map with position it's on and slot it is
	 * @param crate
	 * @return
	 */
	public Map<Integer,Slot> generateSlotList(Crate crate){
		
		Set<Slot> slots = new HashSet<Slot>();
		SecureRandom rand = new SecureRandom();
		if(crate.getSlots().size() < 18)
			return null;
		while(slots.size() < 18 ) {
			int randomnumber = rand.nextInt(crate.getSlots().size() - 1);
			slots.add(crate.getSlots().get(randomnumber));
		}
		
		List<Slot> finalslots = new ArrayList<Slot>();
		
		for(Slot sl : slots) {
			finalslots.add(sl);
			finalslots.add(sl);
		}
		Collections.shuffle(finalslots);
		Map<Integer, Slot> slotmap = new HashMap<Integer, Slot>();

		for (int i = 0; i < finalslots.size(); i++) {
			slotmap.put(i, finalslots.get(i));
		}
		return slotmap;
	}
	
	/**
	 * checks if an itemstack is a command item
	 * @param item
	 * @return
	 */
	public boolean isCommandItem(ItemStack item) {
		
		
		if(item.toContainer().get(DataQuery.of("UnsafeData","HCE_CommandItem")).isPresent()) {
			String id = item.toContainer().get(DataQuery.of("UnsafeData","HCE_CommandItem")).get().toString();
			if(commandItems.containsKey(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get a list of command items
	 * @return
	 */
	public Map<String, CommandItem> getCommandItems() {
		return commandItems;
	}
	

	/**
	 * Set string to text, removes all formatting codes
	 * @param text
	 * @return
	 */
	public Text getTextWithoutColor(String text) {
		
		return Text.of(text
				.replace("&1", "")
				.replace("&2", "")
				.replace("&3", "")
				.replace("&4", "")
				.replace("&5", "")
				.replace("&6", "")
				.replace("&7", "")
				.replace("&8", "")
				.replace("&9", "")
				.replace("&a", "")
				.replace("&b", "")
				.replace("&c", "")
				.replace("&d", "")
				.replace("&e", "")
				.replace("&f", "")
				.replace("&o", "")
				.replace("&m", "")
				.replace("&n", "")
				.replace("&l", "")
				.replace("&k", "")
				.replace("&r", "")

				);
	}
	
	public String getStringWithoutColor(String text) {
		
		return text
				.replace("&0", "")
				.replace("&1", "")
				.replace("&2", "")
				.replace("&3", "")
				.replace("&4", "")
				.replace("&5", "")
				.replace("&6", "")
				.replace("&7", "")
				.replace("&8", "")
				.replace("&9", "")
				.replace("&a", "")
				.replace("&b", "")
				.replace("&c", "")
				.replace("&d", "")
				.replace("&e", "")
				.replace("&f", "")
				.replace("&o", "")
				.replace("&m", "")
				.replace("&n", "")
				.replace("&l", "")
				.replace("&k", "")
				.replace("&r", "");
	}


}
