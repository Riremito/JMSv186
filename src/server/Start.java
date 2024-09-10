package server;

import client.data.ExpTable;
import config.DebugConfig;
import config.ServerConfig;
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
import packet.client.request.PacketFlag;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.PlayerNPC;
import test.ToolMan;
import wz.LoadData;

public class Start {

    public final static void main(final String args[]) {
        // バージョン設定
        if (args.length >= 2) {
            ServerConfig.SetVersion(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }

        // 他言語版
        if (args.length >= 3) {
            ServerConfig.SetRegionNumber(Integer.parseInt(args[2]));
        }

        // バージョンによるコンテンツの有無を設定
        ServerConfig.SetContentFlag();

        // 設定の読み込み
        ServerConfig.SetDataPath();
        ServerConfig.SetProperty();
        LoginServer.SetWorldConfig();

        // 管理画面
        ToolMan.Open();

        Debug.InfoLog(ServerConfig.GetRegionName() + " v" + ServerConfig.GetVersion() + "." + ServerConfig.GetSubVersion());
        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() == 131) {
            ServerConfig.SetPacketEncryption(false);
        }

        ExpTable.Init();
        PacketFlag.Update();

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
        if (!DebugConfig.do_not_load_wz_xml) {
            LoadData.LoadDataFromXML();
        }
        RandomRewards.getInstance();

        MapleOxQuizFactory.getInstance().initialize();
        MapleGuildRanking.getInstance().getRank();
        MapleFamilyBuff.getBuffEntry();

        Debug.InfoLog("Start Game Server");
        ChannelServer.startChannel_Main();

        Debug.InfoLog("Start Cash Shop Server");
        CashShopServer.run_startup_configurations();
        MTSStorage.load();

        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PlayerNPC.loadAll();// touch - so we see database problems early...
        World.registerRespawn();
        LoginServer.setOn(); //now or later
        RankingWorker.getInstance().run();
        Debug.InfoLog("OK");
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            new Thread(ShutdownServer.getInstance()).start();
        }
    }
}
