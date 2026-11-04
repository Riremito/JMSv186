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
// Npc.wz script : PinkBeen_accept (JMS164-302), lua_script_jp の PinkBeen_accept は中身がないため js の処理を利用
public class Npc_TimeTemple_ForgottenTempleKeeperEntrance extends NpcScriptMan {

    private final int map_twilight_of_gods = 270050100; // 神殿の奥 : 神々の黄昏
    private final String portal_name = "sp";

    @Override
    public boolean action(int status, int answer) {
        // TODO : 遠征隊の確認
        warp(this.map_twilight_of_gods, this.portal_name);
        return false;
    }
}
