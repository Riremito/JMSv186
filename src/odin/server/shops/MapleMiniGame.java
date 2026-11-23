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
package odin.server.shops;

import java.util.List;
import odin.client.MapleQuestStatus;
import odin.server.quest.MapleQuest;
import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsMREnterResult;
import tacos.packet.ops.OpsMRLeave;
import tacos.packet.response.ResCMiniRoomBaseDlg;

/**
 * The common part of the mini games (MGRP_), the owner plays against one
 * visitor. MapleOmok handles ORP_ and MapleMemoryGame handles MGP_.
 *
 * @author Riremito
 */
public abstract class MapleMiniGame extends MapleMiniRoom {

    // m_nGameResultType
    public static final int RESULT_LOSE = 0;
    public static final int RESULT_TIE = 1;
    public static final int RESULT_WIN = 2;

    private final String password;
    private final int gameKind;
    private final boolean[] exitAfter = new boolean[2];
    private boolean playing = false;
    private boolean visitorReady = false;
    private int tieRequestSlot = -1;
    private int firstTurn = 0;

    protected MapleMiniGame(TacosCharacter owner, int itemId, String description, String password, int gameKind) {
        super(owner, itemId, description, 1);
        this.password = (password != null) ? password : "";
        this.gameKind = gameKind;
    }

    // the win, tie and lose counts are kept in the custom data of this quest as "losses,ties,wins"
    protected abstract int getRecordQuestId();

    // the board is cleared, returns the shuffled cards or null.
    protected abstract List<Integer> resetBoard();

    @Override
    public boolean isPrivate() {
        return !this.password.isEmpty();
    }

    @Override
    public boolean isGameOn() {
        return this.playing;
    }

    @Override
    protected void detach(TacosCharacter chr) {
        chr.setMiniGame(null);
    }

    @Override
    protected ServerPacket getEnterPacket(int slot, TacosCharacter visitor) {
        return ResCMiniRoomBaseDlg.MiniRoom_MRP_Enter(slot, visitor, this);
    }

    // m_nGameKind, the piece of omok or the number of cards of the memory game
    public int getGameKind() {
        return this.gameKind;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    // the room is created and shown over the owner.
    public void create() {
        getOwner().setMiniGame(this);
        getMap().addMiniGame(this);
        getOwner().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_MiniGame(getOwner(), this));
        updateBalloon();
    }

