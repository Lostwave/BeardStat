package me.tehbeard.BeardStat.DataProviders;

import java.util.List;

import me.tehbeard.BeardStat.BeardStat;
import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.PlayerStatBlob;


/**
 * Will transfer blobs from one stat provider to another.
 * 
 * Mechanism:
 * onPull: Attempt to locate statblob in oldProvider
 *         if found return it and wipe from old provider
 *         if fail use newProvider
 *         
 * @author James
 *
 */
public class TransferDataProvider implements IStatDataProvider{
    
    private IStatDataProvider oldProvider;
    private IStatDataProvider newProvider;

    
    
    /**
     * @param oldProvider
     * @param newProvider
     */
    public TransferDataProvider(IStatDataProvider oldProvider,
            IStatDataProvider newProvider) {
        this.oldProvider = oldProvider;
        this.newProvider = newProvider;
        
        transfer();
    }

    private void transfer() {
        BeardStat.printCon("Beginning data transfer");
        List<String> theList = oldProvider.getStatBlobsHeld();
        PlayerStatBlob b;
        for(String player : theList){
            b = oldProvider.pullPlayerStatBlob(player, false);
            if(b==null){
                BeardStat.printCon("[ERROR] " + player + " not found in old database");
                continue;
            }
            for(PlayerStat s : b.getStats()){
                s.archive();
            }
            BeardStat.printCon("Pushing " + player + " to new dataprovider");
            newProvider.pushPlayerStatBlob(b);
            
        }
        BeardStat.printCon("Flushing data");
        newProvider.flushSync();
        
    }

    public PlayerStatBlob pullPlayerStatBlob(String player) {
        return pullPlayerStatBlob(player,true);
    }

    public PlayerStatBlob pullPlayerStatBlob(String player, boolean create) {
        return newProvider.pullPlayerStatBlob(player,create);
    }

    public void pushPlayerStatBlob(PlayerStatBlob player) {
        newProvider.pushPlayerStatBlob(player);
        
    }

    public void flush() {
        newProvider.flush();
        
    }

    public void deletePlayerStatBlob(String player) {
        newProvider.deletePlayerStatBlob(player);
        
    }

    public boolean hasStatBlob(String player) {
        return newProvider.hasStatBlob(player);
    }

    public List<String> getStatBlobsHeld() {
        return newProvider.getStatBlobsHeld();
    }

    public void flushSync() {
        newProvider.flushSync();
        
    }

}
