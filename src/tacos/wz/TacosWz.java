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
package tacos.wz;

import tacos.property.Property_Java;
import tacos.debug.DebugLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import odin.provider.IMapleData;
import odin.provider.IMapleDataDirectoryEntry;
import odin.provider.IMapleDataProvider;
import odin.provider.WzXML.WZDirectoryEntry;
import odin.provider.WzXML.WZFileEntry;
import odin.provider.WzXML.XMLDomMapleData;
import tacos.odin.OdinPair;
import tacos.server.ServerOdinGame;

/**
 *
 * @author Riremito
 */
public class TacosWz implements IMapleDataProvider {

    private static List<OdinPair<String, IMapleData>> xml_cache = new ArrayList<>();
    private IMapleDataProvider wz_root = null;
    private String root_path = null;

    private File root_dir;
    private WZDirectoryEntry rootForNavigation;

    public TacosWz(String path) {
        this.root_path = path;
        setWzRoot();
    }

    public IMapleDataProvider getWzRoot() {
        return this.wz_root;
    }

    private boolean setWzRoot() {
        File file = new File(Property_Java.getDir_WzXml() + "/" + this.root_path);

        if (!file.exists()) {
            DebugLogger.XmlLog("setWzRoot : path not found, " + this.root_path);
            return false;
        }

        if (!file.isDirectory()) {
            DebugLogger.XmlLog("setWzRoot : path is not a directory, " + this.root_path);
            return false;
        }

        DebugLogger.XmlLog("setWzRoot : path = " + this.root_path);

        this.wz_root = this;
        this.root_dir = file;
        this.rootForNavigation = new WZDirectoryEntry(this.root_dir.getName(), 0, 0, null);
        createEntry(this.root_dir, this.rootForNavigation);
        return true;
    }

    private void createEntry(File dir, WZDirectoryEntry entry) {
        for (File file : dir.listFiles()) {
            String fn = file.getName();
            if (fn.endsWith(".img")) {
                DebugLogger.XmlLog("what's this1? " + file.getName());
                continue;
            }
            if (file.isDirectory()) {
                WZDirectoryEntry sub_entry = new WZDirectoryEntry(fn, 0, 0, entry);
                entry.addDirectory(sub_entry);
                createEntry(file, sub_entry);
                continue;
            }
            if (fn.endsWith(".xml")) {
                String fn_img = fn.substring(0, fn.length() - 4);
                entry.addFile(new WZFileEntry(fn_img, 0, 0, entry));
                continue;
            }
            DebugLogger.XmlLog("what's this2? " + file.getName());
        }
    }

    private static void addXmlCache(String data_path, IMapleData md) {
        xml_cache.add(new OdinPair<>(data_path, md));
    }

    private static IMapleData getXmlCache(String data_path) {
        for (OdinPair<String, IMapleData> pair : xml_cache) {
            if (pair.getLeft().equals(data_path)) {
                return pair.getRight();
            }
        }
        return null;
    }

    private FileInputStream getCustomData(String data_path) {
        if (!ServerOdinGame.IsCustom()) {
            return null;
        }

        int dir_left = data_path.indexOf("/");
        int dir_right = data_path.indexOf("/", dir_left + 1);
        String path_dir = data_path.substring(dir_left, dir_right + 1);
        String pathCustom = data_path;
        pathCustom = pathCustom.replace(path_dir, "/Custom/");
        File dataFileCustom = new File(this.root_dir, pathCustom + ".xml");

        try {
            FileInputStream fis = new FileInputStream(dataFileCustom);
            DebugLogger.XmlLog("custom wz: " + pathCustom + ".xml");
            return fis;

        } catch (FileNotFoundException ex) {

        }
        DebugLogger.XmlLog("custom wz error: " + pathCustom + ".xml");
        return null;
    }

    @Override
    public IMapleData getData(String data_path) {
        // data is already loaded.
        IMapleData md_cache = getXmlCache(data_path);
        if (md_cache != null) {
            //DebugLogger.XmlLog("getData : cached, " + path);
            return md_cache;
        }
        // new data.
        DebugLogger.XmlLog("getData : " + data_path);

        File dataFile = new File(this.root_dir, data_path + ".xml");
        if (!dataFile.exists()) {
            DebugLogger.XmlLog("getData : " + data_path + ".xml");
            return null;
        }
        File imageDataDir = new File(this.root_dir, data_path);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dataFile);
        } catch (FileNotFoundException ex) {
            if (ServerOdinGame.IsCustom()) {
                fis = getCustomData(data_path);
            }
        }

        // not found.
        if (fis == null) {
            addXmlCache(data_path, null);
            return null;
        }

        XMLDomMapleData domMapleData = new XMLDomMapleData(fis, imageDataDir.getParentFile());

        try {
            fis.close();
        } catch (IOException ex) {
        }

        addXmlCache(data_path, domMapleData);
        return domMapleData;
    }

    @Override
    public IMapleDataDirectoryEntry getRoot() {
        return this.rootForNavigation;
    }

    public IMapleData loadData(String path) {
        //DebugLogger.XmlLog("loadData = " + this.root_path + "/" + path);
        return this.wz_root.getData(path);
    }

}
