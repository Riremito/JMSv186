package server;

import config.ClientEdit;
import config.CodePage;
import config.Content;
import config.DeveloperMode;
import config.Region;
import config.Version;
import config.property.Property;
import data.client.DC_Exp;
import server.server.ServerOdinGame;
import handling.channel.MapleGuildRanking;
import server.server.ServerOdinLogin;
import handling.world.World;
import java.sql.SQLException;
import database.query.DQ_Accounts;
import debug.DebugLogger;
import handling.world.family.MapleFamilyBuff;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.network.MapleAESOFB;
import server.server.Server_CashShop;
import server.server.Server_Login;
import test.ToolMan;

public class Start {

    public final static void main(final String args[]) {
        // default = JMS186.1
        // set region & version
        DebugLogger.SetupLog("VERSION");
        if (3 <= args.length) {
            String server_region = args[0];
            int server_version = Integer.parseInt(args[1]);
            int server_version_sub = Integer.parseInt(args[2]);

            if (!Region.setRegion(server_region)) {
                DebugLogger.ErrorLog("Invalid region name.");
                return;
            }

            Version.setVersion(server_version, server_version_sub);
        }
        DebugLogger.InfoLog(Region.GetRegionName() + " v" + Version.getVersion() + "." + Version.getSubVersion());
        // DevLog
        DebugLogger.SetupLog("DEV_LOG");
        DebugLogger.init();
        // TODO : debug config
        // AES
        MapleAESOFB.setAesKey();
        // update content flags
        DebugLogger.SetupLog("FLAG_CONTENT");
        Content.init();
        //Content.showContentList();
        // update client edit flags
        DebugLogger.SetupLog("FLAG_CLIENT_EDIT");
        ClientEdit.init();
        // update exp table
        DebugLogger.SetupLog("EXP_TABLE");
        DC_Exp.init();
        // update packet enum values
        DebugLogger.SetupLog("PACKET_OPS");
        packet.ops.PacketOps.initAll();
        // read properties
        DebugLogger.SetupLog("PROPERTIES");
        if (!Property.initAll()) {
            return;
        }
        //Debug.InfoLog("wz_xml directory : " + Property_Java.getDir_WzXml());
        //Debug.InfoLog("scripts directory : " + Property_Java.getDir_Scripts());
        // set codepage
        DebugLogger.SetupLog("CODEPAGE");
        CodePage.init();
        // ログインサーバー上のゲームサーバー情報
        ServerOdinLogin.SetWorldConfig(); // TODO : fix
        // database
        DQ_Accounts.resetLoginState();
        // 管理画面
        if (DeveloperMode.DM_ADMIN_TOOL.get()) {
            DebugLogger.SetupLog("admin tool is opened.");
            ToolMan.Open();
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

        DebugLogger.SetupLog("LOGIN_SERVER");
        // ?_?
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        Server_Login.init();
        RandomRewards.getInstance();

        MapleOxQuizFactory.getInstance().initialize();
        MapleGuildRanking.getInstance().getRank();
        MapleFamilyBuff.getBuffEntry();

        DebugLogger.SetupLog("GAME_SERVER");
        ServerOdinGame.startChannel_Main();

        DebugLogger.SetupLog("CASHSHOP_SERVER");
        Server_CashShop.init();
        MTSStorage.load();

        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        World.registerRespawn();
        DebugLogger.SetupLog("RANKING");
        RankingWorker.getInstance().run();
        DebugLogger.SetupLog("DONE!");
        return;
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            new Thread(ShutdownServer.getInstance()).start();
        }
    }
}
