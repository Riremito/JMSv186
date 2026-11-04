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
public class Npc_Ludibrium_RobertHolly extends NpcScriptMan {

    private final int cost = 50000;
    private final int add_capacity = 5;
    private final int max_capacity = 100;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("そうか… 君はワシが思ったほど友達がいないようじゃの。それとも#b" + this.cost + "メル#kを持っていないのか？とにかく気が向いたら又来てくれよ。メルがあればな… ヒョホホ…");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("今日もお客さんが沢山くるといいのう… おお！ちょっと待ちなされ。君の顔からして、友達をかなり多く持っているんじゃないかね？どうだ？…ワシに若干のメルさえ払えば、友達のリストを増やしてやろう。しかし、同じエントリーIDの他のキャラクターには反映されないから覚えておくんじゃぞ。さあ、やってみんか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                this.asked = true;
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よしよし、よく決断したのぅ。値段はあまり高くない。今は激安セール期間だから#b友達リスト" + this.add_capacity + "名の追加に5万メル#kが相場じゃな。もちろん、バラでは売らないぞ。一度増やしたら永遠にリストの上限は増えたままだ。どうだ？#b" + this.cost + "メル#kを払うのか？");
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                int capacity = getPlayer().getBuddyCapacity();
                if (this.max_capacity < capacity + this.add_capacity || getPlayer().getMeso() < this.cost) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("そうか… 君はワシが思ったほど友達がいないようじゃの。それとも#b" + this.cost + "メル#kを持っていないのか？とにかく気が向いたら又来てくれよ。メルがあればな… ヒョホホ…");
                    say(nt);
                    return false;
                }
                // TODO : 友達リストの上限変更をクライアントに通知する
                payMoney(this.cost);
                getPlayer().setBuddyCapacity((byte) (capacity + this.add_capacity));
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よし！完了じゃ。君の友達のリストが5個増やしたぞ。確認してみてくれ。もし、それでも友達のリストが足りないのならまたワシの所へ来てくれ。200個までなら増やしてあげるから。もちろんお金は頂くがのぅ。ではな、ヒョホホ～");
                say(nt);
                return false;
            }
            case -1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("そうか…君はワシが思ったほど友達がいないようじゃの。ヒョホホホ、冗談に決まっておるじゃろう。とにかく気が変わったら又来てくれよ。友達を増やしたいならな…ヒョホホ…");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
