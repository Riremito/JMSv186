package odin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.ItemFlag;
import odin.constants.GameConstants;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import odin.client.inventory.MapleInventoryType;
import tacos.config.ContentCustom;
import tacos.config.ContentState;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPair;
import odin.provider.IMapleData;
import tacos.wz.WzDataTool;
import tacos.wz.WzXML;

public class MapleItemInformationProvider {

    private final static MapleItemInformationProvider instance = new MapleItemInformationProvider();
    protected final Map<Integer, List<Integer>> scrollReqCache = new HashMap<Integer, List<Integer>>();
    protected final Map<Integer, Short> slotMaxCache = new HashMap<Integer, Short>();
    protected final Map<Integer, MapleStatEffect> itemEffects = new HashMap<Integer, MapleStatEffect>();
    protected final Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<Integer, Map<String, Integer>>();
    protected final Map<Integer, Map<String, Byte>> itemMakeStatsCache = new HashMap<Integer, Map<String, Byte>>();
    protected final Map<Integer, Short> itemMakeLevel = new HashMap<Integer, Short>();
    protected final Map<Integer, Equip> equipCache = new HashMap<Integer, Equip>();
    protected final Map<Integer, Double> priceCache = new HashMap<Integer, Double>();
    protected final Map<Integer, Integer> wholePriceCache = new HashMap<Integer, Integer>();
    protected final Map<Integer, Integer> monsterBookID = new HashMap<Integer, Integer>();
    protected final Map<Integer, String> nameCache = new HashMap<Integer, String>();
    protected final Map<Integer, String> msgCache = new HashMap<Integer, String>();
    protected final Map<Integer, Map<String, Integer>> SkillStatsCache = new HashMap<Integer, Map<String, Integer>>();
    protected final Map<Integer, Byte> consumeOnPickupCache = new HashMap<Integer, Byte>();
    protected final Map<Integer, Boolean> dropRestrictionCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, Boolean> accCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, Boolean> pickupRestrictionCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, Integer> stateChangeCache = new HashMap<Integer, Integer>();
    protected final Map<Integer, Integer> mesoCache = new HashMap<Integer, Integer>();
    protected final Map<Integer, Boolean> notSaleCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, Integer> karmaEnabledCache = new HashMap<Integer, Integer>();
    protected final Map<Integer, Boolean> blockPickupCache = new HashMap<Integer, Boolean>();
    protected final Map<Integer, List<Integer>> petsCanConsumeCache = new HashMap<Integer, List<Integer>>();
    protected final Map<Integer, List<OdinPair<Integer, Integer>>> summonMobCache = new HashMap<Integer, List<OdinPair<Integer, Integer>>>();
    protected final Map<Integer, Map<Integer, Map<String, Integer>>> equipIncsCache = new HashMap<Integer, Map<Integer, Map<String, Integer>>>();
    protected final Map<Integer, Map<Integer, List<Integer>>> equipSkillsCache = new HashMap<Integer, Map<Integer, List<Integer>>>();
    protected Map<Integer, OdinPair<Integer, List<StructRewardItem>>> RewardItem = new HashMap<>();
    protected final Map<Integer, OdinPair<Integer, List<Integer>>> questItems = new HashMap<>();

    public static final MapleItemInformationProvider getInstance() {
        return instance;
    }

