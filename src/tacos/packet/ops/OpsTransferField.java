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
public enum OpsTransferField implements IPacketOps {
    TF_DONE(0),
    TF_DISABLED_PORTAL(1),
    TF_NOT_CONNECTED_AREA(2),
    TF_NOT_ALLOWED_LEVEL(3),
    TF_NOT_ALLOWED_LEVEL_ITEM(4),
    TF_NOT_ALLOWED_LEVEL_MD,
    TF_NOT_ALLOWED_LEVEL_FOR_ASWAN,
    TF_PARTY_ONLY,
    TF_PARTYBOSS_ONLY,
    TF_PARTYMEMBER_MIA,
    TF_EXPEDITION_ONLY,
    TF_DISABLE_INDUN,
    TF_DISABLE_INDUN_ENTERTIME,
    TF_NOT_AVAILABLE_SHOP,
    UNKNOWN(-1);

    private int value;

    OpsTransferField(int val) {
        this.value = val;
    }

    OpsTransferField() {
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

    public static OpsTransferField find(int val) {
        for (final OpsTransferField ops : OpsTransferField.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            TF_DONE.set(0);
            TF_DISABLED_PORTAL.set(1);
            TF_NOT_CONNECTED_AREA.set(2);
            TF_NOT_ALLOWED_LEVEL.set(3);
            TF_NOT_ALLOWED_LEVEL_ITEM.set(4);
            TF_NOT_ALLOWED_LEVEL_MD.set(5);
            // party level
            TF_NOT_ALLOWED_LEVEL_FOR_ASWAN.set(7);
            TF_PARTY_ONLY.set(8);
            TF_PARTYBOSS_ONLY.set(9);
            TF_PARTYMEMBER_MIA.set(10);
            TF_EXPEDITION_ONLY.set(11);
            TF_DISABLE_INDUN.set(12);
            TF_DISABLE_INDUN_ENTERTIME.set(13);
            TF_NOT_AVAILABLE_SHOP.set(19);
            // 20 : expedition
            return;
        }
    }

}
