package odin.server;

import java.util.LinkedList;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import java.util.List;
import odin.client.inventory.Item;
import odin.client.inventory.Item.ItemFlag;
import odin.constants.GameConstants;
import tacos.client.TacosClient;
import odin.client.inventory.MapleInventoryType;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.response.builder.PB_BroadcastMsg;

public class MapleTrade {

    private MapleTrade partner = null;
    private final List<Item> items = new LinkedList<>();
    private List<Item> exchangeItems;
    private int meso = 0;
    private int exchangeMeso = 0;
    private boolean locked = false;
    private boolean isPointTrade = false;
    private final TacosCharacter wrchr;
    private final byte tradingslot;

    public MapleTrade(byte tradingslot, TacosCharacter chr) {
        this.tradingslot = tradingslot;
        this.wrchr = chr;
    }

    public MapleTrade(byte tradingslot, TacosCharacter chr, boolean isPointTrade) {
        this.tradingslot = tradingslot;
        this.wrchr = chr;
        this.isPointTrade = isPointTrade;
    }

    public boolean IsPointTrading() {
        return this.isPointTrade;
    }

    public void CompleteTrade() {
        if (exchangeItems != null) { // just to be on the safe side...
            for (Item item : exchangeItems) {
                byte flag = item.getFlag();

                if (ItemFlag.KARMA_EQ.check(flag)) {
                    item.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    item.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                }
                MapleInventoryManipulator.addFromDrop(wrchr.getClient(), item, false);
            }
            exchangeItems.clear();
        }
        if (exchangeMeso > 0) {
            wrchr.gainMeso(exchangeMeso - GameConstants.getTaxAmount(exchangeMeso), false, true, false);
        }
        exchangeMeso = 0;

        wrchr.SendPacket(ResCMiniRoomBaseDlg.TradeMessage(tradingslot, (byte) 0x07));
    }

    public void cancel(TacosClient client) {
        cancel(client, 0);
    }

    public void cancel(TacosClient client, int unsuccessful) {
        if (items != null) { // just to be on the safe side...
            for (Item item : items) {
                MapleInventoryManipulator.addFromDrop(client, item, false);
            }
            items.clear();
        }
        if (meso > 0) {
            client.getPlayer().gainMeso(meso, false, true, false);
        }
        meso = 0;

        client.SendPacket(ResCMiniRoomBaseDlg.getTradeCancel(tradingslot, unsuccessful));
    }

    public boolean isLocked() {
        return locked;
    }

    public void setMeso(int meso) {
        if (locked || partner == null || meso <= 0 || this.meso + meso <= 0) {
            return;
        }
        if (wrchr.getMeso() >= meso) {
            wrchr.gainMeso(-meso, false, true, false);
            this.meso += meso;
            wrchr.SendPacket(ResCMiniRoomBaseDlg.getTradeMesoSet((byte) 0, this.meso));
            if (partner != null) {
                partner.getChr().SendPacket(ResCMiniRoomBaseDlg.getTradeMesoSet((byte) 1, this.meso));
            }
        }
    }

    public void addItem(Item item) {
        if (locked || partner == null) {
            return;
        }
        items.add(item);
        wrchr.SendPacket(ResCMiniRoomBaseDlg.getTradeItemAdd((byte) 0, item));
        if (partner != null) {
            partner.getChr().SendPacket(ResCMiniRoomBaseDlg.getTradeItemAdd((byte) 1, item));
        }
    }

    public void chat(String message) {
        wrchr.dropMessage(-2, wrchr.getName() + " : " + message);
        if (partner != null) {
            partner.getChr().SendPacket(ResCMiniRoomBaseDlg.shopChat(wrchr.getName() + " : " + message, 1));
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
        return wrchr;
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

    public boolean setItems(TacosClient client, Item item, byte targetSlot, int quantity) {
        TacosCharacter chr = client.getPlayer();
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
        if (ii.isDropRestricted(item.getItemId()) || ii.isAccountShared(item.getItemId())) {
            if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                chr.updateInv();
                return false;
            }
        }
        Item tradeItem = item.copy();
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            tradeItem.setQuantity(item.getQuantity());
            MapleInventoryManipulator.removeFromSlot(client, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), item.getQuantity(), true);
        } else {
            tradeItem.setQuantity((short) quantity);
            MapleInventoryManipulator.removeFromSlot(client, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), (short) quantity, true);
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

