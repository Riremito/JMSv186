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

import tacos.packet.response.ResCUserLocal;

/**
 *
 * @author Riremito
 */
public class Npc_MapleIsland_GrendelTheReallyOldTutorial extends NpcScriptMan {

    private final int map_job_experience = 1020200; // 職業体験 (魔法使い)
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("魔法使いを体験してみたかったらもう一度私に声をかけておくれ。");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("魔法使いは、華麗な属性魔法とグループでの戦闘時に有効な様々な補助魔法を持っておる。更に、2次転職以後に覚える属性魔法は、反対属性の敵に致命的なダメージを与えられるのだ。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                this.asked = true;
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どうじゃ？魔法使いを体験してみないか？");
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                // 職業体験カットシーン
                getPlayer().SendPacket(ResCUserLocal.SetStandAloneMode(true));
                getPlayer().SendPacket(ResCUserLocal.SetDirectionMode(true));
                warp(this.map_job_experience); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
