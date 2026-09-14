/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.handling.world;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import odin.client.MapleCharacter;
import odin.handling.world.family.MapleFamily;
import odin.handling.world.family.MapleFamilyCharacter;
import tacos.packet.ServerPacket;
import tacos.server.TacosWorld;

public class Family {


    private static final Map<Integer, MapleFamily> families = new LinkedHashMap<>();
    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    static {
        //System.out.println("[MapleFamily] Loading Families");
        Collection<MapleFamily> allGuilds = MapleFamily.loadAll();
        for (MapleFamily g : allGuilds) {
            if (g.isProper()) {
                families.put(g.getId(), g);
            }
        }
    }

    public static MapleFamily getFamily(int id) {
        MapleFamily ret = null;
        lock.readLock().lock();
        try {
            ret = families.get(id);
        } finally {
            lock.readLock().unlock();
        }
        if (ret == null) {
            lock.writeLock().lock();
            try {
                ret = new MapleFamily(id);
                if (ret == null || ret.getId() <= 0 || !ret.isProper()) { //failed to load
                    return null;
                }
                families.put(id, ret);
            } finally {
                lock.writeLock().unlock();
            }
        }
        return ret;
    }

    public static void memberFamilyUpdate(MapleFamilyCharacter mfc, MapleCharacter mc) {
        MapleFamily f = getFamily(mfc.getFamilyId());
        if (f != null) {
            f.memberLevelJobUpdate(mc);
        }
    }

    public static void setFamilyMemberOnline(MapleFamilyCharacter mfc, boolean bOnline, int channel) {
        MapleFamily f = getFamily(mfc.getFamilyId());
        if (f != null) {
            f.setOnline(mfc.getId(), bOnline, channel);
        }
    }

    public static int setRep(int fid, int cid, int addrep, int oldLevel) {
        MapleFamily f = getFamily(fid);
        if (f != null) {
            return f.setRep(cid, addrep, oldLevel);
        }
        return 0;
    }

    public static void save() {
        System.out.println("Saving families...");
        lock.writeLock().lock();
        try {
            for (MapleFamily a : families.values()) {
                a.writeToDB(false);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void setFamily(int familyid, int seniorid, int junior1, int junior2, int currentrep, int totalrep, int cid) {
        MapleCharacter mc = TacosWorld.find(0).findOnlinePlayerById(cid, false);
        if (mc == null) {
            return;
        }
        boolean bDifferent = mc.getFamilyId() != familyid || mc.getSeniorId() != seniorid || mc.getJunior1() != junior1 || mc.getJunior2() != junior2;
        mc.setFamily(familyid, seniorid, junior1, junior2);
        mc.setCurrentRep(currentrep);
        mc.setTotalRep(totalrep);
        if (bDifferent) {
            mc.saveFamilyStatus();
        }
    }

    public static void familyPacket(int gid, ServerPacket message, int cid) {
        MapleFamily f = getFamily(gid);
        if (f != null) {
            f.broadcast(message, -1, f.getMFC(cid).getPedigree());
        }
    }

    public static void disbandFamily(int gid) {
        MapleFamily g = getFamily(gid);
        lock.writeLock().lock();
        try {
            if (g != null) {
                g.disbandFamily();
                families.remove(gid);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void sendFamilyPacket(int targetIds, ServerPacket packet, int exception, int guildid) {
        if (targetIds == exception) {
            return;
        }
        final MapleCharacter player = TacosWorld.find(0).findOnlinePlayerById(targetIds, false);
        if (player != null && player.getFamilyId() == guildid) {
            player.SendPacket(packet);
        }
    }
}
