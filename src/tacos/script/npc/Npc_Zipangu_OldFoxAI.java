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
// Npc.wz script : Go_boss4 (JMS164-302), lua が存在しないため旧 js (scripts_msea102) を参考に実装
// TODO : ボスの召喚位置は不明 (マップのモンスターの位置を利用)
public class Npc_Zipangu_OldFoxAI extends NpcTokyoExpeditionMan {

    private static final String EXPEDITION_NAME = "Nibergen";
    private static final String BOSS_NAME = "ニーベルング";
    private static final int MAP_WAIT = 802000610; // ジパング : 旗艦ファイア・オールドフォックス甲板 2102年
    private static final int MAP_BOSS = 802000611; // ジパング : トウキョウ上空 2102年
    private static final int MOB_BOSS = 9400271; // ニーベルング
    private static final int MOB_X = 183;
    private static final int MOB_Y = 177;

    public Npc_Zipangu_OldFoxAI() {
        super(EXPEDITION_NAME, BOSS_NAME, MAP_WAIT, MAP_BOSS, MOB_BOSS, MOB_X, MOB_Y);
    }
}
