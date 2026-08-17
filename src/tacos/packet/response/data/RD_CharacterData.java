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
package tacos.packet.response.data;

import odin.client.MapleCharacter;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventory;
import odin.client.inventory.MapleInventoryType;
import tacos.config.Region;
import tacos.config.Config;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.response.struct.Structure;
import tacos.shared.SharedDate;

/**
 *
 * @author Riremito
 */
public class RD_CharacterData {

    // CharacterData::Decode
    public static byte[] Encode(MapleCharacter chr) {
        return Encode(chr, -1);
    }

    // CharacterData::Decode
    public static byte[] Encode(MapleCharacter chr, long datamask) {
        ServerPacket data = new ServerPacket();

        if (Region.KMSB.check() || Config.LessOrEqual(Region.KMS, 46) || Config.LessOrEqual(Region.JMS, 131)) {
            data.Encode2((short) datamask); // statmask
        } else {
            // KMS51
            data.Encode8(datamask); // statmask
        }

        data.Encode1(0, Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)); // nCombatOrders
        data.Encode1(0, Config.GreaterOrEqual(Region.GMS, 116));

        if (Config.GreaterOrEqual(Region.KMS, 197)) {
            for (int i = 0; i < 3; i++) {
                data.Encode4(0);
            }
        }

        data.Encode1(0, Config.GreaterOrEqual(Region.KMS, 138) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89));
        data.Encode4(0, Config.GreaterOrEqual(Region.KMS, 119) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89));
        data.Encode1(0, Config.PostBB()); // not 0, Encode1, Encode4(size), EncodeBuffer8, Encode4(size), EncodeBuffer8

        if ((datamask & 2) != 0) {
            data.Encode4(0, Region.THMS.check() || Region.VMS.check());
        }
        if ((datamask & 1) != 0) {
            data.EncodeBuffer(RD_CharacterStat.Encode(chr));
            if (!Region.KMSB.check()) {
                data.Encode1(chr.getBuddylist().getCapacity());
                data.Encode1(0, Config.GreaterOrEqual(Region.EMS, 89));
                data.Encode1(0, Config.GreaterOrEqual(Region.EMS, 89));
                data.Encode1(chr.getBlessOfFairyOrigin() != null ? 1 : 0, Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 67) || Config.GreaterOrEqual(Region.JMS, 165) || Config.GreaterOrEqual(Region.CMS, 74) || Config.GreaterOrEqual(Region.TWMS, 96) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 55));
                if (chr.getBlessOfFairyOrigin() != null) {
                    data.EncodeStr(chr.getBlessOfFairyOrigin(), Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 67) || Config.GreaterOrEqual(Region.JMS, 165) || Config.GreaterOrEqual(Region.CMS, 74) || Config.GreaterOrEqual(Region.TWMS, 96) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 55));
                }
                data.Encode1(0, Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 76)); // not 0, EncodeStr
                data.Encode1(0, Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 76)); // not 0, EncodeStr
                data.Encode8(0, Region.TWMS.check());
            }
        }
        if ((datamask & 2) != 0) {
            data.EncodeBuffer(RD_CharacterStat.EncodeMoney(chr));
            data.EncodeBuffer(RD_CharacterStat.EncodePachinko(chr), Region.JMS.check() || Region.JMST.check() || Region.TWMS.check() || Region.CMS.check() || Region.THMS.check());
            data.Encode1(0, Config.GreaterOrEqual(Region.EMS, 89));
            data.Encode4(0, Config.GreaterOrEqual(Region.EMS, 89));

        }
        if ((datamask & 0x2000000L) != 0) {
            // EMS89 0x8000000 || 0x08
            // GMS126 0x2000000 || 0x08
            data.Encode4(0, Config.GreaterOrEqual(Region.KMS, 138) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89));

        }
        if ((datamask & (0x40000L | 0x8)) != 0) {
            data.Encode4(0, Config.GreaterOrEqual(Region.GMS, 126));
        }
        if ((datamask & 0x4) != 0) {
            data.EncodeBuffer(InventoryInfo(chr, datamask));
        }
        if ((datamask & 0x1000000L) != 0) {
            data.Encode4(0, Config.GreaterOrEqual(Region.KMS, 127) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148));
        }
        if ((datamask & 0x2000000L) != 0) {
            data.Encode4(0, Config.GreaterOrEqual(Region.KMST, 391));
        }
        if ((datamask & 0x4000000L) != 0) {
            data.Encode4(0, Config.Between(Region.KMS, 148, 183));
        }
        if ((datamask & 0x40000000L) != 0) {
            data.Encode4(0, Config.GreaterOrEqual(Region.KMS, 197) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148)); // encode8, encode8
        }
        if ((datamask & 0x800000L) != 0) {
            data.Encode1(0, Config.GreaterOrEqual(Region.KMS, 119) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148));
        }
        if ((datamask & 0x4000000L) != 0) {
            data.Encode4(0, Config.GreaterOrEqual(Region.EMS, 89));
        }
        if ((datamask & 0x100000000L) != 0) {
            data.Encode4(0, Config.GreaterOrEqual(Region.EMS, 89));
        }
        if ((datamask & 0x2000000L) != 0) {
            data.Encode1(0, Config.GreaterOrEqual(Region.EMS, 89));
        }
        if ((datamask & 0x40000000L) != 0) {
            data.Encode4(0, Config.GreaterOrEqual(Region.GMS, 111)); // encode4, encode8
        }
        if ((datamask & 0x40000000000L) != 0) {
            data.Encode4(0, Config.GreaterOrEqual(Region.GMS, 111)); // encode8, encode8
        }
        if ((datamask & 0x20000000L) != 0) {
            data.Encode1(0, Config.GreaterOrEqual(Region.GMS, 111));
        }
        if ((datamask & 0x100L) != 0) {
            data.EncodeBuffer(Structure.addSkillInfo(chr));
        }
        if ((datamask & 0x8000L) != 0) {
            data.EncodeBuffer(chr.getCoolTime().getBufferForLogin(System.currentTimeMillis()), !Config.LessOrEqual(Region.KMS, 3));
        }
        if ((datamask & 0x200L) != 0) {
            data.EncodeBuffer(Structure.addQuestInfo(chr));
        }
        if ((datamask & 0x4000L) != 0) {
            data.EncodeBuffer(Structure.addQuestComplete(chr), !Config.LessOrEqual(Region.KMS, 1));
        }
        if ((datamask & 0x400L) != 0) {
            data.Encode2(0); // MiniGameRecord, not 0 -> Encode4 x5
        }
        if ((datamask & 0x800L) != 0) {
            data.EncodeBuffer(Structure.addRingInfo(chr)); // 2x3
        }
        if ((datamask & 0x1000L) != 0) {
            data.EncodeBuffer(Structure.addRocksInfo(chr));
        }

        switch (Config.REGION) {
            case KMS:
            case KMST: {
                if (Config.PreBB()) {
                    // not in KMS43
                    if (Config.GreaterOrEqual(Region.KMS, 51)) {
                        if ((datamask & 0x20000) != 0) {
                            data.Encode4(chr.getMonsterBook().getCover());
                        }
                        if ((datamask & 0x10000) != 0) {
                            data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                        }
                    }
                }
                if (Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.KMST, 330)) {
                    if ((datamask & 0x40000) != 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }
                }
                // not in KMS55
                if (Config.Between(Region.KMS, 65, 95)) {
                    if ((datamask & 0x80000L) != 0) {
                        data.Encode2(0);
                    }
                }
                if (Config.PostBB()) {
                    if (Config.GreaterOrEqual(Region.KMS, 197)) {
                        if ((datamask & 0x200000000000L) != 0) {
                            data.Encode2(0);
                        }
                        byte unk_byte = 0;
                        data.Encode1(unk_byte); // unk
                        {
                            if (unk_byte != 0) {
                                if ((datamask & 0x10000000000L) != 0) {
                                    int loop_cnt = 0;
                                    data.Encode4(loop_cnt);
                                    for (int i = 0; i < loop_cnt; i++) {
                                        data.Encode4(0);
                                    }
                                }
                            }
                        }
                        if ((datamask & 0x100000000000L) != 0) {
                            data.Encode4(0); // loop, Encode4, Encode4
                        }
                    }
                    if ((datamask & 0x200000) != 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                    }
                    if (Config.GreaterOrEqual(Region.KMS, 197)) {
                        if ((datamask & 0x80000000000L) != 0) {
                            // KMS197 005D7A49, 005C1DA0
                            // v270 == 10000 || v270 == 10100 || v270 == 10110 || v270 == 10111 || v270 == 10112
                        }
                    }
                    if ((datamask & 0x400000) != 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                    }
                    if ((datamask & 0x4000000) != 0) {
                        if (Config.GreaterOrEqual(Region.KMS, 138) || Config.GreaterOrEqual(Region.KMST, 391)) {
                            data.Encode2(0);
                        }
                    }
                    if ((datamask & 0x20000000) != 0) {
                        if (Config.GreaterOrEqual(Region.KMS, 148)) {
                            for (int i = 0; i < (4 + 4 + 3 + 2); i++) {
                                data.Encode4(0);
                            }
                        }
                    }
                    if ((datamask & 0x10000000) != 0) {
                        if (Config.GreaterOrEqual(Region.KMS, 160)) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                        } else if (Config.GreaterOrEqual(Region.KMS, 148)) {
                            data.Encode1(0);
                            data.Encode2(0);
                            data.Encode2(0);
                        }
                    }
                    if ((datamask & 0x80000000L) != 0) {
                        if (Config.GreaterOrEqual(Region.KMS, 149)) {
                            data.Encode2(0);
                        }
                    }
                    if (Config.GreaterOrEqual(Region.KMS, 197)) {
                        data.Encode4(0);
                        data.Encode1(0);
                    } else if (Config.GreaterOrEqual(Region.KMS, 169)) {
                        // none?
                    } else if (Config.GreaterOrEqual(Region.KMS, 160)) {
                        data.Encode4(0);
                        data.Encode4(0);
                    }
                    if (Config.GreaterOrEqual(Region.KMS, 169)) {
                        // high
                        if ((datamask & 0x100000000L) != 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                        }
                        if ((datamask & 0x200000000L) != 0) {
                            data.Encode1(0);
                            data.Encode2(0);
                        }
                        if ((datamask & 0x400000000L) != 0) {
                            data.Encode1(0);
                        }
                        if ((datamask & 0x800000000L) != 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode1(0);
                        }
                    }
                    if (Config.GreaterOrEqual(Region.KMS, 197)) {
                        if ((datamask & 0x1000000000L) != 0) {
                            data.Encode2(0);
                            data.Encode2(0);
                        }
                        if ((datamask & 0x2000000000L) != 0) {
                            data.Encode1(0);
                        }
                        if ((datamask & 0x4000000000L) != 0) {
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
                        if ((datamask & 0x8000000000L) != 0) {
                            data.Encode1(0);
                        }
                        if ((datamask & 0x40000000000L) != 0) {
                            data.Encode4(0);
                            data.Encode8(0);
                            data.Encode4(0);
                        }
                    }
                }
                break;
            }
            case TWMS: {
                if (Config.PreBB()) {
                    if (Config.GreaterOrEqual(Region.TWMS, 94)) {
                        if ((datamask & 0x20000) != 0) {
                            data.Encode4(chr.getMonsterBook().getCover());
                        }
                        if ((datamask & 0x10000) != 0) {
                            data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                        }
                        if ((datamask & 0x40000L) != 0) {
                            data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                        }
                        if ((datamask & 0x80000L) != 0) {
                            data.Encode2(0);
                        }
                        if (Config.GreaterOrEqual(Region.TWMS, 121)) {
                            if ((datamask & 0x200000L) != 0) {
                                data.Encode2(0);
                            }
                            if ((datamask & 0x400000L) != 0) {
                                data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                            }
                        } else {
                            // TWMS v94
                            if ((datamask & 0x100000L) != 0) {
                                data.Encode2(0);
                            }
                        }
                    }
                } else {
                    if ((datamask & 0x40000L) != 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }
                    if ((datamask & 0x200000L) != 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                    }
                    if ((datamask & 0x400000L) != 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                    }
                    if (Config.GreaterOrEqual(Region.TWMS, 148)) {
                        if ((datamask & 0x4000000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x20000000L) != 0) {
                            for (int i = 0; i < (4 + 4 + 3 + 2); i++) {
                                data.Encode4(0);
                            }
                        }
                        if ((datamask & 0x10000000L) != 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                        }
                        if ((datamask & 0x80000000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x100000000L) != 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                        }
                        if ((datamask & 0x200000000L) != 0) {
                            data.EncodeZeroBytes(84);
                            data.Encode1(0);
                        }
                        if ((datamask & 0x80000000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x100000000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x400000000L) != 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.EncodeZeroBytes(32);
                        }
                        break;
                    }
                    if ((datamask & 0x800000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x1000000L) != 0) {
                        data.Encode2(0);
                    }
                }
                break;
            }
            case CMS: {
                if (Config.PreBB()) {
                    if ((datamask & 0x20000) != 0) {
                        data.Encode4(chr.getMonsterBook().getCover());
                    }
                    if ((datamask & 0x10000) != 0) {
                        data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                    }
                    if ((datamask & 0x40000L) != 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }
                    if ((datamask & 0x80000L) != 0) {
                        data.Encode2(0);
                    }
                    // 宅配?
                    if ((datamask & 0x200000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x400000L) != 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer20
                    }
                } else {
                    // CMS104, same as TWMS148
                    if ((datamask & 0x40000) != 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }
                    if ((datamask & 0x200000) != 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                    }
                    if ((datamask & 0x400000) != 0) {
                        data.Encode2(0);
                    }
                    if (Config.Equal(Region.CMS, 88)) {
                        if ((datamask & 0x800000) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x1000000) != 0) {
                            data.Encode2(0);
                        }
                        break;
                    }
                    if ((datamask & 0x4000000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x20000000L) != 0) {
                        for (int i = 0; i < (4 + 4 + 3 + 2); i++) {
                            data.Encode4(0);
                        }
                    }
                    if ((datamask & 0x10000000L) != 0) {
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode4(0);
                    }
                    if ((datamask & 0x80000000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x100000000L) != 0) {
                        data.Encode4(0);
                        data.Encode4(0);
                    }
                    if ((datamask & 0x200000000L) != 0) {
                        data.EncodeZeroBytes(84);
                        data.Encode1(0);
                    }
                    if ((datamask & 0x80000000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x100000000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x400000000L) != 0) {
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode4(0);
                        data.EncodeZeroBytes(32);
                    }
                }
                break;
            }
            case MSEA: {
                if ((datamask & 0x20000) != 0) {
                    data.Encode4(chr.getMonsterBook().getCover());
                }
                if ((datamask & 0x10000) != 0) {
                    data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                }
                if ((datamask & 0x40000L) != 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if ((datamask & 0x80000L) != 0) {
                    data.Encode2(0);
                }
                if ((datamask & 0x200000L) != 0) {
                    data.Encode2(0);
                }
                break;
            }
            case GMS:
            case GMST: {
                if (Config.GreaterOrEqual(Region.GMS, 126)) {
                    if ((datamask & 0x00020000) != 0) {
                        data.Encode4(0);
                    }
                    if ((datamask & 0x00010000) != 0) {
                        data.Encode1(0);
                        data.Encode2(0);
                    }
                    if ((datamask & 0x100000000000L) != 0) {
                        data.Encode4(0);
                    }
                    if ((datamask & 0x200000000000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x4000000000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x00040000) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x00200000) != 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                    }
                    if ((datamask & 0x00400000) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x04000000) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x20000000L) != 0) {
                        int unk_loop_cnt = (4 + 4 + 3 + 2);
                        for (int i = 0; i < unk_loop_cnt; i++) {
                            data.Encode4(0);
                        }
                    }
                    if ((datamask & 0x10000000L) != 0) {
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode4(0);
                    }
                    if ((datamask & 0x80000000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x100000000L) != 0) {
                        data.Encode4(0);
                        data.Encode4(0);
                    }
                    if ((datamask & 0x200000000L) != 0) {
                        data.Encode1(0);
                        data.Encode2(0);
                    }
                    if ((datamask & 0x400000000L) != 0) {
                        data.Encode1(0);
                    }
                    if ((datamask & 0x800000000L) != 0) {
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode1(0);
                    }
                    if ((datamask & 0x2000000000L) != 0) {
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode8(0);
                    }
                    if ((datamask & 0x1000000000000L) != 0) {
                        data.EncodeZeroBytes(84);
                        data.Encode1(0);
                    }
                    if ((datamask & 0x8000000000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x2000000000000L) != 0) {
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode4(0);
                        data.Encode4(0);
                        data.EncodeZeroBytes(32);
                    }
                    break;
                } else if (Config.GreaterOrEqual(Region.GMS, 95)) {
                    if (Config.GreaterOrEqual(Region.GMS, 111)) {
                        if ((datamask & 0x00020000) != 0) {
                            data.Encode4(0);
                        }
                        if ((datamask & 0x00010000) != 0) {
                            data.Encode1(0);
                            data.Encode2(0);
                        }
                        if ((datamask & 0x80000000L) != 0) {
                            data.Encode4(0);
                        }
                        if ((datamask & 0x100000000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x00040000) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x00080000) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x00400000) != 0 && (chr.getJob() / 100 == 33)) {
                            data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                        }
                        if ((datamask & 0x80000000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x4000000000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x20000000000L) != 0) {
                            int unk_loop_cnt = Config.GreaterOrEqual(Region.GMS, 116) ? (4 + 4 + 3 + 2) : (6 + 5 + 4 + 3);
                            for (int i = 0; i < unk_loop_cnt; i++) {
                                data.Encode4(0);
                            }
                        }
                        if (Config.GreaterOrEqual(Region.GMS, 116)) {
                            if ((datamask & 0x10000000000L) != 0) {
                                data.Encode4(0);
                                data.Encode4(0);
                                data.Encode4(0);
                                data.Encode4(0);
                            }
                            if ((datamask & 0x80000000000L) != 0) {
                                data.Encode2(0);
                            }
                            if ((datamask & 0x100000000000L) != 0) {
                                data.Encode4(0);
                                data.Encode4(0);
                            }
                            if ((datamask & 0x200000000000L) != 0) {
                                data.EncodeZeroBytes(84);
                                data.Encode1(0);
                            }
                            if ((datamask & 0x01000000) != 0) {
                                data.Encode2(0);
                            }
                            if ((datamask & 0x400000000000L) != 0) {
                                data.Encode4(0);
                                data.Encode4(0);
                                data.Encode4(0);
                                data.Encode4(0);
                                data.EncodeZeroBytes(32);
                            }
                            break;
                        }
                        // GMS111
                        if ((datamask & 0x10000000000L) != 0) {
                            data.Encode1(0);
                            data.Encode2(0);
                            data.Encode2(0);
                        }
                        if ((datamask & 0x01000000) != 0) {
                            data.Encode2(0);
                        }
                        break;
                    }
                    // NewYearCardRecord
                    if ((datamask & 0x00040000) != 0) {
                        data.Encode2(0);
                    }
                    // InitQuestExFromRawStr
                    if ((datamask & 0x00080000) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x00200000) != 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                    }
                    // 0x400000 QuestCompleteOld
                    if ((datamask & 0x00400000) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x00800000) != 0) {
                        // VisitorQuestLog
                        data.Encode2(0);
                    }
                    break;
                } else if (Config.GreaterOrEqual(Region.GMS, 83)) {
                    if ((datamask & 0x20000) != 0) {
                        data.Encode4(chr.getMonsterBook().getCover());
                    }
                    if ((datamask & 0x10000) != 0) {
                        data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                    }
                    if ((datamask & 0x40000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x80000L) != 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }
                    if ((datamask & 0x100000L) != 0) {
                        data.Encode2(0);
                    }
                    if (Config.GreaterOrEqual(Region.GMS, 91)) {
                        if ((datamask & 0x200000L) != 0) {
                            data.Encode2(0);
                        }
                    }
                    break;
                } else {
                    if ((datamask & 0x20000) != 0) {
                        data.Encode4(chr.getMonsterBook().getCover());
                    }
                    if (Config.GreaterOrEqual(Region.GMS, 68)) {
                        if ((datamask & 0x10000) != 0) {
                            data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                        }
                    }
                    if (Config.GreaterOrEqual(Region.GMS, 65)) {
                        if ((datamask & 0x40000L) != 0) {
                            data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                        }
                    }
                    if (Config.GreaterOrEqual(Region.GMS, 68)) {
                        if ((datamask & 0x80000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x100000L) != 0) {
                            data.Encode2(0);
                        }
                    }
                }
                break;
            }
            case EMS: {
                if ((datamask & 0x40000L) != 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if (Config.PostBB()) {
                    if ((datamask & 0x1000000L) != 0) {
                        data.Encode2(0); // unknown
                    }
                    if ((datamask & 0x200000L) != 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                    }
                    if ((datamask & 0x400000L) != 0) {
                        data.Encode2(0);
                    }
                    if (Config.GreaterOrEqual(Region.EMS, 89)) {
                        if ((datamask & 0x10000000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x80000000L) != 0) {
                            for (int i = 0; i < (4 + 4 + 3 + 2); i++) {
                                data.Encode4(0);
                            }
                        }
                        if ((datamask & 0x40000000L) != 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                        }
                        if ((datamask & 0x200000000L) != 0) {
                            data.Encode2(0);
                        }
                        if ((datamask & 0x400000000L) != 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                        }
                        if ((datamask & 0x800000000L) != 0) {
                            data.Encode1(0);
                            data.Encode2(0);
                        }
                        if ((datamask & 0x1000000000L) != 0) {
                            data.Encode1(0);
                        }
                        if ((datamask & 0x2000000000L) != 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode1(0);
                        }
                        if ((datamask & 0x8000000000L) != 0) {
                            data.Encode4(0);
                            data.Encode4(0);
                            data.Encode8(0);
                        }
                        data.Encode1(0);
                        data.Encode1(0);
                    }
                    if ((datamask & 0x800000L) != 0) {
                        data.Encode2(0);
                    }
                } else {
                    if ((datamask & 0x80000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x800L) != 0) {
                        data.Encode2(0);
                    }
                }
                break;
            }
            case VMS: {
                if ((datamask & 0x20000) != 0) {
                    data.Encode4(chr.getMonsterBook().getCover());
                }
                if ((datamask & 0x10000) != 0) {
                    data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                }
                if ((datamask & 0x40000L) != 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                break;
            }
            case BMS: {
                if ((datamask & 0x40000L) != 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if ((datamask & 0x80000L) != 0) {
                    data.Encode2(0);
                }
                if ((datamask & 0x20000) != 0) {
                    data.Encode4(chr.getMonsterBook().getCover());
                }
                if ((datamask & 0x10000) != 0) {
                    data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                }
                break;
            }
            case THMS: {
                // THMS96
                if (Config.PostBB()) {
                    if ((datamask & 0x40000L) != 0) {
                        data.Encode2(0);
                    }
                    if ((datamask & 0x200000L) != 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                    }
                    // 0x400000 QuestCompleteOld
                    if ((datamask & 0x400000L) != 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                    }
                    // 0x800000
                    if ((datamask & 0x800000L) != 0) {
                        data.Encode2(0); // not 0, Encode2, Encode2
                    }
                    break;
                }
                // PreBB
                if ((datamask & 0x20000) != 0) {
                    data.Encode4(chr.getMonsterBook().getCover());
                }
                if ((datamask & 0x10000) != 0) {
                    data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                }
                if ((datamask & 0x40000L) != 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if ((datamask & 0x80000L) != 0) {
                    data.Encode2(0);
                }
                if ((datamask & 0x200000L) != 0) {
                    data.Encode2(0);
                }
                break;
            }
            case IMS: {
                if ((datamask & 0x40000L) != 0) {
                    data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                }
                if ((datamask & 0x200000L) != 0 && (chr.getJob() / 100 == 33)) {
                    data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                }
                if ((datamask & 0x400000L) != 0) {
                    data.Encode2(0);
                }
                break;
            }
            case JMS:
            case JMST:
            default: {
                // 0x7C JMS, Present v146-v194
                if (Region.JMS.check() || Region.JMST.check()) {
                    if ((datamask & 0x7C) != 0) {
                        data.Encode2(0); // not 0 -> Encode4, Encode4, Encode2, EncodeStr
                    }
                }
                if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 47) || Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.CMS, 62) || Config.GreaterOrEqual(Region.TWMS, 73) || Config.GreaterOrEqual(Region.THMS, 0) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 0) || Config.GreaterOrEqual(Region.BMS, 24) || Config.GreaterOrEqual(Region.VMS, 35)) {
                    // 0x20000 JMS v146-v194
                    if ((datamask & 0x20000) != 0) {
                        data.Encode4(chr.getMonsterBook().getCover());
                    }
                    // 0x10000 JMS [GW_MonsterBookCode_Encode] v165-v194
                    if ((datamask & 0x10000) != 0) {
                        data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
                    }
                }
                if (Config.GreaterOrEqual(Region.JMST, 110)) {
                    data.Encode4(0);
                    data.Encode2(0);
                    data.Encode4(0);
                    data.Encode4(0);
                } else {
                    if (Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76)) {
                        // 0x10000000
                        if ((datamask & 0x10000000L) != 0) {
                            data.Encode4(0);
                        }
                        // 0x20000000
                        if ((datamask & 0x20000000L) != 0) {
                            data.Encode2(0); // not 0, Encode2
                        }
                    }
                }
                if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)) {
                    // 0x40000 (GMS 0x80000) [QuestInfoPacket] v165-v194
                    if ((datamask & 0x00040000) != 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }
                    if (Config.PreBB()) {
                        if ((datamask & 0x00080000) != 0) {
                            // 0x80000 JMS v165, v186, not in v188
                            data.Encode2(0); // not 0 -> Encode4, Encode2
                        }
                        if (Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                            if ((datamask & 0x00200000) != 0) {
                                // 0x200000 VisitorQuestLog (GMS 0x800000)
                                data.Encode2(0); // not 0 -> Encode2, Encode2
                            }
                        }
                    }
                }
                // v187-v194
                if (Config.PostBB()) {
                    // 0x200000
                    if ((datamask & 0x200000) != 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
                    }
                    // 0x400000 QuestCompleteOld
                    if ((datamask & 0x400000) != 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                    }
                    if (Config.GreaterOrEqual(Region.JMST, 110)) {
                        if ((datamask & 0x02000000) != 0) {
                            data.Encode2(0);
                        }
                    } else {
                        // 0x800000, VisitorQuestLog
                        if ((datamask & 0x800000) != 0) {
                            data.Encode2(0); // not 0, Encode2, Encode2
                        }
                    }
                }
                break;
            }
        }

        return data.getBytes();
    }

    public static final byte[] InventoryInfo(MapleCharacter chr, long datamask) {
        ServerPacket data = new ServerPacket();
        // アイテム欄の数
        // v165-v194
        if ((datamask & 0x80L) != 0) {
            data.Encode1(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit()); // 0x04
            data.Encode1(chr.getInventory(MapleInventoryType.USE).getSlotLimit()); // 0x08
            data.Encode1(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit()); // 0x10
            data.Encode1(chr.getInventory(MapleInventoryType.ETC).getSlotLimit()); // 0x20
            data.Encode1(chr.getInventory(MapleInventoryType.CASH).getSlotLimit()); // 0x40
        }
        // v165-v194 OK
        if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 67) || Config.GreaterOrEqual(Region.JMS, 165) || Config.GreaterOrEqual(Region.CMS, 74) || Config.GreaterOrEqual(Region.TWMS, 96) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
            // 0x100000
            if ((datamask & 0x100000) != 0) {
                data.Encode4(0);
                data.Encode4(0);
            }
        }
        // 装備
        if ((datamask & 4) != 0) {
            MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
            Collection<IItem> equippedC = iv.list();
            List<Item> equipped = new ArrayList<>(equippedC.size());
            for (IItem item : equippedC) {
                equipped.add((Item) item);
            }
            Collections.sort(equipped);
            // 装備済みアイテム
            for (Item item : equipped) {
                if (item.getPosition() < 0 && item.getPosition() > -100) {
                    data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                    data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
                }
            }
            data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            // 装備済みアバター?
            for (Item item : equipped) {
                if (item.getPosition() <= -100 && item.getPosition() > -1000) {
                    data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                    data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
                }
            }
            data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            // 装備
            iv = chr.getInventory(MapleInventoryType.EQUIP);
            for (IItem item : iv.list()) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            // 装備済み -1000
            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 92) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70) || Config.GreaterOrEqual(Region.GMS, 83)) {
                for (Item item : equipped) {
                    if (item.getPosition() <= -1000 && item.getPosition() > -1100) {
                        data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                        data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
                    }
                }
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            }
            // 装備済み -1100
            if (Config.PostBB()) {
                for (Item item : equipped) {
                    if (item.getPosition() <= -1100 && item.getPosition() > -1200) {
                        data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                        data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
                    }
                }
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            }
            if (Config.GreaterOrEqual(Region.EMS, 89)) {
                data.Encode1(0); // 00527A25
                data.Encode2(0);
            }
            // カンナ?
            if (Config.GreaterOrEqual(Region.KMS, 127) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.GMS, 111)) {
                if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.GMS, 116)) {
                    for (Item item : equipped) {
                        if (item.getPosition() <= -1500 && item.getPosition() > -1600) {
                            data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                            data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
                        }
                    }
                    data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
                }
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            }
            if (Config.GreaterOrEqual(Region.GMS, 126)) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            }
            if (Config.GreaterOrEqual(Region.GMS, 131)) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            }
            if (Config.GreaterOrEqual(Region.KMS, 169)) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            }
            if (Config.GreaterOrEqual(Region.KMS, 197)) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            }
            if (Config.GreaterOrEqual(Region.JMS, 308) || Config.GreaterOrEqual(Region.EMS, 89)) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            }
            if (Config.GreaterOrEqual(Region.KMS, 197)) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Equip));
            }
        }
        // 消費
        if ((datamask & 8) != 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.USE).list()) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Consume));
        }
        // 設置
        if ((datamask & 0x10L) != 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.SETUP).list()) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Install));
        }
        // ETC
        if ((datamask & 0x20L) != 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.ETC).list()) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Etc));
        }
        // ポイントアイテム
        if ((datamask & 0x40L) != 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.CASH).list()) {
                data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlot(item));
                data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
            }
            data.EncodeBuffer(RD_GW_ItemSlotBase.EncodeSlotEnd(RD_GW_ItemSlotBase.ItemType.Cash));
        }
        if (Config.GreaterOrEqual(Region.KMS, 197)) {
            for (int i = 3; i <= 4; i++) {
                // from 3 to 4
                data.Encode4(-1);
            }
            return data.getBytes();
        }
        // 不明
        if (Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.GMS, 111)) {
            // func 004FB8B0
            data.Encode4(-1); // not -1, Encode4, Encode4 not -1, Encode4, end  Encode4(-1)
        }

        return data.getBytes();
    }

    public static byte[] Encode_302_1(MapleCharacter chr, long datamask) {
        ServerPacket data = new ServerPacket();

        // 008ABA10
        data.Encode8(datamask);
        data.Encode1(0); // nCombatOrders
        data.Encode1(0); // not 0, Encode4
        data.Encode4(0); // not 0, Encode4, Encode8
        data.Encode1(0); // not 0, Encode1, Encode4, Encode8...
        if ((datamask & 1) != 0) {
            // キャラクター情報
            data.EncodeBuffer(RD_CharacterStat.Encode(chr));
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
        if ((datamask & 2) != 0) {
            data.EncodeBuffer(RD_CharacterStat.EncodeMoney(chr));
            data.EncodeBuffer(RD_CharacterStat.EncodePachinko(chr));
            data.EncodeZeroBytes(12); // unknown
        }
        if ((datamask & 8) != 0 || (datamask & 0x2000000L) != 0) {
            data.Encode4(0);
        }
        if ((datamask & 4) != 0) {
            data.EncodeBuffer(InventoryInfo(chr, datamask));
        }
        if ((datamask & 0x1000000L) != 0) {
            data.Encode4(0);
        }
        if ((datamask & 0x40000000L) != 0) {
            data.Encode4(0);
        }
        if ((datamask & 0x800000L) != 0) {
            data.Encode1(0);
        }
        if ((datamask & 0x100L) != 0) {
            data.EncodeBuffer(Structure.addSkillInfo(chr));
        }
        if ((datamask & 0x8000) != 0) {
            data.EncodeBuffer(chr.getCoolTime().getBufferForLogin(System.currentTimeMillis()));
        }
        if ((datamask & 0x400L) != 0) {
            data.Encode2(0); // not 0 -> Encode4 x5
        }
        if ((datamask & 0x800L) != 0) {
            data.EncodeBuffer(Structure.addRingInfo(chr));
        }
        if ((datamask & 0x1000L) != 0) {
            data.EncodeBuffer(Structure.addRocksInfo(chr));
        }
        if ((datamask & 0x7CL) != 0) {
            data.Encode2(0); // not 0 -> Encode4, Encode4, Encode2, EncodeStr
        }
        if ((datamask & 0x20000L) != 0) {
            data.Encode4(chr.getMonsterBook().getCover());
        }
        if ((datamask & 0x10000L) != 0) {
            data.EncodeBuffer(RD_CStage.GW_MonsterBookCode_Encode(chr));
        }
        if (Config.GreaterOrEqual(Region.JMS, 308)) {
            // JMS308
            if ((datamask & 0x20000000000L) != 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x40000000000L) != 0) {
                data.Encode2(0);
            }
            if ((datamask & 0x200000000000L) != 0) {
                data.Encode2(0);
                data.Encode2(0);
            }
            if ((datamask & 0x800000000000L) != 0) {
                for (int i = 0; i < 10; i++) {
                    data.Encode1(0);
                }
            }
        } else {
            //JMS302
            if ((datamask & 0x8000000000L) != 0) {
                data.Encode4(0);
            }
            if ((datamask & 0x10000000000L) != 0) {
                data.Encode2(0); // 00546810
            }
            if ((datamask & 0x80000000000L) != 0) {
                data.Encode2(0); // 0054B730
                data.Encode2(0);
            }
            if ((datamask & 0x100000000000L) != 0) {
                for (int i = 0; i < 10; i++) {
                    data.Encode1(0);
                }
            }
        }
        if ((datamask & 0x200000L) != 0 && (chr.getJob() / 100 == 33)) {
            data.EncodeBuffer(RD_CStage.GW_WildHunterInfo_Encode());
        }
        if ((datamask & 0x4000000L) != 0) {
            data.Encode2(0);
        }
        if ((datamask & 0x20000000L) != 0) {
            for (int i = 0; i < 13; i++) {
                // 4-4-3-2
                data.Encode4(0);
            }
        }
        if ((datamask & 0x10000000L) != 0) {
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
        }
        if ((datamask & 0x80000000L) != 0) {
            data.Encode2(0);
        }
        if ((datamask & 0x100000000L) != 0) {
            data.Encode4(0);
            data.Encode4(0);
            if (Config.GreaterOrEqual(Region.JMS, 308)) {
                data.Encode4(0);
            }
        }
        if ((datamask & 0x200000000L) != 0) {
            if (Config.GreaterOrEqual(Region.JMS, 308)) {
                data.Encode1(0);
                data.Encode2(0);
            }
        }
        if (Config.GreaterOrEqual(Region.JMS, 308)) {
            // JMS308
            if ((datamask & 0x400000000L) != 0) {
                data.Encode1(0);
            }
            if ((datamask & 0x800000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode1(0);
            }
            if ((datamask & 0x2000000000L) != 0) {
                data.Encode4(0);
                data.Encode4(0);
                data.EncodeZeroBytes(8);
            }
            if ((datamask & 0x1000000000L) != 0) {
                data.Encode2(0);
            }
        } else {
            // JMS302
            if ((datamask & 0x400000000L) != 0) {
                data.Encode2(0);
            }
        }
        data.Encode4(0);
        data.Encode8(SharedDate.getTimestamp());
        if ((datamask & 0x400000000000L) != 0) {
            if (Config.GreaterOrEqual(Region.JMS, 308)) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
                data.EncodeZeroBytes(32);
            }
        }

        return data.getBytes();
    }

    public static byte[] Encode_302_2(MapleCharacter chr, long datamask) {
        ServerPacket data = new ServerPacket();

        // 00552C00
        data.Encode8(datamask);
        if ((datamask & 0x200L) != 0) {
            data.EncodeBuffer(Structure.addQuestInfo(chr));
        }
        if ((datamask & 0x4000L) != 0) {
            data.EncodeBuffer(Structure.addQuestComplete(chr));
        }
        if ((datamask & 0x40000L) != 0) {
            data.EncodeBuffer(Structure.QuestInfoPacket(chr));
        }
        // 0x400000 QuestCompleteOld
        if ((datamask & 0x400000L) != 0) {
            data.Encode2(0);
        }

        return data.getBytes();
    }
}
