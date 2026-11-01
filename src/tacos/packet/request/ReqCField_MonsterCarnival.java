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
package tacos.packet.request;

import tacos.server.map.object.TacosMonster;
import tacos.packet.ops.OpsMCarnivalFail;
import odin.server.MapleCarnivalParty;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.client.TacosClient;
import java.util.List;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCField_MonsterCarnival;
import odin.server.MapleCarnivalFactory;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.ClientPacketHeader;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class ReqCField_MonsterCarnival {

    public static boolean OnPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        TacosCharacter chr = client.getPlayer();
        if (chr == null) {
            return true;
        }

        TacosMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        switch (header) {
            case CP_MCarnivalRequest: {
                OnMCarnivalRequest(chr, cp);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // CUIMonsterCarnival::RequestSend
    // the client waits for a result of the request, a failed result has to be sent.
    public static void OnMCarnivalRequest(TacosCharacter chr, ClientPacket cp) {
        int tab = cp.Decode1();
        int num = cp.Decode4();

        MapleCarnivalParty party = chr.getCarnivalParty();
        TacosMap map = chr.getMap();
        if (party == null || map.getNodeInfo() == null) {
            chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_Unknown));
            return;
        }

        switch (tab) {
            // summon a monster
            case 0: {
                List<SimpleImmutableEntry<Integer, Integer>> mobs = map.getNodeInfo().getMobsToSpawn();
                if (num < 0 || mobs.size() <= num) {
                    chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_Unknown));
                    return;
                }
                int cp_loss = mobs.get(num).getValue();
                if (chr.getAvailableCP() < cp_loss) {
                    chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_NoCP));
                    return;
                }
                TacosMonster mons = WzXML.MOB.createMob(mobs.get(num).getKey());
                if (mons == null || !map.makeCarnivalSpawn(party.getTeam(), mons, num)) {
                    chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_MobLimit));
                    return;
                }
                useCP(chr, party, cp_loss);
                break;
            }
            // debuff skill
            case 1: {
                List<Integer> skill_ids = map.getNodeInfo().getSkillIds();
                MapleCarnivalFactory.MCSkill skill = (0 <= num && num < skill_ids.size()) ? MapleCarnivalFactory.getInstance().getSkill(skill_ids.get(num)) : null;
                if (skill == null) {
                    chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_Unknown));
                    return;
                }
                if (chr.getAvailableCP() < skill.cpLoss) {
                    chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_NoCP));
                    return;
                }
                // TODO : apply the debuff to the enemy team.
                chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_Unknown));
                return;
            }
            // summon a guardian
            case 2: {
                MapleCarnivalFactory.MCSkill skill = MapleCarnivalFactory.getInstance().getGuardian(num);
                if (skill == null) {
                    chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_Unknown));
                    return;
                }
                if (chr.getAvailableCP() < skill.cpLoss) {
                    chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_NoCP));
                    return;
                }
                if (!map.makeCarnivalReactor(party.getTeam(), num)) {
                    chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_GuardianLimit));
                    return;
                }
                useCP(chr, party, skill.cpLoss);
                break;
            }
            default: {
                chr.SendPacket(ResCField_MonsterCarnival.MCarnivalResultFail(OpsMCarnivalFail.MCarnivalFail_Unknown));
                return;
            }
        }
        map.broadcastPacket(ResCField_MonsterCarnival.MCarnivalResultSuccess(chr.getName(), tab, num));
    }

    private static void useCP(TacosCharacter chr, MapleCarnivalParty party, int cp_loss) {
        party.useCP(chr, cp_loss);
        chr.CPUpdate(false, chr.getAvailableCP(), chr.getTotalCP(), 0);
        // the team CP of the team that used CP is updated for everyone in the field.
        chr.getMap().broadcastPacket(ResCField_MonsterCarnival.CPUpdate(true, party.getAvailableCP(), party.getTotalCP(), party.getTeam()));
    }
}
