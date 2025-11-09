package server;

import java.sql.SQLException;

import database.DatabaseConnection;
import server.server.ServerOdinGame;
import handling.world.World;
import server.Timer.*;
import server.server.Server;

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
            for (ServerOdinGame cs : ServerOdinGame.getAllInstances()) {
                cs.setShutdown();
            }
            // login, cashshop, game
            for (Server server : Server.get()) {
                server.shutdown();
            }

            Integer[] chs = ServerOdinGame.getAllInstance().toArray(new Integer[0]);

            for (int i : chs) {
                try {
                    ServerOdinGame cs = ServerOdinGame.getInstance(i);
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
