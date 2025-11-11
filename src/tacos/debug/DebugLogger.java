/*
 * Copyright (C) 2025 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package tacos.debug;

import odin.client.MapleCharacter;
import tacos.config.DeveloperMode;
import tacos.config.Region;
import tacos.config.Version;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import tacos.packet.ClientPacket;

/**
 *
 * @author Riremito
 */
public class DebugLogger {

    // static
    private static String LOG_DIR = "log/";
    private static String LOG_FILE_NAME = "DevLog.txt";
    private static FileWriter fw = null;

    public static void init() {
        try {
            File file = new File(LOG_DIR);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdir();
            }

            fw = new FileWriter(LOG_DIR + LOG_FILE_NAME, true);
            fw.write((getDate() + " Server Reboot - " + Region.getRegion() + " " + Version.getVersion() + "." + Version.getSubVersion() + "\r\n"));
            fw.flush();
        } catch (FileNotFoundException ex) {
            ExceptionLog("DebugLogger - open");
        } catch (IOException ex) {
            ExceptionLog("DebugLogger - first write");
        }
    }

    public static void close() {
        if (fw != null) {
            try {
                fw.close();
            } catch (IOException ex) {
                ExceptionLog("DebugLogger - close");
            }
        }
        fw = null;
    }

    public static boolean DevLog(String text) {
        if (fw == null) {
            return false;
        }
        try {
            if (!DeveloperMode.DM_LOG_DEV.get()) {
                return false;
            }
            fw.write(getDate() + "[DEV]" + text + "\r\n");
            fw.flush();
            return true;
        } catch (IOException ex) {
            ExceptionLog("DebugLogger - write");
        }

        return false;
    }

    private static String getDate() {
        return new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]").format(new Date());
    }

    public static boolean DebugLog(String log_text) {
        if (!DeveloperMode.DM_LOG_DEBUG.get()) {
            return false;
        }
        Log("DEBUG", log_text);
        return true;
    }

    public static boolean DebugLog(MapleCharacter chr, String log_text) {
        if (!DeveloperMode.DM_LOG_DEBUG.get()) {
            return false;
        }
        Log("DEBUG_CHR : \"" + chr.getName() + "\"", log_text);
        return true;
    }

    public static boolean InfoLog(String log_text) {
        if (!DeveloperMode.DM_LOG_INFO.get()) {
            return false;
        }
        Log("INFO", log_text);
        return true;
    }

    // TODO : remove after refactoring all codes.
    public static boolean TestLog(String log_text) {
        Log("TEST", log_text);
        return true;
    }

    public static void ErrorLog(String log_text) {
        Log("ERROR", log_text);
    }

    public static void DBErrorLog(String table_name, String func_name) {
        Log("ERROR_DB", table_name + " : " + func_name);
    }

    public static void ExceptionLog(String log_text) {
        Log("EXCEPTION", log_text);
    }

    public static boolean SetupLog(String log_text) {
        if (!DeveloperMode.DM_LOG_SETUP.get()) {
            return false;
        }
        Log("SETUP", log_text);
        return true;
    }

    public static boolean NetworkLog(String log_text) {
        if (!DeveloperMode.DM_LOG_NETWORK.get()) {
            return false;
        }
        Log("NETWORK", log_text);
        return true;
    }

    public static boolean XmlLog(String log_text) {
        if (!DeveloperMode.DM_LOG_WZ.get()) {
            return false;
        }
        Log("WZ", log_text);
        return true;
    }

    public static boolean AdminLog(String log_text) {
        if (!DeveloperMode.DM_LOG_ADMIN.get()) {
            return false;
        }
        Log("ADMIN", log_text);
        return true;
    }

    public static void CPLog(ClientPacket cp) {
        Log("ClientPacket", cp.GetOpcodeName() + "\n" + cp.get());
    }

    private static void Log(String log_type, String log_text) {
        Log("[" + log_type + "] " + log_text);
    }

    private static void Log(String log_text) {
        // [Date][DEBUG] xxxx
        System.out.println(getDate() + log_text);
    }

}
