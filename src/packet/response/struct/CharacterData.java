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
package packet.response.struct;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import packet.ServerPacket;
import packet.response.struct.GW_ItemSlotBase.ItemType;

/**
 *
 * @author Riremito
 */
public class CharacterData {

    // all data
    public static byte[] Encode(MapleCharacter chr) {
        return Encode(chr, -1);
    }

    // CharacterData::Decode
    // CharacterInfo
    public static byte[] Encode(MapleCharacter chr, long datamask) {
        ServerPacket data = new ServerPacket();

        if (ServerConfig.JMS131orEarlier()) {
            data.Encode2((short) datamask); // statmask
        } else {
            data.Encode8(datamask); // statmask
        }

        if (ServerConfig.JMS180orLater() || ServerConfig.KMS84orLater() || ServerConfig.GMS83orLater()) {
            data.Encode1(0); // nCombatOrders
        }

        if (ServerConfig.KMS197orLater()) {
            for (int i = 0; i < 3; i++) {
                data.Encode4(0);
            }
        }

        if (ServerConfig.KMS138orLater() || ServerConfig.EMS89orLater()) {
            data.Encode1(0);
        }

        if (ServerConfig.KMS119orLater() || ServerConfig.JMST110() || ServerConfig.EMS89orLater()) {
            data.Encode4(0);
        }

        if (ServerConfig.IsPostBB()) {
            data.Encode1(0); // not 0, Encode1, Encode4(size), EncodeBuffer8, Encode4(size), EncodeBuffer8
        }

        if (ServerConfig.IsTHMS() || ServerConfig.IsVMS()) {
            if ((datamask & 0x02) > 0) {
                data.Encode4(0);
            }
        }

        if ((datamask & 0x01) > 0) {
            // キャラクター情報
            data.EncodeBuffer(GW_CharacterStat.Encode(chr));

            // 友達リストの上限
            data.Encode1(chr.getBuddylist().getCapacity());

            if (ServerConfig.EMS89orLater()) {
                data.Encode1(0);
                data.Encode1(0);
            }

            // 精霊の祝福 v165, v186
            if (ServerConfig.JMS165orLater() && !(ServerConfig.IsGMS() && ServerConfig.GetVersion() == 73)) {
                if (chr.getBlessOfFairyOrigin() != null) {
                    data.Encode1(1);
                    data.EncodeStr(chr.getBlessOfFairyOrigin());
                } else {
                    data.Encode1(0);
                }
            }

            // 祝福系統
            if (ServerConfig.JMS194orLater()) {
                // 女王の祝福 max 24
                data.Encode1(0); // not 0, EncodeStr
                // ???
                data.Encode1(0); // not 0, EncodeStr
            }

            if (ServerConfig.IsTWMS()) {
                data.Encode8(0);
            }
        }
        // 0x2 (<< 1) v165-v194
        if ((datamask & 0x02) > 0) {
            data.EncodeBuffer(GW_CharacterStat.EncodeMoney(chr));
            if (ServerConfig.IsJMS()
                    || ServerConfig.IsTWMS()
                    || ServerConfig.IsCMS()
                    || ServerConfig.IsTHMS()) {
                data.EncodeBuffer(GW_CharacterStat.EncodePachinko(chr));
            }
            if (ServerConfig.EMS89orLater()) {
                data.Encode1(0);
                data.Encode4(0);
            }
        }

        if ((datamask & 0x2000000) > 0) {
            // EMS89 0x8000000 || 0x08
            if (ServerConfig.KMS138orLater() && !ServerConfig.KMST391() || ServerConfig.EMS89orLater()) {
                data.Encode4(0);
            }
        }
        // 0x4 (<< 2), 0x100000, 0x4 [addInventoryInfo]
        if ((datamask & 0x04) > 0) {
            data.EncodeBuffer(InventoryInfo(chr, datamask));
        }

        if (ServerConfig.KMS127orLater() || ServerConfig.JMST110()) {
            if ((datamask & 0x1000000) > 0) {
                data.Encode4(0);
            }
        }

        if (ServerConfig.KMST391()) {
            if ((datamask & 0x2000000) > 0) {
                data.Encode4(0);
            }
        }

        if (ServerConfig.KMS197orLater()) {
            if ((datamask & 0x40000000L) > 0) {
                data.Encode4(0); // encode8, encode8
            }
        } else if (ServerConfig.KMS148orLater()) {
            if ((datamask & 0x4000000) > 0) {
                data.Encode4(0);
            }
        }

        if (ServerConfig.KMS119orLater() || ServerConfig.JMST110()) {
            if ((datamask & 0x800000) > 0) {
                data.Encode1(0);
            }
        }

        if (ServerConfig.EMS89orLater()) {
            if ((datamask & 0x4000000) > 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x100000000L) > 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x2000000) > 0) {
                data.Encode1(0);
            }
        }

        // 0x100 [addSkillInfo] v165 changed v186-v194
        if ((datamask & 0x0100) > 0) {
            data.EncodeBuffer(Structure.addSkillInfo(chr));
        }
        // 0x8000 [addCoolDownInfo] v165-v194
        if ((datamask & 0x8000) > 0) {
            data.EncodeBuffer(Structure.addCoolDownInfo(chr));
        }
        // 0x200 [addQuestInfo] changed v165,v186,v188,v194
        if ((datamask & 0x200) > 0) {
            data.EncodeBuffer(Structure.addQuestInfo(chr));
        }
        // 0x4000 QuestComplete v165-v194
        if ((datamask & 0x4000) > 0) {
            data.EncodeBuffer(Structure.addQuestComplete(chr));
        }
        // 0x400 MiniGameRecord v165-v194
        if ((datamask & 0x0400) > 0) {
            data.Encode2(0); // not 0 -> Encode4 x5
        }
        // 0x800 [addRingInfo] v165-v194
        if ((datamask & 0x0800) > 0) {
            data.EncodeBuffer(Structure.addRingInfo(chr)); // 2x3
        }
        // 0x1000 [addRocksInfo] v165-v188 changed v194
        if ((datamask & 0x1000) > 0) {
            data.EncodeBuffer(Structure.addRocksInfo(chr));
        }
        switch (ServerConfig.GetRegion()) {
            case KMS:
            case KMST: {
                if (ServerConfig.IsPreBB()) {
                    if ((datamask & 0x20000) > 0) {
                        data.Encode4(chr.getMonsterBookCover());
                    }
                    if ((datamask & 0x10000) > 0) {
                        data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                    }
                }
                if ((datamask & 0x40000) > 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }

                if (ServerConfig.IsPreBB()) {
                    if ((datamask & 0x80000) > 0) {
                        data.Encode2(0);
                    }
                }

                if (ServerConfig.IsPostBB()) {
                    if (ServerConfig.KMS197orLater()) {
                        if ((datamask & 0x200000000000L) > 0) {
                            data.Encode2(0);
                        }
                        byte unk_byte = 0;
                        data.Encode1(unk_byte); // unk
                        {
                            if (unk_byte != 0) {
                                if ((datamask & 0x10000000000L) > 0) {
                                    int loop_cnt = 0;
                                    data.Encode4(loop_cnt);
                                    for (int i = 0; i < loop_cnt; i++) {
                                        data.Encode4(0);
                                    }
                                }
                            }
                        }

                        if ((datamask & 0x100000000000L) > 0) {
                            data.Encode4(0); // loop, Encode4, Encode4
                        }
                    }
                    if ((datamask & 0x200000) > 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(GW_WildHunterInfo.Encode());
                    }
                    if (ServerConfig.KMS197orLater()) {
                        if ((datamask & 0x80000000000L) > 0) {
                            // KMS197 005D7A49, 005C1DA0
                            // v270 == 10000 || v270 == 10100 || v270 == 10110 || v270 == 10111 || v270 == 10112
                        }
                    }
                    if ((datamask & 0x400000) > 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                    }
                    if ((datamask & 0x4000000) > 0) {
                        if (ServerConfig.KMS138orLater()) {
                            data.Encode2(0);
                        }
                    }
                    if ((datamask & 0x20000000) > 0) {
                        if (ServerConfig.KMS148orLater()) {
                            for (int i = 0; i < (4 + 4 + 3 + 2); i++) {
                                data.Encode4(0);
                            }
                        }
                    }
                    if ((datamask & 0x10000000) > 0) {
                        if (ServerConfig.KMS160orLater()) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                        } else if (ServerConfig.KMS148orLater()) {
                            data.Encode1(0);
                            data.Encode2(0);
                            data.Encode2(0);
                        }
                    }
                    if ((datamask & 0x80000000L) > 0) {
                        if (ServerConfig.KMS149orLater()) {
                            data.Encode2(0);
                        }
                    }
                    if (ServerConfig.KMS197orLater()) {
                        data.Encode4(0);
                    } else if (ServerConfig.KMS160orLater()) {
                        data.Encode4(0);
                        data.Encode4(0);
                    }
                    if (ServerConfig.KMS197orLater()) {
                        data.Encode1(0);
                        if ((datamask & 0x0100000000L) > 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                        }
                        if ((datamask & 0x0200000000L) > 0) {
                            data.Encode1(0);
                            data.Encode2(0);
                        }
                        if ((datamask & 0x0400000000L) > 0) {
                            data.Encode1(0);
                        }
                        if ((datamask & 0x0800000000L) > 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode1(0);
                        }
                        if ((datamask & 0x1000000000L) > 0) {
                            data.Encode2(0);
                            data.Encode2(0);
                        }
                        if ((datamask & 0x2000000000L) > 0) {
                            data.Encode1(0);
                        }
                        if ((datamask & 0x4000000000L) > 0) {
                            // 005CC6C0
                            {
                                data.EncodeStr("KMS197TEST");
                                data.Encode4(0);
                                data.Encode4(0);
                                data.Encode4(0);
                                data.Encode4(0);
                                data.Encode4(0);
                                data.Encode1(0);
                                data.Encode4(0);
                                data.Encode4(0);
                                data.Encode4(0);
                            }
                            data.Encode4(0);
                            data.Encode4(0);
                        }
                        if ((datamask & 0x8000000000L) > 0) {
                            data.Encode1(0);
                        }
                        if ((datamask & 0x40000000000L) > 0) {
                            data.Encode4(0);
                            data.Encode8(0);
                            data.Encode4(0);
                        }
                    }
                }
                break;
            }
            case TWMS: {
                if (ServerConfig.IsPreBB()) {
                    if (ServerConfig.TWMS94orLater()) {
                        if ((datamask & 0x20000) > 0) {
                            data.Encode4(chr.getMonsterBookCover());
                        }
                        if ((datamask & 0x10000) > 0) {
                            data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                        }
                        if ((datamask & 0x40000) > 0) {
                            data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                        }
                        if ((datamask & 0x80000) > 0) {
                            data.Encode2(0);
                        }

                        if (ServerConfig.TWMS121orLater()) {
                            if ((datamask & 0x200000) > 0) {
                                data.Encode2(0);
                            }
                            if ((datamask & 0x400000) > 0) {
                                data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                            }
                        } else {
                            // TWMS v94
                            if ((datamask & 0x100000) > 0) {
                                data.Encode2(0);
                            }
                        }
                    }
                } else {
                    if ((datamask & 0x40000) > 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }
                    if ((datamask & 0x200000) > 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(GW_WildHunterInfo.Encode());
                    }
                    if ((datamask & 0x400000) > 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                    }
                    if ((datamask & 0x800000) > 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x1000000) > 0) {
                        data.Encode2(0);
                    }
                }
                break;
            }
            case CMS: {
                if ((datamask & 0x20000) > 0) {
                    data.Encode4(chr.getMonsterBookCover());
                }
                if ((datamask & 0x10000) > 0) {
                    data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                }
                if ((datamask & 0x40000) > 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if ((datamask & 0x80000) > 0) {
                    data.Encode2(0);
                }
                // 宅配?
                if ((datamask & 0x200000) > 0) {
                    data.Encode2(0);
                }
                if ((datamask & 0x400000) > 0) {
                    data.Encode2(0); // not 0, Encode2, EncodeBuffer20
                }
                break;
            }
            case MSEA: {
                if ((datamask & 0x20000) > 0) {
                    data.Encode4(chr.getMonsterBookCover());
                }
                if ((datamask & 0x10000) > 0) {
                    data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                }
                if ((datamask & 0x40000) > 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if ((datamask & 0x80000) > 0) {
                    data.Encode2(0);
                }
                if ((datamask & 0x200000) > 0) {
                    data.Encode2(0);
                }
                break;
            }
            case GMS: {
                if (ServerConfig.GMS95orLater()) {
                    // NewYearCardRecord
                    if ((datamask & 0x40000) > 0) {
                        data.Encode2(0);
                    }
                    // InitQuestExFromRawStr
                    if ((datamask & 0x80000) > 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x200000) > 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(GW_WildHunterInfo.Encode());
                    }
                    // 0x400000 QuestCompleteOld
                    if ((datamask & 0x400000) > 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x800000) > 0) {
                        // VisitorQuestLog
                        data.Encode2(0);
                    }
                } else if (ServerConfig.GMS84orLater()) {
                    if ((datamask & 0x20000) > 0) {
                        data.Encode4(chr.getMonsterBookCover());
                    }
                    if ((datamask & 0x10000) > 0) {
                        data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                    }
                    if ((datamask & 0x40000) > 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x80000) > 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }
                    if ((datamask & 0x100000) > 0) {
                        data.Encode2(0);
                    }
                    if (ServerConfig.GMS91orLater()) {
                        if ((datamask & 0x200000) > 0) {
                            data.Encode2(0);
                        }
                    }
                } else {
                    if ((datamask & 0x20000) > 0) {
                        data.Encode4(chr.getMonsterBookCover());
                    }
                    if (ServerConfig.GMS72orLater()) {
                        if ((datamask & 0x10000) > 0) {
                            data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                        }
                    }
                    if (ServerConfig.GMS65orLater()) {
                        if ((datamask & 0x40000) > 0) {
                            data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                        }
                    }
                    if (ServerConfig.GMS72orLater()) {
                        if ((datamask & 0x80000) > 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x100000) > 0) {
                            data.Encode2(0);
                        }
                    }
                }
                break;
            }
            case EMS: {
                if ((datamask & 0x40000) > 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if (ServerConfig.IsPostBB()) {
                    if ((datamask & 0x1000000) > 0) {
                        data.Encode2(0); // unknown
                    }
                    if ((datamask & 0x200000) > 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(GW_WildHunterInfo.Encode());
                    }
                    if ((datamask & 0x400000) > 0) {
                        data.Encode2(0);
                    }
                    if (ServerConfig.EMS89orLater()) {
                        if ((datamask & 0x10000000L) > 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x80000000L) > 0) {
                            for (int i = 0; i < (4 + 4 + 3 + 2); i++) {
                                data.Encode4(0);
                            }
                        }
                        if ((datamask & 0x40000000L) > 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                        }
                        if ((datamask & 0x200000000L) > 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x400000000L) > 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                        }
                        if ((datamask & 0x0800000000L) > 0) {
                            data.Encode1(0);
                            data.Encode2(0);
                        }
                        if ((datamask & 0x1000000000L) > 0) {
                            data.Encode1(0);
                        }
                        if ((datamask & 0x2000000000L) > 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode1(0);
                        }
                        if ((datamask & 0x8000000000L) > 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode8(0);
                        }
                        data.Encode1(0);
                        data.Encode1(0);
                    }
                    if ((datamask & 0x800000) > 0) {
                        data.Encode2(0);
                    }

                } else {
                    if ((datamask & 0x80000) > 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x800) > 0) {
                        data.Encode2(0);
                    }
                }
                break;
            }
            case VMS: {
                if ((datamask & 0x20000) > 0) {
                    data.Encode4(chr.getMonsterBookCover());
                }
                if ((datamask & 0x10000) > 0) {
                    data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                }
                if ((datamask & 0x40000) > 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                break;
            }
            case BMS: {
                if ((datamask & 0x40000) > 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if ((datamask & 0x80000) > 0) {
                    data.Encode2(0);
                }
                if ((datamask & 0x20000) > 0) {
                    data.Encode4(chr.getMonsterBookCover());
                }
                if ((datamask & 0x10000) > 0) {
                    data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                }
                break;
            }
            case THMS: {
                // THMS96
                if (ServerConfig.IsPostBB()) {
                    if ((datamask & 0x40000) > 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x200000) > 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(GW_WildHunterInfo.Encode());
                    }
                    // 0x400000 QuestCompleteOld
                    if ((datamask & 0x400000) > 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                    }
                    // 0x800000
                    if ((datamask & 0x800000) > 0) {
                        data.Encode2(0); // not 0, Encode2, Encode2
                    }
                    break;
                }
                // PreBB
                if ((datamask & 0x20000) > 0) {
                    data.Encode4(chr.getMonsterBookCover());
                }
                if ((datamask & 0x10000) > 0) {
                    data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                }
                if ((datamask & 0x40000) > 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if ((datamask & 0x80000) > 0) {
                    data.Encode2(0);
                }
                if ((datamask & 0x200000) > 0) {
                    data.Encode2(0);
                }
                break;
            }
            case IMS: {
                if ((datamask & 0x40000) > 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if ((datamask & 0x200000) > 0 && (chr.getJob() / 100 == 33)) {
                    data.EncodeBuffer(GW_WildHunterInfo.Encode());
                }
                if ((datamask & 0x400000) > 0) {
                    data.Encode2(0);
                }
                break;
            }
            case JMS:
            case JMST:
            default: {
                // 0x7C JMS, Present v146-v194
                if (ServerConfig.IsJMS()) {
                    if ((datamask & 0x7C) > 0) {
                        data.Encode2(0); // not 0 -> Encode4, Encode4, Encode2, EncodeStr
                    }
                }
                if (ServerConfig.JMS146orLater()) {
                    // 0x20000 JMS v146-v194
                    if ((datamask & 0x020000) > 0) {
                        data.Encode4(chr.getMonsterBookCover());
                    }
                    // 0x10000 JMS [addMonsterBookInfo] v165-v194
                    if ((datamask & 0x010000) > 0) {
                        data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                    }
                }
                if (ServerConfig.JMST110()) {
                    data.Encode4(0);
                    data.Encode2(0);
                    data.Encode4(0);
                    data.Encode4(0);
                } else {
                    if (ServerConfig.JMS194orLater()) {
                        // 0x10000000
                        if ((datamask & 0x10000000) > 0) {
                            data.Encode4(0);
                        }
                        // 0x20000000
                        if ((datamask & 0x20000000) > 0) {
                            data.Encode2(0); // not 0, Encode2
                        }
                    }
                }
                if (ServerConfig.JMS164orLater()) {
                    // 0x40000 (GMS 0x80000) [QuestInfoPacket] v165-v194
                    if ((datamask & 0x040000) > 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }
                    if (ServerConfig.IsPreBB()) {
                        if ((datamask & 0x80000) > 0) {
                            // 0x80000 JMS v165, v186, not in v188
                            data.Encode2(0);// not 0 -> Encode4, Encode2
                        }
                        if (ServerConfig.JMS186orLater()) {
                            if ((datamask & 0x200000) > 0) {
                                // 0x200000 VisitorQuestLog (GMS 0x800000)
                                data.Encode2(0); // not 0 -> Encode2, Encode2
                            }
                        }
                    }
                }
                // v188-v194
                if (ServerConfig.IsPostBB()) {
                    // 0x200000
                    if ((datamask & 0x200000) > 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(GW_WildHunterInfo.Encode());
                    }
                    // 0x400000 QuestCompleteOld
                    if ((datamask & 0x400000) > 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                    }

                    if (ServerConfig.JMST110()) {
                        if ((datamask & 0x2000000) > 0) {
                            data.Encode2(0);
                        }
                    } else {
                        // 0x800000
                        if ((datamask & 0x800000) > 0) {
                            data.Encode2(0); // not 0, Encode2, Encode2
                        }
                    }
                }
                break;
            }
        }

        return data.get().getBytes();
    }

    public static final byte[] InventoryInfo(MapleCharacter chr, long datamask) {
        ServerPacket data = new ServerPacket();
        // アイテム欄の数
        // v165-v194
        if ((datamask & 0x80) > 0) {
            data.Encode1(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit()); // 0x04
            data.Encode1(chr.getInventory(MapleInventoryType.USE).getSlotLimit()); // 0x08
            data.Encode1(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit()); // 0x10
            data.Encode1(chr.getInventory(MapleInventoryType.ETC).getSlotLimit()); // 0x20
            data.Encode1(chr.getInventory(MapleInventoryType.CASH).getSlotLimit()); // 0x40
        }

        // v165-v194 OK
        if (ServerConfig.JMS165orLater()
                && !(ServerConfig.IsGMS() && ServerConfig.GetVersion() == 73)
                && !(ServerConfig.IsEMS() && ServerConfig.GetVersion() == 55)) {
            // 0x100000
            if ((datamask & 0x100000) > 0) {
                data.Encode4(0);
                data.Encode4(0);
            }
        }

        // 装備
        if ((datamask & 0x04) > 0) {
            MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
            Collection<IItem> equippedC = iv.list();
            List<Item> equipped = new ArrayList<Item>(equippedC.size());
            for (IItem item : equippedC) {
                equipped.add((Item) item);
            }
            Collections.sort(equipped);

            // 装備済みアイテム
            for (Item item : equipped) {
                if (item.getPosition() < 0 && item.getPosition() > -100) {
                    data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                    data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
                }
            }
            data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            // 装備済みアバター?
            for (Item item : equipped) {
                if (item.getPosition() <= -100 && item.getPosition() > -1000) {
                    data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                    data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
                }
            }
            data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            // 装備
            iv = chr.getInventory(MapleInventoryType.EQUIP);
            for (IItem item : iv.list()) {
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            // 装備済み -1000
            if (ServerConfig.JMS180orLater() || ServerConfig.GMS83orLater()) {
                for (Item item : equipped) {
                    if (item.getPosition() <= -1000 && item.getPosition() > -1100) {
                        data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                        data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
                    }
                }
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            }
            // 装備済み -1100
            if (ServerConfig.IsPostBB()) {
                for (Item item : equipped) {
                    if (item.getPosition() <= -1100 && item.getPosition() > -1200) {
                        data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                        data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
                    }
                }
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            }
            if (ServerConfig.EMS89orLater()) {
                data.Encode1(0); // 00527A25
                data.Encode2(0);
            }
            // カンナ?
            if (ServerConfig.KMS127orLater() || ServerConfig.JMS302orLater() || ServerConfig.JMST110()) {
                if (ServerConfig.JMS302orLater()) {
                    for (Item item : equipped) {
                        if (item.getPosition() <= -1500 && item.getPosition() > -1600) {
                            data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                            data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
                        }
                    }
                    data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
                }
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            }
            if (ServerConfig.JMS308orLater() || ServerConfig.KMS197orLater() || ServerConfig.EMS89orLater()) {
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            }
            if (ServerConfig.KMS197orLater()) {
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            }
        }

        // 消費
        if ((datamask & 0x08) > 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.USE).list()) {
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Consume));
        }
        // 設置
        if ((datamask & 0x10) > 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.SETUP).list()) {
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Install));
        }
        // ETC
        if ((datamask & 0x20) > 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.ETC).list()) {
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Etc));
        }
        // ポイントアイテム
        if ((datamask & 0x40) > 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.CASH).list()) {
                data.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Cash));
        }

        if (ServerConfig.KMS197orLater()) {
            for (int i = 3; i <= 4; i++) { // from 3 to 4
                data.Encode4(-1);
            }
            return data.get().getBytes();
        }

        // 不明
        if (ServerConfig.JMS194orLater()) {
            // func 004FB8B0
            data.Encode4(-1); // not -1, Encode4, Encode4 not -1, Encode4, end  Encode4(-1)
        }

        return data.get().getBytes();
    }

    public static byte[] Encode_302_1(MapleCharacter chr, long datamask) {
        ServerPacket data = new ServerPacket();
        // 008ABA10
        data.Encode8(datamask);
        data.Encode1(0); // nCombatOrders
        data.Encode1(0); // not 0, Encode4
        data.Encode4(0); // not 0, Encode4, Encode8
        data.Encode1(0); // not 0, Encode1, Encode4, Encode8...

        if ((datamask & 0x01) > 0) {
            // キャラクター情報
            data.EncodeBuffer(GW_CharacterStat.Encode(chr));
            // 友達リストの上限
            data.Encode1(chr.getBuddylist().getCapacity());
            // 精霊の祝福
            if (chr.getBlessOfFairyOrigin() != null) {
                data.Encode1(1);
                data.EncodeStr(chr.getBlessOfFairyOrigin());
            } else {
                data.Encode1(0);
            }
            // 女王の祝福
            data.Encode1(0); // not 0, EncodeStr
            data.Encode1(0); // not 0, EncodeStr
        }
        if ((datamask & 0x02) > 0) {
            data.EncodeBuffer(GW_CharacterStat.EncodeMoney(chr));
            data.EncodeBuffer(GW_CharacterStat.EncodePachinko(chr));
            data.EncodeZeroBytes(12); // unknown
        }
        if ((datamask & 0x08) > 0 || (datamask & 0x2000000) > 0) {
            data.Encode4(0);
        }
        if ((datamask & 0x04) > 0) {
            data.EncodeBuffer(InventoryInfo(chr, datamask));
        }
        if ((datamask & 0x1000000) > 0) {
            data.Encode4(0);
        }
        if ((datamask & 0x40000000) > 0) {
            data.Encode4(0);
        }
        if ((datamask & 0x800000) > 0) {
            data.Encode1(0);
        }
        if ((datamask & 0x100) > 0) {
            data.EncodeBuffer(Structure.addSkillInfo(chr));
        }
        if ((datamask & 0x8000) > 0) {
            data.EncodeBuffer(Structure.addCoolDownInfo(chr));
        }
        if ((datamask & 0x400) > 0) {
            data.Encode2(0); // not 0 -> Encode4 x5
        }
        if ((datamask & 0x800) > 0) {
            data.EncodeBuffer(Structure.addRingInfo(chr));
        }
        if ((datamask & 0x1000) > 0) {
            data.EncodeBuffer(Structure.addRocksInfo(chr));
        }
        if ((datamask & 0x7C) > 0) {
            data.Encode2(0); // not 0 -> Encode4, Encode4, Encode2, EncodeStr
        }
        if ((datamask & 0x20000) > 0) {
            data.Encode4(chr.getMonsterBookCover());
        }
        if ((datamask & 0x10000) > 0) {
            data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
        }

        if (ServerConfig.JMS308orLater()) {
            // JMS308
            if ((datamask & 0x20000000000L) > 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x40000000000L) > 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x200000000000L) > 0) {
                data.Encode2(0);
                data.Encode2(0);
            }
            if ((datamask & 0x800000000000L) > 0) {
                for (int i = 0; i < 10; i++) {
                    data.Encode1(0);
                }
            }
        } else {
            //JMS302
            if ((datamask & 0x8000000000L) > 0) {
                data.Encode4(0);
            }

            if ((datamask & 0x10000000000L) > 0) {
                data.Encode2(0); // 00546810
            }
            if ((datamask & 0x80000000000L) > 0) {
                data.Encode2(0); // 0054B730
                data.Encode2(0);
            }
            if ((datamask & 0x100000000000L) > 0) {
                for (int i = 0; i < 10; i++) {
                    data.Encode1(0);
                }
            }
        }

        if ((datamask & 0x200000) > 0 && (chr.getJob() / 100 == 33)) {
            data.EncodeBuffer(GW_WildHunterInfo.Encode());
        }
        if ((datamask & 0x4000000) > 0) {
            data.Encode2(0);
        }
        if ((datamask & 0x20000000) > 0) {
            for (int i = 0; i < 13; i++) {
                // 4-4-3-2
                data.Encode4(0);
            }
        }
        if ((datamask & 0x10000000) > 0) {
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
        }
        if ((datamask & 0x80000000L) > 0) {
            data.Encode2(0);
        }
        if ((datamask & 0x100000000L) > 0) {
            data.Encode4(0);
            data.Encode4(0);
            if (ServerConfig.JMS308orLater()) {
                data.Encode4(0);
            }
        }
        if ((datamask & 0x200000000L) > 0) {
            if (ServerConfig.JMS308orLater()) {
                data.Encode1(0);
                data.Encode2(0);
            }
        }
        if (ServerConfig.JMS308orLater()) {
            // JMS308
            if ((datamask & 0x400000000L) > 0) {
                data.Encode1(0);
            }
            if ((datamask & 0x800000000L) > 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode1(0);
            }
            if ((datamask & 0x2000000000L) > 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.EncodeZeroBytes(8);
            }
            if ((datamask & 0x1000000000L) > 0) {
                data.Encode2(0);
            }
        } else {
            // JMS302
            if ((datamask & 0x400000000L) > 0) {
                data.Encode2(0);
            }
        }
        data.Encode4(0);
        data.Encode8(TestHelper.getTime(System.currentTimeMillis()));
        if ((datamask & 0x400000000000L) > 0) {
            if (ServerConfig.JMS308orLater()) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.EncodeZeroBytes(32);
            }
        }
        return data.get().getBytes();
    }

    public static byte[] Encode_302_2(MapleCharacter chr, long datamask) {
        ServerPacket data = new ServerPacket();
        // 00552C00
        data.Encode8(datamask);
        if ((datamask & 0x200) > 0) {
            data.EncodeBuffer(Structure.addQuestInfo(chr));
        }
        if ((datamask & 0x4000) > 0) {
            data.EncodeBuffer(Structure.addQuestComplete(chr));
        }
        if ((datamask & 0x40000) > 0) {
            data.EncodeBuffer(Structure.QuestInfoPacket(chr));
        }
        // 0x400000 QuestCompleteOld
        if ((datamask & 0x400000) > 0) {
            data.Encode2(0);
        }
        return data.get().getBytes();
    }
}
