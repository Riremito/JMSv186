package packet;

import packet.server.ServerPacket;
import client.ISkill;
import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MapleQuestStatus;
import client.SkillEntry;
import client.inventory.MapleRing;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.ServerConfig;
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
        return data.Get().getBytes();
    }

    public static final byte[] addSkillInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        final Map<ISkill, SkillEntry> skills = chr.getSkills();
        data.Encode2(skills.size());
        for (final Map.Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
            data.Encode4(skill.getKey().getId());
            data.Encode4(skill.getValue().skillevel);

            // not in v165
            if (ServerConfig.IsJMS() && 180 <= ServerConfig.GetVersion()
                    || ServerConfig.IsTWMS()
                    || ServerConfig.IsCMS()) {
                data.EncodeBuffer(addExpirationTime(skill.getValue().expiration));
            }

            if (skill.getKey().isFourthJob()) {
                data.Encode4(skill.getValue().masterlevel);
            }
        }
        return data.Get().getBytes();
    }

    public static final byte[] addCoolDownInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        final List<MapleCoolDownValueHolder> cd = chr.getCooldowns();

        data.Encode2(cd.size());
        for (final MapleCoolDownValueHolder cooling : cd) {
            data.Encode4(cooling.skillId);
            data.Encode2((int) (cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
        }
        return data.Get().getBytes();
    }

    public static byte[] addQuestInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        final List<MapleQuestStatus> started = chr.getStartedQuests();

        data.Encode2(started.size());
        for (final MapleQuestStatus q : started) {
            data.Encode2(q.getQuest().getId());
            data.EncodeStr(q.getCustomData() != null ? q.getCustomData() : "");
        }

        // not in v165, not in v188, but in v194 ???
        if (ServerConfig.IsJMS() && 184 <= ServerConfig.GetVersion() && ServerConfig.GetVersion() <= 186) {
            data.Encode2(0); // not 0, EncodeStr, EncodeStr
        }

        if (ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion()) {
            data.Encode2(0); // not 0, EncodeStr, EncodeStr
        }

        return data.Get().getBytes();
    }

    public static byte[] addQuestComplete(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        final List<MapleQuestStatus> completed = chr.getCompletedQuests();
        int time;
        data.Encode2(completed.size());
        for (final MapleQuestStatus q : completed) {
            data.Encode2(q.getQuest().getId());
            time = KoreanDateUtil.getQuestTimestamp(q.getCompletionTime());
            data.Encode4(time); // maybe start time? no effect.
            data.Encode4(time); // completion time
        }

        return data.Get().getBytes();
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

        return data.Get().getBytes();
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

        if (ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion()) {
            for (int i = 0; i < 13; i++) {
                data.Encode4(999999999);
            }
        }

        return data.Get().getBytes();
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
        return data.Get().getBytes();
    }

    public static final byte[] QuestInfoPacket(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        Map<Integer, String> questinfo = chr.getInfoQuest_Map();

        data.Encode2(questinfo.size());
        for (final Map.Entry<Integer, String> q : questinfo.entrySet()) {
            data.Encode2(q.getKey());
            data.EncodeStr(q.getValue() == null ? "" : q.getValue());
        }
        return data.Get().getBytes();
    }

    // addMonsterStatus
    public static final byte[] MonsterStatus(MapleMonster life) {
        ServerPacket data = new ServerPacket();

        if (ServerConfig.IsPostBB()) {
            data.Encode4(0);
        }

        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131) {
            if (life.getStati().size() <= 1) {
                life.addEmpty(); //not done yet lulz ok so we add it now for the lulz
            }
        } else {
            if (life.getStati().size() <= 0) {
                life.addEmpty(); //not done yet lulz ok so we add it now for the lulz
            }
        }
        if ((ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            data.Encode8(getSpecialLongMask(life.getStati().keySet()));
        }

        data.Encode8(getLongMask_NoRef(life.getStati().keySet()));

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
        return data.Get().getBytes();
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

        return p.Get().getBytes();
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

        return p.Get().getBytes();
    }
}
