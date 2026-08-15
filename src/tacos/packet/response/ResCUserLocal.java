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
import tacos.client.TacosCharacter;
import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsQuest;
import tacos.packet.ops.OpsUI;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.packet.response.data.RD_CUser;

/**
 *
 * @author Riremito
 */
public class ResCUserLocal {

    public static ServerPacket UserSitResult(int id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSitResult);

        boolean is_cancel = (id == -1);
        sp.Encode1(is_cancel ? 0 : 1);
        if (!is_cancel) {
            sp.Encode2(id); // sit
        }

        return sp;
    }

    public static ServerPacket UserEmotionLocal(MapleCharacter chr, int expression) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEmotionLocal);

        sp.EncodeBuffer(RD_CUser.Emotion(expression));
        return sp;
    }

    public static ServerPacket UserEffectLocal(OpsUserEffect ops) {
        return UserEffectLocal(ops, null);
    }

    public static ServerPacket UserEffectLocal(OpsUserEffect ops, PB_UserEffect pb) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectLocal);

        sp.EncodeBuffer(EffectData(ops, pb));
        return sp;
    }

    // CUser::OnEffect
    public static byte[] EffectData(OpsUserEffect ops, PB_UserEffect pb) {
        ServerPacket data = new ServerPacket();

        data.Encode1(ops.get());

        switch (ops) {
            case UserEffect_SkillUse: {
                data.Encode4(pb.skill_id);
                data.Encode1(1);
                data.Encode1(pb.skill_on ? 0 : 1);
                break;
            }
            case UserEffect_SkillAffected: {
                data.Encode4(pb.skill_id);
                data.Encode1(1);
                break;
            }
            case UserEffect_Quest: {
                int count = 1;
                data.Encode1(count); // loop count
                if (0 < count) {
                    data.Encode4(pb.item_id);
                    data.Encode4(pb.item_quantity);
                } else {
                    // this part has never used, wz data does not exist.
                    data.EncodeStr(""); // unk
                    data.Encode4(0); // Effect/Quest.img/num
                }
                break;
            }
            case UserEffect_SkillSpecial: {
                data.Encode4(pb.skill_id);
                break;
            }
            case UserEffect_BuffItemEffect: {
                data.Encode4(pb.skill_id);
                break;
            }
            case UserEffect_ItemMaker: {
                data.Encode4(pb.maker.get());
            }
            default: {
                break;
            }
        }

        return data.getBytes();
    }

    public static ServerPacket showRewardItemAnimation(int itemId, String effect) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectLocal);

        sp.Encode1(15);
        sp.Encode4(itemId);
        sp.Encode1(effect != null && effect.length() > 0 ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            sp.EncodeStr(effect);
        }
        return sp;
    }

    public static ServerPacket showOwnHpHealed(final int amount) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectLocal);

        sp.Encode1(10); //Type
        sp.Encode4(amount);
        return sp;
    }

    public static ServerPacket useWheel(byte charmsleft) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectLocal);

        sp.Encode1(21);
        sp.Encode8(charmsleft);
        return sp;
    }

    public static ServerPacket useCharm(byte charmsleft, byte daysleft) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectLocal);

        sp.Encode1(6);
        sp.Encode1(1);
        sp.Encode1(charmsleft);
        sp.Encode1(daysleft);
        return sp;
    }

    public static ServerPacket ShowWZEffect(final String data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectLocal);

        sp.Encode1(19);
        sp.EncodeStr(data);
        return sp;
    }

    public static ServerPacket showOwnPetLevelUp(final int index) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectLocal);

        sp.Encode1(4);
        sp.Encode1(0);
        sp.Encode4(index); // Pet Index
        return sp;
    }

    public static ServerPacket AranTutInstructionalBalloon(final String data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectLocal);

        sp.Encode1(OpsUserEffect.UserEffect_AvatarOriented.get());
        sp.EncodeStr(data);
        sp.Encode4(1);
        return sp;
    }

    public static ServerPacket UserTeleport(byte portal) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserTeleport);

        sp.Encode1(0); // set last teleported time by client side
        sp.Encode1(portal);
        return sp;
    }

    public static ServerPacket MesoGive_Succeeded(int mesos) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MesoGive_Succeeded);

        sp.Encode4(mesos);
        return sp;
    }

    public static ServerPacket MesoGive_Failed() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MesoGive_Failed);
        return sp;
    }

    public static ServerPacket RandomMesoBagSuccess(byte type, int mesos) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Random_Mesobag_Succeed);
        sp.Encode1(type);
        sp.Encode4(mesos);
        return sp;
    }

    public static ServerPacket RandomMesoBagFailed() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Random_Mesobag_Failed);
        return sp;
    }

    public static ServerPacket UserQuestResult(int quest, int npc, int nextquest) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserQuestResult);

        sp.Encode1(OpsQuest.QuestRes_Act_Success.get());
        sp.Encode2(quest);
        sp.Encode4(npc);
        sp.Encode2(nextquest);
        return sp;
    }

    public static ServerPacket UserQuestResult(MapleCharacter c, int quest, int npc, OpsQuest oq) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserQuestResult);

        sp.Encode1(oq.get());
        sp.Encode2(quest);
        sp.Encode4(npc);
        sp.Encode2(0);
        return sp;
    }

    public static ServerPacket NotifyHPDecByField(int nDamage) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_NotifyHPDecByField);

        sp.Encode4(nDamage);
        return sp;
    }

    // CUserLocal::OnBalloonMsg
    public static ServerPacket UserBalloonMsg(String hint, int width, int height) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserBalloonMsg);

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

        return sp;
    }

    public static ServerPacket UserOpenUIWithOption(OpsUI ops, int npc_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserOpenUIWithOption);

        sp.Encode4(ops.get());
        sp.Encode4(npc_id);
        return sp;
    }

    public static ServerPacket SetDirectionMode(boolean enable) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetDirectionMode);

        sp.Encode1(enable ? 1 : 0);
        sp.Encode4(enable ? 1 : 0);
        return sp;
    }

    public static ServerPacket SetStandAloneMode(boolean enable) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetStandAloneMode);

        sp.Encode1(enable ? 1 : 0);
        return sp;
    }

    // JMS164 only. removed in JMS165. KOC Creation UI Test version.
    public static ServerPacket KOC_UI_Response(int error_code) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_164_KOC_UI_Response);

        /*
            0  : 騎士団キャラクターが作成されました。\r\nゲーム終了後再接続すると\r\n騎士団キャラクターが選択できます。
            1  : 既に同名のキャラクターが存在しています。
            2  : インベントリに空きがありません。ポイントショップにてインベントリ拡張アイテムを購入して下さい。
            3  : この名前は使用できません。
            -1 : 原因不明のエラーで騎士団キャラクターの作成に失敗しました。
         */
        sp.Encode4(error_code);
        return sp;
    }

    public static ServerPacket UserHireTutor(boolean summon) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserHireTutor);

        sp.Encode1(summon ? 1 : 0);
        return sp;
    }

    public static ServerPacket UserTutorMsg(int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserTutorMsg);

        sp.Encode1(1);
        sp.Encode4(type);
        sp.Encode4(7000); // probably the delay
        return sp;
    }

    public static ServerPacket UserTutorMsg(String message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserTutorMsg);

        sp.Encode1(0);
        sp.EncodeStr(message);
        sp.Encode4(200); // IDK
        sp.Encode2(0);
        sp.Encode4(10000); // Probably delay
        return sp;
    }

    public static ServerPacket IncCombo(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_IncCombo);

        sp.Encode4(chr.getCombo());
        return sp;
    }

    // ポイントアイテムのパチンコ玉の充填 (玉ボックス)
    public static ServerPacket PachinkoBoxSuccess(int gain) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_Pachinko_BoxSuccess);
        sp.Encode4(gain); // パチンコ玉の数
        return sp;
    }

    // パチンコ玉の充填に失敗した場合のダイアログ (実質不要)
    public static ServerPacket PachinkoBoxFailure() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_Pachinko_BoxFailure);
        return sp;
    }

    public static ServerPacket UserNoticeMsg(String message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserNoticeMsg);

        sp.EncodeStr(message);
        return sp;
    }

    // チャット欄へのテキスト表示
    public static ServerPacket UserChatMsg(final String msg) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserChatMsg);

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
        return sp;
    }

    public static ServerPacket UserPassiveMove(ParseCMovePath move_path) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserPassiveMove);

        sp.EncodeBuffer(move_path.get());
        return sp;
    }

    public static ServerPacket UserFollowCharacterFailed(int error) {
        final ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserFollowCharacterFailed);

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
        sp.Encode8(error); //5 = canceled request.
        return sp;
    }

    public static ServerPacket PollQuestion(String questions[], String answers[][]) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_JMS_Poll_Question);

        sp.Encode4(questions.length); // number of questions, this may support only 1 question...
        for (int i = 0; i < questions.length; i++) {
            sp.Encode4(i + 1); // unused
            sp.EncodeStr(questions[i]);
            sp.Encode4(answers[i].length);
            for (int j = 0; j < answers[i].length; j++) {
                sp.EncodeStr(answers[i][j]);
            }
        }
        return sp;
    }

    public static ServerPacket SkillCooltimeSet(int skill_id, int cool_time) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SkillCooltimeSet);

        sp.Encode4(skill_id);

        if (Config.GreaterOrEqual(Region.JMS, 302) | Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104)) {
            sp.Encode4(cool_time);
        } else {
            sp.Encode2(cool_time);
        }

        return sp;
    }
}
