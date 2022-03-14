package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.SpigotLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nonnull;
import java.util.List;

public class ECommandsCommand implements TabExecutor {
    private final SpigotLoader pluginInstance;
    private final String ECOMMAND_COMMAND_PERMISSION_NODE = "ecommands.ecommand";

    public ECommandsCommand(SpigotLoader pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!commandSender.hasPermission(ECOMMAND_COMMAND_PERMISSION_NODE)) {
            commandSender.sendMessage(pluginInstance.getMainLang().commonLang.permissionDenied.produce());
            return true;
        }
        if (args.length == 0) {
            return false;
        }
        if ("reload".equalsIgnoreCase(args[0])) {
            commandSender.sendMessage(pluginInstance.getMainLang().eCommandsCommand.reloadLang.produce());
            if (pluginInstance.loadLanguage())
                commandSender.sendMessage(pluginInstance.getMainLang().eCommandsCommand.done.produce());
            else
                commandSender.sendMessage(pluginInstance.getMainLang().eCommandsCommand.failure.produce());
            commandSender.sendMessage(pluginInstance.getMainLang().eCommandsCommand.reloadECore.produce());
            if (pluginInstance.loadEconomyCore())
                commandSender.sendMessage(pluginInstance.getMainLang().eCommandsCommand.done.produce());
            else
                commandSender.sendMessage(pluginInstance.getMainLang().eCommandsCommand.failure.produce());
            return true;
        } else
            return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (!commandSender.hasPermission(ECOMMAND_COMMAND_PERMISSION_NODE)) {
            return null;
        }
        if (strings.length == 1) {
            return List.of("reload");
        }
        return null;
    }
}
