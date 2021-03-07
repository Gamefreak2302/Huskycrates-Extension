package com.gamefreak.huskycratesextension.huskycratesextension.objects;

import java.util.ArrayList;
import java.util.List;


import com.codehusky.huskycrates.crate.virtual.Item;

import ninja.leaping.configurate.ConfigurationNode;

public class Slot {
    private Item displayItem;

    private List<Reward> rewards = new ArrayList<>();
    private List<List<Reward>> rewardGroups = new ArrayList<>();
	
    public Slot(ConfigurationNode node, com.codehusky.huskycrates.crate.virtual.Crate holder){
        this.displayItem = new Item(node.getNode("displayItem"));


        for(ConfigurationNode rNode : node.getNode("rewards").getChildrenList()){
            if(rNode.hasListChildren()){
                ArrayList<Reward> rewardGroup = new ArrayList<>();
                for(ConfigurationNode rgNode : rNode.getChildrenList()){
                    rewardGroup.add(new Reward(rgNode,node.getNode("displayItem"),holder));
                }
                this.rewardGroups.add(rewardGroup);
            }else {
                this.rewards.add(new Reward(rNode,node.getNode("displayItem"),holder));
            }
            

        }


    }
    
    public List<Reward> getRewards() {
		return rewards;
	}
    
    public Item getDisplayItem() {
		return displayItem;

	}
    
    
}
