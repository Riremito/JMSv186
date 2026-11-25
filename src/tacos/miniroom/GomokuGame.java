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

import java.util.List;
import tacos.client.TacosCharacter;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.response.ResCMiniRoomBaseDlg;

/**
 *
 * @author Riremito
 */
public class GomokuGame extends MiniGame {

    private static final int RECORD_QUEST_ID = 122200;
    private static final int BOARD_SIZE = 15;
    private static final int[][] DIRECTIONS = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];

    public GomokuGame(TacosCharacter owner, int itemId, String description, String password, int gameKind) {
        super(owner, itemId, description, password, gameKind);
    }

    @Override
    public OpsMiniRoomType getMiniRoomType() {
        return OpsMiniRoomType.MR_OmokRoom;
    }

    @Override
    protected int getRecordQuestId() {
        return RECORD_QUEST_ID;
    }

    @Override
    protected List<Integer> resetBoard() {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        return null;
    }

    // ORP_PutStoneChecker
    public void putStone(TacosCharacter chr, int x, int y, int type) {
        int slot = getSlot(chr);
        if (!isPlaying() || slot < 0 || !isOnBoard(x, y) || this.board[x][y] != 0) {
            return;
        }
        this.board[x][y] = type;
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_ORP_PutStoneChecker(x, y, type));
        if (isFiveInRow(x, y, type)) {
            finishGame(RESULT_WIN, slot);
        }
    }

    private static boolean isOnBoard(int x, int y) {
        return 0 <= x && x < BOARD_SIZE && 0 <= y && y < BOARD_SIZE;
    }

    // five or more stones in a row through the stone put at (x, y).
    private boolean isFiveInRow(int x, int y, int type) {
        for (int[] direction : DIRECTIONS) {
            int count = 1 + countStones(x, y, direction[0], direction[1], type) + countStones(x, y, -direction[0], -direction[1], type);
            if (5 <= count) {
                return true;
            }
        }
        return false;
    }

    private int countStones(int x, int y, int dx, int dy, int type) {
        int count = 0;
        for (int px = x + dx, py = y + dy; isOnBoard(px, py) && this.board[px][py] == type; px += dx, py += dy) {
            count++;
        }
        return count;
    }
}
