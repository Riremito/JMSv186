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
package tacos.packet.response;

import tacos.debug.DebugLogger;
import java.util.Map;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsChatGroup;
import tacos.packet.ops.OpsLocationResult;
import tacos.packet.ops.OpsTransferChannel;
import tacos.packet.ops.OpsTransferField;
import tacos.packet.ops.Ops_Whisper;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.response.builder.PB_FieldEffect;
import odin.server.maps.MapleNodes;
import tacos.client.TacosCharacter;
import tacos.constants.TacosConstants;
import tacos.packet.ServerPacketHeader;
import tacos.server.TacosServerType;
import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
public class ResCField {

    // CField::OnTransferFieldReqIgnored
    public static ServerPacket TransferFieldReqIgnored(OpsTransferField ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TransferFieldReqIgnored);

        sp.Encode1(ops.get());
        return sp;
    }

    // CField::OnTransferChannelReqIgnored
    public static ServerPacket TransferChannelReqIgnored(OpsTransferChannel ops) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TransferChannelReqIgnored);

        sp.Encode1(ops.get());
        return sp;
    }

    // CField::OnFieldSpecificData
    public static ServerPacket FieldSpecificData(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FieldSpecificData);
        int map_id = chr.getMapId();

        // no data.
        // CField_ShowaBath::DecodeFieldSpecificData
        // CField_Tutorial::DecodeFieldSpecificData
        if (TacosConstants.is_bath(map_id)) {
            return sp;
        }

        // 1 byte extra data.
        // CField_Coconut::DecodeFieldSpecificData
        if (TacosConstants.is_coconut(map_id)) {
            sp.Encode1(chr.getCoconutTeam());
            return sp;
        }
        // CField_Battlefield::DecodeFieldSpecificData
        // CField_MonsterCarnival::DecodeFieldSpecificData
        // CField_MonsterCarnivalRevive::DecodeFieldSpecificData
        return sp;
    }

    // CField::OnGroupMessage
    public static ServerPacket GroupMessage(OpsChatGroup ops, String name, String message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_GroupMessage);

        sp.Encode1(ops.get());
        sp.EncodeStr(name); // sFrom
        sp.EncodeStr(message); // sMsg
        return sp;
    }

    // CField::OnWhisper
    public static ServerPacket Whisper(Ops_Whisper req_res, Ops_Whisper loc_whis, TacosCharacter chr_from, String name_to, String message, TacosCharacter chr_to) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Whisper);

        sp.Encode1(req_res.get() | loc_whis.get());
        switch (req_res) {
            case WP_Result: {
                if (loc_whis == Ops_Whisper.WP_Whisper) {
                    sp.EncodeStr(name_to);
                    sp.Encode1((chr_to != null) ? 1 : 0); // found or not found
                    break;
                }
                if (loc_whis == Ops_Whisper.WP_Location) {
                    sp.EncodeStr(name_to);
                    // not found
                    if (chr_to == null) {
                        sp.Encode1(OpsLocationResult.LR_None.get());
                        sp.Encode4(0);
                        break;
                    }
                    // cs & itc
                    if (chr_to.getServerType() == TacosServerType.ITC_SERVER || chr_to.getServerType() == TacosServerType.CASHSHOP_SERVER) {
                        sp.Encode1(OpsLocationResult.LR_ShopSvr.get());
                        sp.Encode4(0);
                        break;
                    }
                    // same channel
                    if (chr_to.getChannelId() == chr_from.getChannelId()) {
                        sp.Encode1(OpsLocationResult.LR_GameSvr.get());
                        sp.Encode4(chr_to.getPosMap());
                        break;
                    }
                    // different channel
                    sp.Encode1(OpsLocationResult.LR_OtherChannel.get());
                    sp.Encode4(chr_to.getClient().getChannelId());
                    break;
                }
                break;
            }
            case WP_Receive: {
                if (loc_whis == Ops_Whisper.WP_Whisper) {
                    sp.EncodeStr(chr_from.getName()); // sender name
                    sp.Encode1(chr_from.getChannelId() - 1); // sender channel
                    sp.Encode1(0); // admin?
                    sp.EncodeStr(message); // sender message
                    break;
                }
                break;
            }
            default: {
                break;
            }
        }
        // 9  (0x09) = 0x01 | 0x08
        // 72 (0x48) = 0x08 | 0x40
        return sp;
    }

    // CField::OnCoupleMessage, not tested.
    public static ServerPacket CoupleMessage(String sSender, String sChatText) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CoupleMessage);

        sp.Encode1(5);
        sp.EncodeStr(sSender); // sSender
        sp.Encode1(0);
        sp.EncodeStr(sChatText); // sChatText
        return sp;
    }

    // CField::OnSummonItemInavailable
    public static ServerPacket MobSummonItemUseResult(boolean result) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobSummonItemUseResult);

        sp.Encode1(result ? 1 : 0);
        return sp;
    }

    // CField::OnFieldEffect
    public static ServerPacket FieldEffect(OpsFieldEffect flag, PB_FieldEffect pb) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FieldEffect);
        sp.Encode1(flag.get());
        switch (flag) {
            case FieldEffect_Summon: {
                sp.Encode1(0);
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            // 道場
            case FieldEffect_Tremble: {
                sp.Encode1((byte) pb.type);
                sp.Encode4(pb.delay);
                break;
            }
            case FieldEffect_Object: {
                sp.EncodeStr(pb.wz_path);
                break;
            }
            case FieldEffect_Screen: {
                sp.EncodeStr(pb.wz_path);
                break;
            }
            case FieldEffect_Sound: {
                sp.EncodeStr(pb.wz_path);
                break;
            }
            case FieldEffect_MobHPTag: {
                sp.Encode4(pb.monster.getId());
                if (pb.monster.getHp() > Integer.MAX_VALUE) {
                    sp.Encode4((int) (((double) pb.monster.getHp() / pb.monster.getMobMaxHp()) * Integer.MAX_VALUE));
                } else {
                    sp.Encode4((int) pb.monster.getHp());
                }
                if (pb.monster.getMobMaxHp() > Integer.MAX_VALUE) {
                    sp.Encode4(Integer.MAX_VALUE);
                } else {
                    sp.Encode4((int) pb.monster.getMobMaxHp());
                }
                sp.Encode1(pb.monster.getStats().getTagColor());
                sp.Encode1(pb.monster.getStats().getTagBgColor());
                break;
            }
            case FieldEffect_ChangeBGM: {
                sp.EncodeStr(pb.wz_path);
                break;
            }
            case FieldEffect_RewordRullet: {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            default: {
                DebugLogger.ErrorLog("FieldEffect not coded : " + flag);
                break;
            }
        }
        return sp;
    }

    // CField::OnFieldObstacleOnOff
    public static ServerPacket FieldObstacleOnOff(String env, int mode) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FieldObstacleOnOff);

        sp.EncodeStr(env);
        sp.Encode4(mode);
        return sp;
    }

    // CField::OnFieldObstacleOnOffStatus
    public static ServerPacket FieldObstacleOnOffStatus(TacosMap map) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FieldObstacleOnOffStatus);

        sp.Encode4(map.getEnvironment().size());
        for (Map.Entry<String, Integer> mp : map.getEnvironment().entrySet()) {
            sp.EncodeStr(mp.getKey());
            sp.Encode4(mp.getValue());
        }
        return sp;
    }

    // CField::OnFieldObstacleAllReset, not tested.
    public static ServerPacket FieldObstacleAllReset() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FieldObstacleAllReset);

        return sp;
    }

    // CField::OnBlowWeather
    public static ServerPacket BlowWeather(String msg, int itemid, boolean active) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BlowWeather);

        sp.Encode4(active ? itemid : 0);
        if (active && itemid != 0) {
            sp.EncodeStr(msg);
        }

        return sp;
    }

    // CField::OnPlayJukeBox
    public static ServerPacket PlayJukeBox(int item_id, String name) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PlayJukeBox);

        sp.Encode4(item_id); // nItemID
        sp.EncodeStr(name); // sCharacterName
        return sp;
    }

    // LP_AdminResult
    public static ServerPacket AdminResult(int value) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AdminResult);

        sp.Encode1(value);
        sp.EncodeZeroBytes(17);
        return sp;
    }

    // CField::OnQuiz
    public static ServerPacket Quiz(int questionSet, int questionId, boolean askQuestion) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Quiz);

        sp.Encode1(askQuestion ? 1 : 0); // bQuestion
        sp.Encode1(questionSet); // nCategory
        sp.Encode2(questionId);
        return sp;
    }

    // CField::OnDesc
    public static ServerPacket Desc() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Desc);

        sp.Encode1(0);
        return sp;
    }

    // CField::OnClock
    public static ServerPacket Clock(int hour, int min, int sec) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Clock);

        sp.Encode1(1); // station clock
        sp.Encode1(hour); // nHour
        sp.Encode1(min); // nMin
        sp.Encode1(sec); // nSec
        return sp;
    }

    // CField::OnClock
    public static ServerPacket Clock(int time) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Clock);

        sp.Encode1(2); // timer
        sp.Encode4(time); // tDuration (sec)
        return sp;
    }

    // CField::OnSetQuestClear, not tested.
    public static ServerPacket SetQuestClear() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetQuestClear);

        return sp;
    }

    // CField::OnSetQuestTime, not tested.
    public static ServerPacket SetQuestTime(int nQuestID, long ftStart, long ftEnd) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetQuestTime);

        sp.Encode1(1); // count
        sp.Encode4(nQuestID);
        sp.Encode8(ftStart); // ftStart
        sp.Encode8(ftEnd); // ftEnd
        return sp;
    }

    // CField::OnWarnMessage, not tested.
    public static ServerPacket Warn(String message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Warn);

        sp.EncodeStr(message);
        return sp;
    }

    // CField::OnSetObjectState, not tested.
    public static ServerPacket SetObjectState(String name, int state) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetObjectState);

        sp.EncodeStr(name);
        sp.Encode4(state);
        return sp;
    }

    // CField::OnDestroyClock
    public static ServerPacket DestroyClock() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DestroyClock);

        return sp;
    }

    // CField::OnStalkResult, not tested.
    public static ServerPacket StalkResult(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_StalkResult);

        sp.Encode4(1); // count
        sp.Encode4(chr.getId());
        sp.Encode1(1);
        sp.EncodeStr(chr.getName());
        sp.Encode4(chr.getPosition().x);
        sp.Encode4(chr.getPosition().y);
        return sp;
    }

    // CField::OnFootHoldInfo
    public static ServerPacket FootHoldInfo(TacosMap map) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FootHoldInfo);

        sp.Encode4(map.getNodeInfo().getPlatforms().size());
        for (MapleNodes.MaplePlatform mp : map.getNodeInfo().getPlatforms()) {
            sp.EncodeStr(mp.name);
            sp.Encode4(mp.start);
            sp.Encode4(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                sp.Encode4(mp.SN.get(x));
            }
            sp.Encode4(mp.speed);
            sp.Encode4(mp.x1);
            sp.Encode4(mp.x2);
            sp.Encode4(mp.y1);
            sp.Encode4(mp.y2);
            sp.Encode4(mp.x1); //?
            sp.Encode4(mp.y1);
            sp.Encode2(mp.r);
        }

        return sp;
    }

    // CField::OnRequestFootHoldInfo, not tested.
    public static ServerPacket RequestFootHoldInfo() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_RequestFootHoldInfo);

        return sp;
    }

    // CField_KillCount::OnKillCountInfo, not tested.
    public static ServerPacket FieldKillCount(int m_nOnKill) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_FieldKillCount);

        sp.Encode4(m_nOnKill); // m_nOnKill
        return sp;
    }

    // CMessageBoxPool::OnCreateFailed, not tested.
    public static ServerPacket CreateMessgaeBoxFailed() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CreateMessgaeBoxFailed);

        return sp;
    }

    // CMessageBoxPool::OnMessageBoxEnterField, not tested.
    public static ServerPacket MessageBoxEnterField(int dwMessageBoxID, int nItemID, String sHostName, String sMessage, int x, int y) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MessageBoxEnterField);

        sp.Encode4(dwMessageBoxID); // dwMessageBoxID
        sp.Encode4(nItemID); // nItemID
        sp.EncodeStr(sHostName);
        sp.EncodeStr(sMessage);
        sp.Encode2(x); // ptHost.x
        sp.Encode2(y); // ptHost.y
        return sp;
    }

    // CMessageBoxPool::OnMessageBoxLeaveField, not tested.
    public static ServerPacket MessageBoxLeaveField(int nLeaveType, int dwID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MessageBoxLeaveField);

        sp.Encode1(nLeaveType);
        sp.Encode4(dwID); // dwID
        return sp;
    }

    // CField_GuildBoss::OnHealerMove, not tested.
    public static ServerPacket HealerMove(int y) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_HealerMove);

        sp.Encode2(y);
        return sp;
    }

    // CField_GuildBoss::OnPulleyStateChange, not tested.
    public static ServerPacket PulleyStateChange(int m_nState) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_PulleyStateChange);

        sp.Encode1(m_nState); // m_nState
        return sp;
    }

    // CField_Battlefield::OnScoreUpdate, not tested.
    public static ServerPacket BattlefieldScore(int score1, int score2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BattlefieldScore);

        sp.Encode1(score1);
        sp.Encode1(score2);
        return sp;
    }

    // CField_Battlefield::OnTeamChanged, not tested.
    public static ServerPacket BattlefieldTeamChanged(TacosCharacter chr, int nTeam) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BattlefieldTeamChanged);

        sp.Encode4(chr.getId());
        sp.Encode1(nTeam);
        return sp;
    }

    // CField_Witchtower::OnScoreUpdate, not tested.
    public static ServerPacket WitchtowerScore(int score) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_WitchtowerScore);

        sp.Encode1(score);
        return sp;
    }

    // CField::OnHontaleTimer
    public static ServerPacket HontaleTimer(boolean spawned, int time_minute) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_HontaleTimer);

        sp.Encode1(spawned ? 1 : 0);
        sp.Encode1(time_minute); // minute
        return sp;
    }

    // CField::OnChaosZakumTimer
    public static ServerPacket ChaosZakumTimer(boolean spawned, int time_second) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ChaosZakumTimer);

        sp.Encode1(spawned ? 1 : 0);
        sp.Encode4(time_second);
        return sp;
    }

    // CField::OnHontailTimer
    public static ServerPacket HontailTimer(boolean spawned, int time_second) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_HontailTimer);

        sp.Encode1(spawned ? 1 : 0);
        sp.Encode4(time_second);
        return sp;
    }

    // CField::OnZakumTimer
    public static ServerPacket ZakumTimer(boolean spawned, int time_second) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ZakumTimer);

        sp.Encode1(spawned ? 1 : 0);
        sp.Encode4(time_second);
        return sp;
    }

    // CField_Tournament::OnTournament, not tested.
    public static ServerPacket Tournament(int flag, int value1, int value2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Tournament);

        sp.Encode1(flag);
        sp.Encode1(value1);
        sp.Encode1(value2);
        return sp;
    }

    // CField_Tournament::OnTournamentMatchTable, not tested.
    public static ServerPacket TournamentMatchTable(byte[] m_aaMatch, int m_nState) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TournamentMatchTable);

        sp.EncodeBuffer(m_aaMatch, 0x300); // m_aaMatch
        sp.Encode1(m_nState); // m_nState
        return sp;
    }

    // CField_Tournament::OnTournamentSetPrize, not tested.
    public static ServerPacket TournamentSetPrize(int type, boolean has_prize, int prize1, int prize2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TournamentSetPrize);

        sp.Encode1(type);
        sp.Encode1(has_prize ? 1 : 0);
        sp.Encode4(prize1, has_prize);
        sp.Encode4(prize2, has_prize);
        return sp;
    }

    // CField_Tournament::OnTournamentUEW, not tested.
    public static ServerPacket TournamentNoticeUEW(int round) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TournamentNoticeUEW);

        sp.Encode1(round);
        return sp;
    }

    // CField_Tournament::OnTournamentAvatarInfo, not tested.
    public static ServerPacket TournamentAvatarInfo() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TournamentAvatarInfo);

        return sp;
    }

    // CField_Wedding::OnWeddingProgress, not tested.
    public static ServerPacket WeddingProgress(int nStep, int m_dwGroomID, int m_dwBrideID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_WeddingProgress);

        sp.Encode1(nStep);
        sp.Encode4(m_dwGroomID); // m_dwGroomID
        sp.Encode4(m_dwBrideID); // m_dwBrideID
        return sp;
    }

    // CField_Wedding::OnWeddingCeremonyEnd, not tested.
    public static ServerPacket WeddingCremonyEnd() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_WeddingCremonyEnd);

        return sp;
    }

    // CBattleRecordMan::OnDotDamageInfo, not tested.
    public static ServerPacket BattleRecordDotDamageInfo(int damage, int count, boolean bAttrRate, int nDotAttrRate) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BattleRecordDotDamageInfo);

        sp.Encode4(damage);
        sp.Encode4(count);
        sp.Encode1(bAttrRate ? 1 : 0); // bAttrRate
        sp.Encode4(nDotAttrRate, bAttrRate); // nDotAttrRate
        return sp;
    }

    // CBattleRecordMan::OnServerOnCalcRequestResult, not tested.
    public static ServerPacket BattleRecordRequestResult(int result) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_BattleRecordRequestResult);

        sp.Encode1(result);
        return sp;
    }
}
