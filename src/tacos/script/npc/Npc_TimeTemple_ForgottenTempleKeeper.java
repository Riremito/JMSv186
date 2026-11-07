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
public class Npc_TimeTemple_ForgottenTempleKeeper extends NpcScriptMan {

    private final int map_forgotten_twilight = 270050000; // 神殿の奥 : 忘れられた黄昏

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("遠征隊をやめて退場しますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : 遠征隊からの脱退
                warp(this.map_forgotten_twilight); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
