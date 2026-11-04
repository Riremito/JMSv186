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
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
public class Npc_Victoria_DoorOfDimension extends NpcScriptMan {

    // 次元の扉がある場所
    private final int[] map_ids = {
        105070001, // 中央ダンジョン : アリの巣-広場
        100040106, // 隠れ道 : 邪気の森2
        105040305, // 中央ダンジョン : スリーピーダンジョン5
        107000402 // 隠れ道 : ルーパン沼2
    };
    // 次元の世界 (FieldSet)
    private final String[] field_sets = {
        "ThirdJob1", // 隠れ道 : 次元の世界
        "ThirdJob2", // 隠れ道 : 次元の世界
        "ThirdJob3", // 隠れ道 : 次元の世界
        "ThirdJob4" // 隠れ道 : 次元の世界
    };
    // 3次転職の教官
    private final int[] instructors = {
        1022000, // コブシを開いて立て
        1032001, // ハインズ
        1012100, // ヘレナ
        1052001 // ダークロード
    };
    private final int[][] job_ids = {
        {110, 120, 130}, // ファイター, ページ, スピアマン
        {210, 220, 230}, // ウィザード(火・毒), ウィザード(氷・雷), クレリック
        {310, 320}, // ハンター, クロスボウマン
        {410, 420} // アサシン, シーフ
    };
    private final int quest_job_3rd = 7500; // 3次転職

    @Override
    public boolean action(int status, int answer) {
        String quest_data = getPlayer().getQuest(MapleQuest.getInstance(this.quest_job_3rd)).getCustomData();
        for (int i = 0; i < this.map_ids.length; i++) {
            if (getMapId() != this.map_ids[i]) {
                continue;
            }
            if ("p1".equals(quest_data)) {
                for (int job_id : this.job_ids[i]) {
                    if (getPlayer().getJob() == job_id) {
                        if (enterFieldSet(this.field_sets[i], 0) != TacosFieldSet.ENTER_SUCCESS) {
                            // 翻訳
                            say("すでに誰かが#b#p" + this.instructors[i] + "##kの分身と戦っています。また後で来てください。");
                        }
                        return false;
                    }
                }
            }
            break;
        }
        // TODO : 海賊 (中央ダンジョン : エビルアイの巣２) の次元の扉
        // 翻訳
        say("別の次元へ通じる扉があるようですが、なぜか入ることができません。");
        return false;
    }
}
