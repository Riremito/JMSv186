package odin.server;

import tacos.client.TacosCharacter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import tacos.wz.CharacterWz.EquipInfo;
import tacos.wz.ItemWz.ItemInfo;
import tacos.wz.ItemWz.ScrollData;
import tacos.wz.WzDataStorage;
import tacos.wz.WzXML;

public class MapleItemInformationProvider {

    private final static MapleItemInformationProvider instance = new MapleItemInformationProvider();
    private final Map<Integer, MapleStatEffect> itemEffects = new HashMap<>();

    public static MapleItemInformationProvider getInstance() {
        return instance;
    }

    // equip data is in Character.wz, the others are in Item.wz
    private static <V> V getInfo(int itemId, Function<EquipInfo, V> equip, Function<ItemInfo, V> item, V def) {
        if (itemId / 1000000 <= 1) {
            EquipInfo ei = WzXML.CHARACTER.getEquipInfo(itemId);
            return (ei == null) ? def : equip.apply(ei);
        }
        ItemInfo ii = WzXML.ITEM.getItemInfo(itemId);
        return (ii == null) ? def : item.apply(ii);
    }

    public short getSlotMax(int itemId) {
        return (short) (int) getInfo(itemId, EquipInfo::getSlotMax, ItemInfo::getSlotMax, 0);
    }

    public int getWholePrice(int itemId) {
        return getInfo(itemId, EquipInfo::getPrice, ItemInfo::getPrice, -1);
    }

    public double getPrice(int itemId) {
        return getInfo(itemId, ei -> (double) ei.getPrice(), ItemInfo::getUnitPrice, -1.0);
    }

    public boolean canEquip(EquipInfo stats, int itemid, int level, int job, int fame, int str, int dex, int luk, int int_, int supremacy) {
        if ((level + supremacy) >= stats.getReqLevel() && str >= stats.getReqSTR() && dex >= stats.getReqDEX() && luk >= stats.getReqLUK() && int_ >= stats.getReqINT()) {
            int fameReq = stats.getReqPOP();
            return fameReq == 0 || fame >= fameReq;
        }
        return false;
    }

    public Item scrollEquipWithId(Item equip, Item scrollId, boolean ws, TacosCharacter chr, int vegas) {
        if (equip.getType() != 1) { // See Item.java
            return equip;
        }
        Equip nEquip = (Equip) equip;
        int scroll_id = scrollId.getItemId();
        ScrollData sd = WzXML.ITEM.getScrollData(scroll_id);
        boolean always_success = false;
        int succ = GameConstants.isTablet(scroll_id) ? GameConstants.getSuccessTablet(scroll_id, nEquip.getCUC()) : (always_success ? 0 : sd.getSuccess());
        int curse = GameConstants.isTablet(scroll_id) ? GameConstants.getCurseTablet(scroll_id, nEquip.getCUC()) : (always_success ? 0 : sd.getCursed());
        int success = succ + (vegas == 5610000 && succ == 10 ? 20 : (vegas == 5610001 && succ == 60 ? 30 : 0));

        boolean uses_slot = !GameConstants.isCleanSlate(scroll_id) && !always_success;
        return equip;
    }

    public Equip getEquipById(int equip_id) {
        Equip ret = getDefaultEquipById(equip_id);
        ret.getEnhancement().randomize();
        return ret;
    }

    public Equip getDefaultEquipById(int equip_id) {
        Equip ret = WzXML.CHARACTER.getEquipById(equip_id);
        return ret;
    }

    public MapleStatEffect getItemEffect(int itemId) {
        MapleStatEffect ret = itemEffects.get(itemId);
        if (ret == null && WzDataStorage.ITEM.check(itemId)) {
            ret = MapleStatEffect.loadItemEffectFromData(WzXML.ITEM.getItemSpec(itemId), itemId);
            if (ret != null) {
                itemEffects.put(itemId, ret);
            }
        }
        return ret;
    }

    public boolean canScroll(int scrollid, int itemid) {
        return (scrollid / 100) % 100 == (itemid / 10000) % 100;
    }

    // 0 = not, 1 = consume on pickup, 2 = consume + party
    public byte isConsumeOnPickup(int itemId) {
        return (byte) (int) getInfo(itemId, ei -> 0, ItemInfo::getConsumeOnPickup, 0);
    }

    public boolean isDropRestricted(int itemId) {
        return getInfo(itemId, ei -> ei.getTradeBlock() == 1 || ei.getQuest() == 1, ii -> ii.getTradeBlock() == 1 || ii.getQuest() == 1, false);
    }

    public boolean isPickupRestricted(int itemId) {
        return getInfo(itemId, ei -> ei.getOnly() == 1, ii -> ii.getOnly() == 1, false);
    }

    public boolean isAccountShared(int itemId) {
        return getInfo(itemId, ei -> ei.getAccountSharable() == 1, ii -> ii.getAccountSharable() == 1, false);
    }

    public boolean isPickupBlocked(int itemId) {
        return getInfo(itemId, ei -> ei.getPickUpBlock() == 1, ii -> ii.getPickUpBlock() == 1, false);
    }

    // true = cant sell, false = can sell
    public boolean cantSell(int itemId) {
        return getInfo(itemId, ei -> ei.getNotSale() == 1, ii -> ii.getNotSale() == 1, false);
    }

    public boolean isCash(int itemId) {
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH) {
            return true;
        }
        return getInfo(itemId, ei -> ei.getCash() > 0, ii -> ii.getCash() > 0, false);
    }
}
