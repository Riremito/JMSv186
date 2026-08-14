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
package tacos.task;

import odin.client.MapleCharacter;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import tacos.client.TacosBuff;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class CharacterTask {

    public static void updateBuff(MapleCharacter chr, long time) {
        for (TacosBuff.Buff buff : chr.getBuff().getCTSTimeout(time)) {
            chr.SendPacket(ResCWvsContext.TemporaryStatReset(chr, buff.buff_id));
        }
        chr.getBuff().removeTimeout(time);
    }

    public static boolean update(MapleCharacter chr, long time) {
        if (!chr.updateTime(time, 3000)) {
            return false;
        }
        // skill cool time.
        chr.getCoolTime().update(time);
        // buff.
        updateBuff(chr, time);
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
