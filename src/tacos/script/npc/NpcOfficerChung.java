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
public class NpcOfficerChung extends NpcScriptMan {

    private final int map_secret_path = 701010322; // 中国 : 抜け道

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 前提クエストの確認
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("秘密任務遂行のため、通路にお送りします。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                // TODO : 10分の制限時間
                warp(this.map_secret_path); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
