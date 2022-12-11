package me.kodysimpson.conversational.listeners;

import me.kodysimpson.conversational.Conversational;
import me.kodysimpson.conversational.utils.ConvoTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.CompletableFuture;

public class NPCListeners implements Listener {

    private final Conversational plugin;
    public NPCListeners(Conversational plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent e) {

        //Get all the npcs near the player
        for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {

            if (npc.getEntity() == null || npc.getTraitNullable(ConvoTrait.class) == null){
                continue;
            }

            var trait = npc.getTraitNullable(ConvoTrait.class);
            var talkingTo = trait.getTalkingTo();

            //See if the NPC is talking to the player
            if (talkingTo == null || !talkingTo.equals(e.getPlayer())){
                continue;
            }

            //If the player is talking to the NPC but is not within 20 blocks, stop the conversation
            if (npc.getEntity().getLocation().distance(e.getPlayer().getLocation()) > 20){
                trait.stopConversation();
            }else{
                //get what the player typed in chat
                trait.addMessage(e.getMessage());

                CompletableFuture.runAsync(() -> {
                    //Use OpenAI to get a response
                    trait.getResponse(talkingTo, e.getMessage());
                });

                //Cancel the event so the message doesn't show up in chat
                e.setCancelled(true);
            }

        }

    }

}
