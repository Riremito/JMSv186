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
package packet.response.wrapper;

import handling.MaplePacket;
import packet.ops.OpsUserEffect;
import packet.ops.arg.ArgUserEffect;
import packet.response.ResCUserLocal;

/**
 *
 * @author Riremito
 */
public class WrapCUserLocal {

    public static MaplePacket EffectLocal(OpsUserEffect ops) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        return ResCUserLocal.EffectLocal(arg);
    }

}
