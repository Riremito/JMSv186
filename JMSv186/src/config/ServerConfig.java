package config;

import debug.Debug;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ServerConfig {

    // Database
    public static String database_url, database_user, database_password;
    // Login Server
    public static int login_server_port, login_server_userlimit;
    public static boolean login_server_antihack;
    // Game Server
    public static int game_server_channels, game_server_DEFAULT_PORT, game_server_flags;
    public static int game_server_expRate, game_server_mesoRate, game_server_dropRate;
    public static String game_server_serverName, game_server_serverMessage, game_server_event, game_server_events;
    public static boolean game_server_custom, game_server_adminOnly;
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

    // propertiesファイルの読み込み
    public static void SetProperty() {
        Properties DataBase = ReadPropertyFile("properties/database.properties");
        {
            database_url = DataBase.getProperty("database.url");
            database_user = DataBase.getProperty("database.user");
            database_password = DataBase.getProperty("database.password");
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
            game_server_custom = Boolean.parseBoolean(GameServer.getProperty("server.custom"));
            game_server_event = GameServer.getProperty("server.event");
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
    }

    public static Properties ReadPropertyFile(final String path) {
        final Properties p = new Properties();

        FileReader fr;
        try {
            fr = new FileReader(path);
            p.load(fr);
            fr.close();
        } catch (IOException e) {
            Debug.InfoLog("設定ファイルが見つかりません (" + path + ")");
        }

        return p;
    }
}
