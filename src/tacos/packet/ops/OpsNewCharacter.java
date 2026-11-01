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
package tacos.packet.ops;

import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsNewCharacter implements IPacketOps {
    Resistance(0), // レジスタンス
    Adventurers(1), // 冒険家
    KnightsOfCygnus(2),
    DualBlade(1), // デュアルブレイド (冒険家と必ず同じ値になる)
    Aran(3), // アラン
    Evan(4), // エヴァン
    CannonShooter, // キャノンシューター
    Hayato, // ハヤト
    Mercedes, // メルセデス
    DemonSlayer, // デーモンスレイヤー
    Phantom, // ファントム
    Kanna, // カンナ
    Chivalrous, // 蒼龍侠客
    Luminous,
    Kaizer,
    AngelicBuster,
    UNKNOWN;

    private int value;

    private OpsNewCharacter(int value) {
        this.value = value;
    }

    private OpsNewCharacter() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }

    public static OpsNewCharacter find(int value) {
        for (OpsNewCharacter ops : values()) {
            if (ops.get() == value) {
                if (value != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsNewCharacter ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        // JMS186, BB前は多分順番固定
        if (!Config.PostBB()) {
            clear();
            KnightsOfCygnus.set(0);
            Adventurers.set(1);
            DualBlade.set(1);
            Aran.set(2);
            Evan.set(3);
            return;
        }
        if (Config.GreaterOrEqual(Region.KMS, 138) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89)) {
            clear();
            Resistance.set(0);
            Adventurers.set(1);
            KnightsOfCygnus.set(2);
            DualBlade.set(1);
            Aran.set(3);
            Evan.set(4);
            CannonShooter.set(1); // v204
            Mercedes.set(5); // v204
            DemonSlayer.set(6); // v205
            Phantom.set(7); // v213限定, v302は作成不可
            Hayato.set(8); // v301
            Kanna.set(9); // v302
            // v314以降全部作成可能
            if (Config.GreaterOrEqual(Region.TWMS, 148)) {
                Chivalrous.set(10);
            }
            if (Config.GreaterOrEqual(Region.KMS, 169) || Config.GreaterOrEqual(Region.JMS, 308) || Config.GreaterOrEqual(Region.GMS, 126) || Config.GreaterOrEqual(Region.EMS, 89)) {
                Luminous.set(10); // v308
                Kaizer.set(11); // v308
                AngelicBuster.set(12); // v308
            }
            return;
        }
    }

    public static boolean isBadCodedJobs_GMS(OpsNewCharacter ops) {
        switch (ops) {
            case Mercedes: {
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }
}
