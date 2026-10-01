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

import java.awt.Point;
import java.awt.Rectangle;
import tacos.client.TacosClient;
import tacos.packet.response.ResCReactorPool;
import odin.server.Timer.MapTimer;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.script.TacosScriptReactor;
import tacos.server.map.object.TacosReactor;

public class MapleReactor extends TacosReactor {

    private byte state;
    private MapleMap map;
    private boolean timerActive;

    public MapleReactor(int rid) {
        super(rid);
    }

    public void setTimerActive(boolean active) {
        this.timerActive = active;
    }

    public boolean isTimerActive() {
        return timerActive;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public int getReactorType() {
        return stats.getType(state);
    }

    public SimpleImmutableEntry<Integer, Integer> getReactItem() {
        return stats.getReactItem(state);
    }

    //hitReactor command for item-triggered reactors
    public void hitReactor(int dwHitOption, short stance, TacosClient client) {
        if (stats.getType(state) < 999 && stats.getType(state) != -1) {
            //type 2 = only hit from right (kerning swamp plants), 00 is air left 02 is ground left
            final byte oldState = state;
            if (!(stats.getType(state) == 2 && (dwHitOption == 0 || dwHitOption == 2))) { // next state
                state = stats.getNextState(state);

                if (stats.getNextState(state) == -1 || stats.getType(state) == 999) { //end of reactor
                    if ((stats.getType(state) < 100 || stats.getType(state) == 999)) { //reactor broken
                        map.destroyReactor(getObjectId());
                    } else { //item-triggered on final step
                        map.broadcastMessage(ResCReactorPool.ReactorChangeState(this, stance));
                    }
                    TacosScriptReactor.getInstance().act(client, this);
                } else { //reactor not broken yet
                    boolean done = false;
                    map.broadcastMessage(ResCReactorPool.ReactorChangeState(this, stance)); //magatia is weird cause full beaker can be activated by gm hat o.o
                    if (state == stats.getNextState(state) || getId() == 2618000 || getId() == 2309000) { //current state = next state, looping reactor
                        TacosScriptReactor.getInstance().act(client, this);
                        done = true;
                    }
                    if (stats.getTimeOut(state) > 0) {
                        if (!done) {
                            TacosScriptReactor.getInstance().act(client, this);
                        }
                        scheduleSetState(state, oldState, stats.getTimeOut(state));
                    }
                }
            }
        }
    }

    public Rectangle getArea() {
        int height = stats.getBR().y - stats.getTL().y;
        int width = stats.getBR().x - stats.getTL().x;
        int origX = getPosition().x + stats.getTL().x;
        int origY = getPosition().y + stats.getTL().y;

        return new Rectangle(origX, origY, width, height);
    }

    public Point getMobSpawnPoint() {
        Point pos = new Point(getPosition()); // reactor coordinate of map data.
        int height = stats.getBR().y - stats.getTL().y;
        pos.y -= height; // zakum correct spawn coordinate.
        return pos;
    }

    public void scheduleSetState(byte oldState, byte newState, long delay) {
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (MapleReactor.this.state == oldState) {
                    forceHitReactor(newState);
                }
            }
        }, delay);
    }

    // used by script
    public byte getState() {
        return state;
    }

    // used by script
    public void setMap(MapleMap map) {
        this.map = map;
    }

    // used by script
    public MapleMap getMap() {
        return map;
    }

    // used by script
    public void forceStartReactor(TacosClient client) {
        TacosScriptReactor.getInstance().act(client, this);
    }

    // used by script
    public void forceHitReactor(byte newState) {
        setState((byte) newState);
        setTimerActive(false);
        map.broadcastMessage(ResCReactorPool.ReactorChangeState(this, (short) 0));
    }

    // used by script
    public void hitReactor(TacosClient client) {
        hitReactor(0, (short) 0, client);
    }

    // used by script
    public void forceTrigger() {
        map.broadcastMessage(ResCReactorPool.ReactorChangeState(this, (short) 0));
    }

    // used by script
    public void delayedDestroyReactor(long delay) {
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                map.destroyReactor(getObjectId());
            }
        }, delay);
    }

    // used by script
    public void delayedHitReactor(final TacosClient client, long delay) {
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                hitReactor(client);
            }
        }, delay);
    }

}
