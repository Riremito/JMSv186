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

import config.Region;
import config.ServerConfig;
import config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsNewCharacter {
    // JMS v186, BB前は多分順番固定
    KnightsOfCygnus(0),
    Adventurers(1), // 冒険家
    DualBlade(1), // デュアルブレイド (冒険家と同じ値)
    Aran(2), // アラン
    Evan(3), // エヴァン
    // BB後, 順番入れ替わる
    Resistance(-1), // レジスタンス
    CannonShooter(-1), // キャノンシューター
    Hayato(-1), // ハヤト
    Mercedes(-1), // メルセデス
    DemonSlayer(-1), // デーモンスレイヤー
    Phantom(-1), // ファントム
    Kanna(-1), // カンナ
    Chivalrous(-1), // 蒼龍侠客
    Luminous(-1),
    Kaizer(-1),
    AngelicBuster(-1),
    UNKNOWN(-1);

    private int value;

    OpsNewCharacter(int flag) {
        value = flag;
    }

    OpsNewCharacter() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public void set(int val) {
        value = val;
    }

    public static OpsNewCharacter find(int val) {
        for (final OpsNewCharacter o : OpsNewCharacter.values()) {
            if (o.get() == val) {
                return o;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.PostBB()) {
            // JMS v188, 左上から右下に向かって連番 (デュアルブレイドは除外)
            Resistance.set(0);
            Adventurers.set(1);
            KnightsOfCygnus.set(2);
            DualBlade.set(1); // 冒険家と必ず同じ値になる
            Aran.set(3);
            Evan.set(4);
        }
        if (ServerConfig.KMS138orLater() || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.GMS, 111)) {
            CannonShooter.set(1); // v204
            Mercedes.set(5); // v204
            DemonSlayer.set(6); // v205
            Phantom.set(7); // v213限定, v302は作成不可
            Hayato.set(8); // v301
            Kanna.set(9); // v302
            // v314以降全部作成可能
        }
        if (Version.GreaterOrEqual(Region.TWMS, 148)) {
            Chivalrous.set(10);
        }
        if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.EMS, 89)) {
            Luminous.set(10); // v308
            Kaizer.set(11); // v308
            AngelicBuster.set(12); // v308
        }
    }
}
