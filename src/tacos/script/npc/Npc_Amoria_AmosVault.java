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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : PartyAmoria_playBo (BMS8-24, EMS70-76, GMS92-95), 会話文は lua_script_jp (PartyAmoria_playBo) を利用
public class Npc_Amoria_AmosVault extends NpcScriptMan {

    private final String field_set_name = "PartyAmoria";
    private final int map_training = 670010000; // Hidden Street : Amos' Training Ground
    private boolean leave = false;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        if (status == 0) {
            if (field_set.getVar("stageBo_clear").equals("1")) {
                this.leave = true;
                // 翻訳
                askYesNo("ここから出たいか？");
                return true;
            }
            // 翻訳
            say("運は素早い武器に味方する…1分以内に取れるものは全部取れ！行け！");
            field_set.setVar("stageBo_clear", "1");
            return false;
        }
        if (!this.leave) {
            return false;
        }
        if (!ok) {
            // 翻訳
            say("このボーナスステージを楽しんでくれ！");
            return false;
        }
        if (status == 1) {
            // 翻訳
            say("また会おう、友よ。次に会えるのを楽しみにしているぞ！", false, true);
            return true;
        }
        if (status == 2) {
            warp(this.map_training, "st00");
        }
        return false;
    }
}
