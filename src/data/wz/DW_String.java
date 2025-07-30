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

import debug.Debug;
import provider.MapleData;
import provider.MapleDataProvider;

/**
 *
 * @author Riremito
 */
public class DW_String {

    private static DataWz wz = null;

    private static DataWz getWz() {
        if (wz == null) {
            wz = new DataWz("String.wz");
        }
        return wz;
    }

    private static MapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }

    private static MapleData checkSubDirectory(MapleData md, String dir_name) {
        MapleData sub_dir = md.getChildByPath(dir_name);
        if (sub_dir != null) {
            Debug.XmlLog("SubDir OK : " + dir_name);
            return sub_dir;
        }
        return md;
    }

    private static MapleData img_Mob = null;
    private static MapleData img_Npc = null;
    private static MapleData img_Map = null;
    private static MapleData img_Skill = null;

    public static MapleData getMob() {
        if (img_Mob == null) {
            img_Mob = getWz().loadData("Mob.img");
        }
        return img_Mob;
    }

    public static MapleData getNpc() {
        if (img_Npc == null) {
            img_Npc = getWz().loadData("Npc.img");
        }
        return img_Npc;
    }

    public static MapleData getMap() {
        if (img_Map == null) {
            img_Map = getWz().loadData("Map.img");
        }
        return img_Map;
    }

    public static MapleData getSkill() {
        if (img_Skill == null) {
            img_Skill = getWz().loadData("Skill.img");
        }
        return img_Skill;
    }

    private static MapleData img_Item = null; // JMS131
    private static MapleData img_Cash = null;
    private static MapleData img_Consume = null;
    private static MapleData img_Eqp = null;
    private static MapleData img_Etc = null;
    private static MapleData img_Ins = null;
    private static MapleData img_Pet = null;

    public static MapleData getItem() {
        if (img_Item == null) {
            img_Item = getWz().loadData("Item.img");
        }
        return img_Item;
    }

    public static MapleData getCash() {
        if (img_Cash == null) {
            if (getItem() == null) {
                img_Cash = getWz().loadData("Cash.img");
                img_Cash = checkSubDirectory(img_Cash, "Cash");
                return img_Cash;
            }
            img_Cash = checkSubDirectory(getItem(), "Cash");
        }
        return img_Cash;
    }

    public static MapleData getConsume() {
        if (img_Consume == null) {
            if (getItem() == null) {
                img_Consume = getWz().loadData("Consume.img");
                img_Consume = checkSubDirectory(img_Consume, "Con");
                return img_Consume;
            }
            img_Consume = checkSubDirectory(getItem(), "Con");
        }
        return img_Consume;
    }

    public static MapleData getEqp() {
        if (img_Eqp == null) {
            if (getItem() == null) {
                img_Eqp = getWz().loadData("Eqp.img");
                img_Eqp = checkSubDirectory(img_Eqp, "Eqp");
                return img_Eqp;
            }
            img_Eqp = checkSubDirectory(getItem(), "Eqp");
        }
        return img_Eqp;
    }

    public static MapleData getEtc() {
        if (img_Etc == null) {
            if (getItem() == null) {
                img_Etc = getWz().loadData("Etc.img");
                img_Etc = checkSubDirectory(img_Etc, "Etc");
                return img_Etc;
            }
            img_Etc = checkSubDirectory(getItem(), "Etc");
        }
        return img_Etc;
    }

    public static MapleData getIns() {
        if (img_Ins == null) {
            if (getItem() == null) {
                img_Ins = getWz().loadData("Ins.img");
                img_Ins = checkSubDirectory(img_Ins, "Ins");
                return img_Ins;
            }
            img_Ins = checkSubDirectory(getItem(), "Ins");
        }
        return img_Ins;
    }

    public static MapleData getPet() {
        if (img_Pet == null) {
            // please do not use old PetDialog.img (Pet.img)
            if (getItem() == null) {
                img_Pet = getWz().loadData("Pet.img");
                img_Pet = checkSubDirectory(img_Pet, "Pet");
                return img_Pet;
            }
            img_Pet = checkSubDirectory(getItem(), "Pet");
        }
        return img_Pet;
    }

}
