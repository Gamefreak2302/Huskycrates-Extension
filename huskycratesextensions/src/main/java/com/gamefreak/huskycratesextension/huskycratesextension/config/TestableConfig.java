package com.gamefreak.huskycratesextension.huskycratesextension.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Crate;

import ninja.leaping.configurate.ConfigurationNode;

public class TestableConfig {

	
	private static Set<Crate> testableCrates;
	public TestableConfig() {
		
		testableCrates = new HashSet<Crate>();
		
		try {
			ConfigurationNode node = HuskycratesExtension.instance.keyConfig.load();
			
			ConfigurationNode testnode = node.getNode("TestCrate");
			if(testnode.getNode("TestBlackList").hasListChildren()) {
				
				for(ConfigurationNode n: testnode.getNode("TestBlackList").getChildrenList()) {
					Crate cr = HuskycratesExtension.registry.getCrate(n.getString());
					if(cr != null) {
						testableCrates.add(cr);
					}
				}
			}
			
			testnode.getNode("TestBlackList").setValue(testableCrates);
			HuskycratesExtension.instance.keyConfig.save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static Set<Crate> getTestableCrates() {
		return testableCrates;
	}
}
