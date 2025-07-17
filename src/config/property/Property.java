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
package config.property;

import java.io.FileReader;
import java.io.IOException;
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
            prop.load(fr);
            return true;
        } catch (IOException e) {
            // do nothing
        }
        return false;
    }

    public String get(String name) {
        return prop.getProperty(name);
    }

    public int getInt(String name) {
        return Integer.parseInt(get(name));
    }

    public boolean getBoolean(String name) {
        return Boolean.parseBoolean(get(name));
    }

    public static Properties open(String path) {
        try {
            FileReader fr = new FileReader(path);
            Properties pr = new Properties();
            pr.load(fr);
            return pr;
        } catch (IOException e) {
            // do nothing
        }

        return null;
    }

}
