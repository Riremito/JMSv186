package server;

import config.ClientEdit;
import config.Content;
import config.DebugConfig;
import config.property.Property_Java;
import config.Region;
import config.ServerConfig;
import config.Version;
import config.property.Property_Database;
import data.client.DC_Exp;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.login.LoginInformationProvider;
import handling.world.World;
import java.sql.SQLException;
import database.DatabaseConnection;
import debug.Debug;
import debug.DebugLogger;
import handling.world.family.MapleFamilyBuff;
import java.sql.PreparedStatement;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.PlayerNPC;
import test.ToolMan;
import wz.LoadData;

public class Start {

    public final static void main(final String args[]) {
        // default = JMS186.1
        // set region & version
        Debug.InfoLog("[Version]");
        if (3 <= args.length) {
            String server_region = args[0];
            int server_version = Integer.parseInt(args[1]);
            int server_version_sub = Integer.parseInt(args[2]);

            if (!Region.setRegion(server_region)) {
                Debug.ErrorLog("Invalid region name.");
                return;
            }

            Version.setVersion(server_version, server_version_sub);
        }
        Debug.InfoLog(Region.GetRegionName() + " v" + Version.getVersion() + "." + Version.getSubVersion());
        // DevLog
        Debug.InfoLog("[DevLog]");
        DebugLogger.init();
        // TODO : debug config
        // update content flags
        Debug.InfoLog("[Content]");
        Content.init();
        Content.showContentList();
        // update client edit flags
        Debug.InfoLog("[ClientEdit]");
        ClientEdit.init();
        // update exp table
        Debug.InfoLog("[ExpTable]");
        DC_Exp.init();
        // update packet enum values
        Debug.InfoLog("[PacketOps]");
        packet.ops.PacketOps.initAll();
        // path
        Debug.InfoLog("[DataPath]");
        if (!Property_Java.setPath()) {
            Debug.ErrorLog("Invalid wz_xml or scripts dir.");
            return;
        }
        Debug.InfoLog("wz_xml directory : " + Property_Java.getDir_WzXml());
        Debug.InfoLog("scripts directory : " + Property_Java.getDir_Scripts());
        // read properties
        Debug.InfoLog("[Properties]");
        if (!Property_Database.init()) {
            Debug.ErrorLog("Property_Database.");
            return;
        }
        ServerConfig.SetProperty();
        LoginServer.SetWorldConfig();
        // database

        // 管理画面
        ToolMan.Open();

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
        return;
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            new Thread(ShutdownServer.getInstance()).start();
        }
    }
}
