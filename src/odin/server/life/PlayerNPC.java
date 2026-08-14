/*
This file is part of the ZeroFusion MapleStory Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>
ZeroFusion organized by "RMZero213" <RMZero213@hotmail.com>

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
package odin.server.life;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.packet.response.ResCNpcPool;

public class PlayerNPC extends MapleNPC {

    private MapleCharacter player = null;

    public PlayerNPC(int npc_id, MapleCharacter player) {
        super(npc_id, player.getName());
        this.player = player;
    }

    public MapleCharacter getCharacter() {
        return this.player;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.SendPacket(ResCNpcPool.NpcEnterField(this, true));
        client.SendPacket(ResCNpcPool.ImitatedNPCData(this));
        client.SendPacket(ResCNpcPool.NpcChangeController(this, false, true));
    }

}
