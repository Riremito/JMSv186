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
package tacos.debug;

import tacos.client.TacosCharacter;
import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
public class DebugManTest extends DebugMan implements IDebugMan {

    @Override
    public boolean start(TacosCharacter chr) {
        super.start(this, chr);
        return true;
    }

    @Override
    public boolean end(TacosCharacter chr) {
        super.end(chr);
        return true;
    }

    private int target_map_id = 910000000;

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcTag nt = new NpcTag();
                nt.add("DebugManTest...");
                nt.add("r : 赤 = #r赤#k");
                nt.add("g : 緑 = #g緑#k");
                nt.add("b : 青 = #b青#k");
                nt.add("d : 紫 = #d紫#k");
                nt.add("k : 黒 = #k黒");
                nt.add("e : 太字 = #e太字#n");
                nt.add("n : デフォルト = #nデフォルト");
                nt.add("m : マップ名 = #m910000000#");
                nt.add("o : Mob名 = #o130101#");
                nt.add("p : NPC名 = #p1012003#");
                nt.add("t : アイテム名 = #t1302000#");
                nt.add("q : スキル名 = #q4001344#");
                nt.add("z : アイテム情報 = #z1302000#");
                nt.add("v : アイテムアイコン1 = #v1302000#");
                nt.add("i : アイテムアイコン2 = #i1302000#");
                nt.add("s : スキルアイコン = #s4001344#");
                nt.add("c : アイテム所持数 = #c2000004#");
                nt.addMenu(11, "#m100000000#");
                nt.addMenu(22, "#m200000000#");
                super.askMenu(chr, nt);
                return true;
            }
            case 1 -> {
                NpcTag nt = new NpcTag();
                if (answer == 11) {
                    nt.add("go to #m100000000#.");
                    this.target_map_id = 100000000;
                    super.say(chr, nt, true, false);
                    return true;
                }
                if (answer == 22) {
                    nt.add("go to #m200000000#.");
                    this.target_map_id = 200000000;
                    super.say(chr, nt, true, false);
                    return true;
                }
                return false;
            }
            case 2 -> {
                TacosMap map = chr.findMap(this.target_map_id);
                chr.changeMapPortal(map, map.getPortal(0));
                return false;
            }
            default -> {
            }
        }

        return false;
    }
}
