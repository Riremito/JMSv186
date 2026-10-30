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

import java.awt.Point;
import tacos.server.map.TacosReward;
import tacos.server.map.object.TacosReactor;
import tacos.client.TacosClient;
import odin.server.Randomizer;

public class OdinReactorActionManager extends OdinAbstractPlayerInteraction {

    private TacosReactor reactor;

    public OdinReactorActionManager(TacosClient client, TacosReactor reactor) {
        super(client);
        this.reactor = reactor;
    }

    public void spawnZakum() {
        this.reactor.getMap().spawnZakum(this.reactor);
    }

    // only used for meso = false, really. No minItems because meso is used to fill the gap
    public void dropItems() {
        dropItems(false, 0, 0, 0, 0);
    }

    public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
        dropItems(meso, mesoChance, minMeso, maxMeso, 0);
    }

    public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        // Reward.img has its own meso and item drops, the arguments are used only when the reactor does not have the data.
        if (0 <= TacosReward.getReactorReward(getPlayer(), reactor)) {
            return;
        }
        int meso_count = 0;
        if (meso && Math.random() < (1 / (double) mesoChance)) {
            meso_count = 1;
        }
        // if a minimum number of drops is required, add meso
        meso_count = Math.max(meso_count, minItems);

        Point dropPos = reactor.getPosition();
        dropPos.x -= (12 * meso_count);
        for (int i = 0; i < meso_count; i++) {
            int range = maxMeso - minMeso;
            int mesoDrop = ((0 < range) ? Randomizer.nextInt(range) : 0) + minMeso * getClient().getPlayer().getChannelServer().getMesoRate();
            reactor.getMap().spawnMesoDrop(mesoDrop, dropPos, reactor.getPosition(), getPlayer(), false, (byte) 0);
            dropPos.x += 25;
        }
    }

    @Override
    public void spawnNpc(int npcId) {
        spawnNpc(npcId, getPosition());
    }

    // returns slightly above the reactor's position for monster spawns
    public Point getPosition() {
        Point pos = reactor.getPosition();
        pos.y -= 10;
        return pos;
    }

    public TacosReactor getReactor() {
        return this.reactor;
    }

    public void killMonster(int mob_id) {
        reactor.getMap().killMonster(mob_id);
    }

    // summon one monster on reactor location
    @Override
    public void spawnMonster(int id) {
        spawnMonster(id, 1, getPosition());
    }

    // summon monsters on reactor location
    @Override
    public void spawnMonster(int id, int qty) {
        spawnMonster(id, qty, getPosition());
    }

    public boolean dispelAllMonsters(int num) {
        return true;
    }
}
