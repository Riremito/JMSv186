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
package tacos.script.quest;

/**
 *
 * @author Riremito
 */
// Quest.wz startscript : q21101s, endscript : q21101e (JMS186), 鉾の使い手たる英雄, 旧 js (scripts_JMS) を参考に実装, js の end は処理なし
public class Quest_Aran_HeroOfThePolearm extends QuestScriptMan {

    private final int job_aran_beginner = 2000; // アラン (見習い)
    private final int job_aran_1 = 2100; // アラン 1次
    private final int item_medal = 1142129; // 目覚めたアランの勲章
    private final int quest_medal = 29924; // 目覚めたアラン
    private final int skill_hidden = 20009000; // ピグの弱点
    private final int[] skills = {
        21001003, // ブースター(鉾)
        21000000, // コンボアビリティ
        21100002, // ファイナルチャージ
        21100004, // コンボスマッシュ
        21100005, // コンボドレイン
        21110002, // フルスイング
    };
    private final int map_movie = 914090100; // アランの動画 (マップ名なし)
    private final int map_rien = 140000000; // スノーアイランド : リエン村

    @Override
    public boolean questStart(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("#b（少し考える必要がありそうだ…）#k", false, true);
            return false;
        }
        // 動画を見る
        if (getLastAction() == 0 && status == 1) {
            setStandAloneMode(true);
            setDirectionMode(true);
            warp(this.map_movie);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("#b（本当に#p1201001#を振るっていた英雄なのだろうか？いや、間違いない。#p1201001#をしっかりつかんでみよう。きっと反応するはずだ。）#k");
                return true;
            }
            case 1 -> {
                if (getJob() != this.job_aran_beginner) {
                    return false;
                }
                changeJob(this.job_aran_1);
                forceCompleteQuest();
                questEndEffect();
                resetStats(35, 4, 4, 4);
                expandInventory(1, 4);
                expandInventory(2, 4);
                expandInventory(3, 4);
                expandInventory(4, 4);
                gainItem(this.item_medal, 1);
                forceCompleteQuest(this.quest_medal);
                teachSkill(this.skill_hidden, 0, -1);
                teachSkill(this.skill_hidden, 1, 0);
                for (int skill_id : this.skills) {
                    teachSkill(skill_id, 0);
                }
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b（頭の中に何かが浮かんでくるような気がする…）#k", false, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo(1, "映像をスキップしますか？スキップしてもゲームの進行には影響ありません。");
                return true;
            }
            case 3 -> {
                // 動画を見ない
                warp(this.map_rien);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
