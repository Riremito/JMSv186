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

import java.time.LocalDateTime;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : elevator (JMS186), lua の処理を利用
// エレベーターは 4 分周期で運行している
public class Portal_Ludibrium_Elevator extends PortalScriptMan {

    private final int map_floor2 = 222020100; // ヘリオス塔2階
    private final int map_floor99 = 222020200; // ヘリオス塔99階
    private final int map_elevator_up = 222020110; // エレベーター<ルディブリアム行き>
    private final int map_elevator_down = 222020210; // エレベーター<下町行き>

    @Override
    public boolean enter() {
        // lua の continentTime の分
        int minute = LocalDateTime.now().getMinute() % 4;
        if (getMapId() == this.map_floor99 && minute == 3) {
            return transferField(this.map_elevator_down, "st00");
        }
        if (getMapId() == this.map_floor2 && minute == 1) {
            return transferField(this.map_elevator_up, "st00");
        }
        // 翻訳
        message("現在、この区間のエレベーターはご利用いただけません。しばらくしてからもう一度お試しください。");
        return false;
    }
}
