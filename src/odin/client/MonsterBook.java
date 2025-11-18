/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

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
package odin.client;

import odin.constants.GameConstants;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.Serializable;

import tacos.database.DatabaseConnection;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.packet.response.wrapper.WrapCUserLocal;
import tacos.packet.response.wrapper.WrapCUserRemote;
import odin.server.MapleItemInformationProvider;

public class MonsterBook implements Serializable {

    private static final long serialVersionUID = 7179541993413738569L;
    private boolean changed = false;
    private int SpecialCard = 0, NormalCard = 0, BookLevel = 1;
    private Map<Integer, Integer> cards;

    public MonsterBook(Map<Integer, Integer> cards) {
        this.cards = cards;

        for (Entry<Integer, Integer> card : cards.entrySet()) {
            if (GameConstants.isSpecialCard(card.getKey())) {

                SpecialCard += card.getValue();
            } else {
                NormalCard += card.getValue();
            }
        }
        calculateLevel();
    }

    public Map<Integer, Integer> getCards() {
        return cards;
    }

    public final int getTotalCards() {
        return SpecialCard + NormalCard;
    }

    public final int getLevelByCard(final int cardid) {
        return cards.get(cardid) == null ? 0 : cards.get(cardid);
    }

    public final static MonsterBook loadCards(final int charid) throws SQLException {
        final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM monsterbook WHERE charid = ? ORDER BY cardid ASC");
        ps.setInt(1, charid);
        final ResultSet rs = ps.executeQuery();
        Map<Integer, Integer> cards = new LinkedHashMap<Integer, Integer>();
        int cardid, level;

        while (rs.next()) {
            cards.put(rs.getInt("cardid"), rs.getInt("level"));
        }
        rs.close();
        ps.close();
        return new MonsterBook(cards);
    }

    public final void saveCards(final int charid) throws SQLException {
        if (!changed || cards.size() == 0) {
            return;
        }
        final Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM monsterbook WHERE charid = ?");
        ps.setInt(1, charid);
        ps.execute();
        ps.close();

        boolean first = true;
        final StringBuilder query = new StringBuilder();

        for (final Entry<Integer, Integer> all : cards.entrySet()) {
            if (first) {
                first = false;
                query.append("INSERT INTO monsterbook VALUES (DEFAULT,");
            } else {
                query.append(",(DEFAULT,");
            }
            query.append(charid);
            query.append(",");
            query.append(all.getKey()); // Card ID
            query.append(",");
            query.append(all.getValue()); // Card level
            query.append(")");
        }
        ps = con.prepareStatement(query.toString());
        ps.execute();
        ps.close();
    }

    private final void calculateLevel() {
        int Size = NormalCard + SpecialCard;
        BookLevel = 8;

        for (int i = 0; i < 8; i++) {
            if (Size <= GameConstants.getBookLevel(i)) {
                BookLevel = (i + 1);
                break;
            }
        }
    }

    public final byte[] addCardPacket() {
        ServerPacket data = new ServerPacket();

        data.Encode2(cards.size());

        for (Entry<Integer, Integer> all : cards.entrySet()) {
            data.Encode2(GameConstants.getCardShortId(all.getKey())); // Id
            data.Encode1(all.getValue()); // Level
        }

        return data.get().getBytes();
    }

    // addCharInfoPacket
    public final byte[] MonsterBookInfo(final int bookcover) {
        ServerPacket data = new ServerPacket();

        data.Encode4(BookLevel);
        data.Encode4(NormalCard);
        data.Encode4(SpecialCard);
        data.Encode4(NormalCard + SpecialCard);
        data.Encode4(MapleItemInformationProvider.getInstance().getCardMobId(bookcover));
        return data.get().getBytes();
    }

    public final void updateCard(final MapleClient c, final int cardid) {
        c.getSession().write(ResCWvsContext.changeCover(cardid));
    }

    public final int getLevel(final int cardid) {
        if (cards.containsKey(cardid)) {
            return cards.get(cardid);
        }
        return 0;
    }

    public final void addCard(final MapleClient c, final int cardid) {
        changed = true;
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), WrapCUserRemote.EffectRemote(OpsUserEffect.UserEffect_MonsterBookCardGet, c.getPlayer()), false);

        if (cards.containsKey(cardid)) {
            final int levels = cards.get(cardid);
            if (levels >= 5) {
                c.getSession().write(ResCWvsContext.addCard(true, cardid, levels));
            } else {
                if (GameConstants.isSpecialCard(cardid)) {
                    SpecialCard += 1;
                } else {
                    NormalCard += 1;
                }
                c.getSession().write(ResCWvsContext.addCard(false, cardid, 5));
                c.SendPacket(ResWrapper.showGainCard(cardid));
                c.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_MonsterBookCardGet));
                cards.put(cardid, 5);
                calculateLevel();
            }
            return;
        }
        if (GameConstants.isSpecialCard(cardid)) {
            SpecialCard += 1;
        } else {
            NormalCard += 1;
        }
        // New card
        cards.put(cardid, 5);
        c.getSession().write(ResCWvsContext.addCard(false, cardid, 5));
        c.SendPacket(ResWrapper.showGainCard(cardid));
        c.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_MonsterBookCardGet));
        calculateLevel();
    }
}
