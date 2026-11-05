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
public class Npc_Kerning_SmallStreetLight extends NpcScriptMan {

    private final int anti_macro_monster_id = 5090000; // シェード

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (checkMob(this.anti_macro_monster_id)) {
                    // 原文ママ
                    askYesNo("スイッチがある小さい街灯である。スイッチを入れますか。");
                    return true;
                }
                // TODO : 通常メッセージ
                say("街灯がある。");
                return false;
            }
            case 1 -> {
                weakenMacroMob(this.anti_macro_monster_id);
                // 原文ママ
                mapMessage("街灯をスイッチオンしました。光を受けたシェードの力が急激に弱化されます。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
