package config;

import debug.Debug;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import packet.ClientPacket;
import packet.ServerPacket;

public class ServerConfig {

    // 初期スロット数
    public static final byte DEFAULT_INV_SLOT_EQUIP = 72;
    public static final byte DEFAULT_INV_SLOT_USE = 72;
    public static final byte DEFAULT_INV_SLOT_ETC = 24;
    public static final byte DEFAULT_INV_SLOT_SETUP = 24;
    public static final byte DEFAULT_INV_SLOT_CASH = 96;
    private static final byte DEFAULT_INV_SLOT_STORAGE = 4;

    public static boolean JMS146orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Region.getRegion()) {
            case JMS: {
                if (146 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }

            case KMS: {
                if (47 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (62 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (73 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (61 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case BMS: {
                if (24 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case VMS: {
                if (35 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean JMS147orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Region.getRegion()) {
            case JMS: {
                if (147 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (48 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (63 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (74 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (62 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // only 5 jobs
    // シグナス実装前まではほぼ変わらないはずなのでバージョンの誤差は多少あっても問題ない
    public static boolean JMS164orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Region.getRegion()) {
            case JMS: {
                if (164 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                // v2.66
                if (65 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (73 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (94 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (72 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (54 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case VMS: {
                return false;
            }
        }
        return false;
    }

    // Knights of Cygnus update
    public static boolean JMS165orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Region.getRegion()) {
            case JMS: {
                if (165 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (67 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (74 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (96 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (73 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (55 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case VMS: {
                return false;
            }
        }
        return false;
    }

    // stable pre bb
    public static boolean JMS180orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Region.getRegion()) {
            case JMS: {
                if (180 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (92 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (85 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (121 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (91 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (70 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // pre bb with potential
    public static boolean JMS186orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Region.getRegion()) {
            case JMS: {
                if (186 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (85 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (121 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (91 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (70 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS114orLater() {
        // ?
        if (Version.PostBB()) {
            return true;
        }

        switch (Region.getRegion()) {
            case KMS: {
                if (114 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // near Chaos update
    public static boolean JMS194orLater() {
        if (!Version.PostBB()) {
            return false;
        }

        switch (Region.getRegion()) {
            case JMS: {
                if (194 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case JMST: {
                if (110 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (114 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (76 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS118orLater() {
        switch (Region.getRegion()) {
            case KMS: {
                if (118 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS119orLater() {
        switch (Region.getRegion()) {
            case KMS: {
                if (119 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS121orLater() {
        switch (Region.getRegion()) {
            case KMS: {
                if (121 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS127orLater() {
        switch (Region.getRegion()) {
            case KMS: {
                if (127 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean KMS138orLater() {
        switch (Region.getRegion()) {
            case KMS: {
                if (138 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // 2012/08/13
    public static boolean TWMS148orLater() {
        switch (Region.getRegion()) {
            case TWMS: {
                if (148 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // 2012/08/17
    public static boolean CMS104orLater() {
        switch (Region.getRegion()) {
            case CMS: {
                if (104 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Sengoku update
    // 2012/08/16
    public static boolean JMS302orLater() {
        switch (Region.getRegion()) {
            case JMS: {
                if (302 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Angelic Buster version
    public static boolean JMS308orLater() {
        switch (Region.getRegion()) {
            case JMS: {
                if (308 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // test version of potential system
    public static boolean IsPrePotentialVersion() {
        switch (Region.getRegion()) {
            case JMS: {
                if (184 <= Version.getVersion() && Version.getVersion() <= 185) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (95 == Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // around Pirate update
    public static boolean JMS131orEarlier() {
        if (Version.PostBB()) {
            return false;
        }

        switch (Region.getRegion()) {
            case JMS: {
                // not checked v132 to v163
                if (Version.getVersion() <= 131) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Knights of Cygnus update
    // todo : replace to orlater func
    public static boolean JMS165orEarlier() {
        if (Version.PostBB()) {
            return false;
        }

        switch (Region.getRegion()) {
            case JMS: {
                if (Version.getVersion() <= 165) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (Version.getVersion() <= 94) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (Version.getVersion() <= 73) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (Version.getVersion() <= 55) {
                    return true;
                }
                return false;
            }
            case BMS: {
                if (Version.getVersion() <= 24) {
                    return true;
                }
                return false;
            }
            case VMS: {
                if (Version.getVersion() <= 35) {
                    return true;
                }
                return false;
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
            // jdbc:mysql://127.0.0.1:3306/jms_v186?autoReconnect=true&characterEncoding=utf8
            database_url = DataBase.getProperty("database.url");
            if (database_url.isEmpty()) {
                String database_host = DataBase.getProperty("database.host");
                String database_port = DataBase.getProperty("database.port");
                database_url = "jdbc:mysql://" + database_host + ":" + database_port + "/" + Region.GetRegionName() + "_v" + Version.getVersion() + "?autoReconnect=true&characterEncoding=utf8";
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

        Properties ServerPacketHeader = ReadPropertyFile("properties/packet/" + Region.GetRegionName() + "_v" + Version.getVersion() + "_ServerPacket.properties");
        if (ServerPacketHeader != null) {
            ServerPacket.init(ServerPacketHeader);

            //Debug.DebugLog("[SP]");
            for (ServerPacket.Header header : ServerPacket.Header.values()) {
                int val = header.get();
                if (val != -1) {
                    //Debug.DebugLog(String.format("@%04X", val) + " : " + header.name());
                }
            }
        }

        Properties ClientPacketHeader = ReadPropertyFile("properties/packet/" + Region.GetRegionName() + "_v" + Version.getVersion() + "_ClientPacket.properties");
        if (ClientPacketHeader != null) {
            ClientPacket.Load(ClientPacketHeader);

            //Debug.DebugLog("[CP]");
            for (ClientPacket.Header header : ClientPacket.Header.values()) {
                int val = header.Get();
                if (val != -1) {
                    //Debug.DebugLog(String.format("@%04X", val) + " : " + header.name());
                }
            }
        }
    }

    public static void ReloadHeader() {
        Properties ServerPacketHeader = ReadPropertyFile("properties/packet/" + Region.GetRegionName() + "_v" + Version.getVersion() + "_ServerPacket.properties");
        if (ServerPacketHeader != null) {
            ServerPacket.reset();
            ServerPacket.init(ServerPacketHeader);
            Debug.InfoLog("ServerPacket is reloaded!");
        }
        Properties ClientPacketHeader = ReadPropertyFile("properties/packet/" + Region.GetRegionName() + "_v" + Version.getVersion() + "_ClientPacket.properties");
        if (ClientPacketHeader != null) {
            ClientPacket.Reset();
            ClientPacket.Load(ClientPacketHeader);
            Debug.InfoLog("ClientPacket is reloaded!");
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