    protected final IMapleData getStringData(final int itemId) {
        String cat = null;
        IMapleData data;

        if (itemId >= 5010000) {
            data = WzXML.STRING.getCash();
        } else if (itemId >= 2000000 && itemId < 3000000) {
            data = WzXML.STRING.getConsume();
        } else if ((itemId >= 1142000 && itemId < 1143000) || (itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1123000)) {
            data = WzXML.STRING.getEqp();
            cat = "Accessory";
        } else if (itemId >= 1000000 && itemId < 1010000) {
            data = WzXML.STRING.getEqp();
            cat = "Cap";
        } else if (itemId >= 1102000 && itemId < 1103000) {
            data = WzXML.STRING.getEqp();
            cat = "Cape";
        } else if (itemId >= 1040000 && itemId < 1050000) {
            data = WzXML.STRING.getEqp();
            cat = "Coat";
        } else if (itemId >= 20000 && itemId < 22000) {
            data = WzXML.STRING.getEqp();
            cat = "Face";
        } else if (itemId >= 1080000 && itemId < 1090000) {
            data = WzXML.STRING.getEqp();
            cat = "Glove";
        } else if (itemId >= 30000 && itemId < 32000) {
            data = WzXML.STRING.getEqp();
            cat = "Hair";
        } else if (itemId >= 1050000 && itemId < 1060000) {
            data = WzXML.STRING.getEqp();
            cat = "Longcoat";
        } else if (itemId >= 1060000 && itemId < 1070000) {
            data = WzXML.STRING.getEqp();
            cat = "Pants";
        } else if (itemId >= 1610000 && itemId < 1660000) {
            data = WzXML.STRING.getEqp();
            cat = "Mechanic";
        } else if (itemId >= 1802000 && itemId < 1810000) {
            data = WzXML.STRING.getEqp();
            cat = "PetEquip";
        } else if (itemId >= 1920000 && itemId < 2000000) {
            data = WzXML.STRING.getEqp();
            cat = "Dragon";
        } else if (itemId >= 1112000 && itemId < 1120000) {
            data = WzXML.STRING.getEqp();
            cat = "Ring";
        } else if (itemId >= 1092000 && itemId < 1100000) {
            data = WzXML.STRING.getEqp();
            cat = "Shield";
        } else if (itemId >= 1070000 && itemId < 1080000) {
            data = WzXML.STRING.getEqp();
            cat = "Shoes";
        } else if (itemId >= 1900000 && itemId < 1920000) {
            data = WzXML.STRING.getEqp();
            cat = "Taming";
        } else if (itemId >= 1300000 && itemId < 1800000) {
            data = WzXML.STRING.getEqp();
            cat = "Weapon";
        } else if (itemId >= 4000000 && itemId < 5000000) {
            data = WzXML.STRING.getEtc();
        } else if (itemId >= 3000000 && itemId < 4000000) {
            data = WzXML.STRING.getIns();
        } else if (itemId >= 5000000 && itemId < 5010000) {
            data = WzXML.STRING.getPet();
        } else {
            return null;
        }
        if (cat == null) {
            return data.getChildByPath(String.valueOf(itemId));
        } else {
            return data.getChildByPath(cat + "/" + itemId);
        }
    }

    protected final IMapleData getItemData(int id) {
        //DebugLoadTime dlt = new DebugLoadTime("getItemData : " + id);

        IMapleData md_character = WzXML.CHARACTER.getItemData(id);
        if (md_character != null) {
            //dlt.End();
            return md_character;
        }

        IMapleData md_item = WzXML.ITEM.getItemData(id);
        if (md_item != null) {
            //dlt.End();
            return md_item;
        }

        //dlt.End();
        DebugLogger.ErrorLog("getItemData : " + id);
        return null;
    }

    /**
     * returns the maximum of items in one slot
     */
    public final short getSlotMax(final TacosClient client, final int itemId) {
        if (slotMaxCache.containsKey(itemId)) {
            return slotMaxCache.get(itemId);
        }
        short ret = 0;
        final IMapleData item = getItemData(itemId);
        if (item != null) {
            final IMapleData smEntry = item.getChildByPath("info/slotMax");
            if (smEntry == null) {
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    ret = 1;
                } else {
                    ret = 100;
                }
            } else {
                ret = (short) WzDataTool.getInt(smEntry);
            }
        }
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
        final IMapleData item = getItemData(itemId);
        if (item == null) {
            return -1;
        }
        int pEntry = 0;
        final IMapleData pData = item.getChildByPath("info/price");
        if (pData == null) {
            return -1;
        }
        pEntry = WzDataTool.getInt(pData);

