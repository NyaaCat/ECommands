package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.SpigotLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public class ECommandsCommand implements CommandExecutor {
    private final SpigotLoader pluginInstance;

    public ECommandsCommand(SpigotLoader pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
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
}
