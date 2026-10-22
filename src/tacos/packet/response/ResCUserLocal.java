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

    // CUserLocal::OnSitResult
    public static ServerPacket UserSitResult(int id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSitResult);

        boolean is_cancel = (id == -1);
        sp.Encode1(is_cancel ? 0 : 1);
        if (!is_cancel) {
            sp.Encode2(id); // nChairID
        }

        return sp;
    }

    // CUser::OnEmotion
    public static ServerPacket UserEmotionLocal(TacosCharacter chr, int expression) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEmotionLocal);

        sp.EncodeBuffer(RD_CUser.Emotion(expression));
        return sp;
    }

    // CUser::OnEffect
    public static ServerPacket UserEffectLocal(OpsUserEffect ops) {
        return UserEffectLocal(ops, null);
    }

    // CUser::OnEffect
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
                data.Encode4(pb.skill_id); // nSkillID
                data.Encode1(pb.player.getLevel(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186)); // nCharLevel
                data.Encode1(pb.skill_level); // nSLV
                switch (pb.skill_id) {
                    // Berserk
                    case 1320006: {
                        data.Encode1(pb.skill_on ? 1 : 0);
                        break;
                    }
                    // Monster Magnet
                    case 1121001:
                    case 1221001:
                    case 1321001: {
                        data.Encode1(0); // direction
                        break;
                    }
                    default: {
                        break;
                    }
                }
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
            case UserEffect_Pet: {
                data.Encode1(0); // 0 = level up
                data.Encode1(pb.index); // pet index
                break;
            }
            case UserEffect_SkillSpecial: {
                data.Encode4(pb.skill_id);
                break;
            }
            case UserEffect_ProtectOnDieItemUse: {
                data.Encode1(1); // safety charm
                data.Encode1(pb.count); // remaining count
                data.Encode1(pb.days); // remaining days
                break;
            }
            case UserEffect_IncDecHPEffect: {
                data.Encode1(pb.count); // HP
                break;
            }
            case UserEffect_BuffItemEffect: {
                data.Encode4(pb.skill_id);
                break;
            }
            case UserEffect_LotteryUse: {
                boolean has_path = pb.path != null && !pb.path.isEmpty();
                data.Encode4(pb.item_id);
                data.Encode1(has_path ? 1 : 0);
                if (has_path) {
                    data.EncodeStr(pb.path);
                }
                break;
            }
            case UserEffect_ItemMaker: {
                data.Encode4(pb.maker.get());
                break;
            }
            case UserEffect_ReservedEffect: {
                data.EncodeStr(pb.path);
                break;
            }
            case UserEffect_UpgradeTombItemUse: {
                data.Encode1(pb.count); // remaining count
                break;
            }
            case UserEffect_AvatarOriented: {
                data.EncodeStr(pb.path);
                data.Encode4(1);
                break;
            }
            default: {
                break;
            }
        }

        return data.getBytes();
    }

    // CUserLocal::OnTeleport
    public static ServerPacket UserTeleport(byte portal) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserTeleport);

        sp.Encode1(0); // set last teleported time by client side
        sp.Encode1(portal); // m_aPortal index
        return sp;
    }

    // CUserLocal::OnMesoGive_Succeeded
    public static ServerPacket MesoGive_Succeeded(int mesos) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MesoGive_Succeeded);

        sp.Encode4(mesos);
        return sp;
    }

    // CUserLocal::OnMesoGive_Failed
    public static ServerPacket MesoGive_Failed() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MesoGive_Failed);
        return sp;
    }

    // CUserLocal::OnRandomMesobag_Succeeded
    public static ServerPacket Random_Mesobag_Succeed(byte type, int mesos) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Random_Mesobag_Succeed);

        sp.Encode1(type);
        sp.Encode4(mesos);
        return sp;
    }

    // CUserLocal::OnRandomMesobag_Failed
    public static ServerPacket Random_Mesobag_Failed() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Random_Mesobag_Failed);
        return sp;
    }

    // CUserLocal::OnQuestResult
    public static ServerPacket UserQuestResult(OpsQuest ops, int quest_id, int npc_id, int next_quest_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserQuestResult);

        sp.Encode1(ops.get());
        sp.Encode2(quest_id);
        sp.Encode4(npc_id);
        sp.Encode2(next_quest_id);
        return sp;
    }

    // CUserLocal::OnNotifyHPDecByField
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

        sp.EncodeStr(hint); // msg
        sp.Encode2(width); // width
        sp.Encode2(height); // timeout (sec)
        sp.Encode1(1); // avatar oriented, no position
        return sp;
    }

    // CUserLocal::OnOpenUIWithOption
    public static ServerPacket UserOpenUIWithOption(OpsUI ops, int npc_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserOpenUIWithOption);

        sp.Encode4(ops.get());
        sp.Encode4(npc_id);
        return sp;
    }

    // CUserLocal::OnSetDirectionMode
    public static ServerPacket SetDirectionMode(boolean enable) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetDirectionMode);

        sp.Encode1(enable ? 1 : 0);
        sp.Encode4(enable ? 1 : 0);
        return sp;
    }

    // CUserLocal::OnSetStandAloneMode
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

    // CUserLocal::OnHireTutor
    public static ServerPacket UserHireTutor(boolean summon) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserHireTutor);

        sp.Encode1(summon ? 1 : 0);
        return sp;
    }

    // CUserLocal::OnTutorMsg
    public static ServerPacket UserTutorMsg(int index) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserTutorMsg);

        sp.Encode1(1); // message index
        sp.Encode4(index);
        sp.Encode4(7000); // duration
        return sp;
    }

    // CUserLocal::OnTutorMsg
    public static ServerPacket UserTutorMsg(String message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserTutorMsg);

        sp.Encode1(0); // message text
        sp.EncodeStr(message);
        sp.Encode4(200); // width
        sp.Encode2(0);
        sp.Encode4(10000); // Probably delay
        return sp;
    }

    // CUserLocal::OnIncComboResponse
    public static ServerPacket IncCombo(TacosCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_IncCombo);

        sp.Encode4(chr.getCombo()); // m_nCombo
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

    // CUserLocal::OnNoticeMsg
    public static ServerPacket UserNoticeMsg(String message) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserNoticeMsg);

        sp.EncodeStr(message);
        return sp;
    }

    // CUserLocal::OnChatMsg, チャット欄へのテキスト表示
    public static ServerPacket UserChatMsg(String msg) {
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
        sp.Encode2(11); // chat type
        sp.EncodeStr(msg);
        return sp;
    }

    // CUser::OnPassiveMove
    public static ServerPacket UserPassiveMove(ParseCMovePath move_path) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserPassiveMove);

        sp.EncodeBuffer(move_path.get());
        return sp;
    }

    // CUserLocal::OnFollowCharacterFailed
    public static ServerPacket UserFollowCharacterFailed(int error) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserFollowCharacterFailed);

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
        sp.Encode4(error); // 5 = canceled request.
        sp.Encode4(0); // character id
        return sp;
    }

    // unimplemented, buggy.
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

    // CUserLocal::OnSkillCooltimeSet
    public static ServerPacket SkillCooltimeSet(int skill_id, int cool_time) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SkillCooltimeSet);

        sp.Encode4(skill_id); // nSkillID
        if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104)) {
            sp.Encode4(cool_time);
        } else {
            sp.Encode2(cool_time); // tCoolTime (sec)
        }

        return sp;
    }
}
