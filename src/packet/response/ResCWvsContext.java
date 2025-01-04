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
import config.ServerConfig;
import debug.Debug;
import handling.MaplePacket;
import java.sql.Timestamp;
import packet.ServerPacket;
import packet.request.ContextPacket;
import packet.response.struct.GW_CharacterStat;
import packet.response.struct.GW_ItemSlotBase;
import packet.response.struct.InvOp;
import packet.response.struct.SecondaryStat;

/**
 *
 * @author Riremito
 */
public class ResCWvsContext {

    // CWvsContext::OnInventoryOperation
    public static MaplePacket InventoryOperation(boolean unlock, InvOp io) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryOperation);
        sp.Encode1(unlock ? 1 : 0);// m_bExclRequestSent, unlock
        sp.Encode1((io == null) ? 0 : io.get().size());

        if (ServerConfig.JMS302orLater()) {
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
                        sp.EncodeBuffer(GW_ItemSlotBase.Encode(v.item));
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
        return sp.Get();
    }

    // CWvsContext::OnChangeSkillRecordResult
    public static final MaplePacket updateSkill(int skillid, int level, int masterlevel, long expiration) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ChangeSkillRecordResult);
        sp.Encode1(1);
        if (ServerConfig.JMS302orLater()) {
            sp.Encode1(0);
        }
        sp.Encode2(1);
        sp.Encode4(skillid);
        sp.Encode4(level);
        sp.Encode4(masterlevel);
        if (ServerConfig.JMS164orLater()) {
            sp.Encode8(ServerConfig.expiration_date);
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

    public static final MaplePacket Message(ContextPacket.MessageArg ma) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Message);
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
                        Debug.ErrorLog("Unknown DropPickUp Type" + ma.dt.get());
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
                        sp.Encode8(System.currentTimeMillis());
                        break;
                    }
                    default: {
                        Debug.ErrorLog("Unknown QuestRecord Type" + ma.dt.get());
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
            case MS_JMS_PACHINKO: {
                sp.Encode4(ma.Inc_Tama);
                break;
            }
            default: {
                break;
            }
        }
        return sp.Get();
    }

}
