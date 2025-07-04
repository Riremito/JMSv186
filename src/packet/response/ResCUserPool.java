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
package packet.response;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import constants.GameConstants;
import handling.MaplePacket;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.util.ArrayList;
import java.util.List;
import packet.ServerPacket;
import packet.response.struct.AvatarLook;
import packet.response.struct.Structure;
import server.Randomizer;
import tools.Pair;

/**
 *
 * @author Riremito
 */
public class ResCUserPool {

    // CUserPool::OnUserEnterField
    public static MaplePacket UserEnterField(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEnterField);
        sp.Encode4(chr.getId());
        // 自分のキャラクターの場合はここで終了
        if (ServerConfig.JMS164orLater()) {
            sp.Encode1(chr.getLevel());
        }
        sp.EncodeStr(chr.getName());
        if (ServerConfig.JMS194orLater()) {
            sp.EncodeStr("");
        }
        // guild
        MapleGuild gs = null;
        if (0 < chr.getGuildId()) {
            gs = World.Guild.getGuild(chr.getGuildId());
        }
        if (gs != null) {
            // guild info
            sp.EncodeStr(gs.getName());
            sp.Encode2(gs.getLogoBG());
            sp.Encode1(gs.getLogoBGColor());
            sp.Encode2(gs.getLogo());
            sp.Encode1(gs.getLogoColor());
        } else {
            // empty guild
            sp.EncodeStr("");
            sp.Encode2(0);
            sp.Encode1(0);
            sp.Encode2(0);
            sp.Encode1(0);
        }
        List<Pair<Integer, Boolean>> buffvalue = new ArrayList<Pair<Integer, Boolean>>();
        if (ServerConfig.JMS164orLater()) {
            long fbuffmask = 16646144L;
            if (chr.getBuffedValue(MapleBuffStat.SOARING) != null) {
                fbuffmask |= MapleBuffStat.SOARING.getValue();
            }
            if (chr.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) != null) {
                fbuffmask |= MapleBuffStat.MIRROR_IMAGE.getValue();
            }
            if (chr.getBuffedValue(MapleBuffStat.DARK_AURA) != null) {
                fbuffmask |= MapleBuffStat.DARK_AURA.getValue();
            }
            if (chr.getBuffedValue(MapleBuffStat.BLUE_AURA) != null) {
                fbuffmask |= MapleBuffStat.BLUE_AURA.getValue();
            }
            if (chr.getBuffedValue(MapleBuffStat.YELLOW_AURA) != null) {
                fbuffmask |= MapleBuffStat.YELLOW_AURA.getValue();
            }
            sp.Encode8(fbuffmask);
        }
        long buffmask = 0;
        if (chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null && !chr.isHidden()) {
            buffmask |= MapleBuffStat.DARKSIGHT.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.COMBO) != null) {
            buffmask |= MapleBuffStat.COMBO.getValue();
            buffvalue.add(new Pair<Integer, Boolean>(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.COMBO).intValue()), false));
        }
        if (chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) {
            buffmask |= MapleBuffStat.SHADOWPARTNER.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
            buffmask |= MapleBuffStat.SOULARROW.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.DIVINE_BODY) != null) {
            buffmask |= MapleBuffStat.DIVINE_BODY.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.BERSERK_FURY) != null) {
            buffmask |= MapleBuffStat.BERSERK_FURY.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
            buffmask |= MapleBuffStat.MORPH.getValue();
            buffvalue.add(new Pair<Integer, Boolean>(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.MORPH).intValue()), true));
        }
        sp.Encode8(buffmask);
        if (ServerConfig.JMS164orLater()) {
            // buffmask
            if (ServerConfig.JMS194orLater()) {
                sp.Encode4(0);
            }
            for (Pair<Integer, Boolean> i : buffvalue) {
                if (i.right) {
                    sp.Encode2(i.left.shortValue());
                } else {
                    sp.Encode1(i.left.byteValue());
                }
            }
            final int CHAR_MAGIC_SPAWN = Randomizer.nextInt();
            //CHAR_MAGIC_SPAWN is really just tickCount
            //this is here as it explains the 7 "dummy" buffstats which are placed into every character
            //these 7 buffstats are placed because they have irregular packet structure.
            //they ALL have writeShort(0); first, then a long as their variables, then server tick count
            //0x80000, 0x100000, 0x200000, 0x400000, 0x800000, 0x1000000, 0x2000000
            sp.Encode1(0); //start of energy charge
            sp.Encode1(0);
            if (ServerConfig.IsPreBB()) {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode2(0); //start of dash_speed
                sp.Encode8(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode2(0); //start of dash_jump
                sp.Encode8(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode2(0); //start of Monster Riding
                int buffSrc = chr.getBuffSource(MapleBuffStat.MONSTER_RIDING);
                if (buffSrc > 0) {
                    final IItem c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118 /*-122*/);
                    final IItem mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18 /*-22*/);
                    if (GameConstants.getMountItem(buffSrc) == 0 && c_mount != null) {
                        sp.Encode4(c_mount.getItemId());
                    } else if (GameConstants.getMountItem(buffSrc) == 0 && mount != null) {
                        sp.Encode4(mount.getItemId());
                    } else {
                        sp.Encode4(GameConstants.getMountItem(buffSrc));
                    }
                    sp.Encode4(buffSrc);
                } else {
                    sp.Encode8(0);
                }
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode8(0); //speed infusion behaves differently here
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode4(1);
                sp.Encode8(0); //homing beacon
                sp.Encode1(0);
                sp.Encode2(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode4(0); //and finally, something ive no idea
                sp.Encode8(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode2(0);
            }
            sp.Encode2(chr.getJob());
        }
        sp.EncodeBuffer(AvatarLook.Encode(chr));
        sp.Encode4(0); //this is CHARID to follow
        if (ServerConfig.JMS164orLater()) {
            sp.Encode4(0); //probably charid following
            sp.Encode4(0);
            if (ServerConfig.JMS194orLater()) {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
            }
            sp.Encode4(0);
        }
        sp.Encode4(chr.getItemEffect());
        sp.Encode4(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        sp.Encode2(chr.getPosition().x);
        sp.Encode2(chr.getPosition().y);
        sp.Encode1(chr.getStance());
        sp.Encode2(0); // FH
        sp.Encode1(0); // pet size
        sp.Encode4(chr.getMount().getLevel()); // mount lvl
        sp.Encode4(chr.getMount().getExp()); // exp
        sp.Encode4(chr.getMount().getFatigue()); // tiredness
        // MiniRoomBalloon (ゲーム) 1 byte flag + data
        sp.EncodeBuffer(Structure.AnnounceBox(chr));
        // ADBoardBalloon (黒板) 1 byte flag + data
        {
            sp.Encode1(chr.getChalkboard() != null && chr.getChalkboard().length() > 0 ? 1 : 0);
            if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
                sp.EncodeStr(chr.getChalkboard());
            }
        }
        sp.Encode1(0); //count4 -> buf0x10 4
        sp.Encode1(0); //count4 -> buf0x10 4
        // MarriageRecord 1 byte flag + data
        {
            sp.Encode1(0);
        }
        sp.Encode1(chr.getEffectMask()); // Effect
        sp.Encode4(0); // not in KMST, in GMS v95: m_nPhase
        // 特殊マップ専用
        // MonsterCarnival
        if (chr.checkSpecificMap(980000000, 1000) || chr.checkSpecificMap(980030000, 1000)) {
            sp.Encode1((chr.getCarnivalParty() != null) ? chr.getCarnivalParty().getTeam() : 0); // sub_5CD27E
        } // Coconut
        else if (chr.checkSpecificMap(109080000, 1000)) {
            sp.Encode1(chr.getCoconutTeam()); // 0059F0ED
        }
        return sp.get();
    }

    // CUserPool::OnUserLeaveField
    public static MaplePacket UserLeaveField(int player_id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserLeaveField);
        sp.Encode4(player_id);
        return sp.get();
    }

}
