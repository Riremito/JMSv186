package odin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import odin.client.inventory.ItemFlag;
import odin.constants.GameConstants;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import odin.client.inventory.MapleInventoryType;
import tacos.config.ContentCustom;
import tacos.config.ContentState;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.wz.MapleData;
import tacos.wz.WzXML;

public class MapleItemInformationProvider {

    private final static MapleItemInformationProvider instance = new MapleItemInformationProvider();
    protected final Map<Integer, List<Integer>> scrollReqCache = new HashMap<>();
    protected final Map<Integer, Short> slotMaxCache = new HashMap<>();
    protected final Map<Integer, MapleStatEffect> itemEffects = new HashMap<>();
    protected final Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<>();
    protected final Map<Integer, Map<String, Byte>> itemMakeStatsCache = new HashMap<>();
    protected final Map<Integer, Short> itemMakeLevel = new HashMap<>();
    protected final Map<Integer, Equip> equipCache = new HashMap<>();
    protected final Map<Integer, Double> priceCache = new HashMap<>();
    protected final Map<Integer, Integer> wholePriceCache = new HashMap<>();
    protected final Map<Integer, Integer> monsterBookID = new HashMap<>();
    protected final Map<Integer, String> nameCache = new HashMap<>();
    protected final Map<Integer, String> msgCache = new HashMap<>();
    protected final Map<Integer, Map<String, Integer>> SkillStatsCache = new HashMap<>();
    protected final Map<Integer, Byte> consumeOnPickupCache = new HashMap<>();
    protected final Map<Integer, Boolean> dropRestrictionCache = new HashMap<>();
    protected final Map<Integer, Boolean> accCache = new HashMap<>();
    protected final Map<Integer, Boolean> pickupRestrictionCache = new HashMap<>();
    protected final Map<Integer, Integer> stateChangeCache = new HashMap<>();
    protected final Map<Integer, Integer> mesoCache = new HashMap<>();
    protected final Map<Integer, Boolean> notSaleCache = new HashMap<>();
    protected final Map<Integer, Boolean> blockPickupCache = new HashMap<>();
    protected final Map<Integer, List<SimpleImmutableEntry<Integer, Integer>>> summonMobCache = new HashMap<>();
    protected final Map<Integer, Map<Integer, Map<String, Integer>>> equipIncsCache = new HashMap<>();
    protected final Map<Integer, Map<Integer, List<Integer>>> equipSkillsCache = new HashMap<>();
    protected Map<Integer, SimpleImmutableEntry<Integer, List<StructRewardItem>>> RewardItem = new HashMap<>();
    protected final Map<Integer, SimpleImmutableEntry<Integer, List<Integer>>> questItems = new HashMap<>();

    public static final MapleItemInformationProvider getInstance() {
        return instance;
    }

    protected final MapleData getStringData(final int itemId) {
        return WzXML.STRING.getItemStringData(itemId);
    }

    protected final MapleData getItemData(int id) {
        return WzXML.ITEM.getResolvedItemData(id);
    }

    /**
     * returns the maximum of items in one slot
     */
    public final short getSlotMax(final TacosClient client, final int itemId) {
        if (slotMaxCache.containsKey(itemId)) {
            return slotMaxCache.get(itemId);
        }
        short ret = WzXML.ITEM.loadSlotMax(itemId);
        slotMaxCache.put(itemId, ret);
        return ret;
    }

    public short getSlotMax(int item_id) {
        return getSlotMax(null, item_id);
    }

    public final int getWholePrice(final int itemId) {
        if (wholePriceCache.containsKey(itemId)) {
            return wholePriceCache.get(itemId);
        }
        final int pEntry = WzXML.ITEM.loadWholePrice(itemId);
        if (pEntry == -1) {
            return -1;
        }
        wholePriceCache.put(itemId, pEntry);
        return pEntry;
    }

    public final double getPrice(final int itemId) {
        if (priceCache.containsKey(itemId)) {
            return priceCache.get(itemId);
        }
        final double pEntry = WzXML.ITEM.loadPrice(itemId);
        if (pEntry == -1) {
            return -1;
        }
        priceCache.put(itemId, pEntry);
        return pEntry;
    }

    public final Map<String, Byte> getItemMakeStats(final int itemId) {
        if (itemMakeStatsCache.containsKey(itemId)) {
            return itemMakeStatsCache.get(itemId);
        }
        final Map<String, Byte> ret = WzXML.ITEM.loadItemMakeStats(itemId);
        if (ret == null) {
            return null;
        }
        itemMakeStatsCache.put(itemId, ret);
        return ret;
    }

