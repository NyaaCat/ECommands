package cat.nyaa.ecommands.utils;

import cat.nyaa.ecore.EconomyCore;
import cat.nyaa.ecore.TransactionResult;

import java.util.List;
import java.util.UUID;

public record Payment(UUID from, List<UUID> to, double amount,
                      EconomyCore economyCoreInstance) {
    public TransactionResult confirm() {
        return economyCoreInstance.playerTransferToMultiple(from, to, amount);
    }
}
