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
// Map.wz portal script : s4tornado_enter (JMS186), lua の処理を利用
public class Portal_Ludibrium_S4tornado_enter extends PortalScriptMan {

    private final int quest_tornado = 6230; // 4次転職のスキルのクエスト
    private final int quest_tornado2 = 6231;
    private final int item_unknown_letter = 4001110; // [ストーリーブック]正体不明の手紙
    private final int map_hidden_balcony = 922020200; // 隠れたバルコニー
    private final int job_night_lord = 412;
    private final int level_min = 120;

    @Override
    public boolean enter() {
        if (getLevel() < this.level_min || getJob() != this.job_night_lord) {
            return false;
        }
        int state = getQuestStatus(this.quest_tornado);
        if ((state == 0 && 0 < getItemCount(this.item_unknown_letter)) || state == 1 || (state == 2 && getQuestStatus(this.quest_tornado2) == 0)) {
            return transferField(this.map_hidden_balcony, "");
        }
        return false;
    }
}
