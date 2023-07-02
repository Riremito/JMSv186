package packet;

import client.ISkill;
import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MapleQuestStatus;
import client.SkillEntry;
import client.inventory.IEquip;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.ServerConfig;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import server.life.MapleMonster;
import server.shops.AbstractPlayerStore;
import server.shops.IMaplePlayerShop;
import tools.KoreanDateUtil;
import tools.Pair;
import tools.packet.PacketHelper;
import packet.struct.GW_CharacterStat;

public class Structure {

    // Login Server
    public static void CharEntry(ServerPacket p, final MapleCharacter chr, boolean ranking, boolean isAll) {
        p.EncodeBuffer(GW_CharacterStat.Encode(chr));
        AvatarLook(p, chr);

        if (!isAll) {
            if (ServerConfig.version > 165) {
                p.Encode1(0);
            }
        }

        p.Encode1(ranking ? 1 : 0);

        if (ranking) {
            p.Encode4(chr.getRank());
            p.Encode4(chr.getRankMove());
            p.Encode4(chr.getJobRank());
            p.Encode4(chr.getJobRankMove());
        }
    }

    // AvatarLook::Decode
    // CharLook
    public static void AvatarLook(ServerPacket p, final MapleCharacter chr) {
        p.Encode1(chr.getGender());
        p.Encode1(chr.getSkinColor());
        p.Encode4(chr.getFace());
        p.Encode1(0); // smega?
        p.Encode4(chr.getHair());

        final Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);

