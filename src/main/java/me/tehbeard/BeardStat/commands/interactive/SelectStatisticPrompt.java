package me.tehbeard.BeardStat.commands.interactive;

import java.util.HashSet;
import java.util.Iterator;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;
import me.tehbeard.vocalise.parser.ConfigurablePrompt;
import me.tehbeard.vocalise.parser.PromptBuilder;
import me.tehbeard.vocalise.parser.PromptTag;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

@PromptTag(tag="getstat")
public class SelectStatisticPrompt extends ValidatingPrompt implements ConfigurablePrompt{



    private PlayerStatManager playerStatManager = BeardStat.self().getStatManager();
    private Prompt next;

    public SelectStatisticPrompt(){

    }
    public String getPromptText(ConversationContext context) {
        String player = (String)context.getSessionData("player");

        //begin paste
        HashSet<String> stats = new HashSet<String>();
        for( PlayerStat ps :playerStatManager.getPlayerBlob(player).getStats()){
            if(ps.getCat().equalsIgnoreCase((String)context.getSessionData("c"))){
                stats.add(ps.getName());
            }
        }
        String msg = "";

        Iterator<String> it = stats.iterator();
        while(it.hasNext()){
            for(int i=0;i<10;i++){
                if(it.hasNext()){
                    if(i>0){msg+=", ";}
                    msg+=it.next();
                }
            }
            context.getForWhom().sendRawMessage(ChatColor.AQUA + msg);
            msg="";        
        }
        return "Select a statistic to view";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        String player = (String) context.getSessionData("player");
        for(PlayerStat ps :playerStatManager.getPlayerBlob(player).getStats()){
            if(ps.getCat().equalsIgnoreCase((String)context.getSessionData("c"))){
                if(ps.getName().equalsIgnoreCase(input)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context,
            String input) {
        context.setSessionData("s", input);
        return  next;
    }
    public void configure(ConfigurationSection config, PromptBuilder builder) {

        builder.makePromptRef(config.getString("id"),this);
        next = config.isString("next") ? builder.locatePromptById(config.getString("next")) : builder.generatePrompt(config.getConfigurationSection("next"));
    }

}
