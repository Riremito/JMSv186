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
// Npc.wz script : Event03 (JMS131-302), 会話文は lua_script_jp (Event03) を利用
public class Npc_Event_EventPirate extends NpcScriptMan {

    private final int map_exchange = 109050000; // 隠れ道 : 商品交換所
    private final int npc_tenchi = 9000007; // 天地
    private final int item_map = 4031018; // 宝の地図
    private final int item_scroll = 4031019; // 悪魔の文書

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        if (status == 0) {
            if (getItemCount(this.item_map) < 1) {
                say("おい、おい！！！#t" + this.item_map + "#を見つけてくれ！どこかで地図をなくしちまって、それがないと出発できないんだ。");
                return false;
            }
            if (1 <= getItemCount(this.item_scroll)) {
                say("君はもう#r#t" + this.item_scroll + "##kを持ってるよ。この書には驚くほど強力な魔力が込められていて、肌身離さず持っておくべきなんだ。すぐにこの書を#r#p" + this.npc_tenchi + "##kのところへ持って行ってね。");
                return false;
            }
            say("うわあ、君はすごいね。俺たちと一緒に航海しないか？何？忙しいって？うーん…それは無理か。じゃあ代わりに、別の面白い場所へ連れて行ってやるよ。  地図を自由に使えるようにしておくからさ。", false, true);
            return true;
        }
        if (status != 1) {
            return false;
        }
        if (!exchange(0, this.item_map, -1)) {
            say("あれ…本当に#t" + this.item_map + "#を持ってる？もう一度確認してみて。");
            return false;
        }
        warp(this.map_exchange);
        return false;
    }
}
