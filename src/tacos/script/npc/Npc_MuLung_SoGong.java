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

import odin.server.maps.Event_DojoAgent;

/**
 *
 * @author Riremito
 */
public class Npc_MuLung_SoGong extends NpcScriptMan {

    private final int map_entrance = 925020001; // 武陵道場 : 武陵道場入口
    private final int map_exit = 925020002; // 武陵道場 : 武陵道場出口
    // 休憩所 (各15マップ)
    private final int[] resting_map_ids = {
        925020600, // 武陵道場 6階
        925021200, // 武陵道場 12階
        925021800, // 武陵道場 18階
        925022400, // 武陵道場 24階
        925023000, // 武陵道場 30階
        925023600 // 武陵道場 36階
    };
    private final int[] belt_ids = {
        1132000, // 武陵ベルト(白)
        1132001, // 武陵ベルト(黄)
        1132002, // 武陵ベルト(青)
        1132003, // 武陵ベルト(赤)
        1132004 // 武陵ベルト(黒)
    };
    private final int[] belt_points = {
        200,
        1800,
        4000,
        9200,
        17000
    };
    private static final int MENU_SOLO = 0;
    private static final int MENU_PARTY = 1;
    private static final int MENU_BELT = 2;
    private static final int MENU_RESET = 3;
    private static final int MENU_MEDAL = 4;
    private static final int MENU_ABOUT = 5;
    private static final int MENU_NEXT = 0;
    private static final int MENU_LEAVE = 1;
    private static final int MENU_RECORD = 2;
    private int selected = -1;

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        boolean resting = false;
        for (int resting_map_id : this.resting_map_ids) {
            if (resting_map_id <= map_id && map_id <= resting_map_id + 14) {
                resting = true;
                break;
            }
        }
        // 道場入口
        if (map_id == this.map_entrance) {
            switch (status) {
                case 0 -> {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("俺の師匠は武陵で一番強いんだ！　そんなお方にお前が挑むだぁ？　後で後悔すんなよ");
                    nt.addMenu(MENU_SOLO, "一人で挑戦してみる");
                    nt.addMenu(MENU_PARTY, "一緒に挑戦してみる");
                    nt.add("");
                    nt.addMenu(MENU_BELT, "ベルトが欲しい");
                    nt.addMenu(MENU_RESET, "修練点数をリセットしたい");
                    // JMS オリジナル?
                    nt.addMenu(MENU_MEDAL, "勲章が欲しい");
                    nt.addMenu(MENU_ABOUT, "武陵道場ってなに？");
                    askMenu(nt);
                    return true;
                }
                case 1 -> {
                    this.selected = answer;
                    // TODO : 英語の文章
                    switch (this.selected) {
                        case MENU_SOLO -> {
                            if (getPlayer().getParty() != null) {
                                say("Please leave your party.");
                                return false;
                            }
                            // TODO : 記録した階 (クエスト 150000 のカスタムデータ) からの再開
                            // The last time you took the challenge yourself, you were able to reach Floor #18. I can take you straight to that floor, if you want. Are you interested?
                            Event_DojoAgent.warpStartDojo(getPlayer(), false);
                            return false;
                        }
                        case MENU_PARTY -> {
                            if (getPlayer().getParty() == null) {
                                // TODO : グループに入っていない場合の文章
                                return false;
                            }
                            // TODO : グループリーダーの確認
                            // Hey, you're not even a leader of your party. What are you doing trying to sneak in? Tell your party leader to talk to me if you want to enter the premise...
                            NpcScriptTag nt = new NpcScriptTag();
                            nt.add("Would you like to Enter now?");
                            say(nt, false, true);
                            return true;
                        }
                        case MENU_BELT -> {
                            NpcScriptTag nt = new NpcScriptTag();
                            nt.add("お前の修練点数は #b" + getPlayer().getDojo() + "#k 点だな。 師匠は才能のあるヤツが好きなんだ。修練点数が一定レベル以上溜まれば、点数によってベルトがもらえるぞ。");
                            nt.add("");
                            for (int i = 0; i < this.belt_ids.length; i++) {
                                nt.add("#L" + i + "##i" + this.belt_ids[i] + ":# #t" + this.belt_ids[i] + "##l");
                            }
                            askMenu(nt);
                            return true;
                        }
                        case MENU_RESET -> {
                            askYesNo("You know if you reset your training points, then it'll return to 0, right? I can honestly say that it's not necessarily a bad thing. Once you reset your training points and start over again, then you'll be able to receive the belts once more. Do you want to reset your training points?");
                            return true;
                        }
                        case MENU_MEDAL -> {
                            // TODO : 勲章が欲しい
                            return false;
                        }
                        case MENU_ABOUT -> {
                            say("My master is the most powerful individual in Mu Lung, and he is responsible for erecting this amazing Mu Lung Training Tower. Mu Lung Training Tower is a colossal training facility that consists of 38 floors. Each floor represents additional levels of difficulty. Of course, with your skills, reaching the top floor will be impossible...");
                            return false;
                        }
                        default -> {
                        }
                    }
                    return false;
                }
                case 2 -> {
                    switch (this.selected) {
                        case MENU_PARTY -> {
                            Event_DojoAgent.warpStartDojo(getPlayer(), true);
                        }
                        case MENU_BELT -> {
                            // TODO : ベルトの受け取り (getDojoRecord() 番目のベルトかつ修練点数 belt_points 以上, レベル制限), 元のスクリプトのベルト取得処理がおかしい
                            if (answer < 0 || this.belt_ids.length <= answer) {
                                return false;
                            }
                            NpcScriptTag nt = new NpcScriptTag();
                            nt.add("#i" + this.belt_ids[answer] + ":# #t" + this.belt_ids[answer] + "#をもらうためには #b35レベル以上#kじゃなきゃいけなくて、累積修練点数 #b" + this.belt_points[answer] + "点#kが必要だ。");
                            nt.add("");
                            nt.add("お前がこのベルトをもらうには、修練点数を #r" + (this.belt_points[answer] - getPlayer().getDojo()) + "#k点溜めねーとダメだな。");
                            say(nt);
                        }
                        case MENU_RESET -> {
                            getPlayer().setDojoRecord(true);
                            say("I have resetted your training points to 0.");
                        }
                        default -> {
                        }
                    }
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // 休憩所
        if (resting) {
            switch (status) {
                case 0 -> {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("ここまで無事に来たなんてな…。でも、ここから先は簡単じゃねーぞ。どうだ？　続けて挑戦すんのか？");
                    nt.addMenu(MENU_NEXT, "引き続き挑戦する");
                    nt.addMenu(MENU_LEAVE, "外へ出る");
                    nt.addMenu(MENU_RECORD, "ここまでの成績を記録する");
                    askMenu(nt);
                    return true;
                }
                case 1 -> {
                    this.selected = answer;
                    switch (this.selected) {
                        case MENU_NEXT -> {
                            Event_DojoAgent.warpNextMap(getPlayer(), true);
                        }
                        case MENU_LEAVE -> {
                            // TODO : 英語の文章
                            askYesNo("Do you want to quit? You really want to leave here?");
                            return true;
                        }
                        case MENU_RECORD -> {
                            // TODO : 成績の記録 (クエスト 150000 のカスタムデータ)
                            // 記録 : I have just recorded your progress. The next time you get here, I'll sent you directly to this level.
                            // グループの場合 : Hey.. you can't record your progress with a team...
                        }
                        default -> {
                        }
                    }
                    return false;
                }
                case 2 -> {
                    // TODO : グループリーダーの場合はグループ全体を移動
                    warp(this.map_exit); // TODO : portal name.
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // 道場の各階
        switch (status) {
            case 0 -> {
                // TODO : 英語の文章
                askYesNo("What? You're ready to quit already? You just need to move on to the next level. Are you sure you want to quit?");
                return true;
            }
            case 1 -> {
                // TODO : グループリーダーの場合はグループ全体を移動
                warp(this.map_exit); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
