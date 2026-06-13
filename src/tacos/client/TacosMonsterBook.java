/*
 * Copyright (C) 2026 Riremito
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
package tacos.client;

import java.util.LinkedHashMap;
import java.util.Map;
import odin.constants.GameConstants;
import odin.server.MapleItemInformationProvider;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.packet.response.wrapper.WrapCUserLocal;
import tacos.packet.response.wrapper.WrapCUserRemote;

/**
 *
 * @author Riremito
 */
public class TacosMonsterBook {

    private final TacosCharacter chr;
    private final LinkedHashMap<Integer, Integer> cards;
    private boolean changed = false;
    private int SpecialCard = 0;
    private int NormalCard = 0;
    private int BookLevel = 1;

    public TacosMonsterBook(TacosCharacter chr) {
        this.chr = chr;
        this.cards = new LinkedHashMap<>();
    }

    public LinkedHashMap<Integer, Integer> getCards() {
        return this.cards;
    }

    public void update() {
        this.SpecialCard = 0;
        this.NormalCard = 0;
        for (Map.Entry<Integer, Integer> card : this.cards.entrySet()) {
            if (GameConstants.isSpecialCard(card.getKey())) {
                this.SpecialCard += card.getValue();
            } else {
                this.NormalCard += card.getValue();
            }
        }
        int Size = this.NormalCard + this.SpecialCard;
        this.BookLevel = 8;

        for (int i = 0; i < 8; i++) {
            if (Size <= GameConstants.getBookLevel(i)) {
                this.BookLevel = (i + 1);
                break;
            }
        }
    }

    public int getLevel(int cardid) {
        if (cards.containsKey(cardid)) {
            return cards.get(cardid);
        }
        return 0;
    }

    public int getTotalCards() {
        return SpecialCard + NormalCard;
    }

    public int getLevelByCard(int cardid) {
        return cards.get(cardid) == null ? 0 : cards.get(cardid);
    }

    // pakcet
    public byte[] addCardPacket() {
        ServerPacket data = new ServerPacket();

        data.Encode2(cards.size());

        for (Map.Entry<Integer, Integer> all : cards.entrySet()) {
            data.Encode2(GameConstants.getCardShortId(all.getKey())); // Id
            data.Encode1(all.getValue()); // Level
        }

        return data.getBytes();
    }

    // addCharInfoPacket
    public byte[] MonsterBookInfo(int bookcover) {
        ServerPacket data = new ServerPacket();

        data.Encode4(BookLevel);
        data.Encode4(NormalCard);
        data.Encode4(SpecialCard);
        data.Encode4(NormalCard + SpecialCard);
        data.Encode4(MapleItemInformationProvider.getInstance().getCardMobId(bookcover));
        return data.getBytes();
    }

    public void addCard(int cardid) {
        changed = true;
        this.chr.getMap().broadcastMessage(this.chr, WrapCUserRemote.EffectRemote(OpsUserEffect.UserEffect_MonsterBookCardGet, this.chr), false);

        if (cards.containsKey(cardid)) {
            final int levels = cards.get(cardid);
            if (levels >= 5) {
                this.chr.SendPacket(ResCWvsContext.MonsterBookSetCard(true, cardid, levels));
            } else {
                if (GameConstants.isSpecialCard(cardid)) {
                    SpecialCard += 1;
                } else {
                    NormalCard += 1;
                }
                this.chr.SendPacket(ResCWvsContext.MonsterBookSetCard(false, cardid, 5));
                this.chr.SendPacket(ResWrapper.showGainCard(cardid));
                this.chr.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_MonsterBookCardGet));
                cards.put(cardid, 5);
                update();
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
        this.chr.SendPacket(ResCWvsContext.MonsterBookSetCard(false, cardid, 5));
        this.chr.SendPacket(ResWrapper.showGainCard(cardid));
        this.chr.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_MonsterBookCardGet));
        update();
    }
}
