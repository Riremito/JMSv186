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
public class Npc_MapleIsland_DarkLordTutorial extends NpcScriptMan {

    private final int map_job_experience = 1020400; // 職業体験 (盗賊)
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("盗賊を体験してみたかったらもう一度俺に声をかけてくれよ。");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("盗賊はLUKと、ある程度のDEXとSTRが重要になる職業で、戦闘では、相手を奇襲したり、姿を隠したり…特殊なスキルを使えるんだ。高い機動力と回避率を持つ盗賊は、様々なスキルを駆使して戦うことができる。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                this.asked = true;
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どうだい？盗賊を体験してみないか？");
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
