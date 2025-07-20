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
package data.wz;

import config.ServerConfig;
import config.property.Property_Java;
import java.io.File;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;

/**
 *
 * @author Riremito
 */
public class DW_Quest {

    private static DW_Quest instance = null;

    public static DW_Quest getInstance() {
        if (instance == null) {
            instance = new DW_Quest();
        }
        return instance;
    }

    private MapleDataProvider wz_root = null;
    private MapleData actions = null;
    private MapleData requirements = null;
    private MapleData info = null;
    private MapleData pinfo = null;

    DW_Quest() {
        this.wz_root = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/Quest.wz"));

        this.actions = wz_root.getData("Act.img");
        this.requirements = wz_root.getData("Check.img");
        this.info = wz_root.getData("QuestInfo.img");
        if (ServerConfig.JMS164orLater()) {
            this.pinfo = wz_root.getData("PQuest.img");
        }
    }

    public static MapleData getActions() {
        return getInstance().actions;
    }

    public static MapleData getRequirements() {
        return getInstance().requirements;
    }

    public static MapleData getInfo() {
        return getInstance().info;
    }

    public static MapleData getPinfo() {
        return getInstance().pinfo;
    }

}
