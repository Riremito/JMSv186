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
package tacos.script.portal;

import odin.handling.world.MapleParty.MaplePartyCharacter;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : s4rush (JMS186), lua の処理を利用
// 4次職の戦士 2 人のパーティーで入場する
public class Portal_Sleepywood_S4rush extends PortalScriptMan {

    private final int quest_rush = 6110; // ラッシュのクエスト
    private final int job_hero = 112;
    private final int job_paladin = 122;
    private final int job_dark_knight = 132;
    private final int warrior_count = 2;

    @Override
    public boolean enter() {
        if (getQuestStatus(this.quest_rush) != 1) {
            // 翻訳
            message("封印された場所には入れません。");
            return false;
        }
        if (!isPartyBoss()) {
            // 翻訳
            message("戦士2人で構成されたパーティーのリーダーが、入場を決められます。");
            return false;
        }
        // lua の s4rushPcheck
        int count = 0;
        for (MaplePartyCharacter member : getPlayer().getParty().getMembers()) {
            int job = member.getJobId();
            if (job == this.job_hero || job == this.job_paladin || job == this.job_dark_knight) {
                count++;
            }
        }
        if (count != this.warrior_count) {
            // 翻訳
            message("入場できません。パーティーメンバーの職業が戦士ではないか、パーティーが2人で構成されていません。");
            return false;
        }
        TacosFieldSet field_set = getFieldSet("S4rush");
        int res = field_set == null ? TacosFieldSet.ENTER_ERROR : field_set.enterParty(getPlayer(), 0);
        switch (res) {
            case TacosFieldSet.ENTER_SUCCESS -> {
                return true;
            }
            case TacosFieldSet.ENTER_NO_PARTY -> {
                // 翻訳
                message("パーティーを組んでいません。パーティーで挑戦できます。");
            }
            case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                // 翻訳
                message("2人のパーティーを組んでいるとクエストを行えます。2人でパーティーを組んでください。");
            }
            case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                // 翻訳
                message("パーティーの中にレベル条件を満たしていないキャラクターがいます。レベル120以上である必要があります。レベルを調整してください。");
            }
            case TacosFieldSet.ENTER_USING -> {
                // 翻訳
                message("現在ほかのパーティーがクエストのクリアに挑戦中です。しばらくしてからもう一度お試しください。");
            }
            default -> {
                // 翻訳 (lua では self.say)
                message("不明な理由により入場できません。もう一度お試しください。");
            }
        }
        return false;
    }
}
