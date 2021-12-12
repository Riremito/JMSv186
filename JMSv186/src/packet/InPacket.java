// サーバー側から送信されるパケットのヘッダの定義
package packet;

import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import java.util.ArrayList;

public class InPacket {

    // Encoder
    private ArrayList<Byte> packet = new ArrayList<>();
    private int encoded = 0;

    public InPacket(Header header) {
        short w = (short) header.Get();

        packet.add((byte) (w & 0xFF));
        packet.add((byte) ((w >> 8) & 0xFF));
        encoded += 2;
    }

    public InPacket(short w) {
        packet.add((byte) (w & 0xFF));
        packet.add((byte) ((w >> 8) & 0xFF));
        encoded += 2;
    }

    public void Encode1(byte b) {
        packet.add(b);
        encoded += 1;
    }

    public void Encode2(short w) {
        Encode1((byte) (w & 0xFF));
        Encode1((byte) ((w >> 8) & 0xFF));
    }

    public void Encode4(int dw) {
        Encode2((short) (dw & 0xFFFF));
        Encode2((short) ((dw >> 16) & 0xFFFF));
    }

    public void Encode8(long qw) {
        Encode4((int) (qw & 0xFFFFFFFF));
        Encode4((int) ((qw >> 32) & 0xFFFFFFFF));
    }

    public void EncodeStr(String str) {
        byte[] b = str.getBytes();
        Encode2((short) b.length);

        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
    }

    public void EncodeBuffer(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
    }

    public String Packet() {
        byte[] b = new byte[encoded];
        for (int i = 0; i < encoded; i++) {
            b[i] = packet.get(i);
        }

        short header = (short) (((short) b[0] & 0xFF) | ((short) b[1] & 0xFF << 8));
        String text = String.format("@%04X", header);

        for (int i = 2; i < encoded; i++) {
            text += String.format(" %02X", b[i]);
        }

        return text;
    }

    public MaplePacket Get() {
        byte[] b = new byte[encoded];
        for (int i = 0; i < encoded; i++) {
            b[i] = packet.get(i);
        }
        return new ByteArrayMaplePacket(b);
    }

