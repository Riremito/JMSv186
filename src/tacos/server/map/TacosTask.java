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
package tacos.server.map;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.MapleCoolDownValueHolder;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapItem;
import tacos.client.TacosBuff;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.ResCDropPool.LeaveType;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TacosTask {

    private static final int DROP_ITEM_EXPIRED = 15000;

    public static boolean doTask(MapleClient client) {
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        long time = System.currentTimeMillis();

        doMapTask(chr, map, time);
        doCharacterTask(chr, time);
        return true;
    }

    public static boolean doMapTask(MapleCharacter chr, MapleMap map, long time) {
        if (!map.updateTime(time, 5000)) {
            return false;
        }

        // drop removal.
        for (MapleMapItem mmi : map.getAllItems()) {
            long object_created_time = mmi.getTime();
            if (object_created_time == 0) {
                continue;
            }

            long delta = time - object_created_time;
            if (DROP_ITEM_EXPIRED <= delta) {
                map.removeMapObject(mmi);
                map.broadcastMessage(ResCDropPool.DropLeaveField(mmi, LeaveType.EXPIRED));
            }
        }

        // mob respawn.
        long interval = map.getCreateMobInterval();
        map.updateSpawn();
        return true;
    }

    public static void doCharacterTask_Buff(MapleCharacter chr, long time) {
        for (TacosBuff.Buff buff : chr.getBuff().getCTSTimeout(time)) {
            chr.SendPacket(ResCWvsContext.TemporaryStatReset(chr, buff.buff_id));
        }
        chr.getBuff().removeTimeout(time);
    }

    public static boolean doCharacterTask(MapleCharacter chr, long time) {
        if (!chr.updateTime(time, 3000)) {
            return false;
        }

        // skill cool time.
        for (MapleCoolDownValueHolder cdvh : chr.getCooldowns()) {
            if (cdvh.end_time <= time) {
                int skill_id = cdvh.skill_id;
                chr.removeCooldown(skill_id);
                chr.SendPacket(ResCUserLocal.SkillCooltimeSet(skill_id, 0));
            }
        }

        // buff.
        doCharacterTask_Buff(chr, time);

        // pet.
        for (MaplePet pet : chr.getPets()) {
            if (!pet.getSummoned()) {
                continue;
            }
            if (pet.getPetItemId() == 5000054 && 0 < pet.getSecondsLeft()) {
                pet.setSecondsLeft(pet.getSecondsLeft() - 1);
                if (pet.getSecondsLeft() <= 0) {
                    chr.unequipPet(pet, true, true);
                    continue;
                }
            }
            int newFullness = pet.getFullness() - WzXML.ITEM.getHunger(pet.getPetItemId());
            if (newFullness <= 5) {
                pet.setFullness(15);
                chr.unequipPet(pet, true, true);
                continue;
            }
            pet.setFullness(newFullness);
            chr.SendPacket(ResWrapper.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition())));
        }

        return true;
    }
}
