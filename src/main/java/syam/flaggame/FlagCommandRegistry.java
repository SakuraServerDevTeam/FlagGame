/*
 * Copyright (C) 2017 SakuraServerDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package syam.flaggame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import jp.llv.flaggame.api.FlagGameAPI;
import jp.llv.flaggame.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import syam.flaggame.command.BaseCommand;
import syam.flaggame.command.ConfirmCommand;
import syam.flaggame.command.HelpCommand;
import syam.flaggame.command.ReloadCommand;
import syam.flaggame.command.TpCommand;
import syam.flaggame.command.area.AreaDashboardCommand;
import syam.flaggame.command.area.AreaDeleteCommand;
import syam.flaggame.command.area.AreaInitCommand;
import syam.flaggame.command.area.AreaListCommand;
import syam.flaggame.command.area.AreaSelectCommand;
import syam.flaggame.command.area.AreaSetCommand;
import syam.flaggame.command.area.data.AreaDataDeleteCommand;
import syam.flaggame.command.area.data.AreaDataListCommand;
import syam.flaggame.command.area.data.AreaDataLoadCommand;
import syam.flaggame.command.area.data.AreaDataSaveCommand;
import syam.flaggame.command.area.data.AreaDataTimingCommand;
import syam.flaggame.command.area.message.AreaMessageAddCommand;
import syam.flaggame.command.area.message.AreaMessageDeleteCommand;
import syam.flaggame.command.area.message.AreaMessageListCommand;
import syam.flaggame.command.area.message.AreaMessageTimingCommand;
import syam.flaggame.command.area.permission.AreaPermissionDashboardCommand;
import syam.flaggame.command.area.permission.AreaPermissionListCommand;
import syam.flaggame.command.area.permission.AreaPermissionSetCommand;
import syam.flaggame.command.area.permission.AreaPermissionTestCommand;
import syam.flaggame.command.debug.AccountStateCommand;
import syam.flaggame.command.game.GameCloseCommand;
import syam.flaggame.command.game.GameJoinCommand;
import syam.flaggame.command.game.GameLeaveCommand;
import syam.flaggame.command.game.GameListCommand;
import syam.flaggame.command.game.GameReadyCommand;
import syam.flaggame.command.game.GameStartCommand;
import syam.flaggame.command.game.GameWatchCommand;
import syam.flaggame.command.kit.KitCreateCommand;
import syam.flaggame.command.kit.KitDeleteCommand;
import syam.flaggame.command.kit.KitGetCommand;
import syam.flaggame.command.objective.ObjectiveDeleteCommand;
import syam.flaggame.command.objective.ObjectiveListCommand;
import syam.flaggame.command.objective.ObjectiveSetCommand;
import syam.flaggame.command.player.PlayerExpCommand;
import syam.flaggame.command.player.PlayerInfoCommand;
import syam.flaggame.command.player.PlayerStatsCommand;
import syam.flaggame.command.player.PlayerVibeCommand;
import syam.flaggame.command.player.bit.BitAddCommand;
import syam.flaggame.command.player.bit.BitBalanceCommand;
import syam.flaggame.command.player.bit.BitPayCommand;
import syam.flaggame.command.player.nick.NickLockCommand;
import syam.flaggame.command.player.nick.NickSetCommand;
import syam.flaggame.command.player.nick.NickUnlockCommand;
import syam.flaggame.command.stage.StageCreateCommand;
import syam.flaggame.command.stage.StageDashboardCommand;
import syam.flaggame.command.stage.StageDeleteCommand;
import syam.flaggame.command.stage.StageInfoCommand;
import syam.flaggame.command.stage.StageListCommand;
import syam.flaggame.command.stage.StageRateCommand;
import syam.flaggame.command.stage.StageSaveCommand;
import syam.flaggame.command.stage.StageSelectCommand;
import syam.flaggame.command.stage.StageSetCommand;
import syam.flaggame.command.stage.StageStatsCommand;
import syam.flaggame.command.stage.StageTagAddCommand;
import syam.flaggame.command.stage.StageTagRemoveCommand;
import syam.flaggame.util.Actions;

/**
 *
 * @author SakuraServerDev
 */
public enum FlagCommandRegistry implements TabExecutor {

