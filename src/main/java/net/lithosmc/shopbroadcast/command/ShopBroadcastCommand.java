package net.lithosmc.shopbroadcast.command;

import net.lithosmc.shopbroadcast.ShopBroadcast;
import net.lithosmc.shopbroadcast.messages.SBMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ShopBroadcastCommand implements TabExecutor {

    private final String PERM_PREFIX = "shopbroadcast.";

    private final SBCmdLogic logic;

    public ShopBroadcastCommand(ShopBroadcast plugin) {
        this.logic = new SBCmdLogic(plugin);
    }

    // Utility
    private boolean hasSubPerm(CommandSender sender, String perm) {
        if (!sender.hasPermission(PERM_PREFIX + perm)) {
            SBMessages.send(sender, "no-perm");
            return false;
        }
        return true;
    }

    private boolean forcePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            SBMessages.send(sender, "player-only");
            return false;
        }

        return true;
    }

    private UUID getTarget(CommandSender sender, String targetName) {
        var targetPlayer = Bukkit.getOfflinePlayerIfCached(targetName);

        if (targetPlayer == null) {
            SBMessages.send(sender, "invalid-player");
            return null;
        }

        return targetPlayer.getUniqueId();
    }

    private String collect(String[] array, int start) {
        if (array == null || start < 0 || start > array.length)
            return "";

        StringJoiner joiner = new StringJoiner(" ");
        for (int i = start; i < array.length; i++) {
            joiner.add(array[i]);
        }

        return joiner.toString();
    }

    private Optional<Integer> getAdIndex(String[] args, int meetsLength, int parseIndex) {
        if (args.length > parseIndex && args.length >= meetsLength) {
            try {
                var adIndex = Integer.parseInt(args[parseIndex]);
                return Optional.of(adIndex);
            } catch (NumberFormatException ignore) {
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            help(sender, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "buy" -> {
                if (!hasSubPerm(sender, "buy")
                        || !forcePlayer(sender)) {
                    break;
                }

                if (args.length == 1) {
                    sendSubUsage(sender, label, "buy", "buy");
                    break;
                }

                String msg = collect(args, 1);
                logic.buy((Player) sender, msg);
            }
            case "adminbuy" -> {
                if (hasSubPerm(sender, "buy.admin")) {
                    if (args.length < 3) {
                        sendSubUsage(sender, label, "adminbuy", "admin-buy");
                    } else {
                        var target = getTarget(sender, args[1]);
                        if (target != null) {
                            var msg = collect(args, 2);
                            logic.buyAdmin(sender, target, msg);
                        }
                    }
                }
            }
            case "edit" -> {
                if (hasSubPerm(sender, "edit") && forcePlayer(sender)) {
                    var senderUUID = ((Player) sender).getUniqueId();
                    if (args.length == 1) {
                        sendSubUsage(sender, label, "edit", "edit");
                        break;
                    }

                    var adIndexOpt = getAdIndex(args, 3, 1);
                    String msg = collect(args, adIndexOpt.isPresent() ? 2 : 1);
                    logic.edit(sender, senderUUID, adIndexOpt.orElse(1), msg);
                }
            }
            // edit <target> [ad num] <message>
            case "adminedit" -> {
                if (!hasSubPerm(sender, "edit.admin")) {
                    break;
                }

                if (args.length < 3) {
                    // Specify message
                    sendSubUsage(sender, label, "adminedit", "admin-edit");
                    break;
                }

                var target = getTarget(sender, args[1]);
                if (target == null)
                    break;

                var adIndexOpt = getAdIndex(args, 4, 2);

                var msg = collect(args, adIndexOpt.isPresent() ? 3 : 2);
                logic.edit(sender, target, adIndexOpt.orElse(1), msg);
            }
            case "delete" -> {
                if (!hasSubPerm(sender, "delete")
                        || !forcePlayer(sender)) {
                    break;
                }
                var senderUUID = ((Player) sender).getUniqueId();

                var adIndexOpt = getAdIndex(args, 2, 0);
                logic.delete(sender, senderUUID, adIndexOpt.orElse(1));
            }
            // admindelete <target> [ad num]
            case "admindelete" -> {
                if (!hasSubPerm(sender, "delete.admin"))
                    break;

                if (args.length < 2) {
                    sendSubUsage(sender, label, "admindelete", "admin-delete");
                    break;
                }

                var target = getTarget(sender, args[1]);
                if (target == null)
                    break;

                var adIndexOpt = getAdIndex(args, 3, 2);
                logic.delete(sender, target, adIndexOpt.orElse(1));
            }
            // set [ad index] <type> [type data]
            case "set" -> {
                if (!hasSubPerm(sender, "set")
                    || !forcePlayer(sender))
                    break;

                var senderUUID = ((Player) sender).getUniqueId();

                if (args.length < 2) {
                    sendSubUsage(sender, label, "set", "set");
                    break;
                }

                var adOptIndex = getAdIndex(args, 3, 1);
                final var typeStr = adOptIndex.isPresent() ? args[2] : args[1];

                final String dataStr;
                if (adOptIndex.isPresent() && args.length == 4)
                    dataStr = args[3];
                else if (adOptIndex.isEmpty() && args.length == 3)
                    dataStr = args[2];
                else
                    dataStr = null;

                logic.set(sender, senderUUID, adOptIndex.orElse(1), typeStr, dataStr);
            }
            // list [player]
            case "list" -> {
                if (!hasSubPerm(sender, "list"))
                    break;

                UUID target = null;
                if (args.length == 2) {
                    target = getTarget(sender, args[1]);
                    if (target == null)
                        break;
                }

                logic.list(sender, target);
            }
            case "reload" -> {
                if (!hasSubPerm(sender, "reload"))
                    break;

                logic.reload(sender);
            }
            case "help" -> {
                if (!hasSubPerm(sender, "help"))
                    break;

                help(sender, label);
            }
            default -> SBMessages.send(sender, "cmd-invalid", label);
        }

        return true;
    }

    private void sendSubUsage(CommandSender sender, String lbl, String sub, String key) {
        var subArgs = SBMessages.of("cmd-" + key + "-args");
        SBMessages.send(sender, "cmd-usage", lbl, sub + " " + subArgs);
    }

    private String getSubHelp(String lbl, String key, String subCmd) {
        var subArgs = SBMessages.of("cmd-" + key + "-args");
        var desc = SBMessages.of("cmd-" + key + "-desc");
        return SBMessages.of("cmd-help-list", lbl, subCmd, subArgs, desc);
    }

    private void help(CommandSender sender, String label) {
        var msgJoiner = new StringJoiner("\n");
        Predicate<String> hasPerm = (perm) -> sender.hasPermission(PERM_PREFIX + perm);
        BiConsumer<String, String> addSub = (key, subCmd) -> msgJoiner.add(getSubHelp(label, key, subCmd));

        if (hasPerm.test("buy"))
            addSub.accept("buy", "buy");

        if (hasPerm.test("buy.other"))
            addSub.accept("admin-buy", "adminbuy");

        if (hasPerm.test("edit"))
            addSub.accept("edit", "edit");

        if (hasPerm.test("edit.admin"))
            addSub.accept("admin-edit", "adminedit");

        if (hasPerm.test("set"))
            addSub.accept("set", "set");

        if (hasPerm.test("delete"))
            addSub.accept("delete", "delete");

        if (hasPerm.test("delete.admin"))
            addSub.accept("admin-delete", "admindelete");

        if (hasPerm.test("list"))
            addSub.accept("list", "list");

        if (hasPerm.test("reload"))
            addSub.accept("reload", "reload");

        SBMessages.send(sender, "cmd-help", msgJoiner.toString());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length != 1)
            return null;

        var subCompletes = new ArrayList<String>(5);
        BiConsumer<String, String> addCompletion = (sub, perm) -> {
            if (sender.hasPermission(PERM_PREFIX + perm))
                subCompletes.add(sub);
        };

        addCompletion.accept("buy", "buy");
        addCompletion.accept("adminbuy", "buy.admin");
        addCompletion.accept("edit", "edit");
        addCompletion.accept("adminedit", "edit.admin");
        addCompletion.accept("delete", "delete");
        addCompletion.accept("admindelete", "delete.delete");
        addCompletion.accept("list", "list");
        addCompletion.accept("help", "help");
        addCompletion.accept("set", "set");
        addCompletion.accept("reload", "reload");

        var zeroArg = args[0].trim().toLowerCase();

        if (zeroArg.isEmpty())
            return subCompletes;

        var matchedCompletes = new ArrayList<String>(5);
        for (String subComplete : subCompletes) {
            if (subComplete.startsWith(zeroArg))
                matchedCompletes.add(subComplete);
        }

        return matchedCompletes;
    }
}

