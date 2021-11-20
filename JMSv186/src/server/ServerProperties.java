package server;

import debug.Debug;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ServerProperties {

    public static Properties LoadConfig(final String filename) {
        final Properties p = new Properties();

        FileReader fr;
        try {
            fr = new FileReader(filename);
            p.load(fr);
            fr.close();
        } catch (IOException e) {
            Debug.InfoLog("設定ファイルが見つかりません (" + e + ")");
        }

        return p;
    }
}
