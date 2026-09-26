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

import odin.client.MapleCharacter;
import tacos.client.TacosCharacter;
import tacos.client.TacosSummonSkill;
import tacos.packet.ops.OpsAssist;
import tacos.packet.ops.OpsMoveAbility;

/**
 *
 * @author Riremito
 */
public class TacosSummon extends TacosMapObject {

    private int nCharLevel;
    private int nSkillID;
    private int nSLV;
    private int summon_time;
    private int summon_hp = 1;

    public TacosSummon(TacosCharacter player, TacosSummonSkill tss) {
        this.nCharLevel = player.getLevel();
        this.nSkillID = tss.getId();
        this.nSLV = tss.getLevel();
        this.summon_time = 5000;//tss.getTime() * 1000;
        this.summon_hp = tss.getX();
        setOwnerId(player.getId());
        setFootholdId(player.getFH());
        setPosition(player.getPosition());
    }

    public int getSkillID() {
        return nSkillID;
    }

    public int getOwnerLevel() {
        return nCharLevel;
    }

    public int getSLV() {
        return nSLV;
    }

    public int getTime() {
        return this.summon_time;
    }

    public int getHp() {
        return this.summon_hp;
    }

    public void setHp(int hp) {
        this.summon_hp = hp;
    }

    public OpsMoveAbility getMoveAbility() {
        switch (getSkillID()) {
            case 3211002: // puppet sniper
            case 3111002: // puppet ranger
            case 33111003:
            case 13111004: // puppet cygnus
            case 5211001: // octopus - pirate
            case 5220002: // advanced octopus - pirate
            case 4341006:
            case 35111002:
            case 35111005: //TEMP
            case 35111004: //TEMP
            //case 35111011: //TEMP
            case 35121009:
            //case 35121010: //TEMP
            case 35121011:
                //case 4111007: //TEMP
                return OpsMoveAbility.MOVEABILITY_STOP;
            case 3211005: // golden eagle
            case 3111005: // golden hawk
            case 33111005:
            case 2311006: // summon dragon
            case 3221005: // frostprey
            case 3121006: // phoenix
                return OpsMoveAbility.MOVEABILITY_FLY;
            case 5211002: // bird - pirate
                return OpsMoveAbility.MOVEABILITY_FLY_RANDOM;
            case 32111006: //reaper
                return OpsMoveAbility.MOVEABILITY_WALK_RANDOM;
            case 1321007: // beholder
            case 2121005: // elquines
            case 2221005: // ifrit
            case 2321003: // bahamut
            case 12111004: // Ifrit
            case 11001004: // soul
            case 12001004: // flame
            case 13001004: // storm
            case 14001005: // darkness
            case 15001004: // lightning
            case 35111001:
            case 35111010:
            case 35111009:
                return OpsMoveAbility.MOVEABILITY_WALK;
        }
        return OpsMoveAbility.UNKNOWN;
    }

    public OpsAssist getSummonType() {
        if (isPuppet()) {
            return OpsAssist.ASSIST_NONE;
        }
        switch (getSkillID()) {
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

    public boolean isPuppet() {
        switch (getSkillID()) {
            case 3111002:
            case 3211002:
            case 13111004:
            case 4341006:
            case 33111003:
                return true;
        }
        return false;
    }

    public MapleCharacter getOwner() {
        return null;
    }
}
