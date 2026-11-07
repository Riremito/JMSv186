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
// Npc.wz script : PartyAmoria_enter2 (BMS8-24, EMS70-76, GMS92-95), 会話文は lua_script_jp (PartyAmoria_enter2) を利用
// lua の party_check_item は lua でも入場後に確認しているため削除
public class Npc_Amoria_AmosEntrance extends NpcScriptMan {

    private final String field_set_name = "PartyAmoria";
    private final int map_entrance = 670010100; // Hidden Street : Entrance of Amorian Challenge
    private final int map_training = 670010000; // Hidden Street : Amos' Training Ground
    // lua の takeawayitem (クエストアイテム)
    private final int[] items_quest = {
        4031592, // Entrance Ticket
        4031593, // Lip Lock Key
        4031594, // Geist Fang
        4031595, // Magik Mirror Shard
        4031596, // Wing Hammer
        4031597 // Cupid Code Piece
    };
    private int menu = 0;

    @Override
    public boolean action(int status, int answer) {
        if (getMapId() != this.map_entrance) {
            return false;
        }
        if (status == 0) {
            NpcScriptTag nt = new NpcScriptTag();
            // 翻訳
            nt.add("よし。何をしたい？\r\n#b#L0# パーティークエストを始めたい。\r\n#L1# ここから出して！#l#k", false);
            askMenu(nt);
            return true;
        }
        if (status == 1) {
            this.menu = answer;
            if (answer == 1) {
                // 翻訳
                say("ふむ…まあ、また今度な。じゃあな～！", false, true);
                return true;
            }
            if (answer != 0) {
                return false;
            }
            if (!isPartyBoss()) {
                // 翻訳
                say("パーティーメンバーと一緒にクエストに挑戦してみないか？ここには、チームワークなしでは乗り越えられない障害や問題が待っている。挑戦したいなら、#bパーティーリーダー#kに俺に話しかけるよう伝えてくれ。");
                return false;
            }
            // 翻訳
            say("よし、パーティーリーダーが来たな。さて、君とパーティーメンバーの準備はいいか？それではアモリアパーティークエストの入口へ送ろう。一人ひとりの健闘を祈る！", false, true);
            return true;
        }
        if (status != 2) {
            return false;
        }
        if (this.menu == 1) {
            warp(this.map_training, "st00");
            return false;
        }
        int res = enterFieldSetParty(this.field_set_name, 0);
        switch (res) {
            case TacosFieldSet.ENTER_SUCCESS -> {
                for (int item : this.items_quest) {
                    removeItemAll(item);
                }
            }
            case TacosFieldSet.ENTER_NO_PARTY -> {
                // 翻訳
                say("パーティーに入っていないな。このクエストはパーティーに入っているときだけ挑戦できる。");
            }
            case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                // 翻訳
                say("パーティーが6人ではない。パーティーメンバーが6人そろったら戻ってきてくれ。");
            }
            case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                // 翻訳
                say("パーティーの中にレベル40を超えていない者がいる。もう一度確認してくれ。");
            }
            case TacosFieldSet.ENTER_USING -> {
                // 翻訳
                say("すでにほかのパーティーがクエストのクリアを目指して入っている。しばらくしてからもう一度試してくれ。");
            }
            default -> {
                // 翻訳
                say("不明な理由により、君たちのパーティーを入れることはできない。しばらくしてからもう一度試してくれ。");
            }
        }
        return false;
    }
}
