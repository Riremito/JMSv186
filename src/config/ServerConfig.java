package config;

import debug.Debug;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import packet.client.ClientPacket;
import packet.server.ServerPacket;

public class ServerConfig {

    private static boolean packet_encryption = true;

    public static void SetPacketEncryption(boolean encryption_flag) {
        packet_encryption = encryption_flag;
    }

    public static boolean PacketEncryptionEnabled() {
        return packet_encryption;
    }

    public enum Region {
        KMS,
        JMS,
        CMS,
        TWMS,
        MSEA,
        GMS,
        EMS,
        unk,
    }

    public static boolean IsKMS() {
        return GetRegion() == Region.KMS;
    }

    public static boolean IsJMS() {
        return GetRegion() == Region.JMS;
    }

    public static boolean IsCMS() {
        return GetRegion() == Region.CMS;
    }

    public static boolean IsTWMS() {
        return GetRegion() == Region.TWMS;
    }

    public static boolean IsMSEA() {
        return GetRegion() == Region.MSEA;
    }

    private static boolean job_pirate = true;
    private static boolean job_KOC = true;
    private static boolean job_Aran = true;
    private static boolean job_Evan = true;
    private static boolean job_DB = true;
    private static boolean job_Resistance = true;
    private static boolean is_postBB = false;

    public static boolean IsPostBB() {
        return is_postBB;
    }

    public static boolean IsPreBB() {
        return !IsPostBB();
    }

