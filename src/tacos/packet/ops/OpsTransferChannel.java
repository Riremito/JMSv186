/*
 * Copyright (C) 2025 Riremito
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

import tacos.config.Region;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsTransferChannel implements IPacketOps {
    TC_DONE(0),
    TC_GAMESVR_DISCONNECTED(1),
    TC_SHOPSVR_DISCONNECTED(2),
    TC_PVESVR_DISCONNECTED,
    TC_USER_NOTACTIVEACCOUNT,
    TC_ITCSVR_DISCONNECTED,
    TC_ITCSVR_OVERLIMITUSER,
    TC_CHANNEL_RESTRICTION,
    TC_ITCSVR_LOWLEVELUSER,
    UNKNOWN(-1);

    private int value;

    OpsTransferChannel(int val) {
        this.value = val;
    }

    OpsTransferChannel() {
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

    // not used
    public static IPacketOps find(int val) {
        for (final IPacketOps ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            TC_DONE.set(0);
            TC_GAMESVR_DISCONNECTED.set(1);
            TC_SHOPSVR_DISCONNECTED.set(2);
            TC_PVESVR_DISCONNECTED.set(3);
            TC_USER_NOTACTIVEACCOUNT.set(4);
            TC_ITCSVR_DISCONNECTED.set(5);
            TC_ITCSVR_OVERLIMITUSER.set(6);
            TC_CHANNEL_RESTRICTION.set(7);
            TC_ITCSVR_LOWLEVELUSER.set(8);
            // 9 : change settings
            return;
        }
    }

}
