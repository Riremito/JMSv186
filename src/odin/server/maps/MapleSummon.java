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
package odin.server.maps;

import java.awt.Point;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import tacos.packet.response.ResCSummonedPool;
import odin.server.MapleStatEffect;
import tacos.packet.ops.OpsAssist;
import tacos.packet.ops.OpsMoveAbility;

public class MapleSummon extends AbstractAnimatedMapleMapObject {

    private final int ownerid;
    private final int skillLevel;
    private final int ownerLevel;
    private final int skill;
    private int fh;
    private MapleMap map; //required for instanceMaps
    private short hp;
    private boolean changedMap = false;
    private OpsMoveAbility movementType;

    public MapleSummon(MapleCharacter owner, MapleStatEffect skill, Point pos, OpsMoveAbility movementType) {
        super();
        this.ownerid = owner.getId();
        this.ownerLevel = owner.getLevel();
        this.skill = skill.getSourceId();
        this.map = owner.getMap();
        this.skillLevel = skill.getLevel();
        this.movementType = movementType;
        setPosition(pos);
        try {
            this.fh = owner.getMap().getFootholds().findBelow(pos).getId();
        } catch (NullPointerException e) {
            this.fh = 0; //lol, it can be fixed by movement
        }
    }

    @Override
    public final void sendSpawnData(final TacosClient client) {
    }

    @Override
    public final void sendDestroyData(final TacosClient client) {
        client.SendPacket(ResCSummonedPool.SummonedLeaveField(this, false));
    }

    public final void updateMap(final MapleMap map) {
        this.map = map;
    }

    public final MapleCharacter getOwner() {
        return map.getCharacterById(ownerid);
    }

    public final int getFh() {
        return fh;
    }

    public final void setFh(final int fh) {
        this.fh = fh;
    }

    public final int getOwnerId() {
        return ownerid;
    }

    public final int getOwnerLevel() {
        return ownerLevel;
    }

    public final int getSkill() {
        return skill;
    }

    public final short getHP() {
        return hp;
    }

    public final void addHP(final short delta) {
        this.hp += delta;
    }

    public OpsMoveAbility getMovementType() {
        return movementType;
    }

    public final boolean isPuppet() {
        switch (skill) {
            case 3111002:
            case 3211002:
            case 13111004:
            case 4341006:
            case 33111003:
                return true;
        }
        return false;
    }

    public final boolean isGaviota() {
        return skill == 5211002;
    }

    public final boolean isBeholder() {
        return skill == 1321007;
    }

    public final int getSkillLevel() {
        return skillLevel;
    }

    public OpsAssist getSummonType() {
        if (isPuppet()) {
            return OpsAssist.ASSIST_NONE;
        }
        switch (skill) {
            case 1321007: {
                return OpsAssist.ASSIST_HEAL;
            }
            case 35111001: //satellite.
            case 35111009:
            case 35111010: {
                return OpsAssist.ASSIST_ATTACK_EX;
            }
            case 35121009: //bots n. tots
            {
                return OpsAssist.ASSIST_SUMMON;
            }
            case 4111007: {
                return OpsAssist.ASSIST_ATTACK_COUNTER;
            }
            default: {
                break;
            }
        }
        return OpsAssist.ASSIST_ATTACK;
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }

    public final boolean isChangedMap() {
        return changedMap;
    }

    public final void setChangedMap(boolean cm) {
        this.changedMap = cm;
    }
}
