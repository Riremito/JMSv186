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

import odin.server.quest.MapleQuest;

/**
 *
 * @author Riremito
 */
// Npc.wz script : dooat (JMS176-302), lua_script_jp の dooat は中身がないため js の処理を利用
public class Npc_Ariant_Duarte extends NpcScriptMan {

    private final int quest_pyramid_kills = 7760; // ピラミッドで倒したモンスターの数 (クエストのデータ)
    private final int quest_protector_medal = 29932; // ファラオの守護者の勲章
    private final int item_protector_medal = 1142142; // ファラオの守護者の勲章
    private final int item_gem_base = 4001321; // 宝石 (4001322 ~ 4001325) の基準
    private final int item_sapphire = 4001322; // イェティファラオのサファイア
    private final int item_treasure_box = 2022613; // ファラオの宝箱
    private final int item_pharaoh_belt = 1132012; // ファラオのベルト
    private final int item_pharaoh_belt_hell = 1132013; // 不滅のファラオのベルト
    private final int map_pyramid_hill = 926010000; // 隠れ道 : ピラミッドの丘
    private final int map_shadow_first = 926020001; // 隠れ道 : ピラミッドの陰
    private final int map_shadow_last = 926020004;
    private final int map_pyramid_first = 926010001; // 隠れ道 : ネトのピラミッド
    private final int map_pyramid_last = 926010004;
    private final int map_pyramid_room_first = 926010100;
    private final int map_pyramid_room_last = 926013504;
    private final int kills_required = 50000;
    private final int MENU_ENTER = 1;
    private final int MENU_TOMB = 2;
    private final int MENU_STORY = 3;
    private final int MENU_MEDAL = 4;
    // 難易度ごとのレベル制限 (最低, 最高)
    private final int[][] mode_levels = {
        {40, 60}, // イージー
        {45, 60}, // ノーマル
        {50, 60}, // ハード
        {61, 999} // ヘル
    };
    private int menu = 0;
    private boolean asked_exit = false;

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        switch (status) {
            case -1 -> {
                // 退場の確認でいいえを選択した
                return false;
            }
            case 0 -> {
                // ピラミッドの陰 : 宝石を受け取って戻る
                if (this.map_shadow_first <= map_id && map_id <= this.map_shadow_last) {
                    int item_id = this.item_gem_base + map_id % 10;
                    if (!exchange(0, item_id, 1)) {
                        // TODO : 文章 (適当)
                        say("ETCインベントリに空きを1つ作ってください。");
                        return false;
                    }
                    warp(map_id - 10000);
                    return false;
                }
                if (this.map_pyramid_first <= map_id && map_id <= this.map_pyramid_last) {
                    warp(this.map_pyramid_hill);
                    return false;
                }
                if (this.map_pyramid_room_first <= map_id && map_id <= this.map_pyramid_room_last) {
                    this.asked_exit = true;
                    // TODO : 文章 (適当)
                    askYesNo("ここから出ますか？");
                    return true;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("私はデュアートだ。#b#e");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_ENTER, "ピラミッドに入る。");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_TOMB, "イェティファラオの墓へ向かう。");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_STORY, "イェティファラオの宝石の話を聞く。");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_MEDAL, "<ファラオの守護者>の勲章を受け取る。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (this.asked_exit) {
                    warp(this.map_pyramid_hill);
                    return false;
                }
                this.menu = answer;
                switch (this.menu) {
                    case MENU_ENTER -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        // TODO : 文章 (適当)
                        nt.add("神の怒りも知らぬ愚か者よ、自らの運命を選ぶがいい！");
                        for (int i = 0; i < this.mode_levels.length; i++) {
                            nt.add("#L" + i + "# #v" + (3994115 + i) + "# #l", false);
                        }
                        askMenu(nt);
                        return true;
                    }
                    case MENU_TOMB -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        // TODO : 文章 (適当)
                        nt.add("どの宝石を持ってきた？");
                        for (int i = 0; i < 4; i++) {
                            nt.addMenu(i, "#i" + (this.item_sapphire + i) + "##t" + (this.item_sapphire + i) + "#");
                        }
                        askMenu(nt);
                        return true;
                    }
                    case MENU_STORY -> {
                        // TODO : 文章 (適当)
                        say("イェティファラオの墓では、ファラオの分身である#bジュニアイェティファラオ#kを倒して力を証明すれば、#e#b#t" + this.item_treasure_box + "##k#nを手に入れることができる。その箱の中にはとても特別な宝物が入っている。それが#b#t" + this.item_pharaoh_belt + "##kだ。\r\n#i" + this.item_pharaoh_belt + "# #t" + this.item_pharaoh_belt + "#\r\n\r\nそして、もしヘルモードを生き延びることができたなら、#b#t" + this.item_pharaoh_belt_hell + "##kを手に入れられるだろう。\r\n#i" + this.item_pharaoh_belt_hell + "# #t" + this.item_pharaoh_belt_hell + "#\r\nもっとも、ネトがそれを許すはずはないがな。");
                        return false;
                    }
                    case MENU_MEDAL -> {
                        String data = getPlayer().getQuest(MapleQuest.getInstance(this.quest_pyramid_kills)).getCustomData();
                        int kills = 0;
                        try {
                            kills = Integer.parseInt(data);
                        } catch (NumberFormatException e) {
                            kills = 0;
                        }
                        if (kills < this.kills_required) {
                            // TODO : 文章 (適当)
                            say("ピラミッドでモンスターを50,000匹以上倒してから、また来なさい。倒した数 : " + kills);
                            return false;
                        }
                        if (checkItem(this.item_protector_medal) || !exchange(0, this.item_protector_medal, 1)) {
                            // TODO : 文章 (適当)
                            say("インベントリに空きを作ってください。");
                            return false;
                        }
                        MapleQuest.getInstance(this.quest_protector_medal).forceStart(getPlayer(), getNpcId(), "");
                        MapleQuest.getInstance(this.quest_protector_medal).forceComplete(getPlayer(), getNpcId());
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 2 -> {
                if (this.menu == MENU_ENTER) {
                    if (answer < 0 || this.mode_levels.length <= answer) {
                        return false;
                    }
                    int level = getPlayer().getLevel();
                    if (level < this.mode_levels[answer][0]) {
                        // TODO : 文章 (適当)
                        say("レベル" + this.mode_levels[answer][0] + "以上でなければならない。");
                        return false;
                    }
                    if (this.mode_levels[answer][1] < level) {
                        // TODO : 文章 (適当)
                        say("レベル" + this.mode_levels[answer][1] + "以下でなければならない。");
                        return false;
                    }
                    // TODO : ピラミッドへの入場 (パーティーの確認, start_PyramidSubway)
                    return false;
                }
                if (this.menu == MENU_TOMB) {
                    if (answer < 0 || 4 <= answer) {
                        return false;
                    }
                    if (!checkItem(this.item_sapphire + answer)) {
                        // TODO : 文章 (適当)
                        say("その宝石を持っていないようだな。");
                        return false;
                    }
                    // TODO : イェティファラオの墓への入場 (bonus_PyramidSubway), 入場できた場合に宝石を消費する
                    return false;
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
