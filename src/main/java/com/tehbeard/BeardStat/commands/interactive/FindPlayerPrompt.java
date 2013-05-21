package com.tehbeard.BeardStat.commands.interactive;

import me.tehbeard.vocalise.parser.ConfigurablePrompt;
import me.tehbeard.vocalise.parser.PromptBuilder;
import me.tehbeard.vocalise.parser.PromptTag;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import com.tehbeard.BeardStat.BeardStat;

@PromptTag(tag = "findplayer")
public class FindPlayerPrompt extends ValidatingPrompt implements ConfigurablePrompt {

    private Prompt next;

    @Override
    public String getPromptText(ConversationContext context) {
        return "Enter a player to lookup";
    }

    @Override
    public void configure(ConfigurationSection config, PromptBuilder builder) {
        builder.makePromptRef(config.getString("id"), this);
        this.next = config.isString("next") ? builder.locatePromptById(config.getString("next")) : builder
                .generatePrompt(config.getConfigurationSection("next"));
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        return (BeardStat.self().getStatManager().findPlayerBlob(input) != null);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input) {
        context.setSessionData("player", input);
        return this.next;
    }
}
