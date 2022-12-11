package me.kodysimpson.conversational;

import me.kodysimpson.conversational.commands.SummonNPCCommand;
import me.kodysimpson.conversational.listeners.NPCListeners;
import me.kodysimpson.conversational.utils.ConvoTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.logging.Level;

public final class Conversational extends JavaPlugin implements Listener {

    private BukkitAudiences adventure;

    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {

        this.adventure = BukkitAudiences.create(this);

        if(getServer().getPluginManager().getPlugin("Citizens") == null || !getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ConvoTrait.class));

        // Plugin startup logic
        getCommand("summonnpc").setExecutor(new SummonNPCCommand());
        getServer().getPluginManager().registerEvents(new NPCListeners(this), this);
    }
}
