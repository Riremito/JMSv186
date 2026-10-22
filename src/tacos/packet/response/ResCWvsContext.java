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

import odin.client.MapleQuestStatus;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import tacos.client.TacosCharacter.MapleMount;
import tacos.config.Region;
import odin.constants.GameConstants;
import tacos.shared.TacosSharedDate;
import tacos.debug.DebugLogger;
import odin.handling.channel.MapleGuildRanking;
import odin.handling.world.MapleParty;
import odin.handling.world.MapleParty.MaplePartyCharacter;
import odin.handling.world.MapleParty.PartyOperation;
import odin.handling.world.family.MapleFamily;
import odin.handling.world.family.MapleFamilyBuff;
import odin.handling.world.family.MapleFamilyCharacter;
import odin.handling.world.guild.MapleBBSThread;
import odin.handling.world.guild.MapleGuild;
import odin.handling.world.guild.MapleGuildAlliance;
import odin.handling.world.guild.MapleGuildCharacter;
import tacos.server.TacosWorld;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsBodyPart;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.ops.OpsAlliance;
import tacos.packet.ops.OpsGuildBBS;
import tacos.packet.ops.OpsGuild;
import tacos.packet.ops.OpsFamily;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.packet.ops.OpsChangeStat;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.ops.OpsMapTransfer;
import tacos.packet.ops.OpsFriend;
import tacos.packet.response.builder.PB_Friend;
import tacos.packet.ops.OpsMessage;
import tacos.packet.response.builder.PB_Message;
import tacos.packet.ops.OpsShopScanner;
import tacos.packet.response.data.RD_CWvsContext;
import tacos.packet.response.data.RD_CharacterStat;
import tacos.packet.response.data.RD_GW_ItemSlotBase;
import tacos.packet.response.builder.PB_InvOp;
import odin.server.MapleItemInformationProvider;
import tacos.client.TacosBuff;
import tacos.client.TacosBuff.Buff;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.client.TacosCharacter;
import tacos.client.TacosMapleGift.MapleGiftData;
import tacos.client.TacosMonsterBook;
import tacos.config.Config;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsGivePopularity;
import tacos.packet.ops.OpsMarriage;
import tacos.packet.ops.OpsParty;
import tacos.packet.ops.OpsSecondaryStat;
import tacos.packet.response.data.RD_AvatarLook;
import tacos.packet.response.data.RD_CStage;

/**
 *
 * @author Riremito
 */
public class ResCWvsContext {

