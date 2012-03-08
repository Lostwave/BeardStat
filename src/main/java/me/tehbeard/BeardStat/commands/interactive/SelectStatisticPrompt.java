package me.tehbeard.BeardStat.commands.interactive;

import java.util.HashSet;
import java.util.Iterator;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

public class SelectStatisticPrompt extends ValidatingPrompt{


    private static Prompt self = new SelectStatisticPrompt();
    private PlayerStatManager playerStatManager = BeardStat.self().getStatManager();

    public static Prompt getInstance(){return self ;}

    private SelectStatisticPrompt(){

    }
    public String getPromptText(ConversationContext context) {
        Player player = ((Player)context.getForWhom());

        //begin paste
        HashSet<String> stats = new HashSet<String>();
        for( PlayerStat ps :playerStatManager.getPlayerBlob(player.getName()).getStats()){
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
        Player player = ((Player)context.getForWhom());
        for(PlayerStat ps :playerStatManager.getPlayerBlob(player.getName()).getStats()){
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
        return ShowStatisticPrompt.getInstance();
    }

}
