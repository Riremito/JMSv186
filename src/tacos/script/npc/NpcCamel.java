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

import tacos.client.TacosCharacter;
import tacos.debug.DebugMan;
import tacos.debug.DebugMan.NpcTag;
import tacos.debug.IDebugMan;

/**
 *
 * @author Riremito
 */
public class NpcCamel extends DebugMan implements IDebugMan {

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

    private final int cost = 1500;

    private boolean payMoney(TacosCharacter chr, int cost) {
        return chr.gainMeso(-cost, true, false, true);
    }

    private String getMapName(int map_id) {
        return "#m" + map_id + "#";
    }

    private String getNpcName(int npc_id) {
        return "#p" + npc_id + "#";
    }

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcTag nt = new NpcTag();
                if (chr.getMap().getId() == 260020000) {
                    nt.add("#b" + getNpcName(getNpcId()) + "#kに乗って錬金術の村#b" + getMapName(261000000) + "#kに移動しますか。費用は" + this.cost + "メルです。");
                } else {
                    // TODO : 文章
                    nt.add("#b" + getNpcName(getNpcId()) + "#kに乗って#b" + getMapName(260000000) + "#kに移動しますか。費用は" + this.cost + "メルです。");
                }
                super.askYesNo(chr, nt);
                return true;
            }
            case 1 -> {
                NpcTag nt = new NpcTag();
                if (!payMoney(chr, this.cost)) {
                    // not coded yet.
                    return false;
                }
                if (chr.getMapId() == 260020000) {
                    // 260020000 -> マガティア
                    chr.changeMapById(261000000); // TODO : portal name.
                } else {
                    // 260020700 -> アリアント
                    chr.changeMapById(260000000); // TODO : portal name.
                }
                return false;
            }
            case -1 -> {
                // not coded yet.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
