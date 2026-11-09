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
package tacos.wz.opt;

/**
 *
 * @author Riremito
 */
public enum PortalType {
    PORTALTYPE_STARTPOINT(0),
    PORTALTYPE_INVISIBLE(1),
    PORTALTYPE_VISIBLE(2),
    PORTALTYPE_COLLISION(3),
    PORTALTYPE_CHANGABLE(4),
    PORTALTYPE_CHANGABLE_INVISIBLE(5),
    PORTALTYPE_TOWNPORTAL_POINT(6),
    PORTALTYPE_SCRIPT(7),
    PORTALTYPE_SCRIPT_INVISIBLE(8),
    PORTALTYPE_COLLISION_SCRIPT(9),
    PORTALTYPE_HIDDEN(10),
    PORTALTYPE_SCRIPT_HIDDEN(11),
    PORTALTYPE_COLLISION_VERTICAL_JUMP(12),
    PORTALTYPE_COLLISION_CUSTOM_IMPACT(13),
    UNKNOWN;

    private final int value;

    private PortalType(int value) {
        this.value = value;
    }

    private PortalType() {
        this.value = -1;
    }

    public int get() {
        return this.value;
    }

    public static PortalType find(int value) {
        for (PortalType ops : values()) {
            if (ops.get() == value) {
                if (value != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }
}
