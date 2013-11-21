package com.tehbeard.beardstat.manager;

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.DbPlatform;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.dataproviders.IStatDataProvider;
import com.tehbeard.beardstat.dataproviders.ProviderQuery;
import com.tehbeard.beardstat.dataproviders.ProviderQueryResult;
import com.tehbeard.beardstat.manager.OnlineTimeManager.ManagerRecord;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import net.dragonzone.promise.Deferred;
import net.dragonzone.promise.Promise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manages a cache of online stat entities;
 *
 * @author James
 */
public class EntityStatManager {

    private final CacheDatabase cache = new CacheDatabase();
    private final DbPlatform platform;
    private final IStatDataProvider backendDatabase;
    private ExecutorService loadQueue = Executors.newSingleThreadExecutor();

    public EntityStatManager(DbPlatform platform, IStatDataProvider backendDatabase) {
        this.platform = platform;
        this.backendDatabase = backendDatabase;

    }

    public EntityStatBlob getBlobForPlayer(Player player){
        return getBlobForPlayerAsync(player).getValue();
    }

    public EntityStatBlob getBlob(ProviderQuery query){
        return getBlobASync(query).getValue();
    }

    public EntityStatBlob[] getBlobs(ProviderQuery query){
        ProviderQueryResult[] results = queryDatabase(query);
        EntityStatBlob[] blobs = new EntityStatBlob[results.length];
        for(int i = 0;i<results.length; i++){
            blobs[i] = getBlob(results[i].asProviderQuery());
        }
        return blobs;
    }

    public Promise<EntityStatBlob> getBlobForPlayerAsync(Player player){
        //TODO use uuid in future
        return getBlobASync(new ProviderQuery(player.getName(), IStatDataProvider.PLAYER_TYPE, null, true));
    }

    public Promise<EntityStatBlob> getBlobASync(final ProviderQuery query) {
        if (query.likeName) {
            throw new IllegalStateException("Cannot use partial matching in query to fetch a blob");
        }

        if (!cache.hasEntry(query)) {
            final Deferred<EntityStatBlob> promise = new Deferred<EntityStatBlob>();
            cache.addToCache(query, promise);
            loadQueue.submit(new ASyncLoadBlob(query, backendDatabase, promise));
        }
        return cache.getCache(query);
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
        for( EntityStatBlob blob : cache.getLoadedBlobs()){
            if (blob.getType().equals(IStatDataProvider.PLAYER_TYPE)) {
                    String entityName = blob.getName();
                    ManagerRecord timeRecord = OnlineTimeManager.getRecord(entityName);

                    if (timeRecord != null) {
                        platform.getLogger().log(Level.FINE, "saving time: [Player : {0} , world: {1}, time: {2}]", new Object[]{entityName, timeRecord.world, timeRecord.sessionTime()});
                        if (timeRecord.world != null) {
                            blob.getStat(BeardStat.DEFAULT_DOMAIN, timeRecord.world, "stats", "playedfor").incrementStat(timeRecord.sessionTime());
                        }
                    }
                    if (isPlayerOnline(entityName)) {
                        OnlineTimeManager.setRecord(entityName, Bukkit.getPlayer(entityName).getWorld().getName());
                    } else {
                        OnlineTimeManager.wipeRecord(entityName);
                        cache.remove(new ProviderQuery(blob.getName(), blob.getType(), blob.getUUID(),false));
                    }
                }
        }
    }

    private boolean isPlayerOnline(String player) {
        return Bukkit.getOfflinePlayer(player).isOnline();
    }

    public String getLocalizedStatisticName(String gameTag) {
        return this.backendDatabase.getStatistic(gameTag).getLocalizedName();
    }

    public String formatStat(String gameTag, int value) {
        return this.backendDatabase.getStatistic(gameTag).formatStat(value);
    }

    public void flush() {
        this.backendDatabase.flush();
    }
}