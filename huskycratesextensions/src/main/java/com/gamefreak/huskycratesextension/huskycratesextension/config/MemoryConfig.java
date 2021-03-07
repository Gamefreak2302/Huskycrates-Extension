package com.gamefreak.huskycratesextension.huskycratesextension.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Crate;

import ninja.leaping.configurate.ConfigurationNode;

public class MemoryConfig {

	public static List<Crate> crates;
	public static boolean multiple;
	public static int maxChances;

	public MemoryConfig() {

		ConfigurationNode node;
		try {
			node = HuskycratesExtension.instance.keyConfig.load();
			crates = new ArrayList<Crate>();
			ConfigurationNode memoryNode = node.getNode("memory");

			MemoryConfig.multiple = memoryNode.getNode("allowMultiplePrizes") != null
					? memoryNode.getNode("allowMultiplePrizes").getBoolean()
					: false;
			MemoryConfig.maxChances = memoryNode.getNode("maxChances") != null 
					? memoryNode.getNode("maxChances").getInt() <= 0? 
							5:memoryNode.getNode("maxChances").getInt()
					: 5;

			if (memoryNode.getNode("cratesAsMemory").hasListChildren()) {
				for (ConfigurationNode n : memoryNode.getNode("cratesAsMemory").getChildrenList()) {
					if (isCrate(n.getString())) {
						Crate cr = HuskycratesExtension.registry.getCrate(n.getString().toLowerCase());
						if (cr != null)
							crates.add(cr);
					}
				}

			}
			memoryNode.getNode("cratesAsMemory").setValue(crates.stream().map(s -> s.getId()).collect(Collectors.toList()));
			memoryNode.getNode("allowMultiplePrizes").setValue(multiple);
			memoryNode.getNode("maxChances").setValue(maxChances);
			HuskycratesExtension.instance.keyConfig.save(node);

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private boolean isCrate(String crate) {
		return HuskycratesExtension.registry.getCrates().containsKey(crate.toLowerCase());
	}
	public static boolean isMemoryCrate(String crateID) {
		List<String> cratenames = crates.stream().map(s -> s.getId().toLowerCase()).collect(Collectors.toList());
		return cratenames.contains(crateID.toLowerCase());
	}

}
