package odin.server;

import java.util.LinkedList;
import java.util.List;
import odin.client.inventory.IItem;
import odin.client.inventory.ItemFlag;
import odin.constants.GameConstants;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import odin.client.inventory.MapleInventoryType;
import java.lang.ref.WeakReference;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.packet.response.wrapper.ResWrapper;

public class MapleTrade {

    private MapleTrade partner = null;
    private final List<IItem> items = new LinkedList<>();
    private List<IItem> exchangeItems;
    private int meso = 0;
    private int exchangeMeso = 0;
    private boolean locked = false;
    private boolean isPointTrade = false;
    private final WeakReference<MapleCharacter> wrchr;
    private final byte tradingslot;

    public MapleTrade(final byte tradingslot, final MapleCharacter chr) {
        this.tradingslot = tradingslot;
        this.wrchr = new WeakReference<>(chr);
    }

    public MapleTrade(final byte tradingslot, final MapleCharacter chr, boolean isPointTrade) {
        this.tradingslot = tradingslot;
        this.wrchr = new WeakReference<>(chr);
        this.isPointTrade = isPointTrade;
    }

    public boolean IsPointTrading() {
        return this.isPointTrade;
    }

    public final void CompleteTrade() {
        if (exchangeItems != null) { // just to be on the safe side...
            for (final IItem item : exchangeItems) {
                byte flag = item.getFlag();

                if (ItemFlag.KARMA_EQ.check(flag)) {
                    item.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    item.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                }
                MapleInventoryManipulator.addFromDrop(wrchr.get().getClient(), item, false);
            }
            exchangeItems.clear();
        }
        if (exchangeMeso > 0) {
            wrchr.get().gainMeso(exchangeMeso - GameConstants.getTaxAmount(exchangeMeso), false, true, false);
        }
        exchangeMeso = 0;

        wrchr.get().SendPacket(ResCMiniRoomBaseDlg.TradeMessage(tradingslot, (byte) 0x07));
    }

    public final void cancel(final TacosClient client) {
        cancel(client, 0);
    }

