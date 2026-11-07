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
// Map.wz portal script : ludi021 (JMS186), lua の処理を利用
public class Portal_Ludibrium_Ludi021 extends PortalScriptMan {

    private final int item_machine_part = 4031092; // 機械部品
    private final int map_toy_factory = 220020000; // ルディブリアム : オモチャ工場-1工程<1区域>

    @Override
    public boolean enter() {
        removeItemAll(this.item_machine_part);
        return transferField(this.map_toy_factory, "q000");
    }
}
