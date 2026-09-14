/*
This file is part of the ZeroFusion MapleStory Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>
ZeroFusion organized by "RMZero213" <RMZero213@hotmail.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.server.shops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import odin.client.MapleQuestStatus;
import odin.server.quest.MapleQuest;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.ServerPacket;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapObjectType;
import odin.constants.GameConstants;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.ItemLoader;
import tacos.database.query.DQ_Hiredmerch;
import tacos.server.TacosWorld;
import tacos.packet.response.ResCUser;
import java.sql.SQLException;

public class MapleMiniGame {

    public final static byte HIRED_MERCHANT = 1;
    public final static byte PLAYER_SHOP = 2;
    public final static byte OMOK = 3;
    public final static byte MATCH_CARD = 4;

    private Point position = new Point();
    private int objectId;

    protected boolean open = false;
    protected boolean available = false;
    protected String ownerName;
    protected String des;
    protected String pass;
    protected int ownerId;
    protected int owneraccount;
    protected int itemId;
    protected int channel;
    protected int map;
    protected AtomicInteger meso = new AtomicInteger(0);
    protected WeakReference<MapleCharacter> chrs[];
    protected List<String> visitors = new LinkedList<>();
    protected List<BoughtItem> bought = new LinkedList<>();
    protected List<MaplePlayerShopItem> items = new LinkedList<>();

    private final static int slots = 2; //change?!
    private boolean[] exitAfter;
    private boolean[] ready;
    private int[] points;
    private int GameType = 0;
    private int[][] piece = new int[15][15];
    private List<Integer> matchcards = new ArrayList<>();
    int loser = 0;
    int turn = 1;
    int piecetype = 0;
    int firstslot = 0;
    int tie = -1;

    @SuppressWarnings("unchecked")
    public MapleMiniGame(MapleCharacter owner, int itemId, String description, String pass, int GameType) {
        this.setPosition(owner.getPosition());
        this.ownerName = owner.getName();
        this.ownerId = owner.getId();
        this.owneraccount = owner.getAccountId();
        this.itemId = itemId;
        this.des = description;
        this.pass = pass;
        this.map = owner.getMapId();
        this.channel = owner.getClient().getChannelId();
        chrs = new WeakReference[slots - 1];
        for (int i = 0; i < chrs.length; i++) {
            chrs[i] = new WeakReference<>(null);
        }
        this.GameType = GameType;
        this.points = new int[slots];
        this.exitAfter = new boolean[slots];
        this.ready = new boolean[slots];
        reset();
    }

    public Point getPosition() {
        return new Point(position);
    }

    public void setPosition(Point position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int id) {
        this.objectId = id;
    }

    public int getMaxSize() {
        return chrs.length + 1;
    }

    public int getSize() {
        return getFreeSlot() == -1 ? getMaxSize() : getFreeSlot();
    }

    public void broadcastToVisitors(ServerPacket packet) {
        broadcastToVisitors(packet, true);
    }

    public void broadcastToVisitors(ServerPacket packet, boolean owner) {
        for (WeakReference<MapleCharacter> chr : chrs) {
            if (chr != null && chr.get() != null) {
                chr.get().SendPacket(packet);
            }
        }
        if (getShopType() != HIRED_MERCHANT && owner && getMCOwner() != null) {
            getMCOwner().SendPacket(packet);
        }
    }

    public void broadcastToVisitors(ServerPacket packet, int exception) {
        for (WeakReference<MapleCharacter> chr : chrs) {
            if (chr != null && chr.get() != null && getVisitorSlot(chr.get()) != exception) {
                chr.get().SendPacket(packet);
            }
        }
        if (getShopType() != HIRED_MERCHANT && getMCOwner() != null && exception != ownerId) {
            getMCOwner().SendPacket(packet);
        }
    }

    public int getMeso() {
        return meso.get();
    }

    public void setMeso(int meso) {
        this.meso.set(meso);
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean saveItems() {
        if (getShopType() != HIRED_MERCHANT) { //hired merch only
            return false;
        }
        Integer packageid = DQ_Hiredmerch.add(ownerId, owneraccount, meso.get());
        if (packageid == null) {
            return false;
        }
        List<SimpleImmutableEntry<Item, MapleInventoryType>> iters = new ArrayList<>();
        Item item;
        for (MaplePlayerShopItem pItems : items) {
            if (pItems.item == null || pItems.bundles <= 0) {
                continue;
            }
            if (pItems.item.getQuantity() <= 0 && !GameConstants.isRechargable(pItems.item.getItemId())) {
                continue;
            }
            item = pItems.item.copy();
            item.setQuantity((short) (item.getQuantity() * pItems.bundles));
            iters.add(new SimpleImmutableEntry<>(item, GameConstants.getInventoryType(item.getItemId())));
        }
        try {
            ItemLoader.HIRED_MERCHANT.saveItems(iters, packageid, owneraccount, ownerId);
            return true;
        } catch (SQLException se) {
        }
        return false;
    }

    public MapleCharacter getVisitor(int num) {
        return chrs[num].get();
    }

    public void update() {
        if (isAvailable() && getMCOwner() != null) {
            getMap().broadcastMessage(ResCUser.sendPlayerShopBox(getMCOwner()));
        }
    }

    public void addVisitor(MapleCharacter visitor) {
        int i = getFreeSlot();
        if (i > 0) {
            broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameNewVisitor(visitor, i, this));
            chrs[i - 1] = new WeakReference<>(visitor);
            if (!isOwner(visitor)) {
                visitors.add(visitor.getName());
            }
            if (i == 3) {
                update();
            }
        }
    }

    public void removeVisitor(MapleCharacter visitor) {
        final byte slot = getVisitorSlot(visitor);
        boolean shouldUpdate = getFreeSlot() == -1;
        if (slot > 0) {
            broadcastToVisitors(ResCMiniRoomBaseDlg.shopVisitorLeave(slot), slot);
            chrs[slot - 1] = new WeakReference<>(null);
            if (shouldUpdate) {
                update();
            }
        }
    }

    public byte getVisitorSlot(MapleCharacter visitor) {
        for (byte i = 0; i < chrs.length; i++) {
            if (chrs[i] != null && chrs[i].get() != null && chrs[i].get().getId() == visitor.getId()) {
                return (byte) (i + 1);
            }
        }
        if (visitor.getId() == ownerId) { //can visit own store in merch, otherwise not.
            return 0;
        }
        return -1;
    }

    public void removeAllVisitors(int error, int type) {
        for (int i = 0; i < chrs.length; i++) {
            MapleCharacter visitor = getVisitor(i);
            if (visitor != null) {
                if (type != -1) {
                    visitor.SendPacket(ResCMiniRoomBaseDlg.shopErrorMessage(error, type));
                }
                broadcastToVisitors(ResCMiniRoomBaseDlg.shopVisitorLeave(getVisitorSlot(visitor)), getVisitorSlot(visitor));
                visitor.setPlayerShop(null);
                chrs[i] = new WeakReference<>(null);
            }
        }
        update();
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getOwnerAccId() {
        return owneraccount;
    }

    public String getDescription() {
        if (des == null) {
            return "";
        }
        return des;
    }

    public List<SimpleImmutableEntry<Byte, MapleCharacter>> getVisitors() {
        List<SimpleImmutableEntry<Byte, MapleCharacter>> chrz = new LinkedList<>();
        for (byte i = 0; i < chrs.length; i++) { //include owner or no
            if (chrs[i] != null && chrs[i].get() != null) {
                chrz.add(new SimpleImmutableEntry<>((byte) (i + 1), chrs[i].get()));
            }
        }
        return chrz;
    }

    public List<MaplePlayerShopItem> getItems() {
        return items;
    }

    public void addItem(MaplePlayerShopItem item) {
        items.add(item);
    }

    public boolean removeItem(int item) {
        return false;
    }

    public void removeFromSlot(int slot) {
        items.remove(slot);
    }

    public byte getFreeSlot() {
        for (byte i = 0; i < chrs.length; i++) {
            if (chrs[i] == null || chrs[i].get() == null) {
                return (byte) (i + 1);
            }
        }
        return -1;
    }

    public int getItemId() {
        return itemId;
    }

    public boolean isOwner(MapleCharacter chr) {
        return chr.getId() == ownerId && chr.getName().equals(ownerName);
    }

    public String getPassword() {
        if (pass == null) {
            return "";
        }
        return pass;
    }

    public void sendDestroyData(TacosClient client) {
    }

    public void sendSpawnData(TacosClient client) {
    }

    public MapleMapObjectType getType() {
        return MapleMapObjectType.SHOP;
    }

    public MapleCharacter getMCOwner() {
        return getMap().getCharacterById(ownerId);
    }

    public MapleMap getMap() {
        return TacosWorld.find(0).getChannelServer(channel).findMap(map);
    }

    public int getGameType() {
        if (getShopType() == HIRED_MERCHANT) { //hiredmerch
            return 5;
        } else if (getShopType() == PLAYER_SHOP) { //shop lol
            return 4;
        } else if (getShopType() == OMOK) { //omok
            return 1;
        } else if (getShopType() == MATCH_CARD) { //matchcard
            return 2;
        }
        return 0;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean b) {
        this.available = b;
    }

    public List<BoughtItem> getBoughtItems() {
        return bought;
    }

    public static final class BoughtItem {

        public int id;
        public int quantity;
        public int totalPrice;
        public String buyer;

        public BoughtItem(final int id, final int quantity, final int totalPrice, final String buyer) {
            this.id = id;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.buyer = buyer;
        }
    }

    public void reset() {
        for (int i = 0; i < slots; i++) {
            points[i] = 0;
            exitAfter[i] = false;
            ready[i] = false;
        }
    }

    public void setFirstSlot(int type) {
        firstslot = type;
    }

    public int getFirstSlot() {
        return firstslot;
    }

    public void setPoints(int slot) {
        points[slot]++;
        checkWin();
    }

    public int getPoints() {
        int ret = 0;
        for (int i = 0; i < slots; i++) {
            ret += points[i];
        }
        return ret;
    }

    public void checkWin() {
        if (getPoints() >= getMatchesToWin() && isOpen()) {
            int x = 0;
            int highest = 0;
            boolean tie = false;
            for (int i = 0; i < slots; i++) {
                if (points[i] > highest) {
                    x = i;
                    highest = points[i];
                    tie = false;
                } else if (points[i] == highest) {
                    tie = true;
                }
                points[i] = 0;
            }
            this.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameResult(this, tie ? 1 : 2, x));
            this.setOpen(true);
            update();
            checkExitAfterGame();
        }
    }

    public void setPieceType(int type) {
        piecetype = type;
    }

    public int getPieceType() {
        return piecetype;
    }

    public void setGameType() {
        if (GameType == 2) { //omok = 1
            matchcards.clear();
            for (int i = 0; i < getMatchesToWin(); i++) {
                matchcards.add(i);
                matchcards.add(i);
            }
        }
    }

    public void shuffleList() {
        if (GameType == 2) {
            Collections.shuffle(matchcards);
        } else {
            piece = new int[15][15];
        }
    }

    public int getCardId(int slot) {
        return matchcards.get(slot - 1);
    }

    public int getMatchesToWin() {
        return (getPieceType() == 0 ? 6 : (getPieceType() == 1 ? 10 : 15));
    }

    public int getLoser() {
        return loser;
    }

    public void send(TacosClient client) {
        if (getMCOwner() == null) {
            closeShop(false, false, 0);
            return;
        }
        client.SendPacket(ResCMiniRoomBaseDlg.getMiniGame(client, this));
    }

    public void setReady(int slot) {
        ready[slot] = !ready[slot];
    }

    public boolean isReady(int slot) {
        return ready[slot];
    }

    public void setPiece(int move1, int move2, int type, MapleCharacter chr) {
        if (piece[move1][move2] == 0 && isOpen()) {
            piece[move1][move2] = type;
            // なんか勝てないし、if文の中に入れるとゲームが進まないので謎
            this.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameMoveOmok(move1, move2, type));
            boolean found = false;
            for (int y = 0; y < 15; y++) {
                for (int x = 0; x < 15; x++) {
                    if (!found && searchCombo(x, y, type)) {
                        this.broadcastToVisitors(ResCMiniRoomBaseDlg.getMiniGameResult(this, 2, getVisitorSlot(chr)));
                        this.setOpen(true);
                        update();
                        checkExitAfterGame();
                        found = true;
                    }
                }
            }
            nextLoser();
        }
    }

    public void nextLoser() { //lol
        loser++;
        if (loser > slots - 1) {
            loser = 0;
        }
    }

    public void exit(MapleCharacter player) {
        player.setPlayerShop(null);
        if (isOwner(player)) {
            update();
            removeAllVisitors(3, 1);
        } else {
            removeVisitor(player);
        }
    }

    public boolean isExitAfter(MapleCharacter player) {
        if (getVisitorSlot(player) > -1) {
            return this.exitAfter[getVisitorSlot(player)];
        }
        return false;
    }

    public void setExitAfter(MapleCharacter player) {
        if (getVisitorSlot(player) > -1) {
            this.exitAfter[getVisitorSlot(player)] = !this.exitAfter[getVisitorSlot(player)];
        }
    }

    public void checkExitAfterGame() {
        for (int i = 0; i < slots; i++) {
            if (exitAfter[i]) {
                exitAfter[i] = false;
                exit(i == 0 ? getMCOwner() : chrs[i - 1].get());
            }
        }
    }

    public boolean searchCombo(int x, int y, int type) {
        boolean ret = false;
        if (!ret && x < 11) {
            ret = true;
            for (int i = 0; i < 5; i++) {
                if (piece[x + i][y] != type) {
                    ret = false;
                    break;
                }
            }
        }
        if (!ret && y < 11) {
            ret = true;
            for (int i = 0; i < 5; i++) {
                if (piece[x][y + i] != type) {
                    ret = false;
                    break;
                }
            }
        }
        if (!ret && x < 11 && y < 11) {
            ret = true;
            for (int i = 0; i < 5; i++) {
                if (piece[x + i][y + i] != type) {
                    ret = false;
                    break;
                }
            }
        }
        if (!ret && x > 3 && y < 11) {
            ret = true;
            for (int i = 0; i < 5; i++) {
                if (piece[x - i][y + i] != type) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }

    public int getScore(MapleCharacter chr) {
        //TODO: Fix formula
        int score = 2000;
        int wins = getWins(chr);
        int ties = getTies(chr);
        int losses = getLosses(chr);
        if (wins + ties + losses > 0) {
            score += wins * 2;
            score += ties;
            score -= losses * 2;
        }
        return score;
    }

    public byte getShopType() {
        return GameType == 1 ? OMOK : MATCH_CARD;
    }

    //questids:
    //omok - win = 122200
    //matchcard - win = 122210
    //TODO: record points
    public int getWins(MapleCharacter chr) {
        return Integer.parseInt(getData(chr).split(",")[2]);
    }

    public int getTies(MapleCharacter chr) {
        return Integer.parseInt(getData(chr).split(",")[1]);
    }

    public int getLosses(MapleCharacter chr) {
        return Integer.parseInt(getData(chr).split(",")[0]);
    }

    public void setPoints(int i, int type) { //lose = 0, tie = 1, win = 2
        MapleCharacter z;
        if (i == 0) {
            z = getMCOwner();
        } else {
            z = getVisitor(i - 1);
        }
        if (z != null) {
            String[] data = getData(z).split(",");
            data[type] = String.valueOf(Integer.parseInt(data[type]) + 1);
            StringBuilder newData = new StringBuilder();
            for (int s = 0; s < data.length; s++) {
                newData.append(data[s]);
                newData.append(",");
            }
            String newDat = newData.toString();
            z.getQuestNAdd(MapleQuest.getInstance(GameType == 1 ? 122200 : 122210)).setCustomData(newDat.substring(0, newDat.length() - 1));
        }
    }

    public String getData(MapleCharacter chr) {
        MapleQuest quest = MapleQuest.getInstance(GameType == 1 ? 122200 : 122210);
        MapleQuestStatus record;
        if (chr.getQuestNoAdd(quest) == null) {
            record = chr.getQuestNAdd(quest);
            record.setCustomData("0,0,0");
        } else {
            record = chr.getQuestNoAdd(quest);
            if (record.getCustomData() == null || record.getCustomData().length() < 5 || record.getCustomData().indexOf(",") == -1) {
                record.setCustomData("0,0,0"); //refresh
            }
        }
        return record.getCustomData();
    }

    public int getRequestedTie() {
        return tie;
    }

    public void setRequestedTie(int t) {
        this.tie = t;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int t) {
        this.turn = t;
    }

    public void closeShop(boolean s, boolean z, int reason) {
        removeAllVisitors(3, 1);
        if (getMCOwner() != null) {
            getMCOwner().setPlayerShop(null);
        }
        update();
        getMap().removeMapObject(this);
    }

    public void buy(TacosClient client, int z, short i) {
    }
}