    public static boolean SetContentFlag() {
        switch (GetRegion()) {
            case KMS: {
                if (101 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case JMS: {
                if (187 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case CMS: {
                if (87 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case TWMS: {
                if (123 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case MSEA: {
                if (103 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case GMS: {
                if (93 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            case EMS: {
                if (73 <= GetVersion()) {
                    is_postBB = true;
                }
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // codepage
    public static boolean utf8 = false;
    public static Charset codepage_ascii;
    public static Charset codepage_utf8;

    // Version
    private static Region region_name = Region.JMS;
    private static byte region_number = 3; // JMS
    public static int version = 186;
    private static byte version_sub = 1;

    public static byte GetRegionNumber() {
        return region_number;
    }

    public static Region GetRegion() {
        return region_name;
    }

    public static int GetVersion() {
        return version;
    }

    public static int GetSubVersion() {
        return version_sub;
    }

    public static String GetRegionName() {
        return "" + region_name;
    }

    public static boolean SetRegionNumber(int region_num) {
        region_number = (byte) region_num;

        switch (region_number) {
            case 1: {
                region_name = Region.KMS;
                return true;
            }
            case 3: {
                region_name = Region.JMS;
                return true;
            }
            case 4: {
                region_name = Region.CMS;
                return true;
            }
            case 6: {
                region_name = Region.TWMS;
                return true;
            }
            case 7: {
                region_name = Region.MSEA;
                return true;
            }
            case 8: {
                region_name = Region.GMS;
                return true;
            }
            case 9: {
                region_name = Region.EMS;
                return true;
            }
            default: {
                break;
            }
        }

        region_name = Region.unk;
        return false;
    }

    public static void SetVersion(int ver1, int ver2) {
        version = ver1;
        version_sub = (byte) ver2;
    }
    public static String wz_path, script_path;

    // コマンドライン引数からファイルパスを取得
    public static void SetDataPath() {
        wz_path = System.getProperty("riresaba.path.wz");
        script_path = System.getProperty("riresaba.path.script");
    }

    // Database
    public static String database_url, database_user, database_password;
    // Login Server
    public static int login_server_port, login_server_userlimit;
    public static boolean login_server_antihack;
    // Game Server
    public static int game_server_channels, game_server_DEFAULT_PORT, game_server_flags;
    public static int game_server_expRate, game_server_mesoRate, game_server_dropRate;
    public static String game_server_serverName, game_server_serverMessage, game_server_event, game_server_events;
    public static boolean game_server_adminOnly;
    public static boolean game_server_enable_hammer, game_server_enable_EE, game_server_enable_potential, game_server_enable_mphp;
    public static boolean game_server_custom, game_server_god_equip, game_server_disable_scroll_boom, game_server_disable_scroll_failure, game_server_disable_star_consuming, game_server_disable_stone_consuming, game_server_disable_boss_timer;
    // Cash Shop
    public static int cash_shop_server_port;
    // Maple Trade Space Server
    public static int maple_trade_space_server_port;
    // Test Game Server
    public static int test_game_server_channels, test_game_server_DEFAULT_PORT, test_game_server_flags;
    public static String test_game_server_serverName, test_game_server_serverMessage, test_game_server_event;
    // Game Server - Pachinko
    public static String 豆豆装备[], 豆豆坐骑[], 消耗品[], 黄金狗几率[], 小白怪[], 大白怪[], 紫色怪[], 粉色怪[], 飞侠[], 海盗[], 法师[], 战士[], 弓箭手[], 女皇[], 白怪奖励[], 色怪奖励[], 五职业奖励[], 女皇奖励[];
    public static int 海洋帽子几率, 力度搞假, 豆豆奖励范围;

    // キャラクター作成後の最初のMapID
    public static int first_mapid = 910000000; // フリーマーケット入口
    // 存在しないMapIDへ飛んでしまった場合に強制的にワープさせる場所
    public static int error_mapid = 800000000; // キノコ神社

    // propertiesファイルの読み込み
    public static void SetProperty() {
        Properties DataBase = ReadPropertyFile("properties/database.properties");
        {
            // jdbc:mysql://127.0.0.1:3306/v186?autoReconnect=true&characterEncoding=utf8
            database_url = DataBase.getProperty("database.url");
            if (database_url.isEmpty()) {
                String database_host = DataBase.getProperty("database.host");
                String database_port = DataBase.getProperty("database.port");
                if (IsJMS()) {
                    database_url = "jdbc:mysql://" + database_host + ":" + database_port + "/v" + version + "?autoReconnect=true&characterEncoding=utf8";
                } else {
                    database_url = "jdbc:mysql://" + database_host + ":" + database_port + "/" + GetRegionName() + "_v" + version + "?autoReconnect=true&characterEncoding=utf8";
                }
            }

            database_user = DataBase.getProperty("database.user");
            database_password = DataBase.getProperty("database.password");
        }

        // test
        Properties TestConfig = ReadPropertyFile("properties/test.properties");
        {
            // codepage
            utf8 = Boolean.parseBoolean(TestConfig.getProperty("codepage.use_utf8"));
            codepage_ascii = Charset.forName(TestConfig.getProperty("codepage.ascii"));
            codepage_utf8 = Charset.forName(TestConfig.getProperty("codepage.utf8"));
            // map
            first_mapid = Integer.parseInt(TestConfig.getProperty("config.first_mapid"));
            error_mapid = Integer.parseInt(TestConfig.getProperty("config.error_mapid"));
            // debug
            DebugConfig.log_packet = Boolean.parseBoolean(TestConfig.getProperty("debug.show_packet"));
            DebugConfig.log_debug = Boolean.parseBoolean(TestConfig.getProperty("debug.show_debug_log"));
            DebugConfig.log_admin = Boolean.parseBoolean(TestConfig.getProperty("debug.show_admin_log"));
            DebugConfig.starter_set = Boolean.parseBoolean(TestConfig.getProperty("debug.starter_set"));
            DebugConfig.GM = Boolean.parseBoolean(TestConfig.getProperty("debug.gm_mode"));
            DebugConfig.open_debug_ui = Boolean.parseBoolean(TestConfig.getProperty("debug.admin_ui"));
        }

        Properties LoginServer = ReadPropertyFile("properties/login.properties");
        {

            login_server_port = Integer.parseInt(LoginServer.getProperty("server.port"));
            login_server_userlimit = Integer.parseInt(LoginServer.getProperty("server.userlimit"));
            login_server_antihack = Boolean.parseBoolean(LoginServer.getProperty("server.antihack"));
        }

        Properties GameServer = ReadPropertyFile("properties/kaede.properties");
        {
            game_server_channels = Integer.parseInt(GameServer.getProperty("server.channels"));
            game_server_DEFAULT_PORT = Short.parseShort(GameServer.getProperty("server.port"));
            game_server_expRate = Integer.parseInt(GameServer.getProperty("server.rate.exp"));
            game_server_mesoRate = Integer.parseInt(GameServer.getProperty("server.rate.meso"));
            game_server_dropRate = Integer.parseInt(GameServer.getProperty("server.rate.drop"));
            game_server_serverMessage = GameServer.getProperty("server.message");
            game_server_serverName = GameServer.getProperty("server.name");
            game_server_flags = Integer.parseInt(GameServer.getProperty("server.flags"));
            game_server_adminOnly = Boolean.parseBoolean(GameServer.getProperty("server.admin", "false"));
            game_server_events = GameServer.getProperty("server.events");
            game_server_event = GameServer.getProperty("server.event");

            game_server_enable_hammer = Boolean.parseBoolean(GameServer.getProperty("server.enable.hammer"));
            game_server_enable_EE = Boolean.parseBoolean(GameServer.getProperty("server.enable.ee"));
            game_server_enable_potential = Boolean.parseBoolean(GameServer.getProperty("server.enable.potential"));
            game_server_enable_mphp = Boolean.parseBoolean(GameServer.getProperty("server.enable.mphp"));

            game_server_custom = Boolean.parseBoolean(GameServer.getProperty("server.custom"));
            game_server_god_equip = Boolean.parseBoolean(GameServer.getProperty("server.custom.god_equip"));
            game_server_disable_scroll_boom = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.scroll_boom"));
            game_server_disable_scroll_failure = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.scroll_failure"));
            game_server_disable_star_consuming = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.star_consuming"));
            game_server_disable_stone_consuming = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.stone_consuming"));
            game_server_disable_boss_timer = Boolean.parseBoolean(GameServer.getProperty("server.custom.disable.boss_timer"));

        }

        Properties CashShopServer = ReadPropertyFile("properties/shop.properties");
        {
            cash_shop_server_port = Integer.parseInt(CashShopServer.getProperty("server.port"));
            // Port共有
            maple_trade_space_server_port = cash_shop_server_port;
        }

        Properties TestGameServer = ReadPropertyFile("properties/momiji.properties");
        {
            test_game_server_channels = Integer.parseInt(TestGameServer.getProperty("server.channels"));
            test_game_server_DEFAULT_PORT = Short.parseShort(TestGameServer.getProperty("server.port"));
            test_game_server_serverMessage = TestGameServer.getProperty("server.message");
            test_game_server_serverName = TestGameServer.getProperty("server.name");
            test_game_server_flags = Integer.parseInt(TestGameServer.getProperty("server.flags"));
            test_game_server_event = TestGameServer.getProperty("server.event");
        }

        Properties Pachinko = ReadPropertyFile("properties/beans.properties");
        {
            豆豆装备 = Pachinko.getProperty("ddzb").split(",");
            豆豆坐骑 = Pachinko.getProperty("ddzq").split(",");
            消耗品 = Pachinko.getProperty("xhp").split(",");
            海洋帽子几率 = Integer.parseInt(Pachinko.getProperty("hymzjl"));
            黄金狗几率 = Pachinko.getProperty("hjgjl").split(",");
            大白怪 = Pachinko.getProperty("dbg").split(",");
            小白怪 = Pachinko.getProperty("xbg").split(",");
            紫色怪 = Pachinko.getProperty("zsg").split(",");
            粉色怪 = Pachinko.getProperty("fsg").split(",");
            飞侠 = Pachinko.getProperty("fx").split(",");
            海盗 = Pachinko.getProperty("hd").split(",");
            法师 = Pachinko.getProperty("fs").split(",");
            战士 = Pachinko.getProperty("zs").split(",");
            弓箭手 = Pachinko.getProperty("gjs").split(",");
            女皇 = Pachinko.getProperty("nh").split(",");
            白怪奖励 = Pachinko.getProperty("bgjl").split(",");
            色怪奖励 = Pachinko.getProperty("sgjl").split(",");
            五职业奖励 = Pachinko.getProperty("wzyjl").split(",");
            女皇奖励 = Pachinko.getProperty("nhjl").split(",");
            力度搞假 = Integer.parseInt(Pachinko.getProperty("ldgj"));
            豆豆奖励范围 = Integer.parseInt(Pachinko.getProperty("ddjlfw"));
        }

        Properties ServerPacketHeader = ReadPropertyFile("properties/packet/" + GetRegionName() + "_v" + GetVersion() + "_ServerPacket.properties");
        if (ServerPacketHeader != null) {
            ServerPacket.Load(ServerPacketHeader);

            Debug.DebugLog("[SP]");
            for (ServerPacket.Header header : ServerPacket.Header.values()) {
                int val = header.Get();
                if (val != -1) {
                    Debug.DebugLog(String.format("@%04X", val) + " : " + header.name());
                }
            }
        }

        Properties ClientPacketHeader = ReadPropertyFile("properties/packet/" + GetRegionName() + "_v" + GetVersion() + "_ClientPacket.properties");
        if (ClientPacketHeader != null) {
            ClientPacket.Load(ClientPacketHeader);

            Debug.DebugLog("[CP]");
            for (ClientPacket.Header header : ClientPacket.Header.values()) {
                int val = header.Get();
                if (val != -1) {
                    Debug.DebugLog(String.format("@%04X", val) + " : " + header.name());
                }
            }
        }
    }

    public static boolean IsGMTestMode() {
        return DebugConfig.GM;
    }

    public static Properties ReadPropertyFile(final String path) {
        final Properties p = new Properties();

        FileReader fr;
        try {
            fr = new FileReader(path);
            p.load(fr);
            fr.close();
        } catch (IOException e) {
            Debug.ErrorLog("設定ファイルが見つかりません (" + path + ")");
            return null;
        }

        return p;
    }

}
