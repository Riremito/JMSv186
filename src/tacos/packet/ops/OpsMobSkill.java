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
package tacos.packet.ops;

import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsMobSkill implements IPacketOps {
    MOBSKILL_POWERUP(100),
    MOBSKILL_MAGICUP(101),
    MOBSKILL_PGUARDUP(102),
    MOBSKILL_MGUARDUP(103),
    MOBSKILL_HASTE(104),
    MOBSKILL_POWERUP_M(110),
    MOBSKILL_MAGICUP_M(111),
    MOBSKILL_PGUARDUP_M(112),
    MOBSKILL_MGUARDUP_M(113),
    MOBSKILL_HEAL_M(114),
    MOBSKILL_HASTE_M(115),
    MOBSKILL_SEAL(120),
    MOBSKILL_DARKNESS(121),
    MOBSKILL_WEAKNESS(122),
    MOBSKILL_STUN(123),
    MOBSKILL_CURSE(124),
    MOBSKILL_POISON(125),
    MOBSKILL_SLOW(126),
    MOBSKILL_DISPEL(127),
    MOBSKILL_ATTRACT(128),
    MOBSKILL_BANMAP(129),
    MOBSKILL_AREA_FIRE(130),
    MOBSKILL_AREA_POISON(131),
    MOBSKILL_REVERSE_INPUT(132),
    MOBSKILL_UNDEAD(133),
    MOBSKILL_STOPPORTION(134),
    MOBSKILL_STOPMOTION(135),
    MOBSKILL_FEAR(136),
    MOBSKILL_FROZEN(137),
    MOBSKILL_PHYSICALIMMUNE(140),
    MOBSKILL_MAGICIMMUNE(141),
    MOBSKILL_HARDSKIN(142),
    MOBSKILL_PCOUNTER(143),
    MOBSKILL_MCOUNTER(144),
    MOBSKILL_PMCOUNTER(145),
    MOBSKILL_PAD(150),
    MOBSKILL_MAD(151),
    MOBSKILL_PDR(152),
    MOBSKILL_MDR(153),
    MOBSKILL_ACC(154),
    MOBSKILL_EVA(155),
    MOBSKILL_SPEED(156),
    MOBSKILL_SEALSKILL(157),
    MOBSKILL_BALROGCOUNTER(158),
    MOBSILLL_SPREADSKILLFROMUSER(160),
    MOBSKILL_HEALBYDAMAGE(161),
    MOBSKILL_BIND(162),
    MOBSKILL_SUMMON(200),
    MOBSKILL_SUMMON_CUBE(201),
    UNKNOWN;

    private int value;

    OpsMobSkill(int val) {
        this.value = val;
    }

    OpsMobSkill() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static OpsMobSkill find(int val) {
        for (OpsMobSkill ops : values()) {
            if (ops.get() == val) {
                if (val != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsMobSkill ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Version.PostBB()) {
            return;
        }
        clear();
        // JMS147
        MOBSKILL_POWERUP.set(100);
        MOBSKILL_MAGICUP.set(101);
        MOBSKILL_PGUARDUP.set(102);
        MOBSKILL_MGUARDUP.set(103);
        MOBSKILL_POWERUP_M.set(110);
        MOBSKILL_MAGICUP_M.set(111);
        MOBSKILL_PGUARDUP_M.set(112);
        MOBSKILL_MGUARDUP_M.set(113);
        MOBSKILL_HEAL_M.set(114);
        MOBSKILL_SEAL.set(120);
        MOBSKILL_DARKNESS.set(121);
        MOBSKILL_WEAKNESS.set(122);
        MOBSKILL_STUN.set(123);
        MOBSKILL_CURSE.set(124);
        MOBSKILL_POISON.set(125);
        MOBSKILL_SLOW.set(126);
        MOBSKILL_DISPEL.set(127);
        MOBSKILL_ATTRACT.set(128);
        MOBSKILL_BANMAP.set(129);
        MOBSKILL_AREA_POISON.set(131);
        MOBSKILL_REVERSE_INPUT.set(132);
        MOBSKILL_PHYSICALIMMUNE.set(140);
        MOBSKILL_MAGICIMMUNE.set(141);
        MOBSKILL_PAD.set(150);
        MOBSKILL_MAD.set(151);
        MOBSKILL_PDR.set(152);
        MOBSKILL_MDR.set(153);
        MOBSKILL_ACC.set(154);
        MOBSKILL_EVA.set(155);
        MOBSKILL_SPEED.set(156);
        MOBSKILL_SEALSKILL.set(157);
        MOBSKILL_SUMMON.set(200);
    }
}
