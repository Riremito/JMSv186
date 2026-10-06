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
package tacos.packet.request;

import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import tacos.client.TacosSingleMacro;
import tacos.config.Config;
import tacos.config.Region;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsFuncKeyMapped;
import tacos.packet.response.ResCFuncKeyMappedMan;

/**
 *
 * @author Riremito
 */
public class ReqCFuncKeyMappedMan {

    public static boolean OnPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();

        if (chr == null) {
            return false;
        }

        switch (header) {
            case CP_UserMacroSysDataModified: {
                OnUserMacroSysDataModified(chr, cp);
                return true;
            }
            case CP_FuncKeyMappedModified: {
                OnFuncKeyMappedModified(chr, cp);
                return true;
            }
            case CP_QuickslotKeyMappedModified: {
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnUserMacroSysDataModified(MapleCharacter chr, ClientPacket cp) {
        // MACROSYSDATA::Encode
        byte macro_count = cp.Decode1();
        if (macro_count <= 0 || 6 <= macro_count) {
            return false;
        }

        for (int index = 0; index < macro_count; index++) {
            String name = cp.DecodeStr(); // sName
            byte bMute = cp.Decode1(); // bMute
            int skill_id_1 = cp.Decode4(); // aSkill[0]
            int skill_id_2 = cp.Decode4(); // aSkill[1]
            int skill_id_3 = cp.Decode4(); // aSkill[2]

            TacosSingleMacro tsm = new TacosSingleMacro();
            tsm.setName(name);
            tsm.setShout(bMute);
            tsm.setSkill1(skill_id_1);
            tsm.setSkill2(skill_id_2);
            tsm.setSkill3(skill_id_3);
            chr.getMacros().put(index, tsm);
        }

        return true;
    }

    public static boolean OnFuncKeyMappedModified(MapleCharacter chr, ClientPacket cp) {
        int funckey_type = cp.Decode4();

        switch (OpsFuncKeyMapped.find(funckey_type)) {
            case FuncKeyMapped_KeyModified: {
                int count = cp.Decode4();
                for (int i = 0; i < count; i++) {
                    int vk_code = cp.Decode4();
                    byte vk_type = cp.Decode1();
                    int vk_action = cp.Decode4();
                    chr.changeKeybinding(vk_code, vk_type, vk_action);
                }
                return true;
            }
            case FuncKeyMapped_PetConsumeHPItemModified: {
                int item_id = cp.Decode4();
                chr.setPetAutoHPItem(item_id);
                if (Config.LessOrEqual(Region.JMS, 131) || Region.HKMS.check() || Region.BMS.check() || Region.VMS.check()) {
                    chr.SendPacket(ResCFuncKeyMappedMan.PetConsumeItemInit_JMS131(chr));
                } else {
                    chr.SendPacket(ResCFuncKeyMappedMan.PetConsumeItemInit(chr));
                }
                return true;
            }
            case FuncKeyMapped_PetConsumeMPItemModified: {
                int item_id = cp.Decode4();
                chr.setPetAutoMPItem(item_id);
                if (Config.LessOrEqual(Region.JMS, 131) || Region.HKMS.check() || Region.BMS.check() || Region.VMS.check()) {
                    chr.SendPacket(ResCFuncKeyMappedMan.PetConsumeItemInit_JMS131(chr));
                } else {
                    chr.SendPacket(ResCFuncKeyMappedMan.PetConsumeMPItemInit(chr));
                }
                return true;
            }
            case FuncKeyMapped_JMS_PetConsumeCureItemModified: {
                int item_id = cp.Decode4();
                chr.setPetAutoCureItem(item_id);
                if (Config.LessOrEqual(Region.JMS, 131) || Region.HKMS.check() || Region.BMS.check() || Region.VMS.check()) {
                    // nothing
                } else {
                    chr.SendPacket(ResCFuncKeyMappedMan.PetConsumeCureItemInit(chr));
                }
                return true;
            }
            default: {
                DebugLogger.ErrorLog("OnFuncKeyMappedModified not coded " + funckey_type);
                break;
            }
        }

        return false;
    }
}
