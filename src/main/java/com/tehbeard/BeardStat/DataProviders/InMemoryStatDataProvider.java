/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.BeardStat.DataProviders;

import com.tehbeard.BeardStat.DataProviders.metadata.CategoryMeta;
import com.tehbeard.BeardStat.DataProviders.metadata.DomainMeta;
import com.tehbeard.BeardStat.DataProviders.metadata.StatisticMeta;
import com.tehbeard.BeardStat.DataProviders.metadata.WorldMeta;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.dragonzone.promise.Deferred;
import net.dragonzone.promise.Promise;

/**
 * Memory based stat provider, used for the cache.
 *
 * @author James
 */
public class InMemoryStatDataProvider implements IStatDataProvider {

    Set<EntityStatBlob> cache = new HashSet<EntityStatBlob>();

    @Override
    public Promise<EntityStatBlob> pullEntityBlob(ProviderQuery query) {
        return new Deferred<EntityStatBlob>(pullEntityBlobDirect(query));
        
    }

    @Override
    public EntityStatBlob pullEntityBlobDirect(ProviderQuery query) {
        if(queryDatabase(query).length != 1){
            throw new IllegalStateException("Invalid query provided");
        }
        
        for(EntityStatBlob blob : cache){
            if(matches(blob,query)){return blob;}
        }
        return null;
    }

    @Override
    public void pushEntityBlob(EntityStatBlob blob) {
        cache.add(blob);
    }

    @Override
    public boolean hasEntityBlob(ProviderQuery query) {
        for(EntityStatBlob blob : cache){
            if(matches(blob,query)){return true;}
        }
        
        return false;
    }

    @Override
    public boolean deleteEntityBlob(EntityStatBlob blob) {
        return cache.remove(blob);
    }

    private boolean matches(EntityStatBlob blob, ProviderQuery query) {
        if (query.name != null && !blob.getName().equalsIgnoreCase(query.name)) {
            return false;
        }

        if (query.type != null && !blob.getType().equalsIgnoreCase(query.type)) {
            return false;
        }
        if (query.uuid != null && !blob.getUUID().equals(query.uuid)) {
            return false;
        }

        return true;
    }

    @Override
    public ProviderQueryResult[] queryDatabase(ProviderQuery query) {
        List<ProviderQueryResult> result = new ArrayList<ProviderQueryResult>();
        for (EntityStatBlob blob : cache) {
            if (matches(blob, query)) {
                result.add(new ProviderQueryResult(blob.getEntityID(), blob.getName(), blob.getType(), blob.getUUID()));
            }
        }
        return result.toArray(new ProviderQueryResult[0]);
    }

    @Override
    public void flushSync() {
    }

    @Override
    public void flush() {
    }

    @Override
    public DomainMeta getDomain(String gameTag) {
        return new DomainMeta(0, gameTag);
    }

    @Override
    public WorldMeta getWorld(String gameTag) {
        return new WorldMeta(0, gameTag, gameTag);
    }

    @Override
    public CategoryMeta getCategory(String gameTag) {
        return new CategoryMeta(0, gameTag);
    }

    @Override
    public StatisticMeta getStatistic(String gameTag) {
        return new StatisticMeta(0, gameTag, gameTag, StatisticMeta.Formatting.none);
    }
}
