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

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : hontale_C (JMS186), lua の処理を利用
// Reactor (light) の状態によって光の洞窟か闇の洞窟へパーティーを移動させる
public class Portal_Leafre_Hontale_C extends PortalScriptMan {

    private final int map_cave_light = 240050300; // リプレ : 光の洞窟
    private final int map_cave_dark = 240050310; // リプレ : 闇の洞窟
    private final int state_light = 1;
    private final int state_dark = 3;

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Hontale1");
        if (field_set == null) {
            return false;
        }
        int state = field_set.getReactorState(6, "light");
        if (state != this.state_light && state != this.state_dark) {
            // 翻訳
            message("目に見えない力に行く手を阻まれています。");
            return false;
        }
        if (!isPartyBoss()) {
            // 翻訳
            message("進む方向を決められるのはパーティーリーダーです。");
            return false;
        }
        if (state == this.state_light) {
            // 翻訳
            fieldNotice(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "光の洞窟へ移動しました。");
            return transferFieldAll(this.map_cave_light, "st00");
        }
        // 翻訳
        fieldNotice(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "闇の洞窟へ移動しました。");
        return transferFieldAll(this.map_cave_dark, "st00");
    }
}
