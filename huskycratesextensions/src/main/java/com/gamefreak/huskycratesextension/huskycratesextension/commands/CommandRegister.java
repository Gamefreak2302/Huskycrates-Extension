package com.gamefreak.huskycratesextension.huskycratesextension.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.codehusky.huskycrates.HuskyCrates;
import com.gamefreak.huskycratesextension.huskycratesextension.HuskycratesExtension;

public class CommandRegister {

	public static void register(HuskycratesExtension plugin) {

		Map<String, String> addRemove = new HashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("add", "add");
				put("remove", "remove");
			}
		};

		CommandSpec mainCommand = CommandSpec.builder()
				.child(CommandSpec.builder().executor(new GiveVirtualKeyCommand())
						.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
								GenericArguments.firstParsing(new CrateArgument(Text.of("crate")),
										new KeyArgument(Text.of("key"))),
								GenericArguments.optional(GenericArguments.integer(Text.of("amount")))

						).description(Text.of(TextColors.GOLD, "Used to give virtual keys to other players"))
						.permission("huskycratesextension.give").build(), "give")

				.child(CommandSpec.builder().executor(new ReloadCommand())
						.description(Text.of(TextColors.GOLD, "Used to reload the config files"))
						.permission("huskycratesextension.admin.reload").build(), "reload")

				.child(CommandSpec.builder().executor(new ManageRightClickCrateCommand())
						.arguments(
								GenericArguments.firstParsing(GenericArguments.choices(Text.of("option"), addRemove)),
								GenericArguments.firstParsing(new CrateArgument(Text.of("crate")),
										new KeyArgument(Text.of("key"))))
						.description(Text.of(TextColors.GOLD, "Used to reload the config files"))
						.permission("huskycratesextension.admin.managerightclick").build(), "rightclick")

				.child(CommandSpec.builder().executor(new ConvertCommand())
						.arguments(
								GenericArguments.optional(GenericArguments.firstParsing(
										new CrateArgument(Text.of("crate")), new KeyArgument(Text.of("key")))),
								GenericArguments.optional(GenericArguments.integer(Text.of("amount"))))
						.description(Text.of(TextColors.GOLD, "Converts physical key to virtual keys"))
						.permission("huskycratesextension.convert").build(), "convert")

				.child(CommandSpec.builder().executor(new TestRewardCommand())
						.arguments(GenericArguments.firstParsing(new CrateArgument(Text.of("crate")),
								new KeyArgument(Text.of("key"))))
						.description(Text.of(TextColors.GOLD, "Converts physical key to virtual keys"))
						.permission("huskycratesextension.admin.test").build(), "test")

				.child(CommandSpec.builder().executor(new TestAllRewardCommand())
						.arguments(GenericArguments.firstParsing(new CrateArgument(Text.of("crate")),
								new KeyArgument(Text.of("key"))))
						.description(Text.of(TextColors.GOLD, "Converts physical key to virtual keys"))
						.permission("huskycratesextension.admin.test.all").build(), "testall")

				.child(CommandSpec.builder().executor(new AddRewardCommand())
						.arguments(
								GenericArguments.firstParsing(new CrateArgument(Text.of("crate")),
								new KeyArgument(Text.of("key"))),
								GenericArguments.onlyOne(GenericArguments.enumValue(Text.of("type"),
										com.codehusky.huskycrates.crate.virtual.Slot.RewardType.class)),
								GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("data"))))
						.description(Text.of(TextColors.GOLD, "Converts physical key to virtual keys"))
						.permission("huskycratesextension.admin.addreward").build(), "addReward")

				.child(CommandSpec.builder().executor(new MemoryCrateCommand())
						.arguments(GenericArguments.firstParsing(new CrateArgument(Text.of("crate")),
								new KeyArgument(Text.of("key"))))
						.description(Text.of(TextColors.GOLD, "Converts physical key to virtual keys"))
						.permission("huskycratesextension.memory").build(), "memory")

				.child(CommandSpec.builder().executor(new GiveKeyNoAutocompletionCommand())
						.arguments(
								GenericArguments.optional(
										GenericArguments.firstParsing(GenericArguments.literal(Text.of("virtual"), "v"),
												GenericArguments.literal(Text.of("virtual"), "virtual"))),
								GenericArguments.firstParsing(new CrateArgument(Text.of("crate")),
										new KeyArgument(Text.of("key"))),
								GenericArguments.onlyOne(GenericArguments.string(Text.of("player"))),
								GenericArguments.optional(GenericArguments.integer(Text.of("amount"))))
						.description(Text.of(TextColors.GOLD, "Use this to give keys without auto completion."))
						.permission("huskycratesextension.admin.forcegive").build(), "forcegive")

				.child(CommandSpec.builder().executor(new AddCommandItem())
						.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))),
								GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("command"))))
						.description(Text.of(TextColors.GOLD, "Add a commanditem to the config and list."))
						.permission("huskycratesextension.admin.addcommanditem").build(), "addcommanditem")

				.child(CommandSpec.builder().executor(new GiveCommandItem())
						.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
								GenericArguments.onlyOne(new commandItemArgument(Text.of("commanditem"))),
								GenericArguments.optional(GenericArguments.integer(Text.of("amount"))))
						.description(Text.of(TextColors.GOLD, "Give command item."))
						.permission("huskycratesextension.admin.givecommanditem").build(), "givecommanditem")

				.child(CommandSpec.builder().executor(new GiveRandomReward())
						.arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
								GenericArguments.onlyOne(new CrateArgument(Text.of("crate"))))
						.description(Text.of(TextColors.GOLD, "Give random slot from crate."))
						.permission("huskycratesextension.admin.random").build(), "random")
				
				.child(CommandSpec.builder().executor(new Balance())
						.arguments(GenericArguments.optional(GenericArguments.user(Text.of("player")))
								)
						.description(Text.of(TextColors.GOLD, "Check balance of other player"))
						.permission("huskycrates.bal.base").build(), "bal","balance")
				// insert more .child here
				.build();


		Sponge.getCommandManager().register(plugin, mainCommand, "hce", "huskycratesextension");

	}

	public static class CrateArgument extends CommandElement {

        public CrateArgument(@Nullable Text key) {
            super(key);
        }

        @Nullable
        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            if(HuskyCrates.registry.isCrate(args.peek())){
                return HuskyCrates.registry.getCrate(args.next());
            }
            throw args.createError(Text.of("\"" +args.next() + "\" is not a valid crate."));
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            try {
                if(HuskyCrates.registry.isCrate(args.peek())){
                    return Collections.singletonList(args.next());
                }else{
                    List<String> poss = new ArrayList<>();
                    for(String crateID : HuskyCrates.registry.getCrates().keySet()){
                        if(crateID.indexOf(args.peek()) == 0){
                            poss.add(crateID);
                        }
                    }
                    args.next();
                    return poss;
                }
            } catch (ArgumentParseException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }
    }


    public static class KeyArgument extends CommandElement {

        public KeyArgument(@Nullable Text key) {
            super(key);
        }

        @Nullable
        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            if(HuskyCrates.registry.isKey(args.peek())){
                return HuskyCrates.registry.getKey(args.next());
            }
            throw args.createError(Text.of("\"" +args.next() + "\" is not a valid key."));
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            try {
                if(HuskyCrates.registry.isKey(args.peek())){
                    return Collections.singletonList(args.next());
                }else{
                    List<String> poss = new ArrayList<>();
                    for(String crateID : HuskyCrates.registry.getKeys().keySet()){
                        if(crateID.indexOf(args.peek()) == 0){
                            poss.add(crateID);
                        }
                    }
                    args.next();
                    return poss;
                }
            } catch (ArgumentParseException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }
    }

	public static class commandItemArgument extends CommandElement {

		protected commandItemArgument(Text key) {
			super(key);
		}

		@Nullable
		@Override
		protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
			if (HuskycratesExtension.registry.getCommandItem(args.peek()) != null) {
				return HuskycratesExtension.registry.getCommandItem(args.next());
			}
			throw args.createError(Text.of("\"" + args.next() + "\" is not a valid commandItem."));
		}

		@Override
		public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {

			try {

				List<String> poss = new ArrayList<String>();
				for (String CommandID : HuskycratesExtension.registry.getCommandItems().keySet()) {
					if (CommandID.indexOf(args.peek()) == 0) {

						poss.add(CommandID);
					}

				}
				args.next();
				return poss;

			} catch (ArgumentParseException e) {
				e.printStackTrace();
			}
			return Collections.emptyList();
		}
	}
}
