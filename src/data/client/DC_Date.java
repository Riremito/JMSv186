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
package data.client;

import java.sql.Timestamp;

/**
 *
 * @author Riremito
 */
public class DC_Date {

    public static final long magical_expiration_date = (Timestamp.valueOf("2027-07-07 07:00:00").getTime() + Timestamp.valueOf("2339-01-01 18:00:00").getTime()) * 10000;

    // 2027-07-07
    public static long getMagicalExpirationDate() {
        return magical_expiration_date;
    }

}
