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
package tacos.client;

import java.util.HashMap;
import java.util.Map;
import odin.tools.Pair;

/**
 *
 * @author Riremito
 */
public class TacosKeyLayout {

    private boolean changed = false;
    private Map<Integer, Pair<Byte, Integer>> keymap = new HashMap<>();

    public TacosKeyLayout() {
    }

    public boolean isChanged() {
        return this.changed;
    }

    public Map<Integer, Pair<Byte, Integer>> get() {
        return keymap;
    }

    public void put(int key, byte type, int action) {
        this.keymap.put(key, new Pair<>(type, action));
        this.changed = true;
    }

    public void remove(int key) {
        this.keymap.remove(key);
        this.changed = true;
    }

}
