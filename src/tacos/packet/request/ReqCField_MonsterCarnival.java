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
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.client.TacosClient;
import java.util.List;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCField_MonsterCarnival;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.response.builder.PB_BroadcastMsg;
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

    public static void OnMCarnivalRequest(TacosCharacter chr, ClientPacket cp) {
        if (chr.getCarnivalParty() == null) {
            chr.sendStatChanged(true);
            return;
        }
        int tab = cp.Decode1();
        int num = cp.Decode4();

        TacosMap map = chr.getMap();
        if (tab == 0) {
            List<SimpleImmutableEntry<Integer, Integer>> mobs = map.getNodeInfo().getMobsToSpawn();
            if (num >= mobs.size() || chr.getAvailableCP() < mobs.get(num).getValue()) {
                chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("You do not have the CP.").build()));
                chr.sendStatChanged(true);
                return;
            }
            TacosMonster mons = WzXML.MOB.createMob(mobs.get(num).getKey());
            if (mons != null && map.makeCarnivalSpawn(chr.getCarnivalParty().getTeam(), mons, num)) {
                chr.getCarnivalParty().useCP(chr, mobs.get(num).getValue());
                chr.CPUpdate(false, chr.getAvailableCP(), chr.getTotalCP(), 0);
                for (TacosCharacter player : map.getAllPlayers()) {
                    player.CPUpdate(true, player.getCarnivalParty().getAvailableCP(), player.getCarnivalParty().getTotalCP(), player.getCarnivalParty().getTeam());
                }
                map.broadcastPacket(ResCField_MonsterCarnival.MCarnivalResultSuccess(chr.getName(), tab, num));
                chr.sendStatChanged(true);
            } else {
                chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("You may no longer summon the monster.").build()));
                chr.sendStatChanged(true);
            }

        } else if (tab == 1) { //debuff
            List<Integer> skillid = map.getNodeInfo().getSkillIds();
            if (num >= skillid.size()) {
                chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("An error occurred.").build()));
                chr.sendStatChanged(true);
                return;
            }
            MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getSkill(skillid.get(num)); //ugh wtf
            if (skil == null || chr.getAvailableCP() < skil.cpLoss) {
                chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("You do not have the CP.").build()));
                chr.sendStatChanged(true);
                return;
            }
            boolean found = false;
            if (found) {
                chr.getCarnivalParty().useCP(chr, skil.cpLoss);
                chr.CPUpdate(false, chr.getAvailableCP(), chr.getTotalCP(), 0);
                for (TacosCharacter player : map.getAllPlayers()) {
                    player.CPUpdate(true, player.getCarnivalParty().getAvailableCP(), player.getCarnivalParty().getTotalCP(), player.getCarnivalParty().getTeam());
                    //chr.dropMessage(5, "[" + (chr.getCarnivalParty().getTeam() == 0 ? "Red" : "Blue") + "] " + chr.getName() + " has used a skill. [" + dis.name() + "].");
                }
                map.broadcastPacket(ResCField_MonsterCarnival.MCarnivalResultSuccess(chr.getName(), tab, num));
                chr.sendStatChanged(true);
            } else {
                chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("An error occurred.").build()));
                chr.sendStatChanged(true);
            }
        } else if (tab == 2) { //skill
            MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
            if (skil == null || chr.getAvailableCP() < skil.cpLoss) {
                chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("You do not have the CP.").build()));
                chr.sendStatChanged(true);
                return;
            }
            if (map.makeCarnivalReactor(chr.getCarnivalParty().getTeam(), num)) {
                chr.getCarnivalParty().useCP(chr, skil.cpLoss);
                chr.CPUpdate(false, chr.getAvailableCP(), chr.getTotalCP(), 0);
                for (TacosCharacter player : map.getAllPlayers()) {
                    player.CPUpdate(true, player.getCarnivalParty().getAvailableCP(), player.getCarnivalParty().getTotalCP(), player.getCarnivalParty().getTeam());
                }
                map.broadcastPacket(ResCField_MonsterCarnival.MCarnivalResultSuccess(chr.getName(), tab, num));
                chr.sendStatChanged(true);
            } else {
                chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("You may no longer summon the being.").build()));
                chr.sendStatChanged(true);
            }
        }

    }
}
