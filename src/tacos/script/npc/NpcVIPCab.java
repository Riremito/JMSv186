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

import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
public class NpcVIPCab extends NpcScriptMan {

    private final int map_ant_tunnel_park = 105070001; // 中央ダンジョン : アリの巣-広場

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcTag nt = new NpcTag();
                nt.add("初心者ではない方には決められた料金が請求されます。アリの巣広場はビクトリアアイランドの中央にあるダンジョンの奥の、24時間屋台が棲んでいるところです。#b10,000メル#kでアリの巣広場まで如何でしょうか？");
                super.askYesNo(chr, nt);
                return true;
            }
            case 1 -> {
                // TODO : 10000 メルの支払い
                chr.changeMapById(this.map_ant_tunnel_park); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
