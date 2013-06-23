package com.tehbeard.beardstat.listeners;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.dragonzone.promise.Deferred;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.PlayerStatManager;
import com.tehbeard.BeardStat.listeners.StatVehicleListener;

@RunWith(PowerMockRunner.class)
public class TestVehicleListener {

    private PlayerStatManager manager;
    private List<String> blacklist = new ArrayList<String>();
    private StatVehicleListener listener;

    private EntityStatBlob blob;
    
    
    @Before
    public void setup(){
        //Create test blob
        blob = new EntityStatBlob("bob", 0, "player");
        //world blacklist
        blacklist.add("blacklisted");

        //Mock manager to return our blob
        manager = mock(PlayerStatManager.class);
        when(manager.getPlayerBlobASync(anyString())).thenReturn(new Deferred<EntityStatBlob>(blob));

        listener = new StatVehicleListener(blacklist, manager);
    }
    
    @Test
    public void testPlayerCartRide(){
        
        World world = mock(World.class);
        when(world.getName()).thenReturn("overworld");
        
        Location to = new Location(world,0,0,0);
        Location from = new Location(world,1,0,0);
        
        Vehicle v = mock(RideableMinecart.class);
        when(v.getWorld()).thenReturn(world);
        
        Player bob = mock(Player.class);
        when(bob.getName()).thenReturn("bob");
        
        when(v.getPassenger()).thenReturn(bob);
        
        when(v.getType()).thenReturn(EntityType.MINECART);
        
        VehicleMoveEvent event = new VehicleMoveEvent(v, from, to);
        
        listener.onVehicleMove(event);
        
        assertTrue(blob.getStat(BeardStat.DEFAULT_DOMAIN, "overworld", "vehicle", "minecart").isArchive());
        assertEquals("Ride value is 1",1,blob.getStat(BeardStat.DEFAULT_DOMAIN, "overworld", "vehicle", "minecart").getValue());
    }
}
