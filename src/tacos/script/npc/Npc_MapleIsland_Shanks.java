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
public class Npc_MapleIsland_Shanks extends NpcScriptMan {

    private final int item_recommendation_letter = 4031801; // ルーカスの推薦書
    private final int map_lith_harbor = 104000000; // リス : 港口
    private final int map_southperry = 2000000; // メイプルロード : サウスペリ
    private final int cost = 150;
    private final int required_fame = 300;

    @Override
    public boolean action(int status, int answer) {
        // ビクトリアアイランドからメイプルアイランドへ
        if (getMapId() == this.map_lith_harbor) {
            switch (status) {
                case 0 -> {
                    // 原文ママ
                    if (getPlayer().getFame() < this.required_fame) {
                        say("人気度が" + this.required_fame + "以上ないとメイプルアイランドに行くことはできない。");
                        return false;
                    }
                    // 原文ママ
                    askYesNo("特別にサウスペリに移動させてあげよう。どうだい #bメイプルアイランドのサウスペリ#kに行きたいかい？");
                    return true;
                }
                case 1 -> {
                    warp(this.map_southperry); // TODO : portal name.
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // メイプルアイランドからビクトリアアイランドへ
        boolean has_letter = checkItem(this.item_recommendation_letter);
        switch (status) {
            case -1 -> {
                // 翻訳
                say("ふむ…まだここでやり残したことがあるんだな？");
                return false;
            }
            case 0 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("この船に乗れば、広大なフィールドが広がる#bビクトリアアイランド#kに行くことができる。#e" + this.cost + " メル#n必要だけどね。ただ、一度ビクトリアアイランドに渡ってしまうと、修行をつんで一人前にならなければ、このメイプルアイランドには戻ってこれなくなるんだ。ここに比べて危険の多い島でもあるから、レベルを5ぐらいにあげてから旅立つとよいだろうね。");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                if (has_letter) {
                    // 原文ママ
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("それはアムホストの長老ルーカス様の推薦書じゃないか!これがあるなら早く行ってくれ、金はいらんぞ。");
                    say(nt, false, true);
                    return true;
                }
                if (getPlayer().getMeso() < this.cost) {
                    // 翻訳
                    say("何だって？一文無しで旅に出たいってのか？変わった奴だな…");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("ここに飽きたのか？じゃあ…まずは#e" + this.cost + "メル#nをもらおうか…");
                say(nt, false, true);
                return true;
            }
            case 2 -> {
                if (has_letter) {
                    // 原文ママ
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("推薦書を持っているから、特別に料金は免除しよう。さあ！ビクトリアアイランドに出発するぞ！揺れるかもしれないから何かに捕まってくれ！");
                    say(nt, true, true);
                    return true;
                }
                payMoney(this.cost);
                warp(this.map_lith_harbor); // TODO : portal name.
                return false;
            }
            case 3 -> {
                // TODO : 乗船 (移動中のマップ)
                warp(this.map_lith_harbor); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
