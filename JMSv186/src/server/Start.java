package server;

import client.SkillFactory;
import handling.MapleServerHandler;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.login.LoginInformationProvider;
import handling.world.World;
import java.sql.SQLException;
import database.DatabaseConnection;
import debug.Debug;
import handling.world.family.MapleFamilyBuff;
import java.sql.PreparedStatement;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.PlayerNPC;
import server.quest.MapleQuest;

public class Start {

    public static void TestServer() {
        // 設定ファイルの読み込み
        DatabaseConnection.LoadConfig();
        LoginServer.LoadConfig();
        LoginServer.SetWorldConfig();
        CashShopServer.LoadConfig();
        ChannelServer.LoadConfig("momiji");
        World.init();
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        //WZ
        MapleQuest.initQuests();
        MapleLifeFactory.loadQuestCounts();
        ItemMakerFactory.getInstance();
        MapleItemInformationProvider.getInstance().load();
        RandomRewards.getInstance();
        SkillFactory.getSkill(99999999);
        MapleOxQuizFactory.getInstance().initialize();
        MapleCarnivalFactory.getInstance().initialize();
        MapleGuildRanking.getInstance().getRank();
        MapleFamilyBuff.getBuffEntry();
        Debug.InfoLog("Start Game Server");
        ChannelServer.startChannel_Main();
        CashItemFactory.getInstance().initialize();
        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerNPC.loadAll();// touch - so we see database problems early...
        World.registerRespawn();
        Debug.InfoLog("Running...");
    }

    public final static void main(final String args[]) {
        Debug.InfoLog("Starting...");
        for (String arg : args) {
            if (arg.equals("test")) {
                Debug.InfoLog("Mode = " + arg);
                TestServer();
                return;
            }
        }
        // 設定ファイルの読み込み
        DatabaseConnection.LoadConfig();
        LoginServer.LoadConfig();
        LoginServer.SetWorldConfig();
        CashShopServer.LoadConfig();
        ChannelServer.LoadConfig("kaede");

        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
        }

        World.init();

        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        LoginInformationProvider.getInstance();

        Debug.InfoLog("Start Login Server");
        LoginServer.run_startup_configurations();

        //WZ
        MapleQuest.initQuests();
        MapleLifeFactory.loadQuestCounts();
        ItemMakerFactory.getInstance();
        MapleItemInformationProvider.getInstance().load();
        RandomRewards.getInstance();
        SkillFactory.getSkill(99999999);
        MapleOxQuizFactory.getInstance().initialize();
        MapleCarnivalFactory.getInstance().initialize();
        MapleGuildRanking.getInstance().getRank();
        MapleFamilyBuff.getBuffEntry();

        Debug.InfoLog("Start Game Server");
        ChannelServer.startChannel_Main();

        CashItemFactory.getInstance().initialize();

        Debug.InfoLog("Start PointShop Server");
        CashShopServer.run_startup_configurations();
        MTSStorage.load();

        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerNPC.loadAll();// touch - so we see database problems early...
        World.registerRespawn();
        LoginServer.setOn(); //now or later

        Debug.InfoLog("Login Server is opened");
        Debug.InfoLog("Running...");
//        RankingWorker.getInstance().run();
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            new Thread(ShutdownServer.getInstance()).start();
        }
    }
}
