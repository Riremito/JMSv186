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
package tacos.unofficial;

import odin.client.MapleCharacter;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.server.Randomizer;
import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCUserPool;
import tacos.packet.response.ResCUserRemote;

/**
 *
 * @author Riremito
 */
public class PetCharacter implements IPetEx {

    private TacosCharacter character;
    private MapleCharacter clone = null;

    public PetCharacter(TacosCharacter character) {
        this.character = character;
    }

    @Override
    public void SendPacket(ServerPacket packet) {
        this.character.getMap().broadcastMessage(packet);
    }

    @Override
    public boolean spawn(int id) {
        remove();

        MapleCharacter target = this.character.getWorld().findOnlinePlayerById(id);

        if (target == null) {
            return false;
        }

        this.clone = target.cloneCopy();
        this.clone.setPosition(this.character.getPosition());
        this.clone.setFH(this.character.getFH());
        this.clone.setName(String.format("%08X", Randomizer.nextInt(0x77777777)));
        this.clone.setId(Randomizer.nextInt(0x77777777));

        SendPacket(ResCUserPool.UserEnterField(this.clone));
        return true;
    }

    @Override
    public boolean remove() {
        if (this.clone == null) {
            return false;
        }

        SendPacket(ResCUserPool.UserLeaveField(this.clone));
        this.clone = null;
        return true;
    }

    @Override
    public boolean move(ParseCMovePath move_path) {
        if (this.clone == null) {
            return false;
        }

        move_path.update(this.clone);

        SendPacket(ResCUserRemote.UserMove(this.clone, move_path));
        return true;
    }

    public boolean changeEquip() {
        if (this.clone == null) {
            return false;
        }

        this.clone.getInventory(MapleInventoryType.EQUIPPED).resetForClone();
        for (IItem equip : this.character.getInventory(MapleInventoryType.EQUIPPED)) {
            this.clone.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip);
        }

        SendPacket(ResCUserRemote.UserAvatarModified(this.clone, 1));
        return true;
    }
}
