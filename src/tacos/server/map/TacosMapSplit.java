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

    // default is for 800x600.
    private static final int SPLIT_WIDTH = 600;
    private static final int SPLIT_HEIGHT = 450;

    private class MapWall {

        private int left = 0;
        private int top = 0;
        private int right = 0;
        private int bottom = 0;
    }

    private class MapScreen {

        private int width = 0;
        private int height = 0;
    }

    private class MapSplit {

        private int col = 0;
        private int row = 0;
        private int total = 0;
    }

    private final MapWall wall = new MapWall();
    private final MapScreen screen = new MapScreen();
    private final MapSplit split = new MapSplit();

    public int find(int x, int y) {
        int col = (x - this.wall.left) / SPLIT_WIDTH;
        int row = (y - this.wall.top) / SPLIT_HEIGHT;
        return (row * this.split.col) + col;
    }

    public int setSplit(List<MapleFoothold> footholds) {
        if (!setWall(footholds)) {
            return 0;
        }

        this.screen.width = this.wall.right - this.wall.left;
        this.screen.height = this.wall.bottom - this.wall.top;
        this.split.col = (this.screen.width + SPLIT_WIDTH - 1) / SPLIT_WIDTH;
        this.split.row = (this.screen.height + SPLIT_HEIGHT - 1) / SPLIT_HEIGHT;
        this.split.total = this.split.col * this.split.row;
        return this.split.total;
    }

    private boolean setWall(List<MapleFoothold> footholds) {
        for (MapleFoothold foothold : footholds) {
            int fh_left = Math.min(foothold.getX1(), foothold.getX2());
            int fh_top = Math.min(foothold.getY1(), foothold.getY2());
            int fh_right = Math.max(foothold.getX1(), foothold.getX2());
            int fh_bottom = Math.max(foothold.getY1(), foothold.getY2()) + 10;
            int fh_width = fh_right - fh_left;

            if (fh_left < (this.wall.left + 30)) {
                this.wall.left = fh_left + 30;
            }
            if (fh_top < (this.wall.top - 300)) {
                this.wall.top = fh_top - 300;
            }
            if ((this.wall.right - 30) < fh_right) {
                this.wall.right = fh_right - 30;
            }
            if (fh_width != 0) {
                if (this.wall.bottom < fh_bottom) {
                    this.wall.bottom = fh_bottom;
                }
            }
        }
        return true;
    }

    public int getSplit() {
        return this.split.total;
    }

    public int getCol() {
        return this.split.col;
    }

    public int getRow() {
        return this.split.row;
    }

    public void sendInfo(TacosCharacter chr) {
        chr.DebugMsg("wall=" + this.wall.left + "," + this.wall.top + "," + this.wall.right + "," + this.wall.bottom);
        chr.DebugMsg("size=" + this.screen.width + "x" + this.screen.height);
        chr.DebugMsg("split=" + this.split.col + "x" + this.split.row);

        for (int i = 0; i < this.split.row; i++) {
            String array_num = "";
            for (int j = 0; j < this.split.col; j++) {
                array_num += ((i * this.split.col) + j) + "|";
            }
            chr.DebugMsg("index=" + array_num);
        }
        chr.DebugMsg("current=" + find(chr.getPosition().x, chr.getPosition().y));
    }
}
