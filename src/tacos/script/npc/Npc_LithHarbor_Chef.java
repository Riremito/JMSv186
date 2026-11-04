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

import odin.server.Randomizer;

/**
 *
 * @author Riremito
 */
public class Npc_LithHarbor_Chef extends NpcScriptMan {

    private final int item_secret_book = 4031048; // 秘密の呪文書
    // {アイテムID, 個数}
    private final int[][] prizes = {
        {2000004, 100}, // エリクサー
        {4011006, 10}, // 金
        {4011000, 10}, // 青銅
        {4011005, 10}, // オリハルコン
        {4021005, 10}, // サファイア
        {4021001, 10}, // 紫水晶
        {4021007, 10} // ダイヤモンド
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // 翻訳
                say("いいとも…いつでもどうぞ。言っておくけど、そのアイテムを持っていても、インベントリの場所を取るだけだよ。だから、賞品と交換することをおすすめするね。");
                return false;
            }
            case 0 -> {
                if (!checkItem(this.item_secret_book)) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("何の用で俺のところに来たかい？もしかして、俺に一目ぼれ？");
                    say(nt);
                    return false;
                }
                // 翻訳
                askYesNo("おっと、おっと！#b#t" + this.item_secret_book + "##kを持っているのかい？よし…それをくれたら、いいアイテムと交換してあげよう。ただし、もらえるアイテムは「ランダム」で選ばれることはわかっておいてくれ。運次第ってことさ。どうだい？交換するかい？");
                return true;
            }
            case 1 -> {
                int[] prize = this.prizes[Randomizer.nextInt(this.prizes.length)];
                if (!canHold(prize[0], prize[1])) {
                    // 翻訳
                    say("おい、おい！報酬を受け取るには、消費インベントリとETCインベントリに少なくとも1つ空きスロットが必要だよ。空きを作ってから話しかけてくれ。");
                    return false;
                }
                if (!checkItem(this.item_secret_book)) {
                    // 翻訳
                    say("#t" + this.item_secret_book + "#を持っているか、消費インベントリとETCインベントリに十分な空きがあるか確認してくれ。");
                    return false;
                }
                removeItem(this.item_secret_book, 1);
                gainItem(prize[0], prize[1]);
                // 翻訳
                say("#b" + prize[1] + " #t" + prize[0] + "##kが手に入ったかい？どうだい？気に入ったかい？ははは…またね～");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
