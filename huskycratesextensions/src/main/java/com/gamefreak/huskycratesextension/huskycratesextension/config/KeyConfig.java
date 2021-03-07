package com.gamefreak.huskycratesextension.huskycratesextension.config;

import java.io.IOException;

import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;

import ninja.leaping.configurate.ConfigurationNode;

public class KeyConfig {

	private static int cooldownRightClickSeconds;

	public KeyConfig() {

		try {
			ConfigurationNode nodes = HuskycratesExtension.instance.keyConfig.load();

			ConfigurationNode whitelistedKeyNode = nodes.getNode("keys").getNode("whitelist");
			if (whitelistedKeyNode.hasListChildren()) {
				for (ConfigurationNode keynode : whitelistedKeyNode.getChildrenList()) {
					HuskycratesExtension.registry.addCrateWithRightClick(keynode.getString());
				}

			} else {
				if (HuskycratesExtension.registry.getCratesWithRightClick().isEmpty()) {
					HuskycratesExtension.registry.addCrateWithRightClick("example");
				}
			}

			KeyConfig.cooldownRightClickSeconds = nodes.getNode("keys").getNode("cooldownRightClick") != null
					? nodes.getNode("keys").getNode("cooldownRightClick").getInt() > 0
							? nodes.getNode("keys").getNode("cooldownRightClick").getInt()
							: 2
					: 2;
					
					nodes.getNode("keys").getNode("cooldownRightClick").setValue(KeyConfig.cooldownRightClickSeconds);
			HuskycratesExtension.instance.keyConfig.save(nodes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getCooldownRightClickSeconds() {
		return cooldownRightClickSeconds;
	}
}
