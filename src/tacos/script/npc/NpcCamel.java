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

/**
 *
 * @author Riremito
 */
public class NpcCamel extends NpcScriptMan {

    private final int cost = 1500;

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (chr.getMap().getId() == 260020000) {
                    nt.add("#b" + getNpcName() + "#kに乗って錬金術の村#b" + getMapName(261000000) + "#kに移動しますか。費用は" + this.cost + "メルです。");
                } else {
                    // TODO : 文章
                    nt.add("#b" + getNpcName() + "#kに乗って#b" + getMapName(260000000) + "#kに移動しますか。費用は" + this.cost + "メルです。");
                }
                askYesNo(chr, nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (!payMoney(chr, this.cost)) {
                    // not coded yet.
                    return false;
                }
                if (chr.getMapId() == 260020000) {
                    // 260020000 -> マガティア
                    warp(chr, 261000000); // TODO : portal name.
                } else {
                    // 260020700 -> アリアント
                    warp(chr, 260000000); // TODO : portal name.
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
