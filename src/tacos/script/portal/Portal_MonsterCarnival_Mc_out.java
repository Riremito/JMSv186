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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : mc_out (JMS186), lua の処理を利用
// モンスターカーニバルに来る前の町へ戻る
public class Portal_MonsterCarnival_Mc_out extends PortalScriptMan {

    private final int quest_carnival_return = 7050; // 戻り先の町
    private final int map_kerning = 103000000; // カニングシティー (lua のコメントではヘネシス)
    private final int map_orbis = 200000000; // オルビス
    private final int map_ludibrium = 220000000; // ルディブリアム

    @Override
    public boolean enter() {
        String val = getQuestData(this.quest_carnival_return);
        int map_id;
        switch (val) {
            case "0" -> {
                map_id = this.map_kerning;
            }
            case "1" -> {
                map_id = this.map_orbis;
            }
            default -> {
                map_id = this.map_ludibrium;
            }
        }
        transferField(map_id, "");
        forceCompleteQuest(this.quest_carnival_return);
        return true;
    }
}
