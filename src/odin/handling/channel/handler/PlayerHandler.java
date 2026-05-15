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

import java.awt.Point;
import odin.client.inventory.IItem;
import odin.client.ISkill;
import odin.client.SkillFactory;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleBuffStat;
import odin.client.MapleClient;
import odin.client.MapleCharacter;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import odin.server.MapleStatEffect;
import odin.server.maps.FieldLimitType;

public class PlayerHandler {

    public static void UseItemEffect(final int itemId, final MapleClient c, final MapleCharacter chr) {
        final IItem toUse = chr.getInventory(MapleInventoryType.CASH).findById(itemId);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            chr.updateInv();
            return;
        }
        if (itemId != 5510000) {
            chr.setItemEffect(itemId);
        }
        chr.getMap().broadcastMessage(chr, ResCUserRemote.UserSetActiveEffectItem(chr, itemId), false);
    }

    public static void SkillEffect(MapleCharacter chr, int skill_id, byte skill_level, short action, byte m_nPrepareSkillActionSpeed) {

        final ISkill skill = SkillFactory.getSkill(skill_id);
        if (chr == null) {
            return;
        }
        final int skilllevel_serv = chr.getSkillLevel(skill);

        if (skilllevel_serv > 0 && skilllevel_serv == skill_level && skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            chr.getMap().broadcastMessage(chr, ResCUserRemote.UserSkillPrepare(chr, skill_id, skill_level, action, m_nPrepareSkillActionSpeed), false);
        }

        // クローン : 暴風とか
        if (chr.isCloning()) {
            MapleCharacter chr_clone = chr.getClone();
            chr.getMap().broadcastMessageClone(chr_clone, ResCUserRemote.UserSkillPrepare(chr_clone, skill_id, skill_level, action, m_nPrepareSkillActionSpeed));
        }
    }

    public static void SpecialMove(MapleCharacter chr, ClientPacket cp, int skillid, int skillLevel, Point pos) {
        final ISkill skill = SkillFactory.getSkill(skillid);
        final MapleStatEffect effect = skill.getEffect(chr.getSkillLevel(GameConstants.getLinkedAranSkill(skillid)));

        if (effect.getCooldown() > 0) {
            if (chr.skillisCooling(skillid)) {
                chr.sendStatChanged(true);
                return;
            }
            if (skillid != 5221006) { // Battleship
                chr.SendPacket(ResCUserLocal.SkillCooltimeSet(skillid, effect.getCooldown()));
                chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }

        switch (skillid) {
            case 1121001:
            case 1221001:
            case 1321001:
            case 9001020: // GM magnet
                /*
                final byte number_of_mobs = slea.readByte();
                slea.skip(3);
                for (int i = 0; i < number_of_mobs; i++) {
                    int mobId = slea.readInt();

                    final MapleMonster mob = chr.getMap().getMonsterByOid(mobId);
                    if (mob != null) {
//			chr.getMap().broadcastMessage(chr, MaplePacketCreator.showMagnet(mobId, slea.readByte()), chr.getPosition());
                        mob.switchController(chr, mob.isControllerHasAggro());
                    }
                }
                chr.getMap().broadcastMessage(chr, ResCUserRemote.showBuffeffect(chr.getId(), skillid, 1, slea.readByte()), chr.getPosition());
                c.getSession().write(MaplePacketCreator.enableActions());
                 */
                chr.sendStatChanged(true);
                break;
            default:
                if (pos == null) {
                    pos = chr.getPosition();
                }

                if (effect.isMagicDoor()) { // Mystic Door
                    if (!FieldLimitType.MysticDoor.check(chr.getMap().getFieldLimit())) {
                        effect.applyTo(chr, pos);
                    } else {
                        chr.sendStatChanged(true);
                    }
                } else {
                    final int mountid = MapleStatEffect.parseMountInfo(chr, skill.getId());
                    if (mountid != 0 && mountid != GameConstants.getMountItem(skill.getId()) && chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) == null && chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118/*-122*/) == null) {
                        if (!GameConstants.isMountItemAvailable(mountid, chr.getJob())) {
                            chr.sendStatChanged(true);
                            return;
                        }
                    }
                    effect.applyTo(chr, pos);
                }
                break;
        }
    }
}
