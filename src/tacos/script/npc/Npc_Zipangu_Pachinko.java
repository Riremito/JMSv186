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
public class Npc_Zipangu_Pachinko extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getTama() <= 0) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("パチンコ玉が足りないため、パチンコをすることができません。只今、ポイントショップのETCの経済活動欄でパチンコ玉を販売中ですので、ご利用ください。");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("パチンコを始めましょうか。");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                switch (getNpcId()) {
                    case 9100200, 9100201 -> {
                        getPlayer().StartPachinko(0);
                    }
                    case 9100202, 9100203 -> {
                        getPlayer().StartPachinko(1);
                    }
                    case 9100204, 9100205 -> {
                        getPlayer().StartPachinko(2);
                    }
                    default -> {
                    }
                }
                return false;
            }
            case -1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("残念ですね…。後で時間ができましたら、ご利用ください。");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
