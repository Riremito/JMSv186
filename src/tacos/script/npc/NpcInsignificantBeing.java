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
// Npc.wz script : s4snipe (JMS131-302), 会話文は lua_script_jp (s4snipe) を利用
public class NpcInsignificantBeing extends NpcScriptMan {

    private final int quest_snipe = 6108; // 4次転職 (スナイプ)
    private final int quest_snipe_pre = 6107; // 4次転職 (スナイプの前提)
    private final int required_members = 2;
    private final int required_level = 120;

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        if (getPlayer().getQuestStatus(this.quest_snipe) != 1 || getPlayer().getQuestStatus(this.quest_snipe_pre) != 2) {
            say("何の話をしているのかわかりません。");
            return false;
        }
        if (getPlayer().getParty() == null || getPlayer().getParty().getLeader().getId() != getPlayer().getId()) {
            say("4次転職した弓使い2人でパーティーを組んだら、パーティーリーダーが私に話しかけてください。");
            return false;
        }
        // ボウマスター, クロスボウマスター
        int count = 0;
        for (MaplePartyCharacter mpc : getPlayer().getParty().getMembers()) {
            if (mpc.getJobId() == 312 || mpc.getJobId() == 322) {
                count++;
            }
        }
        if (count != this.required_members) {
            say("入場できません。パーティーメンバーの職業がボウマスターかクロスボウマスターではないか、パーティーが" + this.required_members + "人で構成されていません。");
            return false;
        }
        if (TacosFieldSet.getPartyMembers(getPlayer()).size() != this.required_members) {
            say(this.required_members + "人のパーティーを組んでいるとクエストを行えます。" + this.required_members + "人でパーティーを組んでください。");
            return false;
        }
        for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
            if (member.getLevel() < this.required_level) {
                say("パーティーの中にレベル条件を満たしていないキャラクターがいます。レベル" + this.required_level + "以上である必要があります。レベルを調整してください。");
                return false;
            }
        }
        switch (enterFieldSetParty("S4snipe", 0)) {
            case TacosFieldSet.ENTER_SUCCESS -> {
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
