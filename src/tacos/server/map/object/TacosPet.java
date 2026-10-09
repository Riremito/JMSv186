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
package tacos.server.map.object;

/**
 *
 * @author Riremito
 */
public class TacosPet extends TacosMapObject {

    private String name;
    private boolean summoned = false;
    private int unique_id;
    private int item_id;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getSummoned() {
        return this.summoned;
    }

    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    public int getUniqueId() {
        return this.unique_id;
    }

    public void setUniqueId(int id) {
        this.unique_id = id;
    }

    public int getPetItemId() {
        return this.item_id;
    }

    public void setPetItemId(int id) {
        this.item_id = id;
    }
}
