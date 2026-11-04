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
// Npc.wz script : Go_boss2 (JMS164-302), lua が存在しないため旧 js (scripts_msea102) を参考に実装
// TODO : 旧 js の簡易電磁パルス発信機 (4032202) の受け取りと制限時間 (20分) は未実装
public class NpcDeeda extends NpcTokyoExpeditionMan {

    private static final String EXPEDITION_NAME = "2095_tokyo";
    private static final String BOSS_NAME = "公園 2095年";
    private static final int MAP_WAIT = 802000310; // ジパング : 公園 2095年
    private static final int MAP_BOSS = 802000311; // ジパング : 公園 2095年
    private static final int MOB_BOSS = 0; // なし (マップのモンスターを利用)
    private static final int MOB_X = 0;
    private static final int MOB_Y = 0;

    public NpcDeeda() {
        super(EXPEDITION_NAME, BOSS_NAME, MAP_WAIT, MAP_BOSS, MOB_BOSS, MOB_X, MOB_Y);
    }
}
