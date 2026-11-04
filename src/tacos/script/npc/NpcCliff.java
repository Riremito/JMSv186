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
// Npc.wz script : desc_tree (JMS131-194), 会話文は lua_script_jp (desc_tree) を利用
public class NpcCliff extends NpcScriptMan {

    private final int npc_rudi = 2002001; // ルディ

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        String[] says = {
            "あそこに雪だるまたちが並んでいるのが見える？その一人に話しかければ、有名なクリスマスツリーのところへ連れて行ってくれるわ（すごく大きいのよ！そこでは、いろいろな飾りを使ってツリーを飾りつけることができるの。どう？楽しそうでしょう？",
            "各マップには一度に6人までしか入れないし、そこでは#b取引や商店の開設#kはできないの。それと、途中で落とした飾りを拾えるのはあなただけだから、ここでなくす心配はないわ。",
            "もちろん、マップに落としたアイテムが消えることはないわ。準備ができたら、中にいる雪だるまから出られるの。外に出ると、マップの中に落としたアイテムはもう取り戻せないから、そういうアイテムを拾うことは気にしなくていいわ。いいでしょう？",
            "さあ、#p" + this.npc_rudi + "#のところへ行って、クリスマスの飾りをもっと買って、ツリーを飾りつけてね～ 最高！一番大きくてきれいな飾りは彼のところでは売っていないの。たぶん…どこかのモンスターに持っていかれちゃったのよ！ダダダン～"
        };
        if (status < 0 || says.length <= status) {
            return false;
        }
        say(says[status], 0 < status, status < says.length - 1);
        return status < says.length - 1;
    }
}
