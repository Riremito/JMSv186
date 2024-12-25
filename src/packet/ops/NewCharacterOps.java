/*
 * Copyright (C) 2024 Riremito
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
package packet.ops;

import config.ServerConfig;

/**
 *
 * @author Riremito
 */
public enum NewCharacterOps {
    // JMS v186, BB前は多分順番固定
    KnightsOfCygnus(0),
    Adventurers(1), // 冒険家
    DualBlade(1), // デュアルブレイド (冒険家と同じ値)
    Aran(2), // アラン
    Evan(3), // エヴァン
    Resistance(-1),
    UNKNOWN(-1);

    private int value;

    NewCharacterOps(int flag) {
        value = flag;
    }

    NewCharacterOps() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public void set(int val) {
        value = val;
    }

    public static NewCharacterOps find(int val) {
        for (final NewCharacterOps o : NewCharacterOps.values()) {
            if (o.get() == val) {
                return o;
            }
        }
        return UNKNOWN;
    }

    public static void Init() {
        if (ServerConfig.IsPostBB()) {
            // JMS v188, 左上から右下に向かって連番 (デュアルブレイドは除外)
            Resistance.set(0);
            Adventurers.set(1);
            KnightsOfCygnus.set(2);
            DualBlade.set(1); // 冒険家と必ず同じ値になる
            Aran.set(3);
            Evan.set(4);
        }
    }
}
