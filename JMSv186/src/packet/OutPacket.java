// クライアント側から送信されるパケットのヘッダの定義
package packet;

public class OutPacket {

    private byte[] packet;
    private int decoded;

    // MapleのInPacketのDecodeのように送信されたパケットを再度Decodeする
    public OutPacket(byte[] b) {
        packet = b;
        decoded = 0;
    }

    public static Header ToHeader(short w) {
        for (final Header h : Header.values()) {
            if (h.Get() == w) {
                return h;
            }
        }

        return Header.UNKNOWN;
    }

    public String Packet() {
        short header = (short) (((short) packet[0] & 0xFF) | ((short) packet[1] & 0xFF << 8));
        String text = String.format("@%04X", header);

        for (int i = 2; i < packet.length; i++) {
            text += String.format(" %02X", packet[i]);
        }

        return text;
    }

    public byte Decode1() {
        return (byte) packet[decoded++];
    }

    public short Decode2() {
        return (short) (((short) Decode1() & 0xFF) | (((short) Decode1() & 0xFF) << 8));
    }

    public int Decode4() {
        return (int) (((int) Decode2() & 0xFFFF) | (((int) Decode2() & 0xFFFF) << 16));
    }

    public long Decode8() {
        return (long) (((long) Decode4() & 0xFFFFFFFF) | (((long) Decode4() & 0xFFFFFFFF) << 32));
    }

    public byte[] DecodeBuffer() {
        int length = Decode2();
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            buffer[i] = Decode1();
        }

