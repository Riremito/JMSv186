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
import odin.client.MapleClient;
import odin.client.SkillMacro;
import tacos.config.Region;
import tacos.config.Version;
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

    /*
        @006C : CP_UserMacroSysDataModified
        @008E : CP_FuncKeyMappedModified
        @00BF : CP_QuickslotKeyMappedModified
     */
    public static boolean OnPacket(ClientPacketHeader header, ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();

        if (chr == null) {
            return false;
        }

        switch (header) {
            // ChangeSkillMacro
            case CP_UserMacroSysDataModified: {
                byte macro_count = cp.Decode1();
                for (int i = 0; i < macro_count; i++) {
                    String name = cp.DecodeStr();
                    byte shout = cp.Decode1();
                    int skill_id_1 = cp.Decode4();
                    int skill_id_2 = cp.Decode4();
                    int skill_id_3 = cp.Decode4();

                    chr.updateMacros(i, new SkillMacro(skill_id_1, skill_id_2, skill_id_3, name, shout, i));
                }

                return true;
            }
            // ChangeKeymap
            case CP_FuncKeyMappedModified: {
                OnFuncKeyMappedModified(cp, chr);
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

    public static boolean OnFuncKeyMappedModified(ClientPacket cp, MapleCharacter chr) {
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
                if (Version.LessOrEqual(Region.JMS, 131)) {
                    chr.SendPacket(ResCFuncKeyMappedMan.getPetAutoHPMP_JMS_v131(chr));
                } else {
                    chr.SendPacket(ResCFuncKeyMappedMan.getPetAutoHP(chr));
                }
                return true;
            }
            case FuncKeyMapped_PetConsumeMPItemModified: {
                int item_id = cp.Decode4();
                chr.setPetAutoMPItem(item_id);
                if (Version.LessOrEqual(Region.JMS, 131)) {
                    chr.SendPacket(ResCFuncKeyMappedMan.getPetAutoHPMP_JMS_v131(chr));
                } else {
                    chr.SendPacket(ResCFuncKeyMappedMan.getPetAutoMP(chr));
                }
                return true;
            }
            case FuncKeyMapped_JMS_PetConsumeCureItemModified: {
                int item_id = cp.Decode4();
                chr.setPetAutoCureItem(item_id);
                if (Version.LessOrEqual(Region.JMS, 131)) {
                    // nothing
                } else {
                    chr.SendPacket(ResCFuncKeyMappedMan.getPetAutoCure(chr));
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
