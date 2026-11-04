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
public class Npc_Orbis_Lisa extends NpcScriptMan {

    private final int quest_where_is_hella = 3006; // ヘラの行方は？
    private final int quest_hidden_grave = 3017; // 隠された小さい墓

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_where_is_hella) == 0) {
                    // 翻訳
                    say("#bヘラ#kを探しているの？一応ここに住んでいるんだけど、最近は会えないわよ。何か月か前に急に町を出て行って、それっきり戻ってこないの。彼女の家に行ってもあまり意味はないけど、少なくとも家政婦さんはいるはずよ。その人に話を聞いてみたら？");
                    return false;
                }
                if (getPlayer().getQuestStatus(this.quest_hidden_grave) == 0) {
                    // 翻訳
                    say("#bヘラ#kがどこへ行ったか…え？元気だってことだけはわかってるって？うーん…よそ者の言葉を信じていいのかわからないけど、本当ならよかったわ。もちろんジェイドにはもう伝えたわよね？誰よりも彼女のことを心配しているのは彼だもの。");
                    return false;
                }
                // 翻訳
                say("最近、モンスターたちがずっと凶暴で残酷になってきてるの。  もしこの近くまで来たらどうしよう？？そんなことにならないといいけど、ね？ね？");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