    public enum Header {
        // added
        MINIGAME_PACHINKO_UPDATE_TAMA,
        UNKNOWN_RELOAD_MINIMAP,
        UNKNOWN_RELOAD_MAP,
        // unknown
        RELOG_RESPONSE,
        BBS_OPERATION,
        SERVERSTATUS,
        XMAS_SURPRISE,
        DUEY,
        EARN_TITLE_MSG,
        // test
        PING,
        LOGIN_STATUS,
        SERVERLIST,
        CHARLIST,
        SERVER_IP,
        CHAR_NAME_RESPONSE,
        ADD_NEW_CHAR_ENTRY,
        DELETE_CHAR_RESPONSE,
        CHANGE_CHANNEL,
        SECONDPW_ERROR,
        LOGIN_AUTH,
        MODIFY_INVENTORY_ITEM,
        UPDATE_INVENTORY_SLOT,
        UPDATE_STATS,
        GIVE_BUFF,
        CANCEL_BUFF,
        TEMP_STATS,
        TEMP_STATS_RESET,
        UPDATE_SKILLS,
        FAME_RESPONSE,
        SHOW_STATUS_INFO,
        SHOW_NOTES,
        TROCK_LOCATIONS,
        UPDATE_MOUNT,
        SHOW_QUEST_COMPLETION,
        SEND_TITLE_BOX,
        USE_SKILL_BOOK,
        FINISH_SORT,
        FINISH_GATHER,
        CHAR_INFO,
        PARTY_OPERATION,
        EXPEDITION_OPERATION,
        BUDDYLIST,
        GUILD_OPERATION,
        ALLIANCE_OPERATION,
        SPAWN_PORTAL,
        SERVERMESSAGE,
        PIGMI_REWARD,
        OWL_OF_MINERVA,
        ENGAGE_REQUEST,
        ENGAGE_RESULT,
        YELLOW_CHAT,
        FISHING_BOARD_UPDATE,
        PLAYER_NPC,
        MONSTERBOOK_ADD,
        MONSTERBOOK_CHANGE_COVER,
        AVATAR_MEGA,
        ENERGY,
        GHOST_POINT,
        GHOST_STATUS,
        FAIRY_PEND_MSG,
        SEND_PEDIGREE,
        OPEN_FAMILY,
        FAMILY_MESSAGE,
        FAMILY_INVITE,
        FAMILY_JUNIOR,
        SENIOR_MESSAGE,
        FAMILY,
        REP_INCREASE,
        FAMILY_LOGGEDIN,
        FAMILY_BUFF,
        FAMILY_USE_REQUEST,
        LEVEL_UPDATE,
        MARRIAGE_UPDATE,
        JOB_UPDATE,
        FOLLOW_REQUEST,
        TOP_MSG,
        SKILL_MACRO,
        WARP_TO_MAP,
        MTS_OPEN,
        CS_OPEN,
        SERVER_BLOCKED,
        SHOW_EQUIP_EFFECT,
        MULTICHAT,
        WHISPER,
        BOSS_ENV,
        MOVE_ENV,
        UPDATE_ENV,
        MAP_EFFECT,
        CASH_SONG,
        GM_EFFECT,
        OX_QUIZ,
        GMEVENT_INSTRUCTIONS,
        CLOCK,
        BOAT_EFF,
        BOAT_EFFECT,
        STOP_CLOCK,
        PYRAMID_UPDATE,
        PYRAMID_RESULT,
        MOVE_PLATFORM,
        SPAWN_PLAYER,
        REMOVE_PLAYER_FROM_MAP,
        CHATTEXT,
        CHATTEXT1,
        CHALKBOARD,
        UPDATE_CHAR_BOX,
        SHOW_SCROLL_EFFECT,
        SHOW_POTENTIAL_RESET,
        SHOW_POTENTIAL_EFFECT,
        FISHING_CAUGHT,
        PAMS_SONG,
        FOLLOW_EFFECT,
        SPAWN_PET,
        MOVE_PET,
        PET_CHAT,
        PET_NAMECHANGE,
        PET_COMMAND,
        SPAWN_SUMMON,
        REMOVE_SUMMON,
        MOVE_SUMMON,
        SUMMON_ATTACK,
        SUMMON_SKILL,
        DAMAGE_SUMMON,
        DRAGON_SPAWN,
        DRAGON_MOVE,
        DRAGON_REMOVE,
        MOVE_PLAYER,
        CLOSE_RANGE_ATTACK,
        RANGED_ATTACK,
        MAGIC_ATTACK,
        ENERGY_ATTACK,
        SKILL_EFFECT,
        CANCEL_SKILL_EFFECT,
        DAMAGE_PLAYER,
        FACIAL_EXPRESSION,
        SHOW_ITEM_EFFECT,
        SHOW_CHAIR,
        UPDATE_CHAR_LOOK,
        SHOW_FOREIGN_EFFECT,
        GIVE_FOREIGN_BUFF,
        CANCEL_FOREIGN_BUFF,
        UPDATE_PARTYMEMBER_HP,
        LOAD_GUILD_NAME,
        LOAD_GUILD_ICON,
        CANCEL_CHAIR,
        SHOW_ITEM_GAIN_INCHAT,
        CURRENT_MAP_WARP,
        MESOBAG_SUCCESS,
        MESOBAG_FAILURE,
        RANDOM_MESOBAG_SUCCESS,
        RANDOM_MESOBAG_FAILURE,
        UPDATE_QUEST_INFO,
        PLAYER_HINT,
        REPAIR_WINDOW,
        CYGNUS_INTRO_LOCK,
        CYGNUS_INTRO_DISABLE_UI,
        SUMMON_HINT,
        SUMMON_HINT_MSG,
        ARAN_COMBO,
        TAMA_BOX_SUCCESS,
        TAMA_BOX_FAILURE,
        GAME_POLL_REPLY,
        FOLLOW_MESSAGE,
        FOLLOW_MOVE,
        FOLLOW_MSG,
        GAME_POLL_QUESTION,
        COOLDOWN,
        SPAWN_MONSTER,
        KILL_MONSTER,
        SPAWN_MONSTER_CONTROL,
        MOVE_MONSTER,
        MOVE_MONSTER_RESPONSE,
        APPLY_MONSTER_STATUS,
        CANCEL_MONSTER_STATUS,
        MOB_TO_MOB_DAMAGE,
        DAMAGE_MONSTER,
        SHOW_MONSTER_HP,
        SHOW_MAGNET,
        CATCH_MONSTER,
        MOB_SPEAKING,
        MONSTER_PROPERTIES,
        REMOVE_TALK_MONSTER,
        TALK_MONSTER,
        SPAWN_NPC,
        REMOVE_NPC,
        SPAWN_NPC_REQUEST_CONTROLLER,
        NPC_ACTION,
        SPAWN_HIRED_MERCHANT,
        DESTROY_HIRED_MERCHANT,
        UPDATE_HIRED_MERCHANT,
        DROP_ITEM_FROM_MAPOBJECT,
        REMOVE_ITEM_FROM_MAP,
        KITE_MESSAGE,
        SPAWN_KITE,
        REMOVE_KITE,
        SPAWN_MIST,
        REMOVE_MIST,
        SPAWN_DOOR,
        REMOVE_DOOR,
        REACTOR_HIT,
        REACTOR_SPAWN,
        REACTOR_DESTROY,
        ROLL_SNOWBALL,
        HIT_SNOWBALL,
        SNOWBALL_MESSAGE,
        LEFT_KNOCK_BACK,
        HIT_COCONUT,
        COCONUT_SCORE,
        MONSTER_CARNIVAL_START,
        MONSTER_CARNIVAL_OBTAINED_CP,
        MONSTER_CARNIVAL_PARTY_CP,
        MONSTER_CARNIVAL_SUMMON,
        MONSTER_CARNIVAL_DIED,
        CHAOS_HORNTAIL_SHRINE,
        CHAOS_ZAKUM_SHRINE,
        HORNTAIL_SHRINE,
        ZAKUM_SHRINE,
        NPC_TALK,
        OPEN_NPC_SHOP,
        CONFIRM_SHOP_TRANSACTION,
        OPEN_STORAGE,
        MERCH_ITEM_MSG,
        MERCH_ITEM_STORE,
        RPS_GAME,
        MESSENGER,
        PLAYER_INTERACTION,
        CS_UPDATE,
        CS_OPERATION,
        KEYMAP,
        PET_AUTO_HP,
        PET_AUTO_MP,
        GET_MTS_TOKENS,
        MTS_OPERATION,
        BLOCK_PORTAL,
        ARIANT_SCOREBOARD,
        ARIANT_THING,
        ARIANT_PQ_START,
        VICIOUS_HAMMER,
        VEGA_SCROLL,
        REPORT_PLAYER_MSG,
        NPC_CONFIRM,
        UPDATE_BEANS,
        TIP_BEANS,
        OPEN_BEANS,
        SHOOT_BEANS;

