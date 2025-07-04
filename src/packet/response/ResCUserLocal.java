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
package packet.response;

import client.MapleCharacter;
import config.ServerConfig;
import constants.ServerConstants;
import handling.MaplePacket;
import java.awt.Point;
import java.util.List;
import packet.ServerPacket;
import packet.ops.arg.ArgUserEffect;
import packet.ops.OpsQuest;
import packet.ops.OpsUserEffect;
import packet.response.struct.TestHelper;
import server.movement.LifeMovementFragment;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCUserLocal {

    public static MaplePacket SitResult(int id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserSitResult);
        boolean is_cancel = (id == -1);
        sp.Encode1(is_cancel ? 0 : 1);
        if (!is_cancel) {
            sp.Encode2(id); // sit
        }
        return sp.get();
    }

    public static MaplePacket EffectLocal(ArgUserEffect arg) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectLocal);
        sp.EncodeBuffer(EffectData(arg));
        return sp.get();
    }

    public static byte[] EffectData(ArgUserEffect arg) {
        ServerPacket data = new ServerPacket();
        data.Encode1(arg.ops.get());

        switch (arg.ops) {
            case UserEffect_LevelUp:
            case UserEffect_JobChanged:
            case UserEffect_QuestComplete:
            case UserEffect_MonsterBookCardGet:
            case UserEffect_ItemLevelUp: {
                // no data
                break;
            }
            default: {
                break;
            }
        }
        // todo
        return data.get().getBytes();
    }

    public static MaplePacket showRewardItemAnimation(int itemId, String effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.get());
        mplew.write(15);
        mplew.writeInt(itemId);
        mplew.write(effect != null && effect.length() > 0 ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            mplew.writeMapleAsciiString(effect);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showOwnBuffEffect(int skillid, int effectid) {
        return showOwnBuffEffect(skillid, effectid, (byte) 3);
    }

    public static MaplePacket showOwnBuffEffect(int skillid, int effectid, byte direction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.get());
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(1); //skill level = 1 for the lulz
        mplew.write(1); //0 = doesnt show? or is this even here
        if (direction != (byte) 3) {
            mplew.write(direction);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket showOwnHpHealed(final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.get());
        mplew.write(10); //Type
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static MaplePacket useWheel(byte charmsleft) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.get());
        mplew.write(21);
        mplew.writeLong(charmsleft);
        return mplew.getPacket();
    }

    public static MaplePacket useCharm(byte charmsleft, byte daysleft) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.get());
        mplew.write(6);
        mplew.write(1);
        mplew.write(charmsleft);
        mplew.write(daysleft);
        return mplew.getPacket();
    }

    public static final MaplePacket ShowWZEffect(final String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.get());
        mplew.write(19);
        mplew.writeMapleAsciiString(data);
        return mplew.getPacket();
    }

    public static final MaplePacket showOwnPetLevelUp(final int index) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.get());
        mplew.write(4);
        mplew.write(0);
        mplew.writeInt(index); // Pet Index
        return mplew.getPacket();
    }

    public static final MaplePacket ItemMakerResult(boolean is_success) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.get());
        mplew.write(OpsUserEffect.UserEffect_ItemMaker.get());
        mplew.writeInt(is_success ? 0 : 1);
        return mplew.getPacket();
    }

    public static final MaplePacket AranTutInstructionalBalloon(final String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.get());
        mplew.write(OpsUserEffect.UserEffect_AvatarOriented.get());
        mplew.writeMapleAsciiString(data);
        mplew.writeInt(1);
        return mplew.getPacket();
    }

    public static final MaplePacket Teleport(byte portal) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTeleport);
        sp.Encode1(0); // set last teleported time by client side
        sp.Encode1(portal);
        return sp.get();
    }

    public static MaplePacket sendMesobagSuccess(int mesos) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MesoGive_Succeeded);
        sp.Encode4(mesos);
        return sp.get();
    }

    public static MaplePacket sendMesobagFailed() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MesoGive_Failed);
        return sp.get();
    }

    public static MaplePacket RandomMesoBagSuccess(byte type, int mesos) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Random_Mesobag_Succeed);
        sp.Encode1(type);
        sp.Encode4(mesos);
        return sp.get();
    }

    public static MaplePacket RandomMesoBagFailed() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Random_Mesobag_Failed);
        return sp.get();
    }

    public static MaplePacket updateQuestFinish(int quest, int npc, int nextquest) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserQuestResult);
        sp.Encode1(OpsQuest.QuestRes_Act_Success.get());
        sp.Encode2(quest);
        sp.Encode4(npc);
        sp.Encode2(nextquest);
        return sp.get();
    }

    public static MaplePacket updateQuestInfo(MapleCharacter c, int quest, int npc, OpsQuest oq) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserQuestResult);
        sp.Encode1(oq.get());
        sp.Encode2(quest);
        sp.Encode4(npc);
        sp.Encode2(0);
        return sp.get();
    }

    public static MaplePacket sendHint(String hint, int width, int height) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (width < 1) {
            width = hint.length() * 10;
            if (width < 40) {
                width = 40;
            }
        }
        if (height < 5) {
            height = 5;
        }
        mplew.writeShort(ServerPacket.Header.LP_UserBalloonMsg.get());
        mplew.writeMapleAsciiString(hint);
        mplew.writeShort(width);
        mplew.writeShort(height);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static final MaplePacket sendRepairWindow(int npc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserOpenUIWithOption.get());
        mplew.writeInt(34); //sending 0x21 here opens evan skill window o.o
        mplew.writeInt(npc);
        return mplew.getPacket();
    }

    public static MaplePacket IntroLock(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetDirectionMode.get());
        mplew.write(enable ? 1 : 0);
        mplew.writeInt(enable ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket IntroDisableUI(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetStandAloneMode.get());
        mplew.write(enable ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket summonHelper(boolean summon) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserHireTutor.get());
        mplew.write(summon ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket summonMessage(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTutorMsg.get());
        mplew.write(1);
        mplew.writeInt(type);
        mplew.writeInt(7000); // probably the delay
        return mplew.getPacket();
    }

    public static MaplePacket summonMessage(String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTutorMsg.get());
        mplew.write(0);
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(200); // IDK
        mplew.writeShort(0);
        mplew.writeInt(10000); // Probably delay
        return mplew.getPacket();
    }

    public static MaplePacket testCombo(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_IncCombo.get());
        mplew.writeInt(value);
        return mplew.getPacket();
    }

    // ポイントアイテムのパチンコ玉の充填 (玉ボックス)
    public static MaplePacket PachinkoBoxSuccess(int gain) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_Pachinko_BoxSuccess);
        sp.Encode4(gain); // パチンコ玉の数
        return sp.get();
    }

    // パチンコ玉の充填に失敗した場合のダイアログ (実質不要)
    public static MaplePacket PachinkoBoxFailure() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_Pachinko_BoxFailure);
        return sp.get();
    }

    public static MaplePacket getPollReply(String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserNoticeMsg.get());
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }

    // チャット欄へのテキスト表示
    public static final MaplePacket getFollowMessage(final String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserChatMsg.get());
        /*
        // どのような内容のテキストでも問題なし
        0x0000  全体チャット
        0x0001  内緒
        0x0002  ピンク
        0x0003  友達
        0x0004  ギルド
        0x0005  連合
        0x0006  灰色
        0x0007  黄色
        0x0008  薄い黄色
        0x0009  水色
        0x000A  GM
        0x000B  薄いピンク
        0x000C  メガホン
        0x0011  濃い紫
        0x0017  黄色
        0x0018  薄い水色
        0x0019  GM
        0x001A  体験用アバター
        // "名前 : メッセージ" 形式のテキストでないとクライアントがクラッシュする
        0x000D  拡声器
        0x000E  体験用アバター
        0x000F  アバターランダムボックス
        0x0010  アイテム拡声器
        0x0012  ワールド拡声器
        0x0013  3連拡声器のプレビューと同等
        0x0014  ハート拡声器
        0x0015  ドクロ拡声器
        0x0016  ハートバルーン拡声器
         */
        mplew.writeShort(11);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static MaplePacket moveFollow(Point otherStart, Point myStart, Point otherEnd, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserPassiveMove.get());
        mplew.writePos(otherStart);
        mplew.writePos(myStart);
        TestHelper.serializeMovementList(mplew, moves);
        mplew.write(17); //what? could relate to movePlayer
        for (int i = 0; i < 8; i++) {
            mplew.write(136); //?? sometimes 44
        }
        mplew.write(8); //?
        mplew.writePos(otherEnd);
        mplew.writePos(otherStart);
        return mplew.getPacket();
    }

    public static MaplePacket getFollowMsg(int opcode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserFollowCharacterFailed.get());
        /*
        0x00    原因不明の理由で自動追尾を申請できませんでした。
        0x01    相手が自動追尾できない位置にいるか距離が遠すぎて自動追尾できません。
        0x02    相手が自動追尾できない位置にいるか距離が遠すぎて自動追尾できません。
        0x03    相手は現在自動追尾申請できない状態です。
        0x04    自動追尾中のキャラクターがいると自動追尾申請できません。
        0x05    相手が自動追尾を許可しませんでした。
        0x06    離れているようです。
        0x07    以降0x00と同じ
         */
        mplew.writeLong(opcode); //5 = canceled request.
        return mplew.getPacket();
    }

    public static MaplePacket getPollQuestion() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_JMS_Poll_Question.get());
        mplew.writeInt(1);
        mplew.writeInt(14);
        mplew.writeMapleAsciiString(ServerConstants.Poll_Question);
        mplew.writeInt(ServerConstants.Poll_Answers.length); // pollcount
        for (byte i = 0; i < ServerConstants.Poll_Answers.length; i++) {
            mplew.writeMapleAsciiString(ServerConstants.Poll_Answers[i]);
        }
        return mplew.getPacket();
    }

    public static MaplePacket SkillCooltimeSet(int skill_id, int cool_time) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SkillCooltimeSet);

        sp.Encode4(skill_id);

        if (ServerConfig.IsPostBB()) {
            sp.Encode4(cool_time);
        } else {
            sp.Encode2(cool_time);
        }
        return sp.get();
    }

}
