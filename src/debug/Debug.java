/*
    デバッグ系の処理
 */
package debug;

import client.MapleCharacter;
import config.DeveloperMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import packet.ClientPacket;

public class Debug {

    private static void Log(String text) {
        System.out.println((new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] ").format(new Date())) + text);
    }

    // デバッグ出力
    public static boolean DebugLog(String text) {
        if (!DeveloperMode.DM_DEBUG_LOG.get()) {
            return false;
        }

        Log("[DEBUG] " + text);
        return true;
    }

    public static boolean XmlLog(String text) {
        if (!DeveloperMode.DM_XML_LOG.get()) {
            return false;
        }

        Log("[XML] " + text);
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

    public static void ExceptionLog(String text) {
        Log("[EXCEPTION] " + text);
    }

    public static void DebugLog(MapleCharacter chr, String text) {
        Log("[DEBUG_CHR][\"" + chr.getName() + "\"] " + text);
    }

    // 管理用
    public static boolean AdminLog(String text) {
        if (!DeveloperMode.DM_ADMIN_LOG.get()) {
            return false;
        }

        Log("[ADMIN] " + text);
        return true;
    }

    public static void CPLog(ClientPacket cp) {
        Log("[CP][" + cp.GetOpcodeName() + "]\n" + cp.Packet());
    }

}
