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

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : davyJohn_play (JMS131-302), 会話文は lua_script_jp (davyJohn_play) を利用
public class Npc_HerbTown_KikyoKoraiPlay extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_MENU = 1;
    private static final int NODE_SAYS = 2;
    private static final int NODE_OUT = 3;
    private final int map_road = 925100000; // 海賊船への道
    private final int map_bow = 925100100; // 船首突破
    private final int map_deck1 = 925100200; // 甲板突破1
    private final int map_kikyo1 = 925100201; // 100年生キキョウの領域1
    private final int map_minion1 = 925100202; // デイビーゾーンの手下1
    private final int map_deck2 = 925100300; // 甲板突破2
    private final int map_kikyo2 = 925100301; // 100年生キキョウの領域2
    private final int map_minion2 = 925100302; // デイビーゾーンの手下2
    private final int map_pirate = 925100400; // 海賊退治!
    private final int map_captain = 925100500; // 海賊船長の威厳
    private final int map_exit = 925100700; // 海賊船出口
    private final int map_outside = 251010404; // 白草村 : 海賊船の向こう
    private final int npc_wu_yang = 2094001; // キキョウニジン
    private final int item_old_key = 4001117; // 古い鍵
    private final int item_rookie = 4001120; // 新入海賊の証
    private final int item_middle = 4001121; // 中級海賊の証
    private final int item_veteran = 4001122; // 高級海賊の証
    private final int mob_rookie = 9300114; // デイビーゾーンの荒いクロコファミリー
    private final int mob_middle = 9300115; // デイビーゾーンの荒いクルー
    private final int mob_veteran = 9300116; // デイビーゾーンの荒いマスタークルー
    private final int mob_kikyo = 9300112; // デイビーゾーンの100年生キキョウ
    private final int mob_kikyo_hermit = 9300113; // デイビーゾーンの100年生キキョウ仙人
    // パーティークエストで入手するアイテム (lua の davy_takeawayitem)
    private final int[] items_quest = {
        4001117, // 古い鍵
        4001119, // 古地図
        4001120, // 新入海賊の証
        4001121, // 中級海賊の証
        4001122, // 高級海賊の証
        4001123 // 透明アイテム
    };
    private final int seal_count = 20;
    private int node = NODE_START;
    private int last_status = 0;
    private String[] says = null;
    private int says_index = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        int map_id = getMapId();
        switch (this.node) {
            case NODE_START -> {
                if (map_id == this.map_exit) {
                    for (int item_id : this.items_quest) {
                        removeItemAll(item_id);
                    }
                    warp(this.map_outside);
                    return false;
                }
                if (map_id == this.map_captain) {
                    // 翻訳
                    askYesNo("クエストを中断してこのエリアから出ますか？");
                    this.node = NODE_OUT;
                    return true;
                }
                if (map_id == this.map_kikyo1 || map_id == this.map_kikyo2) {
                    boolean first = map_id == this.map_kikyo1;
                    TacosFieldSet field_set = getFieldSet(first ? "DavyJohn3" : "DavyJohn4");
                    if (field_set == null) {
                        return false;
                    }
                    String key_clear = first ? "d201_clear" : "d301_clear";
                    String key_treasure = first ? "clear3hd" : "clear4hd";
                    // 翻訳
                    if (!field_set.getVar(key_clear).equals("1")) {
                        field_set.setVar(key_clear, "1");
                        say("ここの桔梗たちは完全に海賊王に支配されています。とても不幸なことですが、どうかあなたの手で倒してください。全部倒したら、私に話しかけてください。");
                        return false;
                    }
                    if (0 < getMobCount(map_id, this.mob_kikyo) + getMobCount(map_id, this.mob_kikyo_hermit)) {
                        say("海賊王の桔梗たちを全部倒しましたか？");
                        return false;
                    }
                    if (field_set.getVar(key_treasure).equals("1")) {
                        say("宝物を見つけてしまった以上、海賊王はものすごく激怒するかもしれません…");
                        return false;
                    }
                    // TODO : lua の setReactorState の第4引数 (0) の用途は不明
                    field_set.setReactorState(1, first ? "treasure1" : "treasure2", 1);
                    field_set.setVar(key_treasure, "1");
                    say("海賊王の桔梗たちを無事に倒したようですね！あれ…あれは、彼らが必死に守ろうとしていた海賊王の宝物では？誰かが宝物を持ち去ったら、海賊王はさぞかし怒るでしょうね。それに、この宝箱を開けるには鍵が必要です…");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                if (map_id == this.map_bow) {
                    nt.add("どうしますか？\r\n#b#L0# クエストを続ける。#l\r\n#L1# 海賊船から降りる。#l", false);
                } else if (map_id == this.map_road || map_id == this.map_deck1 || map_id == this.map_minion1 || map_id == this.map_deck2 || map_id == this.map_minion2 || map_id == this.map_pirate) {
                    nt.add("どうしますか？\r\n#b#L0# グオンの話を聞く。#l\r\n#L1# 海賊船から降りる。#l", false);
                } else {
                    return false;
                }
                askMenu(nt);
                this.node = NODE_MENU;
                return true;
            }
            case NODE_MENU -> {
                if (answer != 0) {
                    // 翻訳
                    askYesNo("クエストを中断してこのエリアから出ますか？");
                    this.node = NODE_OUT;
                    return true;
                }
                // 翻訳
                if (map_id == this.map_road) {
                    this.says = new String[]{
                        "私はグオン、桔梗の王ウーヤンの家来です。薬草畑の桔梗を見ましたか？みんな凶暴なモンスターになって、手当たり次第に人を襲っているんです。でも、これは私たち桔梗のせいではないことをわかってください。すべては#b海賊王#kのせいなんです！",
                        "#b海賊王#kが桔梗の王#b#p" + this.npc_wu_yang + "##k様をさらって以来、私たちを容赦なく支配しているんです。#b#p" + this.npc_wu_yang + "##k様の命を危険にさらすわけにはいかないので、私たち桔梗は彼の命令に従うしかないのです。",
                        "どうか#b海賊王#kの魔の手から#b#p" + this.npc_wu_yang + "##k様を救い出してください。それが私たち桔梗が平和を取り戻す唯一の方法なんです。海賊王とウーヤン様を乗せた海賊船は、今にも出航しようとしています。東の果てにある船に乗り込んでください！"
                    };
                } else if (map_id == this.map_deck1 || map_id == this.map_deck2) {
                    this.says = new String[]{
                        "ここは海賊王が支配する海賊船の甲板です。海賊王は船の一番奥で休んでいます。#bウーヤン#k様もそこにいるはずです。海賊たちを倒して、#bウーヤン#k様を救い出してください！"
                    };
                } else if (map_id == this.map_minion1 || map_id == this.map_minion2) {
                    this.says = new String[]{
                        "ここは海賊王の手下だけがいる場所です。彼らには突然現れては消えるという特殊な能力があります。",
                        // lua では map_minion1 の文末のみ ".." になっている
                        map_id == this.map_minion1 ? "海賊王は最も信頼する手下に、何かとても大切なものを預けたようです.." : "海賊王は最も信頼する手下に、何かとても大切なものを預けたようです…"
                    };
                } else if (map_id == this.map_pirate) {
                    this.says = new String[]{
                        "海賊たちはみんな、あなたが船にいることに気づいています。戦闘担当でない者まで、全員があなたを襲う準備をしています。海賊が次から次へと押し寄せてくるので、海賊が出てくる扉を閉めることをおすすめします。",
                        "扉を閉めるのに必要な#b#t" + this.item_old_key + "##kは、おそらく今は海賊たちが持っています。彼らを倒して船の扉を閉めてください！"
                    };
                } else if (map_id == this.map_bow) {
                    TacosFieldSet field_set = getFieldSet("DavyJohn2");
                    if (field_set == null) {
                        return false;
                    }
                    String mob_gen = field_set.getVar("mobGen");
                    if (!isPartyBoss()) {
                        if (mob_gen.equals("end")) {
                            say("海賊王が作った封印を解くことに成功しました。すぐに次の場所へ移動してください。");
                            return false;
                        }
                        this.says = new String[]{
                            "気をつけて！ここにはいつ海賊が現れてもおかしくありません。だからといって、ここを素通りできるわけではありません。海賊王が次のステージへのポータルを3重に封印したからです！",
                            "封印を解くには、持ち主が海賊であることを示すアイテム#b海賊の紋章#kが必要です。紋章を封印の前に置けば、封印は自動的に解除されます。ここにいる海賊を倒して#b海賊の紋章#kを手に入れ、私に渡してください。封印を解く方法を見つけます。",
                            "パーティーリーダーを通して開始してください。"
                        };
                    } else {
                        switch (mob_gen) {
                            case "1", "3", "5" -> {
                                int item_id = mob_gen.equals("1") ? this.item_rookie : mob_gen.equals("3") ? this.item_middle : this.item_veteran;
                                int mob_id = mob_gen.equals("1") ? this.mob_rookie : mob_gen.equals("3") ? this.mob_middle : this.mob_veteran;
                                if (getItemCount(item_id) < this.seal_count) {
                                    if (mob_gen.equals("5")) {
                                        say("まだ#b#t" + item_id + "#を20個#k集めていないようですね。それとも、もう海賊船から降りますか？");
                                    } else {
                                        say("まだ#b#t" + item_id + "#を20個#k集めていないようですね。");
                                    }
                                    return false;
                                }
                                if (!exchange(0, item_id, -this.seal_count)) {
                                    if (mob_gen.equals("1")) {
                                        say("#b#t" + item_id + "#が20個#kあるか確認してみてください。");
                                    } else {
                                        say("まだ#b#t" + item_id + "#を20個#k集めていないようですね。");
                                    }
                                    return false;
                                }
                                setMobGen(map_id, false, mob_id);
                                removeAllMob(map_id);
                                if (mob_gen.equals("1")) {
                                    field_set.setVar("mobGen", "2");
                                    field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "グオンがポータルの第1の封印を解除しました。");
                                    say("#b#t" + item_id + "##kを全部集めたようですね。次の海賊たちと戦う準備ができたら教えてください。");
                                } else if (mob_gen.equals("3")) {
                                    field_set.setVar("mobGen", "4");
                                    field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "グオンがポータルの第2の封印を解除しました。");
                                    say("#b#t" + item_id + "##kを全部集めたようですね。次の海賊たちと戦う準備ができたら教えてください。");
                                } else {
                                    field_set.setVar("mobGen", "end");
                                    field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "グオンがポータルの最後の封印を解除しました。すぐに次の場所へ移動してください。");
                                    say("#b#t" + item_id + "##kを全部集めたようですね。海賊たちを見事に倒してくれました。さあ、船の一番右側にあるポータルへ移動してください。");
                                }
                                return false;
                            }
                            case "2" -> {
                                say("さて、次は#b#t" + this.item_middle + "#を20個#k集める方法を見つけてください。気をつけて、海賊たちが向かってきています！");
                                setMobGen(map_id, true, this.mob_middle);
                                field_set.setVar("mobGen", "3");
                                return false;
                            }
                            case "4" -> {
                                say("あと少しです。最後の封印を解くには#b#t" + this.item_veteran + "#が20個#k必要です。急いで！海賊たちが向かってきています！");
                                setMobGen(map_id, true, this.mob_veteran);
                                field_set.setVar("mobGen", "5");
                                return false;
                            }
                            case "end" -> {
                                say("ここでできることはすべてやり遂げました。すぐに次の場所へ移動してください。");
                                return false;
                            }
                            default -> {
                                this.says = new String[]{
                                    "気をつけて！このあたりにはいつ海賊が現れてもおかしくありません。海賊王が次のステージへのポータルを3重に封印したせいで、ここを素通りすることはできないんです！",
                                    "封印を解くには、持ち主が海賊であることを示すアイテム#b海賊の紋章#kが必要です。紋章を封印の前に置けば、封印は自動的に解除されます。現れる海賊を倒して、落とす紋章を集めてください。#b海賊の紋章#kが十分に集まったら私に渡してください。封印を解いてあげます。",
                                    "もうすぐ海賊たちがここに押し寄せてきます。彼らを倒して、#b#t" + this.item_rookie + "#を少なくとも20個#k手に入れてください。"
                                };
                                setMobGen(map_id, true, this.mob_rookie);
                                field_set.setVar("mobGen", "1");
                            }
                        }
                    }
                } else {
                    return false;
                }
                this.says_index = 0;
                this.node = NODE_SAYS;
                say(this.says[0], false, 1 < this.says.length);
                return 1 < this.says.length;
            }
            case NODE_SAYS -> {
                this.says_index += ok ? 1 : -1;
                if (this.says_index < 0 || this.says.length <= this.says_index) {
                    return false;
                }
                say(this.says[this.says_index], 0 < this.says_index, this.says_index < this.says.length - 1);
                return this.says_index < this.says.length - 1;
            }
            case NODE_OUT -> {
                if (!ok) {
                    // 翻訳
                    say("もう一度よく考えてから、話しかけてください。");
                    return false;
                }
                warp(this.map_exit);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