        wholePriceCache.put(itemId, pEntry);
        return pEntry;
    }

    public final double getPrice(final int itemId) {
        if (priceCache.containsKey(itemId)) {
            return priceCache.get(itemId);
        }
        final IMapleData item = getItemData(itemId);
        if (item == null) {
            return -1;
        }
        Double pEntry = null;
        IMapleData pData = item.getChildByPath("info/unitPrice");
        if (pData != null) {
            pEntry = WzDataTool.getDouble(pData, 1.0);
        } else {
            pData = item.getChildByPath("info/price");
            if (pData == null) {
                return -1;
            }
            pEntry = (double) WzDataTool.getInt(pData, 1);
        }
        if (itemId == 2070019 || itemId == 2330007) {
            pEntry = 1.0;
        }
        priceCache.put(itemId, pEntry);
        return pEntry;
    }

    public final Map<String, Byte> getItemMakeStats(final int itemId) {
        if (itemMakeStatsCache.containsKey(itemId)) {
            return itemMakeStatsCache.get(itemId);
        }
        if (itemId / 10000 != 425) {
            return null;
        }
        final Map<String, Byte> ret = new LinkedHashMap<>();
        final IMapleData item = getItemData(itemId);
        if (item == null) {
            return null;
        }
        final IMapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        ret.put("incPAD", (byte) WzDataTool.getIntPath("incPAD", info, 0)); // WATK
        ret.put("incMAD", (byte) WzDataTool.getIntPath("incMAD", info, 0)); // MATK
        ret.put("incACC", (byte) WzDataTool.getIntPath("incACC", info, 0)); // ACC
        ret.put("incEVA", (byte) WzDataTool.getIntPath("incEVA", info, 0)); // AVOID
        ret.put("incSpeed", (byte) WzDataTool.getIntPath("incSpeed", info, 0)); // SPEED
        ret.put("incJump", (byte) WzDataTool.getIntPath("incJump", info, 0)); // JUMP
        ret.put("incMaxHP", (byte) WzDataTool.getIntPath("incMaxHP", info, 0)); // HP
        ret.put("incMaxMP", (byte) WzDataTool.getIntPath("incMaxMP", info, 0)); // MP
        ret.put("incSTR", (byte) WzDataTool.getIntPath("incSTR", info, 0)); // STR
        ret.put("incINT", (byte) WzDataTool.getIntPath("incINT", info, 0)); // INT
        ret.put("incLUK", (byte) WzDataTool.getIntPath("incLUK", info, 0)); // LUK
        ret.put("incDEX", (byte) WzDataTool.getIntPath("incDEX", info, 0)); // DEX
//	ret.put("incReqLevel", MapleDataTool.getInt("incReqLevel", info, 0)); // IDK!
        ret.put("randOption", (byte) WzDataTool.getIntPath("randOption", info, 0)); // Black Crystal Wa/MA
        ret.put("randStat", (byte) WzDataTool.getIntPath("randStat", info, 0)); // Dark Crystal - Str/Dex/int/Luk

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
        final Map<Integer, Map<String, Integer>> ret = new LinkedHashMap<Integer, Map<String, Integer>>();
        final IMapleData item = getItemData(itemId);
        if (item == null) {
            return null;
        }
        final IMapleData info = item.getChildByPath("info/level/info");
        if (info == null) {
            return null;
        }
        for (IMapleData dat : info.getChildren()) {
            Map<String, Integer> incs = new HashMap<String, Integer>();
            for (IMapleData data : dat.getChildren()) { //why we have to do this? check if number has skills or not
                if (data.getName().length() > 3) {
                    incs.put(data.getName().substring(3), WzDataTool.getIntPath(data.getName(), dat, 0));
                }
            }
            ret.put(Integer.parseInt(dat.getName()), incs);
        }
        equipIncsCache.put(itemId, ret);
        return ret;
    }

    public final Map<Integer, List<Integer>> getEquipSkills(final int itemId) {
        if (equipSkillsCache.containsKey(itemId)) {
            return equipSkillsCache.get(itemId);
        }
        final Map<Integer, List<Integer>> ret = new LinkedHashMap<Integer, List<Integer>>();
        final IMapleData item = getItemData(itemId);
        if (item == null) {
            return null;
        }
        final IMapleData info = item.getChildByPath("info/level/case");
        if (info == null) {
            return null;
        }
        for (IMapleData dat : info.getChildren()) {
            for (IMapleData data : dat.getChildren()) { //why we have to do this? check if number has skills or not
                if (data.getName().length() == 1) { //the numbers all them are one digit. everything else isnt so we're lucky here..
                    List<Integer> adds = new ArrayList<Integer>();
                    for (IMapleData skil : data.getChildByPath("Skill").getChildren()) {
                        adds.add(WzDataTool.getIntPath("id", skil, 0));
                    }
                    ret.put(Integer.valueOf(data.getName()), adds);
                }
            }
        }
        equipSkillsCache.put(itemId, ret);
        return ret;
    }

    public final Map<String, Integer> getEquipStats(int itemId) {
        if (equipStatsCache.containsKey(itemId)) {
            return equipStatsCache.get(itemId);
        }
        final Map<String, Integer> ret = new LinkedHashMap<>();
        final IMapleData item = getItemData(itemId);
        if (item == null) {
            return null;
        }
        final IMapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        for (final IMapleData data : info.getChildren()) {
            if (data.getName().startsWith("inc")) {
                ret.put(data.getName().substring(3), WzDataTool.getInt(data, 0));
            }
        }
        ret.put("tuc", WzDataTool.getIntPath("tuc", info, 0));
        ret.put("reqLevel", WzDataTool.getIntPath("reqLevel", info, 0));
        ret.put("reqJob", WzDataTool.getIntPath("reqJob", info, 0));
        ret.put("reqSTR", WzDataTool.getIntPath("reqSTR", info, 0));
        ret.put("reqDEX", WzDataTool.getIntPath("reqDEX", info, 0));
        ret.put("reqINT", WzDataTool.getIntPath("reqINT", info, 0));
        ret.put("reqLUK", WzDataTool.getIntPath("reqLUK", info, 0));
        ret.put("reqPOP", WzDataTool.getIntPath("reqPOP", info, 0));
        ret.put("cash", WzDataTool.getIntPath("cash", info, 0));
        ret.put("canLevel", info.getChildByPath("level") == null ? 0 : 1);
        ret.put("cursed", WzDataTool.getIntPath("cursed", info, 0));
        ret.put("success", WzDataTool.getIntPath("success", info, 0));
        ret.put("setItemID", WzDataTool.getIntPath("setItemID", info, 0));
        ret.put("equipTradeBlock", WzDataTool.getIntPath("equipTradeBlock", info, 0));
        ret.put("durability", WzDataTool.getIntPath("durability", info, -1));

        if (GameConstants.isMagicWeapon(itemId)) {
            ret.put("elemDefault", WzDataTool.getIntPath("elemDefault", info, 100));
            ret.put("incRMAS", WzDataTool.getIntPath("incRMAS", info, 100)); // Poison
            ret.put("incRMAF", WzDataTool.getIntPath("incRMAF", info, 100)); // Fire
            ret.put("incRMAL", WzDataTool.getIntPath("incRMAL", info, 100)); // Lightning
            ret.put("incRMAI", WzDataTool.getIntPath("incRMAI", info, 100)); // Ice
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
        final List<Integer> ret = new ArrayList<Integer>();
        final IMapleData data = getItemData(itemId).getChildByPath("req");

        if (data == null) {
            return ret;
        }
        for (final IMapleData req : data.getChildren()) {
            ret.add(WzDataTool.getInt(req));
        }
        scrollReqCache.put(itemId, ret);
        return ret;
    }

    public final IItem scrollEquipWithId(final IItem equip, final IItem scrollId, final boolean ws, final MapleCharacter chr, final int vegas) {
        if (equip.getType() == 1) { // See IItem.java
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

    public final IItem getEquipById(int equipId) {
        return getEquipById(equipId, -1);
    }

    public final IItem getEquipById(final int equipId, final int ringId) {
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
            IMapleData item = getItemData(itemId);
            if (item == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
            itemEffects.put(itemId, ret);
        }
        return ret;
    }

    public final List<OdinPair<Integer, Integer>> getSummonMobs(final int itemId) {
        if (summonMobCache.containsKey(itemId)) {
            return summonMobCache.get(itemId);
        }
        if (!GameConstants.isSummonSack(itemId)) {
            return null;
        }
        final IMapleData data = getItemData(itemId).getChildByPath("mob");
        if (data == null) {
            return null;
        }
        final List<OdinPair<Integer, Integer>> mobPairs = new ArrayList<>();

        for (final IMapleData child : data.getChildren()) {
            mobPairs.add(new OdinPair<>(
                    WzDataTool.getIntPath("id", child, 0),
                    WzDataTool.getIntPath("prob", child, 0)));
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
        IMapleData data = getItemData(id);
        int monsterid = WzDataTool.getIntPath("info/mob", data, 0);

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
        final IMapleData strings = getStringData(itemId);
        if (strings == null) {
            return null;
        }
        final String ret = WzDataTool.getStringPath("name", strings, null);
        nameCache.put(itemId, ret);
        return ret;
    }

    public final String getMsg(final int itemId) {
        if (msgCache.containsKey(itemId)) {
            return msgCache.get(itemId);
        }
        final IMapleData strings = getStringData(itemId);
        if (strings == null) {
            return null;
        }
        final String ret = WzDataTool.getStringPath("msg", strings, null);
        msgCache.put(itemId, ret);
        return ret;
    }

    public final short getItemMakeLevel(final int itemId) {
        if (itemMakeLevel.containsKey(itemId)) {
            return itemMakeLevel.get(itemId);
        }
        if (itemId / 10000 != 400) {
            return 0;
        }
        final short lvl = (short) WzDataTool.getIntPath("info/lv", getItemData(itemId), 0);
        itemMakeLevel.put(itemId, lvl);
        return lvl;
    }

    public byte isConsumeOnPickup(int itemId) {
        // 0 = not, 1 = consume on pickup, 2 = consume + party
        if (consumeOnPickupCache.containsKey(itemId)) {
            return consumeOnPickupCache.get(itemId);
        }
        final IMapleData data = getItemData(itemId);
        byte consume = (byte) WzDataTool.getIntPath("spec/consumeOnPickup", data, 0);
        if (consume == 0) {
            consume = (byte) WzDataTool.getIntPath("specEx/consumeOnPickup", data, 0);
        }
        if (consume == 1) {
            if (WzDataTool.getIntPath("spec/party", getItemData(itemId), 0) > 0) {
                consume = 2;
            }
        }
        consumeOnPickupCache.put(itemId, consume);
        return consume;
    }

    public final boolean isDropRestricted(final int itemId) {
        if (dropRestrictionCache.containsKey(itemId)) {
            return dropRestrictionCache.get(itemId);
        }
        final IMapleData data = getItemData(itemId);

        boolean trade = false;
        if (WzDataTool.getIntPath("info/tradeBlock", data, 0) == 1 || WzDataTool.getIntPath("info/quest", data, 0) == 1) {
            trade = true;
        }
        dropRestrictionCache.put(itemId, trade);
        return trade;
    }

    public final boolean isPickupRestricted(final int itemId) {
        if (pickupRestrictionCache.containsKey(itemId)) {
            return pickupRestrictionCache.get(itemId);
        }
        final boolean bRestricted = WzDataTool.getIntPath("info/only", getItemData(itemId), 0) == 1;

        pickupRestrictionCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public final boolean isAccountShared(final int itemId) {
        if (accCache.containsKey(itemId)) {
            return accCache.get(itemId);
        }
        final boolean bRestricted = WzDataTool.getIntPath("info/accountSharable", getItemData(itemId), 0) == 1;

        accCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public final int getStateChangeItem(final int itemId) {
        if (stateChangeCache.containsKey(itemId)) {
            return stateChangeCache.get(itemId);
        }
        final int triggerItem = WzDataTool.getIntPath("info/stateChangeItem", getItemData(itemId), 0);
        stateChangeCache.put(itemId, triggerItem);
        return triggerItem;
    }

    public final int getMeso(final int itemId) {
        if (mesoCache.containsKey(itemId)) {
            return mesoCache.get(itemId);
        }
        final int triggerItem = WzDataTool.getIntPath("info/meso", getItemData(itemId), 0);
        mesoCache.put(itemId, triggerItem);
        return triggerItem;
    }

    // info/damaとか
    public final int getInt(final int itemId, final String text) {
        final int triggerItem = WzDataTool.getIntPath(text, getItemData(itemId), 0);
        return triggerItem;
    }

    public final boolean isKarmaEnabled(final int itemId) {
        if (karmaEnabledCache.containsKey(itemId)) {
            return karmaEnabledCache.get(itemId) == 1;
        }
        final int iRestricted = WzDataTool.getIntPath("info/tradeAvailable", getItemData(itemId), 0);

        karmaEnabledCache.put(itemId, iRestricted);
        return iRestricted == 1;
    }

    public final boolean isPKarmaEnabled(final int itemId) {
        if (karmaEnabledCache.containsKey(itemId)) {
            return karmaEnabledCache.get(itemId) == 2;
        }
        final int iRestricted = WzDataTool.getIntPath("info/tradeAvailable", getItemData(itemId), 0);

        karmaEnabledCache.put(itemId, iRestricted);
        return iRestricted == 2;
    }

    public final boolean isPickupBlocked(final int itemId) {
        if (blockPickupCache.containsKey(itemId)) {
            return blockPickupCache.get(itemId);
        }
        final boolean iRestricted = WzDataTool.getIntPath("info/pickUpBlock", getItemData(itemId), 0) == 1;

        blockPickupCache.put(itemId, iRestricted);
        return iRestricted;
    }

    public final boolean cantSell(final int itemId) { //true = cant sell, false = can sell
        if (notSaleCache.containsKey(itemId)) {
            return notSaleCache.get(itemId);
        }
        final boolean bRestricted = WzDataTool.getIntPath("info/notSale", getItemData(itemId), 0) == 1;

        notSaleCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public OdinPair<Integer, List<StructRewardItem>> getRewardItem(final int itemid) {
        if (RewardItem.containsKey(itemid)) {
            return RewardItem.get(itemid);
        }
        final IMapleData data = getItemData(itemid);
        if (data == null) {
            return null;
        }
        final IMapleData rewards = data.getChildByPath("reward");
        if (rewards == null) {
            return null;
        }
        int totalprob = 0; // As there are some rewards with prob above 2000, we can't assume it's always 100
        List<StructRewardItem> all = new ArrayList<>();

        for (final IMapleData reward : rewards) {
            StructRewardItem struct = new StructRewardItem();

            struct.itemid = WzDataTool.getIntPath("item", reward, 0);
            struct.prob = (byte) WzDataTool.getIntPath("prob", reward, 0);
            struct.quantity = (short) WzDataTool.getIntPath("count", reward, 0);
            struct.effect = WzDataTool.getStringPath("Effect", reward, "");
            struct.worldmsg = WzDataTool.getStringPath("worldMsg", reward, null);
            struct.period = WzDataTool.getIntPath("period", reward, -1);

            totalprob += struct.prob;

            all.add(struct);
        }
        OdinPair<Integer, List<StructRewardItem>> toreturn = new OdinPair<>(totalprob, all);
        RewardItem.put(itemid, toreturn);
        return toreturn;
    }

    public final Map<String, Integer> getSkillStats(final int itemId) {
        if (SkillStatsCache.containsKey(itemId)) {
            return SkillStatsCache.get(itemId);
        }
        if (!(itemId / 10000 == 228 || itemId / 10000 == 229 || itemId / 10000 == 562)) { // Skillbook and mastery book
            return null;
        }
        final IMapleData item = getItemData(itemId);
        if (item == null) {
            return null;
        }
        final IMapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        final Map<String, Integer> ret = new LinkedHashMap<>();
        for (final IMapleData data : info.getChildren()) {
            if (data.getName().startsWith("inc")) {
                ret.put(data.getName().substring(3), WzDataTool.getInt(data, 0));
            }
        }
        ret.put("masterLevel", WzDataTool.getIntPath("masterLevel", info, 0));
        ret.put("reqSkillLevel", WzDataTool.getIntPath("reqSkillLevel", info, 0));
        ret.put("success", WzDataTool.getIntPath("success", info, 0));

        final IMapleData skill = info.getChildByPath("skill");

        for (int i = 0; i < skill.getChildren().size(); i++) { // List of allowed skillIds
            ret.put("skillid" + i, WzDataTool.getIntPath(Integer.toString(i), skill, 0));
        }
        SkillStatsCache.put(itemId, ret);
        return ret;
    }

    public final List<Integer> petsCanConsume(final int itemId) {
        if (petsCanConsumeCache.get(itemId) != null) {
            return petsCanConsumeCache.get(itemId);
        }
        final List<Integer> ret = new ArrayList<Integer>();
        final IMapleData data = getItemData(itemId);
        if (data == null || data.getChildByPath("spec") == null) {
            return ret;
        }
        int curPetId = 0;
        for (IMapleData c : data.getChildByPath("spec")) {
            try {
                Integer.parseInt(c.getName());
            } catch (NumberFormatException e) {
                continue;
            }
            curPetId = WzDataTool.getInt(c, 0);
            if (curPetId == 0) {
                break;
            }
            ret.add(Integer.valueOf(curPetId));
        }
        petsCanConsumeCache.put(itemId, ret);
        return ret;
    }

    public OdinPair<Integer, List<Integer>> questItemInfo(int itemId) {
        if (questItems.containsKey(itemId)) {
            return questItems.get(itemId);
        }
        if (itemId / 10000 != 422 || getItemData(itemId) == null) {
            return null;
        }
        final IMapleData itemD = getItemData(itemId).getChildByPath("info");
        if (itemD == null || itemD.getChildByPath("consumeItem") == null) {
            return null;
        }
        final List<Integer> consumeItems = new ArrayList<Integer>();
        for (IMapleData consume : itemD.getChildByPath("consumeItem")) {
            consumeItems.add(WzDataTool.getInt(consume, 0));
        }
        final OdinPair<Integer, List<Integer>> questItem = new OdinPair<>(WzDataTool.getIntPath("questId", itemD, 0), consumeItems);
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
