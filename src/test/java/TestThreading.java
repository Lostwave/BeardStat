import java.util.ArrayList;
import java.util.List;

import me.tehbeard.BeardStat.containers.PlayerStat;
import me.tehbeard.BeardStat.containers.StaticPlayerStat;

import org.junit.Test;


public class TestThreading {


    public static void main(String[] args){
        final PlayerStat pps = new StaticPlayerStat("cat","stat", 0);

        Runnable r = new Runnable() {
            PlayerStat ps = pps;

            public void run() {
                for(int i =0;i<100;i++){
                    ps.incrementStat(1);
                }
                System.out.println("thread ending, value " + ps.getValue());
            }
        };
        System.out.println("Loading threads");
        List<Thread> t = new ArrayList<Thread>();
        for(int i = 0;i<20;i++){
            Thread tr = new Thread(r);
            t.add(tr);
            tr.start();
        }
        System.out.println("Loaded threads");
        for(Thread tt  : t){
            try {
                tt.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("program ending, value " + pps.getValue());
    }
}
