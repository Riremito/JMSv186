/*
    デバッグ系の処理
 */
package debug;

import java.text.SimpleDateFormat;
import java.util.Date;
import packet.ServerPacket;
import packet.ClientPacket;

public class Debug {

    // デバッグ出力
    public static void DebugLog(String text) {
        System.out.println((new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] ").format(new Date())) + text);
    }

    // 情報出力
    public static void InfoLog(String text) {
        System.out.println((new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] ").format(new Date())) + text);
    }

    // パケット出力
    public static void DebugPacket(ClientPacket p) {
        DebugLog("[CP] " + p.Packet());
    }

    public static void DebugInPacket(ServerPacket p) {
        //DebugLog("[DebugInPacket] " + p.Packet());
    }

    public static void DebugSendPacket(byte b[]) {
        if (b.length < 2) {
            return;
        }
        short header = (short) (((short) b[0] & 0xFF) | (((short) b[1] & 0xFF) << 8));
        String text = String.format("@%04X", header);

        for (int i = 2; i < b.length; i++) {
            text += String.format(" %02X", b[i]);
        }
        DebugLog("[OutPacket] " + text);
    }

    public static void DebugProcessPacket(byte b[]) {
        if (b.length < 2) {
            return;
        }
        short header = (short) (((short) b[0] & 0xFF) | (((short) b[1] & 0xFF) << 8));
        String text = String.format("@%04X", header);

        for (int i = 2; i < b.length; i++) {
            text += String.format(" %02X", b[i]);
        }
        DebugLog("[InPacket] " + text);
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
