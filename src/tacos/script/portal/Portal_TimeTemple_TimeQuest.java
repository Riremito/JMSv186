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
// Map.wz portal script : timeQuest (JMS186), js の処理を利用
// 時間の神殿
// TODO : 前提クエスト (4列目) が完了していない場合は前のマップへ移動させる
public class Portal_TimeTemple_TimeQuest extends PortalScriptMan {

    private final int item_chaos_orb = 4032002; // 混沌の玉
    // 現在のマップ, 移動先のマップ, 移動先のポータル, 前提クエスト
    private final Object[][] time_quest_portal = {
        // 思い出の道
        {270010100, 270010110, "out00", 3501},
        {270010200, 270010210, "out00", 3502},
        {270010300, 270010310, "out00", 3503},
        {270010400, 270010410, "out00", 3504},
        {270010500, 270020000, "out00", 3507},
        // 後悔の道
        {270020100, 270020110, "out00", 3508},
        {270020200, 270020210, "out00", 3509},
        {270020300, 270020310, "out00", 3510},
        {270020400, 270020410, "out00", 3511},
        {270020500, 270030000, "out00", 3514},
        // 忘却の道
        {270030100, 270030110, "out00", 3515},
        {270030200, 270030210, "out00", 3516},
        {270030300, 270030310, "out00", 3517},
        {270030400, 270030410, "out00", 3518},
        {270030500, 270040000, "out00", 3521},
        // ピンクビーン
        {270040000, 270040100, "out00", 0},
    };

    @Override
    public boolean enter() {
        for (Object[] data : this.time_quest_portal) {
            if (getMapId() == (int) data[0]) {
                // TODO : js ではデバッグ用に混沌の玉を与えている
                if (!haveItem(this.item_chaos_orb)) {
                    gainItem(this.item_chaos_orb, 1);
                }
                return warp((int) data[1], (String) data[2]);
            }
        }
        return false;
    }
}
