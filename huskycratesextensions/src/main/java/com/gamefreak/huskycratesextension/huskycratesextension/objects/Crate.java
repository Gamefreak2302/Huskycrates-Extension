package com.gamefreak.huskycratesextension.huskycratesextension.objects;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;

public class Crate {
    private String id;
    private List<Slot> slots = new ArrayList<Slot>();
    private com.codehusky.huskycrates.crate.virtual.Crate crate;

	
	public Crate(ConfigurationNode node,com.codehusky.huskycrates.crate.virtual.Crate crate) {
	    	setId(node.getKey().toString());
	    	this.crate = crate;
            for(ConfigurationNode slot : node.getNode("slots").getChildrenList()){
                Slot thisSlot = new Slot(slot,crate);
                slots.add(thisSlot);
            }

            
	}
	
	public List<Slot> getSlots() {
		return slots;
	}
	
	public Slot getSlot(int slot) {
		return slots.get(slot);
	}

	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public com.codehusky.huskycrates.crate.virtual.Crate getCrate() {
		return crate;
	}
	

}
