/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.test;

import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.containers.documents.DocumentFile;
import com.tehbeard.beardstat.containers.documents.IStatDocument;
import com.tehbeard.beardstat.dataproviders.IStatDataProvider;
import com.tehbeard.beardstat.dataproviders.ProviderQuery;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.test.MemoDocument.Memo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author James
 */
public class TestDocCommand implements CommandExecutor {
    private final EntityStatManager manager;

    public TestDocCommand(EntityStatManager manager){
        this.manager = manager;
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        IStatDataProvider provider = manager.getProvider();
        
        DocumentFile file = provider.pullDocument(new ProviderQuery("tehbeard", IStatDataProvider.PLAYER_TYPE, null,false), "default", "SUDO");
        MemoDocument doc = null;
        doc = (MemoDocument)file.getDocument(MemoDocument.class);
        for(Memo memo : doc.memos){
            sender.sendMessage(memo.toString());
        }
        
        
        return true;
    }
    
}
