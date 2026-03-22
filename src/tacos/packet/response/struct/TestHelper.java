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
package tacos.packet.response.struct;

/**
 *
 * @author Riremito
 */
public class TestHelper {

    public static final byte[] unk1 = new byte[]{(byte) 0, (byte) 64, (byte) 224, (byte) 253};
    public static final long MAX_TIME = 150842304000000000L; //00 80 05 BB 46 E6 17 02
    private static final long FT_UT_OFFSET = 116444592000000000L; // EDT
    public static final byte[] unk2 = new byte[]{(byte) 59, (byte) 55, (byte) 79, (byte) 1};

    public static final long getTime(final long realTimestamp) {
        if (realTimestamp == -1) {
            return MAX_TIME;
        }
        long time = realTimestamp / 1000; // convert to seconds
        return (time * 10000000) + FT_UT_OFFSET;
    }

    public static final long getKoreanTimestamp(final long realTimestamp) {
        if (realTimestamp == -1) {
            return MAX_TIME;
        }
        long time = realTimestamp / 1000 / 60; // convert to minutes
        return (time * 600000000) + FT_UT_OFFSET;
    }

}
