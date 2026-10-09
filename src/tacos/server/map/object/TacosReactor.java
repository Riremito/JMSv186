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

import odin.server.maps.MapleReactorStats;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TacosReactor extends TacosMapObject {

    private int id;
    private int bFlip = 0;
    private String name = "";
    protected MapleReactorStats stats;
    private boolean alive = true;

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
}
