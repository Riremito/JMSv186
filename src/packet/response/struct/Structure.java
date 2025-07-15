package packet.response.struct;

import packet.ServerPacket;
import client.ISkill;
import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MapleQuestStatus;
import client.SkillEntry;
import client.inventory.MapleRing;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import server.life.MapleMonster;
import server.shops.AbstractPlayerStore;
import server.shops.IMaplePlayerShop;
import tools.KoreanDateUtil;
import tools.Pair;

public class Structure {

    // Login Server
    public static final byte[] addExpirationTime(final long time) {
        ServerPacket data = new ServerPacket();
        data.Encode1(0);
        data.Encode2(1408);
        if (time != -1) {
            data.Encode4(KoreanDateUtil.getItemTimestamp(time));
            data.Encode1(1);
        } else {
            data.Encode4(400967355);
            data.Encode1(2);
        }
        return data.get().getBytes();
    }

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
        if (ServerConfig.JMS302orLater()) {
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

    public static final byte[] addSkillInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        if (ServerConfig.KMS148orLater() || ServerConfig.JMS302orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            data.Encode1(1);
        }
        final Map<ISkill, SkillEntry> skills = chr.getSkills();
        data.Encode2(skills.size());
        for (final Map.Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
            data.Encode4(skill.getKey().getId());
            data.Encode4(skill.getValue().skillevel);

            // not in v165
            if (ServerConfig.JMS180orLater() || ServerConfig.GMS83orLater()) {
                data.EncodeBuffer(addExpirationTime(skill.getValue().expiration));
            }

            if (is_skill_need_master_level(skill.getKey().getId())) {
                data.Encode4(skill.getValue().masterlevel);
            }
            if (ServerConfig.JMS302orLater()) {
                if (skill.getKey().getId() == 40020002 || skill.getKey().getId() == 80000004) {
                    data.Encode4(0);
                }
            }
            if (ServerConfig.KMS197orLater()) {
                data.Encode2(0);
            }
        }
        return data.get().getBytes();
    }

    public static final byte[] addCoolDownInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        final List<MapleCoolDownValueHolder> cd = chr.getCooldowns();

