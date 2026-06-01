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
package tacos.shared;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 *
 * @author Riremito
 */
public class SharedDate {

    private static final String DATE_BASE = "2339-01-01 18:00:00"; // UTC+9 (JST)
    private static final String DATE_MAGICAL = "2027-07-07 07:00:00";
    private static final String DATE_FOREVER = "2079-07-07 07:00:00";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat SDF_QUEST = new SimpleDateFormat("yyyy-MM-dd");

    public static long getTimestampLong(String date) {
        return (Timestamp.valueOf(date).getTime() + Timestamp.valueOf(DATE_BASE).getTime()) * 10000;
    }

    // quest date.
    public static String getDateString() {
        return SDF_QUEST.format(Calendar.getInstance().getTime());
    }

    public static String getDateString(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(FORMATTER);
    }

    // quest complete time.
    public static long getTimestamp() {
        return getTimestamp(System.currentTimeMillis());
    }

    public static long getTimestamp(long timestamp) {
        return getTimestampLong(getDateString(timestamp));
    }

    // 2027-07-07 (Pet)
    public static long getMagicalExpirationDate() {
        return getTimestampLong(DATE_MAGICAL);
    }

    // 2079-07-07 (non Pet items)
    public static long getNoExpirationDate() {
        return getTimestampLong(DATE_FOREVER);
    }
}
