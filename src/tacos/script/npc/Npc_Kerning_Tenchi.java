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
// Npc.wz script : Event04 (JMS131-302), 会話文は lua_script_jp (Event04) を利用
public class Npc_Kerning_Tenchi extends NpcScriptMan {

    private final int item_scroll = 4031019; // 悪魔の文書
    private final int item_talisman = 4000008; // 死者の符籍
    private final int item_magic_box = 4031017; // マジックボックス
    private final int talisman_count = 50;

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        if (getItemCount(this.item_scroll) < 1) {
            if (status == 0) {
                say("名もなき者よ…放っておいてくれ…");
            }
            return false;
        }
        boolean enough = this.talisman_count <= getItemCount(this.item_talisman);
        String[] says;
        if (enough) {
            says = new String[]{
                "お前のような名もなき者が、これほど希少で貴重なものを持っているとは悪くない。何？書を解読してほしいだと？断る。太古の秘められた力に満ちたこの書は、大魔法使いでさえ簡単には扱えんのだ。",
                "だが、待て…その書を見せてくれないか？無事に解読できれば、世界中にはびこる邪悪な力を滅ぼす使命に、大いに役立つかもしれん。",
                "無事に解読するには、#b#t" + this.item_talisman + "#が50個#k必要だろう。お守りと書を持ってきてくれれば、長年邪悪な力を倒して集めてきたアイテムの中でも、最高の逸品をやろう。",
                "よかろう、約束した貴重なアイテムをやろう。これは#r#t" + this.item_magic_box + "##kといって、太古の最悪のモンスターの1体を倒して手に入れたものだ。簡単に手に入るアイテムでは「ない」ぞ。",
                "箱の中には入手困難なアイテムが入っている。残念ながら鍵をなくしてしまって、開けてやることができん。#bカニングシティ#kには腕利きの#r鍵屋#kがいるから、そこで開けてもらえるかもしれんな.."
            };
        } else {
            says = new String[]{
                "お前のような名もなき者が、これほど希少で貴重なものを持っているとは悪くない。何？書を解読してほしいだと？断る。太古の秘められた力に満ちたこの書は、大魔法使いでさえ簡単には扱えんのだ。",
                "だが、待て…その書を見せてくれないか？無事に解読できれば、世界中にはびこる邪悪な力を滅ぼす使命に、大いに役立つかもしれん。",
                "無事に解読するには、#b#t" + this.item_talisman + "#が50個#k必要だろう。お守りと書を持ってきてくれれば、長年邪悪な力を倒して集めてきたアイテムの中でも、最高の逸品をやろう。"
            };
        }
        if (status < 0) {
            return false;
        }
        if (status < says.length - 1) {
            say(says[status], 0 < status, true);
            return true;
        }
        if (status == says.length - 1) {
            say(says[status], 0 < status, enough);
            return enough;
        }
        // TODO : lua の exchangeEx の Period:21600 (有効期限) は未実装
        if (!exchange(0, this.item_scroll, -1, this.item_talisman, -this.talisman_count, this.item_magic_box, 1)) {
            say("インベントリに空きがないようだな。空きを作ってから話しかけてくれ。");
        }
        return false;
    }
}
