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
public class Npc_Ariant_Karcasa extends NpcScriptMan {

    // ビクトリアアイランドの町 (ランダム)
    private final int[] map_towns = {
        100000000, // ヘネシス : ヘネシス
        101000000, // エリニア : エリニア
        102000000, // ぺリオン : ぺリオン
        103000000, // カニングシティー : カニングシティー
        104000000 // リス : 港口
    };
    private final int cost = 10000;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // TODO : 文章 (適当)
                say("おや…スピードや高いところが怖いのかい？俺の飛行の腕が信用できないって？大丈夫、問題点はすべて解決済みさ！");
                return false;
            }
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("どうやってここを知ったのかは知らないが、来る場所は間違ってないぜ！ニハル砂漠をさまよって故郷が恋しくなった人のために、ビクトリアアイランドへの直行便を出しているんだ！飛行船のことは心配いらない。落ちたのは1、2回だけさ！あの小さな船での長旅は窮屈じゃないかい？どうだい、この直行便に乗ってみないか？");
                askAccept(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("2つ覚えておいてくれ。1つ、この便は本来は海外輸送用だから、#rどの町に着くかは保証できない#k。2つ、特別便だから少し高くつく。料金は#e#b" + this.cost + "メル#n#kだ。ちょうど出発する便があるが、乗るかい？");
                askAccept(nt);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (適当)
                say("よし、出発するぞ～", false, true);
                return true;
            }
            case 3 -> {
                if (getMeso() < this.cost) {
                    // TODO : 文章 (適当)
                    say("おいおい、お金が足りないのかい？乗るには#b" + this.cost + "#kメル必要だと言っただろう。");
                    return false;
                }
                payMoney(this.cost);
                warp(this.map_towns[Randomizer.nextInt(this.map_towns.length)]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
