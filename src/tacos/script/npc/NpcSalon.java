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
package tacos.script.npc;

import java.util.ArrayList;
import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class NpcSalon extends NpcScriptMan {

    public static final int KIND_HAIR = 0;
    public static final int KIND_HAIR_COLOR = 1;
    public static final int KIND_FACE = 2;
    public static final int KIND_FACE_COLOR = 3;
    public static final int KIND_SKIN = 4;

    // 美容院のメニュー1つ分
    public static class SalonEntry {

        private final String label;
        private final int kind;
        private final int coupon;
        private final boolean random;
        private final String ask_text;
        private final String no_text;
        private final String ok_text;
        private final String ng_text;
        private final String same_text;
        // {男性, 女性} or {共通}
        private final int[][] styles;

        public SalonEntry(String label, int kind, int coupon, boolean random, String ask_text, String no_text, String ok_text, String ng_text, String same_text, int[][] styles) {
            this.label = label;
            this.kind = kind;
            this.coupon = coupon;
            this.random = random;
            this.ask_text = ask_text;
            this.no_text = no_text;
            this.ok_text = ok_text;
            this.ng_text = ng_text;
            this.same_text = same_text;
            this.styles = styles;
        }
    }

    private final String intro_text;
    private final String menu_text;
    private final SalonEntry[] entries;
    private final ArrayList<Integer> style_ids = new ArrayList<>();
    private SalonEntry entry = null;
    private int entry_status = -1;

    public NpcSalon(String intro_text, String menu_text, SalonEntry... entries) {
        this.intro_text = intro_text;
        this.menu_text = menu_text;
        this.entries = entries;
    }

    @Override
    public boolean action(int status, int answer) {
        int menu_status = (this.intro_text != null) ? 1 : 0;
        // いいえを選択した
        if (this.entry != null && this.entry.random && status == this.entry_status - 1) {
            say(this.entry.no_text);
            return false;
        }
        if (status < menu_status) {
            say(this.intro_text, false, true);
            return true;
        }
        if (this.entry == null) {
            if (this.menu_text == null) {
                this.entry = this.entries[0];
            } else if (status == menu_status) {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(this.menu_text);
                for (int i = 0; i < this.entries.length; i++) {
                    nt.addMenu(i, this.entries[i].label);
                }
                askMenu(nt);
                return true;
            } else {
                if (answer < 0 || this.entries.length <= answer) {
                    return false;
                }
                this.entry = this.entries[answer];
            }
            this.entry_status = status;
            // 選択可能なスタイル
            int hair = getPlayer().getHair();
            int face = getPlayer().getFace();
            int current = (this.entry.kind == KIND_SKIN) ? getPlayer().getSkinColor() : (this.entry.kind == KIND_FACE || this.entry.kind == KIND_FACE_COLOR) ? face : hair;
            int[] bases = this.entry.styles[(this.entry.styles.length == 2) ? getPlayer().getGender() : 0];
            this.style_ids.clear();
            for (int base : bases) {
                int id;
                WzDataStorage wz;
                switch (this.entry.kind) {
                    case KIND_HAIR -> {
                        id = base + hair % 10;
                        wz = WzDataStorage.HAIR;
                    }
                    case KIND_HAIR_COLOR -> {
                        id = hair / 10 * 10 + base;
                        wz = WzDataStorage.HAIR;
                    }
                    case KIND_FACE -> {
                        id = base + face / 100 % 10 * 100;
                        wz = WzDataStorage.FACE;
                    }
                    case KIND_FACE_COLOR -> {
                        id = face - face / 100 % 10 * 100 + base;
                        wz = WzDataStorage.FACE;
                    }
                    default -> {
                        id = base;
                        wz = WzDataStorage.SKIN;
                    }
                }
                if (wz.check(id) && !this.style_ids.contains(id)) {
                    this.style_ids.add(id);
                }
            }
            // 既に同じスタイル
            if (this.entry.same_text != null && this.style_ids.contains(current)) {
                say(this.entry.same_text);
                return false;
            }
            if (this.style_ids.isEmpty()) {
                return false;
            }
            if (this.entry.random) {
                askYesNo(this.entry.ask_text);
            } else {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(this.entry.ask_text);
                askAvatar(nt, this.style_ids);
            }
            return true;
        }
        if (status != this.entry_status + 1) {
            return false;
        }
        int index = this.entry.random ? (int) (Math.random() * this.style_ids.size()) : answer;
        if (index < 0 || this.style_ids.size() <= index) {
            return false;
        }
        if (!checkItem(this.entry.coupon)) {
            say(this.entry.ng_text);
            return false;
        }
        int id = this.style_ids.get(index);
        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.CASH, this.entry.coupon, 1, true, false);
        switch (this.entry.kind) {
            case KIND_HAIR, KIND_HAIR_COLOR -> {
                getPlayer().setHair(id);
            }
            case KIND_FACE, KIND_FACE_COLOR -> {
                getPlayer().setFace(id);
            }
            default -> {
                getPlayer().setSkinColor(id);
            }
        }
        getPlayer().sendStatChanged();
        say(this.entry.ok_text);
        return false;
    }
}
