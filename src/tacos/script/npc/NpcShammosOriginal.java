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
// Npc.wz script : shammos_Original (JMS194-302), lua と旧 js が存在しないため String.wz の会話文を参考に実装
// TODO : 氷の峡谷入口へ移動する条件は不明
public class NpcShammosOriginal extends NpcScriptMan {

    private final int map_entrance = 921120000; // 氷の峡谷 : 氷の峡谷入口
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        if (status == 0) {
            // 原文ママ (String.wz の g0)
            askYesNo("じゃ、私だけの秘密通路について来い");
            return true;
        }
        if (ok) {
            warp(this.map_entrance);
        }
        return false;
    }
}
