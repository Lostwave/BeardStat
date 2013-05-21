package com.tehbeard.BeardStat.DataProviders;

import java.util.List;

import net.dragonzone.promise.Promise;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.IStat;

/**
 * Will transfer blobs from one stat provider to another.
 * 
 * Mechanism: onPull: Attempt to locate statblob in oldProvider if found return
 * it and wipe from old provider if fail use newProvider
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
        BeardStat.printCon("Beginning data transfer");
        List<String> theList = this.oldProvider.getStatBlobsHeld();
        EntityStatBlob b;
        for (String player : theList) {
            b = this.oldProvider.pullPlayerStatBlob(player, false).getValue();
            if (b == null) {
                BeardStat.printCon("[ERROR] " + player + " not found in old database");
                continue;
            }
            for (IStat s : b.getStats()) {
                s.archive();
            }
            BeardStat.printCon("Pushing " + player + " to new dataprovider");
            this.newProvider.pushPlayerStatBlob(b);

        }
        BeardStat.printCon("Flushing data");
        this.newProvider.flushSync();

    }

    @Override
    public Promise<EntityStatBlob> pullPlayerStatBlob(String player) {
        return pullPlayerStatBlob(player, true);
    }

    @Override
    public Promise<EntityStatBlob> pullPlayerStatBlob(String player, boolean create) {
        return this.newProvider.pullPlayerStatBlob(player, create);
    }

    @Override
    public void pushPlayerStatBlob(EntityStatBlob player) {
        this.newProvider.pushPlayerStatBlob(player);

    }

    @Override
    public void flush() {
        this.newProvider.flush();

    }

    @Override
    public void deletePlayerStatBlob(String player) {
        this.newProvider.deletePlayerStatBlob(player);

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
