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

import odin.handling.world.MapleParty.MaplePartyCharacter;
import tacos.client.TacosCharacter;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : s4common2 (JMS131-302), 会話文は lua_script_jp (s4common2) を利用
public class Npc_Aquarium_Carta extends NpcScriptMan {

    private final int quest_cracked_dimension = 6301; // 4次転職 (ひび割れた次元)
    private final int item_piece = 4031472; // 意思の欠片
    private final int item_miniature = 4000175; // ワルメンボウのミニチュア
    private final int required_piece = 40;
    private final int required_level = 120;

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        if (getQuestStatus(this.quest_cracked_dimension) != 1) {
            say("ひび割れた次元？どこでその話を聞いたのですか？");
            return false;
        }
        if (!isPartyBoss()) {
            say("入場を申請できるのはパーティーリーダーのみです。代表者が私に話しかけてください。");
            return false;
        }
        if (this.required_piece <= getItemCount(this.item_piece)) {
            say("#b#t" + this.item_piece + "##kを" + this.required_piece + "個持っているなら、これ以上は必要ありません。");
            return false;
        }
        if (!checkItem(this.item_miniature)) {
            say("#b#t" + this.item_miniature + "##kがないと、ひび割れた次元に入れません。");
            return false;
        }
        for (MaplePartyCharacter mpc : getPlayer().getParty().getMembers()) {
            int job = mpc.getJobId();
            if (job % 10 != 2 || job < 100 || 500 <= job) {
                say("パーティーの中に4次転職していない人がいると入場できません。");
                return false;
            }
        }
        for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
            if (member.getLevel() < this.required_level) {
                say("パーティーの中にレベル条件を満たしていないキャラクターがいます。レベル" + this.required_level + "以上である必要があります。レベルを調整してください。");
                return false;
            }
        }
        switch (enterFieldSetParty("S4common2", 0)) {
            case TacosFieldSet.ENTER_SUCCESS -> {
                exchange(0, this.item_miniature, -1);
            }
            case TacosFieldSet.ENTER_NO_PARTY -> {
                say("パーティーを組んでいません。パーティーで挑戦できます。");
            }
            case TacosFieldSet.ENTER_USING -> {
                say("現在ほかのパーティーがクエストのクリアに挑戦中です。しばらくしてからもう一度お試しください。");
            }
            default -> {
                say("不明な理由により入場できません。もう一度お試しください。");
            }
        }
        return false;
    }
}
