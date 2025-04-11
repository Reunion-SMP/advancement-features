package org.agsfefd.endContext;

import org.agsfefd.endContext.calculators.HasVisitedEndCalculator;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;

public final class EndContext extends JavaPlugin {
    private final List<ContextCalculator<Player>> registeredCalculators = new ArrayList<>();
    private ContextManager contextManager;

    @Override
    public void onEnable() {
        LuckPerms luckPerms = getServer().getServicesManager().load(LuckPerms.class);
        if (luckPerms == null) {
            throw new IllegalStateException("LuckPerms API not loaded.");
        }
        // Plugin startup logic
        getLogger().info("Registering custom context calculators...");
        this.contextManager = luckPerms.getContextManager();
        setup();
    }

    @Override
    public void onDisable() {
        unregisterAll();
    }

    private void setup() {
        register("has-visited-end", null, HasVisitedEndCalculator::new);
    }

    private void register(String option, String requiredPlugin, Supplier<ContextCalculator<Player>> calculatorSupplier){
        ContextCalculator<Player> calculator = calculatorSupplier.get();
        this.contextManager.registerCalculator(calculator);
        this.registeredCalculators.add(calculator);
    }

    private void unregisterAll() {
        this.registeredCalculators.forEach(c -> this.contextManager.unregisterCalculator(c));
        this.registeredCalculators.clear();
    }
}
