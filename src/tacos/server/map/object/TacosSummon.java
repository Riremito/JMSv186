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

import tacos.client.TacosCharacter;
import tacos.client.TacosSummonSkill;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsAssist;
import tacos.packet.ops.OpsMoveAbility;
import tacos.packet.ops.OpsSkill;

/**
 *
 * @author Riremito
 */
public class TacosSummon extends TacosMapObject {

    private int nCharLevel;
    private int nSkillID;
    private int nSLV;
    private TacosCharacter player;
    // wz data.
    private int summon_time;
    private int summon_hp = 1;
    // client data.
    OpsSkill skill;
    OpsMoveAbility move_ability;
    OpsAssist assist;

    public TacosSummon(TacosCharacter player, TacosSummonSkill tss) {
        this.nCharLevel = player.getLevel();
        this.nSkillID = tss.getId();
        this.nSLV = tss.getLevel();
        this.player = player;
        this.summon_time = tss.getTime() * 1000;
        this.summon_hp = tss.getX();
        setOwnerId(player.getId());
        setFootholdId(player.getFH());
        setPosition(player.getPosition());
        setSummonData(tss.getId());
    }

    public int getSkillID() {
        return this.nSkillID;
    }

    public int getOwnerLevel() {
        return this.nCharLevel;
    }

    public int getSLV() {
        return this.nSLV;
    }

    public TacosCharacter getOwner() {
        return this.player;
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

    public OpsSkill getSkill() {
        return this.skill;
    }

    public OpsMoveAbility getMoveAbility() {
        return this.move_ability;
    }

    public OpsAssist getAssist() {
        return this.assist;
    }

    private void setMoveAbility(OpsMoveAbility move_ability) {
        this.move_ability = move_ability;
    }

    private void setAssist(OpsAssist assist) {
        this.assist = assist;
    }

    private void setSummonData(int nSkillID) {
        this.skill = OpsSkill.find(nSkillID);
        switch (this.skill) {
            case DARKKNIGHT_BEHOLDER -> {
                // ダークスピリット
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_HEAL);
            }
            case ARCHMAGE1_IFRIT -> {
                // エルクィネス (PreBB)
                // イフリート (BIGBANG)
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case ARCHMAGE2_ELQUINES -> {
                // イフリート (PreBB)
                // エルクィネス (BIGBANG)
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case PRIEST_SUMMON_DRAGON -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_FLY);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case BISHOP_BAHAMUT -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case RANGER_PUPPET -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_NONE);
            }
            case RANGER_SILVER_HAWK -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_FLY);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case BOWMASTER_PHOENIX -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_FLY);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case SNIPER_PUPPET -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_NONE);
            }
            case SNIPER_GOLDEN_EAGLE -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_FLY);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case CROSSBOWMASTER_FREEZER -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_FLY);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case VALKYRIE_OCTOPUS -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case VALKYRIE_GABIOTA -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_FLY_RANDOM);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case CAPTAIN_SUPPORT_OCTOPUS -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case SOULMASTER_SOUL -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case FLAMEWIZARD_FLAME -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case FLAMEWIZARD_IFRIT -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case WINDBREAKER_STORM -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case WINDBREAKER_PUPPET -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_NONE);
            }
            case NIGHTWALKER_DARKNESS -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case STRIKER_LIGHTNING -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case DUAL5_DUMMY_EFFECT -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            // BIGBANG
            case HERMIT_SHADOW_MIRROR -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_ATTACK_COUNTER);
            }
            case BMAGE_REVIVE -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK_RANDOM);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case WILDHUNTER_TRAP -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case WILDHUNTER_SILVER_HAWK -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_FLY);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case MECHANIC_SATELITE -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK_EX);
            }
            case MECHANIC_TESLA_COIL -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case MECHANIC_SIEGE1 -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case MECHANIC_VELOCITY_CONTROLER -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            case MECHANIC_SATELITE2 -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK_EX);
            }
            case MECHANIC_SATELITE3 -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_WALK);
                setAssist(OpsAssist.ASSIST_ATTACK_EX);
            }
            case MECHANIC_ROBOROBO -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_SUMMON);
            }
            case MECHANIC_ROBOROBO_DUMMY -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_SUMMON);
            }
            case KANNA_KISHIN -> {
                setMoveAbility(OpsMoveAbility.MOVEABILITY_STOP);
                setAssist(OpsAssist.ASSIST_ATTACK);
            }
            default -> {
                setMoveAbility(OpsMoveAbility.UNKNOWN);
                setAssist(OpsAssist.UNKNOWN);
                DebugLogger.ErrorLog("setSummonData : " + nSkillID + " ( " + this.skill + " ), not coded.");
            }
        }
    }
}
