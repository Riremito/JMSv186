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

import odin.server.MapleInventoryManipulator;
import odin.server.Randomizer;
import odin.server.quest.MapleQuest;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : ludi020 (JMS131-302), 会話文は lua_script_jp (ludi020) を利用
public class Npc_Ludibrium_AssistantCheng extends NpcScriptMan {

    private static final int MODE_NONE = 0;
    private static final int MODE_ENTER = 1;
    private static final int MODE_MENU = 2;
    private static final int MODE_REWARD = 3;
    private static final int MODE_WARP = 4;
    private final int quest_parts = 3239; // おもちゃ工場の機械部品
    private final int item_parts = 4031092; // 機械部品
    private final int required_parts = 10;
    private final int map_factory = 220020000; // ルディブリアム : オモチャ工場-1工程<1区域>
    private final int map_inside = 922000000; // オモチャ工場<4 区域>
    private final int map_exit = 922000009; // 秘密通路
    // お礼
    private final int[] rewards = {
        2040704, // ジャンプの書(靴)60%
        2040705, // ジャンプの書(靴)10%
        2040707, // 移動速度の書(靴)60%
        2040708 // 移動速度の書(靴)10%
    };
    private int mode = MODE_NONE;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        // いいえを選択した
        if (status == 0 && this.asked) {
            // 翻訳
            say("それこそ私が求めていた心構えです！何かを始めたら、最後までやり遂げることを学ばないと！さあ、プラスチックの樽を調べて、#b#t" + this.item_parts + "#を" + this.required_parts + "個#k見つけてきてください。");
            return false;
        }
        if (status == 0) {
            int state = getPlayer().getQuestStatus(this.quest_parts);
            if (state == 2) {
                // 翻訳
                say("あなたのおかげで、おもちゃ工場はまた完璧に動いています。手伝いに来てくれて本当にうれしいです。予備の部品はちゃんと管理しているので、心配いりません。さて、それでは！仕事に戻らないと！");
                return false;
            }
            if (state != 1) {
                // 翻訳
                say("最近、おもちゃ工場から機械部品がなくなっていて、とても心配しているんです。手伝いをお願いしたいのですが、あなたは私たちを助けられるほど強くはなさそうですね。誰に助けを求めればいいんでしょう？");
                return false;
            }
            if (getMapId() == this.map_factory) {
                this.mode = MODE_ENTER;
                // 翻訳
                say("よし、それじゃあ。その部屋の中には、プラスチックの樽があちこちに転がっています。樽を叩いて倒し、なくなった#b#t" + this.item_parts + "##kが見つかるか探してください。#b#t" + this.item_parts + "#を" + this.required_parts + "個#k集めたら、戻ってきて私に話しかけてください。制限時間がありますよ！さあ、急いで！", false, true);
                return true;
            }
            if (getMapId() != this.map_inside) {
                return false;
            }
            int count = getPlayer().getItemQuantity(this.item_parts, false);
            if (this.required_parts <= count) {
                this.mode = MODE_REWARD;
                // 翻訳
                say("よくやりました！#b#t" + this.item_parts + "#を" + this.required_parts + "個#k集めてくれましたね。こんなに大きな手助けをしてくれたので、素敵なお礼をあげましょう。その前に、消費インベントリに空きがあるか確認してください。", false, true);
                return true;
            }
            this.mode = MODE_MENU;
            NpcScriptTag nt = new NpcScriptTag();
            if (count == 0) {
                // 翻訳
                nt.add("その部屋の中には、プラスチックの樽があちこちに転がっています。樽を叩いて倒し、なくなった#b#t" + this.item_parts + "##kが見つかるか探してください。制限時間が終わる前に#b#t" + this.item_parts + "#を" + this.required_parts + "個#k集めて、戻ってきて私に話しかけてください。話している間にも時間は過ぎていますから、急いでください！\r\n#b#L0# ここから出たい。#l", false);
            } else {
                // 翻訳
                nt.add("まだなくなった#b#t" + this.item_parts + "##kを" + this.required_parts + "個集めていないようですね。この部屋にあるプラスチックの樽を壊して、なくなった#b#t" + this.item_parts + "##kが入っていないか探してください。制限時間が終わる前に#b#t" + this.item_parts + "##kを" + this.required_parts + "個集めたら、私のところに持ってきてください。いつでもここから出たくなったら、私に話しかけてくださいね。\r\n#b#L0# ここから出たい。#l", false);
            }
            askMenu(nt);
            return true;
        }
        switch (this.mode) {
            case MODE_ENTER -> {
                int count = getPlayer().getItemQuantity(this.item_parts, false);
                if (0 < count) {
                    exchange(0, this.item_parts, -count);
                }
                if (enterFieldSet("Ludi020", 0) != TacosFieldSet.ENTER_SUCCESS) {
                    // 翻訳
                    say("すみませんが、ほかの人が中で樽を調べているようです。ここに入れるのは一度に1人だけなので、順番を待ってください。");
                }
                return false;
            }
            case MODE_MENU -> {
                switch (status) {
                    case 1 -> {
                        if (answer != 0) {
                            return false;
                        }
                        this.asked = true;
                        // 翻訳
                        askYesNo("本当に諦めますか？まあ、外に出してあげることはできますが、次にここに来たときは最初からやり直しになりますよ。それでもここから出ますか？");
                        return true;
                    }
                    case 2 -> {
                        warp(this.map_exit);
                        return false;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case MODE_REWARD -> {
                // TODO : lua では 4 番目のアイテムが選ばれない (選ばれた場合は交換に失敗する)
                int reward = this.rewards[Randomizer.rand(0, this.rewards.length - 1)];
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), reward, 1, "")) {
                    // 翻訳
                    say("うーん…今は消費インベントリがいっぱいのようですね。これではお礼を受け取れません。インベントリに空きを作ってから、もう一度話しかけてください。");
                    return false;
                }
                int count = getPlayer().getItemQuantity(this.item_parts, false);
                if (!exchange(0, this.item_parts, -count, reward, 1)) {
                    // 翻訳
                    say("本当に#b#t" + this.item_parts + "#を" + this.required_parts + "個#k持っていますか？確かなら、消費インベントリに空きがあるか確認してください。");
                    return false;
                }
                getPlayer().gainExp(2700, true, true, true);
                MapleQuest.getInstance(this.quest_parts).forceComplete(getPlayer(), getNpcId());
                this.mode = MODE_WARP;
                // 翻訳
                say("どうですか？私があげた#b#t" + reward + "##kは気に入りましたか？手伝ってくれて、どうお礼を言えばいいかわかりません。あなたの頑張りのおかげで、おもちゃ工場は順調に動いているはずです。それでは外へお送りしますね。お元気で！", false, true);
                return true;
            }
            case MODE_WARP -> {
                warp(this.map_factory, "q000");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
