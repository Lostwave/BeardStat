package com.tehbeard.beardstat.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import net.dragonzone.promise.Deferred;
import net.dragonzone.promise.Promise;

import com.tehbeard.beardstat.Refs;
import com.tehbeard.beardstat.BeardStatRuntimeException;
import com.tehbeard.beardstat.DbPlatform;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.dataproviders.IStatDataProvider;
import com.tehbeard.beardstat.dataproviders.ProviderQuery;
import com.tehbeard.beardstat.dataproviders.ProviderQueryResult;
import com.tehbeard.beardstat.dataproviders.metadata.StatisticMeta;
import com.tehbeard.beardstat.manager.OnlineTimeManager.ManagerRecord;
import java.util.Iterator;



/**
 * Manages a cache of online stat entities;
 *
 * @author James
 */
public class EntityStatManager {

    private final Map<UUID,Promise<EntityStatBlob>> uuidCache = new HashMap<UUID, Promise<EntityStatBlob>>();
    private final DbPlatform platform;
    private final IStatDataProvider backendDatabase;
    private ExecutorService loadQueue = Executors.newSingleThreadExecutor();

    public EntityStatManager(DbPlatform platform, IStatDataProvider backendDatabase) {
        this.platform = platform;

        this.backendDatabase = backendDatabase;

    }


    /**
     * Get the blob for a player
     * @param name name of the player
     * @param player
     * @return
     */
    public Promise<EntityStatBlob> getPlayer(String name, UUID player){
        return getPlayerAsync(name, player, true);
    }
    
    public Promise<EntityStatBlob> getPlayerAsync(String name, UUID player, boolean create){
        return get(new ProviderQuery(name, player, create));
    }
    
    public EntityStatBlob getPlayer(String name, UUID player, boolean create){
        try{
        return getPlayerAsync(name, player, create).getValue();
        }catch(Exception e){
            platform.handleError(new BeardStatRuntimeException("An error occured loading a stat blob for " + player.toString(), e, true));
            return null;
        }
    }
    
    public Promise<EntityStatBlob> get(ProviderQuery query){
        if(!uuidCache.containsKey(query.getUUID())){
        final Deferred<EntityStatBlob> promise = new Deferred<EntityStatBlob>();

        uuidCache.put(query.getUUID(), promise);//Cache UUID
        loadQueue.submit(new ASyncLoadBlob(query, backendDatabase, promise));
        }

        
        return uuidCache.get(query.getUUID());
    }
    
    /**
     * Query the database
     *
     * @param providerQuery
     * @return
     */
    public ProviderQueryResult[] queryDatabase(ProviderQuery providerQuery) {
        return backendDatabase.queryDatabase(providerQuery);
    }

    public void saveCache() {
        Iterator<Promise<EntityStatBlob>> cacheIterator = uuidCache.values().iterator();
        while(cacheIterator.hasNext()){
            Promise<EntityStatBlob> blobP = cacheIterator.next();
            if(blobP.isResolved()){
                EntityStatBlob blob = blobP.getValue();
                if (blob.getType().equals(IStatDataProvider.PLAYER_TYPE)) {
                    String entityName = blob.getName();
                    ManagerRecord timeRecord = OnlineTimeManager.getRecord(entityName);

                    if (timeRecord != null) {
                        platform.getLogger().log(Level.FINE, "saving time: [Player : {0} , world: {1}, time: {2}]", new Object[]{entityName, timeRecord.world, timeRecord.sessionTime()});
                        if (timeRecord.world != null) {
                            blob.getStat(Refs.DEFAULT_DOMAIN, timeRecord.world, "stats", "playedfor").incrementStat(timeRecord.sessionTime());
                        }
                    }
                    if (isPlayerOnline(entityName)) {
                        OnlineTimeManager.setRecord(entityName, platform.getWorldForPlayer(entityName));
                    } else {
                        OnlineTimeManager.wipeRecord(entityName);
                        cacheIterator.remove();
                    }
                }
                backendDatabase.pushEntityBlob(blob);
            }
        }
    }

    private boolean isPlayerOnline(String player) {
        return platform.isPlayerOnline(player);
    }

    public String getLocalizedStatisticName(String gameTag) {
        StatisticMeta meta = this.backendDatabase.getStatistic(gameTag,false);
        if(meta!= null){
            return meta.getLocalizedName();
        }
        return gameTag;
    }

    public String formatStat(String gameTag, int value) {
        StatisticMeta meta = this.backendDatabase.getStatistic(gameTag,false);
        if(meta!=null){
            return meta.formatStat(value);
        }
        return "" + value;
    }

    public void flush() {
        this.backendDatabase.flush();
    }

}
