/*
    デバッグ系の処理
 */
package debug;

import client.MapleClient;
import config.DebugConfig;
import java.text.SimpleDateFormat;
import java.util.Date;
import packet.ServerPacket;
import packet.ClientPacket;

public class Debug {

    private static void Log(String text) {
        System.out.println((new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] ").format(new Date())) + text);
    }

    // デバッグ出力
    public static boolean DebugLog(String text) {
        if (!DebugConfig.log_debug) {
            return false;
        }

        Log("[DEBUG] " + text);
        return true;
    }

    // 情報出力
    public static void InfoLog(String text) {
        Log("[INFO] " + text);
    }

    // エラー出力
    public static void ErrorLog(String text) {
        Log("[ERROR] " + text);
    }

    public static boolean UserDebugLog(MapleClient c, String text) {
        return DebugLog("[" + text + "] [Character = \"" + c.getPlayer().getName() + "\"]");
    }

    public static void UserInfoLog(MapleClient c, String text) {
        InfoLog("[" + text + "] [Character = \"" + c.getPlayer().getName() + "\"]");
    }

    public static void UserErrorLog(MapleClient c, String text) {
        ErrorLog("[" + text + "] [Character = \"" + c.getPlayer().getName() + "\"]");
    }

    // 管理用
    public static boolean AdminLog(String text) {
        if (!DebugConfig.log_admin) {
            return false;
        }

        Log("[ADMIN] " + text);
        return true;
    }

    // パケット出力 (Client Packet)
    public static boolean PacketLog(ClientPacket p) {
        if (!DebugConfig.log_packet) {
            return false;
        }

        Log("[CP][" + p.GetOpcodeName() + "]\n" + p.Packet());
        return true;
    }

    // パケット出力 (Server Packet)
    public static boolean PacketLog(ServerPacket p) {
        if (!DebugConfig.log_packet) {
            return false;
        }

        Log("[SP][" + p.GetOpcodeName() + "]\n" + p.Packet());
        return true;
    }

    // 不要なNPCを設置しない
    private static final int npc_block_list[] = {
        9010000, // イベントガイド
        9010010, // カサンドラ
        9000040, // ダリア (勲章)
        9000041, // 寄付 (勲章)
        1002103, // アール (ファミリーガイド)
        9105009, // ナオミ (メイプルクリスタル)
        9105019, // 助手みどり (調髪)
        9000021, // ガガ (モンスターレイド)
        9102002, // オスト (にわとりイベント)
        9330093, // ビッキィ＆ケッキー (バレンタインイベント)
        9001102, // 月うさぎ (月)
        9120105, // キャサリン (パチンコ)
        9201023, // ナナ (結婚式)
        9000018, // マチルダ (ネットカフェ)
        2101018, // セザール (闘技場)
        2042000, // シュピゲルマン (モンスターカーニバル)
        9250120, // 公衆電話 (ビジター)
        9250121, // ゴミ箱おじさん (ビジター)
        9250123, // ??? (ビジター)
        9250156, // OSSS研究員 (ビジター)
        9250136 // ビンポス (ビジター)
    };

    public static boolean CheckNPCBlock(int npcid, String npcName) {
        // 電光板は不要なので消す
        if (npcName.endsWith("電光板")) {
            return true;
        }
        // 過去のイベント関連のNPCが残存しているので消す
        for (int i = 0; i < npc_block_list.length; i++) {
            if (npc_block_list[i] == npcid) {
                return true;
            }
        }
        return false;
    }
}
