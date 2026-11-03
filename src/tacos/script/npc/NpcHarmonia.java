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
public class NpcHarmonia extends NpcScriptMan {

    private final int quest_job4 = 6904; // 英雄としての資質
    private final int item_skill_book = 2280003; // [スキルブック]メイプルヒーロー
    private final int required_level = 120;
    private final int[] job_from = {
        111, // ナイト
        121, // クルセイダー
        131 // ドラゴンナイト
    };
    private final int[] job_to = {
        112, // ヒーロー
        122, // パラディン
        132 // ダークナイト
    };
    // 翻訳
    private final String[] job_names = {
        "ヒーロー",
        "パラディン",
        "ダークナイト"
    };
    // 翻訳
    private final String[][] job_texts = {
        {
            "君は戦士の頂点、#bヒーロー#kになった。複数の敵を攻撃できる#bラッシュ#kのスキルと、#bスタンス#k、#bアキレス#kとともに不屈の意志を得られるだろう。",
            "ヒーローの力はこれだけではない。ヒーローは攻撃力と防御力に優れた、バランスの取れた戦士だ。修行を積めば、コンボアタックだけでなくさまざまな攻撃スキルも習得できる。"
        },
        {
            "君は戦士の頂点、#bパラディン#kになった。複数の敵を攻撃できる#bラッシュ#kのスキルと、#bスタンス#k、#bアキレス#kとともに不屈の意志を得られるだろう。",
            "パラディンの力はこれだけではない。パラディンは属性攻撃と防御に優れている。修行を積めば、新しい属性を使えるようになり、チャージブローの限界を超えられるかもしれない。"
        },
        {
            "君は戦士の頂点、#bダークナイト#kになった。複数の敵を攻撃できる#bラッシュ#kのスキルと、#bスタンス#k、#bアキレス#kとともに不屈の意志を得られるだろう。",
            "ダークナイトの力はこれだけではない。ダークナイトは闇の力を使える。信じられないほど強力な闇の力で攻撃し、闇の化身を召喚することもできる。"
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
                        say("君は最高の戦士、#b" + this.job_names[i] + "#kの地位に就いた。強い力にはより大きな責任が伴う。これから待ち受けるすべての試練を乗り越えられるよう願っている。");
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
                    say("戦士の極みの道を歩むには、まだ弱すぎる。もっと強くなったら戻ってきてくれ。");
                    return false;
                }
                if (getPlayer().getQuestStatus(this.quest_job4) != 2) {
                    // 翻訳
                    say("まだ4次転職する準備ができていないようだ。準備ができたら話しかけてくれ。");
                    return false;
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("君は真の戦士になる資格がある。 \r\n転職しますか？");
                nt.addMenu(MENU_CHANGE, this.job_names[this.job_index] + "に転職したい。");
                nt.addMenu(MENU_THINK, "少し考えさせて。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != MENU_CHANGE) {
                    // 翻訳
                    say("最高の戦士になることをためらう必要はない..決心したらいつでも話しかけてくれ。準備ができていれば、4次転職させてやろう。");
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
