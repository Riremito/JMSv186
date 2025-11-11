package odin.server;

import java.util.LinkedList;
import java.util.List;
import odin.client.inventory.IItem;
import odin.client.inventory.ItemFlag;
import odin.constants.GameConstants;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.MapleInventoryType;
import odin.client.messages.CommandProcessor;
import odin.constants.ServerConstants.CommandType;
import java.lang.ref.WeakReference;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.packet.response.wrapper.WrapCWvsContext;

public class MapleTrade {

    private MapleTrade partner = null;
    private final List<IItem> items = new LinkedList<IItem>();
    private List<IItem> exchangeItems;
    private int meso = 0, exchangeMeso = 0;
    private boolean locked = false;
    private final WeakReference<MapleCharacter> chr;
    private final byte tradingslot;
    private boolean isPointTrade = false;

    public MapleTrade(final byte tradingslot, final MapleCharacter chr) {
        this.tradingslot = tradingslot;
        this.chr = new WeakReference<MapleCharacter>(chr);
    }

    public MapleTrade(final byte tradingslot, final MapleCharacter chr, boolean isPointTrade) {
        this.tradingslot = tradingslot;
        this.chr = new WeakReference<MapleCharacter>(chr);
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
                MapleInventoryManipulator.addFromDrop(chr.get().getClient(), item, false);
            }
            exchangeItems.clear();
        }
        if (exchangeMeso > 0) {
            chr.get().gainMeso(exchangeMeso - GameConstants.getTaxAmount(exchangeMeso), false, true, false);
        }
        exchangeMeso = 0;

        chr.get().getClient().getSession().write(ResCMiniRoomBaseDlg.TradeMessage(tradingslot, (byte) 0x07));
    }

    public final void cancel(final MapleClient c) {
        cancel(c, 0);
    }

    public final void cancel(final MapleClient c, final int unsuccessful) {
        if (items != null) { // just to be on the safe side...
            for (final IItem item : items) {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            items.clear();
        }
        if (meso > 0) {
            c.getPlayer().gainMeso(meso, false, true, false);
        }
        meso = 0;

        c.getSession().write(ResCMiniRoomBaseDlg.getTradeCancel(tradingslot, unsuccessful));
    }

    public final boolean isLocked() {
        return locked;
    }

    public final void setMeso(final int meso) {
        if (locked || partner == null || meso <= 0 || this.meso + meso <= 0) {
            return;
        }
        if (chr.get().getMeso() >= meso) {
            chr.get().gainMeso(-meso, false, true, false);
            this.meso += meso;
            chr.get().getClient().getSession().write(ResCMiniRoomBaseDlg.getTradeMesoSet((byte) 0, this.meso));
            if (partner != null) {
                partner.getChr().getClient().getSession().write(ResCMiniRoomBaseDlg.getTradeMesoSet((byte) 1, this.meso));
            }
        }
    }

    public final void addItem(final IItem item) {
        if (locked || partner == null) {
            return;
        }
        items.add(item);
        chr.get().getClient().getSession().write(ResCMiniRoomBaseDlg.getTradeItemAdd((byte) 0, item));
        if (partner != null) {
            partner.getChr().getClient().getSession().write(ResCMiniRoomBaseDlg.getTradeItemAdd((byte) 1, item));
        }
    }

    public final void chat(final String message) {
        if (!CommandProcessor.processCommand(chr.get().getClient(), message, CommandType.TRADE)) {
            chr.get().dropMessage(-2, chr.get().getName() + " : " + message);
            if (partner != null) {
                partner.getChr().getClient().getSession().write(ResCField.shopChat(chr.get().getName() + " : " + message, 1));
            }
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
        return chr.get();
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

    public final boolean setItems(final MapleClient c, final IItem item, byte targetSlot, final int quantity) {
        int target = getNextTargetSlot();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (target == -1 || GameConstants.isPet(item.getItemId()) || isLocked() || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.CASH && quantity != 1) || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP && quantity != 1)) {
            return false;
        }
        final byte flag = item.getFlag();
        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
            c.getSession().write(WrapCWvsContext.updateStat());
            return false;
        }
        if (ii.isDropRestricted(item.getItemId()) || ii.isAccountShared(item.getItemId())) {
            if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                c.getSession().write(WrapCWvsContext.updateStat());
                return false;
            }
        }
        IItem tradeItem = item.copy();
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            tradeItem.setQuantity(item.getQuantity());
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), item.getQuantity(), true);
        } else {
            tradeItem.setQuantity((short) quantity);
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), (short) quantity, true);
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
        if (chr.get().getMeso() + exchangeMeso < 0) {
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
            if (ii.isPickupRestricted(item.getItemId()) && chr.get().getInventory(GameConstants.getInventoryType(item.getItemId())).findById(item.getItemId()) != null) {
                return 2;
            }
        }
        if (chr.get().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || chr.get().getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || chr.get().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || chr.get().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || chr.get().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
            return 1;
        }
        return 0;
    }

    public final static void completeTrade(final MapleCharacter c) {
        final MapleTrade local = c.getTrade();
        final MapleTrade partner = local.getPartner();

        if (partner == null || local.locked) {
            return;
        }
        local.locked = true; // Locking the trade
        partner.getChr().getClient().getSession().write(ResCMiniRoomBaseDlg.getTradeConfirmation());

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
                local.cancel(c.getClient(), lz == 0 ? lz2 : lz);
            }
            partner.getChr().setTrade(null);
            c.setTrade(null);
        }
    }

    public static final void cancelTrade(final MapleTrade Localtrade, final MapleClient c) {
        Localtrade.cancel(c);

        final MapleTrade partner = Localtrade.getPartner();
        if (partner != null) {
            partner.cancel(partner.getChr().getClient());
            partner.getChr().setTrade(null);
        }
        if (Localtrade.chr.get() != null) {
            Localtrade.chr.get().setTrade(null);
        }
    }

    public static final void startTrade(final MapleCharacter c, boolean isPointTrade) {
        if (c.getTrade() == null) {
            c.setTrade(new MapleTrade((byte) 0, c, isPointTrade));
            c.getClient().getSession().write(ResCMiniRoomBaseDlg.getTradeStart(c.getClient(), c.getTrade(), (byte) 0, isPointTrade));
        } else {
            c.getClient().getSession().write(ResWrapper.BroadCastMsgEvent("You are already in a trade"));
        }
    }

    public static final void inviteTrade(final MapleCharacter c1, final MapleCharacter c2) {
        if (c1 == null || c1.getTrade() == null) {
            return;
        }
        if (c2 != null && c2.getTrade() == null) {
            c2.setTrade(new MapleTrade((byte) 1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().getSession().write(ResCMiniRoomBaseDlg.getTradeInvite(c1, c1.getTrade().IsPointTrading()));
        } else {
            c1.getClient().getSession().write(ResWrapper.BroadCastMsgEvent("The other player is already trading with someone else."));
            cancelTrade(c1.getTrade(), c1.getClient());
        }
    }

    public static final void visitTrade(final MapleCharacter c1, final MapleCharacter c2, boolean isPointTrade) {
        if (c1.getTrade() != null && c1.getTrade().getPartner() == c2.getTrade() && c2.getTrade() != null && c2.getTrade().getPartner() == c1.getTrade()) {
            // We don't need to check for map here as the user is found via MapleMap.getCharacterById()
            c2.getClient().getSession().write(ResCMiniRoomBaseDlg.getTradePartnerAdd(c1));
            c1.getClient().getSession().write(ResCMiniRoomBaseDlg.getTradeStart(c1.getClient(), c1.getTrade(), (byte) 1, isPointTrade));
            //c1.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
            //c2.dropMessage(-2, "System : Use @tradehelp to see the list of trading commands");
        } else {
            c1.getClient().getSession().write(ResWrapper.BroadCastMsgEvent("The other player has already closed the trade"));
        }
    }

    public static final void declineTrade(final MapleCharacter c) {
        final MapleTrade trade = c.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                MapleCharacter other = trade.getPartner().getChr();
                other.getTrade().cancel(other.getClient());
                other.setTrade(null);
                other.dropMessage(5, c.getName() + " has declined your trade request");
            }
            trade.cancel(c.getClient());
            c.setTrade(null);
        }
    }
}
