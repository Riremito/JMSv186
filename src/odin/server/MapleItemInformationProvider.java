package odin.server;

import tacos.client.TacosCharacter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import odin.client.inventory.Item.ItemFlag;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import tacos.config.ContentCustom;
import tacos.config.ContentState;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.wz.WzXML;

public class MapleItemInformationProvider {

    private final static MapleItemInformationProvider instance = new MapleItemInformationProvider();
    private final Map<Integer, List<Integer>> scrollReqCache = new HashMap<>();
    private final Map<Integer, Short> slotMaxCache = new HashMap<>();
    private final Map<Integer, MapleStatEffect> itemEffects = new HashMap<>();
    private final Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<>();
    private final Map<Integer, Map<String, Byte>> itemMakeStatsCache = new HashMap<>();
    private final Map<Integer, Short> itemMakeLevel = new HashMap<>();
    private final Map<Integer, Double> priceCache = new HashMap<>();
    private final Map<Integer, Integer> wholePriceCache = new HashMap<>();
    private final Map<Integer, Integer> monsterBookID = new HashMap<>();
    private final Map<Integer, String> nameCache = new HashMap<>();
    private final Map<Integer, String> msgCache = new HashMap<>();
    private final Map<Integer, Map<String, Integer>> SkillStatsCache = new HashMap<>();
    private final Map<Integer, Byte> consumeOnPickupCache = new HashMap<>();
    private final Map<Integer, Boolean> dropRestrictionCache = new HashMap<>();
    private final Map<Integer, Boolean> accCache = new HashMap<>();
    private final Map<Integer, Boolean> pickupRestrictionCache = new HashMap<>();
    private final Map<Integer, Integer> stateChangeCache = new HashMap<>();
    private final Map<Integer, Integer> mesoCache = new HashMap<>();
    private final Map<Integer, Boolean> notSaleCache = new HashMap<>();
    private final Map<Integer, Boolean> blockPickupCache = new HashMap<>();
    private final Map<Integer, List<SimpleImmutableEntry<Integer, Integer>>> summonMobCache = new HashMap<>();
    private final Map<Integer, Map<Integer, Map<String, Integer>>> equipIncsCache = new HashMap<>();
    private final Map<Integer, Map<Integer, List<Integer>>> equipSkillsCache = new HashMap<>();
    private final Map<Integer, SimpleImmutableEntry<Integer, List<Integer>>> questItems = new HashMap<>();

    // equip stats by their wz name
    private enum EquipStat {
        STR("STR", Equip::getIncSTR, Equip::setStr),
        DEX("DEX", Equip::getIncDEX, Equip::setDex),
        INT("INT", Equip::getIncINT, Equip::setInt),
        LUK("LUK", Equip::getIncLUK, Equip::setLuk),
        PAD("PAD", Equip::getIncPAD, Equip::setWatk),
        PDD("PDD", Equip::getIncPDD, Equip::setWdef),
        MAD("MAD", Equip::getIncMAD, Equip::setMatk),
        MDD("MDD", Equip::getIncMDD, Equip::setMdef),
        ACC("ACC", Equip::getIncACC, Equip::setAcc),
        EVA("EVA", Equip::getIncEVA, Equip::setAvoid),
        SPEED("Speed", Equip::getIncSpeed, Equip::setSpeed),
        JUMP("Jump", Equip::getIncJump, Equip::setJump),
        MHP("MHP", Equip::getIncMaxHP, Equip::setHp),
        MMP("MMP", Equip::getIncMaxMP, Equip::setMp),
        MHPR("MHPr", Equip::getHpR, Equip::setHpR),
        MMPR("MMPr", Equip::getMpR, Equip::setMpR),
        CRAFT("Craft", Equip::getIncCraft, Equip::setHands);

        private final String name;
        private final ToIntFunction<Equip> getter;
        private final ObjIntConsumer<Equip> setter;

        EquipStat(String name, ToIntFunction<Equip> getter, ObjIntConsumer<Equip> setter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
        }

        public int get(Equip equip) {
            return getter.applyAsInt(equip);
        }

        public void set(Equip equip, int value) {
            setter.accept(equip, value);
        }

        public void add(Equip equip, int value) {
            set(equip, get(equip) + value);
        }

        public static EquipStat find(String name) {
            for (EquipStat stat : values()) {
                if (stat.name.equals(name)) {
                    return stat;
                }
            }
            return null;
        }
    }

