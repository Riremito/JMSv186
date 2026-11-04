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
// Npc.wz script : Tombstone (BMS8-24, CMS86-88, EMS76, GMS92-95), 会話文は lua_script_jp (Tombstone) を利用
public class NpcTombstone extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        if (status == 0) {
            // 翻訳
            say("不死の戦士、クリストファー・クリムゾンハート、ここに眠る。");
        }
        return false;
    }
}