    private int check() { //0 = fine, 1 = invent space not, 2 = pickupRestricted
        if (wrchr.getMeso() + exchangeMeso < 0) {
            return 1;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
        for (Item item : exchangeItems) {
            switch (GameConstants.getInventoryType(item.getItemId())) {
                case EQUIP:
                    eq++;
                    break;
                case USE:
                    use++;
                    break;
                case SETUP:
                    setup++;
                    break;
                case ETC:
                    etc++;
                    break;
                case CASH: // Not allowed, probably hacking
                    cash++;
                    break;
            }
            if (ii.isPickupRestricted(item.getItemId()) && wrchr.getInventory(GameConstants.getInventoryType(item.getItemId())).findById(item.getItemId()) != null) {
                return 2;
            }
        }
        if (wrchr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || wrchr.getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || wrchr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || wrchr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || wrchr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
            return 1;
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
        partner.getChr().SendPacket(ResCMiniRoomBaseDlg.getTradeConfirmation());

        partner.exchangeItems = local.items; // Copy this to partner's trade since it's alreadt accepted
        partner.exchangeMeso = local.meso; // Copy this to partner's trade since it's alreadt accepted

        if (partner.isLocked()) { // Both locked
            int lz = local.check(), lz2 = partner.check();
            if (lz == 0 && lz2 == 0) {
                local.CompleteTrade();
                partner.CompleteTrade();
            } else {
                // NOTE : IF accepted = other party but inventory is full, the item is lost.
                partner.cancel(partner.getChr().getClient(), lz == 0 ? lz2 : lz);
                local.cancel(player.getClient(), lz == 0 ? lz2 : lz);
            }
            partner.getChr().setTrade(null);
            player.setTrade(null);
        }
    }

    public static void cancelTrade(MapleTrade Localtrade, TacosClient client) {
        Localtrade.cancel(client);

        MapleTrade partner = Localtrade.getPartner();
        if (partner != null) {
            partner.cancel(partner.getChr().getClient());
            partner.getChr().setTrade(null);
        }
        if (Localtrade.wrchr != null) {
            Localtrade.wrchr.setTrade(null);
        }
    }

    public static void startTrade(TacosCharacter player, boolean isPointTrade) {
        if (player.getTrade() == null) {
            player.setTrade(new MapleTrade((byte) 0, player, isPointTrade));
            player.SendPacket(ResCMiniRoomBaseDlg.getTradeStart(player.getClient(), player.getTrade(), (byte) 0, isPointTrade));
        } else {
            player.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("You are already in a trade").build()));
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
            player2.SendPacket(ResCMiniRoomBaseDlg.getTradeInvite(player1, player1.getTrade().IsPointTrading()));
        } else {
            player1.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("The other player is already trading with someone else.").build()));
            cancelTrade(player1.getTrade(), player1.getClient());
        }
    }

    public static void visitTrade(TacosCharacter player1, TacosCharacter player2, boolean isPointTrade) {
        if (player1.getTrade() != null && player1.getTrade().getPartner() == player2.getTrade() && player2.getTrade() != null && player2.getTrade().getPartner() == player1.getTrade()) {
            // We don't need to check for map here as the user is found via TacosMap.getCharacterById()
            player2.SendPacket(ResCMiniRoomBaseDlg.getTradePartnerAdd(player1));
            player1.SendPacket(ResCMiniRoomBaseDlg.getTradeStart(player1.getClient(), player1.getTrade(), (byte) 1, isPointTrade));
            //c1.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
            //c2.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
        } else {
            player1.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("The other player has already closed the trade").build()));
        }
    }

    public static void declineTrade(TacosCharacter player) {
        MapleTrade trade = player.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                TacosCharacter other = trade.getPartner().getChr();
                other.getTrade().cancel(other.getClient());
                other.setTrade(null);
                other.dropMessage(5, player.getName() + " has declined your trade request");
            }
            trade.cancel(player.getClient());
            player.setTrade(null);
        }
    }
}