        data.Encode2(cd.size());
        for (final MapleCoolDownValueHolder cooling : cd) {
            data.Encode4(cooling.skillId);
            if (ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
                data.Encode4((int) (cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
            } else {
                data.Encode2((int) (cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
            }
        }
        return data.get().getBytes();
    }

    public static byte[] addQuestInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        final List<MapleQuestStatus> started = chr.getStartedQuests();

        if (ServerConfig.KMS138orLater() || ServerConfig.JMS302orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            data.Encode1(0);
        }

        data.Encode2(started.size());
        for (final MapleQuestStatus q : started) {
            data.Encode2(q.getQuest().getId());
            data.EncodeStr(q.getCustomData() != null ? q.getCustomData() : "");
        }

        // not in v165, not in v188, but in v194 ???
        if (ServerConfig.IsJMS() && 184 <= Version.getVersion() && Version.getVersion() <= 186) {
            data.Encode2(0); // not 0, EncodeStr, EncodeStr
        }

        if ((ServerConfig.JMS194orLater() && !ServerConfig.IsKMS() && !ServerConfig.IsEMS()) || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            data.Encode2(0); // not 0, EncodeStr, EncodeStr
        }

        if (ServerConfig.KMS138orLater() || ServerConfig.JMS302orLater() || ServerConfig.EMS89orLater()) {
            data.Encode2(0);
        }

        return data.get().getBytes();
    }

    public static byte[] addQuestComplete(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        if (ServerConfig.KMS148orLater() || ServerConfig.JMS302orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            data.Encode1(0);
        }

        final List<MapleQuestStatus> completed = chr.getCompletedQuests();
        int time;
        data.Encode2(completed.size());
        for (final MapleQuestStatus q : completed) {
            data.Encode2(q.getQuest().getId());
            time = KoreanDateUtil.getQuestTimestamp(q.getCompletionTime());
            data.Encode4(time); // maybe start time? no effect.
            data.Encode4(time); // completion time
        }

        if (ServerConfig.KMS148orLater() || ServerConfig.JMS302orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            data.Encode2(0);
        }
        return data.get().getBytes();
    }

    // v165, v186
    public static final byte[] addRingInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        Pair<List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
        List<MapleRing> cRing = aRing.getLeft();

        data.Encode2(cRing.size());
        for (MapleRing ring : cRing) {
            // 33 bytes
            data.Encode4(ring.getPartnerChrId());
            data.EncodeBuffer(ring.getPartnerName(), 13);
            data.Encode8(ring.getRingId());
            data.Encode8(ring.getPartnerRingId());
        }

        List<MapleRing> fRing = aRing.getRight();
        data.Encode2(fRing.size());
        for (MapleRing ring : fRing) {
            // 37 bytes
            data.Encode4(ring.getPartnerChrId());
            data.EncodeBuffer(ring.getPartnerName(), 13);
            data.Encode8(ring.getRingId());
            data.Encode8(ring.getPartnerRingId());
            data.Encode4(ring.getItemId());
        }

        data.Encode2(0);
        // if not 0, 48 bytes

        return data.get().getBytes();
    }

    public static final byte[] addRocksInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        final int[] mapz = chr.getRegRocks();
        for (int i = 0; i < 5; i++) { // VIP teleport map
            data.Encode4(mapz[i]);
        }

        final int[] map = chr.getRocks();
        for (int i = 0; i < 10; i++) { // VIP teleport map
            data.Encode4(map[i]);
        }

        if (ServerConfig.JMS194orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            for (int i = 0; i < 13; i++) {
                data.Encode4(999999999);
            }
        }

        if (ServerConfig.IsEMS() && Version.PostBB()) {
            for (int i = 0; i < 13; i++) {
                data.Encode4(999999999);
            }
        }

        return data.get().getBytes();
    }

