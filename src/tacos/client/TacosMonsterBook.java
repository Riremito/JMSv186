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
import odin.server.MapleItemInformationProvider;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class TacosMonsterBook {

    private int nMonsterBookCoverID = 0;
    private final LinkedHashMap<Integer, Integer> cards;
    private int nLevel = 1;
    private int nNormal = 0;
    private int nSpecial = 0;
    private int nTotal = 0;
    private int nCoverMobID = 0;
    private int modified_count = 0;

    public TacosMonsterBook() {
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
        this.nLevel = 1;
        this.nNormal = 0;
        this.nSpecial = 0;
        this.nTotal = this.cards.size();

        for (Map.Entry<Integer, Integer> card : this.cards.entrySet()) {
            if (card.getValue() <= 0) {
                continue;
            }
            // boss monster or not.
            if (TacosConstants.is_special_monster_card(card.getKey())) {
                this.nSpecial++;
            } else {
                this.nNormal++;
            }
        }

        // MonsterBookInfo::GetBookLevel
        int total_cards = 0;
        for (int book_level = 1; book_level < 8; book_level++) {
            total_cards += (book_level * 10);
            if (this.nTotal <= total_cards) {
                break;
            }
            this.nLevel++;
        }
    }

    public int getCardCount(int nCardID) {
        Integer nCardCount = cards.get(nCardID);
        if (nCardCount == null) {
            return 0;
        }
        return nCardCount;
    }

    public boolean add(int nCardID, int quantity) {
        return TacosMonsterBook.this.add(nCardID, quantity, true);
    }

    public boolean add(int nCardID, int quantity, boolean auto_update) {
        if (!TacosConstants.is_monster_card(nCardID)) {
            return false;
        }

        int nCardCount = getCardCount(nCardID);
        if (5 <= nCardCount) {
            return false;
        }

        nCardCount += quantity;
        nCardCount = Math.min(nCardCount, 5);

        this.cards.put(nCardID, nCardCount);
        if (auto_update) {
            update();
        }

        this.modified_count++;
        return true;
    }

    public int getModifiedCount() {
        return this.modified_count;
    }

    public void resetModifiedCount() {
        this.modified_count = 0;
    }
}
