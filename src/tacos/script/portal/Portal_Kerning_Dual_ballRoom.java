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
// Map.wz portal script : dual_ballRoom (JMS186), js の処理を利用
public class Portal_Kerning_Dual_ballRoom extends PortalScriptMan {

    private final int quest_ball_room = 2363;
    private final int item_insight = 4032616; // 慧眼

    @Override
    public boolean enter() {
        // TODO : js ではマップ移動の処理が未実装
        if (getQuestStatus(this.quest_ball_room) == 1 && !haveItem(this.item_insight)) {
            gainItem(this.item_insight, 1);
        }
        return false;
    }
}
