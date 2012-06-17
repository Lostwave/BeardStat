package me.tehbeard.BeardStat.commands.interactive;


import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.commands.formatters.FormatFactory;
import me.tehbeard.BeardStat.containers.PlayerStatManager;
import me.tehbeard.vocalise.parser.ConfigurablePrompt;
import me.tehbeard.vocalise.parser.PromptBuilder;
import me.tehbeard.vocalise.parser.PromptTag;


import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

@PromptTag(tag="showstat")
public class ShowStatisticPrompt extends MessagePrompt implements ConfigurablePrompt{


    private PlayerStatManager playerStatManager = BeardStat.self().getStatManager();
    private Prompt next;

    public ShowStatisticPrompt(){

    }
    public String getPromptText(ConversationContext context) {
        String player = (String) context.getSessionData("player");
        
        String msg = (String)context.getSessionData("c") + "." + (String)context.getSessionData("s") + " = ";
        msg += FormatFactory.formatStat(playerStatManager.getPlayerBlob(player).getStat((String)context.getSessionData("c"), (String)context.getSessionData("s")));
        return msg;
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
        return next;
    }

    public void configure(ConfigurationSection config, PromptBuilder builder) {

        builder.makePromptRef(config.getString("id"),this);
        next = config.isString("next") ? builder.locatePromptById(config.getString("next")) : builder.generatePrompt(config.getConfigurationSection("next"));
    }
}
