package server;

import java.sql.SQLException;

import database.DatabaseConnection;
import server.server.Server_CashShop;
import server.server.Server_Game;
import server.server.Server_Login;
import handling.world.World;
import server.Timer.*;

public class ShutdownServer implements Runnable {

    private static final ShutdownServer instance = new ShutdownServer();
    public static boolean running = false;

    public static ShutdownServer getInstance() {
        return instance;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (running) { //Run once!
                return;
            }
            running = true;
        }

        try {
            for (Server_Game cs : Server_Game.getAllInstances()) {
                cs.setShutdown();
            }
            Server_Login.shutdown();
            Integer[] chs = Server_Game.getAllInstance().toArray(new Integer[0]);

            for (int i : chs) {
                try {
                    Server_Game cs = Server_Game.getInstance(i);
                    synchronized (this) {
                        cs.shutdown(this);
//                        try {
//                            this.wait();
//                        } catch (InterruptedException ex) {
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Server_CashShop.shutdown();
            World.Guild.save();
            World.Alliance.save();
            World.Family.save();
            DatabaseConnection.closeAll();
        } catch (SQLException e) {
            System.err.println("THROW" + e);
        }
        WorldTimer.getInstance().stop();
        MapTimer.getInstance().stop();
        MobTimer.getInstance().stop();
        BuffTimer.getInstance().stop();
        CloneTimer.getInstance().stop();
        EventTimer.getInstance().stop();
        EtcTimer.getInstance().stop();

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            //shutdown
        }
        System.exit(0); //not sure if this is really needed for ChannelServer
    }
}
