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

import odin.handling.channel.handler.ItemMakerHandler;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.ops.arg.ArgUserEffect;
import tacos.packet.response.ResCUserLocal;

/**
 *
 * @author Riremito
 */
public class WrapCUserLocal {

    public static ServerPacket EffectLocal(OpsUserEffect ops) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        return ResCUserLocal.UserEffectLocal(arg);
    }

    public static ServerPacket EffectLocal(OpsUserEffect ops, int skill_id) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        arg.skill_id = skill_id;
        return ResCUserLocal.UserEffectLocal(arg);
    }

    public static ServerPacket EffectLocal(OpsUserEffect ops, int skill_id, boolean skill_on) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        arg.skill_id = skill_id;
        arg.skill_on = skill_on;
        return ResCUserLocal.UserEffectLocal(arg);
    }

    public static ServerPacket EffectLocal(OpsUserEffect ops, ItemMakerHandler.ItemMakerResult imr) {
        ArgUserEffect arg = new ArgUserEffect();
        arg.ops = ops;
        arg.imr = imr;
        return ResCUserLocal.UserEffectLocal(arg);
    }

    public static ServerPacket getShowItemGain(int itemId, short quantity, boolean inChat) {
        // ?_?
        if (inChat) {
            // maybe wrong packet header
            ArgUserEffect arg = new ArgUserEffect();
            arg.ops = OpsUserEffect.UserEffect_Quest;
            arg.item_id = itemId;
            arg.item_quantity = quantity;
            return ResCUserLocal.UserEffectLocal(arg);
        }
        // SHOW_STATUS_INFO -> LP_Message
        return ResWrapper.DropPickUpMessage(itemId, quantity);
    }
}
