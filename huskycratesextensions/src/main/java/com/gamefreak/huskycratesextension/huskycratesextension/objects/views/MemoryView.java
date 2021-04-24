package com.gamefreak.huskycratesextension.huskycratesextension.objects.views;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.Page;
import com.codehusky.huskyui.states.action.ActionType;
import com.codehusky.huskyui.states.action.runnable.RunnableAction;
import com.codehusky.huskyui.states.element.ActionableElement;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.config.MemoryConfig;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Crate;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Slot;

public class MemoryView implements Consumer<Page> {

	private Map<Integer, Slot> winnings;
	private Player player;
	private Crate crate2;
	private com.codehusky.huskycrates.crate.virtual.Crate crate;
	private Slot firstPrize;
	private int firstSlot;
	private Slot secondPrize;
	private int secondSlot;
	private StateContainer container;
	private int chances = 5;
	private Map<Integer, Slot> slots;
	private final ItemStack CHEST = ItemStack.builder().itemType(ItemTypes.CHEST)
			.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "???")).build();

	public MemoryView(com.codehusky.huskycrates.crate.virtual.Crate crate, Player player) {

		this.player = player;
		this.chances = MemoryConfig.maxChances;
		this.firstPrize = null;
		this.secondPrize = null;
		this.crate = crate;
		this.winnings = new HashMap<Integer, Slot>();
		crate2 = HuskycratesExtension.registry.getCrates().get(crate.getId().toLowerCase());
		slots = HuskycratesExtension.registry.generateSlotList(crate2);

		StateContainer container = new StateContainer();
		Page.PageBuilder builder = Page.builder();

		builder.setTitle(Text.of(TextSerializers.FORMATTING_CODE.deserialize(crate.getName())));
		builder.setAutoPaging(false);

		for (int i = 0; i < slots.size(); i++) {

			builder.addElement(

					getActionElement(slots.get(i), i, slots.get(i).getDisplayItem().toItemStack())
		
			).setUpdatable(true).setUpdater(this).setInterrupt(() -> {
				if (hasWon() && !MemoryConfig.multiple) {
					return;
				} else
				if (chances <= 0) {
					return;
				}
			});
		}

		Page built = builder.build("test");
		container.setInitialState(built);
		container.launchFor(player);
	}

	/**
	 * 
	 * @return true if player has won
	 */
	private boolean hasWon() {
		if (firstPrize == null || secondPrize == null)
			return false;
		return firstPrize.getDisplayItem().getName() == secondPrize.getDisplayItem().getName()
				&& firstSlot != secondSlot;
	}

	@Override
	public void accept(Page page) {

		if (!hasWon()) {
			int num = 0;
			for (Inventory slot : page.getPageView().slots()) {
				if (winnings.get(num) == null) {

					if (firstPrize == null && secondPrize == null) {
						slot.set(ItemStack.builder().from(slot.peek().get()).itemType(ItemTypes.CHEST)
								.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "???")).build());
					} else {
						if (firstPrize != null) {

							if (firstSlot == num) {

								slot.set(ItemStack.builder().from(slot.peek().get())
										.itemType(firstPrize.getDisplayItem().getItemType())
										.add(Keys.DISPLAY_NAME,transferStringToText(firstPrize.getDisplayItem().getName()))
										.build()

								);

							}
							if (secondPrize != null) {
								if (firstSlot == num) {
									ItemStack.builder().from(slot.peek().get())
											.itemType(firstPrize.getDisplayItem().getItemType())
											.add(Keys.DISPLAY_NAME, transferStringToText(firstPrize.getDisplayItem().getName()))
											.build();
								} else if (secondSlot == num) {

									slot.set(ItemStack.builder().from(slot.peek().get())
											.itemType(secondPrize.getDisplayItem().getItemType())
											.add(Keys.DISPLAY_NAME, transferStringToText(secondPrize.getDisplayItem().getName()))
											.build());

								} else {
									slot.set(ItemStack.builder().from(slot.peek().get()).itemType(CHEST.getType())
											.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "???")).build());
								}
							}

						}

					}

				} else {
					Slot sl = winnings.get(num);
					slot.set(
							sl.getDisplayItem().toItemStack()
							);
				}
				num++;

			}
			if (firstPrize != null && secondPrize != null) {

				LocalDateTime time = LocalDateTime.now();
				LocalDateTime cooldown = LocalDateTime.now().plusSeconds(1);
				while (time.isBefore(cooldown)) {
					time = LocalDateTime.now();
				}
				this.wrongCombo(page);
			}

			if (chances <= 0) {
				player.sendMessage(Text.of(TextColors.RED, "You ran out of chances"));
				player.closeInventory();
			}
		} else {
			int num = 0;
			winnings.put(firstSlot, firstPrize);
			winnings.put(secondSlot, secondPrize);

			for (Inventory slot : page.getPageView().slots()) {

				if (winnings.get(num) != null) {
					Slot item = winnings.get(num);

					slot.set(
							item.getDisplayItem().toItemStack()
							);
				} else {

					slot.set(ItemStack.builder().from(slot.peek().get()).itemType(ItemTypes.CHEST)
							.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "???")).build());

				}

				num++;
			}

			
			player.sendMessage(Text.of(TextColors.GREEN, "Congrats you won!"));
			HuskycratesExtension.registry.execute(firstPrize, crate, player);

			firstPrize = null;
			secondPrize = null;
			
			if(!MemoryConfig.multiple ) {
				player.closeInventory();
			}
		}

	}

	public void wrongCombo(Page page) {

		if (firstPrize != null && secondPrize != null) {

			int num = 0;
			for(Inventory slot: page.getPageView().slots()) {
				
				if(winnings.get(num)!= null) {
					slot.set(
							winnings.get(num).getDisplayItem().toItemStack()
							);
				}else {
					slot.set(ItemStack.builder().from(slot.peek().get()).itemType(ItemTypes.CHEST)
							.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "???")).build());
				}
				

			}
			firstPrize = null;
			secondPrize = null;
			chances--;
			String moves = chances!=1?"move":"moves";
			if(chances > 0) {
				player.sendMessage(Text.of(TextColors.GRAY,String.format("%s %s left", chances,moves)));
			}
			

		}

	}

	private ActionableElement getActionElement(Slot slot, int number, ItemStack displayitem) {

		return new ActionableElement(new RunnableAction(this.container, ActionType.NONE, "", c -> {

			if (firstPrize == null) {
				firstPrize = slots.get(number);
				firstSlot = number;
			} else {

				secondPrize = slots.get(number);
				secondSlot = number;
			}

		}), displayitem);
	}
	
	private Text transferStringToText(String text) {
		return HuskycratesExtension.registry.getTextWithoutColor(text);
	}

}
