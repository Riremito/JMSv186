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
public class NpcPerry extends NpcScriptMan {

    private final int cost = 1500;

    private boolean payMoney(TacosCharacter chr, int cost) {
        return chr.gainMeso(-cost, true, false, true);
    }

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcTag nt = new NpcTag();
                // TODO : カニングシティ版の文章
                nt.add("いらっしゃ～～いッ！オラはここジパングとビクトリアアイランドとを往復するペリカンさ、運賃さえ払ってくれればビクトリアアイランドまでひとっ飛びで行くよ。運賃（片道）" + this.cost + "メル払うかい？");
                super.askYesNo(chr, nt);
                return true;
            }
            case 1 -> {
                NpcTag nt = new NpcTag();
                if (!payMoney(chr, this.cost)) {
                    // not coded yet.
                    return false;
                }
                if (chr.getMapId() == 800000000) {
                    // キノコ神社 -> カニングシティ
                    chr.changeMapById(103000000, "gm02");
                } else {
                    // カニングシティ -> キノコ神社
                    chr.changeMapById(800000000, "st00");
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
