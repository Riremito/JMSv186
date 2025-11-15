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

import odin.client.MapleCharacter;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import java.util.Map;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsChatGroup;
import tacos.packet.ops.OpsLocationResult;
import tacos.packet.ops.OpsTransferChannel;
import tacos.packet.ops.OpsTransferField;
import tacos.packet.ops.Ops_Whisper;
import tacos.packet.ops.arg.ArgFieldEffect;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleNodes;
import odin.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCField {

    public static MaplePacket TransferFieldReqIgnored(OpsTransferField ops) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TransferFieldReqIgnored);

        sp.Encode1(ops.get());
        return sp.get();
    }

    public static MaplePacket TransferChannelReqIgnored(OpsTransferChannel ops) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TransferChannelReqIgnored);

        sp.Encode1(ops.get());
        return sp.get();
    }

    public static MaplePacket MobSummonItemUseResult(boolean result) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MobSummonItemUseResult);

        sp.Encode1(result ? 1 : 0);
        return sp.get();
    }

    public static MaplePacket PlayJukeBox(int item_id, String name) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PlayJukeBox);

        sp.Encode4(item_id);
        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket GroupMessage(OpsChatGroup ops, String name, String message) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GroupMessage);

        sp.Encode1(ops.get());
        sp.EncodeStr(name);
        sp.EncodeStr(message);
        return sp.get();
    }

    // environmentChange, musicChange, showEffect, playSound
    // ShowBossHP, trembleEffect
    public static MaplePacket FieldEffect(ArgFieldEffect st) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FieldEffect);
        sp.Encode1(st.flag.get());
        switch (st.flag) {
            case FieldEffect_Summon: {
                sp.Encode1(0);
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            // 道場
            case FieldEffect_Tremble: {
                sp.Encode1((byte) st.type);
                sp.Encode4(st.delay);
                break;
            }
            case FieldEffect_Object: {
                sp.EncodeStr(st.wz_path);
                break;
            }
            case FieldEffect_Screen: {
                sp.EncodeStr(st.wz_path);
                break;
            }
            case FieldEffect_Sound: {
                sp.EncodeStr(st.wz_path);
                break;
            }
            case FieldEffect_MobHPTag: {
                sp.Encode4(st.monster.getId());
                if (st.monster.getHp() > Integer.MAX_VALUE) {
                    sp.Encode4((int) (((double) st.monster.getHp() / st.monster.getMobMaxHp()) * Integer.MAX_VALUE));
                } else {
                    sp.Encode4((int) st.monster.getHp());
                }
                if (st.monster.getMobMaxHp() > Integer.MAX_VALUE) {
                    sp.Encode4(Integer.MAX_VALUE);
                } else {
                    sp.Encode4((int) st.monster.getMobMaxHp());
                }
                sp.Encode1(st.monster.getStats().getTagColor());
                sp.Encode1(st.monster.getStats().getTagBgColor());
                break;
            }
            case FieldEffect_ChangeBGM: {
                sp.EncodeStr(st.wz_path);
                break;
            }
            case FieldEffect_RewordRullet: {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            default: {
                DebugLogger.ErrorLog("FieldEffect not coded : " + st.flag);
                break;
            }
        }
        return sp.get();
    }

    public static MaplePacket showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_Quiz.get());
        mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);
        return mplew.getPacket();
    }

    public static MaplePacket showChaosHorntailShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_HontaleTimer.get());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket showChaosZakumShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ChaosZakumTimer.get());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket stopClock() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_DestroyClock.get());
        return mplew.getPacket();
    }

    public static MaplePacket showHorntailShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_HontailTimer.get());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket showZakumShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ZakumTimer.get());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket showEquipEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FieldSpecificData.get());
        return mplew.getPacket();
    }

    public static MaplePacket showEquipEffect(int team) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FieldSpecificData.get());
        mplew.writeShort(team);
        return mplew.getPacket();
    }

    public static final MaplePacket getUpdateEnvironment(final MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FieldObstacleOnOffStatus.get());
        mplew.writeInt(map.getEnvironment().size());
        for (Map.Entry<String, Integer> mp : map.getEnvironment().entrySet()) {
            mplew.writeMapleAsciiString(mp.getKey());
            mplew.writeInt(mp.getValue());
        }
        return mplew.getPacket();
    }

    public static MaplePacket environmentMove(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FieldObstacleOnOff.get());
        mplew.writeMapleAsciiString(env);
        mplew.writeInt(mode);
        return mplew.getPacket();
    }

    public static MaplePacket getClockTime(int hour, int min, int sec) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Clock);
        sp.Encode1(1); // station clock
        sp.Encode1(hour);
        sp.Encode1(min);
        sp.Encode1(sec);
        return sp.get();
    }

    public static MaplePacket getClock(int time) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Clock);
        sp.Encode1(2); // timer
        sp.Encode4(time);
        return sp.get();
    }

    public static MaplePacket Whisper(Ops_Whisper req_res, Ops_Whisper loc_whis, MapleCharacter chr_from, String name_to, String message, MapleCharacter chr_to) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Whisper);

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
                    if (chr_to.getClient().getChannel() < 0) {
                        sp.Encode1(OpsLocationResult.LR_ShopSvr.get());
                        sp.Encode4(0);
                        break;
                    }
                    // same channel
                    if (chr_to.getClient().getChannel() == chr_from.getClient().getChannel()) {
                        sp.Encode1(OpsLocationResult.LR_GameSvr.get());
                        sp.Encode4(chr_to.getMapId());
                        break;
                    }
                    // different channel
                    sp.Encode1(OpsLocationResult.LR_OtherChannel.get());
                    sp.Encode4(chr_to.getClient().getChannel());
                    break;
                }
                break;
            }
            case WP_Receive: {
                if (loc_whis == Ops_Whisper.WP_Whisper) {
                    sp.EncodeStr(chr_from.getName()); // sender name
                    sp.Encode1(chr_from.getClient().getChannel() - 1); // sender channel
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
        return sp.get();
    }

    // CField::OnBlowWeather
    public static MaplePacket BlowWeather(String msg, int itemid, boolean active) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_BlowWeather);

        sp.Encode4(active ? itemid : 0);
        if (active && itemid != 0) {
            sp.EncodeStr(msg);
        }

        return sp.get();
    }

    public static final MaplePacket getMovingPlatforms(final MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FootHoldInfo.get());
        mplew.writeInt(map.getPlatforms().size());
        for (MapleNodes.MaplePlatform mp : map.getPlatforms()) {
            mplew.writeMapleAsciiString(mp.name);
            mplew.writeInt(mp.start);
            mplew.writeInt(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                mplew.writeInt(mp.SN.get(x));
            }
            mplew.writeInt(mp.speed);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.x2);
            mplew.writeInt(mp.y1);
            mplew.writeInt(mp.y2);
            mplew.writeInt(mp.x1); //?
            mplew.writeInt(mp.y1);
            mplew.writeShort(mp.r);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showEventInstructions() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_Desc.get());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket GameMaster_Func(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AdminResult.get());
        mplew.write(value);
        mplew.writeZeroBytes(17);
        return mplew.getPacket();
    }

}
