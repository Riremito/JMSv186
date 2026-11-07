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
public class Npc_MapleIsland_DancesWithBalrogTutorial extends NpcScriptMan {

    private final int map_job_experience = 1020100; // 職業体験 (戦士)
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("戦士を体験してみたかったらもう一度俺に声をかけてくれよ。");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("戦士は、凄まじい攻撃力と体力を持つ職業で、戦場の最前線でその真価を発揮するんだ。基本攻撃力がとても強い職業で、高レベルのスキルを覚えればもっと強い力を発揮する事ができるよ。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                this.asked = true;
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どうだい？戦士を体験してみないか？");
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                // 職業体験カットシーン
                setStandAloneMode(true);
                setDirectionMode(true);
                warp(this.map_job_experience); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
