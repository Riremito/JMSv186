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
import tacos.config.DeveloperMode;
import tacos.config.Version;
import tacos.network.MaplePacket;
import java.awt.Point;
import tacos.packet.ServerPacket;
import tacos.packet.ops.arg.ArgUserEffect;
import tacos.packet.ops.OpsQuest;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.data.DataCUser;

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

    public static MaplePacket Emotion(MapleCharacter chr, int expression) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEmotionLocal);

        sp.EncodeBuffer(DataCUser.Emotion(expression));
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
            case UserEffect_SkillUse: {
                data.Encode4(arg.skill_id);
                data.Encode1(1);
                data.Encode1(arg.skill_on ? 0 : 1);
                break;
            }
            case UserEffect_SkillAffected: {
                data.Encode4(arg.skill_id);
                data.Encode1(1);
                break;
            }
            case UserEffect_Quest: {
                int count = 1;
                data.Encode1(count); // loop count
                if (0 < count) {
                    data.Encode4(arg.item_id);
                    data.Encode4(arg.item_quantity);
                } else {
                    data.EncodeStr("");
                    data.Encode4(0);
                }
                break;
            }
            case UserEffect_SkillSpecial: {
                data.Encode4(arg.skill_id);
                break;
            }
            case UserEffect_BuffItemEffect: {
                data.Encode4(arg.skill_id);
                break;
            }
            case UserEffect_ItemMaker: {
                data.Encode4(arg.imr.get());
            }
            default: {
                break;
            }
        }
        // todo
        return data.get().getBytes();
    }

    public static MaplePacket showRewardItemAnimation(int itemId, String effect) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectLocal);

        sp.Encode1(15);
        sp.Encode4(itemId);
        sp.Encode1(effect != null && effect.length() > 0 ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            sp.EncodeStr(effect);
        }
        return sp.get();
    }

    public static final MaplePacket showOwnHpHealed(final int amount) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectLocal);

        sp.Encode1(10); //Type
        sp.Encode4(amount);
        return sp.get();
    }

    public static MaplePacket useWheel(byte charmsleft) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectLocal);

        sp.Encode1(21);
        sp.Encode8(charmsleft);
        return sp.get();
    }

    public static MaplePacket useCharm(byte charmsleft, byte daysleft) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectLocal);

        sp.Encode1(6);
        sp.Encode1(1);
        sp.Encode1(charmsleft);
        sp.Encode1(daysleft);
        return sp.get();
    }

    public static final MaplePacket ShowWZEffect(final String data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectLocal);

        sp.Encode1(19);
        sp.EncodeStr(data);
        return sp.get();
    }

    public static final MaplePacket showOwnPetLevelUp(final int index) {
        final ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectLocal);

        sp.Encode1(4);
        sp.Encode1(0);
        sp.Encode4(index); // Pet Index
        return sp.get();
    }

    public static final MaplePacket AranTutInstructionalBalloon(final String data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectLocal);

        sp.Encode1(OpsUserEffect.UserEffect_AvatarOriented.get());
        sp.EncodeStr(data);
        sp.Encode4(1);
        return sp.get();
    }

    public static final MaplePacket Teleport(byte portal) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTeleport);
        sp.Encode1(0); // set last teleported time by client side
        sp.Encode1(portal);
        return sp.get();
    }

    public static MaplePacket MesoGive_Succeeded(int mesos) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MesoGive_Succeeded);

        sp.Encode4(mesos);
        return sp.get();
    }

    public static MaplePacket MesoGive_Failed() {
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

    // CUserLocal::OnBalloonMsg
    public static MaplePacket BalloonMsg(String hint, int width, int height) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserBalloonMsg);

        if (width < 1) {
            width = hint.length() * 10;
            if (width < 40) {
                width = 40;
            }
        }
        if (height < 5) {
            height = 5;
        }

        sp.EncodeStr(hint);
        sp.Encode2(width);
        sp.Encode2(height);
        sp.Encode1(1);

        return sp.get();
    }

    public static final MaplePacket sendRepairWindow(int npc) {
        final ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserOpenUIWithOption);

        sp.Encode4(34); //sending 0x21 here opens evan skill window o.o
        sp.Encode4(npc);
        return sp.get();
    }

    public static MaplePacket IntroLock(boolean enable) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetDirectionMode);

        sp.Encode1(enable ? 1 : 0);
        sp.Encode4(enable ? 1 : 0);
        return sp.get();
    }

    public static MaplePacket IntroDisableUI(boolean enable) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetStandAloneMode);

        sp.Encode1(enable ? 1 : 0);
        return sp.get();
    }

    public static MaplePacket summonHelper(boolean summon) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserHireTutor);

        sp.Encode1(summon ? 1 : 0);
        return sp.get();
    }

    public static MaplePacket summonMessage(int type) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTutorMsg);

        sp.Encode1(1);
        sp.Encode4(type);
        sp.Encode4(7000); // probably the delay
        return sp.get();
    }

    public static MaplePacket summonMessage(String message) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTutorMsg);

        sp.Encode1(0);
        sp.EncodeStr(message);
        sp.Encode4(200); // IDK
        sp.Encode2(0);
        sp.Encode4(10000); // Probably delay
        return sp.get();
    }

    public static MaplePacket testCombo(int value) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_IncCombo);

        sp.Encode4(value);
        return sp.get();
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
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserNoticeMsg);

        sp.EncodeStr(message);
        return sp.get();
    }

    // チャット欄へのテキスト表示
    public static final MaplePacket getFollowMessage(final String msg) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserChatMsg);

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
        sp.Encode2(11);
        sp.EncodeStr(msg);
        return sp.get();
    }

    public static MaplePacket moveFollow(Point otherStart, Point myStart, Point otherEnd/*, List<LifeMovementFragment> moves*/) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserPassiveMove);

        sp.Encode2(otherStart.x);
        sp.Encode2(otherStart.y);
        sp.Encode2(myStart.x);
        sp.Encode2(myStart.y);
        //TestHelper.serializeMovementList(mplew, moves);
        sp.Encode1(17); //what? could relate to movePlayer
        for (int i = 0; i < 8; i++) {
            sp.Encode1(136); //?? sometimes 44
        }
        sp.Encode1(8); //?
        sp.Encode2(otherEnd.x);
        sp.Encode2(otherEnd.y);
        sp.Encode2(otherStart.x);
        sp.Encode2(otherStart.y);
        return sp.get();
    }

    public static MaplePacket getFollowMsg(int opcode) {
        final ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserFollowCharacterFailed);

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
        sp.Encode8(opcode); //5 = canceled request.
        return sp.get();
    }

    public static MaplePacket PollQuestion(String questions[], String answers[][]) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_Poll_Question);

        sp.Encode4(questions.length); // number of questions, this may support only 1 question...
        for (int i = 0; i < questions.length; i++) {
            sp.Encode4(i + 1); // unused
            sp.EncodeStr(questions[i]);
            sp.Encode4(answers[i].length);
            for (int j = 0; j < answers[i].length; j++) {
                sp.EncodeStr(answers[i][j]);
            }
        }
        return sp.get();
    }

    public static MaplePacket SkillCooltimeSet(int skill_id, int cool_time) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SkillCooltimeSet);

        sp.Encode4(skill_id);

        if (DeveloperMode.DM_SKILL_COOL_TIME.getInt() != 0) {
            cool_time = Math.min(cool_time, DeveloperMode.DM_SKILL_COOL_TIME.getInt());
        }

        if (Version.PostBB()) {
            sp.Encode4(cool_time);
        } else {
            sp.Encode2(cool_time);
        }
        return sp.get();
    }

}
