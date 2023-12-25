package server;

import client.data.ExpTable;
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
import packet.*;
import packet.content.PacketFlag;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.PlayerNPC;
import wz.LoadData;

public class Start {

    public final static void main(final String args[]) {
        // バージョン設定
        if (args.length >= 2) {
            ServerConfig.SetVersion(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }

        // 設定の読み込み
        ServerConfig.SetDataPath();
        ServerConfig.SetProperty();
        LoginServer.SetWorldConfig();

        // 管理画面
        tools.admin.main.main();

        Debug.InfoLog("JMS v" + ServerConfig.version + "." + ServerConfig.version_sub);

        switch (ServerConfig.version) {
            case 131: {
                v131_0_CP.Set();
                v131_0_SP.Set();
                break;
            }
            case 164: {
                v164_0_CP.Set();
                v164_0_SP.Set();
                break;
            }
            // test
            case 165: {
                v165_0_CP.Set();
                v165_0_SP.Set();
                break;
            }
            // ゴミ
            case 176: {
                ClientPacket.SetForJMSv176();
                ServerPacket.SetForJMSv176();
                break;
            }
            // ゴミ
            case 184: {
                ClientPacket.SetForJMSv184();
                ServerPacket.SetForJMSv184();
                break;
            }
            case 185:
            case 186: {
                v186_1_CP.Set();
                v186_1_SP.Set();

                ClientPacket.SetCustomHeader();
                ServerPacket.SetCustomHeader();
                break;
            }
            // ゴミ
            case 187: {
                ClientPacket.SetForJMSv187();
                ServerPacket.SetForJMSv187();
                break;
            }
            // クラッシュ確率が下がったが、画面崩壊の影響で遅い
            case 188: {
                v188_0_CP.Set();
                v188_0_SP.Set();
                break;
            }
            // そこそこ安定して動作する
            case 194: {
                v194_0_CP.Set();
                v194_0_SP.Set();
                break;
            }
            // 起動まぁまぁ早い
            case 201: {
                v201_0_CP.Set();
                v201_0_SP.Set();
                break;
            }
            // 起動が遅い
            case 302: {
                ClientPacket.SetForJMSv302();
                ServerPacket.SetForJMSv302();
                break;
            }
            // x64 test
            case 414: {
                break;
            }
            default: {
                Debug.ErrorLog("the version is not supported!");
                return;
            }
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
        LoadData.LoadDataFromXML();
        RandomRewards.getInstance();

        MapleOxQuizFactory.getInstance().initialize();
        MapleGuildRanking.getInstance().getRank();
        MapleFamilyBuff.getBuffEntry();

        Debug.InfoLog("Start Game Server");
        ChannelServer.startChannel_Main();

        CashItemFactory.getInstance().initialize();

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
