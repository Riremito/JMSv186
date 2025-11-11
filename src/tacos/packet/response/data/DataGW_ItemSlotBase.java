/*
 * Copyright (C) 2023 Riremito
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
 */
package tacos.packet.response.data;

import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import tacos.config.Content;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.data.client.DC_Date;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class DataGW_ItemSlotBase {

    public enum ItemType {
        Equip,
        Consume,
        Install,
        Etc,
        Cash,
        Pet,
        UNKNOWN;
    }

    public static final byte[] EncodeSlot(final IItem item) {
        ServerPacket data = new ServerPacket();

        short pos = item.getPosition();
        if (pos <= -1) {
            pos *= -1;
            if (pos > 100 && pos < 1000) {
                pos -= 100;
            }
        }

        if (ServerConfig.JMS165orEarlier() || Version.LessOrEqual(Region.KMS, 84)) {
            data.Encode1(pos);
        } else {
            // v186+
            if (item.getType() == 1) {
                data.Encode2(pos);
            } else {
                data.Encode1(pos);
            }
        }
        return data.get().getBytes();
    }

    public static final byte[] EncodeSlotEnd(ItemType it) {
        ServerPacket data = new ServerPacket();

        if (ServerConfig.JMS165orEarlier() || Version.LessOrEqual(Region.KMS, 84)) {
            data.Encode1(0);
        } else {
            // v186+
            if (it == ItemType.Equip) {
                data.Encode2(0);
            } else {
                data.Encode1(0);
            }
        }

        return data.get().getBytes();
    }

    public static byte getPotentialRank(Equip equip) {
        // 未確認アイテム
        if (equip.getHidden() != 0) {
            return 1;
        }
        int rank = equip.getRank();
        // JMSv 186 :  5,  6,  7
        // JMS v302 : 17, 18, 19, 20

        // 潜在能力なし
        if (rank == 0) {
            return 0;
        }

        // 等級
        return (byte) ((Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) ? (rank + 16) : (rank + 4));

    }

    // GW_ItemSlotBase::Decode
    // addItemInfo
    public static final byte[] Encode(final IItem item) {
        ServerPacket data = new ServerPacket();

        data.Encode1(item.getPet() != null ? 3 : item.getType());

        // GW_ItemSlotBase::CreateItem call bellow
        int it = item.getPet() != null ? 3 : item.getType();
        switch (it) {
            // Equip
            case 1: {
                final Equip equip = (Equip) item;
                boolean hasUniqueId = 0 < equip.getUniqueId();
                data.EncodeBuffer(RawEncode(item));
                // JMS184-185, KMS95 (test version of potential system)
                if (Content.PrePotential.get()) {
                    data.EncodeBuffer(EncodeEquip_JMS184(equip, hasUniqueId));
                    break;
                }

                if (Version.GreaterOrEqual(Region.KMS, 197)) {
                    data.EncodeBuffer(EncodeEquip_KMS197(equip, hasUniqueId));
                    break;
                }

                if (Version.GreaterOrEqual(Region.EMS, 89)) {
                    data.EncodeBuffer(EncodeEquip_EMS89(equip, hasUniqueId));
                    break;
                }

                data.Encode1(equip.getUpgradeSlots());
                data.Encode1(equip.getLevel());
                data.Encode2(equip.getStr());
                data.Encode2(equip.getDex());
                data.Encode2(equip.getInt());
                data.Encode2(equip.getLuk());
                data.Encode2(equip.getHp());
                data.Encode2(equip.getMp());
                data.Encode2(equip.getWatk());
                data.Encode2(equip.getMatk());
                data.Encode2(equip.getWdef());
                data.Encode2(equip.getMdef());
                data.Encode2(equip.getAcc());
                data.Encode2(equip.getAvoid());
                data.Encode2(equip.getHands());
                data.Encode2(equip.getSpeed());
                data.Encode2(equip.getJump());
                data.EncodeStr(equip.getOwner());

                if (Version.LessOrEqual(Region.KMS, 1)) {
                    break;
                }

                if (Version.LessOrEqual(Region.KMS, 31)) {
                    data.Encode2(0);
                    break;
                }

                if (Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
                    data.Encode4(equip.getFlag());
                } else {
                    data.Encode2(equip.getFlag()); // item._ZtlSecureTear_nAttribute
                }
                // リバース武器
                if (ServerConfig.JMS164orLater() || Region.IsVMS()) {
                    data.Encode1(0); // item._ZtlSecureTear_nLevelUpType
                    data.Encode1(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // item._ZtlSecureTear_nLevel
                    data.Encode4(equip.getExpPercentage() * 4); // item._ZtlSecureTear_nEXP
                }
                // 耐久度
                if (ServerConfig.JMS180orLater() || Version.GreaterOrEqual(Region.GMS, 84)) {
                    data.Encode4(equip.getDurability()); // item._ZtlSecureTear_nDurability
                }
                // ビシャスのハンマー
                if (ServerConfig.JMS180orLater() || Version.GreaterOrEqual(Region.GMS, 73) || Region.IsBMS()) {
                    if (!Region.IsKMS() || Version.GreaterOrEqual(Region.KMS, 95)) {
                        data.Encode4(equip.getViciousHammer()); // item._ZtlSecureTear_nIUC, JMS v302 MAX = 0xDF (15 / (13+2))
                    }
                }
                if (ServerConfig.KMS127orLater() || Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
                    data.Encode2(0);
                }
                if (Version.GreaterOrEqual(Region.GMS, 126)) {
                    data.Encode2(0);
                    data.Encode4(0);
                    data.Encode1(0);
                    data.Encode1(0);
                }
                // 潜在能力, 装備強化 (星)
                if (ServerConfig.JMS186orLater()) {
                    data.Encode1(getPotentialRank(equip)); // option._ZtlSecureTear_nGrade
                    data.Encode1(equip.getEnhance()); // option._ZtlSecureTear_nCHUC
                    data.Encode2(equip.getPotential1()); // option._ZtlSecureTear_nOption1
                    data.Encode2(equip.getPotential2()); // option._ZtlSecureTear_nOption2
                    data.Encode2(equip.getPotential3()); // option._ZtlSecureTear_nOption3
                    data.Encode2(0); // option._ZtlSecureTear_nSocket1, v302 潜在能力4個目?
                    data.Encode2(0); // option._ZtlSecureTear_nSocket2, v302 カナトコ?
                }
                if (Version.GreaterOrEqual(Region.KMS, 169) || Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
                    data.Encode2(0);
                }
                if (Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
                    data.Encode2(0);
                }
                if (Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
                    data.Encode2(0);
                    data.Encode2(0);
                }
                if (Version.GreaterOrEqual(Region.GMS, 126)) {
                    data.Encode2(0);
                }
                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    data.Encode2(0); // v302, Alien Stone
                    data.Encode4(0);
                    data.Encode4(0);
                    data.Encode4(0);
                }
                if (!hasUniqueId) {
                    data.Encode8(0);
                }
                if (ServerConfig.JMS164orLater()) {
                    data.Encode8(0);
                    data.Encode4(-1);
                }

                // CMS
                if (Version.GreaterOrEqual(Region.CMS, 104)) {
                    data.Encode8(0);
                    data.Encode8(0);
                    data.Encode4(0);
                    for (int i = 0; i < 3; i++) {
                        data.Encode4(0);
                    }
                    break;
                }

                if (Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.JMST, 110)) {
                    data.Encode4(0);
                    data.Encode1(0);
                    data.Encode1(0); // 1 = 赤色アイテム
                    data.Encode2(0); // 1-4 = 魂の書
                }
                break;
            }
            // Pet
            case 3: {
                data.EncodeBuffer(RawEncode(item));
                // GW_ItemSlotPet::RawDecode
                data.EncodeBuffer(item.getPet().getName(), Region.IsBMS() ? 21 : 13);
                data.Encode1(item.getPet().getLevel()); // nLevel_CS
                data.Encode2(item.getPet().getCloseness()); // nTameness_CS
                data.Encode1(item.getPet().getFullness()); // nRepleteness_CS
                // 魔法の効力期限, Windows時間
                data.Encode8(DC_Date.getMagicalExpirationDate()); // dateDead
                data.Encode2(0); // nPetAttribute_CS
                data.Encode2(item.getPet().getFlags()); // usPetSkill_CS

                if (Version.LessOrEqual(Region.KMS, 31)) {
                    break;
                }

                if (ServerConfig.JMS164orLater() || Region.IsVMS()) {
                    // 魔法の時間, デンデン専用 (残り時間)
                    data.Encode4((item.getItemId() == 5000054) ? 3600 : 0); // nRemainLife_CS
                }
                if (ServerConfig.JMS180orLater() || Version.GreaterOrEqual(Region.KMS, 84) || Version.GreaterOrEqual(Region.GMS, 83)) {
                    data.Encode2(0); // nAttribute_CS
                }
                if (Version.LessOrEqual(Region.GMS, 95) || Version.LessOrEqual(Region.EMS, 72)) {
                    break;
                }
                // GMS111, EMS76
                if (ServerConfig.JMS186orLater()) {
                    data.Encode1(item.getPet().getSummoned() ? 1 : 0);
                    data.Encode4(0);
                }
                if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.KMS, 169) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 126)) {
                    data.Encode4(0);
                }
                if (Version.GreaterOrEqual(Region.KMS, 197)) {
                    data.Encode2(0);
                }
                break;
            }
            // Consume, Install, Etc, Cash
            default: {
                data.EncodeBuffer(RawEncode(item));
                // GW_ItemSlotBundle::RawDecode
                data.Encode2(item.getQuantity());
                data.EncodeStr(item.getOwner());

                if (Version.LessOrEqual(Region.KMS, 1)) {
                    break;
                }
                data.Encode2(item.getFlag());

                if (Version.LessOrEqual(Region.KMS, 31)) {
                    break;
                }

                int item_img_num = item.getItemId() / 10000;

                // 手裏剣 or 弾丸
                if (item_img_num == 207 || item_img_num == 233 || item_img_num == 287 || item_img_num == 288 || item_img_num == 289) {
                    // 8 bytes
                    data.Encode4(2);
                    data.Encode2(0x54);
                    data.Encode1(0);
                    data.Encode1(0x34);
                }
                if (item_img_num == 223) {
                    // KMST only
                    if (Version.Equal(Region.KMST, 391)) {
                        data.Encode8(0);
                    }
                }

                if (Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.JMST, 110)) {
                    data.Encode4(0);
                    data.Encode2(0);
                    data.Encode2(0);
                    data.Encode2(0);
                    data.Encode2(0);
                    data.Encode8(0);
                    data.Encode8(0);
                    data.Encode8(0);
                    data.Encode8(0);
                    data.Encode4(0);
                    data.Encode2(0);
                }
                break;
            }
        }

        return data.get().getBytes();
    }
    // JMS v184, v185, KMS 95

    public static final byte[] EncodeEquip_JMS184(Equip equip, boolean hasUniqueId) {
        ServerPacket data = new ServerPacket();

        data.Encode1(equip.getUpgradeSlots());
        data.Encode1(equip.getLevel());
        data.Encode1(getPotentialRank(equip));
        data.Encode2(equip.getStr());
        data.Encode2(equip.getDex());
        data.Encode2(equip.getInt());
        data.Encode2(equip.getLuk());
        data.Encode2(equip.getHp());
        data.Encode2(equip.getMp());
        data.Encode2(equip.getWatk());
        data.Encode2(equip.getMatk());
        data.Encode2(equip.getWdef());
        data.Encode2(equip.getMdef());
        data.Encode2(equip.getAcc());
        data.Encode2(equip.getAvoid());
        data.Encode2(equip.getHands());
        data.Encode2(equip.getSpeed());
        data.Encode2(equip.getJump());
        data.EncodeStr(equip.getOwner());
        data.Encode2(equip.getFlag());
        data.Encode1(0); // item._ZtlSecureTear_nLevelUpType
        data.Encode1(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // item._ZtlSecureTear_nLevel
        data.Encode4(equip.getExpPercentage() * 4); // item._ZtlSecureTear_nEXP
        data.Encode4(equip.getDurability()); // item._ZtlSecureTear_nDurability
        data.Encode1(equip.getEnhance()); // option._ZtlSecureTear_nCHUC
        data.Encode2(equip.getPotential1()); // option._ZtlSecureTear_nOption1
        data.Encode2(equip.getPotential2()); // option._ZtlSecureTear_nOption2
        data.Encode2(equip.getPotential3()); // option._ZtlSecureTear_nOption3
        data.Encode2(0); // option._ZtlSecureTear_nSocket1
        data.Encode2(0); // option._ZtlSecureTear_nSocket2
        if (!hasUniqueId) {
            data.Encode8(0);
        }
        data.Encode8(0);
        data.Encode4(-1);
        return data.get().getBytes();
    }

    public static final byte[] EncodeEquip_EMS89(Equip equip, boolean hasUniqueId) {
        ServerPacket data = new ServerPacket();

        data.Encode1(equip.getUpgradeSlots());
        data.Encode1(equip.getLevel());
        data.Encode2(equip.getStr());
        data.Encode2(equip.getDex());
        data.Encode2(equip.getInt());
        data.Encode2(equip.getLuk());
        data.Encode2(equip.getHp());
        data.Encode2(equip.getMp());
        data.Encode2(equip.getWatk());
        data.Encode2(equip.getMatk());
        data.Encode2(equip.getWdef());
        data.Encode2(equip.getMdef());
        data.Encode2(equip.getAcc());
        data.Encode2(equip.getAvoid());
        data.Encode2(equip.getHands());
        data.Encode2(equip.getSpeed());
        data.Encode2(equip.getJump());
        data.EncodeStr(equip.getOwner());
        data.Encode2(equip.getFlag()); // item._ZtlSecureTear_nAttribute
        data.Encode1(0); // item._ZtlSecureTear_nLevelUpType
        data.Encode1(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // item._ZtlSecureTear_nLevel
        data.Encode4(equip.getExpPercentage() * 4); // item._ZtlSecureTear_nEXP
        data.Encode4(equip.getDurability()); // item._ZtlSecureTear_nDurability
        data.Encode4(equip.getViciousHammer()); // item._ZtlSecureTear_nIUC, JMS v302 MAX = 0xDF (15 / (13+2))
        data.Encode2(0);
        data.Encode1(getPotentialRank(equip)); // option._ZtlSecureTear_nGrade
        data.Encode1(equip.getEnhance()); // option._ZtlSecureTear_nCHUC
        data.Encode2(equip.getPotential1()); // option._ZtlSecureTear_nOption1
        data.Encode2(equip.getPotential2()); // option._ZtlSecureTear_nOption2
        data.Encode2(equip.getPotential3()); // option._ZtlSecureTear_nOption3
        data.Encode2(0); // option._ZtlSecureTear_nSocket1, v302 潜在能力4個目?
        data.Encode2(0); // option._ZtlSecureTear_nSocket2, v302 カナトコ
        data.Encode2(0);
        data.Encode2(0);
        if (!hasUniqueId) {
            data.Encode8(0);
        }
        data.Encode8(0);
        data.Encode4(-1);

        // sub_515F40
        data.Encode8(0);
        data.Encode8(0);
        data.Encode4(0);
        for (int i = 0; i < 3; i++) {
            data.Encode4(0);
        }

        return data.get().getBytes();
    }

    public static final byte[] EncodeEquip_KMS197(Equip equip, boolean hasUniqueId) {
        ServerPacket data = new ServerPacket();

        {
            long flag1 = -1;
            long flag2 = -1;
            data.Encode4((int) flag1);
            if ((flag1 & 0x01) > 0) {
                data.Encode1(equip.getUpgradeSlots());
            }
            if ((flag1 & 0x02) > 0) {
                data.Encode1(equip.getLevel());
            }
            // x16
            if ((flag1 & 0x04) > 0) {
                data.Encode2(equip.getStr());
            }
            if ((flag1 & 0x08) > 0) {
                data.Encode2(equip.getDex());
            }
            if ((flag1 & 0x10) > 0) {
                data.Encode2(equip.getInt());
            }
            if ((flag1 & 0x20) > 0) {
                data.Encode2(equip.getLuk());
            }
            if ((flag1 & 0x40) > 0) {
                data.Encode2(equip.getHp());
            }
            if ((flag1 & 0x80) > 0) {
                data.Encode2(equip.getMp());
            }
            if ((flag1 & 0x100) > 0) {
                data.Encode2(equip.getWatk());
            }
            if ((flag1 & 0x200) > 0) {
                data.Encode2(equip.getMatk());
            }
            if ((flag1 & 0x400) > 0) {
                data.Encode2(equip.getWdef());
            }
            if ((flag1 & 0x800) > 0) {
                data.Encode2(equip.getMdef());
            }
            if ((flag1 & 0x1000) > 0) {
                data.Encode2(equip.getAcc());
            }
            if ((flag1 & 0x2000) > 0) {
                data.Encode2(equip.getAvoid());
            }
            if ((flag1 & 0x4000) > 0) {
                data.Encode2(equip.getHands());
            }
            if ((flag1 & 0x8000) > 0) {
                data.Encode2(equip.getSpeed());
            }
            if ((flag1 & 0x10000) > 0) {
                data.Encode2(equip.getJump()); // niJump
            }
            if ((flag1 & 0x20000) > 0) {
                data.Encode2(equip.getFlag()); // nAttribute
            }
            if ((flag1 & 0x40000) > 0) {
                data.Encode1(0); // nLevelUpType
            }
            if ((flag1 & 0x80000) > 0) {
                data.Encode1(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // nLevel
            }
            if ((flag1 & 0x100000) > 0) {
                data.Encode8(equip.getExpPercentage() * 4); // nEXP64
            }
            if ((flag1 & 0x200000) > 0) {
                data.Encode4(equip.getDurability()); // nDurability
            }
            if ((flag1 & 0x400000) > 0) {
                data.Encode4(equip.getViciousHammer()); // nIUC
            }
            if ((flag1 & 0x800000) > 0) {
                data.Encode2(0); // niPVPDamage
            }
            if ((flag1 & 0x1000000) > 0) {
                data.Encode1(0); // niReduceReq
            }
            if ((flag1 & 0x2000000) > 0) {
                data.Encode2(0); // nSpecialAttribute
            }
            if ((flag1 & 0x4000000) > 0) {
                data.Encode4(0); // nDurabilityMax
            }
            if ((flag1 & 0x8000000) > 0) {
                data.Encode1(0); // niIncReq
            }
            if ((flag1 & 0x10000000L) > 0) {
                data.Encode1(0); // nGrowthEnchant
            }
            if ((flag1 & 0x20000000L) > 0) {
                data.Encode1(0); // nPSEnchant
            }
            if ((flag1 & 0x40000000L) > 0) {
                data.Encode1(0); // nBDR
            }
            if ((flag1 & 0x80000000L) > 0) {
                data.Encode1(0); // nIMDR
            }
            data.Encode4((int) flag2);
            if ((flag2 & 0x01) > 0) {
                data.Encode1(0); // nDamR
            }
            if ((flag2 & 0x02) > 0) {
                data.Encode1(0); // nStatR
            }
            if ((flag2 & 0x04) > 0) {
                data.Encode1(-1); // nCuttable
            }
            if ((flag2 & 0x08) > 0) {
                data.Encode8(0); // nExGradeOption
            }
            if ((flag2 & 0x10) > 0) {
                data.Encode4(0); // nItemState
            }
        }

        data.EncodeStr(equip.getOwner()); // title
        data.Encode1(getPotentialRank(equip)); // nGrade
        data.Encode1(equip.getEnhance()); // nCHUC
        data.Encode2(equip.getPotential1()); // nOption1
        data.Encode2(equip.getPotential2()); // nOption2
        data.Encode2(equip.getPotential3()); // nOption3
        data.Encode2(0); // nOption4
        data.Encode2(0); // nOption6
        data.Encode2(0); // nOption7
        data.Encode2(0); // nOption5
        if (!hasUniqueId) {
            data.Encode8(0); // liSN
        }
        data.Encode8(0); // ftEquipped
        data.Encode4(-1); // nPrevBonusExpRate
        // GW_CashItemOption::Decode
        {
            data.Encode8(0); // liCashItemSN
            data.Encode8(0); // ftExpireDate
            data.Encode4(0); // anOption
            for (int i = 0; i < 3; i++) {
                data.Encode4(0);
            }
        }
        data.Encode2(0); // nSoulOptionID
        data.Encode2(0); // nSoulSocketID
        data.Encode2(0); // nSoulOption

        return data.get().getBytes();
    }

    // E8 ?? ?? ?? ?? 84 C0 74 ?? 6A 08
    // GW_ItemSlotBase::RawDecode
    public static final byte[] RawEncode(final IItem item) {
        ServerPacket data = new ServerPacket();

        data.Encode4(item.getItemId());
        boolean hasUniqueId = 0 < item.getUniqueId();

        data.Encode1(hasUniqueId ? 1 : 0);

        if (hasUniqueId) {
            data.Encode8(item.getUniqueId());
        }

        data.Encode8(DC_Date.getNoExpirationDate());

        if (ServerConfig.JMS194orLater() || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode4(0);
        }

        return data.get().getBytes();
    }
}
