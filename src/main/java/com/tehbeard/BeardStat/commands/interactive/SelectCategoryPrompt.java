package com.tehbeard.BeardStat.commands.interactive;

import java.util.HashSet;
import java.util.Iterator;

import me.tehbeard.vocalise.parser.ConfigurablePrompt;
import me.tehbeard.vocalise.parser.PromptBuilder;
import me.tehbeard.vocalise.parser.PromptTag;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.IStat;
import com.tehbeard.BeardStat.containers.PlayerStatManager;

@PromptTag(tag = "getcat")
public class SelectCategoryPrompt extends ValidatingPrompt implements ConfigurablePrompt {

    private PlayerStatManager playerStatManager = BeardStat.self().getStatManager();
    private Prompt            next;

    public SelectCategoryPrompt() {

    }

    @Override
    public String getPromptText(ConversationContext context) {
        String player = (String) context.getSessionData("player");

        // begin paste
        HashSet<String> cats = new HashSet<String>();
        for (IStat ps : this.playerStatManager.getPlayerBlob(player).getStats()) {
            if (!cats.contains(ps.getCategory())) {
                cats.add(ps.getCategory());
            }
        }
        String msg = "";

        Iterator<String> it = cats.iterator();
        while (it.hasNext()) {
            msg += ChatColor.BLUE + it.next() + "\n";
        }
        return msg + ChatColor.AQUA + "Select a category to view";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        String player = (String) context.getSessionData("player");
        for (IStat ps : this.playerStatManager.getPlayerBlob(player).getStats()) {
            if (ps.getCategory().equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input) {
        context.setSessionData("c", input);
        return this.next;
    }

    @Override
    public void configure(ConfigurationSection config, PromptBuilder builder) {
        builder.makePromptRef(config.getString("id"), this);
        this.next = config.isString("next") ? builder.locatePromptById(config.getString("next")) : builder
                .generatePrompt(config.getConfigurationSection("next"));
    }

}
