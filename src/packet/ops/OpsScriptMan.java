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
package packet.ops;

import config.ServerConfig;

/**
 *
 * @author Riremito
 */
public enum OpsScriptMan {
    // JMS v186
    SM_SAY(0),
    SM_SAYIMAGE(1),
    SM_ASKYESNO(2),
    SM_ASKTEXT(3),
    SM_ASKNUMBER(4),
    SM_ASKMENU(5),
    SM_ASKQUIZ(6),
    SM_ASKSPEEDQUIZ(7),
    SM_ASKAVATAR(8),
    SM_ASKPET(9),
    SM_ASKPETALL(10),
    SM_ASKACCEPT(12),
    SM_ASKBOXTEXT(13),
    SM_ASKSLIDEMENU(14),
    SM_SCRIPT(-1),
    SM_ASKCENTER(-1),
    SM_ASKMEMBERSHOPAVATAR(-1),
    UNKNOWN(-1);

    int value;

    OpsScriptMan(int skill_id) {
        this.value = skill_id;
    }

    OpsScriptMan() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public void set(int skill_id) {
        this.value = skill_id;
    }

    public static OpsScriptMan find(int skill_id) {
        for (final OpsScriptMan o : OpsScriptMan.values()) {
            if (o.get() == skill_id) {
                return o;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (ServerConfig.JMS302orLater()) {
            return;
        }
        if (ServerConfig.IsPostBB()) {
            return;
        }
        if (ServerConfig.KMS84orEarlier()) {
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
            SM_SAYIMAGE.set(-1);
            SM_ASKSLIDEMENU.set(-1);
        }
        if (ServerConfig.JMS180orLater()) {
            return;
        }
        if (ServerConfig.JMS147orLater()) {
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
            SM_SAYIMAGE.set(-1);
            SM_ASKSLIDEMENU.set(-1);
            return;
        }
        if (ServerConfig.JMS131orEarlier()) {
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
            SM_SAYIMAGE.set(-1);
            SM_ASKSPEEDQUIZ.set(-1);
            SM_ASKSLIDEMENU.set(-1);
            return;
        }
    }
}
