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

import java.util.ArrayList;
import tacos.client.TacosCharacter;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : davyJohn_enter (JMS131-302), 会話文は lua_script_jp (davyJohn_enter) を利用
public class Npc_HerbTown_KikyoKorai extends NpcScriptMan {

    private final String field_set_name = "DavyJohn1";
    private final int map_outside = 251010404; // 白草村 : 海賊船の向こう
    // パーティークエストのマップ
    private final int[] maps_quest = {
        925100000, // 海賊船への道
        925100100, // 船首突破
        925100200, // 甲板突破1
        925100201, // 100年生キキョウの領域1
        925100202, // デイビーゾーンの手下1
        925100300, // 甲板突破2
        925100301, // 100年生キキョウの領域2
        925100302, // デイビーゾーンの手下2
        925100400, // 海賊退治!
        925100500 // 海賊船長の威厳
    };
    // パーティークエストで入手するアイテム (lua の davy_takeawayitem)
    private final int[] items_quest = {
        4001117, // 古い鍵
        4001119, // 古地図
        4001120, // 新入海賊の証
        4001121, // 中級海賊の証
        4001122, // 高級海賊の証
        4001123 // 透明アイテム
    };
    private final int party_member_min = 3;
    private final int level_min = 55;
    private final int level_max = 100;
    private final int level_over = 70;

    @Override
    public boolean action(int status, int answer) {
        // GM 用の処理 (davy_master) は削除
        if (getMapId() != this.map_outside) {
            return false;
        }
        if (!isPartyBoss()) {
            // 翻訳
            say("代表者が私に話しかけてください。");
            return false;
        }
        int num = 0;
        for (int map_id : this.maps_quest) {
            num += getUserCount(map_id);
        }
        if (0 < num) {
            // 翻訳
            say("すでに別のパーティーがクエストのクリアを目指して中に入っています。しばらくしてからもう一度お試しください。");
            return false;
        }
        ArrayList<TacosCharacter> members = TacosFieldSet.getPartyMembers(getPlayer());
        // lua では FieldSet の入場条件で判定しているが、FieldSet のデータに条件がないため NPC で判定する
        int res = TacosFieldSet.ENTER_SUCCESS;
        if (members.size() < this.party_member_min) {
            res = TacosFieldSet.ENTER_PARTY_MEMBER;
        }
        for (TacosCharacter member : members) {
            if (res == TacosFieldSet.ENTER_SUCCESS && (member.getLevel() < this.level_min || this.level_max < member.getLevel())) {
                res = TacosFieldSet.ENTER_PARTY_LEVEL;
            }
        }
        // 入場前のパーティーの情報
        int level_total = 0;
        int over = 0;
        for (TacosCharacter member : members) {
            level_total += member.getLevel();
            if (this.level_over < member.getLevel()) {
                over++;
            }
        }
        if (res == TacosFieldSet.ENTER_SUCCESS) {
            res = enterFieldSetParty(this.field_set_name, 0);
        }
        // 翻訳
        switch (res) {
            case TacosFieldSet.ENTER_SUCCESS -> {
                TacosFieldSet field_set = getFieldSet(this.field_set_name);
                field_set.resetQuestTime();
                // 入場記録 (davy_daycheck) は lua でもコメントアウトされている
                for (int item_id : this.items_quest) {
                    removeItemAll(item_id);
                }
                field_set.setVar("over70", String.valueOf(over));
                field_set.setVar("eLevel", String.valueOf(level_total / members.size()));
                field_set.setVar("eNum", String.valueOf(members.size()));
            }
            case TacosFieldSet.ENTER_NO_PARTY -> {
                say("あなたはどのパーティーにも所属していないようです。このクエストにはパーティーに所属している場合のみ挑戦できます。");
            }
            case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                say("あなたのパーティーは、このクエストの参加に必要な最低人数である3人を満たしていません。パーティーメンバーを増やしてください。");
            }
            case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                say("あなたかパーティーメンバーの誰かが、レベル55～100の範囲外です。メンバー全員がその範囲内になるようにパーティーを組んでください。");
            }
            case TacosFieldSet.ENTER_USING -> {
                say("すでに別のパーティーがクエストのクリアを目指して中に入っています。しばらくしてからもう一度お試しください。");
            }
            default -> {
                say("不明な理由により入場できません。少ししてからもう一度お試しください。");
            }
        }
        return false;
    }
}
