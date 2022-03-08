package cat.nyaa.ecommands.utils;

import cat.nyaa.ecore.EconomyCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class Vault {
    public final boolean isSystemVault;
    public final EconomyCore economyCore;
    public final UUID vaultUUID;
    public final String name;
    public final OfflinePlayer player;

    private Vault(OfflinePlayer player, boolean isSystemVault, EconomyCore economyCore) {
        this.isSystemVault = isSystemVault;
        if (!isSystemVault) {
            assert player != null;
            if (!player.hasPlayedBefore()) {
                throw new IllegalArgumentException("Player not found");
            }
            this.player = player;
            this.vaultUUID = player.getUniqueId();
            this.name = player.getName();
        } else {
            this.player = null;
            this.vaultUUID = null;
            this.name = economyCore.systemVaultName();
        }

        this.economyCore = economyCore;
    }

    @SuppressWarnings("deprecation")
    public static Vault of(String name, EconomyCore economyCore) {
        if (name.equalsIgnoreCase("$system")) {
            return new Vault(null, true, economyCore);
        } else {
            var player = Objects.requireNonNullElse(Bukkit.getPlayerExact(name), Bukkit.getOfflinePlayer(name));
            return new Vault(player, false, economyCore);
        }
    }

    public static Vault of(UUID vaultUUID, EconomyCore economyCore) {
        return new Vault(Bukkit.getOfflinePlayer(vaultUUID), false, economyCore);
    }

    public boolean add(double amount) {
        if (isSystemVault) {
            return economyCore.depositSystemVault(amount);
        } else {
            return economyCore.depositPlayer(vaultUUID, amount);
        }
    }

    public boolean remove(double amount) {
        if (isSystemVault) {
            return economyCore.withdrawSystemVault(amount);
        } else {
            return economyCore.withdrawPlayer(vaultUUID, amount);
        }
    }

    public boolean set(double amount) {
        if (isSystemVault) {
            return economyCore.setSystemBalance(amount);
        } else {
            return economyCore.setPlayerBalance(vaultUUID, amount);
        }
    }

    public double balance() {
        if (isSystemVault) {
            return economyCore.getSystemBalance();
        } else {
            return economyCore.getPlayerBalance(vaultUUID);
        }
    }
}
