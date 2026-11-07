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
// Npc.wz script : begin_jp2 (JMS131-302), 会話文は lua_script_jp (begin_jp2) を利用
public class Npc_MapleIsland_PeterTraining extends NpcScriptMan {

    private final int quest_training = 8141; // 見習いの訓練
    private final int map_lith_harbor = 104000000; // リス : 港口
    private final int map_small_forest = 40000; // メイプルロード : 小さな森

    @Override
    public boolean action(int status, int answer) {
        if (getJob() != 0) {
            if (status == 0) {
                // 翻訳
                say("ここは見習いのためのクエストエリアです。あなたがいるべき場所ではありませんよね？", false, true);
                return true;
            }
            warp(this.map_lith_harbor);
            return false;
        }
        String quest_data = getQuestData(this.quest_training);
        if (!"enter".equals(quest_data)) {
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("すべての訓練を終えましたね。よく頑張りました。旅を始める準備ができたようですね！次の場所へ行けますよ。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("でも、覚えておいてください。向こうに着いたら、モンスターがたくさんいる村です。それでは、さようなら！", true, true);
                return true;
            }
            case 2 -> {
                gainExp(3);
                warp(this.map_small_forest);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
