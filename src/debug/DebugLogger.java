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
package debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

            fw = new FileWriter(LOG_DIR + LOG_FILE_NAME);
            fw.write((getDate() + " Server Reboot\r\n"));
        } catch (FileNotFoundException ex) {
            Debug.ExceptionLog("DebugLogger - open");
        } catch (IOException ex) {
            Debug.ExceptionLog("DebugLogger - first write");
        }
    }

    private static String getDate() {
        return new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]").format(new Date());
    }

    public static void DevLog(String text) {
        if (fw == null) {
            return;
        }
        try {
            fw.write(getDate() + "[DEV]" + text + "\r\n");
            fw.flush();
        } catch (IOException ex) {
            Debug.ExceptionLog("DebugLogger - write");
        }
    }

}
