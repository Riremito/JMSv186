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
package tacos.debug;

/**
 *
 * @author Riremito
 */
public class DebugCommander {

    private static final String DEBUG_COMMAND_PREFIX = "/@!";
    private String message = null;
    private String[] splitted = null;

    public DebugCommander(String message) {
        this.message = message;
    }

    public boolean checkPrefix() {
        if (this.message.length() == 0) {
            return false;
        }

        char prefix = this.message.charAt(0);
        if (DEBUG_COMMAND_PREFIX.indexOf(prefix) == -1) {
            return false;
        }

        this.splitted = ('/' + this.message.substring(1)).split(" ");
        this.splitted[0] = this.splitted[0].toLowerCase();
        return true;
    }

    public int getLength() {
        if (this.splitted == null) {
            return 0;
        }
        return this.splitted.length;
    }

    /*
        引数の数を確認
     */
    public boolean check(int index) {
        if (this.splitted == null) {
            return false;
        }

        return (index + 1) <= this.splitted.length;
    }

    public String get(int index) {
        if (!check(index)) {
            return "";
        }

        return this.splitted[index];
    }

    public int getInt(int index) {
        if (!check(index)) {
            return 0;
        }
        try {
            return Integer.parseInt(this.splitted[index]);
        } catch (NumberFormatException e) {
            // parse error.
        }
        return 0;
    }
}
