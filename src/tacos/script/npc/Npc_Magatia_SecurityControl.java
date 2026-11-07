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
public class Npc_Magatia_SecurityControl extends NpcScriptMan {

    private final int anti_macro_monster_id = 7090000; // 自動警備システム

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (checkMob(this.anti_macro_monster_id)) {
                    // 原文ママ
                    askYesNo("制御装置を通じて自動警備システムを操作可能です。自動警備システムを止めますか。");
                    return true;
                }
                // TODO : 通常メッセージ
                say("制御装置がある。");
                return false;
            }
            case 1 -> {
                weakenMacroMob(this.anti_macro_monster_id);
                // 原文ママ
                mapMessage("自動警備システムを止めました。自動警備システムの侵入者追放機能が止まります。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
