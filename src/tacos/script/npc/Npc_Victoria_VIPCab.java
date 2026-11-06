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
public class Npc_Victoria_VIPCab extends NpcScriptMan {

    private final int map_ant_tunnel_park = 105070001; // 中央ダンジョン : アリの巣-広場
    // 料金 (lua の mTaxi), 初心者は 1/10
    private final int cost = 10000;

    @Override
    public boolean action(int status, int answer) {
        boolean beginner = getJob() == 0;
        int cost_now = beginner ? this.cost / 10 : this.cost;
        // いいえを選択した
        if (getLastAction() == 0 && status == 0) {
            // 翻訳
            say("この町にも楽しめるものはたくさんありますよ。アリの巣公園へ行く必要ができたら、私たちに声をかけてください。");
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("こんにちは！このタクシーはVIPのお客様専用です。普通のタクシーのようにただ町へお連れするのではなく、VIPクラスにふさわしい、はるかに上質なサービスをご提供します。少しお高いですが…たった10,000メルで、#bアリの巣#kまで安全にお連れします。", false, true);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (beginner) {
                    // 翻訳
                    nt.add("初心者の方には90%の特別割引があります。アリの巣は、ビクトリアアイランドの中心、#p1061001#がいるダンジョンのずっと奥にあります。#b1,000メル#kで行きますか？");
                } else {
                    nt.add("初心者ではない方には決められた料金が請求されます。アリの巣広場はビクトリアアイランドの中央にあるダンジョンの奥の、24時間屋台が棲んでいるところです。#b10,000メル#kでアリの巣広場まで如何でしょうか？");
                }
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                if (!payMoney(cost_now)) {
                    // 翻訳
                    say("お金が足りないようですね。申し訳ありませんが、お金がないとご利用いただけません。");
                    return false;
                }
                warp(this.map_ant_tunnel_park); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
