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
package tacos.server.map.object;

import odin.server.MapleItemInformationProvider;
import tacos.database.query.DQ_Pets;
import tacos.server.TacosWorld;

/**
 *
 * @author Riremito
 */
public class TacosPet extends TacosMapObject {

    private static int pet_level_limit = 30;
    private static int pet_closeness_limit = 30000;
    private static int pet_fullness_limit = 100;
    private static final int[] closeness_table = {0, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1224, 1642, 2161, 2793, 3557, 4467, 5542, 6801, 8263, 9950, 11882, 14084, 16578, 19391, 22547, 26074, 30000};

    private int secondsLeft = 0;
    private byte fullness = 100;
    private byte level = 1;
    private short inventorypos = 0;
    private short closeness = 0;
    private short skill_mask = -1; // 0xFFFF

    private String name;
    private boolean summoned = false;
    private int unique_id;
    private int item_id;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getSummoned() {
        return this.summoned;
    }

    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    public int getUniqueId() {
        return this.unique_id;
    }

    public void setUniqueId(int id) {
        this.unique_id = id;
    }

    public int getPetItemId() {
        return this.item_id;
    }

    public void setPetItemId(int id) {
        this.item_id = id;
    }

    public static int getClosenessNeededForLevel(int level) {
        return closeness_table[level - 1];
    }

    private TacosPet(int petitemid, int uniqueid, short inventorypos) {
        this.inventorypos = inventorypos;

        setUniqueId(uniqueid);
        setPetItemId(petitemid);
    }

    public static TacosPet loadFromDb(int itemid, int petid, short inventorypos) {
        TacosPet ret = new TacosPet(itemid, petid, inventorypos);
        if (!DQ_Pets.load(ret, petid)) {
            return null;
        }
        return ret;
    }

    public void saveToDb() {
        DQ_Pets.save(this);
    }

    public static TacosPet createPet(int itemid, int uniqueid) {
        return createPet(itemid, MapleItemInformationProvider.getInstance().getName(itemid), 1, 0, 100, uniqueid, itemid == 5000054 ? 18000 : 0);
    }

    public static TacosPet createPet(int itemid, String name, int level, int closeness, int fullness, int uniqueid, int secondsLeft) {
        if (uniqueid <= -1) { //wah
            uniqueid = TacosWorld.getNextItemUniqueId();
        }
        if (!DQ_Pets.add(uniqueid, name, level, closeness, fullness, secondsLeft)) {
            return null;
        }

        TacosPet pet = new TacosPet(itemid, uniqueid, (short) 0);
        pet.setName(name);
        pet.setLevel(level);
        pet.setFullness(fullness);
        pet.setCloseness(closeness);
        pet.setSecondsLeft(secondsLeft);
        return pet;
    }

    public short getInventoryPosition() {
        return inventorypos;
    }

    public void setInventoryPosition(short inventorypos) {
        this.inventorypos = inventorypos;
    }

    public short getCloseness() {
        return closeness;
    }

    public void setCloseness(int closeness) {
        this.closeness = (short) closeness;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = (byte) level;
    }

    public byte getFullness() {
        return fullness;
    }

    public void setFullness(int fullness) {
        this.fullness = (byte) fullness;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(int sl) {
        this.secondsLeft = sl;
    }

    public short getFlags() {
        return skill_mask;
    }

    // エサやり
    public void feed(int inc_fullness, int inc_closeness) {
        // 満腹時かつ通常エサの場合は親密度を下げる
        if (pet_fullness_limit <= this.fullness && inc_closeness != 100) {
            inc_closeness = -1;
        }
        // 満腹度設定
        if (pet_fullness_limit < (this.fullness + inc_fullness)) {
            this.fullness = (byte) pet_fullness_limit;
        } else {
            this.fullness += inc_fullness;
        }
        // 親密度設定
        if (inc_closeness < 0 && (this.closeness + inc_closeness) < 0) {
            this.closeness = 0;
        }
        if (0 < inc_closeness && pet_closeness_limit < (this.closeness + inc_closeness)) {
            this.closeness = (short) pet_closeness_limit;
        } else {
            this.closeness += inc_closeness;
        }
        // レベル設定
        if ((getLevel() <= pet_level_limit - 1) && getClosenessNeededForLevel(getLevel() + 1) <= this.closeness) {
            level += 1;
            if (pet_level_limit < level) {
                level = (byte) pet_level_limit;
            }
        }
    }
}
