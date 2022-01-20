package cat.nyaa.ecommands;

import cat.nyaa.ecommands.commands.PayCommand;
import cat.nyaa.ecommands.utils.Lang;
import cat.nyaa.ecore.EconomyCore;
import land.melon.lab.simplelanguageloader.SimpleLanguageLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SpigotLoader extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getDataFolder().mkdir();
        var languageLoader = new SimpleLanguageLoader();
        var languageFile = new File(this.getDataFolder(), "language.json");
        Lang language = null;
        try {
            language = languageLoader.loadLanguageFile(languageFile, Lang.class);
            if (language == null) {
                language = new Lang();
            }
            languageLoader.saveLanguageFile(languageFile, language);
        } catch (IOException e) {
            e.printStackTrace();
        }

        var economyProvider = this.getServer().getServicesManager().getRegistration(EconomyCore.class);
        if (economyProvider == null) {
            this.getLogger().severe("ECore not found! Unable to load ECommands!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        var economy = economyProvider.getProvider();

        Objects.requireNonNull(this.getServer().getPluginCommand("pay")).setExecutor(new PayCommand(language, this, economy));
//        Objects.requireNonNull(this.getServer().getPluginCommand("eco")).setExecutor(new EcoCommand(language, this, economy));
    }
}
