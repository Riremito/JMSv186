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
package tacos.server.map.object;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.AbstractMap.SimpleImmutableEntry;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleReactorStats;
import tacos.client.TacosClient;
import tacos.packet.response.ResCReactorPool;
import tacos.script.TacosScriptReactor;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TacosReactor extends TacosMapObject {

    private int id;
    private int bFlip = 0;
    private String name = "";
    private MapleReactorStats stats;
    private boolean alive = true;
    private byte state;
    private MapleMap map;
    private boolean timerActive;

    public TacosReactor(int id) {
        this.id = id;
        this.stats = WzXML.REACTOR.getReactor(id);
    }

    public int getId() {
        return this.id;
    }

    public int getFacingDirection() {
        return this.bFlip;
    }

    public void setFacingDirection(int bFlip) {
        this.bFlip = bFlip;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MapleReactorStats getStats() {
        return this.stats;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
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
    public void hitReactor(int dwHitOption, short tActionDelay, TacosClient client) {
        if (stats.getType(state) < 999 && stats.getType(state) != -1) {
            //type 2 = only hit from right (kerning swamp plants), 00 is air left 02 is ground left
            byte oldState = state;
            if (!(stats.getType(state) == 2 && (dwHitOption == 0 || dwHitOption == 2))) { // next state
                state = stats.getNextState(state);

                if (stats.getNextState(state) == -1 || stats.getType(state) == 999) { //end of reactor
                    if ((stats.getType(state) < 100 || stats.getType(state) == 999)) { //reactor broken
                        map.removeReactor(this);
                    } else { //item-triggered on final step
                        map.broadcastPacket(ResCReactorPool.ReactorChangeState(this, tActionDelay));
                    }
                    TacosScriptReactor.getInstance().act(client, this);
                } else { //reactor not broken yet
                    boolean done = false;
                    map.broadcastPacket(ResCReactorPool.ReactorChangeState(this, tActionDelay)); //magatia is weird cause full beaker can be activated by gm hat o.o
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
        if (this.state == oldState) {
            forceHitReactor(newState);
        }
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
        return this.map;
    }

    // used by script
    public void forceStartReactor(TacosClient client) {
        TacosScriptReactor.getInstance().act(client, this);
    }

    // used by script
    public void forceHitReactor(byte newState) {
        setState((byte) newState);
        setTimerActive(false);
        map.broadcastPacket(ResCReactorPool.ReactorChangeState(this, (short) 0));
    }

    // used by script
    public void hitReactor(TacosClient client) {
        hitReactor(0, (short) 0, client);
    }

    // used by script
    public void forceTrigger() {
        this.map.broadcastPacket(ResCReactorPool.ReactorChangeState(this, (short) 0));
    }

    // used by script
    public void delayedDestroyReactor(long delay) {
        this.map.removeReactor(this);
    }

    // used by script
    public void delayedHitReactor(final TacosClient client, long delay) {
        hitReactor(client);
    }
}
