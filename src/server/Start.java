package server;

import config.ClientEdit;
import config.CodePage;
import config.Content;
import config.DeveloperMode;
import config.Region;
import config.Version;
import config.property.Property;
import data.client.DC_Exp;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.world.World;
import java.sql.SQLException;
import database.query.DQ_Accounts;
import debug.Debug;
import debug.DebugLogger;
import handling.world.family.MapleFamilyBuff;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.network.MapleAESOFB;
import test.ToolMan;

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
        // AES
        MapleAESOFB.setAesKey();
        // update content flags
        Debug.InfoLog("[Content]");
        Content.init();
        //Content.showContentList();
        // update client edit flags
        Debug.InfoLog("[ClientEdit]");
        ClientEdit.init();
        // update exp table
        Debug.InfoLog("[ExpTable]");
        DC_Exp.init();
        // update packet enum values
        Debug.InfoLog("[PacketOps]");
        packet.ops.PacketOps.initAll();
        // read properties
        Debug.InfoLog("[Properties]");
        if (!Property.initAll()) {
            return;
        }
        //Debug.InfoLog("wz_xml directory : " + Property_Java.getDir_WzXml());
        //Debug.InfoLog("scripts directory : " + Property_Java.getDir_Scripts());
        // set codepage
        Debug.InfoLog("[CodePage]");
        CodePage.init();
        // ログインサーバー上のゲームサーバー情報
        LoginServer.SetWorldConfig(); // TODO : fix
        // database
        DQ_Accounts.resetLoginState();
        // 管理画面
        Debug.InfoLog("[AdminTool]");
        if (DeveloperMode.DM_ADMIN_TOOL.get()) {
            ToolMan.Open();
            Debug.InfoLog("Admin Tool Opened.");
        }

        World.init();

        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();

        Debug.InfoLog("Start Login Server");
        LoginServer.run_startup_configurations();
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
