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
package tacos.server.map;

import java.util.List;
import odin.server.maps.MapleFoothold;
import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
public class TacosMapSplit {

    private static int SPLIT_WIDTH = 600; // 600 default.
    private static int SPLIT_HEIGHT = 450; // 450 default.
    private int wall_left = 0;
    private int wall_top = 0;
    private int wall_right = 0;
    private int wall_bottom = 0;
    private int map_width = 0;
    private int map_height = 0;
    private int split_col = 0;
    private int split_row = 0;
    private int split = 0;

    public TacosMapSplit() {

    }

    public int setSplit(List<MapleFoothold> footholds) {
        if (!setWall(footholds)) {
            return 0;
        }
        // set width and height.
        this.map_width = this.wall_right - this.wall_left;
        this.map_height = this.wall_bottom - this.wall_top;
        // set split col and row.
        this.split_col = (this.map_width + SPLIT_WIDTH - 1) / SPLIT_WIDTH;
        this.split_row = (this.map_height + SPLIT_HEIGHT - 1) / SPLIT_HEIGHT;
        this.split = this.split_col * this.split_row;
        return this.split;
    }

    public boolean setWall(List<MapleFoothold> footholds) {
        for (MapleFoothold foothold : footholds) {
            int fh_left = Math.min(foothold.getX1(), foothold.getX2());
            int fh_top = Math.min(foothold.getY1(), foothold.getY2());
            int fh_right = Math.max(foothold.getX1(), foothold.getX2());
            int fh_bottom = Math.max(foothold.getY1(), foothold.getY2()) + 10;
            int fh_width = fh_right - fh_left;

            if (fh_left < (this.wall_left + 30)) {
                this.wall_left = fh_left + 30;
            }
            if (fh_top < (this.wall_top - 300)) {
                this.wall_top = fh_top - 300;
            }
            if ((this.wall_right - 30) < fh_right) {
                this.wall_right = fh_right - 30;
            }
            if (fh_width != 0) {
                if (this.wall_bottom < fh_bottom) {
                    this.wall_bottom = fh_bottom;
                }
            }
        }
        return true;
    }

    public int getSplitMap(int x, int y) {
        int col = (x - this.wall_left) / SPLIT_WIDTH;
        int row = (y - this.wall_top) / SPLIT_HEIGHT;
        return (row * this.split_col) + col;
    }

    public int getSplit() {
        return this.split;
    }

    public int getCol() {
        return this.split_col;
    }

    public int getRow() {
        return this.split_row;
    }

    public void sendInfo(TacosCharacter chr) {
        chr.DebugMsg("wall=" + this.wall_left + "," + this.wall_top + "," + this.wall_right + "," + this.wall_bottom);
        chr.DebugMsg("size=" + this.map_width + "x" + this.map_height);
        chr.DebugMsg("split=" + this.split_col + "x" + this.split_row);

        for (int i = 0; i < this.split_row; i++) {
            String array_num = "";
            for (int j = 0; j < this.split_col; j++) {
                array_num += ((i * this.split_col) + j) + "|";
            }
            chr.DebugMsg("index=" + array_num);
        }
        chr.DebugMsg("current=" + getSplitMap(chr.getPosition().x, chr.getPosition().y));
    }
}
