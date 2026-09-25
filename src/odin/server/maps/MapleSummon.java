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
package odin.server.maps;

import odin.client.MapleCharacter;
import tacos.client.TacosCharacter;
import tacos.packet.ops.OpsSkill;
import tacos.server.map.object.TacosSummon;

public class MapleSummon extends TacosSummon {

    public MapleSummon(TacosCharacter player, OpsSkill ops_skill, int nSLV) {
        super(player, ops_skill, nSLV);
    }

    public MapleCharacter getOwner() {
        return null;
    }

    public boolean isGaviota() {
        return getSkillID() == 5211002;
    }

    public boolean isBeholder() {
        return getSkillID() == 1321007;
    }
}
