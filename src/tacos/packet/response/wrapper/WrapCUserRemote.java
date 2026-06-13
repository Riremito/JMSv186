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
package tacos.packet.response.wrapper;

import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.ops.arg.ArgUserEffect;
import tacos.packet.response.ResCUserRemote;

/**
 *
 * @author Riremito
 */
public class WrapCUserRemote {

    public static ServerPacket EffectRemote(OpsUserEffect ops, TacosCharacter chr) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        arg.chr = chr;
        return ResCUserRemote.UserEffectRemote(arg);
    }

    public static ServerPacket EffectRemote(OpsUserEffect ops, TacosCharacter chr, int skill_id) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        arg.chr = chr;
        arg.skill_id = skill_id;
        return ResCUserRemote.UserEffectRemote(arg);
    }

    public static ServerPacket EffectRemote(OpsUserEffect ops, TacosCharacter chr, int skill_id, boolean skill_on) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        arg.chr = chr;
        arg.skill_id = skill_id;
        arg.skill_on = skill_on;
        return ResCUserRemote.UserEffectRemote(arg);
    }
}
