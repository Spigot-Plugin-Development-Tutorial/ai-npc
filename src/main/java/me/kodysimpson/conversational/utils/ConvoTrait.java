package me.kodysimpson.conversational.utils;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import me.kodysimpson.conversational.Conversational;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

@TraitName("convotrait")
public class ConvoTrait extends Trait {

    private final Conversational plugin = Conversational.getPlugin(Conversational.class);
    private final String CONVO_STARTER;
    private Player talkingTo = null;
    private StringBuilder conversation = new StringBuilder();
    public String role;

    public ConvoTrait() {
        super("convotrait");
        CONVO_STARTER = "The AI: ";
    }

    public ConvoTrait(String role) {
        super("convotrait");
        this.role = role;
        this.CONVO_STARTER = "The following is a conversation with an AI who represents a " + this.role.toLowerCase() + " NPC character in Minecraft. " +
                "The AI should limit his knowledge of the world to minecraft and being a " + this.role.toLowerCase() + " and try not to stray even if asked about something else. " +
                "Play this " + this.role.toLowerCase() + "role the best you can.\n\nHuman: Hey!\n\nAI:";
    }

    @EventHandler
    public void startConversation(NPCRightClickEvent event){

        if (event.getNPC() != npc) return;

        Player p = event.getClicker();
        if (this.talkingTo == null){
            startConversation(p);
        }else{
            if (this.talkingTo != p){

                //See if the person the NPC is talking to is within 20 blocks
                if (npc.getEntity().getLocation().distance(this.talkingTo.getLocation()) > 20){
                    this.talkingTo.sendMessage("The " + this.role + " NPC stopped talking to you because you moved too far away.");
                    startConversation(p);
                }

                p.sendMessage("I am talking to someone else right now!");
            }else{
                p.sendMessage("I am already talking to you!");
            }
        }
    }

    private void startConversation(Player p){
        this.talkingTo = p;
        this.conversation = new StringBuilder(this.CONVO_STARTER);
        getResponse(this.talkingTo, null);
    }

    public void stopConversation(){
        this.talkingTo.sendMessage("You are no longer talking to the " + this.role + " NPC.");
        this.talkingTo = null;
        this.conversation = new StringBuilder();
    }

    public Player getTalkingTo() {
        return talkingTo;
    }

    public void addMessage(String message){
        this.conversation.append("\n\nHuman:").append(message).append("\n\nAI:");
    }

    public void getResponse(Player p, String playerMessage){
        plugin.adventure().sender(p).sendActionBar(Component.text("Thinking..."));
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);

        //Use OpenAI to get a response from GPT-3
        OpenAiService service = new OpenAiService(API TOKEN HERE, 0);
        CompletionRequest request = CompletionRequest.builder()
                .prompt(this.conversation.toString())
                .model("text-davinci-003") //Use the latest davinci model
                .temperature(0.50) //How creative the AI should be
                .maxTokens(150) //How many tokens the AI should generate. Tokens are words, punctuation, etc.
                .topP(1.0) //How much diversity the AI should have. 1.0 is the most diverse
                .frequencyPenalty(0.0) //How much the AI should avoid repeating itself
                .presencePenalty(0.6) //How much the AI should avoid repeating the same words
                .stop(List.of("Human:", "AI:")) //Stop the AI from generating more text when it sees these words
                .build();
        var choices = service.createCompletion(request).getChoices();
        var response = choices.get(0).getText(); //what the AI responds with
        this.conversation.append(response.stripLeading());
        if (playerMessage != null) p.sendMessage("You: " + playerMessage);
        p.sendMessage(this.npc.getName() + ": " + response.stripLeading());
    }

}
