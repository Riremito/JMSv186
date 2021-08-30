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
import handling.world.family.MapleFamilyBuff;
import java.sql.PreparedStatement;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.PlayerNPC;
import server.quest.MapleQuest;

public class Start {

    public static void TestServer() {
        System.out.println("テストサーバー");
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
        MapleServerHandler.registerMBean();
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
        System.out.println("起動完了");
    }

    public final static void main(final String args[]) {
        for (String arg : args) {
            System.out.println(arg);
            if (arg.equals("test")) {
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

        System.out.println("[Loading World]");
        World.init();
        System.out.println("[World Initialized]");

        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        LoginInformationProvider.getInstance();

        System.out.println("[Loading Login]");
        LoginServer.run_startup_configurations();
        System.out.println("[Login Initialized]");

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

        MapleServerHandler.registerMBean();

        System.out.println("[Loading Channel]");
        ChannelServer.startChannel_Main();
        System.out.println("[Channel Initialized]");

        CashItemFactory.getInstance().initialize();

        System.out.println("[Loading CS]");
        CashShopServer.run_startup_configurations();
        MTSStorage.load();
        System.out.println("[CS Initialized]");

        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerNPC.loadAll();// touch - so we see database problems early...
        World.registerRespawn();
        //ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop(); //start it off
        LoginServer.setOn(); //now or later
        System.out.println("[Fully Initialized]");
//        RankingWorker.getInstance().run();
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            new Thread(ShutdownServer.getInstance()).start();
        }
    }
}
