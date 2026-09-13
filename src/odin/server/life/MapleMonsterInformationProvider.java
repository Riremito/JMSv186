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
package odin.server.life;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tacos.database.query.DQ_DropData;

public class MapleMonsterInformationProvider {

    private static MapleMonsterInformationProvider instance = new MapleMonsterInformationProvider();
    private Map<Integer, List<MonsterDropEntry>> drops = new HashMap<>();

    public static MapleMonsterInformationProvider getInstance() {
        return instance;
    }

    public List<MonsterDropEntry> retrieveDrop(final int monsterId) {
        if (drops.containsKey(monsterId)) {
            return drops.get(monsterId);
        }
        final List<MonsterDropEntry> ret = DQ_DropData.getDrops(monsterId);
        drops.put(monsterId, ret);
        return ret;
    }

}
