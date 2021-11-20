/*
    デバッグ系の処理
 */
package debug;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Debug {

    // デバッグ出力
    public static void DebugLog(String text) {
        System.out.println((new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] ").format(new Date())) + text);
    }

    // 情報出力
    public static void InfoLog(String text) {
        System.out.println((new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] ").format(new Date())) + text);
    }
}
