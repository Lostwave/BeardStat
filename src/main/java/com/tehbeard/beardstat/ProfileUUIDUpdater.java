package com.tehbeard.beardstat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.tehbeard.beardstat.dataproviders.IStatDataProvider;
import com.tehbeard.beardstat.dataproviders.ProviderQuery;
import com.tehbeard.beardstat.dataproviders.ProviderQueryResult;
import com.tehbeard.utils.uuid.MojangWebAPI;



public class ProfileUUIDUpdater {
    
    public ProfileUUIDUpdater(Logger logger, IStatDataProvider provider) throws Exception{
        
        logger.warning("Updating UUIDs, warning! This may take a while.");
        logger.info("Loading list of players stored in database");
        
        //Grab the players we have to process.
        ProviderQueryResult[] results = provider.queryDatabase(ProviderQuery.ALL_PLAYERS);

        Set<String> names = new HashSet<String>();
        
        logger.info("Locating entries with no uuid.");
        for( ProviderQueryResult result : results){
            if(result.uuid == null){
                
                names.add(result.name.toLowerCase());
            }
        }
        logger.info("Found " + names.size() + " entries with no uuid");
        logger.info("Calling Mojang Web API to get players UUIDs, This might take a while");
        Map<String, UUID> map = MojangWebAPI.lookupUUIDS(new ArrayList<String>(names));
        logger.info("Processing results...");
        for (Entry<String, UUID> e : map.entrySet()) {
            names.remove(e.getKey().toLowerCase());
            provider.setUUID(e.getKey(),e.getValue().toString().replace("-", ""));
        }
        logger.info("Name->UUID mapping processed.");
        
        //Alert admin if any were not processed.
        if(names.size() > 0){
            logger.warning("Some usernames did not return a profile id, this could be an issue with mojang's server, or the username is no longer valid. (reverted to a non paid name)");
            logger.warning("These players uuids could not be collected.");
            for(String name : names){
                logger.warning(name + (MojangWebAPI.hasPaid(name) ? " Paid account (bug?)" : " Non paid account (Refunded?)"));
            }
            logger.warning("If you believe these players to be active, please try again.");
        }
    }

}