    AREA_DATA(
            "<- manages area data",
            names("data", "d"),
            AreaDataDeleteCommand::new,
            AreaDataListCommand::new,
            AreaDataLoadCommand::new,
            AreaDataSaveCommand::new,
            AreaDataTimingCommand::new
    ),
    AREA_MESSAGE(
            "<- manage area messages",
            names("message", "msg", "m"),
            AreaMessageAddCommand::new,
            AreaMessageDeleteCommand::new,
            AreaMessageListCommand::new,
            AreaMessageTimingCommand::new
    ),
    AREA_PERMISSION(
            "<- manage area permissions",
            names("permission", "perm", "p"),
            AreaPermissionDashboardCommand::new,
            AreaPermissionListCommand::new,
            AreaPermissionSetCommand::new,
            AreaPermissionTestCommand::new
    ),
    AREA(
            "<- manage areas",
            names("area", "a"),
            subcategories(AREA_DATA, AREA_MESSAGE, AREA_PERMISSION),
            AreaDashboardCommand::new,
            AreaDeleteCommand::new,
            AreaInitCommand::new,
            AreaListCommand::new,
            AreaSelectCommand::new,
            AreaSetCommand::new
    ),
    GAME(
            "<- manage games",
            names("game", "g"),
            GameCloseCommand::new,
            GameJoinCommand::new,
            GameLeaveCommand::new,
            GameListCommand::new,
            GameReadyCommand::new,
            GameStartCommand::new,
            GameWatchCommand::new
    ),
    OBJECTIVE(
            "<- manage objectives",
            names("objective", "obj", "o"),
            ObjectiveDeleteCommand::new,
            ObjectiveListCommand::new,
            ObjectiveSetCommand::new
    ),
    BIT(
            "<- manage bits(in-game currency)",
            names("bit", "bits"),
            BitBalanceCommand::new,
            BitPayCommand::new,
            BitAddCommand::new
    ),
    NICK(
            "<- manage nicks",
            names("nick"),
            NickSetCommand::new,
            NickLockCommand::new,
            NickUnlockCommand::new
    ),
    TROPHIE(
            "<- manage trophies",
            names("trophie")
    ),
    PLAYER(
            "<- manage players",
            names("player", "p"),
            subcategories(NICK, TROPHIE, BIT),
            PlayerExpCommand::new,
            PlayerInfoCommand::new,
            PlayerStatsCommand::new,
            PlayerVibeCommand::new
    ),
    STAGE(
            "<- manage stages",
            names("stage", "s"),
            StageCreateCommand::new,
            StageDashboardCommand::new,
            StageDeleteCommand::new,
            StageInfoCommand::new,
            StageListCommand::new,
            StageRateCommand::new,
            StageSaveCommand::new,
            StageSelectCommand::new,
            StageSetCommand::new,
            StageStatsCommand::new,
            StageTagAddCommand::new,
            StageTagRemoveCommand::new
    ),
    KIT(
            "<- manage kits",
            names("kit", "k"),
            KitCreateCommand::new,
            KitGetCommand::new,
            KitDeleteCommand::new
    ),
    DEBUG(
            "<- commands for developers",
            names("debug"),
            AccountStateCommand::new
    ),
    GENERAL(
            "<- general commands",
            names("flag", "fg", "f"),
            subcategories(
                    AREA,
                    GAME,
                    OBJECTIVE,
                    PLAYER,
                    STAGE,
                    KIT,
                    DEBUG
            ),
            HelpCommand::new,
            ConfirmCommand::new,
            ReloadCommand::new,
            TpCommand::new
    ),
    ROOT(
            null,
            null,
            subcategories(
                    GENERAL,
                    AREA,
                    GAME,
                    OBJECTIVE,
                    PLAYER,
                    STAGE,
                    KIT
            )
    );

    private static final String PLUGIN_PREFIX = "flaggame:";
    private static HelpCommand help;

    private final String usage;
    private final String names[];
    private final FlagCommandRegistry[] subcategories;
    private final Function<FlagGameAPI, ? extends BaseCommand>[] constructors;
    private final List<BaseCommand> commands = new ArrayList<>();

    private FlagCommandRegistry(String usage, String[] names, FlagCommandRegistry[] subcategories, Function<FlagGameAPI, ? extends BaseCommand>... commands) {
        this.usage = usage;
        this.names = names;
        this.subcategories = subcategories;
        this.constructors = commands;
    }

    private FlagCommandRegistry(String usage, String[] names, FlagCommandRegistry subcategory, Function<FlagGameAPI, ? extends BaseCommand>... commands) {
        this(usage, names, subcategories(subcategory), commands);
    }

    private FlagCommandRegistry(String usage, String[] names, Function<FlagGameAPI, ? extends BaseCommand>... commands) {
        this(usage, names, subcategories(), commands);
    }

    public void initialize(FlagGameAPI api) {
        for (FlagCommandRegistry subcategory : subcategories) {
            subcategory.initialize(api);
        }
        if (!this.commands.isEmpty()) {
            return;
        }
        for (Function<FlagGameAPI, ? extends BaseCommand> constructor : constructors) {
            BaseCommand command = constructor.apply(api);
            if (command instanceof HelpCommand) {
                help = (HelpCommand) command;
            }
            this.commands.add(command);
        }
    }

    public FlagCommandRegistry[] getSubcategories() {
        return subcategories.clone();
    }

