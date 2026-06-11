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
package tacos.client;

import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class TacosCalcDamage {

    public int seed_1_init = 0;
    public int seed_2_init = 0;
    public int seed_3_init = 0;
    public int seed_1 = 0;
    public int seed_2 = 0;
    public int seed_3 = 0;

    // CRand32::CRand32
    public TacosCalcDamage() {
        int time = (int) (System.currentTimeMillis() % 0x100000000L);
        this.seed_1 = 0x45C82BE5 * time - 0x2D09A4AB;
        this.seed_2 = 0x45C82BE5 * time - 0x2D09A4AB;
        this.seed_3 = 0x45C82BE5 * time - 0x2D09A4AB;
    }

    // CRand32::Random
    public int random() {
        int v1 = (this.seed_1 << 12) ^ (this.seed_1 >>> 19) ^ ((short) (this.seed_1 >>> 6) ^ (short) (this.seed_1 << 12)) & 0x1FFF;
        int v2 = (16 * this.seed_2) ^ (this.seed_2 >>> 25) ^ ((byte) (16 * this.seed_2) ^ (byte) (this.seed_2 >>> 23)) & 0x7F;
        int v3 = (this.seed_3 >>> 11) ^ (this.seed_3 << 17) ^ ((this.seed_3 >>> 8) ^ (this.seed_3 << 17)) & 0x1FFFFF;
        this.seed_1 = v1;
        this.seed_2 = v2;
        this.seed_3 = v3;
        return v1 ^ v2 ^ v3;
    }

    // CalcDamage::SetSeed
    public void setSeed() {
        int v1 = random();
        int v2 = random();
        int v3 = random();
        this.seed_1_init = v1;
        this.seed_2_init = v2;
        this.seed_3_init = v3;
        this.seed_1 = v1 | 0x100000;
        this.seed_2 = v2 | 0x1000;
        this.seed_3 = v3 | 0x10;
        setNextAttackCritical(false);
        //DebugLogger.DebugLog(String.format("setSeed : %08X, %08X, %08X", v1, v2, v3));
    }

    public long[] getRandoms(int size) {
        long rand_array[] = new long[size];
        for (int i = 0; i < size; i++) {
            int v1 = this.seed_1;
            int v2 = this.seed_2;
            int v3 = this.seed_3;
            int rand = random();
            rand_array[i] = Integer.toUnsignedLong(rand);
            //DebugLogger.DebugLog(String.format("seed = %08X, %08X, %08X, ret = %08X", v1, v2, v3, rand));
        }
        return rand_array;
    }

    private boolean nextAttackCritical = false;

    public boolean isNextAttackCritical() {
        return this.nextAttackCritical;
    }

    public void setNextAttackCritical(boolean nextAttackCritical) {
        this.nextAttackCritical = nextAttackCritical;
    }

    // teto's code.
    public static double getRand(long rand, double f0, double f1) {
        if (f0 == f1) {
            return f0;
        }
        if (f0 < f1) {
            return f0 + (double) (rand % 10_000_000) * (f1 - f0) / 9_999_999.0;
        }
        return f1 + (double) (rand % 10_000_000) * (f0 - f1) / 9_999_999.0;
    }
}
