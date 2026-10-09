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
package tacos.packet.response;

import odin.client.MapleCharacter;
import tacos.client.TacosSingleMacro;
import tacos.config.Region;
import java.util.Map;
import tacos.config.Config;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCFuncKeyMappedMan {

    // CWvsContext::OnMacroSysDataInit
    public static ServerPacket MacroSysDataInit(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MacroSysDataInit);

        // MACROSYSDATA::Decode
        sp.Encode1(chr.getMacros().size());
        for (TacosSingleMacro macro : chr.getMacros().values()) {
            sp.EncodeStr(macro.getName()); // sName
            sp.Encode1(macro.isMute() ? 1 : 0); // bMute
            sp.Encode4(macro.getSkill1()); // aSkill[0]
            sp.Encode4(macro.getSkill2()); // aSkill[1]
            sp.Encode4(macro.getSkill3()); // aSkill[2]
        }

        return sp;
    }

    // CFuncKeyMappedMan::OnInit
    public static ServerPacket FuncKeyMappedInit(MapleCharacter chr, boolean keymap_reset) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FuncKeyMappedInit);

        sp.Encode1(keymap_reset ? 1 : 0);

        if (!keymap_reset) {
            int KEY_MAP_SIZE = 94; // 470

            if (Region.KMS.check() || Region.KMST.check()) {
                KEY_MAP_SIZE = 89; // 445
            }

            if (Config.PostBB()) {
                KEY_MAP_SIZE = 126; // 630
            }

            for (int i = 0; i < KEY_MAP_SIZE; i++) {
                Map<Integer, SimpleImmutableEntry<Byte, Integer>> keymap = chr.getKeyLayout().get();
                SimpleImmutableEntry<Byte, Integer> binding = keymap.get(i);
                if (binding == null) {
                    sp.Encode1(0);
                    sp.Encode4(0);
                } else {
                    sp.Encode1((byte) binding.getKey());
                    sp.Encode4((int) binding.getValue());
                }
            }
        }

        return sp;
    }

    // CFuncKeyMappedMan::OnPetConsumeItemInit
    public static ServerPacket PetConsumeItemInit(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetConsumeItemInit);

        sp.Encode4(chr.getPetAutoHPItem());
        return sp;
    }

    // CFuncKeyMappedMan::OnPetConsumeMPItemInit
    public static ServerPacket PetConsumeMPItemInit(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetConsumeMPItemInit);

        sp.Encode4(chr.getPetAutoMPItem());
        return sp;
    }

    // JMS
    public static ServerPacket PetConsumeCureItemInit(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PetConsumeCureItemInit);

        sp.Encode4(chr.getPetAutoCureItem());
        return sp;
    }

    // CFuncKeyMappedMan::OnPetConsumeItemInit, JMS131
    public static ServerPacket PetConsumeItemInit_JMS131(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PetConsumeItemInit);

        sp.Encode4(chr.getPetAutoHPItem());
        sp.Encode4(chr.getPetAutoMPItem());
        return sp;
    }
}
