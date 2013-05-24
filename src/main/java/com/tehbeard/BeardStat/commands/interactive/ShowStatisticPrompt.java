package com.tehbeard.BeardStat.commands.interactive;

import me.tehbeard.vocalise.parser.ConfigurablePrompt;
import me.tehbeard.vocalise.parser.PromptBuilder;
import me.tehbeard.vocalise.parser.PromptTag;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.commands.formatters.FormatFactory;
import com.tehbeard.BeardStat.containers.PlayerStatManager;

/**
 * Display stat to player
 * 
 * @author James
 * 
 */
@PromptTag(tag = "showstat")
public class ShowStatisticPrompt extends MessagePrompt implements ConfigurablePrompt {

    private PlayerStatManager playerStatManager = BeardStat.self().getStatManager();
    private Prompt            next;

    public ShowStatisticPrompt() {

    }

    @Override
    public String getPromptText(ConversationContext context) {
        String player = (String) context.getSessionData("player");

        String msg = (String) context.getSessionData("c") + "." + (String) context.getSessionData("s") + " = ";
        msg += FormatFactory.formatStat(this.playerStatManager.getPlayerBlob(player).getStat(BeardStat.DEFAULT_DOMAIN,
                BeardStat.GLOBAL_WORLD, (String) context.getSessionData("c"), (String) context.getSessionData("s")));// TODO:
                                                                                                                     // FIX
                                                                                                                     // THIS
                                                                                                                     // TO
                                                                                                                     // NEW
                                                                                                                     // STYLE
                                                                                                                     // OF
                                                                                                                     // STATS
        return msg;
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
        return this.next;
    }

    @Override
    public void configure(ConfigurationSection config, PromptBuilder builder) {

        builder.makePromptRef(config.getString("id"), this);
        this.next = config.isString("next") ? builder.locatePromptById(config.getString("next")) : builder
                .generatePrompt(config.getConfigurationSection("next"));
    }
}
