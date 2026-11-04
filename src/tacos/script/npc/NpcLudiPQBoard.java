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
// Npc.wz script : party2_enter (JMS131-302), 会話文は lua_script_jp (party2_enter) を利用
public class NpcLudiPQBoard extends NpcScriptMan {

    private final String field_set_name = "Party3";
    private final int item_pass = 4001022; // 次元の通行券
    private final int item_key = 4001023; // 次元のカギ
    private final int party_member = 6;
    private final int level_min = 35;
    private final int level_max = 50;

    @Override
    public boolean action(int status, int answer) {
        if (!isPartyBoss()) {
            // 翻訳
            say("ここから先は危険な物やモンスターでいっぱいだ。だから、これ以上先へは進ませられない。だが、私たちを救ってルディブリアムに平和を取り戻してくれるというなら、話は別だ。頂上に住む強力な生き物を倒したいなら、パーティーメンバーを集めてくれ。簡単ではないが…君ならできると思う。");
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
                removeItemAll(this.item_pass);
                removeItemAll(this.item_key);
            }
            case TacosFieldSet.ENTER_NO_PARTY -> {
                say("パーティーに入っていないな。このクエストにはパーティーに入っている場合のみ参加できる。");
            }
            case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                say("パーティーが6人ではないので、クエストに参加できない。6人でパーティーを組んでくれ。");
            }
            case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                say("パーティーの中にレベル35～50ではない人がいる。それを解決してからもう一度試してくれ。");
            }
            case TacosFieldSet.ENTER_USING -> {
                say("別のパーティーが中でクエストに参加している。そのパーティーが空けてからもう一度試してくれ。");
            }
            default -> {
                say("理由があって、入れてあげられないんだ。しばらくしてからもう一度試してね。");
            }
        }
        return false;
    }
}