    // the stats a chaos scroll changes
    private static final EquipStat[] CHAOS_STATS = {EquipStat.STR, EquipStat.DEX, EquipStat.INT, EquipStat.LUK, EquipStat.PAD, EquipStat.PDD, EquipStat.MAD, EquipStat.MDD, EquipStat.ACC, EquipStat.EVA, EquipStat.SPEED, EquipStat.JUMP, EquipStat.MHP, EquipStat.MMP};

    public static MapleItemInformationProvider getInstance() {
        return instance;
    }

    // the loader result is cached unless it is null
    private static <V> V cached(Map<Integer, V> cache, int itemId, IntFunction<V> loader) {
        V value = cache.get(itemId);
        if (value == null) {
            value = loader.apply(itemId);
            if (value != null) {
                cache.put(itemId, value);
            }
        }
        return value;
    }

    /**
     * returns the maximum of items in one slot
     */
    public short getSlotMax(int itemId) {
        return cached(slotMaxCache, itemId, WzXML.ITEM::loadSlotMax);
    }

    public int getWholePrice(int itemId) {
        return cached(wholePriceCache, itemId, WzXML.ITEM::loadWholePrice);
    }

    public double getPrice(int itemId) {
        return cached(priceCache, itemId, WzXML.ITEM::loadPrice);
    }

    public Map<String, Byte> getItemMakeStats(int itemId) {
        return cached(itemMakeStatsCache, itemId, WzXML.ITEM::loadItemMakeStats);
    }

    private int rand(int min, int max) {
        return Math.abs(Randomizer.rand(min, max));
    }

    // "STRMin" and "STRMax" etc, "MaxHPMin" is MHP
    public Equip levelUpEquip(Equip equip, Map<String, Integer> sta) {
        Equip nEquip = (Equip) equip.copy();
        for (Entry<String, Integer> stat : sta.entrySet()) {
            String key = stat.getKey();
            if (!key.endsWith("Min")) {
                continue;
            }
            String base = key.substring(0, key.length() - 3);
            Integer max = sta.get(base + "Max");
            EquipStat es = EquipStat.find(base.equals("MaxHP") ? "MHP" : base.equals("MaxMP") ? "MMP" : base);
            if (max == null || es == null || es == EquipStat.MHPR || es == EquipStat.MMPR || es == EquipStat.CRAFT) {
                continue;
            }
            es.add(nEquip, rand(stat.getValue(), max));
        }
        return nEquip;
    }

    public Map<Integer, Map<String, Integer>> getEquipIncrements(int itemId) {
        return cached(equipIncsCache, itemId, WzXML.ITEM::loadEquipIncrements);
    }

    public Map<Integer, List<Integer>> getEquipSkills(int itemId) {
        return cached(equipSkillsCache, itemId, WzXML.ITEM::loadEquipSkills);
    }

    public Map<String, Integer> getEquipStats(int itemId) {
        return cached(equipStatsCache, itemId, WzXML.ITEM::loadEquipStats);
    }

    public boolean canEquip(Map<String, Integer> stats, int itemid, int level, int job, int fame, int str, int dex, int luk, int int_, int supremacy) {
        if ((level + supremacy) >= stats.get("reqLevel") && str >= stats.get("reqSTR") && dex >= stats.get("reqDEX") && luk >= stats.get("reqLUK") && int_ >= stats.get("reqINT")) {
            int fameReq = stats.get("reqPOP");
            return fameReq == 0 || fame >= fameReq;
        }
        return false;
    }

    public int getReqLevel(int itemId) {
        Map<String, Integer> stats = getEquipStats(itemId);
        return (stats == null) ? 0 : stats.get("reqLevel");
    }

    public int getSlots(int itemId) {
        Map<String, Integer> stats = getEquipStats(itemId);
        return (stats == null) ? 0 : stats.get("tuc");
    }

    public List<Integer> getScrollReqs(int itemId) {
        return cached(scrollReqCache, itemId, WzXML.ITEM::loadScrollReqs);
    }