        for (final IItem item : equip.list()) {
            if (item.getPosition() < -128) { //not visible
                continue;
            }
            byte pos = (byte) (item.getPosition() * -1);

            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, item.getItemId());
            } else if ((pos > 100 || pos == -128) && pos != 111) {
                pos = (byte) (pos == -128 ? 28 : pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, item.getItemId());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, item.getItemId());
            }
        }
        for (final Map.Entry<Byte, Integer> entry : myEquip.entrySet()) {
            p.Encode1(entry.getKey());
            p.Encode4(entry.getValue());
        }
        p.Encode1(0xFF); // end of visible itens
        // masked itens
        for (final Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            p.Encode1(entry.getKey());
            p.Encode4(entry.getValue());
        }
        p.Encode1(0xFF); // ending markers

        final IItem cWeapon = equip.getItem((byte) -111);
        p.Encode4(cWeapon != null ? cWeapon.getItemId() : 0);
        p.Encode4(0);

        if (ServerConfig.version >= 164) {
            p.Encode8(0);
        }
    }

    // Game Server
    // よく変わる構造
    public static final byte[] CharacterInfo(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();

        if (ServerConfig.version <= 131) {
            p.Encode2(-1);
        } else {
            p.Encode8(-1);
        }

        if (ServerConfig.version > 165) {
            p.Encode1(0);
        }
        if (ServerConfig.version > 186) {
            p.Encode1(0);
        }
        // キャラクター情報
        p.EncodeBuffer(GW_CharacterStat.Encode(chr));
        // 友達リストの上限
        p.Encode1(chr.getBuddylist().getCapacity());

        // 精霊の祝福
        if (ServerConfig.version >= 165) {
            if (chr.getBlessOfFairyOrigin() != null) {
                p.Encode1(1);
                p.EncodeStr(chr.getBlessOfFairyOrigin());
            } else {
                p.Encode1(0);
            }
        }

        p.EncodeBuffer(GW_CharacterStat.EncodeMoney(chr));
        p.EncodeBuffer(GW_CharacterStat.EncodePachinko(chr));
        // [addInventoryInfo]
        p.EncodeBuffer(InventoryInfo(chr));
        // [addSkillInfo]
        p.EncodeBuffer(addSkillInfo(chr));
        // [addCoolDownInfo]
        p.EncodeBuffer(addCoolDownInfo(chr));
        // [addQuestInfo]
        p.EncodeBuffer(addQuestInfo(chr));

        if (ServerConfig.version < 188) {
            p.Encode2(0);
        }

        // [addRingInfo]
        p.EncodeBuffer(addRingInfo(chr));

        if (ServerConfig.version > 165 || ServerConfig.version <= 131) {
            p.Encode2(0);
        }

        // [addRocksInfo]
        p.EncodeBuffer(addRocksInfo(chr));

        p.Encode2(0);

        if (ServerConfig.version > 131) {
            // [addMonsterBookInfo]
            p.EncodeBuffer(addMonsterBookInfo(chr));
            // [QuestInfoPacket]
            p.EncodeBuffer(QuestInfoPacket(chr));
            // PQ rank?
            p.Encode2(0);
        }

        if (ServerConfig.version > 165) {
            p.Encode2(0);
        }

        return p.Get().getBytes();
    }

    public static final byte[] InventoryInfo(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();
        // アイテム欄の数
        {
            p.Encode1(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
            p.Encode1(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
            p.Encode1(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
            p.Encode1(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
            p.Encode1(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());
        }
        if (ServerConfig.version > 164) {
            p.Encode4(0);
            p.Encode4(0);
        }
        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        Collection<IItem> equippedC = iv.list();
        List<Item> equipped = new ArrayList<Item>(equippedC.size());
        for (IItem item : equippedC) {
            equipped.add((Item) item);
        }
        Collections.sort(equipped);

        // 装備済みアイテム
        {
            for (Item item : equipped) {
                if (item.getPosition() < 0 && item.getPosition() > -100) {
                    p.EncodeBuffer(addItemInfo(item, false, false));
                }
            }
            if (ServerConfig.version <= 164) {
                p.Encode1(0);
            } else {
                p.Encode2(0);
            }
        }
        // 装備済みアバター?
        {
            for (Item item : equipped) {
                if (item.getPosition() <= -100 && item.getPosition() > -1000) {
                    p.EncodeBuffer(addItemInfo(item, false, false));
                }
            }
            // その他装備済みアイテム終了?
            if (ServerConfig.version <= 164) {
                p.Encode1(0);
            } else {
                p.Encode2(0);
            }
        }
        // 装備
        {
            iv = chr.getInventory(MapleInventoryType.EQUIP);
            for (IItem item : iv.list()) {
                p.EncodeBuffer(addItemInfo(item, false, false));
            }
            if (ServerConfig.version <= 164) {
                p.Encode1(0);
            } else {
                p.Encode2(0);
            }
        }
        // v164だとないかも?
        {
            if (ServerConfig.version > 164) {
                for (Item item : equipped) {
                    if (item.getPosition() <= -1000) {
                        p.EncodeBuffer(addItemInfo(item, false, false));
                    }
                }
                if (ServerConfig.version <= 164) {
                    p.Encode1(0);
                } else {
                    p.Encode2(0);
                }
            }
        }
        // v187.0
        // なんかしらのデータが消費アイテムの前にあってずれている
        // おそらくアイテム欄より後でもズレが発生してる
        if (ServerConfig.version >= 188) {
            p.Encode1(0);
            p.Encode1(0);
        }
        // 消費
        {
            iv = chr.getInventory(MapleInventoryType.USE);
            for (IItem item : iv.list()) {
                p.EncodeBuffer(addItemInfo(item, false, false));
            }
            p.Encode1(0);
        }
        // 設置
        {
            iv = chr.getInventory(MapleInventoryType.SETUP);
            for (IItem item : iv.list()) {
                p.EncodeBuffer(addItemInfo(item, false, false));
            }
            p.Encode1(0);
        }
        // ETC
        {
            iv = chr.getInventory(MapleInventoryType.ETC);
            for (IItem item : iv.list()) {
                p.EncodeBuffer(addItemInfo(item, false, false));
            }
            p.Encode1(0);
        }
        // ポイントアイテム
        {
            iv = chr.getInventory(MapleInventoryType.CASH);
            for (IItem item : iv.list()) {
                p.EncodeBuffer(addItemInfo(item, false, false));
            }
            p.Encode1(0);
        }

        return p.Get().getBytes();
    }

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

    public static final byte[] addItemInfo(final IItem item, final boolean zeroPosition, final boolean leaveOut) {
        return addItemInfo(item, zeroPosition, leaveOut, false);
    }

    public static final byte[] addItemInfo(final IItem item, final boolean zeroPosition, final boolean leaveOut, final boolean trade) {
        ServerPacket data = new ServerPacket();
        short pos = item.getPosition();
        if (zeroPosition) {
            if (!leaveOut) {
                data.Encode1(0);
            }
        } else {
            if (pos <= -1) {
                pos *= -1;
                if (pos > 100 && pos < 1000) {
                    pos -= 100;
                }
            }
            // v164では場所は1バイトで全て表現される
            if ((ServerConfig.version > 164 && !trade && item.getType() == 1)/* || ServerConfig.version >= 187*/) {
                data.Encode2(pos);
            } else {
                data.Encode1(pos);
            }
        }
        data.Encode1(item.getPet() != null ? 3 : item.getType());
        data.Encode4(item.getItemId());
        boolean hasUniqueId = item.getUniqueId() > 0;
        //marriage rings arent cash items so dont have uniqueids, but we assign them anyway for the sake of rings
        data.Encode1(hasUniqueId ? 1 : 0);
        if (hasUniqueId) {
            data.Encode8(item.getUniqueId());
        }
        if (item.getPet() != null) { // Pet
            data.EncodeBuffer(addPetItemInfo(item, item.getPet()));
        } else {
            data.EncodeBuffer(addExpirationTime(item.getExpiration()));
            if (item.getType() == 1) {
                final IEquip equip = (IEquip) item;
                data.Encode1(equip.getUpgradeSlots());
                data.Encode1(equip.getLevel());
                // 謎フラグ
                if (ServerConfig.version > 164 && ServerConfig.version <= 184) {
                    data.Encode1(0);
                }
                data.Encode2(equip.getStr());
                data.Encode2(equip.getDex());
                data.Encode2(equip.getInt());
                data.Encode2(equip.getLuk());
                data.Encode2(equip.getHp());
                data.Encode2(equip.getMp());
                data.Encode2(equip.getWatk());
                data.Encode2(equip.getMatk());
                data.Encode2(equip.getWdef());
                data.Encode2(equip.getMdef());
                data.Encode2(equip.getAcc());
                data.Encode2(equip.getAvoid());
                data.Encode2(equip.getHands());
                data.Encode2(equip.getSpeed());
                data.Encode2(equip.getJump());
                data.EncodeStr(equip.getOwner());
                // ポイントアイテムの一度も装備していないことを確認するためのフラグ
                if (hasUniqueId) {
                    // ポイントアイテム交換可能
                    data.Encode2(0x10);
                } else {
                    data.Encode2(equip.getFlag());
                }

                if (ServerConfig.version <= 131) {
                    if (!hasUniqueId) {
                        data.Encode8(equip.getPosition() <= 0 ? -1 : item.getUniqueId());
                    }
                    return data.Get().getBytes();
                }

                data.Encode1(0);
                data.Encode1(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // Item level

                if (hasUniqueId) {
                    data.Encode4(0);
                } else {
                    data.Encode2(0);
                    data.Encode2(equip.getExpPercentage() * 4); // Item Exp... 98% = 25%
                }
                // 耐久度
                if (ServerConfig.version > 164) {
                    data.Encode4(equip.getDurability());
                }
                // ビシャスのハンマー
                if (ServerConfig.version > 164) {
                    if (ServerConfig.game_server_enable_hammer) {
                        data.Encode4(equip.getViciousHammer());
                    } else {
                        data.Encode4(0);
                    }
                }
                // 潜在能力
                if (ServerConfig.version >= 186) {
                    if (!hasUniqueId) {
                        data.Encode1(equip.getState()); //7 = unique for the lulz
                        data.Encode1(equip.getEnhance());
                        if (ServerConfig.game_server_enable_potential) {
                            data.Encode2(equip.getPotential1()); //potential stuff 1. total damage
                            data.Encode2(equip.getPotential2()); //potential stuff 2. critical rate
                            data.Encode2(equip.getPotential3()); //potential stuff 3. all stats
                        } else {
                            data.Encode2(0);
                            data.Encode2(0);
                            data.Encode2(0);
                        }
                    }
                    data.Encode2(equip.getHpR());
                    data.Encode2(equip.getMpR());
                }
                data.Encode8(0);
                data.Encode8(0);
                data.Encode4(-1);
            } else {
                data.Encode2(item.getQuantity());
                data.EncodeStr(item.getOwner());
                data.Encode2(item.getFlag());
                if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    data.Encode4(2);
                    data.Encode2(0x54);
                    data.Encode1(0);
                    data.Encode1(0x34);
                }
            }
        }
        return data.Get().getBytes();
    }

    public static final byte[] addPetItemInfo(final IItem item, final MaplePet pet) {
        ServerPacket data = new ServerPacket();
        data.EncodeBuffer(addExpirationTime(-1));
        data.EncodeBuffer(pet.getName(), 13);
        data.Encode1(pet.getLevel());
        data.Encode2(pet.getCloseness());
        data.Encode1(pet.getFullness());
        if (item == null) {
            data.Encode8(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            data.EncodeBuffer(addExpirationTime(item.getExpiration() <= System.currentTimeMillis() ? -1 : item.getExpiration()));
        }
        if (pet.getPetItemId() == 5000054) {
            data.Encode4(0);
            data.Encode4(pet.getSecondsLeft() > 0 ? pet.getSecondsLeft() : 0); //in seconds, 3600 = 1 hr.
            data.Encode2(0);
        } else {
            data.Encode2(0);
            data.Encode8(item != null && item.getExpiration() <= System.currentTimeMillis() ? 0 : 1);
        }
        data.EncodeZeroBytes(5);
        return data.Get().getBytes();
    }

    public static final byte[] addSkillInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        final Map<ISkill, SkillEntry> skills = chr.getSkills();
        data.Encode2(skills.size());
        for (final Map.Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
            data.Encode4(skill.getKey().getId());
            data.Encode4(skill.getValue().skillevel);

            if (ServerConfig.version > 164) {
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
        if (ServerConfig.version > 131) {
            data.Encode2(0);
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
        return data.Get().getBytes();
    }

    public static final byte[] addRingInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        Pair<List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
        List<MapleRing> cRing = aRing.getLeft();
        data.Encode2(cRing.size());
        for (MapleRing ring : cRing) {
            data.Encode4(ring.getPartnerChrId());
            data.EncodeBuffer(ring.getPartnerName(), 13);
            data.Encode8(ring.getRingId());
            data.Encode8(ring.getPartnerRingId());
        }
        List<MapleRing> fRing = aRing.getRight();
        data.Encode2(fRing.size());
        for (MapleRing ring : fRing) {
            data.Encode4(ring.getPartnerChrId());
            data.EncodeBuffer(ring.getPartnerName(), 13);
            data.Encode8(ring.getRingId());
            data.Encode8(ring.getPartnerRingId());
            data.Encode4(ring.getItemId());
        }
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
        return data.Get().getBytes();
    }

    public static final byte[] addMonsterBookInfo(final MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        data.Encode4(chr.getMonsterBookCover());
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

        if (ServerConfig.version <= 131) {
            if (life.getStati().size() <= 1) {
                life.addEmpty(); //not done yet lulz ok so we add it now for the lulz
            }
        } else {
            if (life.getStati().size() <= 0) {
                life.addEmpty(); //not done yet lulz ok so we add it now for the lulz
            }
        }
        if (ServerConfig.version > 131) {
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
