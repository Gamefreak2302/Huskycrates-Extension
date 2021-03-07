package com.gamefreak.huskycratesextension.huskycratesextension.objects;

import com.codehusky.huskycrates.HuskyCrates;
import com.codehusky.huskycrates.crate.virtual.Crate;
import com.codehusky.huskycrates.crate.virtual.Item;
import com.codehusky.huskycrates.crate.virtual.Slot.RewardType;
import com.codehusky.huskycrates.crate.virtual.effects.Effect;
import com.codehusky.huskycrates.exception.ConfigParseError;
import com.codehusky.huskycrates.exception.InjectionDataError;

import ninja.leaping.configurate.ConfigurationNode;

public class Reward {

	private RewardType rewardType;

	private String rewardString; // can be a message or a command. 

	Reward(ConfigurationNode node, ConfigurationNode displayItemNode, Crate holder) {
		try {
			this.rewardType = RewardType.valueOf(node.getNode("type").getString("").toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new ConfigParseError("Invalid reward type or no reward type specified.",
					node.getNode("type").getPath());
		}


		if (this.rewardType == RewardType.USERCOMMAND || this.rewardType == RewardType.SERVERCOMMAND
				|| this.rewardType == RewardType.SERVERMESSAGE || this.rewardType == RewardType.USERMESSAGE
				|| this.rewardType == RewardType.KEY) {
			rewardString = node.getNode("data").getString();
			if (rewardString == null) {
				throw new ConfigParseError("No data specified for reward.", node.getNode("data").getPath());
			}

		} 
	}

	public Reward( Crate holder, RewardType rewardType, String rewardString, Item rewardItem,
			Item slotDisplayItem, Effect effect, Boolean effectOnPlayer, Integer keyCount) {
		this.rewardType = rewardType;
		if (this.rewardType == RewardType.USERCOMMAND || this.rewardType == RewardType.SERVERCOMMAND
				|| this.rewardType == RewardType.SERVERMESSAGE || this.rewardType == RewardType.USERMESSAGE
				|| this.rewardType == RewardType.KEY) {
			this.rewardString = rewardString;
			if (rewardString == null) {
				throw new InjectionDataError("No data specified for injected reward.");
			}
			if (this.rewardType == RewardType.KEY) {
				if (!HuskyCrates.registry.isKey(rewardString) && !holder.hasLocalKey()
						&& !holder.getLocalKey().getId().equals(rewardString)) {
					throw new InjectionDataError("Invalid injected key ID!");
				} else {
					if (keyCount != null) {
					} else {
						throw new InjectionDataError("You cannot inject null as keyCount.");
					}
				}
			}
		}
	}

	public String getRewardString() {
		return rewardString;
	}

	public RewardType getRewardType() {
		return rewardType;
	}

}
