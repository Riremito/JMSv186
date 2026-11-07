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
public class Npc_ElNath_Amon extends NpcScriptMan {

    private final int map_zakum_altar = 280030000; // ジャクムダンジョン : ジャクムの祭壇
    private final int map_zakum_gate = 211042300; // エルナス : ジャクムへの門
    private final int quest_zakum_count = 7004; // クエスト名なし (ジャクムの祭壇の入場回数)

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getMapId() != this.map_zakum_altar) {
                    // 翻訳
                    askYesNo("本当にあきらめてここから出ますか？次に戻ってきたときは、最初からやり直さなければなりません。");
                    return true;
                }
                String count = getQuestData(this.quest_zakum_count);
                if ("2".equals(count)) {
                    // 翻訳
                    askYesNo("本当にここから出ますか？ジャクムの祭壇には1日2回まで入ることができますが、すでに2回入っているので、今出ると、今日はもうこの祭壇に入れなくなります。");
                    return true;
                }
                // TODO : 入場回数が1または2以外の場合の処理 (luaでは何も表示されない)
                // 翻訳
                askYesNo("本当にここから出ますか？ジャクムの祭壇には1日2回まで入ることができますが、今出ると、今日はあと1回しかこの祭壇に入れません。");
                return true;
            }
            case 1 -> {
                warp(this.map_zakum_gate);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
