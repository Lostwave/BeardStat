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
     * @param player
     * @return
     */
    public Promise<EntityStatBlob> getPlayer(UUID player, String name){
        return getPlayerAsync(player, name, true);
    }
    
    public Promise<EntityStatBlob> getPlayerAsync(UUID player, String name, boolean create){
        return get(new ProviderQuery(player, name, create));
    }
    
    public EntityStatBlob getPlayer(UUID player, String name, boolean create){
        try{
        return getPlayerAsync(player, name, create).getValue();
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
    
//    public EntityStatBlob getPlayerByName(String name){
//        for(Promise<EntityStatBlob> e : uuidCache.values()){
//            if(e.isResolved()){
//                if(e.getValue().getName().equalsIgnoreCase(name)){
//                    return e.getValue();
//                }
//            }
//        }
//        return null;
//    }

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
        for( Promise<EntityStatBlob> blobP : uuidCache.values()){
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
                        uuidCache.remove(blob.getUUID());
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
