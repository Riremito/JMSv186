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
public class NpcVonLeon extends NpcScriptMan {

    private final int map_audience_room = 211070100; // 謁見室
    private final int mob_von_leon = 8840010; // ヴァンレオン

    @Override
    public boolean action(int status, int answer) {
        // ヴァンレオン召喚
        switch (status) {
            case 0 -> {
                // 原文ママ (typo)
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私を倒しに来た勇士たちか…暗黒の魔法使いたちを敵対する者か…どちらでも構わない。お互いの目的さえ明確であればこれ以上語る必要なないはずだ…");
                nt.add("かかってこい。愚かな奴らよ…");
                askAccept(nt);
                return true;
            }
            case 1 -> {
                spawnBossMonster(8840010, 6, -183, -1);
                removeNpc();
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
