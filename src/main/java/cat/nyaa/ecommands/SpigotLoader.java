package cat.nyaa.ecommands;

import cat.nyaa.ecommands.commands.ECommandsCommand;
import cat.nyaa.ecommands.commands.EcoCommand;
import cat.nyaa.ecommands.commands.PayCommand;
import cat.nyaa.ecommands.lang.MainLang;
import cat.nyaa.ecore.EconomyCore;
import land.melon.lab.simplelanguageloader.SimpleLanguageLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SpigotLoader extends JavaPlugin {
    private final SimpleLanguageLoader languageLoader = new SimpleLanguageLoader();
    private final File languageFile = new File(this.getDataFolder(), "language.json");
    private MainLang mainLang;
    private EconomyCore economyCore;

    public MainLang getMainLang() {
        return mainLang;
    }

    public EconomyCore getEconomyCore() {
        return economyCore;
    }

    @Override
    public void onEnable() {
        IGNORE_RESULT(this.getDataFolder().mkdir());
        if (!loadLanguage()) {
            getLogger().severe("Failed to load language file!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if(!loadEconomyCore()){
            getLogger().severe("Failed to load EconomyCore!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Objects.requireNonNull(this.getServer().getPluginCommand("pay")).setExecutor(new PayCommand(this));
        Objects.requireNonNull(this.getServer().getPluginCommand("eco")).setExecutor(new EcoCommand(this));
        Objects.requireNonNull(this.getServer().getPluginCommand("ecommands")).setExecutor(new ECommandsCommand(this));
    }

    public boolean loadLanguage() {
        try {
            mainLang = languageLoader.loadLanguageFile(languageFile, MainLang.class);
            if (mainLang == null) {
                mainLang = new MainLang();
            }
            languageLoader.saveLanguageFile(languageFile, mainLang);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadEconomyCore() {
        if (this.getServer().getPluginManager().getPlugin("ECore") == null)
            return false;
        var economyProvider = this.getServer().getServicesManager().getRegistration(EconomyCore.class);
        if (economyProvider == null)
            return false;
        economyCore = economyProvider.getProvider();
        return true;
    }

    private void IGNORE_RESULT(Object o){
        //ignore
    }
}