    private int rand(int min, int max) {
        return Math.abs((int) Randomizer.rand(min, max));
    }

    public Equip levelUpEquip(Equip equip, Map<String, Integer> sta) {
        Equip nEquip = (Equip) equip.copy();
        //is this all the stats?
        try {
            for (Entry<String, Integer> stat : sta.entrySet()) {
                if (stat.getKey().equals("STRMin")) {
                    nEquip.setStr((short) (nEquip.getStr() + rand(stat.getValue().intValue(), sta.get("STRMax").intValue())));
                } else if (stat.getKey().equals("DEXMin")) {
                    nEquip.setDex((short) (nEquip.getDex() + rand(stat.getValue().intValue(), sta.get("DEXMax").intValue())));
                } else if (stat.getKey().equals("INTMin")) {
                    nEquip.setInt((short) (nEquip.getInt() + rand(stat.getValue().intValue(), sta.get("INTMax").intValue())));
                } else if (stat.getKey().equals("LUKMin")) {
                    nEquip.setLuk((short) (nEquip.getLuk() + rand(stat.getValue().intValue(), sta.get("LUKMax").intValue())));
                } else if (stat.getKey().equals("PADMin")) {
                    nEquip.setWatk((short) (nEquip.getWatk() + rand(stat.getValue().intValue(), sta.get("PADMax").intValue())));
                } else if (stat.getKey().equals("PDDMin")) {
                    nEquip.setWdef((short) (nEquip.getWdef() + rand(stat.getValue().intValue(), sta.get("PDDMax").intValue())));
                } else if (stat.getKey().equals("MADMin")) {
                    nEquip.setMatk((short) (nEquip.getMatk() + rand(stat.getValue().intValue(), sta.get("MADMax").intValue())));
                } else if (stat.getKey().equals("MDDMin")) {
                    nEquip.setMdef((short) (nEquip.getMdef() + rand(stat.getValue().intValue(), sta.get("MDDMax").intValue())));
                } else if (stat.getKey().equals("ACCMin")) {
                    nEquip.setAcc((short) (nEquip.getAcc() + rand(stat.getValue().intValue(), sta.get("ACCMax").intValue())));
                } else if (stat.getKey().equals("EVAMin")) {
                    nEquip.setAvoid((short) (nEquip.getAvoid() + rand(stat.getValue().intValue(), sta.get("EVAMax").intValue())));
                } else if (stat.getKey().equals("SpeedMin")) {
                    nEquip.setSpeed((short) (nEquip.getSpeed() + rand(stat.getValue().intValue(), sta.get("SpeedMax").intValue())));
                } else if (stat.getKey().equals("JumpMin")) {
                    nEquip.setJump((short) (nEquip.getJump() + rand(stat.getValue().intValue(), sta.get("JumpMax").intValue())));
                } else if (stat.getKey().equals("MHPMin")) {
                    nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue().intValue(), sta.get("MHPMax").intValue())));
                } else if (stat.getKey().equals("MMPMin")) {
                    nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue().intValue(), sta.get("MMPMax").intValue())));
                } else if (stat.getKey().equals("MaxHPMin")) {
                    nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue().intValue(), sta.get("MaxHPMax").intValue())));
                } else if (stat.getKey().equals("MaxMPMin")) {
                    nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue().intValue(), sta.get("MaxMPMax").intValue())));
                }
            }
        } catch (NullPointerException e) {
            //catch npe because obviously the wz have some error XD
            e.printStackTrace();
        }
        return nEquip;
    }

    public final Map<Integer, Map<String, Integer>> getEquipIncrements(final int itemId) {
        if (equipIncsCache.containsKey(itemId)) {
            return equipIncsCache.get(itemId);
        }
        final Map<Integer, Map<String, Integer>> ret = WzXML.ITEM.loadEquipIncrements(itemId);
        if (ret == null) {
            return null;
        }
        equipIncsCache.put(itemId, ret);
        return ret;
    }

    public final Map<Integer, List<Integer>> getEquipSkills(final int itemId) {
        if (equipSkillsCache.containsKey(itemId)) {
            return equipSkillsCache.get(itemId);
        }
        final Map<Integer, List<Integer>> ret = WzXML.ITEM.loadEquipSkills(itemId);
        if (ret == null) {
            return null;
        }
        equipSkillsCache.put(itemId, ret);
        return ret;
    }

    public final Map<String, Integer> getEquipStats(int itemId) {
        if (equipStatsCache.containsKey(itemId)) {
            return equipStatsCache.get(itemId);
        }
        final Map<String, Integer> ret = WzXML.ITEM.loadEquipStats(itemId);
        if (ret == null) {
            return null;
        }
        equipStatsCache.put(itemId, ret);
        return ret;
    }

    public final boolean canEquip(final Map<String, Integer> stats, final int itemid, final int level, final int job, final int fame, final int str, final int dex, final int luk, final int int_, final int supremacy) {
        if ((level + supremacy) >= stats.get("reqLevel") && str >= stats.get("reqSTR") && dex >= stats.get("reqDEX") && luk >= stats.get("reqLUK") && int_ >= stats.get("reqINT")) {
            final int fameReq = stats.get("reqPOP");
            if (fameReq != 0 && fame < fameReq) {
                return false;
            }
            return true;
        }
        return false;
    }

    public final int getReqLevel(final int itemId) {
        if (getEquipStats(itemId) == null) {
            return 0;
        }
        return getEquipStats(itemId).get("reqLevel");
    }

    public final int getSlots(final int itemId) {
        if (getEquipStats(itemId) == null) {
            return 0;
        }
        return getEquipStats(itemId).get("tuc");
    }

    public final int getSetItemID(final int itemId) {
        if (getEquipStats(itemId) == null) {
            return 0;
        }
        return getEquipStats(itemId).get("setItemID");
    }

    public final List<Integer> getScrollReqs(final int itemId) {
        if (scrollReqCache.containsKey(itemId)) {
            return scrollReqCache.get(itemId);
        }
        final List<Integer> ret = WzXML.ITEM.loadScrollReqs(itemId);
        scrollReqCache.put(itemId, ret);
        return ret;
    }

    public final Item scrollEquipWithId(final Item equip, final Item scrollId, final boolean ws, final MapleCharacter chr, final int vegas) {
        if (equip.getType() == 1) { // See Item.java
            final Equip nEquip = (Equip) equip;
            final Map<String, Integer> stats = getEquipStats(scrollId.getItemId());
            final Map<String, Integer> eqstats = getEquipStats(equip.getItemId());
            final int succ = (GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getSuccessTablet(scrollId.getItemId(), nEquip.getLevel()) : ((GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()) ? 0 : stats.get("success"))));
            final int curse = (GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getCurseTablet(scrollId.getItemId(), nEquip.getLevel()) : ((GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()) ? 0 : stats.get("cursed"))));
            int success = succ + (vegas == 5610000 && succ == 10 ? 20 : (vegas == 5610001 && succ == 60 ? 30 : 0));

            if (scrollId.getItemId() == 2049100) {
                success = 100;
            }

            if (GameConstants.isPotentialScroll(scrollId.getItemId()) || GameConstants.isEquipScroll(scrollId.getItemId()) || Randomizer.nextInt(100) <= success) {
                switch (scrollId.getItemId()) {
                    case 2049000:
                    case 2049001:
                    case 2049002:
                    case 2049003:
                    case 2049004:
                    case 2049005: {
                        if (nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                            nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 1));
                        }
                        break;
                    }
                    case 2049006:
                    case 2049007:
                    case 2049008: {
                        if (nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                            nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 2));
                        }
                        break;
                    }
                    case 2040727: // Spikes on shoe, prevents slip
                    {
                        byte flag = nEquip.getFlag();
                        flag |= ItemFlag.SPIKES.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2041058: // Cape for Cold protection
                    {
                        byte flag = nEquip.getFlag();
                        flag |= ItemFlag.COLD.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    default: {
                        if (GameConstants.isChaosScroll(scrollId.getItemId())) {
                            final int z = GameConstants.getChaosNumber(scrollId.getItemId());
                            if (nEquip.getStr() > 0) {
                                nEquip.setStr((short) (nEquip.getStr() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getDex() > 0) {
                                nEquip.setDex((short) (nEquip.getDex() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getInt() > 0) {
                                nEquip.setInt((short) (nEquip.getInt() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getLuk() > 0) {
                                nEquip.setLuk((short) (nEquip.getLuk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getWatk() > 0) {
                                nEquip.setWatk((short) (nEquip.getWatk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getWdef() > 0) {
                                nEquip.setWdef((short) (nEquip.getWdef() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMatk() > 0) {
                                nEquip.setMatk((short) (nEquip.getMatk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMdef() > 0) {
                                nEquip.setMdef((short) (nEquip.getMdef() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getAcc() > 0) {
                                nEquip.setAcc((short) (nEquip.getAcc() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getAvoid() > 0) {
                                nEquip.setAvoid((short) (nEquip.getAvoid() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getSpeed() > 0) {
                                nEquip.setSpeed((short) (nEquip.getSpeed() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getJump() > 0) {
                                nEquip.setJump((short) (nEquip.getJump() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getHp() > 0) {
                                nEquip.setHp((short) (nEquip.getHp() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMp() > 0) {
                                nEquip.setMp((short) (nEquip.getMp() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            break;
                        } else if (GameConstants.isEquipScroll(scrollId.getItemId())) {
                            final int chanc = Math.max((scrollId.getItemId() == 2049300 ? 100 : 80) - (nEquip.getEnhance() * 10), 10);
                            // all success
                            /*
                            if (Randomizer.nextInt(100) > chanc) {
                                return null; //destroyed, nib
                            }
                             */
                            if (nEquip.getStr() > 0 || Randomizer.nextInt(50) == 1) { //1/50
                                nEquip.setStr((short) (nEquip.getStr() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getDex() > 0 || Randomizer.nextInt(50) == 1) { //1/50
                                nEquip.setDex((short) (nEquip.getDex() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getInt() > 0 || Randomizer.nextInt(50) == 1) { //1/50
                                nEquip.setInt((short) (nEquip.getInt() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getLuk() > 0 || Randomizer.nextInt(50) == 1) { //1/50
                                nEquip.setLuk((short) (nEquip.getLuk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getWatk() > 0 && GameConstants.isWeapon(nEquip.getItemId())) {
                                int add = (nEquip.getWatk() / 50);
                                if (add == 0) {
                                    add = 1;
                                }
                                nEquip.setWatk((short) (nEquip.getWatk() + add));
                            }
                            if (nEquip.getWdef() > 0 || Randomizer.nextInt(40) == 1) { //1/40
                                nEquip.setWdef((short) (nEquip.getWdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMatk() > 0 && GameConstants.isWeapon(nEquip.getItemId())) {
                                int add = (nEquip.getMatk() / 50);
                                if (add == 0) {
                                    add = 1;
                                }
                                nEquip.setMatk((short) (nEquip.getMatk() + add));
                            }
                            if (nEquip.getMdef() > 0 || Randomizer.nextInt(40) == 1) { //1/40
                                nEquip.setMdef((short) (nEquip.getMdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAcc() > 0 || Randomizer.nextInt(20) == 1) { //1/20
                                nEquip.setAcc((short) (nEquip.getAcc() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAvoid() > 0 || Randomizer.nextInt(20) == 1) { //1/20
                                nEquip.setAvoid((short) (nEquip.getAvoid() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getSpeed() > 0 || Randomizer.nextInt(10) == 1) { //1/10
                                nEquip.setSpeed((short) (nEquip.getSpeed() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getJump() > 0 || Randomizer.nextInt(10) == 1) { //1/10
                                nEquip.setJump((short) (nEquip.getJump() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getHp() > 0 || Randomizer.nextInt(5) == 1) { //1/5
                                nEquip.setHp((short) (nEquip.getHp() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMp() > 0 || Randomizer.nextInt(5) == 1) { //1/5
                                nEquip.setMp((short) (nEquip.getMp() + Randomizer.nextInt(5)));
                            }
                            nEquip.setEnhance((byte) (nEquip.getEnhance() + 1));
                            break;
                        } else if (GameConstants.isPotentialScroll(scrollId.getItemId())) {
                            if (nEquip.getHidden() == 0 && nEquip.getRank() == 0) {
                                final int chanc = scrollId.getItemId() == 2049400 ? 90 : 70;
                                //if (Randomizer.nextInt(100) > chanc) {
                                //    return null; //destroyed, nib
                                //}
                                nEquip.resetPotential(false, false);
                            }
                            break;
                        } else {
                            for (Entry<String, Integer> stat : stats.entrySet()) {
                                final String key = stat.getKey();

                                if (key.equals("STR")) {
                                    nEquip.setStr((short) (nEquip.getStr() + stat.getValue().intValue()));
                                } else if (key.equals("DEX")) {
                                    nEquip.setDex((short) (nEquip.getDex() + stat.getValue().intValue()));
                                } else if (key.equals("INT")) {
                                    nEquip.setInt((short) (nEquip.getInt() + stat.getValue().intValue()));
                                } else if (key.equals("LUK")) {
                                    nEquip.setLuk((short) (nEquip.getLuk() + stat.getValue().intValue()));
                                } else if (key.equals("PAD")) {
                                    nEquip.setWatk((short) (nEquip.getWatk() + stat.getValue().intValue()));
                                } else if (key.equals("PDD")) {
                                    nEquip.setWdef((short) (nEquip.getWdef() + stat.getValue().intValue()));
                                } else if (key.equals("MAD")) {
                                    nEquip.setMatk((short) (nEquip.getMatk() + stat.getValue().intValue()));
                                } else if (key.equals("MDD")) {
                                    nEquip.setMdef((short) (nEquip.getMdef() + stat.getValue().intValue()));
                                } else if (key.equals("ACC")) {
                                    nEquip.setAcc((short) (nEquip.getAcc() + stat.getValue().intValue()));
                                } else if (key.equals("EVA")) {
                                    nEquip.setAvoid((short) (nEquip.getAvoid() + stat.getValue().intValue()));
                                } else if (key.equals("Speed")) {
                                    nEquip.setSpeed((short) (nEquip.getSpeed() + stat.getValue().intValue()));
                                } else if (key.equals("Jump")) {
                                    nEquip.setJump((short) (nEquip.getJump() + stat.getValue().intValue()));
                                } else if (key.equals("MHP")) {
                                    nEquip.setHp((short) (nEquip.getHp() + stat.getValue().intValue()));
                                } else if (key.equals("MMP")) {
                                    nEquip.setMp((short) (nEquip.getMp() + stat.getValue().intValue()));
                                } else if (key.equals("MHPr")) {
                                    nEquip.setHpR((short) (nEquip.getHpR() + stat.getValue().intValue()));
                                } else if (key.equals("MMPr")) {
                                    nEquip.setMpR((short) (nEquip.getMpR() + stat.getValue().intValue()));
                                    // 攻撃速度の書
                                } else if (key.equals("attackSpeed")) {
                                    nEquip.setIncAttackSpeed(nEquip.getIncAttackSpeed() + stat.getValue().intValue());
                                }
                            }
                            break;
                        }
                    }
                }
                if (!GameConstants.isCleanSlate(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isPotentialScroll(scrollId.getItemId())) {
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                    nEquip.setLevel((byte) (nEquip.getLevel() + 1));
                }
            } else {
                if (!ws && !GameConstants.isCleanSlate(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isPotentialScroll(scrollId.getItemId())) {
                    if (ContentState.CS_LOCK_LOSING_UPGRADE_SLOT.get()) {
                        // do nothing
                    } else {
                        nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                    }
                }
                if (Randomizer.nextInt(99) < curse) {
                    if (ContentState.CS_LOCK_BOOM.get()) {
                        // do nothing
                    } else {
                        // boom
                        return null;
                    }
                }
            }
        }
        return equip;
    }

    public final Item getEquipById(int equipId) {
        return getEquipById(equipId, -1);
    }

    public final Item getEquipById(final int equipId, final int ringId) {
        final Equip nEquip = new Equip(equipId, (byte) 0, ringId, (byte) 0);
        nEquip.setQuantity((short) 1);
        final Map<String, Integer> stats = getEquipStats(equipId);
        if (stats != null) {
            for (Entry<String, Integer> stat : stats.entrySet()) {
                final String key = stat.getKey();

                if (key.equals("STR")) {
                    nEquip.setStr((short) stat.getValue().intValue());
                } else if (key.equals("DEX")) {
                    nEquip.setDex((short) stat.getValue().intValue());
                } else if (key.equals("INT")) {
                    nEquip.setInt((short) stat.getValue().intValue());
                } else if (key.equals("LUK")) {
                    nEquip.setLuk((short) stat.getValue().intValue());
                } else if (key.equals("PAD")) {
                    nEquip.setWatk((short) stat.getValue().intValue());
                } else if (key.equals("PDD")) {
                    nEquip.setWdef((short) stat.getValue().intValue());
                } else if (key.equals("MAD")) {
                    nEquip.setMatk((short) stat.getValue().intValue());
                } else if (key.equals("MDD")) {
                    nEquip.setMdef((short) stat.getValue().intValue());
                } else if (key.equals("ACC")) {
                    nEquip.setAcc((short) stat.getValue().intValue());
                } else if (key.equals("EVA")) {
                    nEquip.setAvoid((short) stat.getValue().intValue());
                } else if (key.equals("Speed")) {
                    nEquip.setSpeed((short) stat.getValue().intValue());
                } else if (key.equals("Jump")) {
                    nEquip.setJump((short) stat.getValue().intValue());
                } else if (key.equals("MHP")) {
                    nEquip.setHp((short) stat.getValue().intValue());
                } else if (key.equals("MMP")) {
                    nEquip.setMp((short) stat.getValue().intValue());
                } else if (key.equals("MHPr")) {
                    nEquip.setHpR((short) stat.getValue().intValue());
                } else if (key.equals("MMPr")) {
                    nEquip.setMpR((short) stat.getValue().intValue());
                } else if (key.equals("tuc")) {
                    nEquip.setUpgradeSlots(stat.getValue().byteValue());
                } else if (key.equals("Craft")) {
                    nEquip.setHands(stat.getValue().shortValue());
                } else if (key.equals("durability")) {
                    nEquip.setDurability(stat.getValue());
                }
            }
        }
        equipCache.put(equipId, nEquip);
        return nEquip.copy();
    }

    private final short getRandStat(final short defaultValue, final int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }
        // vary no more than ceil of 10% of stat
        final int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);

        return (short) ((defaultValue - lMaxRange) + Math.floor(Math.random() * (lMaxRange * 2 + 1)));
    }

    public final Equip RireSabaStats(final Equip equip) {
        int r = (int) Math.floor(Math.random() * 5) + 1;
        int min = 0;
        int range = 5;

        // GOD
        if (r == 4) {
            min = 100;
            range = 1000;
        } // Legend
        else if (r == 3) {
            min = 20;
            range = 100;
        } // Super
        else if (r == 2) {
            min = 10;
            range = 10;
        } // Rare
        else if (r == 1) {
            min = 0;
            range = 10;
        } // Normal
        else {
            equip.setStr(getRandStat((short) equip.getStr(), 5));
            equip.setDex(getRandStat((short) equip.getDex(), 5));
            equip.setInt(getRandStat((short) equip.getInt(), 5));
            equip.setLuk(getRandStat((short) equip.getLuk(), 5));
            equip.setMatk(getRandStat((short) equip.getMatk(), 5));
            equip.setWatk(getRandStat((short) equip.getWatk(), 5));
            equip.setAcc(getRandStat((short) equip.getAcc(), 5));
            equip.setAvoid(getRandStat((short) equip.getAvoid(), 5));
            equip.setJump(getRandStat((short) equip.getJump(), 5));
            equip.setHands(getRandStat((short) equip.getHands(), 5));
            equip.setSpeed(getRandStat((short) equip.getSpeed(), 5));
            equip.setWdef(getRandStat((short) equip.getWdef(), 10));
            equip.setMdef(getRandStat((short) equip.getMdef(), 10));
            equip.setHp(getRandStat((short) equip.getHp(), 10));
            equip.setMp(getRandStat((short) equip.getMp(), 10));
            return equip;
        }

        // Legend以上なら存在しない能力値も付与
        int def = equip.getStr();
        if (r >= 3 || def != 0) {
            equip.setStr((short) (def + min + Math.floor(Math.random() * range)));
        }
        def = equip.getDex();
        if (r >= 3 || def != 0) {
            equip.setDex((short) (def + min + Math.floor(Math.random() * range)));
        }
        def = equip.getInt();
        if (r >= 3 || def != 0) {
            equip.setInt((short) (def + min + Math.floor(Math.random() * range)));
        }
        def = equip.getLuk();
        if (r >= 3 || def != 0) {
            equip.setLuk((short) (def + min + Math.floor(Math.random() * range)));
        }
        def = equip.getWatk();
        if (r >= 3 || def != 0) {
            equip.setWatk((short) (def + min + Math.floor(Math.random() * range)));
        }
        def = equip.getMatk();
        if (r >= 3 || def != 0) {
            equip.setMatk((short) (def + min + Math.floor(Math.random() * range)));
        }
        // 戦士優遇
        def = equip.getAcc();
        if (r >= 1 || def != 0) {
            equip.setAcc((short) (def + min + Math.floor(Math.random() * range)));
        }
        // 移動速度とジャンプ力は最大値に合わせる
        def = equip.getSpeed();
        if (r >= 3 || def != 0) {
            equip.setSpeed((short) (def + 40));
        }
        def = equip.getJump();
        if (r >= 3 || def != 0) {
            equip.setJump((short) (def + 23));
        }
        // その他
        equip.setAvoid(getRandStat((short) equip.getAvoid(), 5));
        equip.setHands(getRandStat((short) equip.getHands(), 5));
        equip.setWdef(getRandStat((short) equip.getWdef(), 10));
        equip.setMdef(getRandStat((short) equip.getMdef(), 10));
        equip.setHp(getRandStat((short) equip.getHp(), 10));
        equip.setMp(getRandStat((short) equip.getMp(), 10));
        return equip;
    }

    public final Equip randomizeStats(Equip equip) {
        if (ContentCustom.CC_EQUIP_STAT_RANDOMIZER.get()) {
            return RireSabaStats(equip);
        }

        equip.setStr(getRandStat((short) equip.getStr(), 5));
        equip.setDex(getRandStat((short) equip.getDex(), 5));
        equip.setInt(getRandStat((short) equip.getInt(), 5));
        equip.setLuk(getRandStat((short) equip.getLuk(), 5));
        equip.setMatk(getRandStat((short) equip.getMatk(), 5));
        equip.setWatk(getRandStat((short) equip.getWatk(), 5));
        equip.setAcc(getRandStat((short) equip.getAcc(), 5));
        equip.setAvoid(getRandStat((short) equip.getAvoid(), 5));
        equip.setJump(getRandStat((short) equip.getJump(), 5));
        equip.setHands(getRandStat((short) equip.getHands(), 5));
        equip.setSpeed(getRandStat((short) equip.getSpeed(), 5));
        equip.setWdef(getRandStat((short) equip.getWdef(), 10));
        equip.setMdef(getRandStat((short) equip.getMdef(), 10));
        equip.setHp(getRandStat((short) equip.getHp(), 10));
        equip.setMp(getRandStat((short) equip.getMp(), 10));
        return equip;
    }

    public MapleStatEffect getItemEffect(int itemId) {
        MapleStatEffect ret = itemEffects.get(itemId);
        if (ret == null) {
            MapleData item = getItemData(itemId);
            if (item == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
            itemEffects.put(itemId, ret);
        }
        return ret;
    }

    public final List<SimpleImmutableEntry<Integer, Integer>> getSummonMobs(final int itemId) {
        if (summonMobCache.containsKey(itemId)) {
            return summonMobCache.get(itemId);
        }
        final List<SimpleImmutableEntry<Integer, Integer>> mobPairs = WzXML.ITEM.loadSummonMobs(itemId);
        if (mobPairs == null) {
            return null;
        }
        summonMobCache.put(itemId, mobPairs);
        return mobPairs;
    }

    public int getCardMobId(int id) {
        if (id == 0) {
            return 0;
        }
        if (monsterBookID.containsKey(id)) {
            return monsterBookID.get(id);
        }
        int monsterid = WzXML.ITEM.loadCardMobId(id);

        if (monsterid == 0) { // Hack.
            return 0;
        }
        monsterBookID.put(id, monsterid);
        return monsterBookID.get(id);
    }

    public final boolean canScroll(final int scrollid, final int itemid) {
        return (scrollid / 100) % 100 == (itemid / 10000) % 100;
    }

    public final String getName(final int itemId) {
        if (nameCache.containsKey(itemId)) {
            return nameCache.get(itemId);
        }
        final String ret = WzXML.STRING.loadItemName(itemId);
        nameCache.put(itemId, ret);
        return ret;
    }

    public final String getMsg(final int itemId) {
        if (msgCache.containsKey(itemId)) {
            return msgCache.get(itemId);
        }
        final String ret = WzXML.STRING.loadItemMsg(itemId);
        msgCache.put(itemId, ret);
        return ret;
    }

    public final short getItemMakeLevel(final int itemId) {
        if (itemMakeLevel.containsKey(itemId)) {
            return itemMakeLevel.get(itemId);
        }
        final short lvl = WzXML.ITEM.loadItemMakeLevel(itemId);
        itemMakeLevel.put(itemId, lvl);
        return lvl;
    }

    public byte isConsumeOnPickup(int itemId) {
        // 0 = not, 1 = consume on pickup, 2 = consume + party
        if (consumeOnPickupCache.containsKey(itemId)) {
            return consumeOnPickupCache.get(itemId);
        }
        final byte consume = WzXML.ITEM.loadConsumeOnPickup(itemId);
        consumeOnPickupCache.put(itemId, consume);
        return consume;
    }

    public final boolean isDropRestricted(final int itemId) {
        if (dropRestrictionCache.containsKey(itemId)) {
            return dropRestrictionCache.get(itemId);
        }
        final boolean trade = WzXML.ITEM.loadDropRestricted(itemId);
        dropRestrictionCache.put(itemId, trade);
        return trade;
    }

    public final boolean isPickupRestricted(final int itemId) {
        if (pickupRestrictionCache.containsKey(itemId)) {
            return pickupRestrictionCache.get(itemId);
        }
        final boolean bRestricted = WzXML.ITEM.loadPickupRestricted(itemId);

        pickupRestrictionCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public final boolean isAccountShared(final int itemId) {
        if (accCache.containsKey(itemId)) {
            return accCache.get(itemId);
        }
        final boolean bRestricted = WzXML.ITEM.loadAccountShared(itemId);

        accCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public final int getStateChangeItem(final int itemId) {
        if (stateChangeCache.containsKey(itemId)) {
            return stateChangeCache.get(itemId);
        }
        final int triggerItem = WzXML.ITEM.loadStateChangeItem(itemId);
        stateChangeCache.put(itemId, triggerItem);
        return triggerItem;
    }

    public final int getMeso(final int itemId) {
        if (mesoCache.containsKey(itemId)) {
            return mesoCache.get(itemId);
        }
        final int triggerItem = WzXML.ITEM.loadMeso(itemId);
        mesoCache.put(itemId, triggerItem);
        return triggerItem;
    }

    // info/damaとか
    public final int getInt(final int itemId, final String text) {
        return WzXML.ITEM.loadIntField(itemId, text);
    }

    public final boolean isPickupBlocked(final int itemId) {
        if (blockPickupCache.containsKey(itemId)) {
            return blockPickupCache.get(itemId);
        }
        final boolean iRestricted = WzXML.ITEM.loadPickupBlocked(itemId);

        blockPickupCache.put(itemId, iRestricted);
        return iRestricted;
    }

    public final boolean cantSell(final int itemId) { //true = cant sell, false = can sell
        if (notSaleCache.containsKey(itemId)) {
            return notSaleCache.get(itemId);
        }
        final boolean bRestricted = WzXML.ITEM.loadCantSell(itemId);

        notSaleCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public SimpleImmutableEntry<Integer, List<StructRewardItem>> getRewardItem(final int itemid) {
        if (RewardItem.containsKey(itemid)) {
            return RewardItem.get(itemid);
        }
        final SimpleImmutableEntry<Integer, List<StructRewardItem>> toreturn = WzXML.ITEM.loadRewardItem(itemid);
        if (toreturn == null) {
            return null;
        }
        RewardItem.put(itemid, toreturn);
        return toreturn;
    }

    public final Map<String, Integer> getSkillStats(final int itemId) {
        if (SkillStatsCache.containsKey(itemId)) {
            return SkillStatsCache.get(itemId);
        }
        final Map<String, Integer> ret = WzXML.ITEM.loadSkillStats(itemId);
        if (ret == null) {
            return null;
        }
        SkillStatsCache.put(itemId, ret);
        return ret;
    }

    public SimpleImmutableEntry<Integer, List<Integer>> questItemInfo(int itemId) {
        if (questItems.containsKey(itemId)) {
            return questItems.get(itemId);
        }
        final SimpleImmutableEntry<Integer, List<Integer>> questItem = WzXML.ITEM.loadQuestItemInfo(itemId);
        if (questItem == null) {
            return null;
        }
        questItems.put(itemId, questItem);
        return questItem;
    }

    public final boolean itemExists(final int itemId) {
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.UNDEFINED) {
            return false;
        }
        return getItemData(itemId) != null;
    }

    public final boolean isCash(final int itemId) {
        if (getEquipStats(itemId) == null) {
            return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH;
        }
        return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || getEquipStats(itemId).get("cash") > 0;
    }

    // パチンコ
    // CMS v72から流用
    /*
    public MapleInventoryType getInventoryTypeCS(int itemId) {
        if (inventoryTypeCache.containsKey(itemId)) {
            return inventoryTypeCache.get(itemId);
        }
        MapleInventoryType ret;
        String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = itemData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = MapleInventoryType.getByWZName(topDir.getName());
                    inventoryTypeCache.put(itemId, ret);
                    return ret;
                } else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                    ret = MapleInventoryType.getByWZName(topDir.getName());
                    inventoryTypeCache.put(itemId, ret);
                    return ret;
                }
            }
        }
        root = chrData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr + ".img")) {
                    ret = MapleInventoryType.EQUIP;
                    inventoryTypeCache.put(itemId, ret);
                    return ret;
                }
            }
        }
        ret = MapleInventoryType.UNDEFINED;
        inventoryTypeCache.put(itemId, ret);
        return ret;
    }
     */
}
