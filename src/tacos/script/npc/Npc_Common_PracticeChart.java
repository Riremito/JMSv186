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
public class Npc_Common_PracticeChart extends NpcScriptMan {

    private final int anti_macro_monster_id = 5090001; // 仙人人形

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (checkMob(this.anti_macro_monster_id)) {
                    // 原文ママ
                    askYesNo("練習記録表に名前を書くことができます。書きますか。");
                    return true;
                }
                // 原文ママ
                say("練習日誌を書く練習記録表である。真面目に練習日誌を書いた修練生のみ仙人人形の怒りを静めることができるという。");
                return false;
            }
            case 1 -> {
                weakenMacroMob(this.anti_macro_monster_id);
                // 原文ママ
                mapMessage("練習記録表に名前を書きました。真面目な修練生として認められ、仙人人形を倒す資格を得ました");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
