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
package odin.client.inventory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.Serializable;
import tacos.database.query.DQ_Inventoryitems;
import tacos.database.query.DQ_Pets;
import tacos.database.query.DQ_Rings;

public class MapleInventoryIdentifier implements Serializable {

    private static final long serialVersionUID = 21830921831301L;
    private AtomicInteger runningUID;
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private Lock readLock = rwl.readLock(), writeLock = rwl.writeLock();
    private static MapleInventoryIdentifier instance = new MapleInventoryIdentifier();

    public MapleInventoryIdentifier() {
        this.runningUID = new AtomicInteger(0);
        getNextUniqueId();
    }

    public static int getInstance() {
        return instance.getNextUniqueId();
    }

    public int getNextUniqueId() {
        if (grabRunningUID() <= 0) {
            setRunningUID(initUID());
        }
        incrementRunningUID();
        return grabRunningUID();
    }

    public int grabRunningUID() {
        readLock.lock();
        try {
            return runningUID.get(); //maybe we should really synchronize/do more here..
        } finally {
            readLock.unlock();
        }
    }

    public void incrementRunningUID() {
        setRunningUID(grabRunningUID() + 1);
    }

    public void setRunningUID(int rUID) {
        if (rUID < grabRunningUID()) { //dont go backwards.
            return;
        }
        writeLock.lock();
        try {
            runningUID.set(rUID); //maybe we should really synchronize/do more here..
        } finally {
            writeLock.unlock();
        }
    }

    public int initUID() {
        int ret = 0;
        if (grabRunningUID() > 0) {
            return grabRunningUID();
        }

        int[] ids = new int[4];
        ids[0] = DQ_Inventoryitems.getMaxUniqueId();
        ids[1] = DQ_Pets.getMaxPetId();
        ids[2] = DQ_Rings.getMaxRingId();
        ids[3] = DQ_Rings.getMaxPartnerRingId(); //biggest pl0x. but if this happens -> o_O

        for (int i = 0; i < 4; i++) {
            if (ids[i] > ret) {
                ret = ids[i];
            }
        }
        return ret;
    }
}
