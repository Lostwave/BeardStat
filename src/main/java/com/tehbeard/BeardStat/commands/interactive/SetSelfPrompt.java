package com.tehbeard.BeardStat.commands.interactive;

import me.tehbeard.vocalise.parser.ConfigurablePrompt;
import me.tehbeard.vocalise.parser.PromptBuilder;
import me.tehbeard.vocalise.parser.PromptTag;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

@PromptTag(tag = "self")
public class SetSelfPrompt implements ConfigurablePrompt {

    private Prompt next;

    @Override
    public String getPromptText(ConversationContext context) {
        return null;
    }

    @Override
    public boolean blocksForInput(ConversationContext context) {
        return false;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        context.setSessionData("player", ((Player) context.getForWhom()).getName());
        return this.next;
    }

    @Override
    public void configure(ConfigurationSection config, PromptBuilder builder) {
        builder.makePromptRef(config.getString("id"), this);
        this.next = config.isString("next") ? builder.locatePromptById(config.getString("next")) : builder
                .generatePrompt(config.getConfigurationSection("next"));
    }
}
