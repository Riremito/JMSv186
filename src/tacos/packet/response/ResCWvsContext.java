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
package tacos.packet.response;

import odin.client.MapleCharacter;
import odin.client.MapleQuestStatus;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MapleMount;
import odin.client.inventory.MaplePet;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import odin.constants.GameConstants;
import tacos.shared.SharedDate;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import odin.handling.channel.MapleGuildRanking;
import odin.handling.world.MapleParty;
import odin.handling.world.MaplePartyCharacter;
import odin.handling.world.PartyOperation;
import odin.handling.world.OdinWorld;
import odin.handling.world.family.MapleFamily;
import odin.handling.world.family.MapleFamilyBuff;
import odin.handling.world.family.MapleFamilyCharacter;
import odin.handling.world.guild.MapleBBSThread;
import odin.handling.world.guild.MapleGuild;
import odin.handling.world.guild.MapleGuildAlliance;
import odin.handling.world.guild.MapleGuildCharacter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsBodyPart;
import tacos.packet.ops.arg.ArgBroadcastMsg;
import tacos.packet.ops.OpsChangeStat;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.ops.OpsMapTransfer;
import tacos.packet.ops.arg.ArgFriend;
import tacos.packet.ops.arg.ArgMessage;
import tacos.packet.ops.OpsShopScanner;
import tacos.packet.request.sub.ReqSub_UserConsumeCashItemUseRequest;
import tacos.packet.response.data.DataCUIUserInfo;
import tacos.packet.response.data.DataCWvsContext;
import tacos.packet.response.data.DataGW_CharacterStat;
import tacos.packet.response.data.DataGW_ItemSlotBase;
import tacos.packet.response.struct.InvOp;
import odin.server.MapleItemInformationProvider;
import odin.server.maps.MapleDoor;
import tacos.client.TacosBuff;
import tacos.client.TacosBuff.Buff;
import tacos.odin.OdinPair;
import tacos.client.TacosCharacter;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsGivePopularity;
import tacos.packet.ops.OpsMarriage;
import tacos.packet.ops.OpsParty;
import tacos.packet.ops.OpsSecondaryStat;
import tacos.packet.response.data.DataAvatarLook;
import tacos.packet.response.data.DataForcedStat;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class ResCWvsContext {

    // CWvsContext::OnInventoryOperation
    public static MaplePacket InventoryOperation(boolean unlock, InvOp io) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_InventoryOperation);
        sp.Encode1(unlock ? 1 : 0);// m_bExclRequestSent, unlock
        sp.Encode1((io == null) ? 0 : io.get().size());

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.KMST, 391) || Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0); // unused
        }

        boolean equip_changed = false;
        if (io != null) {
            for (InvOp.InvData v : io.get()) {
                sp.Encode1(v.mode);
                switch (v.mode) {
                    // add
                    case 0: {
                        sp.Encode1(v.type.getType());
                        sp.Encode2(v.item.getPosition());
                        sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(v.item));
                        break;
                    }
                    // update
                    case 1: {
                        sp.Encode1(v.type.getType());
                        sp.Encode2(v.item.getPosition());
                        sp.Encode2(v.item.getQuantity());
                        break;
                    }
                    // move
                    case 2: {
                        sp.Encode1(v.type.getType());
                        sp.Encode2(v.src);
                        sp.Encode2(v.dst);
                        // 装備変更
                        if (v.type.getType() == 1 && (v.src < 0 || v.dst < 0)) {
                            equip_changed = true;
                        }
                        break;
                    }
                    // remove
                    case 3: {
                        sp.Encode1(v.type.getType());
                        sp.Encode2(v.src);
                        // 装備変更
                        if (v.type.getType() == 1 && (v.src < 0)) {
                            equip_changed = true;
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }

            if (equip_changed) {
                sp.Encode1(0); // for CUserLocal::SetSecondaryStatChangedPoint
            }
        }
        return sp.get();
    }

    // CWvsContext::OnInventoryGrow
    public static MaplePacket InventoryGrow(byte invType, byte newSlots) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_InventoryGrow);

        sp.Encode1(invType);
        sp.Encode1(newSlots);
        return sp.get();
    }

    // CWvsContext::OnStatChanged
    public static MaplePacket StatChanged(TacosCharacter chr, boolean unlock, int statmask) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StatChanged);
        // 0 = lock   -> do not clear lock flag
        // 1 = unlock -> clear lock flag
        sp.Encode1(unlock ? 1 : 0); // CWvsContext->bExclRequestSent
        if ((Region.IsEMS() && !Version.GreaterOrEqual(Region.EMS, 89)) || Version.Between(Region.TWMS, 74, 93)) {
            sp.Encode1(0); // EMS v55
        }
        sp.EncodeBuffer(DataGW_CharacterStat.EncodeChangeStat(chr, statmask));
        if (Version.PreBB()) {
            if (Region.IsJMS()) {
                // Pet
                if ((statmask & OpsChangeStat.CS_PETSN.get()) != 0) {
                    int v5 = 0; // CVecCtrlUser::AddMovementInfo
                    sp.Encode1(v5);
                }
            }
            if (Version.GreaterOrEqual(Region.GMS, 91)) {
                sp.Encode1(0); // not 0 -> Encode1
            }
        } else {
            // v188+
            sp.Encode1(0); // not 0 -> Encode1
            sp.Encode1(0); // not 0 -> Encode4, Encode4
        }
        return sp.get();
    }

    // CWvsContext::OnTemporaryStatSet
    public static MaplePacket TemporaryStatSet(TacosCharacter chr, int buff_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TemporaryStatSet);

        boolean is_swallow_buff = false;
        boolean is_dice = false;
        boolean is_blessing_armor = false;
        // SecondaryStat::DecodeForLocal
        int[] buff_mask = TacosBuff.getBuffBuffer();
        for (Buff buff : chr.getBuff().getCTS(buff_id)) {
            buff_mask[buff.ops.getNl()] |= buff.ops.getNr();
            if (buff.ops == OpsSecondaryStat.CTS_Dice) {
                is_dice = true;
            }
            if (buff.ops == OpsSecondaryStat.CTS_BlessingArmor) {
                is_blessing_armor = true;
            }
            if (buff.ops == OpsSecondaryStat.CTS_SwallowAttackDamage || buff.ops == OpsSecondaryStat.CTS_SwallowDefence || buff.ops == OpsSecondaryStat.CTS_SwallowCritical || buff.ops == OpsSecondaryStat.CTS_SwallowMaxMP || buff.ops == OpsSecondaryStat.CTS_SwallowEvasion) {
                is_swallow_buff = true;
            }
        }
        for (int index = 0; index < buff_mask.length; index++) {
            sp.Encode4(buff_mask[buff_mask.length - 1 - index]);
        }
        for (Buff buff : chr.getBuff().getCTS(buff_id)) {
            if (buff.ops.isTwoState()) {
                continue;
            }
            if (Version.GreaterOrEqual(Region.THMS, 96)) {
                sp.Encode4(buff.buff_effect);
            } else {
                sp.Encode2(buff.buff_effect);
            }
            sp.Encode4(buff.buff_id);
            if (ServerConfig.JMS146orLater()) {
                sp.Encode4(buff.buff_time);
            } else {
                sp.Encode2(buff.buff_time);
            }
        }
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode2(0);
        }
        if (ServerConfig.JMS146orLater()) {
            sp.Encode1(0); // nDefenseAtt
            sp.Encode1(0); // nDefenseState
        }
        // JMS187, GMS95
        if (Version.PostBB()) {
            // CTS_SwallowBuff
            if (is_swallow_buff) {
                sp.Encode1(0);
            }
            // CTS_Dice
            if (is_dice) {
                for (int i = 0; i < 22; i++) {
                    sp.Encode4(0);
                }
            }
            // CTS_BlessingArmor
            if (is_blessing_armor) {
                sp.Encode4(0);
            }
        }
        for (Buff buff : chr.getBuff().getCTS(buff_id)) {
            if (!buff.ops.isTwoState()) {
                continue;
            }
            // TemporaryStatBase<long>::DecodeForClient
            sp.Encode4(buff.buff_effect); // m_value
            sp.Encode4(buff.buff_effect_2); // m_reason
            sp.Encode1(0); // DecodeTime
            sp.Encode4(0); // DecodeTime
            switch (buff.ops.getTwoState()) {
                case NO_EXPIRE: {
                    break;
                }
                case EXPIRE_LAST: {
                    sp.Encode2(buff.buff_time / 1000); // m_usExpireTerm
                    break;
                }
                case EXPIRE_CURRENT: {
                    sp.Encode1(0); // DecodeTime
                    sp.Encode4(0); // DecodeTime -> m_tCurrentTime
                    sp.Encode2(buff.buff_time / 1000); // m_usExpireTerm
                    break;
                }
                case GUIDED_BULLET: {
                    sp.Encode4(0); // m_dwMobID
                    break;
                }
                default: {
                    break;
                }
            }
        }
        if (Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.TWMS, 148)) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode4(0);
            sp.Encode4(0);
        }
        // DecodeForLocal - end.
        sp.Encode2(0); // delay
        if (Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode1(0);
        }
        sp.Encode1(0); // CUserLocal::SetSecondaryStatChangedPoint
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode4(0);
        }
        return sp.get();
    }

    // CWvsContext::OnTemporaryStatReset
    public static MaplePacket TemporaryStatReset(TacosCharacter chr, int buff_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TemporaryStatReset);

        int[] buff_mask = TacosBuff.getBuffBuffer();
        for (Buff buff : chr.getBuff().getCTS(buff_id)) {
            buff_mask[buff.ops.getNl()] |= buff.ops.getNr();
        }
        for (int index = 0; index < buff_mask.length; index++) {
            sp.Encode4(buff_mask[buff_mask.length - 1 - index]);
        }

        sp.Encode1(0);
        return sp.get();
    }

    // CWvsContext::OnForcedStatSet
    public static MaplePacket ForcedStatSet(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ForcedStatSet);

        sp.EncodeBuffer(DataForcedStat.Encode(chr));
        return sp.get();
    }

    // CWvsContext::OnForcedStatReset
    public static MaplePacket ForcedStatReset() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ForcedStatReset);
        return sp.get();
    }

    // CWvsContext::OnChangeSkillRecordResult
    public static MaplePacket ChangeSkillRecordResult(int skillid, int level, int masterlevel, long expiration) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ChangeSkillRecordResult);
        sp.Encode1(1);
        if (Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode1(0);
        }
        sp.Encode2(1);
        sp.Encode4(skillid);
        sp.Encode4(level);
        sp.Encode4(masterlevel);
        if (ServerConfig.JMS164orLater()) {
            sp.Encode8(SharedDate.getMagicalExpirationDate());
        }
        sp.Encode1(4);
        return sp.get();
    }

    // CWvsContext::OnSkillUseResult
    public static MaplePacket SkillUseResult() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SkillUseResult);

        sp.Encode1(0); // unused.
        return sp.get();
    }

    // CWvsContext::OnGivePopularityResult
    public static MaplePacket GivePopularityResult(OpsGivePopularity ops, TacosCharacter chr, boolean is_up, TacosCharacter target) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GivePopularityResult);

        sp.Encode1(ops.get());
        switch (ops) {
            case GivePopularityRes_Success: {
                sp.EncodeStr(target.getName());
                sp.Encode1(is_up ? 1 : 0);
                sp.Encode4(target.getFame());
                break;
            }
            case GivePopularityRes_Notify: {
                sp.EncodeStr(chr.getName());
                sp.Encode1(is_up ? 1 : 0);
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    // CWvsContext::OnMessage
    public static MaplePacket Message(ArgMessage ma) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Message);
        sp.Encode1(ma.mt.get());
        switch (ma.mt) {
            case MS_DropPickUpMessage: {
                sp.Encode1(ma.dt.get());
                switch (ma.dt) {
                    case PICKUP_ITEM: {
                        sp.Encode4(ma.ItemID);
                        sp.Encode4(ma.Inc_ItemCount);
                        break;
                    }
                    case PICKUP_MESO: {
                        if (ServerConfig.JMS164orLater()) {
                            sp.Encode1(0);
                        }
                        sp.Encode4(ma.Inc_Meso);
                        if (Version.LessOrEqual(Region.JMS, 131)) {
                            sp.Encode2(0); // Internet cafe bonus
                        } else {
                            sp.Encode4(0);
                        }
                        break;
                    }
                    case PICKUP_MONSTER_CARD: {
                        sp.Encode4(ma.ItemID);
                        break;
                    }
                    case PICKUP_INVENTORY_FULL:
                    case PICKUP_UNAVAILABLE:
                    case PICKUP_BROKEN: {
                        sp.Encode4(0);
                        sp.Encode4(0);
                        break;
                    }
                    default: {
                        DebugLogger.ErrorLog("Unknown DropPickUp Type" + ma.dt.get());
                        break;
                    }
                }
                break;
            }
            // updateQuest, updateQuestMobKills
            case MS_QuestRecordMessage: {
                sp.Encode2(ma.QuestID);
                sp.Encode1(ma.qt.get());
                switch (ma.qt) {
                    case QUEST_START: {
                        sp.Encode1(0); // 0 or not
                        break;
                    }
                    case QUEST_UPDATE: {
                        sp.EncodeStr(ma.str);
                        break;
                    }
                    case QUEST_COMPLETE: {
                        sp.Encode8(SharedDate.getTimestamp());
                        break;
                    }
                    default: {
                        DebugLogger.ErrorLog("Unknown QuestRecord Type" + ma.dt.get());
                        break;
                    }
                }
                break;
            }
            // itemExpired
            case MS_CashItemExpireMessage: {
                sp.Encode4(ma.ItemID);
                break;
            }
            case MS_IncEXPMessage: {
                sp.Encode1(ma.Inc_EXP_TextColor);
                sp.Encode4(ma.Inc_EXP);
                sp.Encode1(ma.InChat); // bOnQuest
                sp.Encode4(0);
                sp.Encode1(ma.Inc_EXP_MobEventBonusPercentage); // nMobEventBonusPercentage
                sp.Encode1(0);
                if (Region.IsTHMS() && Version.getVersion() == 87) {
                    sp.Encode4(ma.Inc_EXP_WeddingBonus); // Wedding Bonus EXP(+%d)
                    sp.Encode4(0); // Party Ring Bonus EXP(+%d)
                    sp.Encode4(0); // EXP Bonus Internet Cafe(+ %d)
                    sp.Encode4(0); // Rainbow Week Bonus EXP(+%d)
                } else if (Version.GreaterOrEqual(Region.GMS, 111)) {
                    sp.Encode4(0);
                } else {
                    sp.Encode4(ma.Inc_EXP_WeddingBonus); // 結婚ボーナス経験値
                    sp.Encode4(0); // グループリングボーナスEXP (?)
                }
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

                if (Version.Equal(Region.GMS, 95)) {
                    sp.Encode4(0);
                    sp.Encode4(0);
                    break;
                }

                if (Version.GreaterOrEqual(Region.GMS, 111)) {
                    sp.Encode1(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    if (ma.InChat != 0) {
                        sp.Encode4(0);
                    }
                    break;
                }

                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                }
                if (ServerConfig.JMS194orLater()) {
                    sp.Encode1(0); // 0 or not
                }
                break;
            }
            // getSPMsg
            case MS_IncSPMessage: {
                sp.Encode2(ma.JobID);
                sp.Encode1(ma.Inc_SP);
                break;
            }
            // getShowFameGain
            case MS_IncPOPMessage: {
                sp.Encode4(ma.Inc_Fame);
                break;
            }
            // showMesoGain
            case MS_IncMoneyMessage: {
                sp.Encode4(ma.Inc_Meso);
                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    sp.Encode4(-1); // 別の数値だとメッセージ非表示
                }
                break;
            }
            // getGPMsg
            case MS_IncGPMessage: {
                sp.Encode4(ma.Inc_GP);
                break;
            }
            // getStatusMsg
            case MS_GiveBuffMessage: {
                sp.Encode4(ma.ItemID);
                break;
            }
            case MS_GeneralItemExpireMessage: {
                break;
            }
            // showQuestMsg
            case MS_SystemMessage: {
                sp.EncodeStr(ma.str);
                break;
            }
            // updateInfoQuest
            case MS_QuestRecordExMessage: {
                sp.Encode2(ma.QuestID);
                sp.EncodeStr(ma.str);
                break;
            }
            case MS_ItemProtectExpireMessage: {
                break;
            }
            case MS_ItemExpireReplaceMessage: {
                break;
            }
            case MS_SkillExpireMessage: {
                break;
            }
            // updateBeansMSG, GainTamaMessage
            case MS_JMS_Pachinko: {
                sp.Encode4(ma.Inc_Tama);
                break;
            }
            default: {
                break;
            }
        }
        return sp.get();
    }

    // CWvsContext::OnMemoResult
    public static MaplePacket MemoResult(ResultSet notes, int count) throws SQLException {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MemoResult);

        sp.Encode1(3);
        sp.Encode1(count);
        for (int i = 0; i < count; i++) {
            sp.Encode4(notes.getInt("id"));
            sp.EncodeStr(notes.getString("from"));
            sp.EncodeStr(notes.getString("message"));
            sp.Encode8(SharedDate.getTimestamp(notes.getLong("timestamp")));
            sp.Encode1(notes.getInt("gift"));
            notes.next();
        }
        return sp.get();
    }

    // CWvsContext::OnMapTransferResult
    public static MaplePacket MapTransferResult(MapleCharacter chr, OpsMapTransfer ops_res, boolean vip) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MapTransferResult);

        sp.Encode1(ops_res.get());
        sp.Encode1(vip ? 1 : 0);

        switch (ops_res) {
            case MapTransferRes_DeleteList:
            case MapTransferRes_RegisterList: {
                int map_list[] = vip ? chr.getRocks() : chr.getRegRocks();
                for (int map_id : map_list) {
                    sp.Encode4(map_id);
                }
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    // CWvsContext::OnAntiMacroResult
    // CWvsContext::OnClaimResult
    // CWvsContext::OnSetClaimSvrAvailableTime
    // CWvsContext::OnClaimSvrStatusChanged
    //CWvsContext::OnSetTamingMobInfo
    public static MaplePacket SetTamingMobInfo(TacosCharacter chr, boolean levelup) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetTamingMobInfo);

        sp.Encode4(chr.getId());
        sp.Encode4(chr.getMount().getLevel());
        sp.Encode4(chr.getMount().getExp());
        sp.Encode4(chr.getMount().getFatigue());
        sp.Encode1(levelup ? 1 : 0);
        return sp.get();
    }

    // CWvsContext::OnQuestClear
    public static MaplePacket QuestClear(int usQuestID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_QuestClear);

        sp.Encode2(usQuestID);
        return sp.get();
    }

    // CWvsContext::OnEntrustedShopCheckResult
    public static MaplePacket EntrustedShopCheckResult(OpsEntrustedShop ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_EntrustedShopCheckResult);

        sp.Encode1(ops.get());

        switch (ops) {
            case EntrustedShopRes_OpenImpossible_Using: {
                sp.Encode4(910000018); // used last 3 numbers to decide FreeMarket room.
                sp.Encode1(0); // channel
                break;
            }
            case EntrustedShopReq_SetMiniMapColor: {
                // may be res...
                sp.Encode4(123);
                break;
            }
            case EntrustedShopReq_RenameResult: {
                // may be res...
                sp.Encode1(1); // 0 = fail, 1 = success.
                break;
            }
            case EntrustedShopRes_GetPosResult: {
                // client sends change channel packet after this.
                sp.Encode4(0);
                sp.Encode1(0); // channel
                break;
            }
            case EntrustedShopRes_Enter: {
                // client sends enter shop packet after this.
                sp.Encode4(123); // own shop id.
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    // CWvsContext::OnSkillLearnItemResult
    public static MaplePacket SkillLearnItemResult(MapleCharacter chr, boolean bIsMaterbook, boolean bUsed, boolean bSucceed) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SkillLearnItemResult);

        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.PostBB()) {
            sp.Encode1(1); // bOnExclRequest
        }

        sp.Encode4(chr.getId());
        sp.Encode1(bIsMaterbook ? 1 : 0); // bIsMaterbook
        sp.Encode4(0); // not used
        sp.Encode4(0); // not used
        sp.Encode1(bUsed ? 1 : 0); // bUsed[0]
        sp.Encode1(bSucceed ? 1 : 0); // bSucceed
        return sp.get();
    }

    // CWvsContext::OnSkillResetItemResult
    // CWvsContext::OnGatherItemResult
    public static MaplePacket GatherItemResult(byte nTI) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GatherItemResult);

        sp.Encode1(0); // unused
        sp.Encode1(nTI);
        return sp.get();
    }

    // CWvsContext::OnSortItemResult
    public static MaplePacket SortItemResult(byte nTI) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SortItemResult);

        sp.Encode1(0); // unused
        sp.Encode1(nTI);
        return sp.get();
    }

    // CWvsContext::OnCharacterInfo
    public static MaplePacket CharacterInfo(MapleCharacter player, boolean isSelf) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CharacterInfo);
        boolean pet_summoned = false;
        for (final MaplePet pet : player.getPets()) {
            if (pet.getSummoned()) {
                pet_summoned = true;
                break;
            }
        }
        sp.Encode4(player.getId());
        sp.Encode1(player.getLevel());
        sp.Encode2(player.getJob());

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
            sp.Encode4(player.getFame());
        } else {
            sp.Encode2(player.getFame());
        }

        if (ServerConfig.JMS147orLater() || Version.GreaterOrEqual(Region.GMS, 61)) {
            sp.Encode1(player.getMarriageId() > 0 ? 1 : 0); // heart red or gray
        }
        String sCommunity = "-";
        String sAlliance = "";
        // Guild
        if (player.getGuildId() <= 0) {
            MapleGuild guild = OdinWorld.Guild.getGuild(player.getGuildId());
            if (guild != null) {
                sCommunity = guild.getName();
                // Alliance
                if (guild.getAllianceId() > 0) {
                    MapleGuildAlliance alliance = OdinWorld.Alliance.getAlliance(guild.getAllianceId());
                    if (alliance != null) {
                        sAlliance = alliance.getName();
                    }
                }
            }
        }

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
        }

        sp.EncodeStr(sCommunity);
        if (ServerConfig.JMS147orLater() || Version.GreaterOrEqual(Region.GMS, 61)) {
            sp.EncodeStr(sAlliance);
        }
        // Pre-BB
        if (Version.Between(Region.JMS, 180, 186)) {
            sp.Encode4(0);
            sp.Encode4(0);
        }
        if (Version.PostBB()) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
        }
        sp.Encode1((player.getPet(0) != null) ? 1 : 0); // pet button clickable
        if (Version.LessOrEqual(Region.JMS, 131)) {
            // inlined?
            if (player.getPet(0) != null) {
                sp.EncodeBuffer(DataCUIUserInfo.SetPetInfo_JMS131(player, player.getPet(0)));
            }
        } else {
            // CUIUserInfo::SetPetInfo
            sp.EncodeBuffer(DataCUIUserInfo.SetPetInfo(player));
        }

        // CUIUserInfo::SetTamingMobInfo
        IItem inv_mount = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
        boolean TamingMobEnabled = false;
        final MapleMount tm = player.getMount();
        if (tm != null && inv_mount != null) {
            TamingMobEnabled = MapleItemInformationProvider.getInstance().getReqLevel(inv_mount.getItemId()) <= player.getLevel();
        }
        sp.Encode1(TamingMobEnabled ? 1 : 0);
        if (tm != null && TamingMobEnabled) {
            sp.Encode4(tm.getLevel());
            sp.Encode4(tm.getExp());
            sp.Encode4(tm.getFatigue());
        }

        // CUIUserInfo::SetWishItemInfo
        final int wishlistSize = player.getWishlistSize();
        sp.Encode1(wishlistSize);
        if (wishlistSize > 0) {
            // CInPacket::DecodeBuffer(v4, iPacket, 4 * wishlistSize);
            final int[] wishlist = player.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                sp.Encode4(wishlist[x]);
            }
        }
        if (ServerConfig.JMS147orLater() || Version.GreaterOrEqual(Region.GMS, 61)) {
            // Monster Book (JMS)
            sp.EncodeBuffer(player.getMonsterBook().MonsterBookInfo(player.getMonsterBookCover()));
        }
        if (ServerConfig.JMS180orLater() || Version.GreaterOrEqual(Region.KMS, 84) || Version.GreaterOrEqual(Region.GMS, 83)) {
            // MedalAchievementInfo::Decode
            IItem inv_medal = player.getInventory(MapleInventoryType.EQUIPPED).getItem(OpsBodyPart.BP_MEDAL.getSlot());
            sp.Encode4(inv_medal == null ? 0 : inv_medal.getItemId());
            List<Integer> medalQuests = new ArrayList<Integer>();
            List<MapleQuestStatus> completed = player.getCompletedQuests();
            for (MapleQuestStatus q : completed) {
                if (q.getQuest().getMedalItem() > 0 && GameConstants.getInventoryType(q.getQuest().getMedalItem()) == MapleInventoryType.EQUIP) {
                    //chair kind medal viewmedal is weird
                    medalQuests.add(q.getQuest().getId());
                }
            }
            sp.Encode2(medalQuests.size());
            for (int x : medalQuests) {
                sp.Encode2(x);
                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    sp.Encode8(0);
                }
            }
            // JMS v180-v186, v187以降消滅
            if (Version.PreBB() && (Region.IsJMS() || Version.GreaterOrEqual(Region.GMS, 91))) {
                // Chair List
                sp.Encode4(player.getInventory(MapleInventoryType.SETUP).list().size());
                // CInPacket::DecodeBuffer(v4, iPacket, 4 * chairs);
                for (IItem chair : player.getInventory(MapleInventoryType.SETUP).list()) {
                    sp.Encode4(chair.getItemId());
                }
            }

            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode1(0);
                sp.Encode1(0);
                sp.Encode1(0);
                sp.Encode1(0);
                sp.Encode1(0);
                sp.Encode1(0);
                for (int i = 0; i < 3; i++) {
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                }
            }

        }
        return sp.get();
    }

    public static MaplePacket PartyResult(OpsParty ops) {
        return PartyResult(ops, null);
    }

    // CWvsContext::OnPartyResult
    public static MaplePacket PartyResult(OpsParty ops, MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PartyResult);

        MapleParty party = chr.getParty();
        int forChannel = chr.getChannelId();
        sp.Encode1(ops.get());

        switch (ops) {
            case PartyReq_InviteParty: {
                sp.Encode4(party.getId());
                sp.EncodeStr(chr.getName());
                sp.Encode4(chr.getLevel());
                if (Version.GreaterOrEqual(Region.JMS, 186)) {
                    sp.Encode4(chr.getJob());
                }
                sp.Encode1(0); // auto join.
                break;
            }
            case PartyRes_LoadParty_Done: {
                sp.Encode4(party.getId());
                sp.EncodeBuffer(addPartyStatus(forChannel, party, true));
                break;
            }
            case PartyRes_CreateNewParty_Done: {
                sp.Encode4(party.getId());
                sp.Encode4(999999999);
                sp.Encode4(999999999);
                sp.Encode8(0);
                break;
            }
            case PartyRes_CreateNewParty_AlreayJoined: {
                break;
            }
            case PartyRes_CreateNewParty_Beginner: {
                break;
            }
            case PartyRes_CreateNewParty_Unknown: {
                break;
            }
            case PartyRes_WithdrawParty_Done: {
                break;
            }
            case PartyRes_WithdrawParty_NotJoined: {
                break;
            }
            case PartyRes_WithdrawParty_Unknown: {
                break;
            }
            case PartyRes_JoinParty_Done: {
                break;
            }
            case PartyRes_JoinParty_Done2: {
                break;
            }
            case PartyRes_JoinParty_AlreadyJoined: {
                break;
            }
            case PartyRes_JoinParty_AlreadyFull: {
                break;
            }
            case PartyRes_JoinParty_OverDesiredSize: {
                break;
            }
            case PartyRes_JoinParty_UnknownUser: {
                break;
            }
            case PartyRes_JoinParty_Unknown: {
                break;
            }
            case PartyRes_InviteParty_Sent: {
                sp.EncodeStr(chr.getName());
                break;
            }
            case PartyRes_InviteParty_BlockedUser: {
                sp.EncodeStr(chr.getName());
                break;
            }
            case PartyRes_InviteParty_AlreadyInvited: {
                break;
            }
            case PartyRes_InviteParty_AlreadyInvitedByInviter: {
                break;
            }
            case PartyRes_InviteParty_Rejected: {
                break;
            }
            case PartyRes_InviteParty_Accepted: {
                break;
            }
            case PartyRes_KickParty_Done: {
                break;
            }
            case PartyRes_KickParty_FieldLimit: {
                break;
            }
            case PartyRes_KickParty_Unknown: {
                break;
            }
            case PartyRes_ChangePartyBoss_Done: {
                sp.Encode4(chr.getId());
                sp.Encode1(1); // dc or not.
                break;
            }
            case PartyRes_ChangePartyBoss_NotSameField: {
                break;
            }
            case PartyRes_ChangePartyBoss_NoMemberInSameField: {
                break;
            }
            case PartyRes_ChangePartyBoss_NotSameChannel: {
                break;
            }
            case PartyRes_ChangePartyBoss_Unknown: {
                break;
            }
            case PartyRes_AdminCannotCreate: {
                break;
            }
            case PartyRes_AdminCannotInvite: {
                break;
            }
            case PartyRes_UserMigration: {
                break;
            }
            case PartyRes_ChangeLevelOrJob: {
                break;
            }
            case PartyRes_SuccessToSelectPQReward: {
                break;
            }
            case PartyRes_FailToSelectPQReward: {
                break;
            }
            case PartyRes_ReceivePQReward: {
                break;
            }
            case PartyRes_FailToRequestPQReward: {
                break;
            }
            case PartyRes_CanNotInThisField: {
                break;
            }
            case PartyRes_ServerMsg: {
                break;
            }
            case PartyInfo_TownPortalChanged: {
                List<MapleDoor> doors = chr.getDoors();
                MapleDoor door = doors.isEmpty() ? null : doors.get(0);
                TacosPortal door_portal = (door != null) ? door.getTownPortal() : null;

                sp.Encode1(door_portal != null ? door_portal.getMysticDoorId() : 0); // number
                sp.Encode4((door != null) ? door.getMapId() : 0);
                sp.Encode4((door != null) ? door.getLink().getMapId() : 0);
                sp.Encode4((door != null) ? door.getSkillId() : 0);
                sp.Encode2((door != null) ? door.getLink().getPosition().x : 0);
                sp.Encode2((door != null) ? door.getLink().getPosition().y : 0);
                break;
            }
            case PartyInfo_OpenGate: {
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    // PARTYDATA::Decode
    private static byte[] addPartyStatus(int forchannel, MapleParty party, boolean leaving) {
        ServerPacket data = new ServerPacket();

        List<MaplePartyCharacter> partymembers = new ArrayList<>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            data.Encode4(partychar.getId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            data.EncodeBuffer(partychar.getName(), 13);
        }
        for (MaplePartyCharacter partychar : partymembers) {
            data.Encode4(partychar.getJobId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            data.Encode4(partychar.getLevel());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            data.Encode4(partychar.isOnline() ? partychar.getChannel() - 1 : -2);
        }
        data.Encode4(party.getLeader().getId());
        for (MaplePartyCharacter partychar : partymembers) {
            data.Encode4(partychar.getChannel() == forchannel ? partychar.getMapid() : 0);
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel && !leaving) {
                data.Encode4(partychar.getDoorTown());
                data.Encode4(partychar.getDoorTarget());
                data.Encode4(partychar.getDoorSkill());
                data.Encode4(partychar.getDoorPosition().x);
                data.Encode4(partychar.getDoorPosition().y);
            } else {
                data.Encode4(leaving ? 999999999 : 0);
                data.Encode8(leaving ? 999999999 : 0);
                data.Encode8(leaving ? -1 : 0);
            }
        }

        return data.get().getBytes();
    }

    public static MaplePacket PartyResult(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PartyResult);

        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                sp.Encode1(12);
                sp.Encode4(party.getId());
                sp.Encode4(target.getId());
                sp.Encode1(op == PartyOperation.DISBAND ? 0 : 1);
                if (op == PartyOperation.DISBAND) {
                    sp.Encode4(target.getId());
                } else {
                    sp.Encode1(op == PartyOperation.EXPEL ? 1 : 0);
                    sp.EncodeStr(target.getName());
                    sp.EncodeBuffer(addPartyStatus(forChannel, party, op == PartyOperation.LEAVE));
                }
                break;
            case JOIN:
                sp.Encode1(15);
                sp.Encode4(party.getId());
                sp.EncodeStr(target.getName());
                sp.EncodeBuffer(addPartyStatus(forChannel, party, false));
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                sp.Encode1(7);
                sp.Encode4(party.getId());
                sp.EncodeBuffer(addPartyStatus(forChannel, party, op == PartyOperation.LOG_ONOFF));
                break;
            case CHANGE_LEADER:
            case CHANGE_LEADER_DC:
                sp.Encode1(31); //test
                sp.Encode4(target.getId());
                sp.Encode1(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                break;
            //1D = expel function not available in this map.
        }
        return sp.get();
    }

    // CWvsContext::OnFriendResult
    public static MaplePacket FriendResult(ArgFriend frs) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FriendResult);
        sp.Encode1(frs.flag.get());
        switch (frs.flag) {
            case FriendRes_LoadFriend_Done:
            case FriendRes_SetFriend_Done:
            case FriendRes_DeleteFriend_Done: {
                sp.EncodeBuffer(DataCWvsContext.CFriend_Reset(frs.chr));
                break;
            }
            case FriendRes_NotifyChange_FriendInfo: {
                break;
            }
            case FriendRes_Invite: {
                sp.Encode4(frs.friend_id); // dwFriendID
                sp.EncodeStr(frs.friend_name);
                sp.Encode4(frs.friend_level); // nLevel
                sp.Encode4(frs.friend_job); // nJobCode
                // CWvsContext::CFriend::Insert, 39 bytes
                sp.Encode4(frs.friend_id);
                sp.EncodeBuffer(frs.friend_name, 13);
                sp.Encode1(0);
                sp.Encode4(frs.friend_channel == -1 ? -1 : frs.friend_channel - 1); // please add channel
                sp.EncodeBuffer(frs.friend_tag, 17);
                // 1 byte
                sp.Encode1(1);
                break;
            }
            case FriendRes_SetFriend_FullMe: {
                // none
                break;
            }
            case FriendRes_SetFriend_FullOther: {
                // none
                break;
            }
            case FriendRes_SetFriend_AlreadySet: {
                break;
            }
            case FriendRes_SetFriend_Master: {
                break;
            }
            case FriendRes_SetFriend_UnknownUser: {
                // none
                break;
            }
            case FriendRes_SetFriend_Unknown: {
                break;
            }
            case FriendRes_AcceptFriend_Unknown: {
                break;
            }
            case FriendRes_DeleteFriend_Unknown: {
                break;
            }
            case FriendRes_Notify: {
                sp.Encode4(frs.friend_id);
                sp.Encode1(0);
                sp.Encode4(frs.friend_channel);
                break;
            }
            case FriendRes_IncMaxCount_Done: {
                sp.Encode1(frs.nFriendMax);
                break;
            }
            case FriendRes_IncMaxCount_Unknown: {
                break;
            }
            case FriendRes_PleaseWait: {
                break;
            }
            default: {
                DebugLogger.ErrorLog("FriendResult not coded : " + frs.flag);
                break;
            }
        }

        return sp.get();
    }

    // CWvsContext::OnExpedtionResult
    // CWvsContext::OnFriendResult
    // CWvsContext::OnGuildResult
    // CWvsContext::OnAllianceResult
    // CWvsContext::OnTownPortal
    // CWvsContext::OnOpenGate
    // CWvsContext::OnBroadcastMsg
    public static MaplePacket BroadcastMsg(ArgBroadcastMsg bma) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BroadcastMsg);
        sp.Encode1(bma.bm.get());

        switch (bma.bm) {
            case BM_NOTICE: // 青文字 [告知事項]
            case BM_ALERT: // ダイアログ
            case BM_EVENT: // ピンク文字
            {
                sp.EncodeStr(bma.message);
                break;
            }
            case BM_SLIDE: // 画面上部の横スクロールメッセージ
            {
                boolean show_msg = bma.message.length() != 0;
                sp.Encode1(show_msg ? 1 : 0);
                if (show_msg) {
                    sp.EncodeStr(bma.message);
                }
                break;
            }
            case BM_NOTICEWITHOUTPREFIX: // 青文字, アイテム情報
            {
                sp.EncodeStr(bma.message);
                sp.Encode4(bma.item_id);
                break;
            }
            case BM_SPEAKERCHANNEL: // 5070000, メガホン
            {
                String text = ReqSub_UserConsumeCashItemUseRequest.MegaphoneGetSenderName(bma.chr) + " : " + bma.message;
                sp.EncodeStr(text);
                break;
            }
            case BM_SPEAKERWORLD: // 5071000, 拡声器
            case BM_HEARTSPEAKER: // 5073000, ハート拡声器
            case BM_SKULLSPEAKER: // 5074000, ドクロ拡声器
            {
                String text = ReqSub_UserConsumeCashItemUseRequest.MegaphoneGetSenderName(bma.chr) + " : " + bma.message;
                int channel = bma.chr.getClient().getChannelId() - 1;
                sp.EncodeStr(text);
                sp.Encode1(channel);
                sp.Encode1(bma.ear);
                break;
            }
            case BM_ITEMSPEAKER: // 5076000, アイテム拡声器
            {
                String text = ReqSub_UserConsumeCashItemUseRequest.MegaphoneGetSenderName(bma.chr) + " : " + bma.message;
                int channel = bma.chr.getClient().getChannelId() - 1;
                boolean show_item = bma.item != null;
                sp.EncodeStr(text);
                sp.Encode1(channel);
                sp.Encode1(bma.ear);
                sp.Encode1(show_item ? 1 : 0);
                if (show_item) {
                    sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(bma.item));
                }
                break;
            }
            case MEGAPHONE_TRIPLE: // 5077000, 三連拡声器
            {
                String name = ReqSub_UserConsumeCashItemUseRequest.MegaphoneGetSenderName(bma.chr);
                int channel = bma.chr.getClient().getChannelId() - 1;
                String text1 = bma.messages.get(0); // ?_?

                sp.EncodeStr(name + " : " + text1);
                sp.Encode1(bma.messages.size());
                for (int i = 1; i < bma.messages.size(); i++) {
                    sp.EncodeStr(name + " : " + bma.messages.get(i));
                }
                sp.Encode1(channel);
                sp.Encode1(bma.ear);
                break;
            }
            case BM_GACHAPONANNOUNCE: {
                String text = bma.chr.getName() + " : " + bma.message;
                sp.EncodeStr(text);
                sp.Encode4(bma.gashapon_type); // 緑 (0) or 茶色 (-1)
                sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(bma.item));
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    // CWvsContext::OnIncubatorResult
    public static MaplePacket IncubatorResult(int itemId, short quantity, int itemId2, short quantity2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_IncubatorResult);

        sp.Encode4(itemId);
        sp.Encode2(quantity);
        sp.Encode4(5060003);
        sp.Encode4(itemId2);
        sp.Encode4(quantity2);
        return sp.get();
    }

    // CWvsContext::OnShopScannerResult
    public static MaplePacket ShopScannerResult(OpsShopScanner ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ShopScannerResult);

        sp.Encode1(ops.get());
        switch (ops) {
            case ShopScannerRes_SearchResult: {
                sp.Encode4(4000000); // item id
                sp.Encode4(1); // 0 -> fail
                sp.EncodeStr("マノ");
                sp.Encode4(18); // shop id or other channel FM num
                sp.EncodeStr("デンデンのカラ売ります");
                sp.Encode4(200); // 数量
                sp.Encode4(1); // バンドル
                sp.Encode4(500); // 価格
                sp.Encode4(910000018); // map id
                sp.Encode1(1); // channel
                sp.Encode1(0);
                break;
            }
            case ShopScannerRes_LoadHotListResult: {
                int hotlist[] = {4000000, 4000016, 4000019};
                sp.Encode1(hotlist.length);
                for (int item_id : hotlist) {
                    sp.Encode4(item_id);
                }
                break;
            }
            default: {
                break;
            }
        }
        return sp.get();
    }

    // CWvsContext::OnShopLinkResult
    // CWvsContext::OnMarriageRequest
    public static MaplePacket MarriageRequest(String name, int cid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MarriageRequest);

        sp.Encode1(0); //mode, 0 = engage, 1 = cancel, 2 = answer.. etc
        sp.EncodeStr(name); // name
        sp.Encode4(cid); // playerid
        return sp.get();
    }

    // CWvsContext::OnMarriageResult
    public static MaplePacket MarriageResult(OpsMarriage ops, int item_id, MapleCharacter male, MapleCharacter female) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MarriageResult);

        sp.Encode1(ops.get()); // 1103 custom quest
        switch (ops) {
            case MarriageRes_Engaged:
            case MarriageRes_Married: {
                // GW_MarriageRecord::Decode, 48 bytes.
                {
                    sp.Encode4(0); // dwMarriageNo
                    sp.Encode4(male.getId()); // dwGroomID
                    sp.Encode4(female.getId()); // dwBrideID
                    sp.Encode2(1); // usStatus
                    sp.Encode4(item_id); // nGroomItemID
                    sp.Encode4(item_id); // nBrideItemID
                    sp.EncodeBuffer(male.getName(), 13); // sGroomName
                    sp.EncodeBuffer(female.getName(), 13); // sBrideName
                }
                break;
            }
            case MarriageRes_ShowInvitation: {
                sp.EncodeStr("");
                sp.EncodeStr("");
                sp.Encode2(0);
                break;
            }
            case MarriageRes_Unknown: {
                boolean is_msg = false;
                sp.Encode1(is_msg ? 1 : 0);
                if (is_msg) {
                    sp.EncodeStr("");
                }
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    // CWvsContext::OnWeddingGiftResult
    // CWvsContext::OnNotifyMarriedPartnerMapTransfer
    // CWvsContext::OnCashPetFoodResult
    // CWvsContext::OnSetWeekEventMessage
    public static MaplePacket SetWeekEventMessage(String text) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetWeekEventMessage);

        sp.Encode1(-1);
        sp.EncodeStr(text);
        return sp.get();
    }

    // パチンコ情報の更新
    public static MaplePacket PachinkoResult(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoResult);
        // 12 bytes
        {
            sp.Encode4(chr.getId()); // キャラクターID (実質不要)
            sp.Encode4(chr.getTama()); // アイテム欄の玉の数に反映される値
            sp.Encode4(0); // 用途不明
        }
        return sp.get();
    }

    public static MaplePacket fishingUpdate(byte type, int id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_Fishing_BoardUpdate);

        sp.Encode1(type);
        sp.Encode4(id);
        return sp.get();
    }

    // CWvsContext::OnSetPotionDiscountRate
    // CWvsContext::OnBridleMobCatchFail
    // CWvsContext::OnImitatedNPCResult
    // CWvsContext::OnImitatedNPCData
    // CWvsContext::OnLimitedNPCDisableInfo
    // CWvsContext::OnMonsterBookSetCard
    public static MaplePacket MonsterBookSetCard(boolean full, int cardid, int level) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MonsterBookSetCard);

        sp.Encode1(full ? 0 : 1);
        if (!full) {
            sp.Encode4(cardid);
            sp.Encode4(level);
        }
        return sp.get();
    }

    // CWvsContext::OnMonsterBookSetCover
    public static MaplePacket MonsterBookSetCover(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MonsterBookSetCover);

        sp.Encode4(chr.getMonsterBookCover());
        return sp.get();
    }

    // CWvsContext::OnHourChanged
    // CWvsContext::OnMiniMapOnOff
    public static MaplePacket MiniMapOnOff() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniMapOnOff);

        sp.Encode1(0); // m_bMiniMapOnOff
        return sp.get();
    }

    // CWvsContext::OnConsultAuthkeyUpdate
    // CWvsContext::OnClassCompetitionAuthkeyUpdate
    // CWvsContext::OnWebBoardAuthkeyUpdate
    // CWvsContext::OnSessionValue
    // CWvsContext::OnPartyValue
    // CWvsContext::OnFieldSetVariable
    public static MaplePacket sendString(final int type, String object, final String amount) {
        ServerPacketHeader header = ServerPacketHeader.UNKNOWN;

        switch (type) {
            case 1:
                header = ServerPacketHeader.LP_SessionValue;
                break;
            case 2:
                header = ServerPacketHeader.LP_PartyValue;
                break;
            case 3:
                header = ServerPacketHeader.LP_FieldSetVariable;
                break;
            default: {
                break;
            }
        }

        ServerPacket sp = new ServerPacket(header);

        sp.EncodeStr(object); //massacre_hit, massacre_cool, massacre_miss, massacre_party, massacre_laststage, massacre_skill
        sp.EncodeStr(amount);
        return sp.get();
    }

    // CWvsContext::OnBonusExpRateChanged
    public static MaplePacket BonusExpRateChanged(int type, int percent) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BonusExpRateChanged);

        sp.Encode2(21); // 0x15
        sp.Encode4(0); // idk
        sp.Encode2(0); // idk
        sp.Encode2(percent); // percent
        sp.Encode2(0); // idk
        return sp.get();
    }

    // CWvsContext::OnFamilyChartResult
    // CWvsContext::OnFamilyInfoResult
    // CWvsContext::OnFamilyResult
    // CWvsContext::OnFamilyJoinRequest
    // CWvsContext::OnFamilyJoinAccepted
    // CWvsContext::OnFamilyPrivilegeList
    // CWvsContext::OnFamilyFamousPointIncResult
    // CWvsContext::OnFamilyNotifyLoginOrLogout
    // CWvsContext::OnFamilySetPrivilege
    // CWvsContext::OnFamilySummonRequest
    // CWvsContext::OnNotifyLevelUp
    // CWvsContext::OnNotifyWedding
    // CWvsContext::OnNotifyJobChange
    public static MaplePacket AvatarMegaphoneUpdateMessage(MapleCharacter chr, int channel, int itemId, String message, boolean ear) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AvatarMegaphoneUpdateMessage);

        sp.Encode4(itemId);
        sp.EncodeStr(chr.getName());
        sp.EncodeStr(message);
        sp.Encode4(channel - 1); // channel
        sp.Encode1(ear ? 1 : 0);
        sp.EncodeBuffer(DataAvatarLook.Encode(chr));
        return sp.get();
    }

    // CWvsContext::OnSuccessInUsegachaponBox
    public static MaplePacket SuccessInUseGachaponBox(int box_item_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SuccessInUseGachaponBox);
        sp.Encode4(box_item_id);
        return sp.get();
    }

    // CWvsContext::OnNewYearCardRes
    // CWvsContext::OnRandomMorphRes
    // CWvsContext::OnCancelNameChangebyOther
    // CWvsContext::OnSetBuyEquipExt
    // CWvsContext::OnSetPassenserRequest
    public static MaplePacket SetPassenserRequest(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetPassenserRequest);

        sp.Encode4(chr.getId()); // nPassenserID
        return sp.get();
    }

    // CWvsContext::OnScriptProgressMessage
    public static MaplePacket ScriptProgressMessage(String msg) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ScriptProgressMessage);

        sp.EncodeStr(msg);
        return sp.get();
    }

    // CWvsContext::OnDataCRCCheckFailed
    // CWvsContext::OnCakePieEventResult
    // CWvsContext::OnUpdateGMBoard
    // CWvsContext::OnShowSlotMessage
    // CWvsContext::OnWildHunterInfo
    // CWvsContext::OnAccountMoreInfo
    // CWvsContext::OnFindFirend
    // CWvsContext::OnStageChange
    // CWvsContext::OnDragonBallBox
    // CWvsContext::OnAskWhetherUsePamsSong
    // CWvsContext::OnTransferChannel
    // CWvsContext::OnDisallowedDeliveryQuestList
    public static MaplePacket CharacterCash(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CMS_CharacterCash);

        sp.Encode4(chr.getId());
        sp.Encode4(chr.getMaplePoint());
        return sp.get();
    }

    public static MaplePacket guildNotice(int gid, String notice) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(68);
        sp.Encode4(gid);
        sp.EncodeStr(notice);
        return sp.get();
    }

    //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    public static MaplePacket memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(bExpelled ? 47 : 44);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.EncodeStr(mgc.getName());
        return sp.get();
    }

    private static byte[] getGuildInfo(MapleGuild guild) {
        ServerPacket data = new ServerPacket();

        data.Encode4(guild.getId());
        data.EncodeStr(guild.getName());

        for (int i = 1; i <= 5; i++) {
            data.EncodeStr(guild.getRankTitle(i));
        }

        data.EncodeBuffer(guild.addMemberData());
        data.Encode4(guild.getCapacity());
        data.Encode2(guild.getLogoBG());
        data.Encode1(guild.getLogoBGColor());
        data.Encode2(guild.getLogo());
        data.Encode1(guild.getLogoColor());
        data.EncodeStr(guild.getNotice());
        data.Encode4(guild.getGP());
        data.Encode4(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);

        return data.get().getBytes();
    }

    public static MaplePacket changeAlliance(MapleGuildAlliance alliance, final boolean in) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(1);
        sp.Encode1(in ? 1 : 0);
        sp.Encode4(in ? alliance.getId() : 0);
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < noGuilds; i++) {
            g[i] = OdinWorld.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                //return WrapCWvsContext.updateStat();
                return null;
            }
        }
        sp.Encode1(noGuilds);
        for (int i = 0; i < noGuilds; i++) {
            sp.Encode4(g[i].getId());
            //must be world
            Collection<MapleGuildCharacter> members = g[i].getMembers();
            sp.Encode4(members.size());
            for (MapleGuildCharacter mgc : members) {
                sp.Encode4(mgc.getId());
                sp.Encode1(in ? mgc.getAllianceRank() : 0);
            }
        }
        return sp.get();
    }

    public static MaplePacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(60);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.Encode4(mgc.getLevel());
        sp.Encode4(mgc.getJobId());
        return sp.get();
    }

    public static MaplePacket allianceMemberOnline(int alliance, int gid, int id, boolean online) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(14);
        sp.Encode4(alliance);
        sp.Encode4(gid);
        sp.Encode4(id);
        sp.Encode1(online ? 1 : 0);
        return sp.get();
    }

    public static MaplePacket changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, final boolean add) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(4);
        sp.Encode4(add ? alliance.getId() : 0);
        sp.Encode4(guild.getId());
        Collection<MapleGuildCharacter> members = guild.getMembers();
        sp.Encode4(members.size());
        for (MapleGuildCharacter mgc : members) {
            sp.Encode4(mgc.getId());
            sp.Encode1(add ? mgc.getAllianceRank() : 0);
        }
        return sp.get();
    }

    public static MaplePacket disbandAlliance(int alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(29);
        sp.Encode4(alliance);
        return sp.get();
    }

    public static MaplePacket newGuildMember(MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(39);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.EncodeBuffer(mgc.getName(), 13);
        sp.Encode4(mgc.getJobId());
        sp.Encode4(mgc.getLevel());
        sp.Encode4(mgc.getGuildRank()); //should be always 5 but whatevs
        sp.Encode4(mgc.isOnline() ? 1 : 0); //should always be 1 too
        sp.Encode4(1); //? could be guild signature, but doesn't seem to matter
        sp.Encode4(mgc.getAllianceRank()); //should always 3
        return sp.get();
    }

    public static MaplePacket updateAllianceRank(int allianceid, MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(27);
        sp.Encode4(allianceid);
        sp.Encode4(mgc.getId());
        sp.Encode4(mgc.getAllianceRank());
        return sp.get();
    }

    public static MaplePacket getGuildAlliance(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(13);
        if (alliance == null) {
            sp.Encode4(0);
            return sp.get();
        }
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = OdinWorld.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                //return WrapCWvsContext.updateStat();
                return null;
            }
        }
        sp.Encode4(noGuilds);
        for (MapleGuild gg : g) {
            sp.EncodeBuffer(getGuildInfo(gg));
        }
        return sp.get();
    }

    public static MaplePacket guildMemberOnline(int gid, int cid, boolean bOnline) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(61);
        sp.Encode4(gid);
        sp.Encode4(cid);
        sp.Encode1(bOnline ? 1 : 0);
        return sp.get();
    }

    public static MaplePacket changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(2);
        sp.Encode4(allianceid);
        sp.Encode4(oldLeader);
        sp.Encode4(newLeader);
        return sp.get();
    }

    public static MaplePacket showGuildRanks(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(73);
        sp.Encode4(npcid);
        sp.Encode4(all.size());
        for (MapleGuildRanking.GuildRankingInfo info : all) {
            sp.EncodeStr(info.getName());
            sp.Encode4(info.getGP());
            sp.Encode4(info.getLogo());
            sp.Encode4(info.getLogoColor());
            sp.Encode4(info.getLogoBg());
            sp.Encode4(info.getLogoBgColor());
        }
        return sp.get();
    }

    public static MaplePacket denyGuildInvitation(String charname) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(55);
        sp.EncodeStr(charname);
        return sp.get();
    }

    public static MaplePacket showGuildInfo(MapleCharacter c) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(26); //signature for showing guild info
        if (c == null || c.getMGC() == null) {
            //show empty guild (used for leaving, expelled)
            sp.Encode1(0);
            return sp.get();
        }
        MapleGuild g = OdinWorld.Guild.getGuild(c.getGuildId());
        if (g == null) {
            //failed to read from DB - don't show a guild
            sp.Encode1(0);
            return sp.get();
        }
        sp.Encode1(1); //bInGuild
        sp.EncodeBuffer(getGuildInfo(g));
        return sp.get();
    }

    public static MaplePacket guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(66);
        sp.Encode4(gid);
        sp.Encode2(bg);
        sp.Encode1(bgcolor);
        sp.Encode2(logo);
        sp.Encode1(logocolor);
        return sp.get();
    }

    private static byte[] addAllianceInfo(MapleGuildAlliance alliance) {
        ServerPacket data = new ServerPacket();

        data.Encode4(alliance.getId());
        data.EncodeStr(alliance.getName());

        for (int i = 1; i <= 5; i++) {
            data.EncodeStr(alliance.getRank(i));
        }

        data.Encode1(alliance.getNoGuilds());
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            data.Encode4(alliance.getGuildId(i));
        }

        data.Encode4(alliance.getCapacity()); // ????
        data.EncodeStr(alliance.getNotice());

        return data.get().getBytes();
    }

    public static MaplePacket guildDisband(int gid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(50);
        sp.Encode4(gid);
        sp.Encode1(1);
        return sp.get();
    }

    public static MaplePacket getAllianceInfo(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(12);
        sp.Encode1(alliance == null ? 0 : 1); //in an alliance
        if (alliance != null) {
            sp.EncodeBuffer(addAllianceInfo(alliance));
        }
        return sp.get();
    }

    public static MaplePacket updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(25);
        sp.Encode4(allianceid);
        sp.Encode4(oldLeader);
        sp.Encode4(newLeader);
        return sp.get();
    }

    public static MaplePacket addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(18);
        sp.EncodeBuffer(addAllianceInfo(alliance));
        sp.Encode4(newGuild.getId()); //???
        sp.EncodeBuffer(getGuildInfo(newGuild));
        sp.Encode1(0); //???
        return sp.get();
    }

    public static MaplePacket updateAlliance(MapleGuildCharacter mgc, int allianceid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(24);
        sp.Encode4(allianceid);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.Encode4(mgc.getLevel());
        sp.Encode4(mgc.getJobId());
        return sp.get();
    }

    public static MaplePacket sendAllianceInvite(String allianceName, MapleCharacter inviter) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(3);
        sp.Encode4(inviter.getGuildId());
        sp.EncodeStr(inviter.getName());
        //alliance invite did NOT change
        sp.EncodeStr(allianceName);
        return sp.get();
    }

    private static byte[] addThread(MapleBBSThread rs) {
        ServerPacket data = new ServerPacket();

        data.Encode4(rs.localthreadID);
        data.Encode4(rs.ownerID);
        data.EncodeStr(rs.name);
        data.Encode8(SharedDate.getTimestamp(rs.timestamp));
        data.Encode4(rs.icon);
        data.Encode4(rs.getReplyCount());
        return data.get().getBytes();
    }

    public static MaplePacket showThread(MapleBBSThread thread) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildBBS);

        sp.Encode1(7);
        sp.Encode4(thread.localthreadID);
        sp.Encode4(thread.ownerID);
        sp.Encode8(SharedDate.getTimestamp(thread.timestamp));
        sp.EncodeStr(thread.name);
        sp.EncodeStr(thread.text);
        sp.Encode4(thread.icon);
        sp.Encode4(thread.getReplyCount());
        for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
            sp.Encode4(reply.replyid);
            sp.Encode4(reply.ownerID);
            sp.Encode8(SharedDate.getTimestamp(reply.timestamp));
            sp.EncodeStr(reply.content);
        }
        return sp.get();
    }

    public static MaplePacket BBSThreadList(final List<MapleBBSThread> bbs, int start) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildBBS);

        sp.Encode1(6);
        if (bbs == null) {
            sp.Encode1(0);
            sp.Encode8(0);
            return sp.get();
        }
        int threadCount = bbs.size();
        MapleBBSThread notice = null;
        for (MapleBBSThread b : bbs) {
            if (b.isNotice()) {
                //notice
                notice = b;
                break;
            }
        }
        final int ret = notice == null ? 0 : 1;
        sp.Encode1(ret);
        if (notice != null) {
            //has a notice
            sp.EncodeBuffer(addThread(notice));
            threadCount--; //one thread didn't count (because it's a notice)
        }
        if (threadCount < start) {
            //seek to the thread before where we start
            //uh, we're trying to start at a place past possible
            start = 0;
        }
        //each page has 10 threads, start = page # in packet but not here
        sp.Encode4(threadCount);
        final int pages = Math.min(10, threadCount - start);
        sp.Encode4(pages);
        for (int i = 0; i < pages; i++) {
            sp.EncodeBuffer(addThread(bbs.get(start + i + ret))); //because 0 = notice
        }
        return sp.get();
    }

    public static MaplePacket createGuildAlliance(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(15);
        sp.EncodeBuffer(addAllianceInfo(alliance));
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = OdinWorld.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                //return WrapCWvsContext.updateStat();
                return null;
            }
        }
        for (MapleGuild gg : g) {
            sp.EncodeBuffer(getGuildInfo(gg));
        }
        return sp.get();
    }

    public static MaplePacket rankTitleChange(int gid, String[] ranks) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(62);
        sp.Encode4(gid);
        for (String r : ranks) {
            sp.EncodeStr(r);
        }
        return sp.get();
    }

    public static MaplePacket genericGuildMessage(byte code) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(code);
        return sp.get();
    }

    public static MaplePacket removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(16);
        sp.EncodeBuffer(addAllianceInfo(alliance));
        sp.EncodeBuffer(getGuildInfo(expelledGuild));
        sp.Encode1(expelled ? 1 : 0); //1 = expelled, 0 = left
        return sp.get();
    }

    public static MaplePacket guildCapacityChange(int gid, int capacity) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(58);
        sp.Encode4(gid);
        sp.Encode1(capacity);
        return sp.get();
    }

    public static MaplePacket updateGP(int gid, int GP) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(72);
        sp.Encode4(gid);
        sp.Encode4(GP);
        return sp.get();
    }

    public static MaplePacket getAllianceUpdate(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(23);
        sp.EncodeBuffer(addAllianceInfo(alliance));
        return sp.get();
    }

    public static MaplePacket guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(5);
        sp.Encode4(gid);
        sp.EncodeStr(charName);
        sp.Encode4(levelFrom);
        sp.Encode4(jobFrom);
        return sp.get();
    }

    public static MaplePacket changeRank(MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(64);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.Encode1(mgc.getGuildRank());
        return sp.get();
    }

    public static MaplePacket changeAllianceRank(int allianceid, MapleGuildCharacter player) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(5);
        sp.Encode4(allianceid);
        sp.Encode4(player.getId());
        sp.Encode4(player.getAllianceRank());
        return sp.get();
    }

    public static MaplePacket sendFamilyJoinResponse(boolean accepted, String added) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyJoinRequestResult);

        sp.Encode1(accepted ? 1 : 0);
        sp.EncodeStr(added);
        return sp.get();
    }

    public static MaplePacket changeRep(int r) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyFamousPointIncResult);

        sp.Encode4(r);
        sp.Encode4(0);
        return sp.get();
    }

    public static MaplePacket sendFamilyInvite(int cid, int otherLevel, int otherJob, String inviter) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyJoinRequest);

        sp.Encode4(cid); //the inviter
        sp.Encode4(otherLevel);
        sp.Encode4(otherJob);
        sp.EncodeStr(inviter);
        return sp.get();
    }

    public static byte[] addFamilyCharInfo(MapleFamilyCharacter ldr) {

        ServerPacket data = new ServerPacket();
        data.Encode4(ldr.getId());
        data.Encode4(ldr.getSeniorId());
        data.Encode2(ldr.getJobId());
        data.Encode1(ldr.getLevel());
        data.Encode1(ldr.isOnline() ? 1 : 0);
        data.Encode4(ldr.getCurrentRep());
        data.Encode4(ldr.getTotalRep());
        data.Encode4(ldr.getTotalRep()); //recorded rep to senior
        data.Encode4(ldr.getTotalRep()); //then recorded rep to sensen
        data.Encode8(Math.max(ldr.getChannel(), 0)); //channel->time online
        data.EncodeStr(ldr.getName());
        return data.get().getBytes();
    }

    public static MaplePacket familyLoggedIn(boolean online, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyNotifyLoginOrLogout);

        sp.Encode1(online ? 1 : 0);
        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket familySummonRequest(String name, String mapname) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilySummonRequest);

        sp.EncodeStr(name);
        sp.EncodeStr(mapname);
        return sp.get();
    }

    public static MaplePacket cancelFamilyBuff() {
        return familyBuff(0, 0, 0, 0);
    }

    public static MaplePacket getFamilyData() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyPrivilegeList);

        List<MapleFamilyBuff.MapleFamilyBuffEntry> entries = MapleFamilyBuff.getBuffEntry();
        sp.Encode4(entries.size()); // Number of events
        for (MapleFamilyBuff.MapleFamilyBuffEntry entry : entries) {
            sp.Encode1(entry.type);
            sp.Encode4(entry.rep);
            sp.Encode4(entry.count);
            sp.EncodeStr(entry.name);
            sp.EncodeStr(entry.desc);
        }
        return sp.get();
    }

    public static MaplePacket familyBuff(int type, int buffnr, int amount, int time) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilySetPrivilege);

        sp.Encode1(type);
        if (type >= 2 && type <= 4) {
            sp.Encode4(buffnr);
            //first int = exp, second int = drop
            sp.Encode4(type == 3 ? 0 : amount);
            sp.Encode4(type == 2 ? 0 : amount);
            sp.Encode1(0);
            sp.Encode4(time);
        }
        return sp.get();
    }

    public static MaplePacket getFamilyPedigree(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyChartResult);

        sp.Encode4(chr.getId());
        MapleFamily family = OdinWorld.Family.getFamily(chr.getFamilyId());
        int descendants = 2;
        int gens = 0;
        int generations = 0;
        if (family == null) {
            sp.Encode4(2);
            sp.EncodeBuffer(addFamilyCharInfo(new MapleFamilyCharacter(chr, 0, 0, 0, 0))); //leader
        } else {
            sp.Encode4(family.getMFC(chr.getId()).getPedigree().size() + 1); //+ 1 for leader, but we don't want leader seeing all msgs
            sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(family.getLeaderId())));
            if (chr.getSeniorId() > 0) {
                MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                if (senior.getSeniorId() > 0) {
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(senior.getSeniorId())));
                }
                sp.EncodeBuffer(addFamilyCharInfo(senior));
            }
        }
        sp.EncodeBuffer(addFamilyCharInfo(chr.getMFC() == null ? new MapleFamilyCharacter(chr, 0, 0, 0, 0) : chr.getMFC()));
        if (family != null) {
            if (chr.getSeniorId() > 0) {
                MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                if (senior != null) {
                    if (senior.getJunior1() > 0 && senior.getJunior1() != chr.getId()) {
                        sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(senior.getJunior1())));
                    } else if (senior.getJunior2() > 0 && senior.getJunior2() != chr.getId()) {
                        sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(senior.getJunior2())));
                    }
                }
            }
            if (chr.getJunior1() > 0) {
                sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(chr.getJunior1())));
            }
            if (chr.getJunior2() > 0) {
                sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(chr.getJunior2())));
            }
            if (chr.getJunior1() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                if (junior.getJunior1() > 0) {
                    descendants++;
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(junior.getJunior1())));
                }
                if (junior.getJunior2() > 0) {
                    descendants++;
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(junior.getJunior2())));
                }
            }
            if (chr.getJunior2() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                if (junior.getJunior1() > 0) {
                    descendants++;
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(junior.getJunior1())));
                }
                if (junior.getJunior2() > 0) {
                    descendants++;
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(junior.getJunior2())));
                }
            }
            gens = family.getGens();
            generations = family.getMemberSize();
        }
        sp.Encode8(descendants);
        sp.Encode4(gens);
        sp.Encode4(-1);
        sp.Encode4(generations);
        if (family != null) {
            if (chr.getJunior1() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                if (junior.getJunior1() > 0) {
                    sp.Encode4(junior.getJunior1());
                    sp.Encode4(family.getMFC(junior.getJunior1()).getDescendants());
                }
                if (junior.getJunior2() > 0) {
                    sp.Encode4(junior.getJunior2());
                    sp.Encode4(family.getMFC(junior.getJunior2()).getDescendants());
                }
            }
            if (chr.getJunior2() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                if (junior.getJunior1() > 0) {
                    sp.Encode4(junior.getJunior1());
                    sp.Encode4(family.getMFC(junior.getJunior1()).getDescendants());
                }
                if (junior.getJunior2() > 0) {
                    sp.Encode4(junior.getJunior2());
                    sp.Encode4(family.getMFC(junior.getJunior2()).getDescendants());
                }
            }
        }
        List<OdinPair<Integer, Integer>> b = chr.usedBuffs();
        sp.Encode4(b.size());
        for (OdinPair<Integer, Integer> ii : b) {
            sp.Encode4(ii.getLeft()); //buffid
            sp.Encode4(ii.getRight()); //times used
        }
        sp.Encode2(2);
        return sp.get();
    }

    public static MaplePacket getFamilyInfo(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyInfoResult);

        sp.Encode4(chr.getCurrentRep()); //rep
        sp.Encode4(chr.getTotalRep()); // total rep
        sp.Encode4(chr.getTotalRep()); //rep recorded today
        sp.Encode2(chr.getNoJuniors());
        sp.Encode2(2);
        sp.Encode2(chr.getNoJuniors());
        MapleFamily family = OdinWorld.Family.getFamily(chr.getFamilyId());
        if (family != null) {
            sp.Encode4(family.getLeaderId()); //??? 9D 60 03 00
            sp.EncodeStr(family.getLeaderName());
            sp.EncodeStr(family.getNotice()); //message?
        } else {
            sp.Encode8(0);
        }
        List<OdinPair<Integer, Integer>> b = chr.usedBuffs();
        sp.Encode4(b.size());
        for (OdinPair<Integer, Integer> ii : b) {
            sp.Encode4(ii.getLeft()); //buffid
            sp.Encode4(ii.getRight()); //times used
        }
        return sp.get();
    }

    public static MaplePacket getSeniorMessage(String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyJoinAccepted);

        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket NotifyLevelUp(boolean family, int level, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_NotifyLevelUp);

        sp.Encode1(family ? 1 : 2);
        sp.Encode4(level);
        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket NotifyJobChange(boolean family, int jobid, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_NotifyJobChange);

        sp.Encode1(family ? 1 : 0);
        sp.Encode4(jobid); //or is this a short
        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket NotifyWedding(boolean family, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_NotifyWedding);

        sp.Encode1(family ? 1 : 0);
        sp.EncodeStr(name);
        return sp.get();
    }
}
