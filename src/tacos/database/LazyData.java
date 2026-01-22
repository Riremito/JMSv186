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
package tacos.database;

/**
 *
 * @author Riremito
 */
public class LazyData {

    private LazyDataNames name;
    private boolean ok;
    private int value_int = 0;
    private String value_str = "";

    public LazyData(LazyDataNames name) {
        this.name = name;
        this.ok = false;
    }

    public LazyDataNames getDataName() {
        return this.name;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public void setInt(int value_int) {
        this.value_int = value_int;
    }

    public void setStr(String value_str) {
        this.value_str = value_str;
    }

    public int getInt() {
        return this.value_int;
    }

    public String getStr() {
        return this.value_str;
    }

    public boolean getOk() {
        return this.ok;
    }

}
