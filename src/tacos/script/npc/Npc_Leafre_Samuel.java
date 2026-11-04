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

import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class Npc_Leafre_Samuel extends NpcScriptMan {

    private final int quest_job4 = 6944; // 英雄の資質
    private final int item_skill_book = 2280003; // [スキルブック]メイプルヒーロー
    private final int required_level = 120;
    private final int[] job_from = {
        511, // バッカニア
        521 // ヴァイキング
    };
    private final int[] job_to = {
        512, // バイパー
        522 // キャプテン
    };
    // TODO : 文章 (適当)
    private final String[] job_names = {
        "バイパー",
        "キャプテン"
    };
    // TODO : 文章 (適当)
    private final String[][] job_texts = {
        {
            "君は最高の海賊、#bバイパー#kになった。",
            "バイパーの力はこれだけではない。バイパーは素早い戦いを得意とし、一度に多くの敵を打ち倒すことができる。"
        },
        {
            "君は最高の海賊、#bキャプテン#kになった。",
            "キャプテンの力はこれだけではない。キャプテンは奇襲を得意とし、敵に気づかれる前に攻撃することができる。"
        }
    };
    private final int MENU_CHANGE = 0;
    private final int MENU_THINK = 1;
    private int job_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                for (int i = 0; i < this.job_from.length; i++) {
                    if (getPlayer().getJob() == this.job_from[i]) {
                        this.job_index = i;
                    }
                }
                if (this.job_index < 0) {
                    // TODO : 文章 (適当)
                    say("なぜ私に会いたいのだ？私に聞きたいことなど何もないだろう。");
                    return false;
                }
                if (getPlayer().getLevel() < this.required_level) {
                    // TODO : 文章 (適当)
                    say("海賊の極みの道を歩むには、まだ弱すぎる。もっと強くなったら戻ってきてくれ。");
                    return false;
                }
                if (getPlayer().getQuestStatus(this.quest_job4) != 2) {
                    // TODO : 文章 (適当)
                    say("まだ4次転職する準備ができていないようだ。準備ができたら話しかけてくれ。");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("君は真の海賊になる資格がある。 \r\n転職しますか？");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_CHANGE, this.job_names[this.job_index] + "に転職したい。");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_THINK, "少し考えさせて。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != MENU_CHANGE) {
                    // TODO : 文章 (適当)
                    say("ためらう必要はない…決心したらいつでも話しかけてくれ。準備ができていれば、4次転職させてやろう。");
                    return false;
                }
                if (getPlayer().getJob() != this.job_from[this.job_index]) {
                    return false;
                }
                if (getPlayer().getRemainingSp() > (getPlayer().getLevel() - this.required_level) * 3) {
                    // TODO : 文章 (適当)
                    say("ふむ…#bSP#kが多すぎる。SPがそんなに残っていては4次転職できない。");
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_skill_book, 1, "")) {
                    // TODO : 文章 (適当)
                    say("インベントリに空きスロットがないので進めない。インベントリを整理してからもう一度試してくれ。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_skill_book, (short) 1);
                getPlayer().changeJob(this.job_to[this.job_index]);
                getPlayer().gainAp((short) 5);
                // TODO : jsでは4次スキルをマスターレベル10で習得させている
                say(this.job_texts[this.job_index][0], false, true);
                return true;
            }
            case 2 -> {
                say(this.job_texts[this.job_index][1], false, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (適当)
                say("すべては君がどれだけ修行するかにかかっていることを忘れるな。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
