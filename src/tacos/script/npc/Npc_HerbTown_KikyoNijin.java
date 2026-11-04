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

import odin.client.inventory.MapleInventoryType;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : davy_clear (JMS131-302), 会話文は lua_script_jp (davy_clear) を利用
public class Npc_HerbTown_KikyoNijin extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_CLEAR = 1;
    private static final int NODE_MENU = 2;
    private static final int NODE_HAT = 3;
    private static final int NODE_RESET_ASK = 4;
    private static final int NODE_RESET = 5;
    private final String field_set_name = "DavyJohn1";
    private final String field_set_clear_name = "DavyJohn6";
    private final int map_captain = 925100500; // 海賊船長の威厳
    private final int map_thanks = 925100600; // キキョウニジンの感謝
    private final int map_exit = 925100700; // 海賊船出口
    private final int quest_clear_count = 7040; // 海賊王の討伐回数
    private final int quest_hat = 7041; // デイビーゾーンの帽子の受け取り状況
    // デイビーゾーンの帽子 (段階ごと)
    private final int[] items_hat = {
        1002571, // デイビーゾーンの帽子
        1002572, // デイビーゾーンの帽子
        1002573, // デイビーゾーンの帽子
        1002574 // デイビーゾーンの帽子
    };
    // デイビーゾーンの帽子を受け取るのに必要な討伐回数 (テストサーバーの値は削除)
    private final int[] give_hat_counts = {50, 150, 300, 500};
    private final int clear_count_max = 500;
    // パーティークエストで入手するアイテム (lua の davy_takeawayitem)
    private final int[] items_quest = {
        4001117, // 古い鍵
        4001119, // 古地図
        4001120, // 新入海賊の証
        4001121, // 中級海賊の証
        4001122, // 高級海賊の証
        4001123 // 透明アイテム
    };
    private int node = NODE_START;
    private int last_status = 0;
    private int clear_count = 0;
    private int give = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        int map_id = getMapId();
        switch (this.node) {
            case NODE_START -> {
                if (map_id == this.map_captain) {
                    if (!isPartyBoss()) {
                        // 翻訳
                        say("クルーの代表者が私に話しかけてください。");
                        return false;
                    }
                    // 翻訳
                    say("助けてくれて本当にありがとうございます。これで、町の桔梗たちを滅ぼそうとしていた海賊王の邪悪な怒りから逃れることができます。これからあなたを外へお送りします。外に出たら、もう一度私に話しかけてください。", false, true);
                    this.node = NODE_CLEAR;
                    return true;
                }
                if (map_id != this.map_thanks) {
                    return false;
                }
                // lua の davy_ini
                if (getQuestData(this.quest_clear_count).isEmpty()) {
                    setQuestData(this.quest_clear_count, "0");
                }
                if (getQuestData(this.quest_hat).isEmpty()) {
                    setQuestData(this.quest_hat, "0");
                }
                try {
                    this.clear_count = Integer.parseInt(getQuestData(this.quest_clear_count)) + 1;
                    this.give = Integer.parseInt(getQuestData(this.quest_hat));
                } catch (NumberFormatException e) {
                    return false;
                }
                if (this.clear_count_max <= this.clear_count) {
                    this.clear_count = this.clear_count_max;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                if (this.give == 4) {
                    nt.add("#b海賊王#kを倒して、私を救ってくださって本当にありがとうございます。何かお手伝いできることはありますか？\r\n#b#L1# ここから出る。#l", false);
                } else {
                    nt.add("#b海賊王#kを倒して、私を救ってくださって本当にありがとうございます。何かお手伝いできることはありますか？\r\n#b#L0#海賊王の討伐回数を確認する。#l\r\n#L1#海賊王の討伐回数をリセットする。#l\r\n#L2#ここから出る。#l", false);
                }
                askMenu(nt);
                this.node = NODE_MENU;
                return true;
            }
            case NODE_CLEAR -> {
                TacosFieldSet field_set = getFieldSet(this.field_set_name);
                TacosFieldSet field_set_clear = getFieldSet(this.field_set_clear_name);
                if (field_set == null || field_set_clear == null) {
                    return false;
                }
                // lua の cExp
                int clear_exp;
                try {
                    int over = Integer.parseInt(field_set.getVar("over70"));
                    int level = Integer.parseInt(field_set.getVar("eLevel"));
                    if (over == 0) {
                        clear_exp = 42000;
                    } else if (level <= 70) {
                        clear_exp = 35000;
                    } else if (level <= 80) {
                        clear_exp = 28000;
                    } else if (level <= 90) {
                        clear_exp = 20000;
                    } else {
                        clear_exp = 10000;
                    }
                } catch (NumberFormatException e) {
                    clear_exp = 42000;
                }
                if (clear_exp < 35000) {
                    field_set_clear.setVar("giveExp", "1");
                }
                if (!field_set_clear.getVar("clear").equals("1")) {
                    if (field_set_clear.getVar("giveExp").equals("1")) {
                        // 翻訳
                        field_set_clear.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "パーティーメンバーの中にレベル70を超える人がいるため、クエストクリアで獲得できる経験値が少し減少します。");
                    }
                    field_set_clear.incExpAll(clear_exp);
                    field_set_clear.setVar("clear", "1");
                }
                // 期間限定のイベント (2007年, 休み特別経験値) の処理は削除
                // GM 用のクリア記録 (davy_daycheck2) の処理は削除
                field_set_clear.transferFieldAll(this.map_thanks, "st00");
                return false;
            }
            case NODE_MENU -> {
                String name = getCharacterName();
                // lua では give == 4 の場合も #L1# (ここから出る) で討伐回数のリセットの処理になる
                if (answer == 1) {
                    if (this.clear_count < this.give_hat_counts[0] || this.give == 0) {
                        // 翻訳
                        say("海賊王を少なくとも" + this.give_hat_counts[0] + "回倒していないか、海賊王の帽子を受け取っていないため、海賊王との戦績をリセットできません。");
                        return false;
                    }
                    // 翻訳
                    askYesNo("#b" + name + "#k様が海賊王を#b" + this.clear_count + "回#k倒しましたが、もし#b#t" + this.items_hat[2] + "##kをなくしてしまったのなら、最初からやり直すこともできます。どうしますか？海賊王との戦績を0にリセットしますか？");
                    this.node = NODE_RESET_ASK;
                    return true;
                }
                if (answer == 2) {
                    setQuestData(this.quest_clear_count, String.valueOf(this.clear_count));
                    for (int item_id : this.items_quest) {
                        removeItemAll(item_id);
                    }
                    // 期間限定のイベント (2007年, クエスト 9664) の処理は lua でもコメントアウトされている
                    warp(this.map_exit);
                    return false;
                }
                if (4 <= this.give) {
                    // 翻訳
                    if (this.clear_count < 10000) {
                        say("#b" + name + "#k様、あなたは海賊王を#b" + this.clear_count + "回#k以上倒し、海賊王の耐えがたい支配から私たちを解放してくれました。本当にありがとうございます。");
                    } else {
                        say("#b" + name + "#k様、あなたは海賊王を#b10,000回#k以上倒し、海賊王の耐えがたい支配から私たちを解放してくれました。本当にありがとうございます。");
                    }
                    return false;
                }
                int need = this.give_hat_counts[this.give];
                int hat = this.items_hat[0];
                // 翻訳
                if (this.clear_count < need) {
                    switch (this.give) {
                        case 0 -> {
                            say("#b" + name + "#kは海賊王を#b" + this.clear_count + "回#k倒しましたが、残念ながら、それだけでは私たちが海賊王の怒りから逃れるには足りません。まずは#b" + need + "回#k倒していただきたいのです。そうすれば本当に助かります。");
                        }
                        case 1, 2 -> {
                            say("#b" + name + "#k様、海賊王を#b" + this.clear_count + "回#k倒してくれてありがとうございます。残念ながら、それだけでは私たちが海賊王の怒りから逃れるには足りません。もう一度中に入って、あと#b" + need + "回#k倒してくだされば、とても助かります。");
                        }
                        default -> {
                            say("#b" + name + "#k様が海賊王を#b" + this.clear_count + "回#k倒してくれて、本当にほっとしています。残念ながら、私たちはまだ海賊王の怒りから逃れられていません。もう一度中に入って、あと#b" + need + "回#k倒してくだされば、とても助かります。");
                        }
                    }
                    return false;
                }
                String count_text = (this.clear_count < this.clear_count_max) ? "#b" + this.clear_count + "回#k" : "少なくとも#b500回#k";
                switch (this.give) {
                    case 0 -> {
                        say("あなたは海賊王を" + count_text + "倒しました。私たち桔梗を救ってくれたお礼として、#b" + need + "#k回以上倒してくれたので、#b#t" + hat + "##kを差し上げます。", false, true);
                    }
                    case 1 -> {
                        if (this.clear_count < this.clear_count_max) {
                            say("あなたは海賊王を" + count_text + "倒したようですね。海賊王から私たちを救ってくれた桔梗一同からの感謝のしるしとして、#b" + need + "回#k以上倒してくれたので、あなたの#b#t" + hat + "##kを強化します。ただし、現在装備している場合は、外して装備インベントリに入れておいてください。", false, true);
                        } else {
                            say("あなたは海賊王を" + count_text + "倒しました。海賊王から私たちを救ってくれた桔梗一同からの感謝のしるしとして、#b" + need + "回#k以上倒してくれたので、あなたの#b#t" + hat + "##kを強化します。ただし、現在装備している場合は、外して装備インベントリに入れておいてください。", false, true);
                        }
                    }
                    case 2 -> {
                        if (this.clear_count < this.clear_count_max) {
                            say("あなたは海賊王を" + count_text + "倒したようですね。海賊王から私たちを救ってくれた桔梗一同からの感謝のしるしとして、#b" + need + "回#k倒してくれたので、あなたの#b#t" + hat + "##kを強化します。現在装備している場合は、必ず外して装備インベントリに入れておいてください。", false, true);
                        } else {
                            say("あなたは海賊王を" + count_text + "倒しました。海賊王から私たちを救ってくれた桔梗一同からの感謝のしるしとして、#b" + need + "回#k以上倒してくれたので、あなたの#b#t" + hat + "##kを強化します。ただし、現在装備している場合は、外して装備インベントリに入れておいてください。", false, true);
                        }
                    }
                    default -> {
                        if (this.clear_count < this.clear_count_max) {
                            say("あなたは海賊王を" + count_text + "倒したようですね。あなたの偉業のおかげで、私たち桔梗は海賊王の邪悪な支配から逃れることができました。お礼として、#b" + need + "回#kを超えたので、#b#t" + this.items_hat[3] + "##kを最高の逸品に仕上げます。ただし、現在装備している場合は、外して装備インベントリに入れておいてください..", false, true);
                        } else {
                            say("あなたは海賊王を" + count_text + "倒しました。あなたの偉業のおかげで、私たち桔梗は海賊王の邪悪な支配から逃れることができました。お礼として、#b" + need + "回#kを超えたので、#b#t" + this.items_hat[3] + "##kを最高の逸品に仕上げます。ただし、現在装備している場合は、外して装備インベントリに入れておいてください。", false, true);
                        }
                    }
                }
                this.node = NODE_HAT;
                return true;
            }
            case NODE_HAT -> {
                int hat = this.items_hat[0];
                int hat_new = this.items_hat[this.give];
                // 翻訳
                if (!hasEmptySlot(MapleInventoryType.EQUIP)) {
                    if (this.give == 0) {
                        say("装備インベントリがいっぱいでないか確認してください。インベントリがいっぱいだと#b#t" + hat + "##kを渡せません。");
                    } else if (this.give == 1) {
                        say("装備インベントリがいっぱいでないか確認してください。インベントリがいっぱいだと#b#t" + hat_new + "##kを渡せません..");
                    } else {
                        say("装備インベントリがいっぱいでないか確認してください。インベントリがいっぱいだと#b#t" + hat_new + "##kを渡せません。");
                    }
                    return false;
                }
                if (0 < getItemCount(hat_new)) {
                    say("あなたはすでに#b#t" + hat + "##kを持っています。");
                    return false;
                }
                boolean ret = (this.give == 0) ? exchange(0, hat_new, 1) : exchange(0, this.items_hat[this.give - 1], -1, hat_new, 1);
                if (!ret) {
                    if (this.give == 0) {
                        say("装備インベントリに空きがあるか確認してください。");
                    } else {
                        say("現在#b#t" + hat + "##kを装備しているなら、すぐに外してください。もし#b#t" + hat + "##kをなくしてしまった場合は、申し訳ありませんが新しいものは受け取れません。");
                    }
                    return false;
                }
                setQuestData(this.quest_hat, String.valueOf(this.give + 1));
                if (this.give == 0) {
                    say("#b#t" + hat + "##kをどうぞ。なくしてしまったら二度と受け取れないので、大切にしてくださいね。");
                } else {
                    say("あなたの#b#t" + hat + "##kを強化しました。#b#t" + hat + "##kをなくしてしまったら二度と受け取れないので、大切にしてくださいね。");
                }
                return false;
            }
            case NODE_RESET_ASK -> {
                if (!ok) {
                    // 翻訳
                    say("よく考えてから決めてください。準備ができたら教えてくださいね。");
                    return false;
                }
                // 翻訳
                askYesNo("本当に海賊王との戦績を#b0勝#kにリセットしますか？一度リセットすると、#b0勝#kから最初からやり直しになります。");
                this.node = NODE_RESET;
                return true;
            }
            case NODE_RESET -> {
                // 翻訳
                if (!ok) {
                    say("よく考えてから決めてください。準備ができたら教えてくださいね。");
                    return false;
                }
                for (int item_id : this.items_hat) {
                    if (0 < getItemCount(item_id) || isWear(item_id)) {
                        say("あなたはすでに#b#t" + this.items_hat[0] + "##kを持っています。#b#t" + this.items_hat[0] + "##kを持っている間は、海賊王との戦績をリセットできません。");
                        return false;
                    }
                }
                setQuestData(this.quest_clear_count, "0");
                setQuestData(this.quest_hat, "0");
                say("あなたは海賊王を#b0回#k倒しました。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
