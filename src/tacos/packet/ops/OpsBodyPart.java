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
package tacos.packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsBodyPart {
    // GMS v95
    BP_HAIR(0),
    BP_CAP(1),
    BP_FACEACC(2),
    BP_EYEACC(3),
    BP_EARACC(4),
    BP_CLOTHES(5),
    BP_PANTS(6),
    BP_SHOES(7),
    BP_GLOVES(8),
    BP_CAPE(9),
    BP_SHIELD(10),
    BP_WEAPON(11),
    BP_RING1(12),
    BP_RING2(13),
    BP_PETWEAR(14),
    BP_RING3(15),
    BP_RING4(16),
    BP_PENDANT(17),
    BP_TAMINGMOB(18),
    BP_SADDLE(19),
    BP_MOBEQUIP(20),
    BP_MEDAL(21),
    //BP_PETRING_LABEL(0x15),
    BP_PETABIL_ITEM(22),
    BP_PETABIL_MESO(23),
    BP_PETABIL_HPCONSUME(24),
    BP_PETABIL_MPCONSUME(25),
    BP_PETABIL_SWEEPFORDROP(26),
    BP_PETABIL_LONGRANGE(27),
    BP_PETABIL_PICKUPOTHERS(28),
    BP_PETRING_QUOTE(29),
    BP_PETWEAR2(30),
    BP_PETRING_LABEL2(31),
    BP_PETRING_QUOTE2(32),
    BP_PETABIL_ITEM2(33),
    BP_PETABIL_MESO2(34),
    BP_PETABIL_SWEEPFORDROP2(35),
    BP_PETABIL_LONGRANGE2(36),
    BP_PETABIL_PICKUPOTHERS2(37),
    BP_PETWEAR3(38),
    BP_PETRING_LABEL3(39),
    BP_PETRING_QUOTE3(40),
    BP_PETABIL_ITEM3(41),
    BP_PETABIL_MESO3(42),
    BP_PETABIL_SWEEPFORDROP3(43),
    BP_PETABIL_LONGRANGE3(44),
    BP_PETABIL_PICKUPOTHERS3(45),
    BP_PETABIL_IGNOREITEMS1(46),
    BP_PETABIL_IGNOREITEMS2(47),
    BP_PETABIL_IGNOREITEMS3(48),
    BP_BELT(50),
    BP_SHOULDER(51),
    BP_NOTHING3(54),
    BP_NOTHING2(55),
    BP_NOTHING1(56),
    BP_NOTHING0(57),
    BP_EXT_0(59),
    BP_EXT_PENDANT1(59),
    BP_EXT_1(60),
    BP_EXT_2(61),
    BP_EXT_3(62),
    BP_EXT_4(63),
    BP_EXT_5(64),
    BP_EXT_6(65),
    BP_COUNT(59),
    BP_EXT_END(59),
    BP_EXT_COUNT(1),
    BP_EXCOUNT(60),
    BP_STICKER(100),
    DP_BASE(1000),
    DP_CAP(1000),
    DP_PENDANT(1001),
    DP_WING(1002),
    DP_SHOES(1003),
    DP_END(1004),
    DP_COUNT(4),
    MP_BASE(1100),
    MP_ENGINE(1100),
    MP_ARM(1101),
    MP_LEG(1102),
    MP_FRAME(1103),
    MP_TRANSISTER(1104),
    MP_END(1105),
    MP_COUNT(5),
    UNKNOWN;

    private int value;

    OpsBodyPart(int flag) {
        value = flag;
    }

    OpsBodyPart() {
        value = -1;
    }

    public short get() {
        return (short) value;
    }

    public short getSlot() {
        return (short) -value;
    }

    public static OpsBodyPart find(int val) {
        for (OpsBodyPart o : OpsBodyPart.values()) {
            if (o.get() == val) {
                return o;
            }
        }
        return UNKNOWN;
    }

    public static OpsBodyPart get_bodypart_from_item(int itemid) {
        int equip_type = itemid / 10000;
        switch (equip_type) {
            case 100: {
                return BP_CAP;
            }
            case 101: {
                return BP_FACEACC;
            }
            case 102: {
                return BP_EYEACC;
            }
            case 103: {
                return BP_EARACC;
            }
            case 104:
            case 105: {
                return BP_CLOTHES;
            }
            case 106: {
                return BP_PANTS;
            }
            case 107: {
                return BP_SHOES;
            }
            case 108: {
                return BP_GLOVES;
            }
            case 109:
            case 119:
            case 134: {
                return BP_SHIELD;
            }
            case 110: {
                return BP_CAPE;
            }
            case 111: {
                // 2-4
                return BP_RING1;
            }
            default:
                break;
        }
        int weapon_type = equip_type / 10;
        switch (weapon_type) {
            case 12: // JMS308, Angelic Buster
            case 13:
            case 14:
            case 15: // 修正必須
            case 16:
            case 17: {
                return BP_WEAPON;
            }
            default:
                break;
        }
        return UNKNOWN;
    }
}