        return buffer;
    }

    public String DecodeStr() {
        int length = Decode2();
        byte[] buffer = new byte[length + 1];

        for (int i = 0; i < length; i++) {
            buffer[i] = Decode1();
        }
        // 終端文字を読み取る
        buffer[length] = Decode1();

        return new String(buffer);
    }

    public enum Header {
        // ヘッダに対応する処理の名前を定義
        UNKNOWN,
        LOGIN_PASSWORD,
        SERVERLIST_REQUEST,
        CHARLIST_REQUEST,
        SERVERSTATUS_REQUEST,
        CHAR_SELECT,
        PLAYER_LOGGEDIN,
        CHECK_CHAR_NAME,
        CREATE_CHAR,
        DELETE_CHAR,
        LATEST_CRASH_DATA,
        AUTH_SECOND_PASSWORD,
        REACHED_LOGIN_SCREEN,
        RSA_KEY,
        CHANGE_MAP,
        CHANGE_CHANNEL,
        ENTER_CASH_SHOP,
        MOVE_PLAYER,
        CANCEL_CHAIR,
        USE_CHAIR,
        CLOSE_RANGE_ATTACK,
        RANGED_ATTACK,
        MAGIC_ATTACK,
        PASSIVE_ENERGY,
        TAKE_DAMAGE,
        CLOSE_CHALKBOARD,
        FACE_EXPRESSION,
        USE_ITEMEFFECT,
        WHEEL_OF_FORTUNE,
        MONSTER_BOOK_COVER,
        NPC_TALK,
        HIRED_MERCHANT_REMOTE,
        NPC_TALK_MORE,
        NPC_SHOP,
        STORAGE,
        MERCH_ITEM_STORE,
        DUEY_ACTION,
        OWL_OPEN_UI,
        OWL_WARP,
        ITEM_SORT,
        ITEM_GATHER,
        ITEM_MOVE,
        USE_ITEM,
        CANCEL_ITEM_EFFECT,
        USE_SUMMON_BAG,
        PET_FOOD,
        USE_MOUNT_FOOD,
        USE_SCRIPTED_NPC_ITEM,
        USE_CASH_ITEM,
        USE_CATCH_ITEM,
        USE_SKILL_BOOK,
        OWL_USE_ITEM_VERSION_SEARCH,
        USE_TELE_ROCK,
        USE_RETURN_SCROLL,
        DISTRIBUTE_AP,
        AUTO_ASSIGN_AP,
        HEAL_OVER_TIME,
        DISTRIBUTE_SP,
        SPECIAL_MOVE,
        CANCEL_BUFF,
        SKILL_EFFECT,
        MESO_DROP,
        GIVE_FAME,
        CHAR_INFO_REQUEST,
        SPAWN_PET,
        CANCEL_DEBUFF,
        CHANGE_MAP_SPECIAL,
        PORTAL_INSIDE_MAP,
        GET_BUFF_REQUEST,
        QUEST_ACTION,
        GENERAL_CHAT,
        USE_UPGRADE_SCROLL,
        USE_EQUIP_SCROLL,
        USE_POTENTIAL_SCROLL,
        USE_MAGNIFY_GLASS,
        USE_HIRED_MERCHANT,
        TROCK_ADD_MAP,
        SKILL_MACRO,
        ITEM_MAKER,
        GM_COMMAND,
        GM_COMMAND_TEXT,
        GM_COMMAND_SERVER_MESSAGE,
        GM_COMMAND_EVENT_START,
        GM_COMMAND_MAPLETV,
        SNOWBALL,
        PET_CHAT,
        REWARD_ITEM,
        REPAIR_ALL,
        REPAIR,
        SOLOMON,
        GACH_EXP,
        FOLLOW_REQUEST,
        FOLLOW_REPLY,
        USE_TREASUER_CHEST,
        PARTYCHAT,
        WHISPER,
        MESSENGER,
        PLAYER_INTERACTION,
        PARTY_OPERATION,
        DENY_PARTY_REQUEST,
        EXPEDITION_OPERATION,
        EXPEDITION_LISTING,
        GUILD_OPERATION,
        DENY_GUILD_REQUEST,
        BUDDYLIST_MODIFY,
        NOTE_ACTION,
        USE_DOOR,
        CHANGE_KEYMAP,
        RPS_GAME,
        RING_ACTION,
        WEDDING_REGISTRY,
        ALLIANCE_OPERATION,
        DENY_ALLIANCE_REQUEST,
        REQUEST_FAMILY,
        OPEN_FAMILY,
        FAMILY_OPERATION,
        DELETE_JUNIOR,
        DELETE_SENIOR,
        ACCEPT_FAMILY,
        USE_FAMILY,
        FAMILY_PRECEPT,
        FAMILY_SUMMON,
        CYGNUS_SUMMON,
        ARAN_COMBO,
        BBS_OPERATION,
        ENTER_MTS,
        AVATAR_RANDOM_BOX_OPEN,
        MOVE_PET,
        PET_COMMAND,
        PET_LOOT,
        PET_AUTO_POT,
        MOVE_SUMMON,
        SUMMON_ATTACK,
        DAMAGE_SUMMON,
        MOVE_DRAGON,
        MOVE_LIFE,
        AUTO_AGGRO,
        FRIENDLY_DAMAGE,
        MONSTER_BOMB,
        HYPNOTIZE_DMG,
        MOB_NODE,
        DISPLAY_NODE,
        NPC_ACTION,
        ITEM_PICKUP,
        DAMAGE_REACTOR,
        TOUCH_REACTOR,
        LEFT_KNOCK_BACK,
        COCONUT,
        MONSTER_CARNIVAL,
        SHIP_OBJECT,
        PARTY_SEARCH_START,
        PARTY_SEARCH_STOP,
        CS_FILL,
        CS_UPDATE,
        BUY_CS_ITEM,
        COUPON_CODE,
        RECOMMENDED_AVATAR,
        ETC_ITEM_UI,
        ETC_ITEM_UI_UPDATE,
        ETC_ITEM_UI_DROP_ITEM,
        MAPLETV,
        UPDATE_QUEST,
        QUEST_ITEM,
        USE_ITEM_QUEST,
        TOUCHING_MTS,
        MTS_TAB,
        BEANS_OPERATION,
        BEANS_UPDATE,
        VICIOUS_HAMMER;

        // 定義値の変更や取得
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

    // JMS v186.1 SendPacket
    public static void SetForJMSv186() {
        // 0x0000
        Header.LOGIN_PASSWORD.Set(0x0001);
        // 0x0002
        Header.SERVERLIST_REQUEST.Set(0x0003);
        Header.CHARLIST_REQUEST.Set(0x0004);
        Header.SERVERSTATUS_REQUEST.Set(0x0005);
        Header.CHAR_SELECT.Set(0x0006);
        Header.PLAYER_LOGGEDIN.Set(0x0007);
        Header.CHECK_CHAR_NAME.Set(0x0008);
        // 0x0009
        // 0x000A @000A, 全キャラクター確認
        Header.CREATE_CHAR.Set(0x000B);
        // 0x000C
        Header.DELETE_CHAR.Set(0x000D);
        // 0x000E InPacket @0x0009から送信されるようになっているが未確認
        Header.LATEST_CRASH_DATA.Set(0x000F);
        // 0x0010
        // 0x0011 InPacket 0x000Eから送信される
        // 0x0012
        // 0x0013
        Header.AUTH_SECOND_PASSWORD.Set(0x0014);
        // 0x0015
        // 0x0016
        // 0x0017
        Header.REACHED_LOGIN_SCREEN.Set(0x0018);
        // 0x0019 InPacket 0x0013から送信される
        Header.RSA_KEY.Set(0x001A);
        // 0x001B
        // 0x001C
        Header.CHANGE_MAP.Set(0x001D);
        Header.CHANGE_CHANNEL.Set(0x001E);
        Header.ENTER_CASH_SHOP.Set(0x001F);
        Header.MOVE_PLAYER.Set(0x0020);
        Header.CANCEL_CHAIR.Set(0x0021);
        Header.USE_CHAIR.Set(0x0022);
        Header.CLOSE_RANGE_ATTACK.Set(0x0023);
        Header.RANGED_ATTACK.Set(0x0024);
        Header.MAGIC_ATTACK.Set(0x0025);
        Header.PASSIVE_ENERGY.Set(0x0026);
        Header.TAKE_DAMAGE.Set(0x0027);
        // 0x0028
        Header.GENERAL_CHAT.Set(0x0029);
        Header.CLOSE_CHALKBOARD.Set(0x002A);
        Header.FACE_EXPRESSION.Set(0x002B);
        Header.USE_ITEMEFFECT.Set(0x002C);
        Header.WHEEL_OF_FORTUNE.Set(0x002D);
        // 0x0030
        Header.MONSTER_BOOK_COVER.Set(0x0031);
        Header.NPC_TALK.Set(0x0032);
        Header.HIRED_MERCHANT_REMOTE.Set(0x0033);
        Header.NPC_TALK_MORE.Set(0x0034);
        Header.NPC_SHOP.Set(0x0035);
        Header.STORAGE.Set(0x0036);
        Header.USE_HIRED_MERCHANT.Set(0x0037);
        // 0x0038
        Header.DUEY_ACTION.Set(0x0039); // OK
        Header.MERCH_ITEM_STORE.Set(0x003A);
        Header.OWL_OPEN_UI.Set(0x003B);
        Header.OWL_WARP.Set(0x003C);
        // 0x003D InPacket 0x0158, 0x0159から送信される
        Header.ITEM_SORT.Set(0x003E);
        Header.ITEM_GATHER.Set(0x003F);
        Header.ITEM_MOVE.Set(0x0040);
        Header.USE_ITEM.Set(0x0041);
        Header.CANCEL_ITEM_EFFECT.Set(0x0042);
        // 0x0043
        Header.USE_SUMMON_BAG.Set(0x0044);
        Header.PET_FOOD.Set(0x0045);
        Header.USE_MOUNT_FOOD.Set(0x0046);
        Header.USE_SCRIPTED_NPC_ITEM.Set(0x0047);
        Header.USE_CASH_ITEM.Set(0x0048);
        Header.USE_CATCH_ITEM.Set(0x004A);
        Header.USE_SKILL_BOOK.Set(0x004B);
        Header.OWL_USE_ITEM_VERSION_SEARCH.Set(0x004C);
        Header.USE_TELE_ROCK.Set(0x004D);
        Header.USE_RETURN_SCROLL.Set(0x004E);
        Header.USE_UPGRADE_SCROLL.Set(0x004F);
        Header.USE_EQUIP_SCROLL.Set(0x0050);
        Header.USE_POTENTIAL_SCROLL.Set(0x0051);
        Header.USE_MAGNIFY_GLASS.Set(0x0052);
        Header.DISTRIBUTE_AP.Set(0x0053);
        Header.AUTO_ASSIGN_AP.Set(0x0054);
        Header.HEAL_OVER_TIME.Set(0x0055);
        // 0x0056
        Header.DISTRIBUTE_SP.Set(0x0057);
        Header.SPECIAL_MOVE.Set(0x0058);
        Header.CANCEL_BUFF.Set(0x0059);
        Header.SKILL_EFFECT.Set(0x005A);
        Header.MESO_DROP.Set(0x005B);
        Header.GIVE_FAME.Set(0x005C);
        // 0x005D
        Header.CHAR_INFO_REQUEST.Set(0x005E);
        Header.SPAWN_PET.Set(0x005F);
        Header.CANCEL_DEBUFF.Set(0x0060);
        // 0x0061
        Header.CHANGE_MAP_SPECIAL.Set(0x0062);
        Header.PORTAL_INSIDE_MAP.Set(0x0063);
        Header.TROCK_ADD_MAP.Set(0x0064);
        // 0x0065
        // 0x0066
        // 0x0067
        // 0x0068
        Header.QUEST_ACTION.Set(0x0069);
        Header.GET_BUFF_REQUEST.Set(0x006A);
        // 0x006B
        Header.SKILL_MACRO.Set(0x006C);
        // 0x006D
        // 0x006E
        Header.ITEM_MAKER.Set(0x006F);
        Header.REWARD_ITEM.Set(0x0070);
        // 0x0071
        Header.REPAIR_ALL.Set(0x0072);
        Header.REPAIR.Set(0x0073);
        // 0x0074
        // 0x0075
        Header.SOLOMON.Set(0x0076);
        Header.GACH_EXP.Set(0x0077);
        Header.FOLLOW_REQUEST.Set(0x0078);
        Header.FOLLOW_REPLY.Set(0x0079);
        Header.USE_TREASUER_CHEST.Set(0x007A);
        Header.GM_COMMAND_SERVER_MESSAGE.Set(0x007B);
        Header.PARTYCHAT.Set(0x007C);
        Header.WHISPER.Set(0x007D);
        Header.MESSENGER.Set(0x007E);
        Header.PLAYER_INTERACTION.Set(0x007F);
        Header.PARTY_OPERATION.Set(0x0080);
        Header.DENY_PARTY_REQUEST.Set(0x0081);
        Header.EXPEDITION_OPERATION.Set(0x0082);
        Header.EXPEDITION_LISTING.Set(0x0083);
        Header.GUILD_OPERATION.Set(0x0084);
        Header.DENY_GUILD_REQUEST.Set(0x0085);
        Header.GM_COMMAND.Set(0x0086);
        Header.GM_COMMAND_TEXT.Set(0x0087);
        Header.BUDDYLIST_MODIFY.Set(0x0088);
        Header.NOTE_ACTION.Set(0x0089);
        // 0x008A
        Header.USE_DOOR.Set(0x008B);
        // 0x008C
        // 0x008D
        Header.CHANGE_KEYMAP.Set(0x008E);
        Header.RPS_GAME.Set(0x008F);
        Header.RING_ACTION.Set(0x0090);
        Header.WEDDING_REGISTRY.Set(0x0091);
        // 0x0092
        // 0x0093
        // 0x0094
        Header.ALLIANCE_OPERATION.Set(0x0095);
        Header.DENY_ALLIANCE_REQUEST.Set(0x0096);
        Header.REQUEST_FAMILY.Set(0x0097);
        Header.OPEN_FAMILY.Set(0x0098);
        Header.FAMILY_OPERATION.Set(0x0099);
        Header.DELETE_JUNIOR.Set(0x009A);
        Header.DELETE_SENIOR.Set(0x009B);
        Header.ACCEPT_FAMILY.Set(0x009C);
        Header.USE_FAMILY.Set(0x009D);
        Header.FAMILY_PRECEPT.Set(0x009E);
        Header.FAMILY_SUMMON.Set(0x009F);
        Header.CYGNUS_SUMMON.Set(0x00A0);
        Header.ARAN_COMBO.Set(0x00A1);
        // 0x00A2 InPacket 0x0111から送信される
        // 0x00A3
        Header.BBS_OPERATION.Set(0x00A4);
        // 0x00A5
        // 0x00A6
        // 0x00A7
        // 0x00A8
        // 0x00A9
        Header.ENTER_MTS.Set(0x00AA);
        Header.AVATAR_RANDOM_BOX_OPEN.Set(0x00AB);
        // 0x00AC
        // 0x00AD
        Header.MOVE_PET.Set(0x00AE);
        Header.PET_CHAT.Set(0x00AF);
        Header.PET_COMMAND.Set(0x00B0);
        Header.PET_LOOT.Set(0x00B1);
        Header.PET_AUTO_POT.Set(0x00B2);
        // 0x00B3
        // 0x00B4
        // 0x00B5
        Header.MOVE_SUMMON.Set(0x00B6);
        Header.SUMMON_ATTACK.Set(0x00B7);
        Header.DAMAGE_SUMMON.Set(0x00B8);
        // 0x00B9
        // 0x00BA
        // 0x00BB
        // 0x00BC
        Header.MOVE_DRAGON.Set(0x00BD);
        // 0x00BE
        // 0x00BF
        // 0x00C0
        // 0x00C1
        // 0x00C2 InPacket 0x007Cから送信される, ファムの歌を利用する処理 @00C2 [00or01]が送信される01は使用フラグ
        // 0x00C3
        // 0x00C4
        // 0x00C5
        // 0x00C6
        Header.MOVE_LIFE.Set(0x00C7);
        Header.AUTO_AGGRO.Set(0x00C8);
        // 0x00C9
        // 0x00CA
        Header.FRIENDLY_DAMAGE.Set(0x00CB);
        Header.MONSTER_BOMB.Set(0x00CC);
        Header.HYPNOTIZE_DMG.Set(0x00CD);
        // 0x00CE
        // 0x00CF
        Header.MOB_NODE.Set(0x00D0);
        Header.DISPLAY_NODE.Set(0x00D1);
        // 0x00D2
        // 0x00D3
        // 0x00D4
        Header.NPC_ACTION.Set(0x00D5);
        // 0x00D6
        // 0x00D7
        // 0x00D8
        // 0x00D9
        Header.ITEM_PICKUP.Set(0x00DA);
        // 0x00DB
        // 0x00DC
        Header.DAMAGE_REACTOR.Set(0x00DD);
        Header.TOUCH_REACTOR.Set(0x00DE);
        // 0x00DF
        // 0x00E0
        // 0x00E1
        Header.GM_COMMAND_EVENT_START.Set(0x00E2);
        // 0x00E3
        Header.SNOWBALL.Set(0x00E4);
        Header.LEFT_KNOCK_BACK.Set(0x00E5);
        Header.COCONUT.Set(0x00E6);
        // 0x00E7
        // 0x00E8
        Header.MONSTER_CARNIVAL.Set(0x00E9);
        // 0x00EA
        // 0x00EB
        Header.SHIP_OBJECT.Set(0x00EC);
        // 0x00ED
        Header.PARTY_SEARCH_START.Set(0x00EE);
        Header.PARTY_SEARCH_STOP.Set(0x00EF);
        // 0x00F0
        // 0x00F1
        // 0x00F2 InPacket 0x00A0から送信される
        Header.BEANS_OPERATION.Set(0x0000F3);
        Header.BEANS_UPDATE.Set(0x0000F4);
        // 0x00F5
        // 0x00F6
        // 0x00F7
        Header.CS_FILL.Set(0x00F8);
        Header.CS_UPDATE.Set(0x00F9);
        Header.BUY_CS_ITEM.Set(0x00FA);
        Header.COUPON_CODE.Set(0x00FB);
        // 0x00FC UI/CashShop.img/CSMemberShop
        // 0x00FD
        Header.RECOMMENDED_AVATAR.Set(0x00FE);
        // 0x00FF
        // 0x0100
        // 0x0101
        // 0x0102
        // 0x0103
        Header.ETC_ITEM_UI_UPDATE.Set(0x0104);
        Header.ETC_ITEM_UI.Set(0x0105);
        Header.ETC_ITEM_UI_DROP_ITEM.Set(0x0106);
        // 0x0107
        // 0x0108
        // 0x0109
        Header.MAPLETV.Set(0x010A);
        // 0x010B @00EAでCOUNSEL UIを開いたときに送信される
        Header.QUEST_ITEM.Set(0x010C);
        // 0x010D @00EC 1FでメイプルイベントのUIを開いたときに@010D 00が送信される
        // 0x010E
        // 0x010F
        // 0x0110
        Header.MTS_TAB.Set(0x0111);
        // 0x0112
        // 0x0113
        Header.GM_COMMAND_MAPLETV.Set(0x0114);
        // 0x0115
        // 0x0116
        // 0x0117
        // 0x0118
        // 0x0119
        Header.VICIOUS_HAMMER.Set(0x0119);
        // 0x011A
        // 0x011B
        // 0x011C
        // 0x011D
        // 0x011E
        Header.TOUCHING_MTS.Set(0x011F);
        // DUEY_ACTION
    }

}