    public List<BaseCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public FlagCommandRegistry getSubCategory(String name) {
        int index = name.indexOf(" ");
        if (index < 0) { // in case of "flag"
            for (FlagCommandRegistry category : subcategories) {
                for (String alias : category.names) {
                    if (name.equalsIgnoreCase(alias)) {
                        return category;
                    }
                }
            }
            return null;
        } else if (index + 1 == name.length()) { // in case of "flag "
            return getSubCategory(name.substring(0, index));
        } else { // in case of "flag stage"
            return getSubCategory(name.substring(0, index))
                    .getSubCategory(name.substring(index + 1, name.length()));
        }
    }

    private boolean startsWith(String name, String input) {
        return name.equalsIgnoreCase(input)
                || (input.toLowerCase().startsWith(name) && input.charAt(name.length()) == ' ');
    }

    private ResolvedCommand getCommand(List<String> input) {
        String flatInput = String.join(" ", input).toLowerCase();
        for (BaseCommand command : commands) {
            if (startsWith(command.getName(), flatInput)) {
                return new ResolvedCommand(command, command.getName());
            }
            for (String alias : command.getAliases()) {
                if (startsWith(alias, flatInput)) {
                    return new ResolvedCommand(command, alias);
                }
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> argList = new ArrayList<>();
        String alias = removePluginPrefix(label);
        argList.add(alias);
        argList.addAll(Arrays.asList(args));
        this.execute(sender, null, argList);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> argList = new ArrayList<>();
        String alias = removePluginPrefix(label);
        argList.add(alias);
        argList.addAll(Arrays.asList(args));
        return this.complete(sender, null, argList);
    }

    public void execute(CommandSender sender, String label, List<String> args) {
        if (args.isEmpty()) {
            sendHelpMessage(sender, label); // can not reach any command
            return;
        }
        ResolvedCommand command = getCommand(args);
        if (command != null) {
            List<String> newArgs = args.subList(StringUtil.countChar(command.label, ' ') + 1, args.size());
            command.command.run(sender, newArgs.toArray(new String[newArgs.size()]), label, command.label);
            return;
        }
        String subcategoryName = args.remove(0);
        FlagCommandRegistry subcategory = getSubCategory(subcategoryName);
        if (subcategory != null) {
            String newLabel = label == null ? subcategoryName : label + ' ' + subcategoryName;
            subcategory.execute(sender, newLabel, args);
            return;
        }
        sendHelpMessage(sender, label); // still old label; new one can be wrong
    }

    public List<String> complete(CommandSender sender, String label, List<String> args) {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("No arguments present");
        }
        if (args.size() == 1) {
            List<String> suggestions = new ArrayList<>();
            for (BaseCommand command : commands) {
                suggestions.add(command.getName());
                suggestions.addAll(Arrays.asList(command.getAliases()));
            }
            for (FlagCommandRegistry subcategory : subcategories) {
                suggestions.addAll(Arrays.asList(subcategory.names));
            }
            return suggestions.stream()
                    .filter(s -> s.startsWith(args.get(0).toLowerCase()))
                    .collect(Collectors.toList());
        }
        ResolvedCommand command = getCommand(args);
        if (command != null) {
            List<String> newArgs = args.subList(StringUtil.countChar(command.label, ' ') + 1, args.size());
            return command.command.complete(sender, newArgs.toArray(new String[newArgs.size()]));
        }
        String subcategoryName = args.remove(0);
        FlagCommandRegistry subcategory = getSubCategory(subcategoryName);
        if (subcategory != null) {
            String newLabel = label == null ? subcategoryName : label + ' ' + subcategoryName;
            return subcategory.complete(sender, newLabel, args);
        }
        return null;
    }

    public void sendUsage(CommandSender sendTo, String label) {
        Actions.message(sendTo, "&7/" + label + "&c " + names[0] + "...&7 " + usage);
    }

    private static void sendHelpMessage(CommandSender sender, String label) {
        help.execute(Collections.emptyList(), label, sender, null);
    }

    private static String[] names(String... names) {
        return names;
    }

    private static FlagCommandRegistry[] subcategories(FlagCommandRegistry... categories) {
        return categories;
    }

    private static String removePluginPrefix(String label) {
        if (label.toLowerCase().startsWith(PLUGIN_PREFIX)) {
            return label.substring(PLUGIN_PREFIX.length(), label.length());
        } else {
            return label;
        }
    }

    public static void initializeAll(FlagGameAPI api) {
        ROOT.initialize(api);
    }

    public static FlagCommandRegistry getCategory(String name) {
        return ROOT.getSubCategory(name);
    }

    private static class ResolvedCommand {

        final BaseCommand command;
        final String label;

        public ResolvedCommand(BaseCommand command, String label) {
            this.command = command;
            this.label = label;
        }
    }

}
