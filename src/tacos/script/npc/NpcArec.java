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
public class NpcArec extends NpcScriptMan {

    private final int[] job_from = {
        410, // アサシン
        420, // シーフ
        432 // ブレードスペシャリスト
    };
    private final int[] job_to = {
        411, // ハーミット
        421, // シーフマスター
        433 // ブレードロード
    };
    private final String[] job_names = {
        "Hermit",
        "Chief Bandit",
        "Blade Lord"
    };
    // SPの確認を行わない
    private final boolean[] skip_sp_check = {
        false,
        false,
        true
    };
    private final int required_level = 70;
    private int job_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 本来は力と知恵のテストが必要, 英語の文章
                for (int i = 0; i < this.job_from.length; i++) {
                    if (getPlayer().getJob() == this.job_from[i]) {
                        this.job_index = i;
                        break;
                    }
                }
                if (this.job_index < 0) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("ジャクムダンジョンを探索しようとしてるか？盗賊でもないくせに声をかけるな。君の職業の長老を訪ねてくれ。");
                    say(nt);
                    return false;
                }
                boolean requirement = this.required_level <= getPlayer().getLevel() && (this.skip_sp_check[this.job_index] || getPlayer().getRemainingSp() <= (getPlayer().getLevel() - this.required_level) * 3);
                if (!requirement) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("Please use all your SP from level 70 and under, and be level 70+...");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("You are indeed a strong one.");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                boolean requirement = this.required_level <= getPlayer().getLevel() && (this.skip_sp_check[this.job_index] || getPlayer().getRemainingSp() <= (getPlayer().getLevel() - this.required_level) * 3);
                if (!requirement) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("Come back when you are level 70 and used all your SP accordingly.");
                    say(nt);
                    return false;
                }
                getPlayer().changeJob(this.job_to[this.job_index]);
                getPlayer().gainAp((short) 5);
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("You are now a #b" + this.job_names[this.job_index] + "#k. May #rOdin#k be with you!");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
