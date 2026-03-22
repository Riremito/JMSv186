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
package tacos.wz.data;

import tacos.wz.TacosWz;
import tacos.config.Content;
import tacos.wz.ids.DWI_Validation;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.List;
import odin.provider.MapleDataTool;
import odin.provider.IMapleData;

/**
 *
 * @author Riremito
 */
public class StringWz extends TacosWz {

    private static StringWz wz = null;

    public static StringWz get() {
        if (wz == null) {
            wz = new StringWz(Content.Wz_SingleFile.get() ? "Data.wz/String" : "String.wz");
        }

        return wz;
    }

    public StringWz(String path) {
        super(path);
    }

    private IMapleData checkSubDirectory(IMapleData md, String dir_name) {
        IMapleData sub_dir = md.getChildByPath(dir_name);
        if (sub_dir != null) {
            DebugLogger.XmlLog("SubDir OK : " + dir_name);
            return sub_dir;
        }
        return md;
    }

    public IMapleData getMob() {
        return getData("Mob.img");
    }

    public IMapleData getNpc() {
        return getData("Npc.img");
    }

    public IMapleData getMap() {
        return getData("Map.img");
    }

    public IMapleData getSkill() {
        return getData("Skill.img");
    }

    private IMapleData img_Item = null; // JMS131
    private IMapleData img_Cash = null;
    private IMapleData img_Consume = null;
    private IMapleData img_Eqp = null;
    private IMapleData img_Etc = null;
    private IMapleData img_Ins = null;
    private IMapleData img_Pet = null;

    public IMapleData getItem() {
        if (img_Item == null) {
            img_Item = getData("Item.img");
        }
        return img_Item;
    }

    public IMapleData getCash() {
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

    public IMapleData getConsume() {
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

    public IMapleData getEqp() {
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

    public IMapleData getEtc() {
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

    public IMapleData getIns() {
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

    public IMapleData getPet() {
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
    private IMapleData img_MonsterBook = null;
    private List<DropMonsterBook> list_drop_monsterbook = null;
    private boolean bookAvailable = true;

    public static class DropMonsterBook {

        public int mob_id = 0;
        public List<Integer> drop_ids = new ArrayList<>();
    }

    public boolean checkBookAvailable() {
        return bookAvailable;
    }

    public IMapleData getMonsterBook() {
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

        IMapleData md_book = getMonsterBook();
        if (md_book != null) {
            for (IMapleData md_mob : md_book.getChildren()) {
                if (Integer.parseInt(md_mob.getName()) == mob_id) {
                    IMapleData md_reward = md_mob.getChildByPath("reward");
                    if (md_reward == null) {
                        break;
                    }
                    for (IMapleData md_drop_item : md_reward.getChildren()) {
                        int item_id = MapleDataTool.getInt(md_drop_item);
                        if (!DWI_Validation.isValidItemID(item_id)) {
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