    public final void cancel(final TacosClient client, final int unsuccessful) {
        if (items != null) { // just to be on the safe side...
            for (final IItem item : items) {
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

    public final boolean isLocked() {
        return locked;
    }

    public final void setMeso(final int meso) {
        if (locked || partner == null || meso <= 0 || this.meso + meso <= 0) {
            return;
        }
        if (wrchr.get().getMeso() >= meso) {
            wrchr.get().gainMeso(-meso, false, true, false);
            this.meso += meso;
            wrchr.get().SendPacket(ResCMiniRoomBaseDlg.getTradeMesoSet((byte) 0, this.meso));
            if (partner != null) {
                partner.getChr().SendPacket(ResCMiniRoomBaseDlg.getTradeMesoSet((byte) 1, this.meso));
            }
        }
    }

    public final void addItem(final IItem item) {
        if (locked || partner == null) {
            return;
        }
        items.add(item);
        wrchr.get().SendPacket(ResCMiniRoomBaseDlg.getTradeItemAdd((byte) 0, item));
        if (partner != null) {
            partner.getChr().SendPacket(ResCMiniRoomBaseDlg.getTradeItemAdd((byte) 1, item));
        }
    }

    public void chat(String message) {
        wrchr.get().dropMessage(-2, wrchr.get().getName() + " : " + message);
        if (partner != null) {
            partner.getChr().SendPacket(ResCMiniRoomBaseDlg.shopChat(wrchr.get().getName() + " : " + message, 1));
        }
    }

    public final MapleTrade getPartner() {
        return partner;
    }

    public final void setPartner(final MapleTrade partner) {
        if (locked) {
            return;
        }
        this.partner = partner;
    }

    public final MapleCharacter getChr() {
        return wrchr.get();
    }

    public final int getNextTargetSlot() {
        if (items.size() >= 9) {
            return -1;
        }
        int ret = 1; //first slot
        for (IItem item : items) {
            if (item.getPosition() == ret) {
                ret++;
            }
        }
        return ret;
    }

    public final boolean setItems(final TacosClient client, final IItem item, byte targetSlot, final int quantity) {
        MapleCharacter chr = client.getPlayer();
        int target = getNextTargetSlot();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (target == -1 || GameConstants.isPet(item.getItemId()) || isLocked() || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.CASH && quantity != 1) || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP && quantity != 1)) {
            return false;
        }
        final byte flag = item.getFlag();
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
        IItem tradeItem = item.copy();
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
            for (IItem itemz : items) {
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

    private final int check() { //0 = fine, 1 = invent space not, 2 = pickupRestricted
        if (wrchr.get().getMeso() + exchangeMeso < 0) {
            return 1;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
        for (final IItem item : exchangeItems) {
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
            if (ii.isPickupRestricted(item.getItemId()) && wrchr.get().getInventory(GameConstants.getInventoryType(item.getItemId())).findById(item.getItemId()) != null) {
                return 2;
            }
        }
        if (wrchr.get().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || wrchr.get().getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || wrchr.get().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || wrchr.get().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || wrchr.get().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
            return 1;
        }
        return 0;
    }

    public final static void completeTrade(final MapleCharacter player) {
        final MapleTrade local = player.getTrade();
        final MapleTrade partner = local.getPartner();

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

    public static final void cancelTrade(final MapleTrade Localtrade, final TacosClient client) {
        Localtrade.cancel(client);

        final MapleTrade partner = Localtrade.getPartner();
        if (partner != null) {
            partner.cancel(partner.getChr().getClient());
            partner.getChr().setTrade(null);
        }
        if (Localtrade.wrchr.get() != null) {
            Localtrade.wrchr.get().setTrade(null);
        }
    }

    public static final void startTrade(final MapleCharacter player, boolean isPointTrade) {
        if (player.getTrade() == null) {
            player.setTrade(new MapleTrade((byte) 0, player, isPointTrade));
            player.SendPacket(ResCMiniRoomBaseDlg.getTradeStart(player.getClient(), player.getTrade(), (byte) 0, isPointTrade));
        } else {
            player.SendPacket(ResWrapper.BroadCastMsgEvent("You are already in a trade"));
        }
    }

    public static final void inviteTrade(final MapleCharacter player1, final MapleCharacter player2) {
        if (player1 == null || player1.getTrade() == null) {
            return;
        }
        if (player2 != null && player2.getTrade() == null) {
            player2.setTrade(new MapleTrade((byte) 1, player2));
            player2.getTrade().setPartner(player1.getTrade());
            player1.getTrade().setPartner(player2.getTrade());
            player2.SendPacket(ResCMiniRoomBaseDlg.getTradeInvite(player1, player1.getTrade().IsPointTrading()));
        } else {
            player1.SendPacket(ResWrapper.BroadCastMsgEvent("The other player is already trading with someone else."));
            cancelTrade(player1.getTrade(), player1.getClient());
        }
    }

    public static final void visitTrade(final MapleCharacter player1, final MapleCharacter player2, boolean isPointTrade) {
        if (player1.getTrade() != null && player1.getTrade().getPartner() == player2.getTrade() && player2.getTrade() != null && player2.getTrade().getPartner() == player1.getTrade()) {
            // We don't need to check for map here as the user is found via MapleMap.getCharacterById()
            player2.SendPacket(ResCMiniRoomBaseDlg.getTradePartnerAdd(player1));
            player1.SendPacket(ResCMiniRoomBaseDlg.getTradeStart(player1.getClient(), player1.getTrade(), (byte) 1, isPointTrade));
            //c1.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
            //c2.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
        } else {
            player1.SendPacket(ResWrapper.BroadCastMsgEvent("The other player has already closed the trade"));
        }
    }

    public static final void declineTrade(final MapleCharacter player) {
        final MapleTrade trade = player.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                MapleCharacter other = trade.getPartner().getChr();
                other.getTrade().cancel(other.getClient());
                other.setTrade(null);
                other.dropMessage(5, player.getName() + " has declined your trade request");
            }
            trade.cancel(player.getClient());
            player.setTrade(null);
        }
    }
}
