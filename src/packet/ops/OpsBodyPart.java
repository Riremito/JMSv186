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
package packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsBodyPart {
    // GMS v95
    BP_HAIR(0x0),
    BP_CAP(0x1),
    BP_FACEACC(0x2),
    BP_EYEACC(0x3),
    BP_EARACC(0x4),
    BP_CLOTHES(0x5),
    BP_PANTS(0x6),
    BP_SHOES(0x7),
    BP_GLOVES(0x8),
    BP_CAPE(0x9),
    BP_SHIELD(0xA),
    BP_WEAPON(0xB),
    BP_RING1(0xC),
    BP_RING2(0xD),
    BP_PETWEAR(0xE),
    BP_RING3(0xF),
    BP_RING4(0x10),
    BP_PENDANT(0x11),
    BP_TAMINGMOB(0x12),
    BP_SADDLE(0x13),
    BP_MOBEQUIP(0x14),
    BP_MEDAL(21),
    //BP_PETRING_LABEL(0x15),
    BP_PETABIL_ITEM(0x16),
    BP_PETABIL_MESO(0x17),
    BP_PETABIL_HPCONSUME(0x18),
    BP_PETABIL_MPCONSUME(0x19),
    BP_PETABIL_SWEEPFORDROP(0x1A),
    BP_PETABIL_LONGRANGE(0x1B),
    BP_PETABIL_PICKUPOTHERS(0x1C),
    BP_PETRING_QUOTE(0x1D),
    BP_PETWEAR2(0x1E),
    BP_PETRING_LABEL2(0x1F),
    BP_PETRING_QUOTE2(0x20),
    BP_PETABIL_ITEM2(0x21),
    BP_PETABIL_MESO2(0x22),
    BP_PETABIL_SWEEPFORDROP2(0x23),
    BP_PETABIL_LONGRANGE2(0x24),
    BP_PETABIL_PICKUPOTHERS2(0x25),
    BP_PETWEAR3(0x26),
    BP_PETRING_LABEL3(0x27),
    BP_PETRING_QUOTE3(0x28),
    BP_PETABIL_ITEM3(0x29),
    BP_PETABIL_MESO3(0x2A),
    BP_PETABIL_SWEEPFORDROP3(0x2B),
    BP_PETABIL_LONGRANGE3(0x2C),
    BP_PETABIL_PICKUPOTHERS3(0x2D),
    BP_PETABIL_IGNOREITEMS1(0x2E),
    BP_PETABIL_IGNOREITEMS2(0x2F),
    BP_PETABIL_IGNOREITEMS3(0x30),
    BP_BELT(0x32),
    BP_SHOULDER(0x33),
    BP_NOTHING3(0x36),
    BP_NOTHING2(0x37),
    BP_NOTHING1(0x38),
    BP_NOTHING0(0x39),
    BP_EXT_0(0x3B),
    BP_EXT_PENDANT1(0x3B),
    BP_EXT_1(0x3C),
    BP_EXT_2(0x3D),
    BP_EXT_3(0x3E),
    BP_EXT_4(0x3F),
    BP_EXT_5(0x40),
    BP_EXT_6(0x41),
    BP_COUNT(0x3B),
    BP_EXT_END(0x3B),
    BP_EXT_COUNT(0x1),
    BP_EXCOUNT(0x3C),
    BP_STICKER(0x64),
    DP_BASE(0x3E8),
    DP_CAP(0x3E8),
    DP_PENDANT(0x3E9),
    DP_WING(0x3EA),
    DP_SHOES(0x3EB),
    DP_END(0x3EC),
    DP_COUNT(0x4),
    MP_BASE(0x44C),
    MP_ENGINE(0x44C),
    MP_ARM(0x44D),
    MP_LEG(0x44E),
    MP_FRAME(0x44F),
    MP_TRANSISTER(0x450),
    MP_END(0x451),
    MP_COUNT(0x5),
    UNKNOWN(-1);

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
        for (final OpsBodyPart o : OpsBodyPart.values()) {
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
