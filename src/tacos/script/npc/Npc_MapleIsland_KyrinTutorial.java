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
public class Npc_MapleIsland_KyrinTutorial extends NpcScriptMan {

    private final int map_job_experience = 1020500; // 職業体験 (海賊)
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("海賊を体験してみたかったらもう一度私に声をかけて。");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("海賊は高いDEX…もしくはSTRが重要になる職業で、銃か拳で戦うんだ。銃…ガンスリンガーは属性別の弾丸を使って効率的に攻撃したり、バトルシップって言う船に乗って強力な攻撃をしかける事ができる。拳…インファイターは、変身する事で強力なスキルを使えるようになるよ。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                this.asked = true;
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どう？海賊を体験してみる？");
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
