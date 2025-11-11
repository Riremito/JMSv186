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
package tacos.config.property;

import tacos.debug.DebugLogger;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 *
 * @author Riremito
 */
public class Property {

    private String path;
    private Properties prop = null;

    Property(String path) {
        this.path = path;
    }

    public boolean open() {
        try {
            FileReader fr = new FileReader(path);
            prop = new Properties();

            //prop.load(fr);
            prop.load(new InputStreamReader(new FileInputStream(path), "UTF8"));
            return true;
        } catch (IOException e) {
            // do nothing
        }
        return false;
    }

    public String get(String name) {
        return prop.getProperty(name);
    }

    public String get(String name, String defval) {
        return prop.getProperty(name, defval);
    }

    public int getInt(String name) {
        return Integer.parseInt(get(name));
    }

    public boolean getBoolean(String name) {
        return Boolean.parseBoolean(get(name));
    }

    public static boolean initAll() {
        if (!Property_Java.setPath()) {
            DebugLogger.ErrorLog("Invalid wz_xml or scripts dir.");
            return false;
        }
        if (!Property_Database.init()) {
            DebugLogger.ErrorLog("Property_Database");
            return false;
        }
        if (!Property_Login.init()) {
            DebugLogger.ErrorLog("Property_Login");
            return false;
        }
        if (!Property_Shop.init()) {
            DebugLogger.ErrorLog("Property_Shop");
            return false;
        }
        if (!Property_World.init()) {
            DebugLogger.ErrorLog("Property_World");
            return false;
        }
        if (!Property_Dummy_World.init()) {
            DebugLogger.ErrorLog("Property_Dummy_World");
            return false;
        }
        if (!Property_Pachinko.init()) {
            DebugLogger.ErrorLog("Property_Pachinko");
            return false;
        }
        if (!Property_Packet.init()) {
            DebugLogger.ErrorLog("Property_Packet is not loaded.");
            // allow to run without loading packet header values.
        }
        if (!Property_Debug.init()) {
            DebugLogger.ErrorLog("Property_Debug is not loaded.");
            // dev mode off
        }
        return true;
    }

    public static Properties open(String path) {
        try {
            //FileReader fr = new FileReader(path);
            Properties pr = new Properties();
            //pr.load(fr);
            pr.load(new InputStreamReader(new FileInputStream(path), "UTF8"));
            return pr;
        } catch (IOException e) {
            // do nothing
        }

        return null;
    }

}
