package com.gamefreak.huskycratesextension.huskycratesextension;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.common.Checks;
import com.codehusky.huskycrates.crate.virtual.Crate;
import com.gamefreak.huskycratesextension.huskycratesextension.commands.CommandRegister;
import com.gamefreak.huskycratesextension.huskycratesextension.config.ItemCommandConfig;
import com.gamefreak.huskycratesextension.huskycratesextension.config.KeyConfig;
import com.gamefreak.huskycratesextension.huskycratesextension.config.MemoryConfig;
import com.gamefreak.huskycratesextension.huskycratesextension.config.Messages;
import com.gamefreak.huskycratesextension.huskycratesextension.config.TestableConfig;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.CommandItem;
import com.google.inject.Inject;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import static com.gamefreak.huskycratesextension.huskycratesextension.ServerStats.*;
//Created by gamefreak_2302

@Plugin(id = ID, name = NAME, version = VERSION, description = DESCRIPTION, authors = { AUTHOR
		}, dependencies = { @Dependency(id = "huskycrates", optional = false),
				@Dependency(id = "huskyui", optional = false) })

public class HuskycratesExtension {

	
	@Inject
	public Logger logger;

	@Inject
	public PluginContainer pC;

	@Inject
	@ConfigDir(sharedRoot = false)
	public Path configDir;

	public static HuskycratesExtension instance;
	public static Registry registry;

	private Path mainConfig;
	@SuppressWarnings("unused")
	private CommentedConfigurationNode keyconfigurations;
	private Path keyConfigPath;
	public ConfigurationLoader<CommentedConfigurationNode> keyConfig;
	
	@SuppressWarnings("unused")
	private CommentedConfigurationNode commanditemconfigurations;
	private Path commandItemConfigPath;
	public ConfigurationLoader<CommentedConfigurationNode> commandItemConfig;

