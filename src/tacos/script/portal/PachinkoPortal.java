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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
public class PachinkoPortal extends TacosSharedPortal {

    private static final int MAP_ID_PACHINKO_HALL = 809030000;
    private static final String PORTAL_PACHINKO_HALL = "out00";

    public PachinkoPortal() {
        super(MAP_ID_PACHINKO_HALL, PORTAL_PACHINKO_HALL);
    }
}
