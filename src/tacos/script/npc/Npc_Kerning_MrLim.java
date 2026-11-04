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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;
import odin.server.maps.Event_PyramidSubway;

/**
 *
 * @author Riremito
 */
public class Npc_Kerning_MrLim extends NpcScriptMan {

    private final int map_subway = 910320000; // ヒドゥンストリート : 捨てられた地下鉄の歴史
    private final int map_dusty_platform = 910320001; // ヒドゥンストリート : ホコリだらけのプラットフォーム
    private final int map_last_platform = 910330001; // ヒドゥンストリート : 最後のプラットフォーム
    private final int map_stage_min = 910320100; // ヒドゥンストリート : ホコリだらけのプラットフォーム
    private final int map_stage_max = 910320304;
    private final int item_boarding_pass = 4001321; // 999客車乗車券
    private final int item_medal = 1142141; // 名誉駅務員の勲章
    private final int level_min = 25;
    private final int level_max = 30;
    private static final int MENU_DUSTY_PLATFORM = 1;
    private static final int MENU_TRAIN_999 = 2;
    private static final int MENU_MEDAL = 3;

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        // ホコリだらけのプラットフォーム
        if (map_id == this.map_dusty_platform) {
            warp(this.map_subway); // TODO : portal name.
            return false;
        }
        // 最後のプラットフォーム
        if (map_id == this.map_last_platform) {
            if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_boarding_pass, 1, "")) {
                // TODO : 英語の文章
                say("Please make room for 1 ETC slot.");
                return false;
            }
            MapleInventoryManipulator.addById(getPlayer(), this.item_boarding_pass, 1); // TODO : item effect.
            warp(this.map_subway); // TODO : portal name.
            return false;
        }
        // 各ステージ
        if (this.map_stage_min <= map_id && map_id <= this.map_stage_max) {
            switch (status) {
                case 0 -> {
                    // TODO : 英語の文章
                    askYesNo("Would you like to exit this place?");
                    return true;
                }
                case 1 -> {
                    warp(this.map_subway); // TODO : portal name.
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私は林次長です。");
                nt.add("#b#e#L" + MENU_DUSTY_PLATFORM + "#ホコリだらけのプラットフォームに下る#l#n");
                nt.add("#L" + MENU_TRAIN_999 + "#999番の客車に行く#l");
                nt.add("#L" + MENU_MEDAL + "#<名誉駅務員> 勲章を受け取る#l#k");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : 英語の文章
                switch (answer) {
                    case MENU_DUSTY_PLATFORM -> {
                        // TODO : グループリーダーの確認
                        int level = getPlayer().getLevel();
                        if (level < this.level_min || this.level_max < level) {
                            say("You must be in the Level Range 25-30 and be the party leader.");
                            return false;
                        }
                        if (!Event_PyramidSubway.warpStartSubway(getPlayer())) {
                            say("The Dusty Platform is currently full at the moment.");
                        }
                    }
                    case MENU_TRAIN_999 -> {
                        if (!checkItem(this.item_boarding_pass)) {
                            say("You do not have the Boarding Pass.");
                            return false;
                        }
                        if (!Event_PyramidSubway.warpBonusSubway(getPlayer())) {
                            say("The Train 999 is currently full at the moment");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_boarding_pass, 1, true, false);
                    }
                    case MENU_MEDAL -> {
                        // TODO : 名誉駅務員の勲章 (item_medal) の受け取り, クエスト 7662 のカスタムデータ (駅でのモンスター討伐数) が 10000 以上, クエスト 29931 の開始と完了
                        // 討伐数が足りない場合 : Please defeat at least 10,000 monsters in the Station and look for me again. Kills : " + mons
                        // アイテム欄が足りない場合 : Please make room.
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
}
