package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.codehusky.huskycrates.crate.virtual.Crate;
import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.Page;
import com.codehusky.huskyui.states.action.ActionType;
import com.codehusky.huskyui.states.action.runnable.RunnableAction;
import com.codehusky.huskyui.states.element.ActionableElement;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;
import com.gamefreak.huskycratesextension.huskycratesextension.config.Messages;

public class TestRewardCommand implements CommandExecutor {

	private com.gamefreak.huskycratesextension.huskycratesextension.objects.Crate crate2;
	private Crate crate;
	private Player player;
	private int slotNr;

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Optional<Crate> cr = args.getOne("crate");
		crate2 = null;
		player = null;
		if (src instanceof Player) {
			player = (Player) src;
			if (!cr.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "Crate can not be empty"));
				return CommandResult.success();
			} else {
				crate = cr.get();

			}

			if (crate != null) {

				crate2 = HuskycratesExtension.registry.getCrates().get(crate.getId().toLowerCase());

				if (crate2 == null) {
					src.sendMessage(Text.of(TextColors.RED,
							"Can not test this crate, please report this issue to the developer"));
					return CommandResult.success();
				}

				if (HuskycratesExtension.registry.getUntestableCrates().contains(crate2)) {
					player.sendMessage(Text.of(TextColors.RED,
							Messages.replaceText(Messages.crateUntestable, crate.getId(), -1, null, player)));
					return CommandResult.success();
				}
				StateContainer container = new StateContainer();

				Page.PageBuilder builder = Page.builder();

				builder.setTitle(Text.of(TextSerializers.FORMATTING_CODE.deserialize(crate.getName())));
				builder.setAutoPaging(true);

				for (int i = 0; i < crate2.getSlots().size(); i++) {
					ItemStack orig = crate2.getSlot(i).getDisplayItem().toItemStack();
					slotNr = i;
					com.gamefreak.huskycratesextension.huskycratesextension.objects.Slot slot = crate2.getSlots()
							.get(i);
					com.codehusky.huskycrates.crate.virtual.Slot slot2 = crate.getSlot(i);
					builder.addElement(

							new ActionableElement(new RunnableAction(container, ActionType.NONE, "", c -> {

								HuskycratesExtension.registry.execute(slot, crate, player);
								String log = String.format("%s tested slot %s from the %s crate", player.getName(),
										"slotNr: " + slotNr + " name: " + slot.getDisplayItem().getName(),
										crate.getId());
								HuskycratesExtension.instance.logger.info(log);
								HuskycratesExtension.registry.SendPlayersNoteOfTest("huskycratesextension.message.test",
										log);
								ItemStack.builder().itemType(slot2.getDisplayItem().toItemStack().getType())
										.add(Keys.DISPLAY_NAME, HuskycratesExtension.registry
												.getTextWithoutColor(slot2.getDisplayItem().getName()))
										.build();
							}), orig));
				}

				Page built = builder.build("test");

				container.setInitialState(built);
				container.launchFor(player);

			} else {
				src.sendMessage(Text.of("Command must be executed as a player!"));
				return CommandResult.success();

			}
		}

		return CommandResult.success();

	}

}