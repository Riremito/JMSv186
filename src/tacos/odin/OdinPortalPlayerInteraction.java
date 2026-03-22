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
package tacos.odin;

import odin.client.MapleClient;
import tacos.server.map.TacosPortal;

public class OdinPortalPlayerInteraction extends OdinAbstractPlayerInteraction {

    private TacosPortal portal;

    public OdinPortalPlayerInteraction(MapleClient client, TacosPortal portal) {
        super(client);
        this.portal = portal;
    }

    public TacosPortal getPortal() {
        return this.portal;
    }

    public void playPortalSE() {
        // many scripts use this, do not remove this function.
    }

    @Override
    public void spawnMonster(int id, int qty) {
        // jnr6_act.js
        // rnj6_act.js
        spawnMonster(id, qty, this.portal.getPosition());
    }

}
