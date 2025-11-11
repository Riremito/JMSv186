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
package packet.request;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.MapleDisease;
import java.util.List;
import packet.ClientPacket;
import packet.response.ResCField_MonsterCarnival;
import packet.response.wrapper.ResWrapper;
import odin.server.MapleCarnivalFactory;
import odin.server.Randomizer;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleMap;
import odin.tools.Pair;

/**
 *
 * @author Riremito
 */
public class ReqCField_MonsterCarnival {

    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return true;
        }

        MapleMap map = chr.getMap();
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

    public static void OnMCarnivalRequest(MapleCharacter chr, ClientPacket cp) {
        if (chr.getCarnivalParty() == null) {
            chr.UpdateStat(true);
            return;
        }
        final int tab = cp.Decode1();
        final int num = cp.Decode4();

        if (tab == 0) {
            final List<Pair<Integer, Integer>> mobs = chr.getMap().getMobsToSpawn();
            if (num >= mobs.size() || chr.getAvailableCP() < mobs.get(num).right) {
                chr.SendPacket(ResWrapper.BroadCastMsgEvent("You do not have the CP."));
                chr.UpdateStat(true);
                return;
            }
            final MapleMonster mons = MapleLifeFactory.getMonster(mobs.get(num).left);
            if (mons != null && chr.getMap().makeCarnivalSpawn(chr.getCarnivalParty().getTeam(), mons, num)) {
                chr.getCarnivalParty().useCP(chr, mobs.get(num).right);
                chr.CPUpdate(false, chr.getAvailableCP(), chr.getTotalCP(), 0);
                for (MapleCharacter player : chr.getMap().getCharactersThreadsafe()) {
                    player.CPUpdate(true, player.getCarnivalParty().getAvailableCP(), player.getCarnivalParty().getTotalCP(), player.getCarnivalParty().getTeam());
                }
                chr.getMap().broadcastMessage(ResCField_MonsterCarnival.playerSummoned(chr.getName(), tab, num));
                chr.UpdateStat(true);
            } else {
                chr.SendPacket(ResWrapper.BroadCastMsgEvent("You may no longer summon the monster."));
                chr.UpdateStat(true);
            }

        } else if (tab == 1) { //debuff
            final List<Integer> skillid = chr.getMap().getSkillIds();
            if (num >= skillid.size()) {
                chr.SendPacket(ResWrapper.BroadCastMsgEvent("An error occurred."));
                chr.UpdateStat(true);
                return;
            }
            final MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getSkill(skillid.get(num)); //ugh wtf
            if (skil == null || chr.getAvailableCP() < skil.cpLoss) {
                chr.SendPacket(ResWrapper.BroadCastMsgEvent("You do not have the CP."));
                chr.UpdateStat(true);
                return;
            }
            final MapleDisease dis = skil.getDisease();
            boolean found = false;
            for (MapleCharacter player : chr.getMap().getCharactersThreadsafe()) {
                if (player.getParty() == null || (player.getParty().getId() != player.getParty().getId())) {
                    if (skil.targetsAll || Randomizer.nextBoolean()) {
                        found = true;
                        if (dis == null) {
                            player.dispel();
                        } else if (skil.getSkill() == null) {
                            player.giveDebuff(dis, 1, 30000, MapleDisease.getByDisease(dis), 1);
                        } else {
                            player.giveDebuff(dis, skil.getSkill());
                        }
                        if (!skil.targetsAll) {
                            break;
                        }
                    }
                }
            }
            if (found) {
                chr.getCarnivalParty().useCP(chr, skil.cpLoss);
                chr.CPUpdate(false, chr.getAvailableCP(), chr.getTotalCP(), 0);
                for (MapleCharacter player : chr.getMap().getCharactersThreadsafe()) {
                    player.CPUpdate(true, player.getCarnivalParty().getAvailableCP(), player.getCarnivalParty().getTotalCP(), player.getCarnivalParty().getTeam());
                    //chr.dropMessage(5, "[" + (chr.getCarnivalParty().getTeam() == 0 ? "Red" : "Blue") + "] " + chr.getName() + " has used a skill. [" + dis.name() + "].");
                }
                chr.getMap().broadcastMessage(ResCField_MonsterCarnival.playerSummoned(chr.getName(), tab, num));
                chr.UpdateStat(true);
            } else {
                chr.SendPacket(ResWrapper.BroadCastMsgEvent("An error occurred."));
                chr.UpdateStat(true);
            }
        } else if (tab == 2) { //skill
            final MapleCarnivalFactory.MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
            if (skil == null || chr.getAvailableCP() < skil.cpLoss) {
                chr.SendPacket(ResWrapper.BroadCastMsgEvent("You do not have the CP."));
                chr.UpdateStat(true);
                return;
            }
            if (chr.getMap().makeCarnivalReactor(chr.getCarnivalParty().getTeam(), num)) {
                chr.getCarnivalParty().useCP(chr, skil.cpLoss);
                chr.CPUpdate(false, chr.getAvailableCP(), chr.getTotalCP(), 0);
                for (MapleCharacter player : chr.getMap().getCharactersThreadsafe()) {
                    player.CPUpdate(true, player.getCarnivalParty().getAvailableCP(), player.getCarnivalParty().getTotalCP(), player.getCarnivalParty().getTeam());
                }
                chr.getMap().broadcastMessage(ResCField_MonsterCarnival.playerSummoned(chr.getName(), tab, num));
                chr.UpdateStat(true);
            } else {
                chr.SendPacket(ResWrapper.BroadCastMsgEvent("You may no longer summon the being."));
                chr.UpdateStat(true);
            }
        }

    }
}
