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
package tacos.data.wz.ids;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

/**
 *
 * @author Riremito
 */
public class DWI_List {

    private ArrayList<Integer> ids = null;
    Random rand = new Random();
    Consumer<ArrayList<Integer>> funcion = null;

    public DWI_List(Consumer<ArrayList<Integer>> funcion) {
        setFunction(funcion);
    }

    public DWI_List() {
    }

    public void setFunction(Consumer<ArrayList<Integer>> funcion) {
        this.funcion = funcion;
    }

    private void init() {
        ids = new ArrayList<>();
        funcion.accept(ids);
    }

    public ArrayList<Integer> getIds() {
        if (ids == null) {
            init();
        }
        return ids;
    }

    public int getRandom() {
        return getIds().get(rand.nextInt(ids.size()));
    }

    public boolean isValidID(int id) {
        return getIds().contains(id);
    }
}
