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
// Map.wz portal script : tamepig_out2 (JMS186), lua の処理を利用
public class Portal_Aquarium_Tamepig_out2 extends PortalScriptMan {

    private final int item_research_report = 4031508; // 研究報告書
    private final int item_pheromone = 4031507; // フェロモン
    private final int map_zoo = 230000003; // アクアリウム : 動物園

    @Override
    public boolean enter() {
        removeItemAll(this.item_research_report);
        removeItemAll(this.item_pheromone);
        return transferField(this.map_zoo, "");
    }
}
