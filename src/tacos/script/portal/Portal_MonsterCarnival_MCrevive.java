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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : MCrevive1 ~ MCrevive6 (JMS186), lua の処理を利用
// 待機マップ (980000102 など) から戦闘マップ (980000101 など) のチームの復活地点へ移動
public class Portal_MonsterCarnival_MCrevive extends PortalScriptMan {

    private final int team_red = 0;

    @Override
    public boolean enter() {
        int map_battle = getMapId() - 1;
        if (getTeamForMCarnival() == this.team_red) {
            return transferField(map_battle, "red_revive");
        }
        return transferField(map_battle, "blue_revive");
    }
}
