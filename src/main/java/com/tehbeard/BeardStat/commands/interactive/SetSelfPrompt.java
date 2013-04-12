package com.tehbeard.BeardStat.commands.interactive;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import me.tehbeard.vocalise.parser.ConfigurablePrompt;
import me.tehbeard.vocalise.parser.PromptBuilder;
import me.tehbeard.vocalise.parser.PromptTag;

@PromptTag(tag="self")
public class SetSelfPrompt implements ConfigurablePrompt {

    private Prompt next;
    public String getPromptText(ConversationContext context) {
        return null;
    }

    public boolean blocksForInput(ConversationContext context) {
        return false;
    }

    public Prompt acceptInput(ConversationContext context, String input) {
        context.setSessionData("player",((Player)context.getForWhom()).getName());
        return next;
    }

    public void configure(ConfigurationSection config, PromptBuilder builder) {
        builder.makePromptRef(config.getString("id"),this);
        next = config.isString("next") ? builder.locatePromptById(config.getString("next")) : builder.generatePrompt(config.getConfigurationSection("next"));
    }
}
