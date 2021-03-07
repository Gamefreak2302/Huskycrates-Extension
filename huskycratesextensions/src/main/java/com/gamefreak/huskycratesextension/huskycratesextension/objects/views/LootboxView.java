package com.gamefreak.huskycratesextension.huskycratesextension.objects.views;

import java.security.SecureRandom;
import java.util.function.Consumer;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Color;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.Page;
import com.codehusky.huskyui.states.action.ActionType;
import com.codehusky.huskyui.states.action.runnable.RunnableAction;
import com.codehusky.huskyui.states.element.ActionableElement;
import com.codehusky.huskyui.states.element.Element;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Crate;
import com.gamefreak.huskycratesextension.huskycratesextension.objects.Slot;

public class LootboxView implements Consumer<Page> {

	private Crate crate2;
	private StateContainer container;
	private Slot choice;

	private final ItemStack RedPane = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE)
			.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, " ")).add(Keys.COLOR, Color.RED)
			.add(Keys.DYE_COLOR, DyeColors.RED).build();
	private final ItemStack WhitePane = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE)
			.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, " ")).add(Keys.COLOR, Color.WHITE)
			.add(Keys.DYE_COLOR, DyeColors.WHITE).build();
	private final ItemStack BlackPane = ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE)
			.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, " ")).add(Keys.COLOR, Color.BLACK)
			.add(Keys.DYE_COLOR, DyeColors.BLACK).build();

	public LootboxView(com.codehusky.huskycrates.crate.virtual.Crate crate, Player player) {

		choice = null;

		crate2 = HuskycratesExtension.registry.getCrates().get(crate.getId().toLowerCase());

		SecureRandom rand = new SecureRandom();
		int randomvalue = rand.nextInt(crate.getSlots().size());
		Slot slot = crate2.getSlot(randomvalue);

		
		StateContainer container = new StateContainer();
		Page.PageBuilder builder = Page.builder();

		builder.setTitle(Text.of(TextSerializers.FORMATTING_CODE.deserialize(crate.getName())));
		builder.setAutoPaging(false);

		for (int i = 0; i < 9; i++) {
			builder.putElement(i, new Element(RedPane));
		}

		for (int i = 9; i < 13; i++) {
			builder.putElement(i, new Element(BlackPane));
		}
		for (int i = 14; i < 18; i++) {
			builder.putElement(i, new Element(BlackPane));
		}
		for (int i = 18; i < 27; i++) {
			builder.putElement(i, new Element(WhitePane));
		}
		for (int i = 27; i < 36; i++) {
			builder.putElement(i,
					new Element(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE)
							.add(Keys.DYE_COLOR, DyeColors.SILVER).add(Keys.DISPLAY_NAME, Text.of(" ")).build()

					));
		}
		

		builder.putElement(13, getActionElement(slot, slot.getDisplayItem().toItemStack()));
		
		
		builder.setUpdatable(true).setUpdater(this).setInterrupt(() -> {

			if (choice != null) {

				HuskycratesExtension.registry.execute(slot, crate, player);
				choice = null;
				return;
			}
		});


		Page built = builder.build("test");
		container.setInitialState(built);
		container.launchFor(player);
	}

	/**
	 * 
	 * @return true if player has won
	 */

	@Override
	public void accept(Page page) {
		int num = 0;
		if (choice == null) {
			for (Inventory slot : page.getPageView().slots()) {
				if (num == 13) {
					slot.set(ItemStack.builder().from(slot.peek().get()).itemType(ItemTypes.CHEST)
							.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "???")).build());
				}
				num++;

			}

		} else {
			num = 0;
			for (Inventory slot : page.getPageView().slots()) {
				if (num == 13) {
					slot.set(
							ItemStack.builder().from(slot.peek().get()).itemType(choice.getDisplayItem().getItemType())
							.add(Keys.DISPLAY_NAME, Text.of(TextSerializers.FORMATTING_CODE.deserialize(choice.getDisplayItem().getName())))
							.build()
							);
//							choice.getDisplayItem().toItemStack());
					
				}
				num++;

			}

			page.interrupt();

			// Current issue: 
			// Issue items can be claimed after win (localdatetime?) + auto complete lootbox giving issue
		}

	}

	private ActionableElement getActionElement(Slot slot, ItemStack displayitem) {

		return new ActionableElement(new RunnableAction(this.container, ActionType.NONE, "", c -> {

			choice = slot;

		}), displayitem);
	}

}
