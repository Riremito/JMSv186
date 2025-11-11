/*
 * Copyright (C) 2025 Riremito
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
package tacos.packet.ops.arg;

import tacos.packet.ops.OpsFieldEffect;
import odin.server.life.MapleMonster;

/**
 *
 * @author Riremito
 */
public class ArgFieldEffect {

    public OpsFieldEffect flag;
    public String wz_path;
    public MapleMonster monster;
    public int type, delay;

    public ArgFieldEffect(OpsFieldEffect flag, String wz_path) {
        this.flag = flag;
        this.wz_path = wz_path;
    }

    public ArgFieldEffect(OpsFieldEffect flag, MapleMonster monster) {
        this.flag = flag;
        this.monster = monster;
    }

    public ArgFieldEffect(OpsFieldEffect flag, int type, int delay) {
        this.flag = flag;
        this.type = type;
        this.delay = delay;
    }
}
