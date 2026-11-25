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
package tacos.miniroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tacos.client.TacosCharacter;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.response.ResCMiniRoomBaseDlg;

/**
 *
 * @author Riremito
 */
public class MemoryGame extends MiniGame {

    private static final int RECORD_QUEST_ID = 122210;
    private final List<Integer> cards = new ArrayList<>();
    private final int[] matches = new int[2];
    // the card turned up first in this turn, -1 = none
    private int firstCard = -1;

    public MemoryGame(TacosCharacter owner, int itemId, String description, String password, int gameKind) {
        super(owner, itemId, description, password, gameKind);
    }

    @Override
    public OpsMiniRoomType getMiniRoomType() {
        return OpsMiniRoomType.MR_MemoryGameRoom;
    }

    @Override
    protected int getRecordQuestId() {
        return RECORD_QUEST_ID;
    }

    // the number of pairs, m_nGameKind 0 = 12 cards, 1 = 20 cards, 2 = 30 cards
    private int getPairs() {
        switch (getGameKind()) {
            case 1:
                return 10;
            case 2:
                return 15;
            default:
                return 6;
        }
    }

    @Override
    protected List<Integer> resetBoard() {
        this.cards.clear();
        for (int i = 0; i < getPairs(); i++) {
            this.cards.add(i);
            this.cards.add(i);
        }
        Collections.shuffle(this.cards);
        this.matches[0] = 0;
        this.matches[1] = 0;
        this.firstCard = -1;
        return this.cards;
    }

    // MGP_TurnUpCard, first = the first card of the turn
    public void turnUpCard(TacosCharacter chr, boolean first, int card) {
        int slot = getSlot(chr);
        if (!isPlaying() || slot < 0 || card < 0 || this.cards.size() <= card || first != (this.firstCard < 0)) {
            return;
        }
        // the first card is shown to the opponent.
        if (this.firstCard < 0) {
            this.firstCard = card;
            broadcast(ResCMiniRoomBaseDlg.MiniRoom_MGP_TurnUpCard(1, card, 0, 1), slot);
            return;
        }
        int first_card = this.firstCard;
        this.firstCard = -1;
        if (first_card == card || !this.cards.get(first_card).equals(this.cards.get(card))) {
            broadcast(ResCMiniRoomBaseDlg.MiniRoom_MGP_TurnUpCard(0, card, first_card, (slot == 0) ? 0 : 1));
            return;
        }
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_MGP_TurnUpCard(0, card, first_card, (slot == 0) ? 2 : 3));
        this.matches[slot]++;
        if (this.matches[0] + this.matches[1] < getPairs()) {
            return;
        }
        if (this.matches[0] == this.matches[1]) {
            finishGame(RESULT_TIE, slot);
            return;
        }
        finishGame(RESULT_WIN, (this.matches[0] < this.matches[1]) ? 1 : 0);
    }
}
