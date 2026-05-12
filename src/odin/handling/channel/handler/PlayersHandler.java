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
package odin.handling.channel.handler;

import odin.client.inventory.IItem;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import tacos.packet.response.ResCUser;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import odin.server.MapleInventoryManipulator;
import tacos.packet.ClientPacket;

public class PlayersHandler {

    public static void FollowRequest(ClientPacket cp, final MapleClient c) {
        MapleCharacter tt = c.getPlayer().getMap().getCharacterById(cp.Decode4());
        if (cp.Decode1() > 0) {
            //1 when changing map
            tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getFollowId() == c.getPlayer().getId()) {
                tt.setFollowOn(true);
                c.getPlayer().setFollowOn(true);
            } else {
                c.getPlayer().checkFollow();
            }
            return;
        }
        if (cp.Decode1() > 0) { //cancelling follow
            tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getFollowId() == c.getPlayer().getId() && c.getPlayer().isFollowOn()) {
                c.getPlayer().checkFollow();
            }
            return;
        }
        if (tt != null && tt.getPosition().distanceSq(c.getPlayer().getPosition()) < 10000 && tt.getFollowId() == 0 && c.getPlayer().getFollowId() == 0 && tt.getId() != c.getPlayer().getId()) { //estimate, should less
            tt.setFollowId(c.getPlayer().getId());
            tt.setFollowOn(false);
            tt.setFollowInitiator(false);
            c.getPlayer().setFollowOn(false);
            c.getPlayer().setFollowInitiator(false);
            tt.getClient().getSession().write(ResCWvsContext.followRequest(c.getPlayer().getId()));
        } else {
            c.getSession().write(ResWrapper.BroadCastMsgAlert("You are too far away."));
        }
    }

    public static void FollowReply(ClientPacket cp, final MapleClient c) {
        if (c.getPlayer().getFollowId() > 0 && c.getPlayer().getFollowId() == cp.Decode4()) {
            MapleCharacter tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getPosition().distanceSq(c.getPlayer().getPosition()) < 10000 && tt.getFollowId() == 0 && tt.getId() != c.getPlayer().getId()) { //estimate, should less
                boolean accepted = cp.Decode1() > 0;
                if (accepted) {
                    tt.setFollowId(c.getPlayer().getId());
                    tt.setFollowOn(true);
                    tt.setFollowInitiator(true);
                    c.getPlayer().setFollowOn(true);
                    c.getPlayer().setFollowInitiator(false);
                    c.getPlayer().getMap().broadcastMessage(ResCUser.followEffect(tt.getId(), c.getPlayer().getId(), null));
                } else {
                    c.getPlayer().setFollowId(0);
                    tt.setFollowId(0);
                    tt.getClient().getSession().write(ResCUserLocal.getFollowMsg(5));
                }
            } else {
                if (tt != null) {
                    tt.setFollowId(0);
                    c.getPlayer().setFollowId(0);
                }
                c.getSession().write(ResWrapper.BroadCastMsgAlert("You are too far away."));
            }
        } else {
            c.getPlayer().setFollowId(0);
        }
    }

    public static void RingAction(ClientPacket cp, final MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        final byte mode = cp.Decode1();
        if (mode == 0) {
            final String name = cp.DecodeStr();
            final int itemid = cp.Decode4();
            final int newItemId = 1112300 + (itemid - 2240004);
            final MapleCharacter player = c.getChannelServer().getOnlinePlayers().findByName(name);
            int errcode = 0;
            if (c.getPlayer().getMarriageId() > 0) {
                errcode = 0x17;
            } else if (player == null) {
                errcode = 0x12;
            } else if (player.getMapId() != c.getPlayer().getMapId()) {
                errcode = 0x13;
            } else if (!c.getPlayer().haveItem(itemid, 1) || itemid < 2240004 || itemid > 2240015) {
                errcode = 0x0D;
            } else if (player.getMarriageId() > 0 || player.getMarriageItemId() > 0) {
                errcode = 0x18;
            } else if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "")) {
                errcode = 0x14;
            } else if (!MapleInventoryManipulator.checkSpace(player.getClient(), newItemId, 1, "")) {
                errcode = 0x15;
            }
            if (errcode > 0) {
                c.getSession().write(ResCWvsContext.sendEngagement((byte) errcode, 0, null, null));
                chr.updateStat();
                return;
            }
            c.getPlayer().setMarriageItemId(itemid);
            player.getClient().getSession().write(ResCWvsContext.sendEngagementRequest(c.getPlayer().getName(), c.getPlayer().getId()));
            //1112300 + (itemid - 2240004)
        } else if (mode == 1) {
            c.getPlayer().setMarriageItemId(0);
        } else if (mode == 2) { //accept/deny proposal
            final boolean accepted = cp.Decode1() > 0;
            final String name = cp.DecodeStr();
            final int id = cp.Decode4();
            final MapleCharacter player = c.getChannelServer().getOnlinePlayers().findByName(name);
            if (c.getPlayer().getMarriageId() > 0 || player == null || player.getId() != id || player.getMarriageItemId() <= 0 || !player.haveItem(player.getMarriageItemId(), 1) || player.getMarriageId() > 0) {
                c.getSession().write(ResCWvsContext.sendEngagement((byte) 0x1D, 0, null, null));
                chr.updateStat();
                return;
            }
            if (accepted) {
                final int newItemId = 1112300 + (player.getMarriageItemId() - 2240004);
                if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "") || !MapleInventoryManipulator.checkSpace(player.getClient(), newItemId, 1, "")) {
                    c.getSession().write(ResCWvsContext.sendEngagement((byte) 0x15, 0, null, null));
                    chr.updateStat();
                    return;
                }
                MapleInventoryManipulator.addById(c, newItemId, (short) 1);
                MapleInventoryManipulator.removeById(player.getClient(), MapleInventoryType.USE, player.getMarriageItemId(), 1, false, false);
                MapleInventoryManipulator.addById(player.getClient(), newItemId, (short) 1);
                player.getClient().getSession().write(ResCWvsContext.sendEngagement((byte) 0x10, newItemId, player, c.getPlayer()));
                player.setMarriageId(c.getPlayer().getId());
                c.getPlayer().setMarriageId(player.getId());
            } else {
                player.getClient().getSession().write(ResCWvsContext.sendEngagement((byte) 0x1E, 0, null, null));
            }
            chr.updateStat();
            player.setMarriageItemId(0);
        } else if (mode == 3) { //drop, only works for ETC
            final int itemId = cp.Decode4();
            final MapleInventoryType type = GameConstants.getInventoryType(itemId);
            final IItem item = c.getPlayer().getInventory(type).findById(itemId);
            if (item != null && type == MapleInventoryType.ETC && itemId / 10000 == 421) {
                MapleInventoryManipulator.drop(c, type, item.getPosition(), item.getQuantity());
            }
        }
    }
}
