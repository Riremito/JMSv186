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

import odin.server.life.MapleNPC;
import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCNpcPool;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class PetNPC implements IPetEx {

    private TacosCharacter character;
    private MapleNPC npc = null;

    public PetNPC(TacosCharacter character) {
        this.character = character;
    }

    @Override
    public void SendPacket(ServerPacket packet) {
        this.character.getMap().broadcastMessage(packet);
    }

    @Override
    public boolean spawn(int npc_id) {
        remove();

        if (!WzDataStorage.NPC.check(npc_id)) {
            return false;
        }

        this.npc = new MapleNPC(npc_id);
        this.npc.setCy(this.character.getPosition().y);
        this.npc.setRx0(this.character.getPosition().x + 50);
        this.npc.setRx1(this.character.getPosition().x - 50);
        this.npc.setPosition(this.character.getPosition());
        this.npc.setFootholdId(this.character.getFH());
        this.character.getMap().addNPC(this.npc);
        SendPacket(ResCNpcPool.NpcEnterField(this.npc));
        return true;
    }

    @Override
    public boolean remove() {
        if (this.npc == null) {
            return false;
        }

        this.character.getMap().removeNPC(this.npc.getObjectId());
        SendPacket(ResCNpcPool.NpcLeaveField(this.npc));
        this.npc = null;
        return true;
    }

    @Override
    public boolean move(ParseCMovePath move_path) {
        if (this.npc == null) {
            return false;
        }

        this.npc.update(move_path);

        SendPacket(ResCNpcPool.NpcMove(this.npc, -1, -1, move_path));
        return true;
    }
}
