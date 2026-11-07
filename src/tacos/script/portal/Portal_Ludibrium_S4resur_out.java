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
// Map.wz portal script : s4resur_out (JMS186), lua の処理を利用
public class Portal_Ludibrium_S4resur_out extends PortalScriptMan {

    private final int quest_resurrection = 6134; // リザレクションのクエスト
    private final int item_fear_of_death = 4031448; // 死の恐怖
    private final int map_forgotten_corridor = 220070400; // ルディブリアム : 忘れられた回廊

    @Override
    public boolean enter() {
        if (getQuestStatus(this.quest_resurrection) != 1) {
            return false;
        }
        if (getItemCount(this.item_fear_of_death) <= 0 && !exchange(0, this.item_fear_of_death, 1)) {
            // 翻訳
            message("インベントリに空きスロットがないので進めません。インベントリを整理してからもう一度お試しください。");
            return false;
        }
        return transferField(this.map_forgotten_corridor, "pt00");
    }
}
