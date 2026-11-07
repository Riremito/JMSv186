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
public class Npc_Henesys_Dragon extends NpcScriptMan {

    private final int map_contract = 900090101; // 夢の中 : 契約の条件

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (適当)
                say("ドラゴンマスターとなる運命を持つ者よ…ついに来たか。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (適当)
                say("行け、ドラゴンマスターとしての務めを果たすのだ…", true, true);
                return true;
            }
            case 2 -> {
                warp(this.map_contract);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
