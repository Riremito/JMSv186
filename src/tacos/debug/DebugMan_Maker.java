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
package tacos.debug;

import java.util.ArrayList;
import odin.server.MapleInventoryManipulator;
import tacos.script.npc.NpcScriptTag;
import tacos.wz.EtcWz.MakerItemInfo;
import tacos.wz.EtcWz.MakerRecipe;
import tacos.wz.WzDataStorage;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class DebugMan_Maker extends DebugNpcScriptMan {

    private enum MakerType {
        All(0),
        Warrior(1),
        Magician(2),
        Archer(4),
        Thief(8),
        Pirate(16);

        private final int value;

        private MakerType(int value) {
            this.value = value;
        }

        private int get() {
            return this.value;
        }

        private static MakerType findByOrdinal(int ordinal) {
            for (MakerType mt : MakerType.values()) {
                if (mt.ordinal() == ordinal) {
                    return mt;
                }
            }
            return null;
        }
    }

    private int maker_type;
    private ArrayList<Integer> maker_items;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                for (MakerType mt : MakerType.values()) {
                    nt.addMenu(mt.ordinal(), mt.name());
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                MakerType mt = MakerType.findByOrdinal(answer);
                if (mt == null) {
                    return false;
                }
                this.maker_type = mt.get();
                this.maker_items = WzXML.ETC.getMakerItems(this.maker_type);
                if (maker_items.isEmpty()) {
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                int index = 0;
                for (Integer item_id : this.maker_items) {
                    nt.addMenu(index++, String.format("%d : #z%d#", item_id, item_id));
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (maker_items.size() <= answer) {
                    return false;
                }
                int maker_item_id = maker_items.get(answer);
                MakerItemInfo mii = WzXML.ETC.getMakerItem(this.maker_type, maker_item_id);
                if (mii == null) {
                    getPlayer().DebugMsg("Maker : broken.");
                    return false;
                }
                getPlayer().DebugMsgItem("Maker : target = " + maker_item_id, maker_item_id);
                if (getPlayer().getMeso() < mii.getMeso()) {
                    getPlayer().addMeso(mii.getMeso());
                }
                if (mii.getReqItem() != 0 && !getPlayer().haveItem(mii.getReqItem())) {
                    if (WzDataStorage.ITEM.check(mii.getReqItem())) {
                        MapleInventoryManipulator.addById(getPlayer(), mii.getReqItem(), (short) 1);
                        getPlayer().DebugMsgItem("Maker : reqItem = " + mii.getReqItem(), mii.getReqItem());
                    } else {
                        getPlayer().DebugMsg("Maker : invalid reqItem.");
                    }
                }
                if (mii.getReqEquip() != 0 && !getPlayer().haveItem(mii.getReqEquip())) {
                    if (WzDataStorage.ITEM.check(mii.getReqEquip())) {
                        MapleInventoryManipulator.addById(getPlayer(), mii.getReqEquip(), (short) 1);
                        getPlayer().DebugMsgItem("Maker : reqEquip = " + mii.getReqEquip(), mii.getReqEquip());
                    } else {
                        getPlayer().DebugMsg("Maker : invalid reqEquip.");
                    }
                }
                for (MakerRecipe recipe : mii.getRecipe()) {
                    if (!getPlayer().haveItem(recipe.getItem(), recipe.getCount())) {
                        if (WzDataStorage.ITEM.check(recipe.getItem())) {
                            MapleInventoryManipulator.addById(getPlayer(), recipe.getItem(), (short) recipe.getCount());
                            getPlayer().DebugMsgItem("Maker : recipe = " + recipe.getItem() + " (" + recipe.getCount() + ")", recipe.getItem());
                        } else {
                            getPlayer().DebugMsg("Maker : recipe item.");
                        }
                    }
                }
                if (mii.getCatalyst() != 0 && !getPlayer().haveItem(mii.getCatalyst())) {
                    if (WzDataStorage.ITEM.check(mii.getCatalyst())) {
                        MapleInventoryManipulator.addById(getPlayer(), mii.getCatalyst(), (short) 1);
                        getPlayer().DebugMsgItem("Maker : catalyst = " + mii.getCatalyst(), mii.getCatalyst());
                    } else {
                        getPlayer().DebugMsg("Maker : invalid catalyst.");
                    }
                }
                getPlayer().sendStatChanged();
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