	/**
	 * executed when starting the server
	 * 
	 * @param event
	 */
	@Listener
	public void serverStartEvent(GameInitializationEvent event) {

		try {
			instance = this;
			registry = new Registry();
			CommandRegister.register(this);
			load();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		readConfig();

	}
	
	@Listener
	public void onServerStartedEvent(GameStartedServerEvent event) {
		logger.info( "=== " + NAME + " version " + VERSION + " has started succesfully" + " === ");
	}
	

	/**
	 * loads the configs, create registry and register commands
	 * 
	 * @throws IOException
	 */
	private void load() throws IOException {

		//got to do something
		//try again
		registry.loadHuskyCratesCrate();

		mainConfig = configDir.resolve("config/");
		keyConfigPath = configDir.resolve("config/keys.conf");
		commandItemConfigPath = configDir.resolve("config/commandItem.conf");

		commandItemConfig = HoconConfigurationLoader.builder().setPath(commandItemConfigPath).build();
		keyConfig = HoconConfigurationLoader.builder().setPath(keyConfigPath).build();

		if (checkOrInitalizeDirectory(mainConfig) && checkOrInitalizeConfig(keyConfigPath) && checkOrInitalizeConfig(commandItemConfigPath)) {
			try {
				keyconfigurations = keyConfig.load();
				commanditemconfigurations = commandItemConfig.load();
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failed to load config");
			}
		} else {
			logger.error("Config initialization experienced an error. Please report this to the developer for help.");
		}

		Path KeyMigrationPath = configDir.resolve("keys.conf");
		migrateConfigs(KeyMigrationPath, "/config/keys.conf");
		
		Path commandItemMigrationPath = configDir.resolve("commandItem.conf");
		migrateConfigs(commandItemMigrationPath, "/config/commandItem.conf");

	}

	private void migrateConfigs(Path n, String name) {
		Path conf = Paths.get(configDir.toString() + name);
		if (n.toFile().exists()) {
			checkOrInitalizeDirectory(mainConfig);
			try {
				Files.move(n, conf, StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * creates directory if not exist
	 * 
	 * @param path
	 * @return
	 */
	private boolean checkOrInitalizeDirectory(Path path) {
		if (!path.toFile().exists()) {
			if (!path.toFile().mkdirs()) {
				logger.error("Failed to create new directory at " + path.toAbsolutePath().toString());
				return false;
			}
		}
		return true;
	}

	/**
	 * creates file if not exist
	 * 
	 * @param path
	 * @return
	 */
	private boolean checkOrInitalizeConfig(Path path) {
		if (!path.toFile().exists()) {
			try {
				boolean success = path.toFile().createNewFile();
				if (!success) {
					logger.error("Failed to create new config at " + path.toAbsolutePath().toString());
					return false;
				}
				PrintWriter pw = new PrintWriter(path.toFile());
//                pw.println(defaultContent);
				pw.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Reads config, if node is empty, add default values
	 */
	private void readConfig() {

		new KeyConfig();
		new Messages();
		new MemoryConfig();
		new TestableConfig();
		new ItemCommandConfig();
		
//		logger.info(registry.getCommandItems().keySet().stream().collect(Collectors.joining("\n")));

		try {
			registry.loadHuskyCratesCrate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * clears all crates read config to refresh info
	 */
	public void reloadConfig() {

		logger.info("Config and crates have been reloaded");
		registry.clearCrates();
		try {
			load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Could not load config files");
		}
		readConfig();
		

	}

	/**
	 * Opens a crate on right-click with key
	 * 
	 * @apiNote This only works when the cooldown is over, the crate is in
	 *          cratesWithRightclick and the key is a key
	 * @param event
	 */
	@Listener
	public void onRightClick(InteractItemEvent.Secondary.MainHand event) {

		LocalDateTime time = LocalDateTime.now();
		if (event.getSource() instanceof Player) {

			Player player = (Player) event.getSource();
			ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();

			Checks commonChecks = new Checks();
			if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
				Optional<Object> opt = item.toContainer().get(DataQuery.of("UnsafeData", "HCKEYID"));
				String keyId = null;
				if (opt.isPresent())
					keyId = item.toContainer().get(DataQuery.of("UnsafeData", "HCKEYID")).get().toString();
				if (keyId != null) {
					if (HuskyCrates.registry.isKey(keyId)) {
						if (!registry.isCooldownInteractEventValid(player)) {
							if (HuskycratesExtension.registry.getCratesWithRightClick()
									.contains(keyId.replace("LOCALKEY_", "").trim().toLowerCase())) {
								if (!HuskyCrates.KEY_SECURITY || HuskyCrates.registry.validateSecureKey(item, 1)) {
									registry.addCooldownInteractEvent(player, time);
									Crate crate = HuskyCrates.registry.getCrate(keyId.replace("LOCALKEY_", "").trim());
									commonChecks.tryCrateFromCrate(crate, player);
									item.setQuantity(item.getQuantity() - 1);
									HuskycratesExtension.instance.logger
											.info(String.format("%s has opened a %s crate at %s", player.getName(),
													crate.getName(), player.getLocation().toString()));
								} else {
									player.sendMessage(Text.of(TextColors.RED,
//											"This key is a duplicate and can not be consumed!"
											Messages.distanceOpenDuplicate));
									logger.info(player.getName() + " tried to use a duplicated key!");
								}
							} else {
								player.sendMessage(Text.of(TextColors.RED, String.format(
//										"you can not open this crate from distance, please use this key on crate."
//												+ "\nif you think this is wrong, please contact the server owner"
										Messages.distanceOpenInvalidKey)));
							}
						}
					}
				}
			}

			if( item != null && registry.isCommandItem(item)) {
				String id =  item.toContainer().get(DataQuery.of("UnsafeData","HCE_CommandItem")).get().toString();
				CommandItem it = registry.getCommandItem(id);
				item.setQuantity(item.getQuantity() -1);
				it.execute(player);
			}
		}
	}

	/**
	 * Shows preview when left click with key
	 * 
	 * @param event
	 */
	@Listener
	public void onKeyLeftClick(InteractItemEvent.Primary.MainHand event) {

		if (event.getSource() instanceof Player) {

			Player player = (Player) event.getSource();
			Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);
			if (item.isPresent()) {
				String keyId = null;
				if (registry.isKey(item.get())) {
					keyId = item.get().toContainer().get(DataQuery.of("UnsafeData", "HCKEYID")).get().toString();
					Crate crate = HuskyCrates.registry.getCrate(keyId.replace("LOCALKEY_", "").trim());
					crate.launchPreview(player);

				}
			}

		}
	}

	/**
	 * On executing hc reload, will also reload huskycratesextension
	 * 
	 * @param e is sendcommand event
	 */
	@Listener
	public void reload(SendCommandEvent e) {

		if ((e.getCommand() + " " + e.getArguments()).equalsIgnoreCase("hc reload")) {
			if (e.getSource() instanceof Player) {
				Player player = (Player) e.getSource();
				if (player.hasPermission("huskycratesextension.reload"))
					reloadConfig();
			} else {
				reloadConfig();
			}
		}
	}
	
	@Listener
	public void onBlockPlaced(ChangeBlockEvent.Place e) {
		if(e.getSource() instanceof Player) {
//			Player player = (Player)e.getSource();
			
			ItemStackSnapshot item = e.getCause().getContext().get(EventContextKeys.USED_ITEM).get();
			if(item.toContainer().get(DataQuery.of("UnsafeData","HCE_CommandItem")).isPresent()) {
				e.setCancelled(true);
			}
			
			if(item.toContainer().get(DataQuery.of("UnsafeData","HCKEYID")).isPresent()) {
				e.setCancelled(true);
			}
			
			if(item.toContainer().get(DataQuery.of("UnsafeData","HCKEYID")).isPresent()) {
				e.setCancelled(true);
			}
		}
	}
}
