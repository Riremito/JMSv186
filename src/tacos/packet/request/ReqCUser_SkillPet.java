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
package tacos.packet.request;

import tacos.client.TacosClient;
import odin.server.maps.MapleMap;
import tacos.client.TacosCharacter;
import tacos.client.TacosSkillPet;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCUser_SkillPet;

/**
 *
 * @author Riremito
 */
public class ReqCUser_SkillPet {

    public static boolean OnSkillPetPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        TacosCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        int spet_id = cp.Decode4();
        TacosSkillPet skill_pet = chr.getSkillPet();
        if (skill_pet == null || skill_pet.getId() != spet_id) {
            return false;
        }

        switch (header) {
            case CP_SkillPetMove: {
                byte unk = cp.Decode1();
                ParseCMovePath move_path = new ParseCMovePath();
                if (move_path.Decode(cp)) {
                    skill_pet.update(move_path);
                    map.broadcastMessage(chr, ResCUser_SkillPet.SkillPetMove(skill_pet, move_path), false);
                }
                return true;
            }
            case CP_SkillPetAction: {
                return true;
            }
            case CP_SkillPetState: {
                return true;
            }
            case CP_SkillPetDropPickUpRequest: {
                return true;
            }
            case CP_SkillPetUpdateExceptionListRequest: {
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }
}