        private int value;

        Header(int header) {
            value = header;
        }

        Header() {
            value = 0xFFFF;
        }

        private boolean Set(int header) {
            value = header;
            return true;
        }

        public int Get() {
            return value;
        }
    }

    public static void SetForJMSv164() {
        Header.LOGIN_STATUS.Set(0x0000);
        Header.SERVERLIST.Set(0x0002);
        Header.CHARLIST.Set(0x0003);
        Header.SERVER_IP.Set(0x0004);
        Header.CHAR_NAME_RESPONSE.Set(0x0005);
        Header.ADD_NEW_CHAR_ENTRY.Set(0x0006);
        Header.DELETE_CHAR_RESPONSE.Set(0x0007);
        Header.LOGIN_AUTH.Set(0x0018);
        Header.WARP_TO_MAP.Set(0x0067);
    }

    public static void SetForJMSv184() {
        Header.LOGIN_STATUS.Set(0x0000);
        Header.SERVERLIST.Set(0x0002);
        Header.CHARLIST.Set(0x0003);
        Header.SERVER_IP.Set(0x0004);
        Header.CHAR_NAME_RESPONSE.Set(0x0005);
        Header.ADD_NEW_CHAR_ENTRY.Set(0x0006);
        Header.DELETE_CHAR_RESPONSE.Set(0x0007);
        Header.CHANGE_CHANNEL.Set(0x0008);
        Header.PING.Set(0x0009);
        Header.LOGIN_AUTH.Set(0x0018);

        Header.WARP_TO_MAP.Set(0x007B);

        Header.SPAWN_NPC.Set(0xFFFF);
        Header.FAMILY.Set(0xFFFF);
        Header.KEYMAP.Set(0xFFFF);
        Header.SERVERMESSAGE.Set(0xFFFF);
        Header.OPEN_FAMILY.Set(0xFFFF);
        Header.CS_OPEN.Set(0xFFFF);
    }

