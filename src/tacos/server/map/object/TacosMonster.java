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

import tacos.packet.ops.OpsMobAppear;

/**
 *
 * @author Riremito
 */
public class TacosMonster extends TacosMapObject {

    private int id;
    private int f;
    private int cy;
    private int rx0;
    private int rx1;
    private int nHomeFoothold = 0;
    private int nAppearType = -1;
    private OpsMobAppear appear_type = OpsMobAppear.MOBAPPEAR_NORMAL;
    private int dwSummonOption = 0;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getF() {
        return this.f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getCy() {
        return this.cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    public int getRx0() {
        return this.rx0;
    }

    public void setRx0(int rx0) {
        this.rx0 = rx0;
    }

    public int getRx1() {
        return this.rx1;
    }

    public void setRx1(int rx1) {
        this.rx1 = rx1;
    }

    public int getHomeFoothold() {
        return this.nHomeFoothold;
    }

    public void setHomeFoothold(int nHomeFoothold) {
        this.nHomeFoothold = nHomeFoothold;
    }

    public OpsMobAppear getAT() {
        return this.appear_type;
    }

    public void setAT(OpsMobAppear appear_type) {
        this.appear_type = appear_type;
    }

    public int getATEx() {
        return this.nAppearType;
    }

    public void setATEx(int nAppearType) {
        this.nAppearType = nAppearType;
    }

    public int getSummonOption() {
        return this.dwSummonOption;
    }

    public void setSummonOption(int dwSummonOption) {
        this.dwSummonOption = dwSummonOption;
    }
}
