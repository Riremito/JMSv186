package tacos.packet.response.struct;

import tacos.packet.ServerPacket;
import odin.client.ISkill;
import odin.client.MapleCharacter;
import odin.client.MapleQuestStatus;
import odin.client.SkillEntry;
import odin.client.inventory.MapleRing;
import tacos.config.Region;
import tacos.config.Config;
import tacos.config.Version;
import java.util.List;
import java.util.Map;
import odin.server.shops.AbstractPlayerStore;
import odin.server.shops.IMaplePlayerShop;
import tacos.odin.OdinPair;
import tacos.shared.SharedDate;

public class Structure {

    public static boolean is_ignore_master_level_for_common(int skill_id) {
        // JMS v302
        switch (skill_id) {
            case 1120012:
            case 1220013:
            case 1320011:
            case 2121009:
            case 2221009:
            case 2321010:
            case 3120010:
            case 3120011:
            case 3120012:
            case 3220009:
            case 3220010:
            case 3220012:
            case 4110012:
            case 4210012:
            case 4340010:
            case 5120011:
            case 5220012:
            case 5220014:
            case 5321003:
            case 5321004:
            case 5321006:
            case 5320007:
            case 21120011:
            case 22181004:
            case 23120011:
            case 23121008:
            case 33120010:
            case 33121005:
            case 1: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean is_skill_need_master_level(int skill_id) {
        // JMS v302
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            return is_skill_need_master_level_302(skill_id);
        }
        // JMS v188-v194
        if (Version.PostBB()) {
            return is_skill_need_master_level_188(skill_id);
        }
        // JMS under 186
        int job_id = skill_id / 10000;
        // エヴァン
        if (2200 <= job_id && job_id <= 2218) {
            if (7 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // デュアルブレイド
        if (430 <= job_id && job_id <= 434) {
            if (4 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // 初心者
        switch (job_id) {
            case 0:
            case 1000:
            case 2000:
            case 2001:
            case 3000: {
                return false;
            }
            default: {
                break;
            }
        }
        // 4次転職
        if (2 <= (job_id % 10)) {
            return true;
        }
        return false;
    }

    public static boolean is_skill_need_master_level_188(int skill_id) {
        int job_id = skill_id / 10000;
        // 除外スキル
        switch (skill_id) {
            case 1120012:
            case 1220013:
            case 1320011:
            case 2120009:
            case 2220009:
            case 2320010:
            case 3120010:
            case 3120011:
            case 3220009:
            case 3220010:
            case 4120010:
            case 4220009:
            case 5120011:
            case 5220012:
            case 32120009:
            case 33120010:
            case 1: {
                return false;
            }
            default: {
                break;
            }
        }

        // デュアルブレイド
        switch (skill_id) {
            case 4311003:
            case 4321000:
            case 4331002:
            case 4331005: {
                return true;
            }
            default: {
                break;
            }
        }
        // エヴァン
        if (2200 <= job_id && job_id <= 2218) {
            if (7 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // デュアルブレイド
        if (430 <= job_id && job_id <= 434) {
            if (4 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // 初心者
        switch (job_id) {
            case 0:
            case 1000:
            case 2000:
            case 2001:
            case 3000: {
                return false;
            }
            default: {
                break;
            }
        }
        // 4次転職
        if (2 <= (job_id % 10)) {
            return true;
        }
        return false;
    }

    public static boolean is_skill_need_master_level_302(int skill_id) {
        int job_id = skill_id / 10000;

        if (is_ignore_master_level_for_common(skill_id)) {
            return false;
        }

        // JMS v302
        if (9200 <= job_id) {
            return false;
        }
        // ライディング
        if (job_id == 8000) {
            if (80001063 <= skill_id && skill_id <= 80001077) {
                return true;
            }
            if (skill_id == 80001123) {
                return true;
            }
            return false;
        }

        switch (job_id) {
            case 0:
            case 1000:
            case 2000:
            case 2001:
            case 2002:
            case 2003:
            case 3000:
            case 3001:
            case 4001:
            case 4002: {
                return false;
            }
            default: {
                break;
            }
        }

        if (skill_id == 42120024) {
            return false;
        }

        switch (skill_id) {
            case 4311003:
            case 4321000:
            case 4331002:
            case 4331005: {
                return true;
            }
            default: {
                break;
            }
        }

        // エヴァン
        if (2200 <= job_id && job_id <= 2218) {
            if (7 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // デュアルブレイド
        if (430 <= job_id && job_id <= 434) {
            if (4 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // JMS v164
        if (2 <= (job_id % 10)) {
            return true;
        }
        return false;
    }

    public static byte[] addSkillInfo(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        if (Version.GreaterOrEqual(Region.KMS, 148) || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode1(1);
        }
        final Map<ISkill, SkillEntry> skills = chr.getSkills();
        data.Encode2(skills.size());
        for (final Map.Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
            data.Encode4(skill.getKey().getId());
            data.Encode4(skill.getValue().skillevel);

            // not in v165
            if (Config.JMS180orLater() || Version.GreaterOrEqual(Region.GMS, 83)) {
                data.Encode8(SharedDate.getTimestamp(skill.getValue().expiration));
            }

            if (is_skill_need_master_level(skill.getKey().getId())) {
                data.Encode4(skill.getValue().masterlevel);
            }
            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                if (skill.getKey().getId() == 40020002 || skill.getKey().getId() == 80000004) {
                    data.Encode4(0);
                }
            }
        }
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            data.Encode2(0);
        }
        return data.getBytes();
    }

    public static byte[] addQuestInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        final List<MapleQuestStatus> started = chr.getStartedQuests();

        if (Config.KMS138orLater() || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode1(0);
        }

        data.Encode2(started.size());
        for (final MapleQuestStatus q : started) {
            data.Encode2(q.getQuest().getId());
            data.EncodeStr(q.getCustomData() != null ? q.getCustomData() : "");
        }

        // not in v165, not in v188, but in v194 ???
        if (Version.Between(Region.JMS, 184, 186)) {
            data.Encode2(0); // not 0, EncodeStr, EncodeStr
        }

        if (Region.KMS.check() || Region.KMST.check() || Version.LessOrEqual(Region.EMS, 76)) {
            // none
        } else if ((Config.JMS194orLater()) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode2(0); // not 0, EncodeStr, EncodeStr
        }

        if (Config.KMS138orLater() || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode2(0);
        }

        return data.getBytes();
    }

    public static byte[] addQuestComplete(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        if (Version.GreaterOrEqual(Region.KMS, 148) || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode1(0);
        }

        data.Encode2(chr.getCompletedQuests().size());
        for (MapleQuestStatus mqs : chr.getCompletedQuests()) {
            data.Encode2(mqs.getQuest().getId());
            data.Encode8(SharedDate.getTimestamp(mqs.getCompletionTime()));
        }

        if (Version.GreaterOrEqual(Region.KMS, 148) || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode2(0);
        }
        return data.getBytes();
    }

    // v165, v186
    public static final byte[] addRingInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        OdinPair<List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
        List<MapleRing> cRing = aRing.getLeft();

        data.Encode2(cRing.size());
        // GW_CoupleRecord::Decode, 33 bytes.
        for (MapleRing ring : cRing) {
            data.Encode4(ring.getPartnerChrId()); // dwPairCharacterID
            data.EncodeBuffer(ring.getPartnerName(), 13); // sPairCharacterName
            data.Encode8(ring.getRingId()); // liSN
            data.Encode8(ring.getPartnerRingId()); // liPairSN
        }

        if (Version.LessOrEqual(Region.KMS, 1)) {
            // nothing
        } else {
            // GW_FriendRecord::Decode, 37 bytes.
            List<MapleRing> fRing = aRing.getRight();
            data.Encode2(fRing.size());
            for (MapleRing ring : fRing) {
                data.Encode4(ring.getPartnerChrId()); // dwPairCharacterID
                data.EncodeBuffer(ring.getPartnerName(), 13); // sPairCharacterName
                data.Encode8(ring.getRingId()); // liSN
                data.Encode8(ring.getPartnerRingId()); // liPairSN
                data.Encode4(ring.getItemId()); // dwFriendItemID
            }
        }

        if (Version.LessOrEqual(Region.KMS, 41)) {
            // nothing
        } else {
            int married = 0;
            data.Encode2(married);
            // GW_MarriageRecord::Decode, 48 bytes.
            for (int i = 0; i < married; i++) {
                data.Encode4(0); // dwMarriageNo
                data.Encode4(0); // dwGroomID
                data.Encode4(0); // dwBrideID
                data.Encode2(0); // usStatus
                data.Encode4(0); // nGroomItemID
                data.Encode4(0); // nBrideItemID
                data.EncodeBuffer("", 13); // sGroomName
                data.EncodeBuffer("", 13); // sBrideName
            }
        }

        return data.getBytes();
    }

    public static final byte[] addRocksInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        final int[] mapz = chr.getRegRocks();
        for (int i = 0; i < 5; i++) { // VIP teleport map
            data.Encode4(mapz[i]);
        }

        if (Version.LessOrEqual(Region.KMS, 1)) {
            return data.getBytes();
        }

        final int[] map = chr.getRocks();
        for (int i = 0; i < 10; i++) { // VIP teleport map
            data.Encode4(map[i]);
        }

        if (Config.JMS194orLater() || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            for (int i = 0; i < 13; i++) {
                data.Encode4(999999999);
            }
        }

        if (Version.GreaterOrEqual(Region.EMS, 73) || Version.GreaterOrEqual(Region.GMS, 111)) {
            for (int i = 0; i < 13; i++) {
                data.Encode4(999999999);
            }
        }

        return data.getBytes();
    }

    public static final byte[] QuestInfoPacket(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        Map<Integer, String> questinfo = chr.getInfoQuest_Map();

        data.Encode2(questinfo.size());
        for (final Map.Entry<Integer, String> q : questinfo.entrySet()) {
            data.Encode2(q.getKey());
            data.EncodeStr(q.getValue() == null ? "" : q.getValue());
        }
        return data.getBytes();
    }

    // addAnnounceBox
    public static final byte[] AnnounceBox(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr) && chr.getPlayerShop().getShopType() != 1 && chr.getPlayerShop().isAvailable()) {
            data.EncodeBuffer(Interaction(chr.getPlayerShop()));
        } else {
            data.Encode1(0);
        }

        return data.getBytes();
    }

    // addInteraction
    public static final byte[] Interaction(IMaplePlayerShop shop) {
        ServerPacket data = new ServerPacket();

        data.Encode1(shop.getGameType());
        data.Encode4(((AbstractPlayerStore) shop).getObjectId());
        data.EncodeStr(shop.getDescription());
        if (shop.getShopType() != 1) {
            data.Encode1(shop.getPassword().length() > 0 ? 1 : 0); //password = false
        }
        data.Encode1(shop.getItemId() % 10);
        data.Encode1(shop.getSize()); //current size
        data.Encode1(shop.getMaxSize()); //full slots... 4 = 4-1=3 = has slots, 1-1=0 = no slots
        if (shop.getShopType() != 1) {
            data.Encode1(shop.isOpen() ? 0 : 1);
        }

        return data.getBytes();
    }
}
