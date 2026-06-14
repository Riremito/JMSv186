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
import tacos.debug.DebugLogger;
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
    private int nMonsterBookCoverID = 0;
    private final LinkedHashMap<Integer, Integer> cards;
    private boolean changed = false;
    private int nLevel = 1;
    private int nNormal = 0;
    private int nSpecial = 0;
    private int nTotal = 0;
    private int nCoverMobID = 0;
    
    public TacosMonsterBook(TacosCharacter chr) {
        this.chr = chr;
        this.nMonsterBookCoverID = 0;
        this.cards = new LinkedHashMap<>();
    }
    
    public int getLevel() {
        return this.nLevel;
    }
    
    public int getNormal() {
        return this.nNormal;
    }
    
    public int getSpecial() {
        return this.nSpecial;
    }
    
    public int getTotal() {
        return this.nTotal;
    }
    
    public int getCoverMobID() {
        return this.nCoverMobID;
    }
    
    public int getCover() {
        return this.nMonsterBookCoverID;
    }
    
    public boolean setCover(int nMonsterBookCoverID) {
        if (nMonsterBookCoverID != 0) {
            int mob_id = MapleItemInformationProvider.getInstance().getCardMobId(nMonsterBookCoverID);
            if (mob_id == 0) {
                DebugLogger.ErrorLog("setCover : invalid nMonsterBookCoverID.");
                return false;
            }
            this.nCoverMobID = mob_id;
        }
        
        this.nMonsterBookCoverID = nMonsterBookCoverID;
        return true;
    }
    
    public LinkedHashMap<Integer, Integer> getCards() {
        return this.cards;
    }
    
    public void update() {
        this.nSpecial = 0;
        this.nNormal = 0;
        for (Map.Entry<Integer, Integer> card : this.cards.entrySet()) {
            if (GameConstants.isSpecialCard(card.getKey())) {
                this.nSpecial += card.getValue();
            } else {
                this.nNormal += card.getValue();
            }
        }
        this.nTotal = this.nNormal + this.nSpecial;
        this.nLevel = 8;
        
        for (int i = 0; i < 8; i++) {
            if (this.nTotal <= GameConstants.getBookLevel(i)) {
                this.nLevel = (i + 1);
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
        return nSpecial + nNormal;
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
    
    public void addCard(int cardid) {
        changed = true;
        this.chr.getMap().broadcastMessage(this.chr, WrapCUserRemote.EffectRemote(OpsUserEffect.UserEffect_MonsterBookCardGet, this.chr), false);
        
        if (cards.containsKey(cardid)) {
            final int levels = cards.get(cardid);
            if (levels >= 5) {
                this.chr.SendPacket(ResCWvsContext.MonsterBookSetCard(true, cardid, levels));
            } else {
                if (GameConstants.isSpecialCard(cardid)) {
                    nSpecial += 1;
                } else {
                    nNormal += 1;
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
            nSpecial += 1;
        } else {
            nNormal += 1;
        }
        // New card
        cards.put(cardid, 5);
        this.chr.SendPacket(ResCWvsContext.MonsterBookSetCard(false, cardid, 5));
        this.chr.SendPacket(ResWrapper.showGainCard(cardid));
        this.chr.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_MonsterBookCardGet));
        update();
    }
}