    // CWvsContext::OnInventoryOperation
    public static ServerPacket InventoryOperation(boolean unlock, PB_InvOp io) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_InventoryOperation);
        sp.Encode1(unlock ? 1 : 0);// m_bExclRequestSent, unlock
        sp.Encode1((io == null) ? 0 : io.get().size());
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 197) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89)); // unused

        boolean equip_changed = false;
        if (io != null) {
            for (PB_InvOp.InvData v : io.get()) {
                sp.Encode1(v.mode);
                switch (v.mode) {
                    // add
                    case 0: {
                        sp.Encode1(v.type.getType());
                        sp.Encode2(v.item.getPosition());
                        sp.EncodeBuffer(RD_GW_ItemSlotBase.Encode(v.item));
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
        return sp;
    }

    // CWvsContext::OnInventoryGrow
    public static ServerPacket InventoryGrow(byte invType, byte newSlots) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_InventoryGrow);

        sp.Encode1(invType);
        sp.Encode1(newSlots);
        return sp;
    }

    // CWvsContext::OnStatChanged
    public static ServerPacket StatChanged(TacosCharacter chr, boolean unlock, int statmask) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StatChanged);

        sp.Encode1(unlock ? 1 : 0); // CWvsContext->bExclRequestSent
        sp.Encode1(0, Config.Between(Region.TWMS, 74, 93) || Config.Between(Region.EMS, 55, 76));
        sp.EncodeBuffer(RD_CharacterStat.EncodeChangeStat(chr, statmask));
        if (Config.PreBB()) {
            if (Region.JMS.check() || Region.JMST.check() || Region.CMS.check()) {
                // Pet
                if ((statmask & OpsChangeStat.CS_PETSN.get()) != 0) {
                    int v5 = 0; // CVecCtrlUser::AddMovementInfo
                    sp.Encode1(v5);
                }
            }
            sp.Encode1(0, Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.EMS, 70)); // not 0 -> Encode1
        } else {
            // v188+
            sp.Encode1(0); // not 0 -> Encode1
            sp.Encode1(0); // not 0 -> Encode4, Encode4
        }

        return sp;
    }

    // TemporaryStatBase<long>::DecodeForClient
    public static void EncodeTwoState(ServerPacket sp, TacosCharacter chr, Buff buff) {
        switch (buff.ops.getTwoState()) {
            case RIDING: {
                sp.Encode4(chr.getMount() != null ? chr.getMount().getItemId() : 0); // m_value
                sp.Encode4(1); // m_reason
                sp.Encode1(0); // DecodeTime
                sp.Encode4(0); // DecodeTime
                break;
            }
            case NO_EXPIRE: {
                sp.Encode4(buff.buff_effect); // m_value
                sp.Encode4(buff.buff_effect_2); // m_reason
                sp.Encode1(0); // DecodeTime
                sp.Encode4(0); // DecodeTime
                break;
            }
            case EXPIRE_LAST: {
                sp.Encode4(buff.buff_effect); // m_value
                sp.Encode4(buff.buff_effect_2); // m_reason
                sp.Encode1(0); // DecodeTime
                sp.Encode4(0); // DecodeTime
                sp.Encode2(buff.buff_time / 1000); // m_usExpireTerm
                break;
            }
            case EXPIRE_CURRENT: {
                sp.Encode4(buff.buff_effect); // m_value
                sp.Encode4(buff.buff_effect_2); // m_reason
                sp.Encode1(0); // DecodeTime
                sp.Encode4(0); // DecodeTime
                sp.Encode1(0); // DecodeTime
                sp.Encode4(0); // DecodeTime -> m_tCurrentTime
                sp.Encode2(buff.buff_time / 1000); // m_usExpireTerm
                break;
            }
            case GUIDED_BULLET: {
                sp.Encode4(buff.buff_effect); // m_value
                sp.Encode4(buff.buff_effect_2); // m_reason
                sp.Encode1(0); // DecodeTime
                sp.Encode4(0); // DecodeTime
                sp.Encode4(0); // m_dwMobID
                break;
            }
            default: {
                sp.Encode4(buff.buff_effect); // m_value
                sp.Encode4(buff.buff_effect_2); // m_reason
                sp.Encode1(0); // DecodeTime
                sp.Encode4(0); // DecodeTime
                break;
            }
        }
    }

    // CWvsContext::OnTemporaryStatSet
    public static ServerPacket TemporaryStatSet(TacosCharacter chr, int buff_id) {
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
            if (Config.GreaterOrEqual(Region.THMS, 96)) {
                sp.Encode4(buff.buff_effect);
            } else {
                sp.Encode2(buff.buff_effect);
            }
            sp.Encode4(buff.buff_id);
            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 47) || Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.CMS, 62) || Config.GreaterOrEqual(Region.TWMS, 73) || Config.GreaterOrEqual(Region.THMS, 0) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 0) || Config.GreaterOrEqual(Region.BMS, 24) || Config.GreaterOrEqual(Region.VMS, 35) || Region.HKMS.check()) {
                sp.Encode4(buff.buff_time);
            } else {
                sp.Encode2(buff.buff_time);
            }
        }
        sp.Encode2(0, Config.GreaterOrEqual(Region.KMS, 197));
        if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 47) || Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.CMS, 62) || Config.GreaterOrEqual(Region.TWMS, 73) || Config.GreaterOrEqual(Region.THMS, 0) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 0) || Config.GreaterOrEqual(Region.BMS, 24) || Config.GreaterOrEqual(Region.VMS, 35)) {
            sp.Encode1(0); // nDefenseAtt
            sp.Encode1(0); // nDefenseState
        }
        // JMS187, GMS95
        if (Config.PostBB()) {
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
            EncodeTwoState(sp, chr, buff);
        }
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 197) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.TWMS, 148));
        if (Config.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode4(0);
            sp.Encode4(0);
        }
        // DecodeForLocal - end.
        sp.Encode2(0); // delay
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 197) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89));
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 197));
        sp.Encode1(0); // CUserLocal::SetSecondaryStatChangedPoint
        sp.Encode4(0, Config.GreaterOrEqual(Region.KMS, 197));
        return sp;
    }

    // CWvsContext::OnTemporaryStatReset
    public static ServerPacket TemporaryStatReset(TacosCharacter chr, int buff_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TemporaryStatReset);

        int[] buff_mask = TacosBuff.getBuffBuffer();
        for (Buff buff : chr.getBuff().getCTS(buff_id)) {
            buff_mask[buff.ops.getNl()] |= buff.ops.getNr();
        }
        for (int index = 0; index < buff_mask.length; index++) {
            sp.Encode4(buff_mask[buff_mask.length - 1 - index]);
        }

        sp.Encode1(0);
        return sp;
    }

    // CWvsContext::OnForcedStatSet
    public static ServerPacket ForcedStatSet(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ForcedStatSet);

        sp.EncodeBuffer(RD_CWvsContext.ForcedStat_Encode(chr));
        return sp;
    }

    // CWvsContext::OnForcedStatReset
    public static ServerPacket ForcedStatReset() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ForcedStatReset);

        return sp;
    }

    // CWvsContext::OnChangeSkillRecordResult
    public static ServerPacket ChangeSkillRecordResult(int skillid, int level, int masterlevel, long expiration) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ChangeSkillRecordResult);
        sp.Encode1(1);
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 197) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89));
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 197));
        sp.Encode2(1);
        sp.Encode4(skillid);
        sp.Encode4(level);
        sp.Encode4(masterlevel);
        sp.Encode8(TacosSharedDate.getMagicalExpirationDate(), Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        sp.Encode1(4);
        return sp;
    }

    // CWvsContext::OnSkillUseResult
    public static ServerPacket SkillUseResult() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SkillUseResult);

        sp.Encode1(0); // unused.
        return sp;
    }

    // CWvsContext::OnGivePopularityResult
    public static ServerPacket GivePopularityResult(OpsGivePopularity ops, TacosCharacter chr, boolean is_up, TacosCharacter target) {
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

        return sp;
    }

    // CWvsContext::OnMessage
    public static ServerPacket Message(OpsMessage mt, PB_Message pb) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Message);
        sp.Encode1(mt.get());
        switch (mt) {
            case MS_DropPickUpMessage: {
                sp.Encode1(pb.dt.get());
                switch (pb.dt) {
                    case PICKUP_ITEM: {
                        sp.Encode4(pb.ItemID);
                        sp.Encode4(pb.Inc_ItemCount);
                        break;
                    }
                    case PICKUP_MESO: {
                        sp.Encode1(0, Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
                        sp.Encode4(pb.Inc_Meso);
                        if (Config.LessOrEqual(Region.JMS, 131) || Config.LessOrEqual(Region.CMS, 73)) {
                            sp.Encode2(0); // Internet cafe bonus
                        } else {
                            sp.Encode4(0);
                        }
                        break;
                    }
                    case PICKUP_MONSTER_CARD: {
                        sp.Encode4(pb.ItemID);
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
                        DebugLogger.ErrorLog("Unknown DropPickUp Type" + pb.dt.get());
                        break;
                    }
                }
                break;
            }
            // updateQuest, updateQuestMobKills
            case MS_QuestRecordMessage: {
                sp.Encode2(pb.QuestID);
                sp.Encode1(pb.qt.get());
                switch (pb.qt) {
                    case QUEST_START: {
                        sp.Encode1(0); // 0 or not
                        break;
                    }
                    case QUEST_UPDATE: {
                        sp.EncodeStr(pb.str);
                        break;
                    }
                    case QUEST_COMPLETE: {
                        sp.Encode8(TacosSharedDate.getTimestamp());
                        break;
                    }
                    default: {
                        DebugLogger.ErrorLog("Unknown QuestRecord Type" + pb.dt.get());
                        break;
                    }
                }
                break;
            }
            // itemExpired
            case MS_CashItemExpireMessage: {
                sp.Encode4(pb.ItemID);
                break;
            }
            case MS_IncEXPMessage: {
                sp.Encode1(pb.Inc_EXP_TextColor);
                sp.Encode4(pb.Inc_EXP);
                sp.Encode1(pb.InChat); // bOnQuest
                sp.Encode4(0);
                sp.Encode1(pb.Inc_EXP_MobEventBonusPercentage); // nMobEventBonusPercentage
                sp.Encode1(0);
                if (Config.Equal(Region.THMS, 87)) {
                    sp.Encode4(pb.Inc_EXP_WeddingBonus); // Wedding Bonus EXP(+%d)
                    sp.Encode4(0); // Party Ring Bonus EXP(+%d)
                    sp.Encode4(0); // EXP Bonus Internet Cafe(+ %d)
                    sp.Encode4(0); // Rainbow Week Bonus EXP(+%d)
                } else if (Config.GreaterOrEqual(Region.GMS, 111)) {
                    sp.Encode4(0);
                } else {
                    sp.Encode4(pb.Inc_EXP_WeddingBonus); // 結婚ボーナス経験値
                    sp.Encode4(0); // グループリングボーナスEXP (?)
                }
                if (0 < pb.Inc_EXP_MobEventBonusPercentage) {
                    sp.Encode1(pb.Inc_EXP_PlayTimeHour);
                }
                if (pb.InChat != 0) {
                    sp.Encode1(0);
                }
                sp.Encode1(0); // nPartyBonusEventRate
                sp.Encode4(pb.Inc_EXP_PartyBonus); // グループボーナス経験値
                sp.Encode4(pb.Inc_EXP_EquipmentBonus); // アイテム装着ボーナス経験値
                sp.Encode4(0); // not used
                sp.Encode4(pb.Inc_EXP_RainbowWeekBonus); // レインボーウィークボーナス経験値

                if (Config.Equal(Region.GMS, 95)) {
                    sp.Encode4(0);
                    sp.Encode4(0);
                    break;
                }

                if (Config.GreaterOrEqual(Region.GMS, 111)) {
                    sp.Encode1(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    if (pb.InChat != 0) {
                        sp.Encode4(0);
                    }
                    break;
                }

                if (Config.GreaterOrEqual(Region.JMS, 302)) {
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                }
                sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76)); // 0 or not
                break;
            }
            // getSPMsg
            case MS_IncSPMessage: {
                sp.Encode2(pb.JobID);
                sp.Encode1(pb.Inc_SP);
                break;
            }
            // getShowFameGain
            case MS_IncPOPMessage: {
                sp.Encode4(pb.Inc_Fame);
                break;
            }
            // showMesoGain
            case MS_IncMoneyMessage: {
                sp.Encode4(pb.Inc_Meso);
                sp.Encode4(-1, Config.GreaterOrEqual(Region.JMS, 302)); // 別の数値だとメッセージ非表示
                break;
            }
            // getGPMsg
            case MS_IncGPMessage: {
                sp.Encode4(pb.Inc_GP);
                break;
            }
            // getStatusMsg
            case MS_GiveBuffMessage: {
                sp.Encode4(pb.ItemID);
                break;
            }
            case MS_GeneralItemExpireMessage: {
                break;
            }
            // showQuestMsg
            case MS_SystemMessage: {
                sp.EncodeStr(pb.str);
                break;
            }
            // updateInfoQuest
            case MS_QuestRecordExMessage: {
                sp.Encode2(pb.QuestID);
                sp.EncodeStr(pb.str);
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
                sp.Encode4(pb.Inc_Tama);
                break;
            }
            default: {
                break;
            }
        }
        return sp;
    }

    // CWvsContext::OnMemoResult
    public static ServerPacket MemoResult(ResultSet notes, int count) throws SQLException {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MemoResult);

        sp.Encode1(3);
        sp.Encode1(count);
        for (int i = 0; i < count; i++) {
            sp.Encode4(notes.getInt("id"));
            sp.EncodeStr(notes.getString("from"));
            sp.EncodeStr(notes.getString("message"));
            sp.Encode8(TacosSharedDate.getTimestamp(notes.getLong("timestamp")));
            sp.Encode1(notes.getInt("gift"));
            notes.next();
        }
        return sp;
    }

    // CWvsContext::OnMapTransferResult
    public static ServerPacket MapTransferResult(TacosCharacter chr, OpsMapTransfer ops_res, boolean vip) {
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

        return sp;
    }

    // CWvsContext::OnAntiMacroResult
    // CWvsContext::OnClaimResult
    // CWvsContext::OnSetClaimSvrAvailableTime
    // CWvsContext::OnClaimSvrStatusChanged
    //CWvsContext::OnSetTamingMobInfo
    public static ServerPacket SetTamingMobInfo(TacosCharacter chr, boolean levelup) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetTamingMobInfo);

        sp.Encode4(chr.getId());
        sp.Encode4(chr.getMount().getLevel());
        sp.Encode4(chr.getMount().getExp());
        sp.Encode4(chr.getMount().getFatigue());
        sp.Encode1(levelup ? 1 : 0);
        return sp;
    }

    // CWvsContext::OnQuestClear
    public static ServerPacket QuestClear(int usQuestID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_QuestClear);

        sp.Encode2(usQuestID);
        return sp;
    }

    // CWvsContext::OnEntrustedShopCheckResult
    public static ServerPacket EntrustedShopCheckResult(OpsEntrustedShop ops) {
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

        return sp;
    }

    // CWvsContext::OnSkillLearnItemResult
    public static ServerPacket SkillLearnItemResult(TacosCharacter chr, boolean bIsMaterbook, boolean bUsed, boolean bSucceed) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SkillLearnItemResult);

        sp.Encode1(1, Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186)); // bOnExclRequest
        sp.Encode4(chr.getId());
        sp.Encode1(bIsMaterbook ? 1 : 0); // bIsMaterbook
        sp.Encode4(0); // not used
        sp.Encode4(0); // not used
        sp.Encode1(bUsed ? 1 : 0); // bUsed[0]
        sp.Encode1(bSucceed ? 1 : 0); // bSucceed
        return sp;
    }

    // CWvsContext::OnSkillResetItemResult
    // CWvsContext::OnGatherItemResult
    public static ServerPacket GatherItemResult(byte nTI) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GatherItemResult);

        sp.Encode1(0); // unused
        sp.Encode1(nTI);
        return sp;
    }

    // CWvsContext::OnSortItemResult
    public static ServerPacket SortItemResult(byte nTI) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SortItemResult);

        sp.Encode1(0); // unused
        sp.Encode1(nTI);
        return sp;
    }

    // CWvsContext::OnCharacterInfo
    public static ServerPacket CharacterInfo(TacosCharacter player, boolean isSelf) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CharacterInfo);

        sp.Encode4(player.getId()); // dwCharacterId
        sp.Encode1(player.getLevel()); // nLevel
        sp.Encode2(player.getJob()); // nJob

        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
            sp.Encode4(player.getFame()); // nPOP
        } else {
            sp.Encode2(player.getFame()); // nPOP
        }

        sp.Encode1(player.getMarriageId() > 0 ? 1 : 0, Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 48) || Config.GreaterOrEqual(Region.JMS, 147) || Config.GreaterOrEqual(Region.CMS, 63) || Config.GreaterOrEqual(Region.TWMS, 74) || Config.GreaterOrEqual(Region.THMS, 0) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 0)); // bIsMarried

        String sCommunity = "-";
        String sAlliance = "";
        // Guild
        if (player.getGuildId() <= 0) {
            MapleGuild guild = player.getWorld().getGuild().getGuild(player.getGuildId());
            if (guild != null) {
                sCommunity = guild.getName();
                // Alliance
                if (guild.getAllianceId() > 0) {
                    MapleGuildAlliance alliance = player.getWorld().getAlliance().getAlliance(guild.getAllianceId());
                    if (alliance != null) {
                        sAlliance = alliance.getName();
                    }
                }
            }
        }

        sp.Encode1(0, Config.GreaterOrEqual(Region.JMS, 302));
        sp.EncodeStr(sCommunity);
        sp.EncodeStr(sAlliance, Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 48) || Config.GreaterOrEqual(Region.JMS, 147) || Config.GreaterOrEqual(Region.CMS, 63) || Config.GreaterOrEqual(Region.TWMS, 74) || Config.GreaterOrEqual(Region.THMS, 0) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 0));

        // Pre-BB
        if (Config.Between(Region.JMS, 180, 186)) {
            sp.Encode4(0);
            sp.Encode4(0);
        }

        sp.Encode1(0, Config.PostBB());
        sp.Encode1(0, Config.GreaterOrEqual(Region.JMS, 302));
        sp.Encode1((player.getPetByIndex(0) != null) ? 1 : 0); // bPetActivated
        if (Config.LessOrEqual(Region.JMS, 131)) {
            // inlined?
            if (player.getPetByIndex(0) != null) {
                sp.EncodeBuffer(RD_CWvsContext.CUIUserInfo_SetPetInfo_JMS131(player, player.getPetByIndex(0)));
            }
        } else if (Config.GreaterOrEqual(Region.GMS, 95)) {
            if (player.getPetByIndex(0) != null) {
                sp.EncodeBuffer(RD_CWvsContext.CUIUserInfo_SetMultiPetInfo_GMS95(player));
            }
        } else {
            // CUIUserInfo::SetPetInfo, CUIUserInfo::SetMultiPetInfo
            sp.EncodeBuffer(RD_CWvsContext.CUIUserInfo_SetPetInfo(player));
        }

        // CUIUserInfo::SetTamingMobInfo
        Item inv_mount = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
        boolean TamingMobEnabled = false;
        MapleMount tm = player.getMount();
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
        int wishlistSize = player.getWishlistSize();
        sp.Encode1(wishlistSize);
        if (wishlistSize > 0) {
            // CInPacket::DecodeBuffer(v4, iPacket, 4 * wishlistSize);
            int[] wishlist = player.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                sp.Encode4(wishlist[x]);
            }
        }

        if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 48) || Config.GreaterOrEqual(Region.JMS, 147) || Config.GreaterOrEqual(Region.CMS, 63) || Config.GreaterOrEqual(Region.TWMS, 74) || Config.GreaterOrEqual(Region.THMS, 0) || Config.GreaterOrEqual(Region.GMS, 62) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 0)) {
            if (Config.GreaterOrEqual(Region.GMS, 93)) {
                // none.
            } else {
                // MonsterBookInfo::Decode
                sp.EncodeBuffer(MonsterBookInfo_Encode(player.getMonsterBook()));
            }
        }

        if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
            // MedalAchievementInfo::Decode
            Item inv_medal = player.getInventory(MapleInventoryType.EQUIPPED).getItem(OpsBodyPart.BP_MEDAL.getSlot());
            sp.Encode4(inv_medal == null ? 0 : inv_medal.getItemId()); // nEquipedMedalID
            List<Integer> medalQuests = new ArrayList<>();
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
                sp.Encode8(0, Config.GreaterOrEqual(Region.JMS, 302));
            }
            // JMS v180-v186, v187以降消滅
            if (Config.Between(Region.JMS, 180, 186) || Config.GreaterOrEqual(Region.GMS, 91)) {
                // Chair List
                sp.Encode4(player.getInventory(MapleInventoryType.SETUP).list().size());
                // CInPacket::DecodeBuffer(v4, iPacket, 4 * chairs);
                for (Item chair : player.getInventory(MapleInventoryType.SETUP).list()) {
                    sp.Encode4(chair.getItemId());
                }
            }

            if (Config.GreaterOrEqual(Region.JMS, 302)) {
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
        return sp;
    }

    // MonsterBookInfo::Decode
    public static byte[] MonsterBookInfo_Encode(TacosMonsterBook mb) {
        ServerPacket data = new ServerPacket();

        data.Encode4(mb.getLevel()); // nLevel
        data.Encode4(mb.getNormal()); // nNormal
        data.Encode4(mb.getSpecial()); // nSpecial
        data.Encode4(mb.getTotal()); // nTotal
        data.Encode4(mb.getCoverMobID()); // nCoverMobID
        return data.getBytes();
    }

    // CWvsContext::OnPartyResult
    public static ServerPacket PartyResult(OpsParty ops) {
        return PartyResult(ops, null);
    }

    // CWvsContext::OnPartyResult
    public static ServerPacket PartyResult(OpsParty ops, TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PartyResult);

        MapleParty party = chr.getParty();
        int forChannel = chr.getChannelId();
        sp.Encode1(ops.get());

        switch (ops) {
            case PartyReq_InviteParty: {
                sp.Encode4(party.getId());
                sp.EncodeStr(chr.getName());
                sp.Encode4(chr.getLevel());
                sp.Encode4(chr.getJob(), Config.GreaterOrEqual(Region.JMS, 186));
                sp.Encode1(0); // auto join.
                break;
            }
            case PartyRes_LoadParty_Done: {
                sp.Encode4(party.getId());
                sp.EncodeBuffer(addPartyStatus(forChannel, party));
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
                /*
                List<MapleDoor> doors = chr.getDoors();
                MapleDoor door = doors.isEmpty() ? null : doors.get(0);
                TacosPortal door_portal = (door != null) ? door.getTownPortal() : null;

                sp.Encode1(door_portal != null ? door_portal.getMysticDoorId() : 0); // number
                sp.Encode4((door != null) ? door.getMapId() : 0);
                sp.Encode4((door != null) ? door.getLink().getMapId() : 0);
                sp.Encode4((door != null) ? door.getSkillId() : 0);
                sp.Encode2((door != null) ? door.getLink().getPosition().x : 0);
                sp.Encode2((door != null) ? door.getLink().getPosition().y : 0);
                 */
                break;
            }
            case PartyInfo_OpenGate: {
                break;
            }
            default: {
                break;
            }
        }

        return sp;
    }

    // PARTYDATA::Decode
    // PARTYDATA::Decode (JMS147 : 0x12A, JMS186 : 0x142, JMS187, GMS95 : 0x17A bytes)
    private static byte[] addPartyStatus(int forchannel, MapleParty party) {
        ServerPacket data = new ServerPacket();

        List<MaplePartyCharacter> partymembers = new ArrayList<>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        // PARTYMEMBER
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
        data.Encode4(party.getLeader().getId()); // dwPartyBossCharacterID
        // adwFieldID, JMS186
        if (!Config.LessOrEqual(Region.JMS, 164)) {
            for (MaplePartyCharacter partychar : partymembers) {
                data.Encode4(partychar.getChannel() == forchannel ? partychar.getMapid() : 0);
            }
        }
        // aTownPortal : mystic doors are not coded.
        for (int i = 0; i < partymembers.size(); i++) {
            data.Encode4(999999999); // m_dwTownID
            data.Encode4(999999999); // m_dwFieldID
            data.Encode4(0); // m_nSKillID
            data.Encode4(-1); // m_ptFieldPortal.x
            data.Encode4(-1); // m_ptFieldPortal.y
        }
        // JMS187, GMS95
        if (Config.PostBB()) {
            for (int i = 0; i < partymembers.size(); i++) {
                data.Encode4(0); // aPQReward
            }
            for (int i = 0; i < partymembers.size(); i++) {
                data.Encode4(0); // aPQRewardType
            }
            data.Encode4(0); // dwPQRewardMobTemplateID
            data.Encode4(0); // bPQReward
        }
        return data.getBytes();
    }

    // CWvsContext::OnPartyResult
    public static ServerPacket PartyResult(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PartyResult);

        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                sp.Encode1(OpsParty.PartyRes_WithdrawParty_Done.get());
                sp.Encode4(party.getId());
                sp.Encode4(target.getId());
                sp.Encode1(op == PartyOperation.DISBAND ? 0 : 1);
                if (op != PartyOperation.DISBAND) {
                    sp.Encode1(op == PartyOperation.EXPEL ? 1 : 0);
                    sp.EncodeStr(target.getName());
                    sp.EncodeBuffer(addPartyStatus(forChannel, party));
                }
                break;
            case JOIN:
                sp.Encode1(OpsParty.PartyRes_JoinParty_Done.get());
                sp.Encode4(party.getId());
                sp.EncodeStr(target.getName());
                sp.EncodeBuffer(addPartyStatus(forChannel, party));
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                sp.Encode1(OpsParty.PartyRes_LoadParty_Done.get());
                sp.Encode4(party.getId());
                sp.EncodeBuffer(addPartyStatus(forChannel, party));
                break;
            case CHANGE_LEADER:
            case CHANGE_LEADER_DC:
                sp.Encode1(OpsParty.PartyRes_ChangePartyBoss_Done.get());
                sp.Encode4(target.getId());
                sp.Encode1(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                break;
            //1D = expel function not available in this map.
        }
        return sp;
    }

    // CWvsContext::OnFriendResult
    public static ServerPacket FriendResult(OpsFriend flag, PB_Friend pb) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FriendResult);
        sp.Encode1(flag.get());
        switch (flag) {
            case FriendRes_LoadFriend_Done:
            case FriendRes_SetFriend_Done:
            case FriendRes_DeleteFriend_Done: {
                sp.EncodeBuffer(RD_CWvsContext.CFriend_Reset(pb.chr));
                break;
            }
            case FriendRes_NotifyChange_FriendInfo: {
                break;
            }
            case FriendRes_Invite: {
                sp.Encode4(pb.friend_id); // dwFriendID
                sp.EncodeStr(pb.friend_name);
                sp.Encode4(pb.friend_level); // nLevel
                sp.Encode4(pb.friend_job); // nJobCode
                // CWvsContext::CFriend::Insert, 39 bytes
                sp.Encode4(pb.friend_id);
                sp.EncodeBuffer(pb.friend_name, 13);
                sp.Encode1(0);
                sp.Encode4(pb.friend_channel == -1 ? -1 : pb.friend_channel - 1); // please add channel
                sp.EncodeBuffer(pb.friend_tag, 17);
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
                sp.Encode4(pb.friend_id);
                sp.Encode1(0);
                sp.Encode4(pb.friend_channel);
                break;
            }
            case FriendRes_IncMaxCount_Done: {
                sp.Encode1(pb.nFriendMax);
                break;
            }
            case FriendRes_IncMaxCount_Unknown: {
                break;
            }
            case FriendRes_PleaseWait: {
                break;
            }
            default: {
                DebugLogger.ErrorLog("FriendResult not coded : " + flag);
                break;
            }
        }

        return sp;
    }

    // CWvsContext::OnExpedtionResult
    // CWvsContext::OnFriendResult
    // CWvsContext::OnGuildResult
    // CWvsContext::OnAllianceResult
    // CWvsContext::OnTownPortal
    // CWvsContext::OnOpenGate
    // CWvsContext::OnBroadcastMsg
    public static ServerPacket BroadcastMsg(OpsBroadcastMsg bm, PB_BroadcastMsg pb) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BroadcastMsg);
        sp.Encode1(bm.get());

        switch (bm) {
            case BM_NOTICE: // 青文字 [告知事項]
            case BM_ALERT: // ダイアログ
            case BM_EVENT: // ピンク文字
            {
                sp.EncodeStr(pb.message);
                break;
            }
            case BM_SLIDE: // 画面上部の横スクロールメッセージ
            {
                boolean show_msg = pb.message.length() != 0;
                sp.Encode1(show_msg ? 1 : 0);
                if (show_msg) {
                    sp.EncodeStr(pb.message);
                }
                break;
            }
            case BM_NOTICEWITHOUTPREFIX: // 青文字, アイテム情報
            {
                sp.EncodeStr(pb.message);
                sp.Encode4(pb.item_id);
                break;
            }
            case BM_SPEAKERCHANNEL: // 5070000, メガホン
            {
                String text = pb.chr.getPlayerNameWithMedal() + " : " + pb.message;
                sp.EncodeStr(text);
                break;
            }
            case BM_SPEAKERWORLD: // 5071000, 拡声器
            case BM_HEARTSPEAKER: // 5073000, ハート拡声器
            case BM_SKULLSPEAKER: // 5074000, ドクロ拡声器
            {
                String text = pb.chr.getPlayerNameWithMedal() + " : " + pb.message;
                int channel = pb.chr.getClient().getChannelId() - 1;
                sp.EncodeStr(text);
                sp.Encode1(channel);
                sp.Encode1(pb.ear);
                break;
            }
            case BM_ITEMSPEAKER: // 5076000, アイテム拡声器
            {
                String text = pb.chr.getPlayerNameWithMedal() + " : " + pb.message;
                int channel = pb.chr.getClient().getChannelId() - 1;
                boolean show_item = pb.item != null;
                sp.EncodeStr(text);
                sp.Encode1(channel);
                sp.Encode1(pb.ear);
                sp.Encode1(show_item ? 1 : 0);
                if (show_item) {
                    sp.EncodeBuffer(RD_GW_ItemSlotBase.Encode(pb.item));
                }
                break;
            }
            case BM_ARTSPEAKERWORLD: // 5077000, 三連拡声器
            {
                String name = pb.chr.getPlayerNameWithMedal();
                int channel = pb.chr.getClient().getChannelId() - 1;
                String text1 = pb.messages.get(0); // ?_?

                sp.EncodeStr(name + " : " + text1);
                sp.Encode1(pb.messages.size());
                for (int i = 1; i < pb.messages.size(); i++) {
                    sp.EncodeStr(name + " : " + pb.messages.get(i));
                }
                sp.Encode1(channel);
                sp.Encode1(pb.ear);
                break;
            }
            case BM_GACHAPONANNOUNCE: {
                String text = pb.chr.getName() + " : " + pb.message;
                sp.EncodeStr(text);
                sp.Encode4(pb.gashapon_type); // 緑 (0) or 茶色 (-1)
                sp.EncodeBuffer(RD_GW_ItemSlotBase.Encode(pb.item));
                break;
            }
            default: {
                break;
            }
        }

        return sp;
    }

    // CWvsContext::OnIncubatorResult
    public static ServerPacket IncubatorResult(int itemId, short quantity, int itemId2, short quantity2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_IncubatorResult);

        sp.Encode4(itemId);
        sp.Encode2(quantity);
        sp.Encode4(5060003);
        sp.Encode4(itemId2);
        sp.Encode4(quantity2);
        return sp;
    }

    // CWvsContext::OnShopScannerResult
    public static ServerPacket ShopScannerResult(OpsShopScanner ops) {
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
        return sp;
    }

    // CWvsContext::OnShopLinkResult
    // CWvsContext::OnMarriageRequest
    public static ServerPacket MarriageRequest(String name, int cid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MarriageRequest);

        sp.Encode1(0); //mode, 0 = engage, 1 = cancel, 2 = answer.. etc
        sp.EncodeStr(name); // name
        sp.Encode4(cid); // playerid
        return sp;
    }

    // CWvsContext::OnMarriageResult
    public static ServerPacket MarriageResult(OpsMarriage ops, int item_id, TacosCharacter male, TacosCharacter female) {
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

        return sp;
    }

    // CWvsContext::OnWeddingGiftResult
    // CWvsContext::OnNotifyMarriedPartnerMapTransfer
    // CWvsContext::OnCashPetFoodResult
    // CWvsContext::OnSetWeekEventMessage
    public static ServerPacket SetWeekEventMessage(String text) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetWeekEventMessage);

        sp.Encode1(-1);
        sp.EncodeStr(text);
        return sp;
    }

    // パチンコ情報の更新
    public static ServerPacket PachinkoResult(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_PachinkoResult);
        // 12 bytes
        {
            sp.Encode4(chr.getId()); // キャラクターID (実質不要)
            sp.Encode4(chr.getTama()); // アイテム欄の玉の数に反映される値
            sp.Encode4(0); // 用途不明
        }
        return sp;
    }

    // unimplemented, cancel action is not coded.
    public static ServerPacket MapleGift(MapleGiftData maple_gift_data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_MapleGift);

        int type = maple_gift_data == null ? 3 : 1;
        sp.Encode1(type);
        switch (type) {
            case 1: {
                sp.EncodeBuffer(RD_CStage.MapleGift_Encode(maple_gift_data));
                break;
            }
            case 3: {
                // no data, show error message for who sent gift.
                break;
            }
            default: {
                // do nothing.
                break;
            }
        }

        return sp;
    }

    // LP_JMS_Fishing_BoardUpdate
    public static ServerPacket Fishing_BoardUpdate(byte type, int id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_Fishing_BoardUpdate);

        sp.Encode1(type);
        sp.Encode4(id);
        return sp;
    }

    // CWvsContext::OnSetPotionDiscountRate
    // CWvsContext::OnBridleMobCatchFail
    // CWvsContext::OnImitatedNPCResult
    // CWvsContext::OnImitatedNPCData
    // CWvsContext::OnLimitedNPCDisableInfo
    // CWvsContext::OnMonsterBookSetCard
    public static ServerPacket MonsterBookSetCard(boolean is_added, int nCardID, int nCardCount) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MonsterBookSetCard);

        sp.Encode1(is_added ? 1 : 0);
        if (is_added) {
            sp.Encode4(nCardID); // nCardID
            sp.Encode4(nCardCount); // nCardCount
        }

        return sp;
    }

    // CWvsContext::OnMonsterBookSetCover
    public static ServerPacket MonsterBookSetCover(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MonsterBookSetCover);

        sp.Encode4(chr.getMonsterBook().getCover());
        return sp;
    }

    // CWvsContext::OnHourChanged
    // CWvsContext::OnMiniMapOnOff
    public static ServerPacket MiniMapOnOff() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MiniMapOnOff);

        sp.Encode1(0); // m_bMiniMapOnOff
        return sp;
    }

    // CWvsContext::OnConsultAuthkeyUpdate
    // CWvsContext::OnClassCompetitionAuthkeyUpdate
    // CWvsContext::OnWebBoardAuthkeyUpdate
    // CWvsContext::OnSessionValue
    // CWvsContext::OnPartyValue
    // CWvsContext::OnFieldSetVariable
    public static ServerPacket sendString(int type, String object, String amount) {
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
        return sp;
    }

    // CWvsContext::OnBonusExpRateChanged
    public static ServerPacket BonusExpRateChanged(OpsBodyPart body_part, int nHour, int rate) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BonusExpRateChanged);

        sp.Encode4(body_part.get()); // BP_PENDANT (17)
        sp.Encode4(nHour);
        sp.Encode4(rate);
        return sp;
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
    public static ServerPacket AvatarMegaphoneUpdateMessage(TacosCharacter chr, int channel, int itemId, String message, boolean ear) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AvatarMegaphoneUpdateMessage);

        sp.Encode4(itemId);
        sp.EncodeStr(chr.getName());
        sp.EncodeStr(message);
        sp.Encode4(channel - 1); // channel
        sp.Encode1(ear ? 1 : 0);
        sp.EncodeBuffer(RD_AvatarLook.Encode(chr));
        return sp;
    }

    // CWvsContext::OnSuccessInUsegachaponBox
    public static ServerPacket SuccessInUseGachaponBox(int box_item_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SuccessInUseGachaponBox);
        sp.Encode4(box_item_id);
        return sp;
    }

    // CWvsContext::OnNewYearCardRes
    // CWvsContext::OnRandomMorphRes
    // CWvsContext::OnCancelNameChangebyOther
    // CWvsContext::OnSetBuyEquipExt
    // CWvsContext::OnSetPassenserRequest
    public static ServerPacket SetPassenserRequest(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetPassenserRequest);

        sp.Encode4(chr.getId()); // nPassenserID
        return sp;
    }

    // CWvsContext::OnScriptProgressMessage
    public static ServerPacket ScriptProgressMessage(String msg) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ScriptProgressMessage);

        sp.EncodeStr(msg);
        return sp;
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
    public static ServerPacket CharacterCash(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CMS_CharacterCash);

        sp.Encode4(chr.getId());
        sp.Encode4(chr.getMaplePoint());
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_SetNotice_Done(int gid, String notice) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_SetNotice_Done.get());
        sp.Encode4(gid);
        sp.EncodeStr(notice);
        return sp;
    }

    //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_WithdrawGuild_Done(MapleGuildCharacter mgc, boolean bExpelled) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1((bExpelled ? OpsGuild.GuildRes_KickGuild_Done : OpsGuild.GuildRes_WithdrawGuild_Done).get());
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.EncodeStr(mgc.getName());
        return sp;
    }

    // GUILDDATA::Decode
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
        data.Encode4(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0); // nAllianceID
        // JMS187, GMS95
        data.Encode1(1, Config.PostBB()); // nLevel
        // JMS188, GMS95
        data.Encode2(0, Config.GreaterOrEqual(Region.JMS, 188) || Config.GreaterOrEqual(Region.GMS, 95)); // guild skill count

        return data.getBytes();
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_ChangeLevelOrJob(MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_ChangeLevelOrJob.get());
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.Encode4(mgc.getLevel());
        sp.Encode4(mgc.getJobId());
        return sp;
    }

    // AllianceRes_NotifyLoginOrLogout
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_NotifyLoginOrLogout(int alliance, int gid, int id, boolean online) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_NotifyLoginOrLogout.get());
        sp.Encode4(alliance);
        sp.Encode4(gid);
        sp.Encode4(id);
        sp.Encode1(online ? 1 : 0);
        return sp;
    }

    // AllianceRes_Destroy_Done
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_Destroy_Done(int alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_Destroy_Done.get());
        sp.Encode4(alliance);
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_JoinGuild_Done(MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_JoinGuild_Done.get());
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.EncodeBuffer(mgc.getName(), 13);
        sp.Encode4(mgc.getJobId());
        sp.Encode4(mgc.getLevel());
        sp.Encode4(mgc.getGuildRank()); //should be always 5 but whatevs
        sp.Encode4(mgc.isOnline() ? 1 : 0); //should always be 1 too
        sp.Encode4(1); //? could be guild signature, but doesn't seem to matter
        sp.Encode4(mgc.getAllianceRank()); //should always 3
        return sp;
    }

    // AllianceRes_LoadGuildDone
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_LoadGuildDone(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_LoadGuildDone.get());
        List<MapleGuild> guilds = getAllianceGuilds(alliance);
        sp.Encode4(guilds.size());
        for (MapleGuild guild : guilds) {
            sp.EncodeBuffer(getGuildInfo(guild)); // GUILDDATA::Decode
        }
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_NotifyLoginOrLogout(int gid, int cid, boolean bOnline) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_NotifyLoginOrLogout.get());
        sp.Encode4(gid);
        sp.Encode4(cid);
        sp.Encode1(bOnline ? 1 : 0);
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_ShowGuildRanking(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_ShowGuildRanking.get());
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
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult(OpsGuild ops, String charname) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(ops.get());
        sp.EncodeStr(charname);
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_LoadGuild_Done(TacosCharacter player) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_LoadGuild_Done.get());
        if (player == null || player.getMGC() == null) {
            //show empty guild (used for leaving, expelled)
            sp.Encode1(0);
            return sp;
        }
        MapleGuild g = player.getWorld().getGuild().getGuild(player.getGuildId());
        if (g == null) {
            //failed to read from DB - don't show a guild
            sp.Encode1(0);
            return sp;
        }
        sp.Encode1(1); //bInGuild
        sp.EncodeBuffer(getGuildInfo(g));
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_SetMark_Done(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_SetMark_Done.get());
        sp.Encode4(gid);
        sp.Encode2(bg);
        sp.Encode1(bgcolor);
        sp.Encode2(logo);
        sp.Encode1(logocolor);
        return sp;
    }

    // ALLIANCEDATA::Decode
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
        data.Encode4(alliance.getCapacity()); // nMaxMemberNum
        data.EncodeStr(alliance.getNotice());
        return data.getBytes();
    }

    private static List<MapleGuild> getAllianceGuilds(MapleGuildAlliance alliance) {
        List<MapleGuild> guilds = new ArrayList<>();
        if (alliance == null) {
            return guilds;
        }
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            MapleGuild guild = TacosWorld.find(0).getGuild().getGuild(alliance.getGuildId(i));
            if (guild != null) {
                guilds.add(guild);
            }
        }
        return guilds;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_RemoveGuild_Done(int gid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_RemoveGuild_Done.get());
        sp.Encode4(gid);
        sp.Encode1(1);
        return sp;
    }

    // AllianceRes_LoadDone
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_LoadDone(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_LoadDone.get());
        sp.Encode1(alliance == null ? 0 : 1);
        if (alliance != null) {
            sp.EncodeBuffer(addAllianceInfo(alliance)); // ALLIANCEDATA::Decode
        }
        return sp;
    }

    // AllianceRes_ChangeMaster_Done
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_ChangeMaster_Done(int allianceid, int newLeader, int oldLeader) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_ChangeMaster_Done.get());
        sp.Encode4(allianceid);
        sp.Encode4(oldLeader); // grade 2
        sp.Encode4(newLeader); // grade 1
        return sp;
    }

    // AllianceRes_SetGradeName_Done
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_SetGradeName_Done(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_SetGradeName_Done.get());
        sp.Encode4(alliance.getId());
        for (int i = 1; i <= 5; i++) {
            sp.EncodeStr(alliance.getRank(i));
        }
        return sp;
    }

    // AllianceRes_SetNotice_Done
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_SetNotice_Done(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_SetNotice_Done.get());
        sp.Encode4(alliance.getId());
        sp.EncodeStr(alliance.getNotice());
        return sp;
    }

    // AllianceRes_Invite_Done
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_Invite_Done(MapleGuildAlliance alliance, MapleGuild newGuild) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_Invite_Done.get());
        sp.EncodeBuffer(addAllianceInfo(alliance));
        sp.Encode4(newGuild.getId());
        sp.EncodeBuffer(getGuildInfo(newGuild));
        return sp;
    }

    // AllianceRes_ChangeLevelOrJob
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_ChangeLevelOrJob(MapleGuildCharacter mgc, int allianceid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_ChangeLevelOrJob.get());
        sp.Encode4(allianceid);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.Encode4(mgc.getLevel());
        sp.Encode4(mgc.getJobId());
        return sp;
    }

    // AllianceReq_Invite
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_Invite(String allianceName, TacosCharacter inviter) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceReq_Invite.get());
        sp.Encode4(inviter.getGuildId());
        sp.EncodeStr(inviter.getName());
        sp.EncodeStr(allianceName);
        return sp;
    }

    private static byte[] addThread(MapleBBSThread rs) {
        ServerPacket data = new ServerPacket();

        data.Encode4(rs.localthreadID);
        data.Encode4(rs.ownerID);
        data.EncodeStr(rs.name);
        data.Encode8(TacosSharedDate.getTimestamp(rs.timestamp));
        data.Encode4(rs.icon);
        data.Encode4(rs.getReplyCount());
        return data.getBytes();
    }

    // GuildBBS_ViewEntryResult
    // CUIGuildBBS::OnGuildBBSPacket
    public static ServerPacket GuildBBS_ViewEntryResult(MapleBBSThread thread) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildBBS);

        sp.Encode1(OpsGuildBBS.GuildBBS_ViewEntryResult.get());
        sp.Encode4(thread.localthreadID); // nCurEntryID
        sp.Encode4(thread.ownerID); // nCurCharacterID
        sp.Encode8(TacosSharedDate.getTimestamp(thread.timestamp)); // ftCurDate
        sp.EncodeStr(thread.name);
        sp.EncodeStr(thread.text);
        sp.Encode4(thread.icon); // nEmoticon
        sp.Encode4(thread.getReplyCount());
        for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
            sp.Encode4(reply.replyid); // m_nSN
            sp.Encode4(reply.ownerID);
            sp.Encode8(TacosSharedDate.getTimestamp(reply.timestamp));
            sp.EncodeStr(reply.content);
        }
        return sp;
    }

    // GuildBBS_EntryNotFound
    // CUIGuildBBS::OnGuildBBSPacket
    public static ServerPacket GuildBBS_EntryNotFound() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildBBS);

        sp.Encode1(OpsGuildBBS.GuildBBS_EntryNotFound.get());
        return sp;
    }

    // GuildBBS_LoadListResult, start = index of the first thread.
    // CUIGuildBBS::OnGuildBBSPacket
    public static ServerPacket GuildBBS_LoadListResult(MapleGuild guild, int start) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildBBS);

        sp.Encode1(OpsGuildBBS.GuildBBS_LoadListResult.get());
        MapleBBSThread notice = (guild != null) ? guild.getBBSNotice() : null;
        List<MapleBBSThread> threads = (guild != null) ? guild.getBBS() : new ArrayList<>();
        sp.Encode1(notice != null ? 1 : 0);
        if (notice != null) {
            sp.EncodeBuffer(addThread(notice));
        }
        if (start < 0 || threads.size() <= start) {
            start = 0;
        }
        int count = Math.min(10, threads.size() - start); // 10 threads per page
        sp.Encode4(threads.size()); // nEntryListTotalCount
        sp.Encode4(count);
        for (int i = 0; i < count; i++) {
            sp.EncodeBuffer(addThread(threads.get(start + i)));
        }
        return sp;
    }

    // AllianceRes_CreateDone
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_CreateDone(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_CreateDone.get());
        sp.EncodeBuffer(addAllianceInfo(alliance));
        // GUILDDATA for each guild id in ALLIANCEDATA.
        for (MapleGuild guild : getAllianceGuilds(alliance)) {
            sp.EncodeBuffer(getGuildInfo(guild));
        }
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_SetGradeName_Done(int gid, String[] ranks) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_SetGradeName_Done.get());
        sp.Encode4(gid);
        for (String r : ranks) {
            sp.EncodeStr(r);
        }
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult(OpsGuild ops) {
        return GuildResult(ops.get());
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult(int code) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(code);
        return sp;
    }

    // AllianceRes_Withdraw_Done
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_Withdraw_Done(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_Withdraw_Done.get());
        sp.EncodeBuffer(addAllianceInfo(alliance));
        sp.Encode4(expelledGuild.getId());
        // the guild that left reads its new GUILDDATA and the message type.
        sp.EncodeBuffer(getGuildInfo(expelledGuild));
        sp.Encode1(expelled ? 1 : 0); // 1 = expelled, 0 = left
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_IncMaxMemberNum_Done(int gid, int capacity) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_IncMaxMemberNum_Done.get());
        sp.Encode4(gid);
        sp.Encode1(capacity);
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_IncPoint_Done(int gid, int GP) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_IncPoint_Done.get());
        sp.Encode4(gid);
        sp.Encode4(GP);
        sp.Encode4(1, Config.PostBB()); // nLevel
        return sp;
    }

    // AllianceRes_UpdateAllianceInfo
    // CWvsContext::OnAllianceResult
    public static ServerPacket AllianceResult_UpdateAllianceInfo(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AllianceResult);

        sp.Encode1(OpsAlliance.AllianceRes_UpdateAllianceInfo.get());
        sp.EncodeBuffer(addAllianceInfo(alliance));
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_InviteGuild(int gid, String charName, int levelFrom, int jobFrom) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildReq_InviteGuild.get());
        sp.Encode4(gid);
        sp.EncodeStr(charName);
        sp.Encode4(levelFrom);
        sp.Encode4(jobFrom);
        return sp;
    }

    // CWvsContext::OnGuildResult
    public static ServerPacket GuildResult_SetMemberGrade_Done(MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GuildResult);

        sp.Encode1(OpsGuild.GuildRes_SetMemberGrade_Done.get());
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.Encode1(mgc.getGuildRank());
        return sp;
    }

    // CWvsContext::OnFamilyJoinRequestResult
    public static ServerPacket FamilyJoinRequestResult(boolean accepted, String added) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyJoinRequestResult);

        sp.Encode1(accepted ? 1 : 0);
        sp.EncodeStr(added);
        return sp;
    }

    // CWvsContext::OnFamilyFamousPointIncResult
    public static ServerPacket FamilyFamousPointIncResult(int r, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyFamousPointIncResult);

        sp.Encode4(r);
        sp.EncodeStr(name);
        return sp;
    }

    // CWvsContext::OnFamilyJoinRequest
    public static ServerPacket FamilyJoinRequest(int cid, int otherLevel, int otherJob, String inviter) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyJoinRequest);

        sp.Encode4(cid); //the inviter
        sp.Encode4(otherLevel);
        sp.Encode4(otherJob);
        sp.EncodeStr(inviter);
        return sp;
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
        return data.getBytes();
    }

    // CWvsContext::OnFamilyNotifyLoginOrLogout
    public static ServerPacket FamilyNotifyLoginOrLogout(boolean online, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyNotifyLoginOrLogout);

        sp.Encode1(online ? 1 : 0);
        sp.EncodeStr(name);
        return sp;
    }

    // CWvsContext::OnFamilyResult
    public static ServerPacket FamilyResult(OpsFamily ops) {
        return FamilyResult(ops, 0);
    }

    // CWvsContext::OnFamilyResult
    public static ServerPacket FamilyResult(OpsFamily ops, int value) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyResult);

        sp.Encode4(ops.get());
        sp.Encode4(value); // meso for NotEnoughMoney
        return sp;
    }

    // CWvsContext::OnFamilySummonRequest
    public static ServerPacket FamilySummonRequest(String name, String mapname) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilySummonRequest);

        sp.EncodeStr(name);
        sp.EncodeStr(mapname);
        return sp;
    }

    public static ServerPacket cancelFamilyBuff() {
        return FamilySetPrivilege(0, 0, 0, 0);
    }

    // CWvsContext::OnFamilyPrivilegeList
    public static ServerPacket FamilyPrivilegeList() {
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
        return sp;
    }

    // CWvsContext::OnFamilySetPrivilege, Privilege::Decode
    public static ServerPacket FamilySetPrivilege(int type, int buffnr, int amount, int time) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilySetPrivilege);

        sp.Encode1(type);
        if (type != 0) {
            sp.Encode4(buffnr); // nIndex
            sp.Encode4(type == 3 ? 0 : amount); // dwIncExpRate
            sp.Encode4(type == 2 ? 0 : amount); // dwIncDropRate
            sp.Encode1(0); // DecodeTime
            sp.Encode4(time); // DecodeTime
        }
        return sp;
    }

    // CUIFamilyChart::DecodeLocalChart
    public static ServerPacket FamilyChartResult(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyChartResult);

        MapleFamily family = chr.getWorld().getFamily().getFamily(chr.getFamilyId());
        MapleFamilyCharacter mine = (chr.getMFC() != null) ? chr.getMFC() : new MapleFamilyCharacter(chr, 0, 0, 0, 0);
        // leader, grand parent, parent, me, sibling, children and grand children.
        Map<Integer, MapleFamilyCharacter> members = new LinkedHashMap<>();
        List<MapleFamilyCharacter> grand_children = new ArrayList<>();
        if (family == null) {
            members.put(mine.getId(), mine);
        } else {
            addFamilyMember(members, family.getMFC(family.getLeaderId()));
            MapleFamilyCharacter parent = (chr.getSeniorId() > 0) ? family.getMFC(chr.getSeniorId()) : null;
            if (parent != null && parent.getSeniorId() > 0) {
                addFamilyMember(members, family.getMFC(parent.getSeniorId()));
            }
            addFamilyMember(members, parent);
            members.put(mine.getId(), mine);
            if (parent != null) {
                for (int sibling_id : new int[]{parent.getJunior1(), parent.getJunior2()}) {
                    if (sibling_id > 0 && sibling_id != chr.getId()) {
                        addFamilyMember(members, family.getMFC(sibling_id));
                    }
                }
            }
            for (int child_id : new int[]{chr.getJunior1(), chr.getJunior2()}) {
                MapleFamilyCharacter child = (child_id > 0) ? family.getMFC(child_id) : null;
                if (child == null) {
                    continue;
                }
                addFamilyMember(members, child);
                for (int grand_child_id : new int[]{child.getJunior1(), child.getJunior2()}) {
                    MapleFamilyCharacter grand_child = (grand_child_id > 0) ? family.getMFC(grand_child_id) : null;
                    if (grand_child != null) {
                        addFamilyMember(members, grand_child);
                        grand_children.add(grand_child);
                    }
                }
            }
        }

        sp.Encode4(chr.getId());
        sp.Encode4(members.size());
        for (MapleFamilyCharacter member : members.values()) {
            sp.EncodeBuffer(addFamilyCharInfo(member));
        }
        // m_mStatistic : number of descendants
        sp.Encode4(grand_children.size());
        for (MapleFamilyCharacter grand_child : grand_children) {
            sp.Encode4(grand_child.getId());
            sp.Encode4(grand_child.getDescendants());
        }
        // m_mPrivilege : times used
        List<SimpleImmutableEntry<Integer, Integer>> used = chr.usedBuffs();
        sp.Encode4(used.size());
        for (SimpleImmutableEntry<Integer, Integer> privilege : used) {
            sp.Encode4(privilege.getKey());
            sp.Encode4(privilege.getValue());
        }
        sp.Encode2(2);
        return sp;
    }

    private static void addFamilyMember(Map<Integer, MapleFamilyCharacter> members, MapleFamilyCharacter member) {
        if (member != null) {
            members.putIfAbsent(member.getId(), member);
        }
    }

    // CWvsContext::OnFamilyInfoResult
    public static ServerPacket FamilyInfoResult(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyInfoResult);

        sp.Encode4(chr.getCurrentRep()); //rep
        sp.Encode4(chr.getTotalRep()); // total rep
        sp.Encode4(chr.getTotalRep()); //rep recorded today
        sp.Encode2(chr.getNoJuniors());
        sp.Encode2(2);
        sp.Encode2(chr.getNoJuniors());
        MapleFamily family = chr.getWorld().getFamily().getFamily(chr.getFamilyId());
        if (family != null) {
            sp.Encode4(family.getLeaderId()); //??? 9D 60 03 00
            sp.EncodeStr(family.getLeaderName());
            sp.EncodeStr(family.getNotice()); //message?
        } else {
            sp.Encode8(0);
        }
        List<SimpleImmutableEntry<Integer, Integer>> b = chr.usedBuffs();
        sp.Encode4(b.size());
        for (SimpleImmutableEntry<Integer, Integer> ii : b) {
            sp.Encode4(ii.getKey()); //buffid
            sp.Encode4(ii.getValue()); //times used
        }
        return sp;
    }

    // CWvsContext::OnFamilyJoinAccepted
    public static ServerPacket FamilyJoinAccepted(String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FamilyJoinAccepted);

        sp.EncodeStr(name);
        return sp;
    }

    // CWvsContext::OnNotifyLevelUp
    public static ServerPacket NotifyLevelUp(boolean family, int level, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_NotifyLevelUp);

        sp.Encode1(family ? 1 : 2);
        sp.Encode4(level);
        sp.EncodeStr(name);
        return sp;
    }

    // never used in JMS, other region may be used this packet before Lv20 limit removal.
    public static ServerPacket KOC_UI_Open() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_164_KOC_UI_Open);

        return sp;
    }

    // CWvsContext::OnNotifyJobChange
    public static ServerPacket NotifyJobChange(boolean family, int jobid, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_NotifyJobChange);

        sp.Encode1(family ? 1 : 0);
        sp.Encode4(jobid); //or is this a short
        sp.EncodeStr(name);
        return sp;
    }

    // CWvsContext::OnNotifyWedding
    public static ServerPacket NotifyWedding(boolean family, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_NotifyWedding);

        sp.Encode1(family ? 1 : 0);
        sp.EncodeStr(name);
        return sp;
    }
}
