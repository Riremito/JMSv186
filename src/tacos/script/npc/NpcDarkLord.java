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
public class NpcDarkLord extends NpcScriptMan {

    private final int job_1st_level = 10;
    private final int job_1st = 400; // 盗賊
    private final int job_2nd_level = 30;
    private final int[] job_2nd = {
        410, // アサシン
        420 // シーフ
    };
    private final int job_3rd_level = 70;
    private final int[] job_3rd = {
        411, // ハーミット
        421 // シーフマスター
    };
    private final int job_4th_level = 120;
    private static final int STEP_NONE = 0;
    private static final int STEP_1ST = 1;
    private static final int STEP_2ND = 2;
    private static final int STEP_3RD = 3;
    private static final int STEP_4TH = 4;
    // 転職中に条件が切り替わってしまうためNPC会話開始時に職業を判定する
    private int step = -1;

    @Override
    public boolean action(int status, int answer) {
        if (this.step == -1) {
            int job = getPlayer().getJob();
            int level = getPlayer().getLevel();
            this.step = STEP_NONE;
            if (this.job_1st_level <= level && job == 0) {
                this.step = STEP_1ST;
            } else if (this.job_2nd_level <= level && job == this.job_1st) {
                this.step = STEP_2ND;
            } else if (this.job_3rd_level <= level) {
                for (int job_id : this.job_2nd) {
                    if (job == job_id) {
                        this.step = STEP_3RD;
                        break;
                    }
                }
            }
            if (this.step == STEP_NONE && this.job_4th_level <= level) {
                for (int job_id : this.job_3rd) {
                    if (job == job_id) {
                        this.step = STEP_4TH;
                        break;
                    }
                }
            }
        }
        // 転職を断った
        if (status == -1) {
            say("まだ心の準備ができてないか？");
            return false;
        }
        switch (this.step) {
            case STEP_1ST -> {
                switch (status) {
                    case 0 -> {
                        askYesNo("盗賊のアジトへようこそ。こうでもしないと入口もなかなか分かり辛い所だ。ふふ、出る時に門をよく憶えておいてくれ。じゃ、盗賊になる準備はできたか？");
                        return true;
                    }
                    case 1 -> {
                        // TODO : インベントリの拡張 (装備, ETC)
                        getPlayer().resetStats(4, 25, 4, 4);
                        getPlayer().changeJob(this.job_1st);
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("これで君は盗賊になった。盗賊のスキルが使用できるようになったからスキルウィンドウを開いてみてくれ。レベルを上げるとより多くのスキルを覚えることができるんだ。");
                        say(nt, false, true);
                        return true;
                    }
                    case 2 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("スキルだけでは物足りないな？ステータスも盗賊らしくならないと本物の盗賊じゃないな。盗賊はLUKがメインステータスで、DEXが補助ステータスになる。ステータスの上げ方が分からなかったら#b自動配分#kを使用するといい。");
                        say(nt, true, true);
                        return true;
                    }
                    case 3 -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("そして、プレゼントがあるぞ。君の装備とETCアイテムの保管箱の個数を増やしてあげたんだ。インベントリが広いと旅が楽しくなるもの。ふふ…");
                        say(nt, true, true);
                        return true;
                    }
                    case 4 -> {
                        // TODO : 武器の支給
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("さて！俺が君に教えられるのはここまでだ。君に役立つような武器もいくつか渡したから旅をしながら自身を鍛えてみてくれ。");
                        say(nt, true, false);
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case STEP_2ND, STEP_3RD, STEP_4TH -> {
                // TODO : 2次転職 (job_2nd), 3次転職 (job_3rd), 4次転職, 元のスクリプトはテスト用の選択肢で職業IDを直接選択していた
                return false;
            }
            default -> {
            }
        }
        // BB前? : 冒険も強くなることも良いが楽しみながら暮したくないか…？　我々と一緒に世の中を楽しみながら生きて行くのはどうだ？　本当に面白いぞ？
        say("隠密な会話…？隠密な行動が特技の盗賊だが、それは敵と戦う程度で充分だ。");
        /*
        マイが言ってた人が君か？#h0#…ふむ。彼女に言われたどおりに、かなり資質があるような若造だな…おい、盗賊になりたいって？盗賊について分かってるか？
        盗賊というとちっぽけなこそ泥を思う人もいるが、実は違うんだ。メイプルワールドの盗賊は闇の中で鋭い短刀と手裏剣で戦う人たちだ。もしかしたら正々堂々としてない部分があるかもしれない。だが、それも盗賊の本質。否定する必要はないだろ。
        職業としての盗賊は素早くて強力なスキルで敵を攻撃するんだ。体力は弱いほうだが、その分動きが早いためモンスターを避けるのも難しくない。強力な運でクリティカルな攻撃もうまいしな。
        どうだ？盗賊の道を共に歩むか？君が盗賊の道に進むと決めたら転職官の特権で君をすぐに#bカニングシティー、盗賊のアジト#kに招待する…隠密な所だから光栄に思うんだな。#rだが、他の職業がもっと良かったら？それなら断ってくれ。盗賊じゃない他の道を推薦するから#k。
         */
        return false;
    }
}
