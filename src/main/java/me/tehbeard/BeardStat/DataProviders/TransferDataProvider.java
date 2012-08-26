package me.tehbeard.BeardStat.DataProviders;

import java.util.List;

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
    }

    public PlayerStatBlob pullPlayerStatBlob(String player) {
        return pullPlayerStatBlob(player,true);
    }

    public PlayerStatBlob pullPlayerStatBlob(String player, boolean create) {
        PlayerStatBlob blob = oldProvider.pullPlayerStatBlob(player, false);
        if(blob == null){
            blob = newProvider.pullPlayerStatBlob(player,create);
        }
        return blob;
    }

    public void pushPlayerStatBlob(PlayerStatBlob player) {
        newProvider.pushPlayerStatBlob(player);
        
    }

    public void flush() {
        newProvider.flush();
        
    }

    public void deletePlayerStatBlob(String player) {
        
    }

    public boolean hasStatBlob(String player) {
        // TODO Auto-generated method stub
        return false;
    }

    public List<String> getStatBlobsHeld() {
        // TODO Auto-generated method stub
        return null;
    }

}
