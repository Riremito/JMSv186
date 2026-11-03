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
public class NpcHikari extends NpcScriptMan {

    private final int cost = 300;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("銭湯に入りたいですか？では、" + this.cost + "メルを払いなさい。");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (!payMoney(this.cost)) {
                    nt.add("入場料" + this.cost + "メルを持っているか確認してください。");
                    say(nt);
                    return false;
                }
                int target_map_id = getPlayer().getGender() == 0 ? 801000100 : 801000200;
                warp(target_map_id);
                return false;
            }
            case -1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("今度利用してください。");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
