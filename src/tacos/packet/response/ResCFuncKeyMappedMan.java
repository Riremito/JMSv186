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
import odin.client.SkillMacro;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.network.MaplePacket;
import java.util.Map;
import tacos.packet.ServerPacket;
import odin.tools.Pair;

/**
 *
 * @author Riremito
 */
public class ResCFuncKeyMappedMan {

    /*
        @007D : LP_MacroSysDataInit
        @017C : LP_FuncKeyMappedInit
        @017D : LP_PetConsumeItemInit
        @017E : LP_PetConsumeMPItemInit
     */
    public static MaplePacket getMacros(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MacroSysDataInit);

        SkillMacro[] macros = chr.getMacros();
        int macro_count = 0;
        for (SkillMacro macro : macros) {
            if (macro != null) {
                macro_count++;
            }
        }

        sp.Encode1(macro_count);

        if (0 < macro_count) {
            for (SkillMacro macro : macros) {
                if (macro != null) {
                    sp.EncodeStr(macro.getName());
                    sp.Encode1(macro.getShout());
                    sp.Encode4(macro.getSkill1());
                    sp.Encode4(macro.getSkill2());
                    sp.Encode4(macro.getSkill3());
                }
            }
        }

        return sp.get();
    }

    public static MaplePacket getKeymap(MapleCharacter chr, boolean keymap_reset) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FuncKeyMappedInit);

        sp.Encode1(keymap_reset ? 1 : 0);

        if (!keymap_reset) {
            int KEY_MAP_SIZE = 94; // 470

            if (Region.IsKMS()) {
                KEY_MAP_SIZE = 89; // 445
            }

            if (Version.PostBB()) {
                KEY_MAP_SIZE = 126; // 630
            }

            for (int i = 0; i < KEY_MAP_SIZE; i++) {
                Map<Integer, Pair<Byte, Integer>> keymap = chr.getKeyLayout().get();
                Pair<Byte, Integer> binding = keymap.get(i);
                if (binding == null) {
                    sp.Encode1(0);
                    sp.Encode4(0);
                } else {
                    sp.Encode1(binding.getLeft());
                    sp.Encode4(binding.getRight());
                }
            }
        }

        return sp.get();
    }

    public static MaplePacket getPetAutoHPMP_JMS_v131(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PetConsumeItemInit);
        sp.Encode4(chr.getPetAutoHPItem());
        sp.Encode4(chr.getPetAutoMPItem());
        return sp.get();
    }

    public static MaplePacket getPetAutoHP(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PetConsumeItemInit);
        sp.Encode4(chr.getPetAutoHPItem());
        return sp.get();
    }

    public static MaplePacket getPetAutoMP(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PetConsumeMPItemInit);
        sp.Encode4(chr.getPetAutoMPItem());
        return sp.get();
    }

    public static MaplePacket getPetAutoCure(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_PetConsumeCureItemInit);
        sp.Encode4(chr.getPetAutoCureItem());
        return sp.get();
    }
}
