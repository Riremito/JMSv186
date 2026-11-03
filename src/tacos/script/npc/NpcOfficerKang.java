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
public class NpcOfficerKang extends NpcScriptMan {

    private final int map_mountain = 701010320; // 中国 : 中原山岳地帯2

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こんにちは。僕はハークと申します。何の用でしょうか。");
                nt.addMenu(0, "ここから出たいです。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                warp(this.map_mountain); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
