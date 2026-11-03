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

import odin.server.maps.SavedLocationType;

/**
 *
 * @author Riremito
 */
public class NpcNara extends NpcScriptMan {

    private final int item_free_pass = 4031134; // フリーパスカード
    private final int map_florina_beach = 110000000; // フロリナ : ビーチ
    private final int cost = 2000;
    private final int MENU_PAY = 0;
    private final int MENU_FREE_PASS = 1;
    private final int MENU_WHAT_IS_FREE_PASS = 2;
    private int menu = -1;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    // 翻訳
                    say("ここで何か用事があるんだろう。旅や狩りで疲れているはずだ。少し休んで、気が変わったらまた話しかけてくれ。");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("港口から少し離れたところに#bビーチ#kという幻想的な海岸があると聞いたことはあるか？#b" + this.cost + "メル#kを払うか#bフリーパスカード#kを持っているなら、いつでも俺がそこに運んでやるぜ。");
                nt.addMenu(MENU_PAY, this.cost + "メル払う");
                nt.addMenu(MENU_FREE_PASS, "フリーパスカードを持っている。");
                nt.addMenu(MENU_WHAT_IS_FREE_PASS, "フリーパスカードとは？");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.menu = answer;
                switch (this.menu) {
                    case MENU_PAY -> {
                        this.asked = true;
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("#b" + this.cost + "メル#kを払ってビーチに行くんだな？でも、そこにもモンスターがいるらしいから油断するよ！では、早速出向準備をするが…今すぐビーチに行くのか？");
                        askYesNo(nt);
                        return true;
                    }
                    case MENU_FREE_PASS -> {
                        this.asked = true;
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("#b#t" + this.item_free_pass + "##kを持っているのかい？それならいつでも問題なく#m" + this.map_florina_beach + "#へ行けるよ。でも、向こうにはモンスターもいるかもしれないから気をつけてな。よし、今すぐ#m" + this.map_florina_beach + "#へ行くかい？");
                        askYesNo(nt);
                        return true;
                    }
                    case MENU_WHAT_IS_FREE_PASS -> {
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("#b#t" + this.item_free_pass + "##kのことが気になるんだね。ははは、無理もない。#t" + this.item_free_pass + "#は、持っているだけで#m" + this.map_florina_beach + "#へタダで行けるアイテムなんだ。俺たちでさえ買わなきゃならなかったほどのレアアイテムなんだけど、残念ながら数週間前、長い週末の間になくしちまってね。");
                        say(nt, false, true);
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 2 -> {
                switch (this.menu) {
                    case MENU_PAY -> {
                        if (!payMoney(this.cost)) {
                            // 翻訳
                            say("メルが足りないようだね。お金を貯める方法はたくさんあるだろう…防具を売ったり…モンスターを倒したり…クエストをこなしたり…わかるだろう？");
                            return false;
                        }
                        getPlayer().saveLocation(SavedLocationType.FLORINA);
                        warp(this.map_florina_beach, "st00");
                        return false;
                    }
                    case MENU_FREE_PASS -> {
                        if (!checkItem(this.item_free_pass)) {
                            // 翻訳
                            say("うーん、それで#b#t" + this.item_free_pass + "##kは一体どこにあるんだい？？本当に持ってるのか？もう一度確認してみな！");
                            return false;
                        }
                        getPlayer().saveLocation(SavedLocationType.FLORINA);
                        warp(this.map_florina_beach, "st00");
                        return false;
                    }
                    case MENU_WHAT_IS_FREE_PASS -> {
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("あれがないまま帰ってきて、本当に残念に思ってるんだ。誰かが拾って、安全な場所にしまってくれているといいんだけどな。まあ、そういう話さ。もしかしたら君が見つけて、役立ててくれるかもしれないね。何か質問があったら、何でも聞いてくれ。");
                        say(nt);
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