    // JMS v186.1 ProcessPacket
    public static void SetForJMSv186() {
        Header.LOGIN_STATUS.Set(0x0000);
        // 0x0001 SERVERSTATUS?
        Header.SERVERLIST.Set(0x0002);
        Header.CHARLIST.Set(0x0003);
        Header.SERVER_IP.Set(0x0004);
        Header.CHAR_NAME_RESPONSE.Set(0x0005);
        Header.ADD_NEW_CHAR_ENTRY.Set(0x0006);
        Header.DELETE_CHAR_RESPONSE.Set(0x0007);
        Header.CHANGE_CHANNEL.Set(0x0008);
        Header.PING.Set(0x0009);
        // 0x000A
        // 0x000B
        // 0x000C
        // 0x000D CHANNEL_SELECTED?
        // 0x000E @000E ..., @0011 00 00 を送信
        // 0x000F 未使用
        // 0x0010 未使用
        // 0x0011 未使用
        // 0x0012
        // 0x0013
        // 0x0014
        // 0x0015 @0015 [00], 不法プログラムまたは悪性コードが感知されたためゲームを強制終了します。
        Header.SECONDPW_ERROR.Set(0x0016);
        // 0x0017
        Header.LOGIN_AUTH.Set(0x0018);
        // 0x0019 未使用
        // 0x001A 未使用
        Header.MODIFY_INVENTORY_ITEM.Set(0x001B);
        Header.UPDATE_INVENTORY_SLOT.Set(0x001C);
        Header.UPDATE_STATS.Set(0x001D);
        Header.GIVE_BUFF.Set(0x001E);
        Header.CANCEL_BUFF.Set(0x001F);
        Header.TEMP_STATS.Set(0x0020);
        Header.TEMP_STATS_RESET.Set(0x0021);
        Header.UPDATE_SKILLS.Set(0x0022);
        // 0x0023
        Header.FAME_RESPONSE.Set(0x0024);
        Header.SHOW_STATUS_INFO.Set(0x0025);
        Header.SHOW_NOTES.Set(0x0026);
        Header.TROCK_LOCATIONS.Set(0x0027);
        // 0x0028 未使用
        // 0x0029
        // 0x002A @002A [02-03, 41-47]..., 通報後のダイアログ通知
        // 0x002B
        // 0x002C
        Header.UPDATE_MOUNT.Set(0x002D);
        Header.SHOW_QUEST_COMPLETION.Set(0x002E);
        Header.SEND_TITLE_BOX.Set(0x002F);
        Header.USE_SKILL_BOOK.Set(0x0030);
        Header.FINISH_SORT.Set(0x0031);
        Header.FINISH_GATHER.Set(0x0032);
        // 0x0033 未使用
        // 0x0034 未使用
        Header.CHAR_INFO.Set(0x0035);
        Header.PARTY_OPERATION.Set(0x0036);
        // 0x0037 未使用
        Header.EXPEDITION_OPERATION.Set(0x0038);
        Header.BUDDYLIST.Set(0x0039);
        // 0x003A 未使用
        Header.GUILD_OPERATION.Set(0x003B);
        Header.ALLIANCE_OPERATION.Set(0x003C);
        Header.SPAWN_PORTAL.Set(0x003D);
        // 0x003E
        Header.SERVERMESSAGE.Set(0x003F);
        Header.PIGMI_REWARD.Set(0x0040);
        Header.OWL_OF_MINERVA.Set(0x0041);
        // 0x0042
        Header.ENGAGE_REQUEST.Set(0x0043);
        Header.ENGAGE_RESULT.Set(0x0044);
        // 0x0045 @0045 [09], ウェディング登録? @0091が送信される
        // 0x0046 @0046 int,int, MTSか?
        // 0x0047 @0047 [01]..., 現在ペットはこのえさが食べることができません。もう一度確認してください。
        Header.YELLOW_CHAT.Set(0x0048);
        // 0x0049
        // 0x004A @004A ..., 当該モンスターの体力が強くてできません。
        // 0x004B 未使用
        Header.MINIGAME_PACHINKO_UPDATE_TAMA.Set(0x4C);
        // 0x004D パチンコ景品受け取りUI
        // 0x004E @004E int,int, パチンコ球をx子プレゼントします。というダイアログ誤字っているのでたぶん未実装的な奴
        // 0x004F @004F [01 or 03], プレゼントの通知
        // 0x0050 @0050 strig, string..., 相性占い結果UI
        Header.FISHING_BOARD_UPDATE.Set(0x0051);
        // 0x0052 @0052 String, 任意メッセージをダイアログに表示
        // 0x0053 @0053 [01 (00, 02は謎)], ワールド変更申請のキャンセル
        // 0x0054 @0054 int, プレイタイム終了まで残りx分x秒です。
        // 0x0055 @0055 byte, なんも処理がされない関数
        Header.PLAYER_NPC.Set(0x0056);
        Header.MONSTERBOOK_ADD.Set(0x0057);
        Header.MONSTERBOOK_CHANGE_COVER.Set(0x0058);
        // 0x0059 BBS_OPERATION?
        // 0x005A @005A String, 任意メッセージをダイアログに表示
        Header.AVATAR_MEGA.Set(0x005B);
        // 0x005C
        // 0x005D
        Header.UNKNOWN_RELOAD_MINIMAP.Set(0x005E);
        // 0x005F
        // 0x0060
        // 0x0061
        Header.ENERGY.Set(0x0062);
        Header.GHOST_POINT.Set(0x0063);
        Header.GHOST_STATUS.Set(0x0064);
        Header.FAIRY_PEND_MSG.Set(0x0065);
        Header.SEND_PEDIGREE.Set(0x0066);
        Header.OPEN_FAMILY.Set(0x0067);
        Header.FAMILY_MESSAGE.Set(0x0068);
        Header.FAMILY_INVITE.Set(0x0069);
        Header.FAMILY_JUNIOR.Set(0x006A);
        Header.SENIOR_MESSAGE.Set(0x006B);
        Header.FAMILY.Set(0x006C);
        Header.REP_INCREASE.Set(0x006D);
        Header.FAMILY_LOGGEDIN.Set(0x006E);
        Header.FAMILY_BUFF.Set(0x006F);
        Header.FAMILY_USE_REQUEST.Set(0x0070);
        Header.LEVEL_UPDATE.Set(0x0071);
        Header.MARRIAGE_UPDATE.Set(0x0072);
        Header.JOB_UPDATE.Set(0x0073);
        // 0x0074
        Header.FOLLOW_REQUEST.Set(0x0075); // @0076を送信
        // 0x0076 @0076 [不明], マジェスティックボックスの中身獲得後のUI
        Header.TOP_MSG.Set(0x0077);
        // 0x0078 @0078 string, イベンドガイドのNPC会話で任意文字列を表示
        // 0x0079 @0079 [0x02 or], イベンドガイドのNPC会話のエラーメッセージの呼び出し
        // 0x007A
        // 0x007B @007B int,string, 灰色のメッセージ
        // 0x007C @007C, ファムの歌を利用するか選択するUI, @00C2 [00or01]が送信される01は使用フラグ
        Header.SKILL_MACRO.Set(0x007D);
        Header.WARP_TO_MAP.Set(0x007E);
        Header.MTS_OPEN.Set(0x007F);
        Header.CS_OPEN.Set(0x0080);
        // 0x0081
        // 0x0082
        Header.UNKNOWN_RELOAD_MAP.Set(0x0083);
        // 0x0084 @0084 [01-07], マップ移動時のエラーメッセージ (テレポストーン?)
        Header.SERVER_BLOCKED.Set(0x0085);
        Header.SHOW_EQUIP_EFFECT.Set(0x0086);
        Header.MULTICHAT.Set(0x0087);
        Header.WHISPER.Set(0x0088);
        Header.BLOCK_PORTAL.Set(0x0089);
        Header.BOSS_ENV.Set(0x008A);
        Header.MOVE_ENV.Set(0x008B);
        Header.UPDATE_ENV.Set(0x008C);
        // 0x008D
        Header.MAP_EFFECT.Set(0x008E);
        Header.CASH_SONG.Set(0x008F);
        Header.GM_EFFECT.Set(0x0090);
        Header.OX_QUIZ.Set(0x0091);
        Header.GMEVENT_INSTRUCTIONS.Set(0x0092);
        //Header.CLOCK.Set(0x0093);
        //Header.BOAT_EFF.Set(0x0094);
        //Header.BOAT_EFFECT.Set(0x0095);
        // 0x0096
        // 0x0097
        // 0x0098
        Header.STOP_CLOCK.Set(0x0099);
        // 0x009A 未使用
        // 0x009B
        //Header.PYRAMID_UPDATE.Set(0x009C);
        //Header.PYRAMID_RESULT.Set(0x009D);
        // 0x009E
        Header.MOVE_PLATFORM.Set(0x009F);
        // 0x00A0 0x00F2を送信
        Header.SPAWN_PLAYER.Set(0x00A1);
        Header.REMOVE_PLAYER_FROM_MAP.Set(0x00A2);
        Header.CHATTEXT.Set(0x00A3);
        Header.CHATTEXT1.Set(0x00A4);
        Header.CHALKBOARD.Set(0x00A5);
        Header.UPDATE_CHAR_BOX.Set(0x00A6);
        // 0x00A7
        Header.SHOW_SCROLL_EFFECT.Set(0x00A8);
        // 0x00A9
        // 0x00AA
        Header.SHOW_POTENTIAL_RESET.Set(0x00AB);
        Header.SHOW_POTENTIAL_EFFECT.Set(0x00AC);
        // 0x00AD Damage Effect
        // 0x00AE
        // 0x00AF
        // 0x00B0
        Header.FOLLOW_EFFECT.Set(0x00B1);
        Header.FISHING_CAUGHT.Set(0x00B2);
        Header.PAMS_SONG.Set(0x00B3);
        Header.SPAWN_PET.Set(0x00B4);
        // 0x00B5 未使用
        // 0x00B6 未使用
        Header.MOVE_PET.Set(0x00B7);
        Header.PET_CHAT.Set(0x00B8);
        Header.PET_NAMECHANGE.Set(0x00B9);
        // 0x00BA
        Header.PET_COMMAND.Set(0x00BB);
        //Header.SPAWN_SUMMON.Set(0x00BC);
        //Header.REMOVE_SUMMON.Set(0x00BD);
        //Header.MOVE_SUMMON.Set(0x00BE);
        //Header.SUMMON_ATTACK.Set(0x00BF);
        Header.SUMMON_SKILL.Set(0x00C0);
        Header.DAMAGE_SUMMON.Set(0x00C1);
        Header.DRAGON_SPAWN.Set(0x00C2);
        Header.DRAGON_MOVE.Set(0x00C3);
        Header.DRAGON_REMOVE.Set(0x00C4);
        // 0x00C5 未使用
        Header.MOVE_PLAYER.Set(0x00C6);
        Header.CLOSE_RANGE_ATTACK.Set(0x00C7);
        Header.RANGED_ATTACK.Set(0x00C8);
        Header.MAGIC_ATTACK.Set(0x00C9);
        Header.ENERGY_ATTACK.Set(0x00CA);
        Header.SKILL_EFFECT.Set(0x00CB);
        Header.CANCEL_SKILL_EFFECT.Set(0x00CC);
        Header.DAMAGE_PLAYER.Set(0x00CD);
        Header.FACIAL_EXPRESSION.Set(0x00CE);
        Header.SHOW_ITEM_EFFECT.Set(0x00CF);
        // 0x00D0
        //Header.SHOW_CHAIR.Set(0x00D1);
        Header.UPDATE_CHAR_LOOK.Set(0x00D2);
        Header.SHOW_FOREIGN_EFFECT.Set(0x00D3);
        Header.GIVE_FOREIGN_BUFF.Set(0x00D4);
        Header.CANCEL_FOREIGN_BUFF.Set(0x00D5);
        Header.UPDATE_PARTYMEMBER_HP.Set(0x00D6);
        Header.LOAD_GUILD_NAME.Set(0x00D7);
        Header.LOAD_GUILD_ICON.Set(0x00D8);
        // 0x00D9
        Header.CANCEL_CHAIR.Set(0x00DA);
        // 0x00DB 0x00CEと同一
        Header.SHOW_ITEM_GAIN_INCHAT.Set(0x00DC); // 0x00D3と同一
        Header.CURRENT_MAP_WARP.Set(0x00DD);
        // 0x00DE 未使用
        Header.MESOBAG_SUCCESS.Set(0x00DF);
        Header.MESOBAG_FAILURE.Set(0x00E0);
        Header.RANDOM_MESOBAG_SUCCESS.Set(0x00E1);
        Header.RANDOM_MESOBAG_FAILURE.Set(0x00E2);
        Header.UPDATE_QUEST_INFO.Set(0x00E3);
        // 0x00E4
        // 0x00E5
        // 0x00E6 @00E6 "文字列",short1,short0,byte0, 不明
        // 0x00E7
        Header.PLAYER_HINT.Set(0x00E8);
        // 0x00E9 パケットの構造が複雑, メルをなくしました。(-xxxx)と表示される
        // 0x00EA @00EA, COUNSELのUI, @010Bが送信される
        // 0x00EB @00EB, クラス対抗戦UI
        // 0x00EC @00EC [], 強制的にUIを開く
        Header.REPAIR_WINDOW.Set(0x00ED);
        Header.CYGNUS_INTRO_LOCK.Set(0x00EE);
        Header.CYGNUS_INTRO_DISABLE_UI.Set(0x00EF);
        Header.SUMMON_HINT.Set(0x00F0);
        Header.SUMMON_HINT_MSG.Set(0x00F1);
        Header.ARAN_COMBO.Set(0x00F2);
        Header.TAMA_BOX_SUCCESS.Set(0x00F3);
        Header.TAMA_BOX_FAILURE.Set(0x00F4);
        // 0x00F5
        // 0x00F6
        // 0x00F7
        // 0x00F8
        // 0x00F9 @00F9, アラン4次スキルの説明UI
        Header.GAME_POLL_REPLY.Set(0x00FA); // OK
        Header.FOLLOW_MESSAGE.Set(0x00FB); // OK
        // 0x00FC
        // 0x00FD @00FD 時間, 謎のタイマー出現
        // 0x00FE @00FE int,int,int,int吹っ飛び判定,intダメージ量,..., 攻撃, 被ダメ, KB動作
        Header.FOLLOW_MOVE.Set(0x00FF);
        Header.FOLLOW_MSG.Set(0x0100); // OK
        Header.GAME_POLL_QUESTION.Set(0x0101);
        Header.COOLDOWN.Set(0x0102);
        // 0x0103 未使用
        Header.SPAWN_MONSTER.Set(0x0104);
        Header.KILL_MONSTER.Set(0x0105);
        Header.SPAWN_MONSTER_CONTROL.Set(0x0106);
        Header.MOVE_MONSTER.Set(0x0107);
        Header.MOVE_MONSTER_RESPONSE.Set(0x0108);
        // 0x0109 未使用
        Header.APPLY_MONSTER_STATUS.Set(0x010A);
        Header.CANCEL_MONSTER_STATUS.Set(0x010B);
        // 0x010C メモリ開放系の処理かもしれない
        Header.MOB_TO_MOB_DAMAGE.Set(0x010D);
        Header.DAMAGE_MONSTER.Set(0x010E);
        // 0x010F
        // 0x0110 未使用
        // 0x0111 @0111 int, @00A2を送信
        Header.SHOW_MONSTER_HP.Set(0x0112);
        Header.SHOW_MAGNET.Set(0x0113);
        Header.CATCH_MONSTER.Set(0x0114);
        Header.MOB_SPEAKING.Set(0x0115);
        // 0x0116 @0116 int,int,int,int, 何らかの変数が更新されるが詳細不明
        Header.MONSTER_PROPERTIES.Set(0x0117);
        Header.REMOVE_TALK_MONSTER.Set(0x0118);
        Header.TALK_MONSTER.Set(0x0119);
        // 0x011A
        // 0x011B
        // 0x011C
        // 0x011D 未使用
        Header.SPAWN_NPC.Set(0x011E);
        Header.REMOVE_NPC.Set(0x011F);
        Header.SPAWN_NPC_REQUEST_CONTROLLER.Set(0x0120);
        Header.NPC_ACTION.Set(0x0121);
        // 0x0122
        // 0x0123
        // 0x0124
        // 0x0125 未使用
        Header.SPAWN_HIRED_MERCHANT.Set(0x0126);
        Header.DESTROY_HIRED_MERCHANT.Set(0x0127);
        Header.UPDATE_HIRED_MERCHANT.Set(0x0128);
        Header.DROP_ITEM_FROM_MAPOBJECT.Set(0x0129);
        Header.REMOVE_ITEM_FROM_MAP.Set(0x012A);
        Header.KITE_MESSAGE.Set(0x012B);
        Header.SPAWN_KITE.Set(0x012C);
        Header.REMOVE_KITE.Set(0x012D);
        Header.SPAWN_MIST.Set(0x012E);
        Header.REMOVE_MIST.Set(0x012F);
        Header.SPAWN_DOOR.Set(0x0130);
        Header.REMOVE_DOOR.Set(0x0131);
        // 0x0132
        // 0x0133
        // 0x0134 crash
        // 0x0135 ポータルを開けませんでした。
        // 0x0136
        Header.REACTOR_HIT.Set(0x0137);
        // 0x0138 未使用
        Header.REACTOR_SPAWN.Set(0x0139);
        Header.REACTOR_DESTROY.Set(0x013A);
        //Header.ROLL_SNOWBALL.Set(0x013B);
        //Header.HIT_SNOWBALL.Set(0x013C);
        //Header.SNOWBALL_MESSAGE.Set(0x013D);
        //Header.LEFT_KNOCK_BACK.Set(0x013E);
        //Header.HIT_COCONUT.Set(0x013F);
        //Header.COCONUT_SCORE.Set(0x0140);
        // 0x0141 未使用
        // 0x0142 未使用
        //Header.MONSTER_CARNIVAL_START.Set(0x0143);
        //Header.MONSTER_CARNIVAL_OBTAINED_CP.Set(0x0144);
        //Header.MONSTER_CARNIVAL_PARTY_CP.Set(0x0145);
        //Header.MONSTER_CARNIVAL_SUMMON.Set(0x0146);
        // 0x0147 未使用
        //Header.MONSTER_CARNIVAL_DIED.Set(0x0148);
        // 0x0149 未使用
        // 0x014A 未使用
        // 0x014B 未使用
        // 0x014C 未使用
        // 0x014D 未使用
        // 0x014E 未使用
        // 0x014F @014F byte種類,int残り時間, マップ退場メッセージ
        // 0x0150 未使用
        Header.CHAOS_HORNTAIL_SHRINE.Set(0x0151); // OK
        Header.CHAOS_ZAKUM_SHRINE.Set(0x0152); // OK
        //Header.HORNTAIL_SHRINE.Set(0x0153);
        Header.ZAKUM_SHRINE.Set(0x0154); // OK
        Header.NPC_TALK.Set(0x0155);
        Header.OPEN_NPC_SHOP.Set(0x0156);
        Header.CONFIRM_SHOP_TRANSACTION.Set(0x0157);
        // 0x0158
        // 0x0159
        Header.OPEN_STORAGE.Set(0x015A);
        Header.MERCH_ITEM_MSG.Set(0x015B);
        Header.MERCH_ITEM_STORE.Set(0x015C);
        Header.RPS_GAME.Set(0x015D);
        Header.MESSENGER.Set(0x015E);
        Header.PLAYER_INTERACTION.Set(0x015F);
        // 0x0160 未使用
        // 0x0161 未使用
        // 0x0162 未使用
        // 0x0163 未使用
        // 0x0164 未使用
        // 0x0165 未使用
        // 0x0166 未使用
        Header.TIP_BEANS.Set(0x0167);
        Header.OPEN_BEANS.Set(0x0168);
        Header.SHOOT_BEANS.Set(0x0169);
        // 0x016A
        Header.UPDATE_BEANS.Set(0x016B);
        Header.DUEY.Set(0x016C); // OK
        // 0x016D
        // 0x016E
        Header.CS_UPDATE.Set(0x016F);
        Header.CS_OPERATION.Set(0x0170);
        // 0x0171
        // 0x0172
        // 0x0173
        // 0x0174
        // 0x0175
        // 0x0176
        // 0x0177
        // 0x0178
        // 0x0179
        // 0x017A
        // 0x017B
        Header.KEYMAP.Set(0x017C);
        Header.PET_AUTO_HP.Set(0x017D);
        Header.PET_AUTO_MP.Set(0x017E);
        // 0x017F
        // 0x0180 未使用
        // 0x0181 未使用
        // 0x0182 未使用
        // 0x0183 未使用
        // 0x0184 未使用
        // 0x0185 未使用
        // 0x0186 未使用
        // 0x0187 未使用
        //Header.GET_MTS_TOKENS.Set(0x0188);
        //Header.MTS_OPERATION.Set(0x0189);
        // 0x018A 未使用
        // 0x018B 未使用
        // 0x018C 未使用
        // 0x018D
        // 0x018E
        // 0x018F @018F [01] [01-03], /MapleTV コマンドのエラーメッセージ処理 (GMコマンドなので通常プレイでは不要)
        // 0x0190 未使用 (何もしない関数)
        // 0x0191 実質未使用 (VICIOUS)
        Header.VICIOUS_HAMMER.Set(0x0192);
        // 0x0193 実質未使用 (VICIOUS)
        // 0x0194 実質未使用 (VICIOUS)
        // 0x0195 実質未使用 (VEGA)
        Header.VEGA_SCROLL.Set(0x0196);
        // 0x0197 実質未使用 (VEGA)
        // 0x0198 実質未使用 (VEGA)
        // 0x0199 一番最後の関数 0x00D76700が0以外の値のときのみ動作する
    }

}
