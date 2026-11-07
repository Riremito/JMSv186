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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : Zakum01 (JMS131-302), 会話文は lua_script_jp (Zakum01) を利用
public class Npc_ElNath_Aura extends NpcScriptMan {

    private static final int NODE_SAYS = 0;
    private static final int NODE_MENU = 1;
    private static final int NODE_ASK = 2;
    private static final int NODE_OUT = 3;
    private static final int NODE_WARP = 4;
    private final String field_set_name = "Party2";
    private final int map_exit = 280090000; // 閉鉱 : 悲恋の部屋
    private final int npc_exit = 2030011; // アーリ
    private final int item_paper = 4001015; // 帰還書の紙文書
    private final int item_key = 4001016; // 閉鉱のカギ
    private final int item_fire_ore = 4001018; // 火の原石
    private final int item_fire_ore_piece = 4031061; // 火の原石かけら
    private final int item_return = 2030007; // 閉鉱帰還の書
    private final int quest_stage1 = 7000; // ジャクムの1段階目
    private final int paper_bonus = 30;
    private int node = NODE_SAYS;
    private int last_status = 0;
    private int paper_count = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        switch (this.node) {
            case NODE_SAYS -> {
                String result = field_set.getVar("zakum");
                // 翻訳
                if (result.equals("yes")) {
                    say("無事に試練を突破して、#b#t" + this.item_fire_ore + "##kを私に届けてくれましたね。では、私の隣のポータルに入ってください。パーティーメンバー全員が、精錬された#b#t" + this.item_fire_ore + "##k、#b#t" + this.item_fire_ore_piece + "##kを受け取れます。途中で#b#t" + this.item_paper + "#を30個#k以上集めたので、#b#t" + this.item_return + "##kを受け取る資格もあります。ポータルは今開いています。");
                    return false;
                }
                if (result.equals("no")) {
                    say("無事に試練を突破して、#b#t" + this.item_fire_ore + "##kを私に届けてくれましたね。では、私の隣のポータルに入ってください。パーティーメンバー全員が、精錬された#b#t" + this.item_fire_ore + "##k、#b#t" + this.item_fire_ore_piece + "##kを受け取れます。ポータルは今開いています。");
                    return false;
                }
                String[] says = {
                    "あなたが廃鉱を調査したいと言っていた人ですね。最終目標であるジャクムダンジョンのボスに会うには、必要なアイテムを集めなければなりません。そのアイテムを手に入れるには、まずその材料を手に入れる必要がありますよね？材料の1つ、#b#t" + this.item_fire_ore + "##kはここで手に入ります。簡単ではありませんが…",
                    "ここには、たくさんの洞窟へ通じる入口があります。洞窟に入ると、箱がいくつかあります。それをすべて壊して、#b#t" + this.item_key + "#を7個#k集めてください。箱は攻撃スキルでは壊せません。通常の基本攻撃だけが効きます。その後、7つの鍵を集めて、宝箱がある一番奥の部屋へ行ってください。そこで鍵を落とすと、#b#t" + this.item_fire_ore + "##kが手に入ります。鍵を落としてから手に入るまで少し時間がかかるので、辛抱強く待ってください。",
                    "もちろん、すべての箱に#t" + this.item_key + "#が入っているわけではありません。みなさん、とても予想外の事態に遭遇することになるので、気をつけてください。箱を調べている途中で、ときどき#t" + this.item_paper + "#が飛び出してきます。それも集めれば、きっといいことがありますよ。#t" + this.item_paper + "#は少なくとも30個集める必要があります。今お伝えできるのはこれだけです。"
                };
                if (status < 0) {
                    return false;
                }
                if (status < says.length) {
                    say(says[status], 0 < status, true);
                    return true;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("何か聞きたいことはありますか？\r\n#b#L0# #t" + this.item_fire_ore + "#を持ってきました#k。#l\r\n#b#L1# クエストはやめて、ここから出ます。#l", false);
                askMenu(nt);
                this.node = NODE_MENU;
                return true;
            }
            case NODE_MENU -> {
                // 翻訳
                if (answer == 1) {
                    askYesNo("ミッションの途中でやめると、最初からやり直さなければなりません…それだけでなく、パーティークエストなので、一人でも抜けるとレベルをクリアするのが難しくなるかもしれません。本当に出ますか？");
                    this.node = NODE_OUT;
                    return true;
                }
                if (!isPartyBoss()) {
                    say("洞窟の大きな宝箱に#b#t" + this.item_key + "#を7個#k落として#b#t" + this.item_fire_ore + "##kを手に入れたら、そのアイテムをパーティーのリーダーに渡してください。パーティーのリーダーが#b#t" + this.item_fire_ore + "##kを持って私に話しかければ、レベル1をクリアした合図になります。");
                    return false;
                }
                if (getItemCount(this.item_fire_ore) < 1) {
                    say("まだ#b#t" + this.item_fire_ore + "##kを手に入れていないようですね。制限時間内にここにあるいろいろな宝箱を調べて、#b#t" + this.item_key + "#を7個#k集め、それを洞窟の一番奥にある宝箱に全部落として#b#t" + this.item_fire_ore + "##kを手に入れてください。アイテムを手に入れたら、私に渡してください。");
                    return false;
                }
                this.paper_count = getItemCount(this.item_paper);
                if (this.paper_count == 0) {
                    askYesNo("#b#t" + this.item_fire_ore + "#を1個#k無事に持ち帰ってくれましたが、#b#t" + this.item_paper + "##kは持ち帰っていないようですね。パーティーが集めたのはこれで全部ですか？");
                } else {
                    askYesNo("#b#t" + this.item_fire_ore + "# 1個#kと#b" + this.paper_count + "個の#t" + this.item_paper + "##kを持ち帰ってくれましたね。パーティーメンバーが集めたアイテムはこれで全部ですか？");
                }
                this.node = NODE_ASK;
                return true;
            }
            case NODE_ASK -> {
                // 翻訳
                if (!ok) {
                    say("パーティーメンバーが洞窟で集めたアイテムはすべてパーティーのリーダーに渡し、リーダーがそれをすべて私に渡してください。もう一度確認してください。");
                    return false;
                }
                boolean ret = (this.paper_count == 0) ? exchange(0, this.item_fire_ore, -1) : exchange(0, this.item_fire_ore, -1, this.item_paper, -this.paper_count);
                if (!ret) {
                    if (this.paper_count == 0) {
                        say("#b#t" + this.item_fire_ore + "#を1個#k持っているか確認してください。");
                    } else {
                        say("本当に#b#t" + this.item_fire_ore + "# 1個#kと#b" + this.paper_count + "個の#t" + this.item_paper + "##kを持っているか確認してください。");
                    }
                    return false;
                }
                field_set.setVar("zakum", this.paper_bonus <= this.paper_count ? "yes" : "no");
                say("わかりました。下にできたポータルを使えば、アドビスがいるマップに戻れます。ポータルを使うときに、みなさんが渡してくれた#b#t" + this.item_fire_ore + "##kで作った#b#t" + this.item_fire_ore_piece + "##kを、パーティーメンバー全員に配ります。レベル1のクリア、おめでとうございます。それではまた…");
                return false;
            }
            case NODE_OUT -> {
                if (!ok) {
                    return false;
                }
                // 翻訳
                say("わかりました、出口マップへ送ります。そこに#b#p" + this.npc_exit + "##kが立っています。彼に話しかければ、外に出してくれます。さようなら…", false, true);
                setQuestData(this.quest_stage1, "fail");
                this.node = NODE_WARP;
                return true;
            }
            case NODE_WARP -> {
                warp(this.map_exit, "west00");
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
