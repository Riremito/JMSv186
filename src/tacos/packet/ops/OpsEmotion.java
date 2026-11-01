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
package tacos.packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsEmotion implements IPacketOps {

    EMT_BLINK(0),
    EMT_HIT(1),
    EMT_SMILE(2),
    EMT_TROUBLED(3),
    EMT_CRY(4),
    EMT_ANGRY(5),
    EMT_BEWILDERED(6),
    EMT_STUNNED(7),
    EMT_VOMIT(8),
    EMT_OOPS(9),
    EMT_CHEERS(10),
    EMT_CHU(11),
    EMT_WINK(12),
    EMT_PAIN(13),
    EMT_GLITTER(14),
    EMT_BLAZE(15),
    EMT_SHINE(16),
    EMT_LOVE(17),
    EMT_DESPAIR(18),
    EMT_HUM(19),
    EMT_BOWING(20),
    EMT_HOT(21),
    EMT_DAM(22),
    EMT_QBLUE(23),
    EMT_NO(24),
    UNKNOWN;

    private int value;

    OpsEmotion(int val) {
        this.value = val;
    }

    OpsEmotion() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }
}
