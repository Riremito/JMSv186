package odin.server;

import tacos.client.TacosCharacter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.server.TacosEnhancement;
import tacos.wz.WzDataStorage;
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
        boolean always_success = false;
        int succ = GameConstants.isTablet(scroll_id) ? GameConstants.getSuccessTablet(scroll_id, nEquip.getCUC()) : (always_success ? 0 : stats.get("success"));
        int curse = GameConstants.isTablet(scroll_id) ? GameConstants.getCurseTablet(scroll_id, nEquip.getCUC()) : (always_success ? 0 : stats.get("cursed"));
        int success = succ + (vegas == 5610000 && succ == 10 ? 20 : (vegas == 5610001 && succ == 60 ? 30 : 0));

        boolean uses_slot = !GameConstants.isCleanSlate(scroll_id) && !always_success;
        return equip;
    }

    public Equip getEquipById(int equip_id) {
        Equip ret = getDefaultEquipById(equip_id);
        TacosEnhancement ehancement = new TacosEnhancement(ret);
        ehancement.randomize();
        return ret;
    }

    public Equip getDefaultEquipById(int equip_id) {
        Equip ret = WzXML.CHARACTER.getEquipById(equip_id);
        return ret;
    }

    public MapleStatEffect getItemEffect(int itemId) {
        return cached(itemEffects, itemId, id -> !WzDataStorage.ITEM.check(id) ? null : MapleStatEffect.loadItemEffectFromData(WzXML.ITEM.getItemSpec(id), id));
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

    public boolean isCash(int itemId) {
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH) {
            return true;
        }
        Map<String, Integer> stats = getEquipStats(itemId);
        return stats != null && stats.get("cash") > 0;
    }
}
