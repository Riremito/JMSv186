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
public class Npc_Magatia_IncompleteMagicSquare extends NpcScriptMan {

    private final int anti_macro_monster_id = 8090000; // ディトとロイ

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (checkMob(this.anti_macro_monster_id)) {
                    // TODO : マクロ対策モンスターが存在する場合のメッセージ
                    askYesNo("魔法陣を起動しますか？");
                    return true;
                }
                // 原文ママ
                say("幾何学な模様が描かれている魔方陣だ。完成には至っていないようだが…。");
                return false;
            }
            case 1 -> {
                weakenMacroMob(this.anti_macro_monster_id);
                // TODO : 弱体化時のメッセージ
                mapMessage("ディトとロイが弱体化されました。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
