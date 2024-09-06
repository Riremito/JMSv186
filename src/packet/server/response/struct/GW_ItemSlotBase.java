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
 *
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet.server.response.struct;

import client.inventory.IEquip;
import client.inventory.IItem;
import config.ServerConfig;
import java.sql.Timestamp;
import packet.server.ServerPacket;

/**
 *
 * @author Riremito
 */
public class GW_ItemSlotBase {

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

        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 165) {
            data.Encode1(pos);
        } else {
            // v186+
            if (item.getType() == 1) {
                data.Encode2(pos);
            } else {
                data.Encode1(pos);
            }
        }
        return data.Get().getBytes();
    }

    public static final byte[] EncodeSlotEnd(ItemType it) {
        ServerPacket data = new ServerPacket();

        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 165) {
            data.Encode1(0);
        } else {
            // v186+
            if (it == ItemType.Equip) {
                data.Encode2(0);
            } else {
                data.Encode1(0);
            }
        }

        return data.Get().getBytes();
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
                data.EncodeBuffer(RawEncode(item));

                final IEquip equip = (IEquip) item;
                boolean hasUniqueId = 0 < equip.getUniqueId();

                data.Encode1(equip.getUpgradeSlots());
                data.Encode1(equip.getLevel());
                // v184-v185 潜在内部実装時 (動作はしないがデータの位置が違う)
                if ((ServerConfig.IsJMS() && 184 <= ServerConfig.GetVersion() && ServerConfig.GetVersion() <= 185) || (ServerConfig.IsKMS() && ServerConfig.GetVersion() == 95)) {
                    data.Encode1(equip.getState());
                }
                // data.Encode2((short) equip.getIncAttackSpeed());
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

                // ポイントアイテムを一度も装備していないことを確認するためのフラグ
                if (hasUniqueId) {
                    // ポイントアイテム交換可能
                    data.Encode2(0x10);
                } else {
                    /*
                    0x0001 封印
                    0x0002 滑り防止効果
                    0x0004 寒気防止効果
                    0x0008
                    0x0010 1回交換可能
                     */
                    data.Encode2(equip.getFlag()); // item._ZtlSecureTear_nAttribute
                }

                // 確認必須
                if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131) {
                    if (!hasUniqueId) {
                        data.Encode8(equip.getPosition() <= 0 ? -1 : item.getUniqueId());
                    }
                    break; // test for v131
                }

                data.Encode1(0); // item._ZtlSecureTear_nLevelUpType
                data.Encode1(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // item._ZtlSecureTear_nLevel

                // encode4
                data.Encode4(equip.getExpPercentage() * 4); // item._ZtlSecureTear_nEXP

                // 耐久度
                if ((ServerConfig.IsJMS() && 180 <= ServerConfig.GetVersion())
                        || ServerConfig.IsTWMS()
                        || ServerConfig.IsCMS()
                        || ServerConfig.IsKMS()) {
                    data.Encode4(equip.getDurability()); // item._ZtlSecureTear_nDurability
                }

                // 通常
                if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion()) || !(ServerConfig.IsJMS() && 184 <= ServerConfig.GetVersion() && ServerConfig.GetVersion() <= 185)) {

                    // ビシャスのハンマー
                    if ((ServerConfig.IsJMS() && 180 <= ServerConfig.GetVersion())
                            || ServerConfig.IsTWMS()
                            || ServerConfig.IsCMS()
                            || (ServerConfig.IsKMS() && ServerConfig.IsPostBB())) {
                        if (ServerConfig.game_server_enable_hammer) {
                            data.Encode4(equip.getViciousHammer()); // item._ZtlSecureTear_nIUC
                        } else {
                            data.Encode4(0);
                        }
                    }
                    // 潜在能力
                    if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())
                            || ServerConfig.IsTWMS()
                            || ServerConfig.IsCMS()
                            || ServerConfig.IsKMS()) {
                        if (!(ServerConfig.IsKMS() && ServerConfig.GetVersion() == 95)) {
                            data.Encode1(equip.getState()); // option._ZtlSecureTear_nGrade
                        }
                        data.Encode1(equip.getEnhance()); // option._ZtlSecureTear_nCHUC
                        if (ServerConfig.game_server_enable_potential) {
                            data.Encode2(equip.getPotential1()); // option._ZtlSecureTear_nOption1
                            data.Encode2(equip.getPotential2()); // option._ZtlSecureTear_nOption2
                            data.Encode2(equip.getPotential3()); // option._ZtlSecureTear_nOption3
                        } else {
                            data.Encode2(0);
                            data.Encode2(0);
                            data.Encode2(0);
                        }
                        data.Encode2(equip.getHpR()); // option._ZtlSecureTear_nSocket1
                        data.Encode2(equip.getMpR()); // option._ZtlSecureTear_nSocket2
                    }
                } // 特殊パターン
                else {
                    data.Encode1(equip.getEnhance()); // option._ZtlSecureTear_nCHUC
                    data.Encode2(equip.getPotential1()); // option._ZtlSecureTear_nOption1
                    data.Encode2(equip.getPotential2()); // option._ZtlSecureTear_nOption2
                    data.Encode2(equip.getPotential3()); // option._ZtlSecureTear_nOption3
                    data.Encode2(equip.getHpR()); // option._ZtlSecureTear_nSocket1
                    data.Encode2(equip.getMpR()); // option._ZtlSecureTear_nSocket2
                    data.Encode4(equip.getViciousHammer()); // item._ZtlSecureTear_nIUC
                }

                if (!hasUniqueId) {
                    data.Encode8(0);
                }

                data.Encode8(0);
                data.Encode4(-1);
                break;
            }
            // Pet
            case 3: {
                data.EncodeBuffer(RawEncode(item));
                // GW_ItemSlotPet::RawDecode
                data.EncodeBuffer(item.getPet().getName(), 13);
                data.Encode1(item.getPet().getLevel());
                data.Encode2(item.getPet().getCloseness()); // nTameness_CS
                data.Encode1(item.getPet().getFullness()); // nRepleteness_CS
                // 魔法の効力期限, Windows時間
                data.Encode8((Timestamp.valueOf("2027-07-07 07:00:00").getTime() + Timestamp.valueOf("2339-01-01 18:00:00").getTime()) * 10000); // dateDead
                data.Encode2(0); // nPetAttribute_CS
                data.Encode2(item.getPet().getFlags()); // usPetSkill_CS
                // 魔法の時間, デンデン専用 (残り時間)
                data.Encode4((item.getItemId() == 5000054) ? 3600 : 0); // nRemainLife_CS
                data.Encode2(0); // nAttribute_CS
                data.Encode1(item.getPet().getSummoned() ? 1 : 0);
                data.Encode4(0);
                break;
            }
            // Consume, Install, Etc, Cash
            default: {
                data.EncodeBuffer(RawEncode(item));
                // GW_ItemSlotBundle::RawDecode
                data.Encode2(item.getQuantity());
                data.EncodeStr(item.getOwner());
                data.Encode2(item.getFlag());

                int item_img_num = item.getItemId() / 10000;

                // 手裏剣 or 弾丸
                if (item_img_num == 207 || item_img_num == 233) {
                    // 8 bytes
                    data.Encode4(2);
                    data.Encode2(0x54);
                    data.Encode1(0);
                    data.Encode1(0x34);
                }
                break;
            }
        }

        return data.Get().getBytes();
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

        data.Encode8(-1); // time?

        if (!(ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 188) && ServerConfig.IsPostBB()) {
            data.Encode4(0);
        }

        return data.Get().getBytes();
    }

    public static long getTestExpiration() {
        return (Timestamp.valueOf("2027-07-07 07:00:00").getTime() + Timestamp.valueOf("2339-01-01 18:00:00").getTime()) * 10000;
    }
}
