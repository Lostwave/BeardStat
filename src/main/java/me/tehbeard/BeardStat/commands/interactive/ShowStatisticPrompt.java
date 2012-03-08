package me.tehbeard.BeardStat.commands.interactive;

import java.util.HashSet;
import java.util.Iterator;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.PlayerStatManager;

import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

public class ShowStatisticPrompt extends MessagePrompt{


    private static Prompt self = new ShowStatisticPrompt();
    private PlayerStatManager playerStatManager = BeardStat.self().getStatManager();
    private Prompt promptAgain = new BooleanPrompt() {
        
        public String getPromptText(ConversationContext context) {
            return "Search a new stat?";
        }
        
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context,
                boolean input) {
            return input ? SelectCategoryPrompt.getInstance() : END_OF_CONVERSATION;
        }
    };

    public static Prompt getInstance(){return self ;}

    private ShowStatisticPrompt(){

    }
    public String getPromptText(ConversationContext context) {
        Player player = ((Player)context.getForWhom());
        String msg = (String)context.getSessionData("c") + " :: " + (String)context.getSessionData("s") + " ";
        msg += playerStatManager.getPlayerBlob(player.getName()).getStat((String)context.getSessionData("c"), (String)context.getSessionData("s")).getValue();
        return msg;
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
        return promptAgain ;
    }


}
