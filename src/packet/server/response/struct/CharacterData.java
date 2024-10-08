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
import packet.server.ServerPacket;
import packet.server.response.struct.GW_ItemSlotBase.ItemType;

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

        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131) {
            data.Encode2((short) datamask); // statmask
        } else {
            data.Encode8(datamask); // statmask
        }

        if ((ServerConfig.IsJMS() && 180 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()
                || ServerConfig.IsKMS()) {
            data.Encode1(0); // nCombatOrders
        }

        if (ServerConfig.IsPostBB()) {
            data.Encode1(0); // not 0, Encode1, Encode4(size), EncodeBuffer8, Encode4(size), EncodeBuffer8
        }

        if ((datamask & 0x01) > 0) {
            // キャラクター情報
            data.EncodeBuffer(GW_CharacterStat.Encode(chr));

            // 友達リストの上限
            data.Encode1(chr.getBuddylist().getCapacity());

            // 精霊の祝福 v165, v186
            if ((ServerConfig.IsJMS() && 165 <= ServerConfig.GetVersion())
                    || ServerConfig.IsTWMS()
                    || ServerConfig.IsCMS()
                    || ServerConfig.IsKMS()) {
                if (chr.getBlessOfFairyOrigin() != null) {
                    data.Encode1(1);
                    data.EncodeStr(chr.getBlessOfFairyOrigin());
                } else {
                    data.Encode1(0);
                }
            }

            // 祝福系統
            if (ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion() || (ServerConfig.IsKMS() && ServerConfig.IsPostBB())) {
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
                    || ServerConfig.IsCMS()) {
                data.EncodeBuffer(GW_CharacterStat.EncodePachinko(chr));
            }
        }
        // 0x4 (<< 2), 0x100000, 0x4 [addInventoryInfo]
        if ((datamask & 0x04) > 0) {
            data.EncodeBuffer(InventoryInfo(chr, datamask));
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
            data.EncodeBuffer(Structure.addRingInfo(chr));
        }
        // 0x1000 [addRocksInfo] v165-v188 changed v194
        if ((datamask & 0x1000) > 0) {
            data.EncodeBuffer(Structure.addRocksInfo(chr));
        }
        switch (ServerConfig.GetRegion()) {
            case KMS: {
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
                    if ((datamask & 0x200000) > 0 && (chr.getJob() / 100 == 33)) {
                        data.EncodeBuffer(GW_WildHunterInfo.Encode());
                    }
                    if ((datamask & 0x400000) > 0) {
                        data.Encode2(0); // not 0, Encode2, EncodeBuffer8
                    }
                }
                break;
            }
            case TWMS: {
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
                if ((datamask & 0x400000) > 0) {
                    data.Encode2(0); // not 0, Encode2, EncodeBuffer8
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
            case JMS:
            default: {
                // 0x7C JMS, Present v165-v194
                if (ServerConfig.IsJMS()) {
                    if ((datamask & 0x7C) > 0) {
                        data.Encode2(0); // not 0 -> Encode4, Encode4, Encode2, EncodeStr
                    }
                }
                if (ServerConfig.IsJMS()
                        && 164 <= ServerConfig.GetVersion()) {
                    // 0x20000 JMS v165-v194
                    if ((datamask & 0x020000) > 0) {
                        data.Encode4(chr.getMonsterBookCover());
                    }
                    // 0x10000 JMS [addMonsterBookInfo] v165-v194
                    if ((datamask & 0x010000) > 0) {
                        data.EncodeBuffer(Structure.addMonsterBookInfo(chr));
                    }

                    if (ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion()) {
                        // 0x10000000
                        if ((datamask & 0x10000000) > 0) {
                            data.Encode4(0);
                        }
                        // 0x20000000
                        if ((datamask & 0x20000000) > 0) {
                            data.Encode2(0); // not 0, Encode2
                        }
                    }

                    // 0x40000 (GMS 0x80000) [QuestInfoPacket] v165-v194
                    if ((datamask & 0x040000) > 0) {
                        data.EncodeBuffer(Structure.QuestInfoPacket(chr));
                    }

                    if (ServerConfig.IsPreBB() && (datamask & 0x80000) > 0) {
                        // 0x80000 JMS v165, v186, not in v188
                        data.Encode2(0);// not 0 -> Encode4, Encode2
                    }

                    if (ServerConfig.GetVersion() == 186 && (datamask & 0x200000) > 0) {
                        // 0x200000 VisitorQuestLog (GMS 0x800000)
                        data.Encode2(0); // not 0 -> Encode2, Encode2
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
                        // 0x800000
                        if ((datamask & 0x800000) > 0) {
                            data.Encode2(0); // not 0, Encode2, Encode2
                        }
                    }
                }
                break;
            }
        }
        return data.Get().getBytes();
    }

    public static final byte[] InventoryInfo(MapleCharacter chr, long datamask) {
        ServerPacket p = new ServerPacket();
        // アイテム欄の数
        // v165-v194
        if ((datamask & 0x80) > 0) {
            p.Encode1(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit()); // 0x04
            p.Encode1(chr.getInventory(MapleInventoryType.USE).getSlotLimit()); // 0x08
            p.Encode1(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit()); // 0x10
            p.Encode1(chr.getInventory(MapleInventoryType.ETC).getSlotLimit()); // 0x20
            p.Encode1(chr.getInventory(MapleInventoryType.CASH).getSlotLimit()); // 0x40
        }

        // v165-v194 OK
        if (ServerConfig.IsJMS() && 165 <= ServerConfig.GetVersion()
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()
                || ServerConfig.IsKMS()) {
            // 0x100000
            if ((datamask & 0x100000) > 0) {
                p.Encode4(0);
                p.Encode4(0);
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
                    p.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                    p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
                }
            }
            p.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            // 装備済みアバター?
            for (Item item : equipped) {
                if (item.getPosition() <= -100 && item.getPosition() > -1000) {
                    p.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                    p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
                }
            }
            p.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            // 装備
            iv = chr.getInventory(MapleInventoryType.EQUIP);
            for (IItem item : iv.list()) {
                p.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            p.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            // 装備済み -1000
            if (ServerConfig.IsJMS() && 180 <= ServerConfig.GetVersion()
                    || ServerConfig.IsTWMS()
                    || ServerConfig.IsCMS()
                    || ServerConfig.IsKMS()) {
                for (Item item : equipped) {
                    if (item.getPosition() <= -1000 && item.getPosition() > -1100) {
                        p.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                        p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
                    }
                }
                p.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            }
            // 装備済み -1100
            if (ServerConfig.IsPostBB()) {
                for (Item item : equipped) {
                    if (item.getPosition() <= -1100 && item.getPosition() > -1200) {
                        p.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                        p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
                    }
                }
                p.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Equip));
            }
        }

        // 消費
        if ((datamask & 0x08) > 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.USE).list()) {
                p.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            p.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Consume));
        }
        // 設置
        if ((datamask & 0x10) > 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.SETUP).list()) {
                p.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            p.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Install));
        }
        // ETC
        if ((datamask & 0x20) > 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.ETC).list()) {
                p.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            p.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Etc));
        }
        // ポイントアイテム
        if ((datamask & 0x40) > 0) {
            for (IItem item : chr.getInventory(MapleInventoryType.CASH).list()) {
                p.EncodeBuffer(GW_ItemSlotBase.EncodeSlot(item));
                p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            p.EncodeBuffer(GW_ItemSlotBase.EncodeSlotEnd(ItemType.Cash));
        }
        // 不明
        if (ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion() || (ServerConfig.IsKMS() && ServerConfig.IsPostBB())) {
            // func 004FB8B0
            p.Encode4(-1); // not -1, Encode4, Encode4 not -1, Encode4, end  Encode4(-1)
        }

        return p.Get().getBytes();
    }
}
