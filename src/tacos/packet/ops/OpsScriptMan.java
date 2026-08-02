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
public enum OpsScriptMan implements IPacketOps {
    SM_SAY(0),
    SM_SAYIMAGE(1),
    SM_ASKYESNO(2),
    SM_ASKTEXT(3),
    SM_ASKNUMBER(4),
    SM_ASKMENU(5),
    SM_ASKQUIZ(6),
    SM_ASKSPEEDQUIZ(7),
    SM_ASKAVATAR(8),
    SM_ASKMEMBERSHOPAVATAR(9),
    SM_ASKPET(10),
    SM_ASKPETALL(11),
    SM_SCRIPT(12),
    SM_ASKACCEPT(13),
    SM_ASKBOXTEXT(14),
    SM_ASKSLIDEMENU(15),
    SM_ASKCENTER(16),
    UNKNOWN;

    private int value;

    OpsScriptMan(int val) {
        this.value = val;
    }

    OpsScriptMan() {
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

    public static OpsScriptMan find(int val) {
        for (OpsScriptMan ops : values()) {
            if (ops.get() == val) {
                if (val != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsScriptMan ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 131)) {
            SM_ASKAVATAR.set(9);
            SM_ASKACCEPT.set(14);
            SM_ASKBOXTEXT.set(15);
            SM_ASKSLIDEMENU.set(16);
            return;
        }
        // GMS95
        if (Version.GreaterOrEqual(Region.GMS, 95)) {
            return;
        }
        clear();
        // JMS180-194.
        if (Version.PostBB() || Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70) || Version.GreaterOrEqual(Region.GMS, 84)) {
            SM_SAY.set(0);
            SM_SAYIMAGE.set(1);
            SM_ASKYESNO.set(2);
            SM_ASKTEXT.set(3);
            SM_ASKNUMBER.set(4);
            SM_ASKMENU.set(5);
            SM_ASKQUIZ.set(6);
            SM_ASKSPEEDQUIZ.set(7);
            SM_ASKAVATAR.set(8);
            SM_ASKPET.set(9);
            SM_ASKPETALL.set(10);
            SM_SCRIPT.set(11);
            SM_ASKACCEPT.set(12);
            SM_ASKBOXTEXT.set(13);
            SM_ASKSLIDEMENU.set(14);
            return;
        }
        if (Version.Equal(Region.BMS, 24)) {
            SM_SAY.set(0);
            SM_ASKYESNO.set(1);
            SM_ASKTEXT.set(2);
            SM_ASKNUMBER.set(4);
            SM_ASKMENU.set(5);
            SM_ASKQUIZ.set(6);
            SM_ASKSPEEDQUIZ.set(7);
            SM_ASKAVATAR.set(8);
            SM_ASKPET.set(9);
            SM_ASKPETALL.set(10);
            SM_ASKACCEPT.set(13);
            SM_ASKBOXTEXT.set(14);
            return;
        }
        if (Version.GreaterOrEqual(Region.KMS, 84)) {
            SM_SAY.set(0);
            SM_ASKYESNO.set(1);
            SM_ASKTEXT.set(2);
            SM_ASKNUMBER.set(3);
            SM_ASKMENU.set(4);
            SM_ASKQUIZ.set(5);
            SM_ASKSPEEDQUIZ.set(6);
            SM_ASKAVATAR.set(7);
            SM_ASKPET.set(8);
            SM_ASKPETALL.set(9);
            // reserved
            SM_ASKACCEPT.set(11);
            SM_ASKBOXTEXT.set(12);
            return;
        }
        // JMS146-165.
        if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 47) || Version.GreaterOrEqual(Region.JMS, 146) || Version.GreaterOrEqual(Region.CMS, 62) || Version.GreaterOrEqual(Region.TWMS, 73) || Version.GreaterOrEqual(Region.THMS, 0) || Version.GreaterOrEqual(Region.GMS, 61) || Version.GreaterOrEqual(Region.MSEA, 0) || Version.GreaterOrEqual(Region.EMS, 0) || Version.GreaterOrEqual(Region.BMS, 24) || Version.GreaterOrEqual(Region.VMS, 35)) {
            SM_SAY.set(0);
            SM_ASKYESNO.set(1);
            SM_ASKTEXT.set(2);
            SM_ASKNUMBER.set(3);
            SM_ASKMENU.set(4);
            SM_ASKQUIZ.set(5);
            SM_ASKSPEEDQUIZ.set(6);
            SM_ASKAVATAR.set(7);
            SM_ASKPET.set(8);
            SM_ASKPETALL.set(9);
            // 11, CScriptMan::OnAskYesNo, 0
            SM_ASKACCEPT.set(12);
            SM_ASKBOXTEXT.set(13);
            // 14, CScriptMan::OnSay, 1
            return;
        }
        // JMS131.
        SM_SAY.set(0);
        SM_ASKYESNO.set(1);
        SM_ASKTEXT.set(2);
        SM_ASKNUMBER.set(3);
        SM_ASKMENU.set(4);
        SM_ASKQUIZ.set(5);
        SM_ASKAVATAR.set(6);
        SM_ASKPET.set(7);
        SM_ASKPETALL.set(8);
        // 10, CScriptMan::OnAskYesNo, 0
        SM_ASKACCEPT.set(11);
        SM_ASKBOXTEXT.set(13);
        return;
    }
}
