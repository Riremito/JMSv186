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

/**
 *
 * @author Riremito
 */
public class Npc_Common_Perry extends NpcScriptMan {

    private final int cost = 1500;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : カニングシティ版の文章
                nt.add("いらっしゃ～～いッ！オラはここジパングとビクトリアアイランドとを往復するペリカンさ、運賃さえ払ってくれればビクトリアアイランドまでひとっ飛びで行くよ。運賃（片道）" + this.cost + "メル払うかい？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (!payMoney(this.cost)) {
                    // not coded yet.
                    return false;
                }
                if (getPlayer().getMapId() == 800000000) {
                    // キノコ神社 -> カニングシティ
                    warp(103000000, "gm02");
                } else {
                    // カニングシティ -> キノコ神社
                    warp(800000000, "st00");
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
