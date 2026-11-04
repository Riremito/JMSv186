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
// Npc.wz script : moonrabbit (JMS131-302), 会話文は lua_script_jp (moonrabbit) を利用
// 期間の確認は削除
public class Npc_Henesys_Usachan extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_ENTER = 1;
    private static final int NODE_OUT = 2;
    private final String field_set_name = "MoonRabbit";
    private final int map_square = 100000200; // ヘネシス : 広場
    private final int map_shortcut = 910010100; // 近道
    private final int map_shortcut_pig = 910010400; // 近道
    private final int[] item_moon_rabbit = {
        4001095, // 月見草の種(緑)
        4001096, // 月見草の種(紺)
        4001097, // 月見草の種(紫)
        4001098, // 月見草の種(茶)
        4001099, // 月見草の種(黄)
        4001100, // 月見草の種(青)
        4001101, // 月兎の餅
    };
    private final int party_min = 3;
    private final int level_min = 10;
    private int node = NODE_START;

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        // 翻訳
        switch (this.node) {
            case NODE_START -> {
                if (map_id == this.map_square) {
                    if (!isPartyBoss()) {
                        String[] says = {
                            "やあ！僕はトリー。ここは満月の神秘的なオーラに満ちていて、#Gひとり:ひとり#では誰も入れないんだ。",
                            "ここに入りたいなら、パーティーリーダーが僕に話しかける必要があるよ。リーダーと相談してね。"
                        };
                        if (status < 0 || says.length <= status) {
                            return false;
                        }
                        say(says[status], 0 < status, status < says.length - 1);
                        return status < says.length - 1;
                    }
                    if (status == 0) {
                        say("やあ！僕はトリー。この中には、プリムラが咲く美しい山があるんだ。その山にはグローリーっていう虎が住んでいて、何か食べるものを探しているみたいなんだ。", false, true);
                        return true;
                    }
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("プリムラの山へ行って、パーティーと力を合わせてグローリーを助けてくれないかな？\r\n#b#L0# うん、行くよ。#l", false);
                    askMenu(nt);
                    this.node = NODE_ENTER;
                    return true;
                }
                if (map_id == this.map_shortcut || map_id == this.map_shortcut_pig) {
                    NpcScriptTag nt = new NpcScriptTag();
                    if (map_id == this.map_shortcut) {
                        nt.add("お腹を空かせたグローリーに餅を持ってきてくれてありがとう。もうここですることはなさそうだね。ここから出たい？ \r\n#b#L0# うん。ここから出して。#l", false);
                    } else {
                        nt.add("みんな、ブタたちを追い払うのはもう終わった？もうここですることはなさそうだね。ここから出たい？ \r\n#b#L0# うん、ここから出たい。#l", false);
                    }
                    askMenu(nt);
                    this.node = NODE_OUT;
                    return true;
                }
                return false;
            }
            case NODE_ENTER -> {
                // FieldSet にパーティーの人数とレベルの制限がないため、ここで確認する
                int res = TacosFieldSet.ENTER_SUCCESS;
                if (TacosFieldSet.getPartyMembers(getPlayer()).size() < this.party_min) {
                    res = TacosFieldSet.ENTER_PARTY_MEMBER;
                }
                for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
                    if (member.getLevel() < this.level_min) {
                        res = TacosFieldSet.ENTER_PARTY_LEVEL;
                    }
                }
                if (res == TacosFieldSet.ENTER_SUCCESS) {
                    res = enterFieldSetParty(this.field_set_name, 0);
                }
                switch (res) {
                    case TacosFieldSet.ENTER_SUCCESS -> {
                        // lua の moonrabbit_takeawayitem
                        for (int item_id : this.item_moon_rabbit) {
                            removeItemAll(item_id);
                        }
                    }
                    case TacosFieldSet.ENTER_NO_PARTY -> {
                        say("こんにちは！僕はトリー。ここは満月の神秘的なオーラに満ちていて、#Gひとり:ひとり#では誰も入れないんだ。入りたいなら、#bパーティーリーダー#kが僕に話しかけてね。");
                    }
                    case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                        say("ごめんね、君のパーティーはメンバーが3人に「満たない」よ。レベル10以上のメンバー3人以上でパーティーを組んでね。準備ができたら教えてね。");
                    }
                    case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                        say("ごめんね、パーティーの中にレベル10未満の人がいるよ。全員がレベル10以上のメンバー3人以上でパーティーを組んでね。準備ができたら教えてね。");
                    }
                    case TacosFieldSet.ENTER_USING -> {
                        say("ごめんね、中で別のパーティーがクエストをクリアしようとしているんだ。少ししてからまた話しかけてね。");
                    }
                    default -> {
                        say("理由があって、今は入れてあげられないんだ。しばらくしてからもう一度試してね。");
                    }
                }
                return false;
            }
            case NODE_OUT -> {
                // lua の moonrabbit_takeawayitem
                for (int item_id : this.item_moon_rabbit) {
                    removeItemAll(item_id);
                }
                warp(this.map_square);
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
