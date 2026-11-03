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
public class NpcDoorOfDimension extends NpcScriptMan {

    // 次元の扉がある場所
    private final int[] map_ids = {
        105070001, // 中央ダンジョン : アリの巣-広場
        100040106, // 隠れ道 : 邪気の森2
        105040305, // 中央ダンジョン : スリーピーダンジョン5
        107000402 // 隠れ道 : ルーパン沼2
    };
    // 次元の世界
    private final int[] to_map_ids = {
        108010301, // 隠れ道 : 次元の世界
        108010201, // 隠れ道 : 次元の世界
        108010101, // 隠れ道 : 次元の世界
        108010401 // 隠れ道 : 次元の世界
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
                        // TODO : FieldSet (ThirdJob1 ~ ThirdJob4) で入場する (1人ずつ, 制限時間20分), 他の人が入場中の場合は "すでに誰かが#b#p1022000##kの分身と戦っています。また後で来てください。" (翻訳)
                        warp(this.to_map_ids[i]);
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