    public Item scrollEquipWithId(Item equip, Item scrollId, boolean ws, TacosCharacter chr, int vegas) {
        if (equip.getType() != 1) { // See Item.java
            return equip;
        }
        Equip nEquip = (Equip) equip;
        int scroll_id = scrollId.getItemId();
        Map<String, Integer> stats = getEquipStats(scroll_id);
        Map<String, Integer> eqstats = getEquipStats(equip.getItemId());
        boolean always_success = GameConstants.isEquipScroll(scroll_id) || GameConstants.isPotentialScroll(scroll_id);
        int succ = GameConstants.isTablet(scroll_id) ? GameConstants.getSuccessTablet(scroll_id, nEquip.getCUC()) : (always_success ? 0 : stats.get("success"));
        int curse = GameConstants.isTablet(scroll_id) ? GameConstants.getCurseTablet(scroll_id, nEquip.getCUC()) : (always_success ? 0 : stats.get("cursed"));
        int success = succ + (vegas == 5610000 && succ == 10 ? 20 : (vegas == 5610001 && succ == 60 ? 30 : 0));

        if (scroll_id == 2049100) {
            success = 100;
        }

        // slot consuming scroll (not clean slate, special, enhance or potential scroll)
        boolean uses_slot = !GameConstants.isCleanSlate(scroll_id) && !GameConstants.isSpecialScroll(scroll_id) && !always_success;

        if (always_success || Randomizer.nextInt(100) <= success) {
            applyScroll(nEquip, scroll_id, stats, eqstats);
            if (uses_slot) {
                nEquip.setUpgradeSlots((byte) (nEquip.getRUC() - 1));
                nEquip.setLevel((byte) (nEquip.getCUC() + 1));
            }
            return equip;
        }

        // failed
        if (!ws && uses_slot && !ContentState.CS_LOCK_LOSING_UPGRADE_SLOT.get()) {
            nEquip.setUpgradeSlots((byte) (nEquip.getRUC() - 1));
        }
        if (Randomizer.nextInt(99) < curse && !ContentState.CS_LOCK_BOOM.get()) {
            return null; // boom
        }
        return equip;
    }

    private void applyScroll(Equip nEquip, int scroll_id, Map<String, Integer> stats, Map<String, Integer> eqstats) {
        switch (scroll_id) {
            case 2049000:
            case 2049001:
            case 2049002:
            case 2049003:
            case 2049004:
            case 2049005: {
                addUpgradeSlots(nEquip, eqstats, 1);
                return;
            }
            case 2049006:
            case 2049007:
            case 2049008: {
                addUpgradeSlots(nEquip, eqstats, 2);
                return;
            }
            case 2040727: { // Spikes on shoe, prevents slip
                nEquip.setFlag((byte) (nEquip.getFlag() | ItemFlag.SPIKES.getValue()));
                return;
            }
            case 2041058: { // Cape for Cold protection
                nEquip.setFlag((byte) (nEquip.getFlag() | ItemFlag.COLD.getValue()));
                return;
            }
            default: {
                break;
            }
        }
        if (GameConstants.isChaosScroll(scroll_id)) {
            applyChaosScroll(nEquip, GameConstants.getChaosNumber(scroll_id));
        } else if (GameConstants.isEquipScroll(scroll_id)) {
            applyEnhanceScroll(nEquip);
        } else if (GameConstants.isPotentialScroll(scroll_id)) {
            if (nEquip.getHidden() == 0 && nEquip.getGrade() == 0) {
                nEquip.resetPotential(false, false);
            }
        } else {
            applyStatScroll(nEquip, stats);
        }
    }

    private void addUpgradeSlots(Equip nEquip, Map<String, Integer> eqstats, int slots) {
        if (nEquip.getCUC() + nEquip.getRUC() < eqstats.get("tuc")) {
            nEquip.setUpgradeSlots((byte) (nEquip.getRUC() + slots));
        }
    }

