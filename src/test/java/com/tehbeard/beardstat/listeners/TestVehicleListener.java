package com.tehbeard.beardstat.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

import com.tehbeard.beardstat.BeardStat;
import com.tehbeard.beardstat.dataproviders.IStatDataProvider;
import com.tehbeard.beardstat.WorldManager;
import com.tehbeard.beardstat.containers.EntityStatBlob;
import com.tehbeard.beardstat.manager.EntityStatManager;
import com.tehbeard.beardstat.listeners.StatVehicleListener;
import org.bukkit.GameMode;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class TestVehicleListener {

    private EntityStatManager manager;
    private StatVehicleListener listener;
    private EntityStatBlob blob;

    @Before
    public void setup() {
        // Create test blob
        this.blob = new EntityStatBlob("bob", 0, IStatDataProvider.PLAYER_TYPE,null);

        // Mock manager to return our blob
        this.manager = mock(EntityStatManager.class);
        when(this.manager.getOrCreatePlayerStatBlob(anyString())).thenReturn(new Deferred<EntityStatBlob>(this.blob));

        BeardStat plugin = mock(BeardStat.class);
        // when(plugin.printDebugCon(anyString()))
        BeardStat.worldManager = new WorldManager();
        BeardStat.worldManager.addWorld("blacklisted", false, false, false);
        this.listener = new StatVehicleListener(this.manager, plugin);
    }

    @Test
    public void testPlayerCartRide() {

        World world = mock(World.class);
        when(world.getName()).thenReturn("overworld");

        Location to = new Location(world, 0, 0, 0);
        Location from = new Location(world, 1, 0, 0);

        Vehicle v = mock(RideableMinecart.class);
        when(v.getWorld()).thenReturn(world);

        Player bob = mock(Player.class);
        when(bob.getName()).thenReturn("bob");
        when(bob.getWorld()).thenReturn(world);
        when(bob.getGameMode()).thenReturn(GameMode.SURVIVAL);

        when(v.getPassenger()).thenReturn(bob);

        when(v.getType()).thenReturn(EntityType.MINECART);

        VehicleMoveEvent event = new VehicleMoveEvent(v, from, to);

        this.listener.onVehicleMove(event);

        assertTrue(this.blob.getStat(BeardStat.DEFAULT_DOMAIN, "overworld", "vehicle", "minecart").isArchive());
        assertEquals("Ride value is 1", 1,
                this.blob.getStat(BeardStat.DEFAULT_DOMAIN, "overworld", "vehicle", "minecart").getValue());
    }
}
