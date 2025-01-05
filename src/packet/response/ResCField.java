/*
 * Copyright (C) 2025 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package packet.response;

import client.MapleCharacter;
import client.MapleClient;
import debug.Debug;
import handling.MaplePacket;
import java.util.List;
import packet.ServerPacket;
import packet.response.struct.TestHelper;
import server.shops.AbstractPlayerStore;
import server.shops.HiredMerchant;
import server.shops.IMaplePlayerShop;
import server.shops.MapleMiniGame;
import server.shops.MaplePlayerShop;
import server.shops.MaplePlayerShopItem;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCField {

    public static MaplePacket playCashSong(int itemid, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_PlayJukeBox.Get());
        mplew.writeInt(itemid);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static MaplePacket multiChat(String name, String chattext, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GroupMessage.Get());
        mplew.write(mode); //  0 buddychat; 1 partychat; 2 guildchat
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);
        return mplew.getPacket();
    }

    // environmentChange, musicChange, showEffect, playSound
    // ShowBossHP, trembleEffect
    public static MaplePacket FieldEffect(FieldResponse.FieldEffectStruct st) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FieldEffect);
        sp.Encode1(st.flag.get());
        switch (st.flag) {
            case FieldEffect_Summon:
                {
                    sp.Encode1(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    break;
                }
        // 道場
            case FieldEffect_Tremble:
                {
                    sp.Encode1((byte) st.type);
                    sp.Encode4(st.delay);
                    break;
                }
            case FieldEffect_Object:
                {
                    sp.EncodeStr(st.wz_path);
                    break;
                }
            case FieldEffect_Screen:
                {
                    sp.EncodeStr(st.wz_path);
                    break;
                }
            case FieldEffect_Sound:
                {
                    sp.EncodeStr(st.wz_path);
                    break;
                }
            case FieldEffect_MobHPTag:
                {
                    sp.Encode4(st.monster.getId());
                    if (st.monster.getHp() > Integer.MAX_VALUE) {
                        sp.Encode4((int) (((double) st.monster.getHp() / st.monster.getMobMaxHp()) * Integer.MAX_VALUE));
                    } else {
                        sp.Encode4((int) st.monster.getHp());
                    }
                    if (st.monster.getMobMaxHp() > Integer.MAX_VALUE) {
                        sp.Encode4(Integer.MAX_VALUE);
                    } else {
                        sp.Encode4((int) st.monster.getMobMaxHp());
                    }
                    sp.Encode1(st.monster.getStats().getTagColor());
                    sp.Encode1(st.monster.getStats().getTagBgColor());
                    break;
                }
            case FieldEffect_ChangeBGM:
                {
                    sp.EncodeStr(st.wz_path);
                    break;
                }
            case FieldEffect_RewordRullet:
                {
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    break;
                }
            default:
                {
                    Debug.ErrorLog("FieldEffect not coded : " + st.flag);
                    break;
                }
        }
        return sp.Get();
    }

    public static final MaplePacket shopItemUpdate(final IMaplePlayerShop shop) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(22); // 0x17
        if (shop.getShopType() == 1) {
            mplew.writeInt(0);
        }
        mplew.write(shop.getItems().size());
        for (final MaplePlayerShopItem item : shop.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            TestHelper.addItemInfo(mplew, item.item, true, true);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameNewVisitor(MapleCharacter c, int slot, MapleMiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(4);
        mplew.write(slot);
        TestHelper.addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeShort(c.getJob());
        addGameInfo(mplew, c, game);
        return mplew.getPacket();
    }

    public static final MaplePacket shopMessage(final int type) {
        // show when closed the shop
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 0x28 = All of your belongings are moved successfully.
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(type);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static void addGameInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, MapleMiniGame game) {
        mplew.writeInt(game.getGameType()); // start of visitor; unknown
        mplew.writeInt(game.getWins(chr));
        mplew.writeInt(game.getTies(chr));
        mplew.writeInt(game.getLosses(chr));
        mplew.writeInt(game.getScore(chr)); // points
    }

    public static MaplePacket getMiniGameMoveOmok(int move1, int move2, int move3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(61);
        mplew.writeInt(move1);
        mplew.writeInt(move2);
        mplew.write(move3);
        return mplew.getPacket();
    }

    public static final MaplePacket MerchantBlackListView(final List<String> blackList) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(44); // 45
        mplew.writeShort(blackList.size());
        for (int i = 0; i < blackList.size(); i++) {
            if (blackList.get(i) != null) {
                mplew.writeMapleAsciiString(blackList.get(i));
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameExitAfter(boolean ready) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(ready ? 53 : 54);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameResult(MapleMiniGame game, int type, int x) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(59);
        mplew.write(type); //lose = 0, tie = 1, win = 2
        game.setPoints(x, type);
        if (type != 0) {
            game.setPoints(x == 1 ? 0 : 1, type == 2 ? 0 : 1);
        }
        if (type != 1) {
            if (type == 0) {
                mplew.write(x == 1 ? 0 : 1); //who did it?
            } else {
                mplew.write(x);
            }
        }
        addGameInfo(mplew, game.getMCOwner(), game);
        for (Pair<Byte, MapleCharacter> visitorz : game.getVisitors()) {
            addGameInfo(mplew, visitorz.right, game);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket getPlayerStore(final MapleCharacter chr, final boolean firstTime) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        IMaplePlayerShop ips = chr.getPlayerShop();
        switch (ips.getShopType()) {
            case 2:
                mplew.write(5);
                mplew.write(4);
                mplew.write(4);
                break;
            case 3:
                mplew.write(5);
                mplew.write(2);
                mplew.write(2);
                break;
            case 4:
                mplew.write(5);
                mplew.write(1);
                mplew.write(2);
                break;
        }
        mplew.writeShort(ips.getVisitorSlot(chr));
        TestHelper.addCharLook(mplew, ((MaplePlayerShop) ips).getMCOwner(), false);
        mplew.writeMapleAsciiString(ips.getOwnerName());
        mplew.writeShort(((MaplePlayerShop) ips).getMCOwner().getJob());
        for (final Pair<Byte, MapleCharacter> storechr : ips.getVisitors()) {
            mplew.write(storechr.left);
            TestHelper.addCharLook(mplew, storechr.right, false);
            mplew.writeMapleAsciiString(storechr.right.getName());
            mplew.writeShort(storechr.right.getJob());
        }
        mplew.write(255);
        mplew.writeMapleAsciiString(ips.getDescription());
        mplew.write(10);
        mplew.write(ips.getItems().size());
        for (final MaplePlayerShopItem item : ips.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            TestHelper.addItemInfo(mplew, item.item, true, true);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket updateHiredMerchant(final HiredMerchant shop) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_EmployeeMiniRoomBalloon.Get());
        mplew.writeInt(shop.getOwnerId());
        TestHelper.addInteraction(mplew, shop);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameReady(boolean ready) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(ready ? 55 : 56);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameStart(int loser) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(58);
        mplew.write(loser == 1 ? 0 : 1);
        return mplew.getPacket();
    }

    public static final MaplePacket shopBlockPlayer(final byte slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(10);
        // キャラクターの場所を正しく指定しないとD/C
        mplew.write(slot);
        // 強制退場されました。
        mplew.write(5);
        return mplew.getPacket();
    }

    public static final MaplePacket MerchantVisitorView(List<String> visitor) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(43); // 44
        mplew.writeShort(visitor.size());
        for (String visit : visitor) {
            mplew.writeMapleAsciiString(visit);
            mplew.writeInt(1); /////for the lul
        }
        return mplew.getPacket();
    }

    public static final MaplePacket shopVisitorAdd(final MapleCharacter chr, final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(4);
        mplew.write(slot);
        TestHelper.addCharLook(mplew, chr, false);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeShort(chr.getJob());
        return mplew.getPacket();
    }

    public static final MaplePacket shopChat(final String message, final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(6);
        mplew.write(8);
        mplew.write(slot);
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.writeShort(5);
        mplew.write(2);
        return mplew.getPacket();
    }

    // 雇用商人 整理中 (他人用)
    public static MaplePacket MaintenanceHiredMerchant(int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        // UIを閉じる
        mplew.write(10);
        // キャラクターを指定
        mplew.write(slot);
        // UI閉じるときのメッセージ（何も表示しない設定)
        mplew.write(17);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameClose(byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(10);
        mplew.write(1);
        mplew.write(number);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameRequestTie() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(48);
        return mplew.getPacket();
    }

    // 雇用商人 閉店
    public static MaplePacket CloseHiredMerchant() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        // UIを閉じる
        mplew.write(10);
        // 自分のキャラクターを指定
        mplew.write(0);
        // UI閉じるときのメッセージ（何も表示しない設定)
        mplew.write(20);
        return mplew.getPacket();
    }

    public static final MaplePacket Merchant_Buy_Error(final byte message) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 2 = You have not enough meso
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(24);
        mplew.write(message);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameDenyTie() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(49);
        return mplew.getPacket();
    }

    public static final MaplePacket shopVisitorLeave(final byte slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(10);
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static final MaplePacket shopErrorMessage(final int error, final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(10);
        // 退場する人の番号 0 = 開いている人
        mplew.write(type);
        /*
        商店を閉じる理由
        0   =   なし
        1   =   ここではオープン出来ません。
        2   =   なし
        3   =   商店が閉じています
        4   =   なし
        5   =   強制退場されました。
        6   =   制限時間が経過し、商店を開くことができませんした
        7   =   なし
        8   =   なし
        9   =   なし
        10  =   なし
        11  =   なし
        12  =   なし
        13  =   なし
        14  =   品物は売れ切れです。
         */
        mplew.write(error);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGame(MapleClient c, MapleMiniGame minigame) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(5);
        mplew.write(minigame.getGameType());
        mplew.write(minigame.getMaxSize());
        mplew.writeShort(minigame.getVisitorSlot(c.getPlayer()));
        TestHelper.addCharLook(mplew, minigame.getMCOwner(), false);
        mplew.writeMapleAsciiString(minigame.getOwnerName());
        mplew.writeShort(minigame.getMCOwner().getJob());
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            mplew.write(visitorz.getLeft());
            TestHelper.addCharLook(mplew, visitorz.getRight(), false);
            mplew.writeMapleAsciiString(visitorz.getRight().getName());
            mplew.writeShort(visitorz.getRight().getJob());
        }
        mplew.write(-1);
        mplew.write(0);
        addGameInfo(mplew, minigame.getMCOwner(), minigame);
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            mplew.write(visitorz.getLeft());
            addGameInfo(mplew, visitorz.getRight(), minigame);
        }
        mplew.write(-1);
        mplew.writeMapleAsciiString(minigame.getDescription());
        mplew.writeShort(minigame.getPieceType());
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameSkip(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(60);
        //owner = 1 visitor = 0?
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static final MaplePacket getHiredMerch(final MapleCharacter chr, final HiredMerchant merch, final boolean firstTime) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(5);
        mplew.write(5);
        mplew.write(4);
        mplew.writeShort(merch.getVisitorSlot(chr));
        mplew.writeInt(merch.getItemId());
        mplew.writeMapleAsciiString("\u96c7\u7528\u5546\u4eba");
        for (final Pair<Byte, MapleCharacter> storechr : merch.getVisitors()) {
            mplew.write(storechr.left);
            TestHelper.addCharLook(mplew, storechr.right, false);
            mplew.writeMapleAsciiString(storechr.right.getName());
            mplew.writeShort(storechr.right.getJob());
        }
        mplew.write(-1);
        mplew.writeShort(0);
        mplew.writeMapleAsciiString(merch.getOwnerName());
        if (merch.isOwner(chr)) {
            mplew.writeInt(merch.getTimeLeft());
            mplew.write(firstTime ? 1 : 0);
            mplew.write(merch.getBoughtItems().size());
            for (AbstractPlayerStore.BoughtItem SoldItem : merch.getBoughtItems()) {
                mplew.writeInt(SoldItem.id);
                mplew.writeShort(SoldItem.quantity); // number of purchased
                mplew.writeInt(SoldItem.totalPrice); // total price
                mplew.writeMapleAsciiString(SoldItem.buyer); // name of the buyer
            }
            mplew.writeInt(merch.getMeso());
        }
        mplew.writeMapleAsciiString(merch.getDescription());
        mplew.write(10);
        mplew.writeInt(merch.getMeso()); // meso
        mplew.write(merch.getItems().size());
        for (final MaplePlayerShopItem item : merch.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            TestHelper.addItemInfo(mplew, item.item, true, true);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getMatchCardStart(MapleMiniGame game, int loser) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(58);
        mplew.write(loser == 1 ? 0 : 1);
        int times = game.getPieceType() == 1 ? 20 : (game.getPieceType() == 2 ? 30 : 12);
        mplew.write(times);
        for (int i = 1; i <= times; i++) {
            mplew.writeInt(game.getCardId(i));
        }
        return mplew.getPacket();
    }

    public static MaplePacket getMatchCardSelect(int turn, int slot, int firstslot, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(65);
        mplew.write(turn);
        mplew.write(slot);
        if (turn == 0) {
            mplew.write(firstslot);
            mplew.write(type);
        }
        return mplew.getPacket();
    }
    
}
