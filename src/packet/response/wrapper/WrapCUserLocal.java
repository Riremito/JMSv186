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

import handling.channel.handler.ItemMakerHandler;
import server.network.MaplePacket;
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

    public static MaplePacket EffectLocal(OpsUserEffect ops, int skill_id) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        arg.skill_id = skill_id;
        return ResCUserLocal.EffectLocal(arg);
    }

    public static MaplePacket EffectLocal(OpsUserEffect ops, int skill_id, boolean skill_on) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        arg.skill_id = skill_id;
        arg.skill_on = skill_on;
        return ResCUserLocal.EffectLocal(arg);
    }

    public static final MaplePacket EffectLocal(OpsUserEffect ops, ItemMakerHandler.ItemMakerResult imr) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        arg.imr = imr;
        return ResCUserLocal.EffectLocal(arg);
    }

    public static MaplePacket getShowItemGain(int itemId, short quantity, boolean inChat) {
        // ?_?
        if (inChat) {
            // maybe wrong packet header
            ArgUserEffect arg = new ArgUserEffect();
            arg.ops = OpsUserEffect.UserEffect_Quest;
            arg.item_id = itemId;
            arg.item_quantity = quantity;
            return ResCUserLocal.EffectLocal(arg);
        }
        // SHOW_STATUS_INFO -> LP_Message
        return ResWrapper.DropPickUpMessage(itemId, quantity);
    }
}
