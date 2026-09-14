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

import tacos.config.Content;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Riremito
 */
public class StringWz extends WzXML {

    public StringWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/String" : "String.wz");
    }

    private MapleData checkSubDirectory(MapleData md, String dir_name) {
        MapleData sub_dir = md.getChildByPath(dir_name);
        if (sub_dir != null) {
            DebugLogger.XmlLog("SubDir OK : " + dir_name);
            return sub_dir;
        }
        return md;
    }

    public MapleData getItemStringData(final int itemId) {
        String cat = null;
        MapleData data;

        if (itemId >= 5010000) {
            data = getCash();
        } else if (itemId >= 2000000 && itemId < 3000000) {
            data = getConsume();
        } else if ((itemId >= 1142000 && itemId < 1143000) || (itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1123000)) {
            data = getEqp();
            cat = "Accessory";
        } else if (itemId >= 1000000 && itemId < 1010000) {
            data = getEqp();
            cat = "Cap";
        } else if (itemId >= 1102000 && itemId < 1103000) {
            data = getEqp();
            cat = "Cape";
        } else if (itemId >= 1040000 && itemId < 1050000) {
            data = getEqp();
            cat = "Coat";
        } else if (itemId >= 20000 && itemId < 22000) {
            data = getEqp();
            cat = "Face";
        } else if (itemId >= 1080000 && itemId < 1090000) {
            data = getEqp();
            cat = "Glove";
        } else if (itemId >= 30000 && itemId < 32000) {
            data = getEqp();
            cat = "Hair";
        } else if (itemId >= 1050000 && itemId < 1060000) {
            data = getEqp();
            cat = "Longcoat";
        } else if (itemId >= 1060000 && itemId < 1070000) {
            data = getEqp();
            cat = "Pants";
        } else if (itemId >= 1610000 && itemId < 1660000) {
            data = getEqp();
            cat = "Mechanic";
        } else if (itemId >= 1802000 && itemId < 1810000) {
            data = getEqp();
            cat = "PetEquip";
        } else if (itemId >= 1920000 && itemId < 2000000) {
            data = getEqp();
            cat = "Dragon";
        } else if (itemId >= 1112000 && itemId < 1120000) {
            data = getEqp();
            cat = "Ring";
        } else if (itemId >= 1092000 && itemId < 1100000) {
            data = getEqp();
            cat = "Shield";
        } else if (itemId >= 1070000 && itemId < 1080000) {
            data = getEqp();
            cat = "Shoes";
        } else if (itemId >= 1900000 && itemId < 1920000) {
            data = getEqp();
            cat = "Taming";
        } else if (itemId >= 1300000 && itemId < 1800000) {
            data = getEqp();
            cat = "Weapon";
        } else if (itemId >= 4000000 && itemId < 5000000) {
            data = getEtc();
        } else if (itemId >= 3000000 && itemId < 4000000) {
            data = getIns();
        } else if (itemId >= 5000000 && itemId < 5010000) {
            data = getPet();
        } else {
            return null;
        }
        if (cat == null) {
            return data.getChildByPath(String.valueOf(itemId));
        } else {
            return data.getChildByPath(cat + "/" + itemId);
        }
    }

    public String loadItemName(final int itemId) {
        final MapleData strings = getItemStringData(itemId);
        if (strings == null) {
            return null;
        }
        return WzDataTool.getStringPath("name", strings, null);
    }

    public String loadItemMsg(final int itemId) {
        final MapleData strings = getItemStringData(itemId);
        if (strings == null) {
            return null;
        }
        return WzDataTool.getStringPath("msg", strings, null);
    }

    public MapleData getMob() {
        return getData("Mob.img");
    }

    public MapleData getNpc() {
        return getData("Npc.img");
    }

    public String getNpcName(int npc_id) {
        return WzDataTool.getStringPath(npc_id + "/name", getNpc(), "MISSINGNO");
    }

    public MapleData getMap() {
        return getData("Map.img");
    }

    public MapleData getSkill() {
        return getData("Skill.img");
    }

    public String getSkillName(int skill_id) {
        MapleData skillroot = getSkill().getChildByPath(String.format("%07d", skill_id));
        if (skillroot != null) {
            return WzDataTool.getString(skillroot.getChildByPath("name"), "");
        }
        return null;
    }

    private MapleData img_Item = null; // JMS131
    private MapleData img_Cash = null;
    private MapleData img_Consume = null;
    private MapleData img_Eqp = null;
    private MapleData img_Etc = null;
    private MapleData img_Ins = null;
    private MapleData img_Pet = null;

    public MapleData getItem() {
        if (img_Item == null) {
            img_Item = getData("Item.img");
        }
        return img_Item;
    }

    public MapleData getCash() {
        if (img_Cash == null) {
            if (getItem() == null) {
                img_Cash = getData("Cash.img");
                img_Cash = checkSubDirectory(img_Cash, "Cash");
                return img_Cash;
            }
            img_Cash = checkSubDirectory(getItem(), "Cash");
        }
        return img_Cash;
    }

    public MapleData getConsume() {
        if (img_Consume == null) {
            if (getItem() == null) {
                img_Consume = getData("Consume.img");
                img_Consume = checkSubDirectory(img_Consume, "Con");
                return img_Consume;
            }
            img_Consume = checkSubDirectory(getItem(), "Con");
        }
        return img_Consume;
    }

    public MapleData getEqp() {
        if (img_Eqp == null) {
            if (getItem() == null) {
                img_Eqp = getData("Eqp.img");
                img_Eqp = checkSubDirectory(img_Eqp, "Eqp");
                return img_Eqp;
            }
            img_Eqp = checkSubDirectory(getItem(), "Eqp");
        }
        return img_Eqp;
    }

    public MapleData getEtc() {
        if (img_Etc == null) {
            if (getItem() == null) {
                img_Etc = getData("Etc.img");
                img_Etc = checkSubDirectory(img_Etc, "Etc");
                return img_Etc;
            }
            img_Etc = checkSubDirectory(getItem(), "Etc");
        }
        return img_Etc;
    }

    public MapleData getIns() {
        if (img_Ins == null) {
            if (getItem() == null) {
                img_Ins = getData("Ins.img");
                img_Ins = checkSubDirectory(img_Ins, "Ins");
                return img_Ins;
            }
            img_Ins = checkSubDirectory(getItem(), "Ins");
        }
        return img_Ins;
    }

    public MapleData getPet() {
        if (img_Pet == null) {
            // please do not use old PetDialog.img (Pet.img)
            if (getItem() == null) {
                img_Pet = getData("Pet.img");
                img_Pet = checkSubDirectory(img_Pet, "Pet");
                return img_Pet;
            }
            img_Pet = checkSubDirectory(getItem(), "Pet");
        }
        return img_Pet;
    }

    // MonsterBook
    private MapleData img_MonsterBook = null;
    private List<DropMonsterBook> list_drop_monsterbook = null;
    private boolean bookAvailable = true;

    public static class DropMonsterBook {

        public int mob_id = 0;
        public List<Integer> drop_ids = new ArrayList<>();
    }

    public boolean checkBookAvailable() {
        return bookAvailable;
    }

    public MapleData getMonsterBook() {
        if (img_MonsterBook == null) {
            img_MonsterBook = getData("MonsterBook.img");
            if (img_MonsterBook == null) {
                bookAvailable = false;
            }
        }
        return img_MonsterBook;
    }

    public DropMonsterBook getMonseterBookDrop(int mob_id) {
        if (list_drop_monsterbook == null) {
            list_drop_monsterbook = new ArrayList<>();
        }

        for (DropMonsterBook dmb : list_drop_monsterbook) {
            if (dmb.mob_id == mob_id) {
                return dmb;
            }
        }

        int count = 0;
        DropMonsterBook dmb = new DropMonsterBook();
        dmb.mob_id = mob_id;

        MapleData md_book = getMonsterBook();
        if (md_book != null) {
            for (MapleData md_mob : md_book.getChildren()) {
                if (Integer.parseInt(md_mob.getName()) == mob_id) {
                    MapleData md_reward = md_mob.getChildByPath("reward");
                    if (md_reward == null) {
                        break;
                    }
                    for (MapleData md_drop_item : md_reward.getChildren()) {
                        int item_id = WzDataTool.getInt(md_drop_item);
                        if (!WzDataStorage.ITEM.check(item_id)) {
                            DebugLogger.ErrorLog("invalid monsterbook drop : " + item_id);
                            continue;
                        }
                        dmb.drop_ids.add(item_id);
                        count++;
                    }
                    DebugLogger.DebugLog("monsterbook drop loaded : " + mob_id + " (" + count + ")");
                    break;
                }
            }
        }

        list_drop_monsterbook.add(dmb);
        return dmb;
    }
}
