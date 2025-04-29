package org.agsfefd.endContext;

import org.agsfefd.endContext.calculators.HasVisitedEndCalculator;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;

public final class EndContext extends JavaPlugin implements Listener {
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
        getServer().getPluginManager().registerEvents(this, this);

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


    // === Advancement Listeners ===

    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();

        // Check if player just completed "Free the End"
        if (advancement.getKey().equals(NamespacedKey.minecraft("end/kill_dragon"))) {
            giveDragonEggAdvancement(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Advancement freeTheEnd = Bukkit.getAdvancement(NamespacedKey.minecraft("end/kill_dragon"));
        Advancement dragonEgg = Bukkit.getAdvancement(NamespacedKey.minecraft("end/dragon_egg"));

        if (freeTheEnd != null && dragonEgg != null) {
            if (hasAdvancement(player, freeTheEnd) && !hasAdvancement(player, dragonEgg)) {
                giveDragonEggAdvancement(player);
            }
        }
    }

    private boolean hasAdvancement(Player player, Advancement advancement) {
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        return progress.isDone();
    }

    private void giveDragonEggAdvancement(Player player) {
        Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft("end/dragon_egg"));
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        for (String criteria : progress.getRemainingCriteria()) {
            progress.awardCriteria(criteria);
        }
    }
}