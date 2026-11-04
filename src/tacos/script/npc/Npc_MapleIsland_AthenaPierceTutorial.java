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
public class Npc_MapleIsland_AthenaPierceTutorial extends NpcScriptMan {

    private final int map_job_experience = 1020300; // 職業体験 (弓使い)
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("弓使いを体験してみたかったらもう一度私に声をかけてください。");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("弓使いはDEXとSTRが重要な職業で、戦場の後列から遠距離攻撃を担当しています。地形を用いた狩りにもすごく強いのが特徴です。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                this.asked = true;
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どうですか？弓使いを体験してみませんか？");
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