    // stat +- random(z)
    private void applyChaosScroll(Equip nEquip, int z) {
        for (EquipStat stat : CHAOS_STATS) {
            if (stat.get(nEquip) > 0) {
                stat.add(nEquip, Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1));
            }
        }
    }

    // +random(5), and 1/chance to add the stat the equip does not have
    private static void enhanceStat(Equip nEquip, EquipStat stat, int chance) {
        if (stat.get(nEquip) > 0 || Randomizer.nextInt(chance) == 1) {
            stat.add(nEquip, Randomizer.nextInt(5));
        }
    }

    // attack +2% (at least 1) on weapons
    private static void enhanceAttack(Equip nEquip, EquipStat stat) {
        int value = stat.get(nEquip);
        if (value > 0 && GameConstants.isWeapon(nEquip.getItemId())) {
            stat.add(nEquip, Math.max(value / 50, 1));
        }
    }

    // equip enhancement scroll (always success)
    private void applyEnhanceScroll(Equip nEquip) {
        enhanceStat(nEquip, EquipStat.STR, 50);
        enhanceStat(nEquip, EquipStat.DEX, 50);
        enhanceStat(nEquip, EquipStat.INT, 50);
        enhanceStat(nEquip, EquipStat.LUK, 50);
        enhanceAttack(nEquip, EquipStat.PAD);
        enhanceStat(nEquip, EquipStat.PDD, 40);
        enhanceAttack(nEquip, EquipStat.MAD);
        enhanceStat(nEquip, EquipStat.MDD, 40);
        enhanceStat(nEquip, EquipStat.ACC, 20);
        enhanceStat(nEquip, EquipStat.EVA, 20);
        enhanceStat(nEquip, EquipStat.SPEED, 10);
        enhanceStat(nEquip, EquipStat.JUMP, 10);
        enhanceStat(nEquip, EquipStat.MHP, 5);
        enhanceStat(nEquip, EquipStat.MMP, 5);
        nEquip.setEnhance((byte) (nEquip.getCHUC() + 1));
    }

    // normal scroll : add the stats of the scroll
    private void applyStatScroll(Equip nEquip, Map<String, Integer> stats) {
        for (Entry<String, Integer> stat : stats.entrySet()) {
            if (stat.getKey().equals("attackSpeed")) {
                nEquip.setIncAttackSpeed(nEquip.getIncAttackSpeed() + stat.getValue()); // 攻撃速度の書
                continue;
            }
            EquipStat es = EquipStat.find(stat.getKey());
            if (es != null && es != EquipStat.CRAFT) {
                es.add(nEquip, stat.getValue());
            }
        }
    }

    public Item getEquipById(int equipId) {
        return getEquipById(equipId, -1);
    }

    public Item getEquipById(int equipId, int ringId) {
        Equip nEquip = new Equip(equipId, (byte) 0, ringId, (byte) 0);
        nEquip.setQuantity((short) 1);
        Map<String, Integer> stats = getEquipStats(equipId);
        if (stats != null) {
            for (Entry<String, Integer> stat : stats.entrySet()) {
                String key = stat.getKey();
                EquipStat es = EquipStat.find(key);
                if (es != null) {
                    es.set(nEquip, stat.getValue());
                } else if (key.equals("tuc")) {
                    nEquip.setUpgradeSlots(stat.getValue().byteValue());
                } else if (key.equals("durability")) {
                    nEquip.setDurability(stat.getValue());
                }
            }
        }
        return nEquip.copy();
    }

    // vary no more than ceil of 10% of stat
    private static int getRandStat(int defaultValue, int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }
        int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);
        return (int) ((defaultValue - lMaxRange) + Math.floor(Math.random() * (lMaxRange * 2 + 1)));
    }

    private static void randomizeStat(Equip equip, EquipStat stat, int maxRange) {
        stat.set(equip, getRandStat(stat.get(equip), maxRange));
    }

    public Equip RireSabaStats(Equip equip) {
        int r = (int) Math.floor(Math.random() * 5) + 1;
        int min;
        int range;
        switch (r) {
            case 4: { // GOD
                min = 100;
                range = 1000;
                break;
            }
            case 3: { // Legend
                min = 20;
                range = 100;
                break;
            }
            case 2: { // Super
                min = 10;
                range = 10;
                break;
            }
            case 1: { // Rare
                min = 0;
                range = 10;
                break;
            }
            default: { // Normal
                return defaultRandomizeStats(equip);
            }
        }

        // Legend以上なら存在しない能力値も付与
        for (EquipStat stat : new EquipStat[]{EquipStat.STR, EquipStat.DEX, EquipStat.INT, EquipStat.LUK, EquipStat.PAD, EquipStat.MAD}) {
            int def = stat.get(equip);
            if (r >= 3 || def != 0) {
                stat.set(equip, (int) (def + min + Math.floor(Math.random() * range)));
            }
        }
        // 戦士優遇
        EquipStat.ACC.set(equip, (int) (equip.getIncACC() + min + Math.floor(Math.random() * range)));
        // 移動速度とジャンプ力は最大値に合わせる
        if (r >= 3 || equip.getIncSpeed() != 0) {
            EquipStat.SPEED.add(equip, 40);
        }
        if (r >= 3 || equip.getIncJump() != 0) {
            EquipStat.JUMP.add(equip, 23);
        }
        // その他
        randomizeStat(equip, EquipStat.EVA, 5);
        randomizeStat(equip, EquipStat.CRAFT, 5);
        randomizeStat(equip, EquipStat.PDD, 10);
        randomizeStat(equip, EquipStat.MDD, 10);
        randomizeStat(equip, EquipStat.MHP, 10);
        randomizeStat(equip, EquipStat.MMP, 10);
        return equip;
    }

    private Equip defaultRandomizeStats(Equip equip) {
        randomizeStat(equip, EquipStat.STR, 5);
        randomizeStat(equip, EquipStat.DEX, 5);
        randomizeStat(equip, EquipStat.INT, 5);
        randomizeStat(equip, EquipStat.LUK, 5);
        randomizeStat(equip, EquipStat.MAD, 5);
        randomizeStat(equip, EquipStat.PAD, 5);
        randomizeStat(equip, EquipStat.ACC, 5);
        randomizeStat(equip, EquipStat.EVA, 5);
        randomizeStat(equip, EquipStat.JUMP, 5);
        randomizeStat(equip, EquipStat.CRAFT, 5);
        randomizeStat(equip, EquipStat.SPEED, 5);
        randomizeStat(equip, EquipStat.PDD, 10);
        randomizeStat(equip, EquipStat.MDD, 10);
        randomizeStat(equip, EquipStat.MHP, 10);
        randomizeStat(equip, EquipStat.MMP, 10);
        return equip;
    }

    public Equip randomizeStats(Equip equip) {
        if (ContentCustom.CC_EQUIP_STAT_RANDOMIZER.get()) {
            return RireSabaStats(equip);
        }
        return defaultRandomizeStats(equip);
    }

    public MapleStatEffect getItemEffect(int itemId) {
        return cached(itemEffects, itemId, id -> !WzXML.ITEM.hasItemData(id) ? null : MapleStatEffect.loadItemEffectFromData(WzXML.ITEM.getItemSpec(id), id));
    }

    public List<SimpleImmutableEntry<Integer, Integer>> getSummonMobs(int itemId) {
        return cached(summonMobCache, itemId, WzXML.ITEM::loadSummonMobs);
    }

    public int getCardMobId(int id) {
        if (id == 0) {
            return 0;
        }
        // 0 (no mob) is not cached
        Integer mob_id = cached(monsterBookID, id, card_id -> {
            int ret = WzXML.ITEM.loadCardMobId(card_id);
            return (ret == 0) ? null : ret;
        });
        return (mob_id == null) ? 0 : mob_id;
    }

    public boolean canScroll(int scrollid, int itemid) {
        return (scrollid / 100) % 100 == (itemid / 10000) % 100;
    }

    public String getName(int itemId) {
        return cached(nameCache, itemId, WzXML.STRING::loadItemName);
    }

    public String getMsg(int itemId) {
        return cached(msgCache, itemId, WzXML.STRING::loadItemMsg);
    }

    public short getItemMakeLevel(int itemId) {
        return cached(itemMakeLevel, itemId, WzXML.ITEM::loadItemMakeLevel);
    }

    // 0 = not, 1 = consume on pickup, 2 = consume + party
    public byte isConsumeOnPickup(int itemId) {
        return cached(consumeOnPickupCache, itemId, WzXML.ITEM::loadConsumeOnPickup);
    }

    public boolean isDropRestricted(int itemId) {
        return cached(dropRestrictionCache, itemId, WzXML.ITEM::loadDropRestricted);
    }

    public boolean isPickupRestricted(int itemId) {
        return cached(pickupRestrictionCache, itemId, WzXML.ITEM::loadPickupRestricted);
    }

    public boolean isAccountShared(int itemId) {
        return cached(accCache, itemId, WzXML.ITEM::loadAccountShared);
    }

    public int getStateChangeItem(int itemId) {
        return cached(stateChangeCache, itemId, WzXML.ITEM::loadStateChangeItem);
    }

    public int getMeso(int itemId) {
        return cached(mesoCache, itemId, WzXML.ITEM::loadMeso);
    }

    // info/damaとか
    public int getInt(int itemId, String text) {
        return WzXML.ITEM.loadIntField(itemId, text);
    }

    public boolean isPickupBlocked(int itemId) {
        return cached(blockPickupCache, itemId, WzXML.ITEM::loadPickupBlocked);
    }

    // true = cant sell, false = can sell
    public boolean cantSell(int itemId) {
        return cached(notSaleCache, itemId, WzXML.ITEM::loadCantSell);
    }

    public Map<String, Integer> getSkillStats(int itemId) {
        return cached(SkillStatsCache, itemId, WzXML.ITEM::loadSkillStats);
    }

    public SimpleImmutableEntry<Integer, List<Integer>> questItemInfo(int itemId) {
        return cached(questItems, itemId, WzXML.ITEM::loadQuestItemInfo);
    }

    public boolean itemExists(int itemId) {
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.UNDEFINED) {
            return false;
        }
        return WzXML.ITEM.hasItemData(itemId);
    }

    public boolean isCash(int itemId) {
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH) {
            return true;
        }
        Map<String, Integer> stats = getEquipStats(itemId);
        return stats != null && stats.get("cash") > 0;
    }
}
