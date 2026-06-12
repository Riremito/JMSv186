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

/**
 *
 * @author Riremito
 */
public class TacosCalcDamage {

    public int m_s1 = 0; // m_RndGenForCharacter.m_s1
    public int m_s2 = 0; // m_RndGenForCharacter.m_s2
    public int m_s3 = 0; // m_RndGenForCharacter.m_s3

    // CRand32::CRand32
    public TacosCalcDamage() {
        int time = (int) (System.currentTimeMillis() % 0x100000000L);
        int rand = 0x45C82BE5 * time - 0x2D09A4AB;
        this.m_s1 = rand | 0x100000;
        this.m_s2 = rand | 0x1000;
        this.m_s3 = rand | 0x100;
    }

    // CRand32::Random
    public int random() {
        int v1 = (this.m_s1 << 12) ^ (this.m_s1 >>> 19) ^ ((this.m_s1 >>> 6) ^ (this.m_s1 << 12)) & 0x1FFF;
        int v2 = (this.m_s2 << 4) ^ (this.m_s2 >>> 25) ^ ((this.m_s2 << 4) ^ (this.m_s2 >>> 23)) & 0x7F;
        int v3 = (this.m_s3 >>> 11) ^ (this.m_s3 << 17) ^ ((this.m_s3 >>> 8) ^ (this.m_s3 << 17)) & 0x1FFFFF;
        this.m_s1 = v1;
        this.m_s2 = v2;
        this.m_s3 = v3;
        return v1 ^ v2 ^ v3;
    }

    // CalcDamage::SetSeed
    public void setSeed(int v1, int v2, int v3) {
        this.m_s1 = v1 | 0x100000;
        this.m_s2 = v2 | 0x1000;
        this.m_s3 = v3 | 0x10;
        setNextAttackCritical(false);
    }

    public int[] getRandoms(int size) {
        int rand_array[] = new int[size];
        for (int i = 0; i < size; i++) {
            int rand = random();
            rand_array[i] = rand;
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
    public static double getRand(int rand, double f0, double f1) {
        double range = Math.abs(f1 - f0);
        double base = Math.min(f0, f1);
        return base + (double) (Integer.toUnsignedLong(rand) % 10_000_000) * range / 9_999_999.0;
    }
}
