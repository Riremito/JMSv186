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
public class Npc_Leafre_Hellin extends NpcScriptMan {

    private final int quest_job4 = 6934; // 英雄としての資質
    private final int item_skill_book = 2280003; // [スキルブック]メイプルヒーロー
    private final int required_level = 120;
    private final int[] job_from = {
        411, // ハーミット
        421 // マスターシーフ
    };
    private final int[] job_to = {
        412, // ナイトロード
        422 // シャドー
    };
    // 翻訳
    private final String[] job_names = {
        "ナイトロード",
        "シャドー"
    };
    // 翻訳
    private final String[][] job_texts = {
        {
            "お前は最高の盗賊、#bナイトロード#kになった。ナイトロードは、敵の攻撃をかわす#bフェイク#kや、隠れた仲間を呼び出す#bニンジャアンブッシュ#kが得意だ。敵の死角を突いて攻撃する。",
            "ナイトロードの力はこれだけではない。ナイトロードは素早い戦いが得意だ。一度にたくさんの手裏剣を投げ、大勢の敵をまとめて撃退できるかもしれない。"
        },
        {
            "お前は最高の盗賊、#bシャドー#kになった。シャドーは、敵の攻撃をかわす#bフェイク#kや、隠れた仲間を呼び出す#bニンジャアンブッシュ#kが得意だ。敵の死角を突いて攻撃する。",
            "シャドーの力はこれだけではない。シャドーは奇襲が得意だ。敵に気づかれる前に攻撃し、闇に閉じ込めて倒すことさえできる。"
        }
    };
    private final int MENU_CHANGE = 0;
    private final int MENU_THINK = 1;
    private int job_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                for (int i = 0; i < this.job_to.length; i++) {
                    if (getPlayer().getJob() == this.job_to[i]) {
                        // 翻訳
                        say("お前は最高の盗賊、#b" + this.job_names[i] + "#kの地位に就いた。強い力にはより大きな責任が伴う。これから待ち受けるすべての試練を乗り越えられるよう願っている。");
                        return false;
                    }
                }
                for (int i = 0; i < this.job_from.length; i++) {
                    if (getPlayer().getJob() == this.job_from[i]) {
                        this.job_index = i;
                    }
                }
                if (this.job_index < 0) {
                    // 翻訳
                    say("なぜ私に会いたいのだ？私に聞きたいことなど何もないだろう。");
                    return false;
                }
                if (getPlayer().getLevel() < this.required_level) {
                    // 翻訳
                    say("盗賊の極みの道を歩むには、まだ弱すぎる。もっと強くなったら戻ってこい。");
                    return false;
                }
                if (getPlayer().getQuestStatus(this.quest_job4) != 2) {
                    // 翻訳
                    say("まだ4次転職する準備ができていないようだ。準備ができたら話しかけてくれ。");
                    return false;
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("お前は真の盗賊になる資格がある。 \r\n転職するか？");
                nt.addMenu(MENU_CHANGE, this.job_names[this.job_index] + "に転職したい。");
                nt.addMenu(MENU_THINK, "少し考えさせて。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != MENU_CHANGE) {
                    // 翻訳
                    say("ためらう必要はない…決めたらいつでも話しかけてこい。準備ができていれば、4次転職させてやろう。");
                    return false;
                }
                if (getPlayer().getJob() != this.job_from[this.job_index]) {
                    return false;
                }
                if (getPlayer().getRemainingSp() > (getPlayer().getLevel() - this.required_level) * 3) {
                    // 翻訳
                    say("ふむ…#bSP#kが多すぎる。SPがそんなに残っていては4次転職できない。");
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_skill_book, 1, "")) {
                    // 翻訳
                    say("インベントリに空きスロットがないので進めない。インベントリを整理してからもう一度試してくれ。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_skill_book, (short) 1);
                getPlayer().changeJob(this.job_to[this.job_index]);
                getPlayer().gainAp((short) 5);
                say(this.job_texts[this.job_index][0], false, true);
                return true;
            }
            case 2 -> {
                say(this.job_texts[this.job_index][1], false, true);
                return true;
            }
            case 3 -> {
                // 翻訳
                say("すべては君がどれだけ修行するかにかかっていることを忘れるな。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
