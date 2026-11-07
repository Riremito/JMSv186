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
// Map.wz portal script : enterExitMirror (JMS186), js の処理を利用
// 鏡の中の金箔寺
public class Portal_Common_EnterExitMirror extends PortalScriptMan {

    private final int map_mirror = 950100000; // 隠れ道 : 金箔寺 (鏡の中)
    private final int map_temple = 809060000; // 隠れ道 : 金箔寺

    @Override
    public boolean enter() {
        if (getMapId() == this.map_mirror) {
            return warp(this.map_temple, "mirrorExit");
        }
        if (getMapId() == this.map_temple) {
            return warp(this.map_mirror, "mirrorEnter");
        }
        return false;
    }
}
