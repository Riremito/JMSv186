package odin.server;

import java.util.LinkedList;
import java.util.EnumMap;
import java.util.Map;
import tacos.client.TacosCharacter;
import java.util.List;
import odin.client.inventory.Item;
import odin.client.inventory.Item.ItemFlag;
import odin.constants.GameConstants;
import tacos.client.TacosClient;
import odin.client.inventory.MapleInventoryType;
import tacos.packet.ops.OpsMRLeave;
import tacos.packet.response.ResCMiniRoomBaseDlg;

public class MapleTrade {

    private MapleTrade partner = null;
    private final List<Item> items = new LinkedList<>();
    private List<Item> exchangeItems;
    private int meso = 0;
    private int exchangeMeso = 0;
    private boolean locked = false;
    private final boolean isPointTrade;
    private final TacosCharacter chr;
    private final byte tradingslot;

    public MapleTrade(byte tradingslot, TacosCharacter chr) {
        this(tradingslot, chr, false);
    }

    public MapleTrade(byte tradingslot, TacosCharacter chr, boolean isPointTrade) {
        this.tradingslot = tradingslot;
        this.chr = chr;
        this.isPointTrade = isPointTrade;
    }

    public boolean isPointTrade() {
        return this.isPointTrade;
    }

    public void complete() {
        if (exchangeItems != null) { // just to be on the safe side...
            for (Item item : exchangeItems) {
                byte flag = item.getFlag();

                if (ItemFlag.KARMA_EQ.check(flag)) {
                    item.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    item.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                }
                MapleInventoryManipulator.addFromDrop(chr, item, false);
            }
            exchangeItems.clear();
        }
        if (exchangeMeso > 0) {
            chr.gainMeso(exchangeMeso - GameConstants.getTaxAmount(exchangeMeso), false, true, false);
        }
        exchangeMeso = 0;

        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(tradingslot, OpsMRLeave.TRLeave_TradeDone));
    }

    public void cancel(TacosCharacter chr) {
        cancel(chr, 0);
    }

    public void cancel(TacosCharacter chr, int unsuccessful) {
        for (Item item : items) {
            MapleInventoryManipulator.addFromDrop(chr, item, false);
        }
        items.clear();
        if (meso > 0) {
            chr.gainMeso(meso, false, true, false);
        }
        meso = 0;

        // 0 = canceled, 1 = inventory full, 2 = only one item allowed
        OpsMRLeave reason = (unsuccessful == 0) ? OpsMRLeave.MRLeave_Closed : (unsuccessful == 1 ? OpsMRLeave.TRLeave_TradeFail : OpsMRLeave.TRLeave_TradeFail_OnlyItem);
        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(tradingslot, reason));
    }

    public boolean isLocked() {
        return locked;
    }

    public void setMeso(int meso) {
        if (locked || partner == null || meso <= 0 || this.meso + meso <= 0) {
            return;
        }
        if (chr.getMeso() >= meso) {
            chr.gainMeso(-meso, false, true, false);
            this.meso += meso;
            chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_PutMoney((byte) 0, this.meso));
            partner.getChr().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_PutMoney((byte) 1, this.meso));
        }
    }

    public void addItem(Item item) {
        if (locked || partner == null) {
            return;
        }
        items.add(item);
        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_PutItem((byte) 0, item));
        partner.getChr().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_PutItem((byte) 1, item));
    }

    public void chat(String message) {
        chr.dropMessage(-2, chr.getName() + " : " + message);
        if (partner != null) {
            partner.getChr().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Chat(1, chr.getName() + " : " + message));
        }
    }

    public MapleTrade getPartner() {
        return partner;
    }

    public void setPartner(MapleTrade partner) {
        if (locked) {
            return;
        }
        this.partner = partner;
    }

    public TacosCharacter getChr() {
        return chr;
    }

    public int getNextTargetSlot() {
        if (items.size() >= 9) {
            return -1;
        }
        int ret = 1; //first slot
        for (Item item : items) {
            if (item.getPosition() == ret) {
                ret++;
            }
        }
        return ret;
    }

    public boolean setItems(TacosCharacter chr, Item item, byte targetSlot, int quantity) {
        int target = getNextTargetSlot();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (target == -1 || GameConstants.isPet(item.getItemId()) || isLocked() || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.CASH && quantity != 1) || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP && quantity != 1)) {
            return false;
        }
        byte flag = item.getFlag();
        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
            chr.updateInv();
            return false;
        }
        if ((ii.isDropRestricted(item.getItemId()) || ii.isAccountShared(item.getItemId())) && !(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
            chr.updateInv();
            return false;
        }
        Item tradeItem = item.copy();
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            tradeItem.setQuantity(item.getQuantity());
            MapleInventoryManipulator.removeFromSlot(chr, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), item.getQuantity(), true);
        } else {
            tradeItem.setQuantity((short) quantity);
            MapleInventoryManipulator.removeFromSlot(chr, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), (short) quantity, true);
        }
        if (targetSlot < 0) {
            targetSlot = (byte) target;
        } else {
            for (Item itemz : items) {
                if (itemz.getPosition() == targetSlot) {
                    targetSlot = (byte) target;
                    break;
                }
            }
        }
        tradeItem.setPosition(targetSlot);
        addItem(tradeItem);
        return true;
    }

    // 0 = fine, 1 = no inventory space or meso overflow, 2 = pickup restricted item
    private int check() {
        if (chr.getMeso() + exchangeMeso < 0) {
            return 1;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map<MapleInventoryType, Integer> needed = new EnumMap<>(MapleInventoryType.class);
        for (Item item : exchangeItems) {
            MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
            needed.merge(type, 1, Integer::sum);
            if (ii.isPickupRestricted(item.getItemId()) && chr.getInventory(type).findById(item.getItemId()) != null) {
                return 2;
            }
        }
        for (Map.Entry<MapleInventoryType, Integer> entry : needed.entrySet()) {
            if (chr.getInventory(entry.getKey()).getNumFreeSlot() < entry.getValue()) {
                return 1;
            }
        }
        return 0;
    }

    public static void completeTrade(TacosCharacter player) {
        MapleTrade local = player.getTrade();
        MapleTrade partner = local.getPartner();

        if (partner == null || local.locked) {
            return;
        }
        local.locked = true; // Locking the trade
        partner.getChr().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_Trade());

        partner.exchangeItems = local.items; // Copy this to partner's trade since it's alreadt accepted
        partner.exchangeMeso = local.meso; // Copy this to partner's trade since it's alreadt accepted

        if (partner.isLocked()) { // Both locked
            int lz = local.check(), lz2 = partner.check();
            if (lz == 0 && lz2 == 0) {
                local.complete();
                partner.complete();
            } else {
                // NOTE : IF accepted = other party but inventory is full, the item is lost.
                partner.cancel(partner.getChr(), lz == 0 ? lz2 : lz);
                local.cancel(player, lz == 0 ? lz2 : lz);
            }
            partner.getChr().setTrade(null);
            player.setTrade(null);
        }
    }

    public static void cancelTrade(MapleTrade Localtrade, TacosClient client) {
        Localtrade.cancel(client.getPlayer());

        MapleTrade partner = Localtrade.getPartner();
        if (partner != null) {
            partner.cancel(partner.getChr());
            partner.getChr().setTrade(null);
        }
        if (Localtrade.chr != null) {
            Localtrade.chr.setTrade(null);
        }
    }

    public static void startTrade(TacosCharacter player, boolean isPointTrade) {
        if (player.getTrade() == null) {
            player.setTrade(new MapleTrade((byte) 0, player, isPointTrade));
            player.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_TradingRoom(player, player.getTrade(), (byte) 0, isPointTrade));
        } else {
            player.Notice("You are already in a trade");
        }
    }

    public static void inviteTrade(TacosCharacter player1, TacosCharacter player2) {
        if (player1 == null || player1.getTrade() == null) {
            return;
        }
        if (player2 != null && player2.getTrade() == null) {
            player2.setTrade(new MapleTrade((byte) 1, player2));
            player2.getTrade().setPartner(player1.getTrade());
            player1.getTrade().setPartner(player2.getTrade());
            player2.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Invite(player1, player1.getTrade().isPointTrade()));
        } else {
            player1.Notice("The other player is already trading with someone else.");
            cancelTrade(player1.getTrade(), player1.getClient());
        }
    }

    public static void visitTrade(TacosCharacter player1, TacosCharacter player2, boolean isPointTrade) {
        if (player1.getTrade() != null && player1.getTrade().getPartner() == player2.getTrade() && player2.getTrade() != null && player2.getTrade().getPartner() == player1.getTrade()) {
            // We don't need to check for map here as the user is found via TacosMap.getCharacterById()
            player2.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Enter(1, player1));
            player1.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_TradingRoom(player1, player1.getTrade(), (byte) 1, isPointTrade));
        } else {
            player1.Notice("The other player has already closed the trade");
        }
    }

    public static void declineTrade(TacosCharacter player) {
        MapleTrade trade = player.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                TacosCharacter other = trade.getPartner().getChr();
                other.getTrade().cancel(other);
                other.setTrade(null);
                other.Notice(player.getName() + " has declined your trade request");
            }
            trade.cancel(player);
            player.setTrade(null);
        }
    }
}
