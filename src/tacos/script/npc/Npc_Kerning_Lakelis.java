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

import tacos.client.TacosCharacter;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : party1_enter (JMS131-302), 会話文は lua_script_jp (party1_enter) を利用
public class Npc_Kerning_Lakelis extends NpcScriptMan {

    private final String field_set_name = "Party1";
    private final int item_coupon = 4001007; // クーポン
    private final int item_pass = 4001008; // 通行券
    private final int party_member = 4;
    private final int level_min = 21;
    private final int level_max = 30;

    @Override
    public boolean action(int status, int answer) {
        if (!isPartyBoss()) {
            // 翻訳
            say("パーティーと一緒にクエストに挑戦してみない？ここには、チームワークでしか解決できない障害や問題が待っているわ。挑戦したいなら、#bパーティーリーダー#kに私に話しかけるよう伝えてね。");
            return false;
        }
        int res;
        // lua では FieldSet の入場条件で判定しているが、FieldSet のデータに条件がないため NPC で判定する
        if (TacosFieldSet.getPartyMembers(getPlayer()).size() != this.party_member) {
            res = TacosFieldSet.ENTER_PARTY_MEMBER;
        } else {
            res = TacosFieldSet.ENTER_SUCCESS;
            for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
                if (member.getLevel() < this.level_min || this.level_max < member.getLevel()) {
                    res = TacosFieldSet.ENTER_PARTY_LEVEL;
                    break;
                }
            }
            if (res == TacosFieldSet.ENTER_SUCCESS) {
                res = enterFieldSetParty(this.field_set_name, 0);
            }
        }
        // 翻訳
        switch (res) {
            case TacosFieldSet.ENTER_SUCCESS -> {
                removeItemAll(this.item_coupon);
                removeItemAll(this.item_pass);
            }
            case TacosFieldSet.ENTER_NO_PARTY -> {
                say("パーティーに入っていないわね。このクエストはパーティーに入っているときだけ挑戦できるの。");
            }
            case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                say("パーティーが4人じゃないわ。4人そろったらまた来てね。");
            }
            case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                say("パーティーの中にレベル21～30ではない人がいるわ。もう一度確認してね。");
            }
            case TacosFieldSet.ENTER_USING -> {
                say("すでに別のパーティーがクエストのクリアを目指して入っているわ。しばらくしてからもう一度試してね。");
            }
            default -> {
                say("理由があって、あなたのパーティーを入れてあげられないの。しばらくしてからもう一度試してね。");
            }
        }
        return false;
    }
}
