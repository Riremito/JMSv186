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
public class Npc_Henesys_Nana extends NpcScriptMan {

    private final int map_wedding_town = 680000000; // ウェディングタウン : ウェディングタウン
    private final int MENU_WEDDING_TOWN = 0;
    private final int MENU_CHAIR = 1;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("こんにちは～。このすてきな日に、何かお手伝いできることはありますか？\r\n");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_WEDDING_TOWN, "ウェディングタウンに行きたいです。");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_CHAIR, "結婚しているので、愛の椅子がほしいです！");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (this.asked) {
                    // ウェディングタウンへの移動でいいえを選択した
                    // TODO : 文章 (適当)
                    say("この素晴らしいチャンスを本当に逃してしまうの？とても美しい場所なのに。まだ愛する人に出会えていないのかしら？きっとそうね。誰かに恋をしていたら、このすてきな知らせを無視できるはずがないもの。");
                    return false;
                }
                if (answer == MENU_CHAIR) {
                    // TODO : 結婚している場合の処理
                    // TODO : 文章 (適当)
                    say("本当にごめんなさいね。この愛の椅子は、結婚した方だけのための特別な贈り物なの。まずは結婚してみてはいかが？");
                    return false;
                }
                // TODO : 文章 (適当)
                say("まあ！なんてすてきな日でしょう！世界はとても美しいわ～！世界は愛に満ちていると思いませんか？ここからでも、ウェディングタウンに満ちる愛の気持ちが感じられるわ～！", false, true);
                return true;
            }
            case 2 -> {
                this.asked = true;
                // TODO : 文章 (適当)
                askYesNo("ウェディングタウンに行ったことはありますか？愛があふれる素晴らしい場所で、愛し合う二人が結婚することもできるの。なんてロマンチックなんでしょう！行きたいなら、案内してあげますよ。");
                return true;
            }
            case 3 -> {
                // TODO : 文章 (適当)
                say("正しい決断ですね！ウェディングタウンで愛の気持ちを存分に感じてきてください。帰りたくなったら、ここに戻ってこられるので心配いりませんよ。", false, true);
                return true;
            }
            case 4 -> {
                getPlayer().saveLocation(SavedLocationType.AMORIA);
                warp(this.map_wedding_town);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
