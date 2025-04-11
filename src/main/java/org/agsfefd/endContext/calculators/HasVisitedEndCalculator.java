package org.agsfefd.endContext.calculators;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

public class HasVisitedEndCalculator implements ContextCalculator<Player> {

    private static final String CONTEXT_KEY = "has-visited-end";

    @Override
    public void calculate(Player target, ContextConsumer consumer) {
        // Ensure advancements are supported
        if (Bukkit.getAdvancement(org.bukkit.NamespacedKey.minecraft("end/root")) != null) {
            Advancement advancement = Bukkit.getAdvancement(org.bukkit.NamespacedKey.minecraft("end/root"));

            // Check if the player has the advancement
            assert advancement != null;
            if (target.getAdvancementProgress(advancement).isDone()) {
                consumer.accept(CONTEXT_KEY, "true");
            } else {
                consumer.accept(CONTEXT_KEY, "false");
            }
        }
    }

    @Override
    public ContextSet estimatePotentialContexts() {
        return ImmutableContextSet.builder()
                .add(CONTEXT_KEY, "true")
                .add(CONTEXT_KEY, "false")
                .build();
    }
}

