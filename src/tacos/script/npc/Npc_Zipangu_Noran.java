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
// Npc.wz script : Make_Stone (JMS164-302), lua_script_jp に Make_Stone がないため js の処理を利用
public class Npc_Zipangu_Noran extends NpcScriptMan {

    private final int item_sealed_warrior_stone = 4020010; // 封印された勇者の石
    private final int item_sealed_wiseman_stone = 4020011; // 封印された賢者の石
    private final int item_sealed_saint_stone = 4020012; // 封印された聖者の石
    private final int item_warrior_stone = 4032169; // 勇者の石
    private final int item_wiseman_stone = 4032170; // 賢者の石
    private final int item_saint_stone = 4032171; // 聖者の石
    private final int item_silver_coin = 4032181; // 銀貨
    private final int item_nano_plant = 4032168; // ナノプラント(Ω)
    private final int item_ilbi = 2070006; // 日の手裏剣
    private final int item_magic_throwing_knife = 2070019; // 魔の手裏剣
    private final int item_vital_bullet = 2330003; // バイタルブレット
    private final int item_armor_piercing_bullet = 2330007; // アーマーピアシングブレット
    private final int unseal_coin = 1000;
    private final int create_coin = 2500;
    private final int create_meso = 500000000;
    private final int MENU_UNSEAL = 0;
    private final int MENU_CREATE = 1;
    // 封印を解く石 (封印された石, 封印を解いた石)
    private final int[][] unseals = {
        {item_sealed_warrior_stone, item_warrior_stone},
        {item_sealed_wiseman_stone, item_wiseman_stone},
        // js では封印された賢者の石を消費しているが、封印された聖者の石の誤りと思われる
        {item_sealed_saint_stone, item_saint_stone}
    };
    // 作成するアイテム (作成するアイテム, 必要な石, 必要なアイテム)
    private final int[][] creates = {
        {item_magic_throwing_knife, item_saint_stone, item_ilbi},
        {item_armor_piercing_bullet, item_wiseman_stone, item_vital_bullet}
    };
    private int menu = 0;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("何かご用ですか？");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_UNSEAL, "封印された石の封印を解いてほしい。");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_CREATE, "アイテムを作ってほしい。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.menu = answer;
                if (this.menu == MENU_UNSEAL) {
                    // TODO : 文章 (適当)
                    say("私はノラン、技術者です。あなたのことはここでよく噂になっていますよ。機械のモンスターを倒してくれるなら、私も力を貸しましょう。ブレイズの技術があれば、もっと強力なアイテムを作ることができます。", false, true);
                    return true;
                }
                if (this.menu != MENU_CREATE) {
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("何を作りましょうか？");
                for (int i = 0; i < this.creates.length; i++) {
                    nt.addMenu(i, "#t" + this.creates[i][0] + "#");
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (this.menu == MENU_CREATE) {
                    if (answer < 0 || this.creates.length <= answer) {
                        return false;
                    }
                    int[] create = this.creates[answer];
                    if (!exchange(-this.create_meso, this.item_nano_plant, -1, create[1], -1, create[2], -1, this.item_silver_coin, -this.create_coin, create[0], 1)) {
                        // TODO : 文章 (適当)
                        say("おや？材料が足りないようですね。\r\n#t" + create[0] + "#を作るには、#b#t" + this.item_nano_plant + "##k、#b#t" + create[1] + "##k、#b#t" + create[2] + "##k1個、#b#t" + this.item_silver_coin + "#" + this.create_coin + "枚#k、#b" + this.create_meso + "メル#kが必要です。");
                    }
                    return false;
                }
                // TODO : 文章 (適当)
                say("ブレイズは宇宙を漂うエネルギーを集めることに成功したと言われています。それが本当なら、膨大なエネルギーが手に入るでしょう。そのエネルギーのごく一部は封印された石から取り出せますが、使うには封印を解かなければなりません。私のところへ持ってくれば、封印を解いてあげましょう。", false, true);
                return true;
            }
            case 3 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("封印された石を渡してください。");
                for (int i = 0; i < this.unseals.length; i++) {
                    // TODO : 文章 (適当)
                    nt.addMenu(i, "#t" + this.unseals[i][0] + "#と手数料の#t" + this.item_silver_coin + "#" + this.unseal_coin + "枚を渡す");
                }
                askMenu(nt);
                return true;
            }
            case 4 -> {
                if (answer < 0 || this.unseals.length <= answer) {
                    return false;
                }
                int[] unseal = this.unseals[answer];
                if (!exchange(0, unseal[0], -1, this.item_silver_coin, -this.unseal_coin, unseal[1], 1)) {
                    // TODO : 文章 (適当)
                    say("おや？材料が足りないようですね。\r\n#t" + unseal[1] + "#を作るには、#b#t" + unseal[0] + "##kと#b#t" + this.item_silver_coin + "#" + this.unseal_coin + "枚#kが必要です。");
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
