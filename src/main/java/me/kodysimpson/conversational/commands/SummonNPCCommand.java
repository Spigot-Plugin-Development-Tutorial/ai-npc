package me.kodysimpson.conversational.commands;

import me.kodysimpson.conversational.utils.ConvoTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SummonNPCCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player p){

            if (args.length < 2){
                p.sendMessage("Please specify an NPC name and role.");
                return true;
            }

            String name = args[0];
            String role = args[1];

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.SKELETON, name);
            npc.addTrait(new ConvoTrait(role));
            npc.spawn(p.getLocation());

            p.sendMessage("NPC created with name " + name + " and role " + role);
            p.sendMessage("They will now have conversations with you as a " + role);
        }

        return true;
    }
}
