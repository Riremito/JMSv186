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

import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import tacos.client.TacosCharacter;
import tacos.network.MaplePacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCMobPool;
import tacos.wz.ids.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class PetMob implements IPetEx {

    private TacosCharacter character;
    private MapleMonster monster = null;

    public PetMob(TacosCharacter character) {
        this.character = character;
    }

    @Override
    public void SendPacket(MaplePacket packet) {
        this.character.getMap().broadcastMessage(packet);
    }

    @Override
    public boolean spawn(int id) {
        remove();

        if (!WzDataStorage.MOB.check(id)) {
            return false;
        }

        this.monster = MapleLifeFactory.getMonster(id);
        this.monster.setPosition(this.character.getPosition());
        this.monster.setFH(this.character.getFH());
        this.monster.setOriginFh(this.character.getFH());

        SendPacket(ResCMobPool.MobEnterField(this.monster, -2, 0, 0));
        return true;
    }

    @Override
    public boolean remove() {
        if (this.monster == null) {
            return false;
        }

        SendPacket(ResCMobPool.MobLeaveField(this.monster, 0));
        this.monster = null;
        return true;
    }

    @Override
    public boolean move(ParseCMovePath move_path) {
        if (this.monster == null) {
            return false;
        }

        boolean is_left = (move_path.getMoveAction() & 1) != 0;
        move_path.update(this.monster);
        this.monster.setOriginFh(move_path.getFootHoldId());

        SendPacket(ResCMobPool.MobMove(this.monster, false, is_left ? 1 : 0, 0, move_path));
        return true;
    }
}
