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

/**
 *
 * @author Riremito
 */
// Npc.wz script : wxmasA (JMS146-165), 会話文は lua_script_jp (wxmasA) を利用
// 期間とチャンネルの確認は削除
public class Npc_HappyVille_Rudolph extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_WHAT = 1;
    private static final int NODE_WHERE = 2;
    private static final int NODE_HELP = 3;
    private static final int NODE_GO = 4;
    private static final int NODE_READY = 5;
    private static final int NODE_GO_ASK = 6;
    private static final int NODE_RETURN = 7;
    private final int map_happy_village = 209000000; // 隠れ道 : 幸福の村
    private final int map_cold_zone = 209080000; // 隠れ道 : 氷雪地帯
    private final int item_mitten = 1472063; // 魔法ミトン
    private final int quest_snow = 5008; // 永遠の雪
    private int node = NODE_START;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        // 翻訳
        switch (this.node) {
            case NODE_START -> {
                if (getMapId() != this.map_happy_village) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("持っていた凍った雪を全部エルフたちに渡した？ああ、ハッピービレッジに戻りたいの？ \r\n#b#L0#はい、そこまで連れて行ってください。  \r\n#L1#いいえ、まだここでやることがあります。 \r\n#l#k", false);
                    askMenu(nt);
                    this.node = NODE_RETURN;
                    return true;
                }
                String val = getQuestData(this.quest_snow);
                if (val.isEmpty()) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("おい、そこの君！僕はルドルフ。ここで何をしているのかって？僕の仕事は、ハッピービレッジにいつも雪を降らせることさ！でも今、大ピンチなんだ！ハッピービレッジが最近改装したせいで、永遠の雪の在庫がなくなってしまったんだよ！  \r\n\r\n#b#L0#永遠の雪って何？#l#k\r\n#b#L1#いや…別に…興味ないな。#l#k", false);
                    askMenu(nt);
                    this.node = NODE_WHAT;
                    return true;
                }
                if (val.equals("ing")) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("雪吹き機は極寒ゾーンにあるんだ。とても寒くて風も強いから、一人で行くのは全然簡単じゃないけど、僕が連れて行けば楽勝さ！永遠の雪を見つけたら、雪吹き機に入れてほしいんだ。今すぐ極寒ゾーンへ出発する？\r\n#b#L0#よし、連れて行って！\r\n#L1#ブルルッ！遠慮しておくよ…\r\n#l#k", false);
                    askMenu(nt);
                    this.node = NODE_GO;
                    return true;
                }
                say("ごめんね、今すぐ極寒ゾーンへ連れて行ってあげられるよ。");
                return false;
            }
            case NODE_WHAT -> {
                if (answer != 0) {
                    say("ああ、そうなの？残念…");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("永遠の雪は、ハッピービレッジを一年中真っ白な雪で覆っておくためのものなんだ！永遠の雪は、必要なときにいつでもメイプルクリスマスに雪を補充するのに使われていたんだけど、ハッピービレッジが改装されたあと、永遠の雪をしまっていたコンテナが盗まれてしまったんだ！さらに悪いことに、作業員たちがただの雪だと思って、残りを捨ててしまったんだよ！！！\r\n\r\n#b#L0#どこで永遠の雪をもっと見つけられるの？#l#k", false);
                askMenu(nt);
                this.node = NODE_WHERE;
                return true;
            }
            case NODE_WHERE -> {
                askYesNo("実は、世界中に散らばっているんだ。あちこちで少し見つけるのは難しくないけど、僕一人では雪吹き機を満たすだけの永遠の雪を見つけられないんだ！すてきなホワイトクリスマスを迎えられるように、この特別な雪を探すのを手伝ってくれないかな？");
                this.node = NODE_HELP;
                return true;
            }
            case NODE_HELP -> {
                if (!ok) {
                    say("ああ、そうなの？残念！");
                    return false;
                }
                say("雪吹き機は極寒ゾーンにあるんだ。もうそこにある永遠の雪のせいで、すごく寒い場所なんだよ！#b魔法の手袋#kをはめていないと、手が凍りついてしまうくらい寒いんだ！手袋はそこに見えるプレゼントの箱の中に入っているから、1つ開けて、今すぐ自分の分を取ってね！");
                setQuestData(this.quest_snow, "ing");
                return false;
            }
            case NODE_GO -> {
                if (answer != 0) {
                    say("ああ、寒すぎて耐えられないんだね？残念だなあ…");
                    return false;
                }
                boolean wear = isWear(this.item_mitten);
                boolean has = 0 < getItemCount(this.item_mitten);
                if (!wear && !has) {
                    say("何だって？！！何の防寒もせずにあそこへ行くつもり？だめだめ…そんなことはさせられないよ！ここから先はとても寒いから、ちゃんと装備していないと凍え死んでしまうかもしれないんだ。安全にたどり着くには、#b魔法の手袋#kを必ずはめていないとだめだよ…ああ、その手袋はどこで手に入るのかって？いい質問だね！あそこにプレゼントの箱が積んであるのが見える？1つ開ければ、中に自分の分が入っているよ。それを装備すれば、ひどい寒さからも身を守れる。ただ、無料だからって手袋をたくさんため込まないでね。手袋をはめて出発の準備ができたら教えて。");
                    return false;
                }
                if (!wear) {
                    say("#bまほうの手袋#kを#r#e装備#k#nしていないと、極寒ゾーンに入ったとたん、間違いなく凍え死んでしまうよ。もう一度考え直して。");
                    return false;
                }
                if (has) {
                    say("何だって？本当に2つ以上持っていくつもり？1つあれば十分なんだよ！必要なほかの人のことも考えて、残りはその人たちのために置いていってね。");
                    return false;
                }
                say("見てごらん！準備は万全みたいだね！！！", false, true);
                this.node = NODE_READY;
                return true;
            }
            case NODE_READY -> {
                askYesNo("今すぐ極寒ゾーンへ出発する？");
                this.node = NODE_GO_ASK;
                return true;
            }
            case NODE_GO_ASK -> {
                if (!ok) {
                    say("ああ、そうなの？それなら…僕の助けが必要になったら、いつでも話しかけてね！");
                    return false;
                }
                warp(this.map_cold_zone, "st00");
                return false;
            }
            case NODE_RETURN -> {
                if (answer == 0) {
                    warp(this.map_happy_village, "st00");
                    return false;
                }
                // lua では if の対応の誤りで表示されない
                say("わかった。僕が必要になったら言ってね～");
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
