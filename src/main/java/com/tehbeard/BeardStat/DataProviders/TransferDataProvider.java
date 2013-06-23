package com.tehbeard.BeardStat.DataProviders;

import java.util.List;

import net.dragonzone.promise.Promise;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.IStat;

/**
 * Will transfer blobs from one stat provider to another.
 * 
 * Transfers inside constructor
 * 
 * @author James
 * 
 */
public class TransferDataProvider implements IStatDataProvider {

    private IStatDataProvider oldProvider;
    private IStatDataProvider newProvider;

    /**
     * @param oldProvider
     * @param newProvider
     */
    public TransferDataProvider(IStatDataProvider oldProvider, IStatDataProvider newProvider) {
        this.oldProvider = oldProvider;
        this.newProvider = newProvider;

        transfer();
    }

    private void transfer() {
        //TODO: FEEX
        BeardStat.printCon("Beginning data transfer");
        List<String> theList = this.oldProvider.getStatBlobsHeld();
        EntityStatBlob b;
        for (String player : theList) {
            b = this.oldProvider.pullStatBlob(player,"player", false).getValue();
            if (b == null) {
                BeardStat.printCon("[ERROR] " + player + " not found in old database");
                continue;
            }
            for (IStat s : b.getStats()) {
                s.archive();
            }
            BeardStat.printCon("Pushing " + player + " to new dataprovider");
            this.newProvider.pushStatBlob(b);

        }
        BeardStat.printCon("Flushing data");
        this.newProvider.flushSync();

    }

    @Override
    public Promise<EntityStatBlob> pullStatBlob(String player, String type) {
        return pullStatBlob(player, type, true);
    }

    @Override
    public Promise<EntityStatBlob> pullStatBlob(String player, String type, boolean create) {
        return this.newProvider.pullStatBlob(player,type, create);
    }

    @Override
    public void pushStatBlob(EntityStatBlob player) {
        this.newProvider.pushStatBlob(player);

    }

    @Override
    public void flush() {
        this.newProvider.flush();

    }

    @Override
    public void deleteStatBlob(String player) {
        this.newProvider.deleteStatBlob(player);

    }

    @Override
    public boolean hasStatBlob(String player) {
        return this.newProvider.hasStatBlob(player);
    }

    @Override
    public List<String> getStatBlobsHeld() {
        return this.newProvider.getStatBlobsHeld();
    }

    @Override
    public void flushSync() {
        this.newProvider.flushSync();

    }

}
