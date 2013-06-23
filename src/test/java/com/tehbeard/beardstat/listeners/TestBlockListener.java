package com.tehbeard.beardstat.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.dragonzone.promise.Deferred;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tehbeard.BeardStat.BeardStat;
import com.tehbeard.BeardStat.containers.EntityStatBlob;
import com.tehbeard.BeardStat.containers.IStat;
import com.tehbeard.BeardStat.containers.PlayerStatManager;
import com.tehbeard.BeardStat.listeners.StatBlockListener;
import com.tehbeard.BeardStat.utils.MetaDataCapture;
import com.tehbeard.BeardStat.utils.MetaDataCapture.EntryInfo;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class TestBlockListener {

    private PlayerStatManager manager;
    private List<String> blacklist = new ArrayList<String>();
    private StatBlockListener listener;

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

        listener = new StatBlockListener(blacklist, manager);

    }

    public Byte[] genUniqueSubIds(Material mat){
        if(MetaDataCapture.hasMetaData(mat)){
            EntryInfo info = MetaDataCapture.mats.get(mat);
            Set<Byte> bytes = new HashSet<Byte>();
            for(byte i = (byte) info.min;i<=info.max;i++){
                bytes.add((byte) info.getMetdataValue(i));
            }
            return bytes.toArray(new Byte[0]);
        }


        return new Byte[]{0};
    }

    @Test
    public void testPlayerPlaceBlockTrackedWorld(){

        Player bob = mock(Player.class);
        when(bob.getName()).thenReturn("bob");
        World mockWorld = PowerMockito.mock(World.class);
        when(mockWorld.getName()).thenReturn("overworld");

        when(bob.getWorld()).thenReturn(mockWorld);

        Block block = mock(Block.class);


        int count = 1;
        for(Material m : Material.values()){
            if(!m.isBlock()){continue;}

            when(block.getType()).thenReturn(m);

            Byte[] bytes = genUniqueSubIds(m);
            for(byte b : bytes){
                when(block.getData()).thenReturn(b);

                BlockPlaceEvent event = new BlockPlaceEvent(block, null, null, null, bob, true);

                listener.onBlockPlace(event);

                IStat stat = blob.getStat(BeardStat.DEFAULT_DOMAIN, bob.getWorld().getName(), "stats", "totalblockcreate");
                assertTrue("total blocks placed archived",stat.isArchive());
                assertEquals("total blocks count correct", count, stat.getValue());

                String matName = m.toString().toLowerCase().replace("_", "");
                System.out.println("Testing " + matName);

                String statId = matName;
                if(MetaDataCapture.hasMetaData(m)){
                    statId = matName + "_" + ((int)b);
                }
                stat = blob.getStat(BeardStat.DEFAULT_DOMAIN, bob.getWorld().getName(), "blockcreate", statId);
                assertTrue(statId + " blocks placed archived",stat.isArchive());
                assertEquals(statId + " count",1,stat.getValue());
                count++;
            }

        }
    }

    @Test
    public void testPlayerPlaceBlockUnTrackedWorld(){

        Player bob = mock(Player.class);
        when(bob.getName()).thenReturn("bob");
        World mockWorld = PowerMockito.mock(World.class);
        when(mockWorld.getName()).thenReturn("blacklisted");

        when(bob.getWorld()).thenReturn(mockWorld);

        Block block = mock(Block.class);


        for(Material m : Material.values()){
            if(!m.isBlock()){continue;}

            when(block.getType()).thenReturn(m);

            Byte[] bytes = genUniqueSubIds(m);
            for(byte b : bytes){
                when(block.getData()).thenReturn(b);

                BlockPlaceEvent event = new BlockPlaceEvent(block, null, null, null, bob, true);

                listener.onBlockPlace(event);

                IStat stat = blob.getStat(BeardStat.DEFAULT_DOMAIN, bob.getWorld().getName(), "stats", "totalblockcreate");
                assertFalse("total blocks placed archived",stat.isArchive());
                assertEquals("total blocks count correct", 0, stat.getValue());

                String matName = m.toString().toLowerCase().replace("_", "");
                System.out.println("Testing " + matName);

                String statId = matName;
                if(MetaDataCapture.hasMetaData(m)){
                    statId = matName + "_" + ((int)b);
                }
                stat = blob.getStat(BeardStat.DEFAULT_DOMAIN, bob.getWorld().getName(), "blockcreate", statId);
                assertFalse(statId + " blocks placed archived",stat.isArchive());
                assertEquals(statId + " count",0,stat.getValue());
            }

        }
    }
}
