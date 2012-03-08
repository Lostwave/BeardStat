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

public class SelectCategoryPrompt extends ValidatingPrompt{

    
    private static Prompt self = new SelectCategoryPrompt();
    private PlayerStatManager playerStatManager = BeardStat.self().getStatManager();

    public static Prompt getInstance(){return self ;}
    
    private SelectCategoryPrompt(){
        
    }
    public String getPromptText(ConversationContext context) {
        Player player = ((Player)context.getForWhom());
        
        //begin paste
        HashSet<String> cats = new HashSet<String>();
        for( PlayerStat ps :playerStatManager.getPlayerBlob(player.getName()).getStats()){
            if(!cats.contains(ps.getCat())){
                cats.add(ps.getCat());
            }
        }
        String msg = "";

        Iterator<String> it = cats.iterator();
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
        return "Select a category to view";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        Player player = ((Player)context.getForWhom());
        for(PlayerStat ps :playerStatManager.getPlayerBlob(player.getName()).getStats()){
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
        return SelectCategoryPrompt.getInstance();
    }

}