    // MRP_Enter from a visitor.
    public OpsMREnterResult enter(TacosCharacter visitor, String password) {
        if (this.playing) {
            return OpsMREnterResult.MGEnterResult_AlreadyPlaying;
        }
        if (isPrivate() && !this.password.equals(password)) {
            return OpsMREnterResult.MGEnterResult_InvalidPassword;
        }
        if (isFull()) {
            return OpsMREnterResult.MREnterResult_Full;
        }
        visitor.setMiniGame(this);
        addVisitor(visitor);
        this.visitorReady = false;
        visitor.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_MiniGame(visitor, this));
        updateBalloon();
        return OpsMREnterResult.MREnterResult_Success;
    }

    // MRP_Leave, the dialog is already closed. a game being played is lost.
    public void leave(TacosCharacter chr) {
        if (this.playing && getSlot(chr) != -1) {
            this.exitAfter[0] = false;
            this.exitAfter[1] = false;
            finishGame(RESULT_LOSE, getSlot(chr));
        }
        if (isOwner(chr)) {
            close();
            return;
        }
        removeVisitor(chr);
        this.visitorReady = false;
        updateBalloon();
    }

    // the owner left, the visitor is sent out.
    public void close() {
        kickAllVisitors(OpsMRLeave.MRLeave_HostOut);
        getMap().removeMiniGame(getObjectId());
        detach(getOwner());
        updateBalloon();
    }

    // MGRP_Ready, MGRP_CancelReady from the visitor.
    public void setReady(TacosCharacter chr, boolean ready) {
        if (this.playing || getSlot(chr) <= 0) {
            return;
        }
        this.visitorReady = ready;
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_MGRP_Ready(ready));
    }

    // MGRP_Start from the owner.
    public void start(TacosCharacter chr) {
        if (this.playing || !isOwner(chr) || getUser(1) == null || !this.visitorReady) {
            return;
        }
        this.playing = true;
        this.tieRequestSlot = -1;
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_MGRP_Start(this.firstTurn, resetBoard()));
        updateBalloon();
    }

    // MGRP_Ban, the owner sends the visitor out before the game.
    public void ban(TacosCharacter chr) {
        if (this.playing || !isOwner(chr) || getUser(1) == null) {
            return;
        }
        kickVisitor(getUser(1), OpsMRLeave.MRLeave_Kicked);
        this.visitorReady = false;
        updateBalloon();
    }

    // MGRP_TieRequest, asks the opponent.
    public void requestTie(TacosCharacter chr) {
        int slot = getSlot(chr);
        TacosCharacter opponent = getUser(getOpponentSlot(slot));
        if (!this.playing || slot < 0 || opponent == null) {
            return;
        }
        this.tieRequestSlot = slot;
        opponent.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MGRP_TieRequest());
    }

    // MGRP_TieResult, the answer of the opponent.
    public void answerTie(TacosCharacter chr, boolean accepted) {
        int slot = getSlot(chr);
        if (!this.playing || this.tieRequestSlot < 0 || this.tieRequestSlot == slot) {
            return;
        }
        int request_slot = this.tieRequestSlot;
        this.tieRequestSlot = -1;
        if (accepted) {
            finishGame(RESULT_TIE, request_slot);
            return;
        }
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_MGRP_TieResult());
    }

    // MGRP_GiveUpRequest
    public void giveUp(TacosCharacter chr) {
        int slot = getSlot(chr);
        if (!this.playing || slot < 0) {
            return;
        }
        finishGame(RESULT_LOSE, slot);
    }

    // MGRP_LeaveEngage, MGRP_LeaveEngageCancel, the user leaves after the game.
    public void setExitAfter(TacosCharacter chr, boolean exit) {
        int slot = getSlot(chr);
        if (!this.playing || slot < 0) {
            return;
        }
        this.exitAfter[slot] = exit;
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_MGRP_LeaveEngage(exit));
    }

    // MGRP_TimeOver
    public void timeOver(TacosCharacter chr) {
        int slot = getSlot(chr);
        if (!this.playing || slot < 0) {
            return;
        }
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_MGRP_TimeOver(slot));
    }

    protected static int getOpponentSlot(int slot) {
        return (slot == 0) ? 1 : 0;
    }

    // the result of the user in the slot, the opponent gets the opposite result.
    protected void finishGame(int result, int slot) {
        if (!this.playing) {
            return;
        }
        this.playing = false;
        this.visitorReady = false;
        this.tieRequestSlot = -1;
        int opponent_slot = getOpponentSlot(slot);
        addRecord(getUser(slot), result);
        addRecord(getUser(opponent_slot), RESULT_WIN - result);
        // the loser plays first in the next game.
        if (result == RESULT_LOSE) {
            this.firstTurn = slot;
        } else if (result == RESULT_WIN) {
            this.firstTurn = opponent_slot;
        } else {
            this.firstTurn = getOpponentSlot(this.firstTurn);
        }
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_MGRP_GameResult(this, result, slot));
        updateBalloon();
        checkExitAfterGame();
    }

    private void checkExitAfterGame() {
        boolean owner_exit = this.exitAfter[0];
        TacosCharacter visitor = getUser(1);
        if (this.exitAfter[1] && visitor != null) {
            kickVisitor(visitor, OpsMRLeave.MGLeave_UserRequest);
            updateBalloon();
        }
        this.exitAfter[0] = false;
        this.exitAfter[1] = false;
        if (owner_exit) {
            getOwner().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(0, OpsMRLeave.MGLeave_UserRequest));
            close();
        }
    }

    // records.
    private MapleQuestStatus getRecord(TacosCharacter chr) {
        MapleQuestStatus record = chr.getQuestNAdd(MapleQuest.getInstance(getRecordQuestId()));
        String data = record.getCustomData();
        if (data == null || data.split(",").length != 3) {
            record.setCustomData("0,0,0");
        }
        return record;
    }

    // RESULT_LOSE = losses, RESULT_TIE = ties, RESULT_WIN = wins
    private int getRecord(TacosCharacter chr, int result) {
        try {
            return Integer.parseInt(getRecord(chr).getCustomData().split(",")[result]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void addRecord(TacosCharacter chr, int result) {
        if (chr == null) {
            return;
        }
        String[] data = getRecord(chr).getCustomData().split(",");
        data[result] = String.valueOf(getRecord(chr, result) + 1);
        getRecord(chr).setCustomData(String.join(",", data));
    }

    public int getWins(TacosCharacter chr) {
        return getRecord(chr, RESULT_WIN);
    }

    public int getTies(TacosCharacter chr) {
        return getRecord(chr, RESULT_TIE);
    }

    public int getLosses(TacosCharacter chr) {
        return getRecord(chr, RESULT_LOSE);
    }

    //TODO: Fix formula
    public int getScore(TacosCharacter chr) {
        return 2000 + getWins(chr) * 2 + getTies(chr) - getLosses(chr) * 2;
    }
}
