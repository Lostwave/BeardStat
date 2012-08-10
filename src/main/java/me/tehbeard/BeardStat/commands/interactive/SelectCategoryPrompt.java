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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

@PromptTag(tag="getcat")
public class SelectCategoryPrompt extends ValidatingPrompt implements ConfigurablePrompt{



    private PlayerStatManager playerStatManager = BeardStat.self().getStatManager();
    private Prompt next;

    public SelectCategoryPrompt(){

    }
    public String getPromptText(ConversationContext context) {
        String player = (String) context.getSessionData("player");


        //begin paste
        HashSet<String> cats = new HashSet<String>();
        for( PlayerStat ps :playerStatManager.getPlayerBlob(player).getStats()){
            if(!cats.contains(ps.getCat())){
                cats.add(ps.getCat());
            }
        }
        String msg = "";

        Iterator<String> it = cats.iterator();
        while(it.hasNext()){
            msg+=ChatColor.BLUE + it.next() + "\n";
        }
        return msg + ChatColor.AQUA + "Select a category to view";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        String player = (String) context.getSessionData("player");
        for(PlayerStat ps :playerStatManager.getPlayerBlob(player).getStats()){
            if(ps.getCat().equalsIgnoreCase(input)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context,
            String input) {
        context.setSessionData("c", input);
        return next;
    }

    public void configure(ConfigurationSection config, PromptBuilder builder) {
        builder.makePromptRef(config.getString("id"),this);
        next = config.isString("next") ? builder.locatePromptById(config.getString("next")) : builder.generatePrompt(config.getConfigurationSection("next"));
    }


}