    public static final byte[] addMonsterBookInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        data.Encode1(0);
        // [chr.getMonsterBook().addCardPacket]
        {
            Map<Integer, Integer> cards = chr.getMonsterBook().getCards();
            data.Encode2(cards.size());
            for (Map.Entry<Integer, Integer> all : cards.entrySet()) {
                // ID
                data.Encode2(GameConstants.getCardShortId(all.getKey()));
                // 登録枚数
                data.Encode1(all.getValue());
            }
        }
        return data.get().getBytes();
    }

    public static final byte[] QuestInfoPacket(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        Map<Integer, String> questinfo = chr.getInfoQuest_Map();

        data.Encode2(questinfo.size());
        for (final Map.Entry<Integer, String> q : questinfo.entrySet()) {
            data.Encode2(q.getKey());
            data.EncodeStr(q.getValue() == null ? "" : q.getValue());
        }
        return data.get().getBytes();
    }

    // addMonsterStatus
    public static final byte[] MonsterStatus(MapleMonster life) {
        ServerPacket data = new ServerPacket();

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
        }
        if (Version.PostBB()) {
            data.Encode4(0);
        }

        if (ServerConfig.JMS131orEarlier()) {
            if (life.getStati().size() <= 1) {
                life.addEmpty(); //not done yet lulz ok so we add it now for the lulz
            }
        } else {
            if (life.getStati().size() <= 0) {
                life.addEmpty(); //not done yet lulz ok so we add it now for the lulz
            }
        }
        if (ServerConfig.JMS164orLater()) {
            data.Encode8(getSpecialLongMask(life.getStati().keySet()));
        }

        data.Encode8(getLongMask_NoRef(life.getStati().keySet()));

        // ?_?
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            return data.get().getBytes();
        }

        boolean ignore_imm = false;
        for (MonsterStatusEffect buff : life.getStati().values()) {
            if (buff.getStati() == MonsterStatus.MAGIC_DAMAGE_REFLECT || buff.getStati() == MonsterStatus.WEAPON_DAMAGE_REFLECT) {
                ignore_imm = true;
                break;
            }
        }
        for (MonsterStatusEffect buff : life.getStati().values()) {
            if (buff.getStati() != MonsterStatus.MAGIC_DAMAGE_REFLECT && buff.getStati() != MonsterStatus.WEAPON_DAMAGE_REFLECT) {
                if (ignore_imm) {
                    if (buff.getStati() == MonsterStatus.MAGIC_IMMUNITY || buff.getStati() == MonsterStatus.WEAPON_IMMUNITY) {
                        continue;
                    }
                }
                data.Encode2(buff.getX().shortValue());
                if (buff.getStati() != MonsterStatus.SUMMON) {
                    if (buff.getMobSkill() != null) {
                        data.Encode2(buff.getMobSkill().getSkillId());
                        data.Encode2(buff.getMobSkill().getSkillLevel());
                    } else if (buff.getSkill() > 0) {
                        data.Encode4(buff.getSkill());
                    }
                    data.Encode2(buff.getStati().isEmpty() ? 0 : 1);
                }
            }
        }

        //wh spawn - 15 zeroes instead of 16, then 98 F4 56 A6 C7 C9 01 28, then 7 zeroes
        return data.get().getBytes();
    }

    public static long getSpecialLongMask(Collection<MonsterStatus> statups) {
        long mask = 0;
        for (MonsterStatus statup : statups) {
            if (statup.isFirst()) {
                mask |= statup.getValue();
            }
        }
        return mask;
    }

    public static long getLongMask(Collection<MonsterStatus> statups) {
        long mask = 0;
        for (MonsterStatus statup : statups) {
            if (!statup.isFirst()) {
                mask |= statup.getValue();
            }
        }
        return mask;
    }

    public static long getLongMask_NoRef(Collection<MonsterStatus> statups) {
        long mask = 0;
        boolean ignore_imm = false;
        for (MonsterStatus statup : statups) {
            if (statup == MonsterStatus.MAGIC_DAMAGE_REFLECT || statup == MonsterStatus.WEAPON_DAMAGE_REFLECT) {
                ignore_imm = true;
                break;
            }
        }
        for (MonsterStatus statup : statups) {
            if (statup != MonsterStatus.MAGIC_DAMAGE_REFLECT && statup != MonsterStatus.WEAPON_DAMAGE_REFLECT) {
                if (ignore_imm) {
                    if (statup == MonsterStatus.MAGIC_IMMUNITY || statup == MonsterStatus.WEAPON_IMMUNITY) {
                        continue;
                    }
                }

                if (!statup.isFirst()) {
                    mask |= statup.getValue();
                }
            }
        }
        return mask;
    }

    // addAnnounceBox
    public static final byte[] AnnounceBox(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();
        if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr) && chr.getPlayerShop().getShopType() != 1 && chr.getPlayerShop().isAvailable()) {
            p.EncodeBuffer(Interaction(chr.getPlayerShop()));
        } else {
            p.Encode1(0);
        }

        return p.get().getBytes();
    }

    // addInteraction
    public static final byte[] Interaction(IMaplePlayerShop shop) {
        ServerPacket p = new ServerPacket();
        p.Encode1(shop.getGameType());
        p.Encode4(((AbstractPlayerStore) shop).getObjectId());
        p.EncodeStr(shop.getDescription());
        if (shop.getShopType() != 1) {
            p.Encode1(shop.getPassword().length() > 0 ? 1 : 0); //password = false
        }
        p.Encode1(shop.getItemId() % 10);
        p.Encode1(shop.getSize()); //current size
        p.Encode1(shop.getMaxSize()); //full slots... 4 = 4-1=3 = has slots, 1-1=0 = no slots
        if (shop.getShopType() != 1) {
            p.Encode1(shop.isOpen() ? 0 : 1);
        }

        return p.get().getBytes();
    }
}
