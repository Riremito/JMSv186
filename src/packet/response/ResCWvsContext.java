/*
 * Copyright (C) 2024 Riremito
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
package packet.response;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import config.ServerConfig;
import constants.GameConstants;
import debug.Debug;
import handling.MaplePacket;
import java.sql.Timestamp;
import packet.ServerPacket;
import packet.request.ContextPacket;
import packet.response.struct.GW_CharacterStat;
import packet.response.struct.GW_ItemSlotBase;
import packet.response.struct.SecondaryStat;
import packet.response.struct.TestHelper;
import tools.HexTool;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCWvsContext {

    // CWvsContext::OnInventoryOperation
    public static MaplePacket InventoryOperation(boolean unlock) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryOperation);
        sp.Encode1(unlock ? 1 : 0);// m_bExclRequestSent, unlock
        int item_count = 1; // ItemCount
        sp.Encode1(item_count); // 1+

        if (ServerConfig.JMS302orLater()) {
            sp.Encode1(0); // unused
        }

        do {
            item_count--;
        } while (0 < item_count);

        sp.Encode1(0); // for CUserLocal::SetSecondaryStatChangedPoint
        return sp.Get();
    }

    // CWvsContext::OnChangeSkillRecordResult
    public static final MaplePacket updateSkill(int skillid, int level, int masterlevel, long expiration) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ChangeSkillRecordResult);
        sp.Encode1(1);
        sp.Encode2(1);
        sp.Encode4(skillid);
        sp.Encode4(level);
        sp.Encode4(masterlevel);
        if (ServerConfig.JMS164orLater()) {
            sp.Encode8((Timestamp.valueOf("2027-07-07 07:00:00").getTime() + Timestamp.valueOf("2339-01-01 18:00:00").getTime()) * 10000);
        }
        sp.Encode1(4);
        return sp.Get();
    }

    // CWvsContext::OnTemporaryStatSet
    public static final MaplePacket TemporaryStatSet(MapleCharacter chr, int skill_id) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_TemporaryStatSet);
        // SecondaryStat::DecodeForLocal
        p.EncodeBuffer(SecondaryStat.EncodeForLocal(chr, skill_id));
        p.Encode2(0); // delay
        p.Encode1(0);
        return p.Get();
    }

    // CWvsContext::OnInventoryOperation
    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item) {
        return addInventorySlot(type, item, false);
    }

    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryOperation);
        sp.Encode1(fromDrop ? 1 : 0);
        sp.Encode1(1); // add mode
        sp.Encode1(0);
        sp.Encode1(type.getType()); // iv type
        sp.Encode2(item.getPosition()); // v131-v194
        sp.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        return sp.Get();
    }

    public static MaplePacket scrolledItem(IItem scroll, IItem item, boolean destroyed, boolean potential) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryOperation);
        sp.Encode1(1); // fromdrop always true
        sp.Encode1(destroyed ? 2 : 3);
        sp.Encode1(scroll.getQuantity() > 0 ? 1 : 3);
        sp.Encode1(GameConstants.getInventoryType(scroll.getItemId()).getType()); //can be cash
        sp.Encode2(scroll.getPosition());
        if (scroll.getQuantity() > 0) {
            sp.Encode2(scroll.getQuantity());
        }
        sp.Encode1(3);
        if (!destroyed) {
            sp.Encode1(MapleInventoryType.EQUIP.getType());
            sp.Encode2(item.getPosition());
            sp.Encode1(0);
        }
        sp.Encode1(MapleInventoryType.EQUIP.getType());
        sp.Encode2(item.getPosition());
        if (!destroyed) {
            sp.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        }
        sp.Encode1(1);
        return sp.Get();
    }

    // CWvsContext::OnInventoryGrow
    // CWvsContext::OnStatChanged
    public static final MaplePacket StatChanged(MapleCharacter chr, int unlock, int statmask) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_StatChanged);
        // 0 = lock   -> do not clear lock flag
        // 1 = unlock -> clear lock flag
        sp.Encode1(unlock); // CWvsContext->bExclRequestSent
        sp.EncodeBuffer(GW_CharacterStat.EncodeChangeStat(chr, statmask));
        if (ServerConfig.IsPreBB()) {
            // Pet
            if ((statmask & GW_CharacterStat.Flag.PET1.get()) > 0) {
                int v5 = 0; // CVecCtrlUser::AddMovementInfo
                sp.Encode1(v5);
            }
        } else {
            // v188+
            sp.Encode1(0); // not 0 -> Encode1
            sp.Encode1(0); // not 0 -> Encode4, Encode4
        }
        return sp.Get();
    }

    public static final MaplePacket updatePet(final MaplePet pet, final IItem item) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryOperation);
        sp.Encode1(0);
        sp.Encode1(2);
        sp.Encode1(3);
        sp.Encode1(5);
        sp.Encode2(pet.getInventoryPosition());
        sp.Encode1(0);
        sp.Encode1(5);
        sp.Encode2(pet.getInventoryPosition());
        sp.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        return sp.Get();
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, short src, short dst) {
        return moveInventoryItem(type, src, dst, (byte) -1);
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, short src, short dst, short equipIndicator) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 02"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(dst);
        if (equipIndicator != -1) {
            mplew.write(equipIndicator);
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(fromDrop ? 1 : 0);
        //	mplew.write((slot2 > 0 ? 1 : 0) + 1);
        mplew.write(1);
        mplew.write(1);
        mplew.write(type.getType()); // iv type
        mplew.writeShort(item.getPosition()); // slot id
        mplew.writeShort(item.getQuantity());
        /*	if (slot2 > 0) {
        mplew.write(1);
        mplew.write(type.getType());
        mplew.writeShort(slot2);
        mplew.writeShort(amt2);
        }*/
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeWithRestInventoryItem(MapleInventoryType type, short src, short dst, short srcQ, short dstQ) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 01"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(srcQ);
        mplew.write(HexTool.getByteArrayFromHexString("01"));
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(dstQ);
        return mplew.getPacket();
    }

    public static MaplePacket clearInventoryItem(MapleInventoryType type, short slot, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(fromDrop ? 1 : 0);
        mplew.write(HexTool.getByteArrayFromHexString("01 03"));
        mplew.write(type.getType());
        mplew.writeShort(slot);
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeInventoryItem(MapleInventoryType type, short src, short dst, short total) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.write(1); // merge mode?
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(total);
        return mplew.getPacket();
    }

    public static MaplePacket updateSpecialItemUse(IItem item, byte invType) {
        return updateSpecialItemUse(item, invType, item.getPosition());
    }

    public static MaplePacket updateSpecialItemUse(IItem item, byte invType, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(0); // could be from drop
        mplew.write(2); // always 2
        mplew.write(3); // quantity > 0 (?)
        mplew.write(invType); // Inventory type
        mplew.writeShort(pos); // item slot
        mplew.write(0);
        mplew.write(invType);
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        TestHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(2); //?
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateSpecialItemUse_(IItem item, byte invType) {
        return updateSpecialItemUse_(item, invType, item.getPosition());
    }

    public static MaplePacket updateSpecialItemUse_(IItem item, byte invType, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(0); // could be from drop
        mplew.write(1); // always 2
        mplew.write(0); // quantity > 0 (?)
        mplew.write(invType); // Inventory type
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        TestHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(1); //?
        }
        return mplew.getPacket();
    }

    public static MaplePacket dropInventoryItem(MapleInventoryType type, short src) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        if (src < 0) {
            mplew.write(1);
        }
        return mplew.getPacket();
    }

    public static MaplePacket dropInventoryItemUpdate(MapleInventoryType type, IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01"));
        mplew.write(type.getType());
        mplew.writeShort(item.getPosition());
        mplew.writeShort(item.getQuantity());
        return mplew.getPacket();
    }

    // context packet
    public static MaplePacket getShowInventoryFull() {
        return ContextPacket.getShowInventoryStatus(ContextPacket.DropPickUpMessageType.PICKUP_INVENTORY_FULL);
    }

    public static MaplePacket getInventoryFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(1);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static final MaplePacket Message(ContextPacket.MessageArg ma) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Message);
        sp.Encode1(ma.mt.get());
        switch (ma.mt) {
            case MS_DropPickUpMessage:
                {
                    sp.Encode1(ma.dt.get());
                    switch (ma.dt) {
                        case PICKUP_ITEM:
                            {
                                sp.Encode4(ma.ItemID);
                                sp.Encode4(ma.Inc_ItemCount);
                                break;
                            }
                        case PICKUP_MESO:
                            {
                                if (!(ServerConfig.IsJMS() && ServerConfig.GetVersion() < 164)) {
                                    sp.Encode1(0);
                                }
                                sp.Encode4(ma.Inc_Meso);
                                if (ServerConfig.IsJMS() && ServerConfig.GetVersion() < 164) {
                                    sp.Encode2(0); // Internet cafe bonus
                                } else {
                                    sp.Encode4(0);
                                }
                                break;
                            }
                        case PICKUP_MONSTER_CARD:
                            {
                                sp.Encode4(ma.ItemID);
                                break;
                            }
                        case PICKUP_INVENTORY_FULL:
                        case PICKUP_UNAVAILABLE:
                        case PICKUP_BROKEN:
                            {
                                sp.Encode4(0);
                                sp.Encode4(0);
                                break;
                            }
                        default:
                            {
                                Debug.ErrorLog("Unknown DropPickUp Type" + ma.dt.get());
                                break;
                            }
                    }
                    break;
                }
        // updateQuest, updateQuestMobKills
            case MS_QuestRecordMessage:
                {
                    sp.Encode2(ma.QuestID);
                    sp.Encode1(ma.qt.get());
                    switch (ma.qt) {
                        case QUEST_START:
                            {
                                sp.Encode1(0); // 0 or not
                                break;
                            }
                        case QUEST_UPDATE:
                            {
                                sp.EncodeStr(ma.str);
                                break;
                            }
                        case QUEST_COMPLETE:
                            {
                                sp.Encode8(System.currentTimeMillis());
                                break;
                            }
                        default:
                            {
                                Debug.ErrorLog("Unknown QuestRecord Type" + ma.dt.get());
                                break;
                            }
                    }
                    break;
                }
        // itemExpired
            case MS_CashItemExpireMessage:
                {
                    sp.Encode4(ma.ItemID);
                    break;
                }
            case MS_IncEXPMessage:
                {
                    sp.Encode1(ma.Inc_EXP_TextColor);
                    sp.Encode4(ma.Inc_EXP);
                    sp.Encode1(ma.InChat); // bOnQuest
                    sp.Encode4(0);
                    sp.Encode1(ma.Inc_EXP_MobEventBonusPercentage); // nMobEventBonusPercentage
                    sp.Encode1(0);
                    sp.Encode4(ma.Inc_EXP_WeddingBonus); // 結婚ボーナス経験値
                    sp.Encode4(0); // グループリングボーナスEXP (?)
                    if (0 < ma.Inc_EXP_MobEventBonusPercentage) {
                        sp.Encode1(ma.Inc_EXP_PlayTimeHour);
                    }
                    if (ma.InChat != 0) {
                        sp.Encode1(0);
                    }
                    sp.Encode1(0); // nPartyBonusEventRate
                    sp.Encode4(ma.Inc_EXP_PartyBonus); // グループボーナス経験値
                    sp.Encode4(ma.Inc_EXP_EquipmentBonus); // アイテム装着ボーナス経験値
                    sp.Encode4(0); // not used
                    sp.Encode4(ma.Inc_EXP_RainbowWeekBonus); // レインボーウィークボーナス経験値
                    if (194 <= ServerConfig.GetVersion()) {
                        sp.Encode1(0); // 0 or not
                    }
                    break;
                }
        // getSPMsg
            case MS_IncSPMessage:
                {
                    sp.Encode2(ma.JobID);
                    sp.Encode1(ma.Inc_SP);
                    break;
                }
        // getShowFameGain
            case MS_IncPOPMessage:
                {
                    sp.Encode4(ma.Inc_Fame);
                    break;
                }
        // showMesoGain
            case MS_IncMoneyMessage:
                {
                    sp.Encode4(ma.Inc_Meso);
                    break;
                }
        // getGPMsg
            case MS_IncGPMessage:
                {
                    sp.Encode4(ma.Inc_GP);
                    break;
                }
        // getStatusMsg
            case MS_GiveBuffMessage:
                {
                    sp.Encode4(ma.ItemID);
                    break;
                }
            case MS_GeneralItemExpireMessage:
                {
                    break;
                }
        // showQuestMsg
            case MS_SystemMessage:
                {
                    sp.EncodeStr(ma.str);
                    break;
                }
        // updateInfoQuest
            case MS_QuestRecordExMessage:
                {
                    sp.Encode2(ma.QuestID);
                    sp.EncodeStr(ma.str);
                    break;
                }
            case MS_ItemProtectExpireMessage:
                {
                    break;
                }
            case MS_ItemExpireReplaceMessage:
                {
                    break;
                }
            case MS_SkillExpireMessage:
                {
                    break;
                }
        // updateBeansMSG, GainTamaMessage
            case MS_JMS_PACHINKO:
                {
                    sp.Encode4(ma.Inc_Tama);
                    break;
                }
            default:
                {
                    break;
                }
        }
        return sp.Get();
    }

}
