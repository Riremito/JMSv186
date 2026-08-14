/*
 * Copyright (C) 2026 Riremito
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeMap;
import odin.provider.IMapleData;
import odin.provider.WzXML.XMLDomMapleData;
import tacos.debug.DebugLogger;
import tacos.property.Property_Java;

/**
 *
 * @author Riremito
 */
public class ServerImg {

    public static final ServerImg SI = new ServerImg();

    public ServerImg() {
        setWzRoot();
    }

    boolean loaded = false;

    public boolean isLoaded() {
        return this.loaded;
    }

    // 船
    public IMapleData getContinent() {
        return getData("Continent.img");
    }

    // イベント
    public IMapleData getFieldSet() {
        return getData("FieldSet.img");
    }

    // ガチャポン
    public IMapleData getGachapon() {
        return getData("Gachapon.img");
    }

    // NPC商店
    public IMapleData getNpcShop() {
        return getData("NpcShop.img");
    }

    // Reactor
    public IMapleData getReactorAction() {
        return getData("ReactorAction.img");
    }

    // ドロップ
    public IMapleData getReward() {
        return getData("Reward.img");
    }

    private File root_dir;

    private boolean setWzRoot() {
        String path = Property_Java.getDir_WzXml_BMS8() + "/";
        File file = new File(path);

        if (!file.exists()) {
            return false;
        }

        if (!file.isDirectory()) {
            return false;
        }

        DebugLogger.XmlLog("setWzRoot(S) : " + path);
        this.root_dir = file;
        this.loaded = true;
        return true;
    }

    private TreeMap<String, IMapleData> xml_cache = new TreeMap<>();

    private IMapleData getData(String data_path) {
        IMapleData data = this.xml_cache.get(data_path);
        if (data != null) {
            return data;
        }

        File dataFile = new File(this.root_dir, data_path + ".xml");
        if (!dataFile.exists()) {
            return null;
        }

        File imageDataDir = new File(this.root_dir, data_path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dataFile);
        } catch (FileNotFoundException ex) {
        }

        if (fis == null) {
            this.xml_cache.put(data_path, null);
            return null;
        }

        XMLDomMapleData domMapleData = new XMLDomMapleData(fis, imageDataDir.getParentFile());
        try {
            fis.close();
        } catch (IOException ex) {
        }

        this.xml_cache.put(data_path, domMapleData);
        DebugLogger.XmlLog("getData(S) : " + data_path);
        return domMapleData;
    }
}
