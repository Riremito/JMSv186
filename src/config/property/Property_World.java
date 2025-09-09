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
package config.property;

import config.ContentCustom;
import config.ContentState;
import config.Region;

/**
 *
 * @author Riremito
 */
public class Property_World {

    // info
    private static int channels;
    private static int languages = 1;
    private static int port_default;
    private static int flags;
    private static String name;
    private static String message;
    private static String event;
    // rate
    private static int rate_exp;
    private static int rate_meso;
    private static int rate_drop;
    // more
    public static boolean admin_only;
    public static String events;
    // content block
    private static boolean lock_hp_wash;
    private static boolean lock_hammer;
    private static boolean lock_EE;
    private static boolean lock_potential;
    private static boolean lock_boom;
    private static boolean lock_losing_upgrade_slot;
    private static boolean lock_losing_throwing;
    private static boolean lock_losing_stone;

    // custom content
    private static boolean custom_content;
    private static boolean drop_god_equip;
    private static int master_monster_timer;

    public static int getChannels() {
        return channels;
    }

    public static int getLanguages() {
        return languages;
    }

    public static int getPort() {
        return port_default;
    }

    public static int getFlags() {
        return flags;
    }

    public static String getName() {
        return name;
    }

    public static String getMessage() {
        return message;
    }

    public static String getEvent() {
        return event;
    }

    public static int getRateExp() {
        return rate_exp;
    }

    public static int getRateMeso() {
        return rate_meso;
    }

    public static int getRateDrop() {
        return rate_drop;
    }

    public static boolean getAdminOnly() {
        return admin_only;
    }

    public static String getEvents() {
        return events;
    }

    public static boolean init() {
        Property conf = new Property("properties/kaede.properties");
        if (!conf.open()) {
            return false;
        }

        // world info
        channels = conf.getInt("server.channels");
        if (Region.check(Region.EMS)) {
            languages = 5;
            channels = 2 * languages;
        }
        port_default = conf.getInt("server.port");
        flags = conf.getInt("server.flags");
        name = conf.get("server.name");
        message = conf.get("server.message");
        event = conf.get("server.event");
        // rate
        rate_exp = conf.getInt("server.rate.exp");
        rate_meso = conf.getInt("server.rate.meso");
        rate_drop = conf.getInt("server.rate.drop");

        admin_only = conf.getBoolean("server.admin");
        events = conf.get("server.events");

        // content state
        lock_hp_wash = conf.getBoolean("server.lock_hp_wash");
        lock_hammer = conf.getBoolean("server.lock.hammer");
        lock_EE = conf.getBoolean("server.lock.ee");
        lock_potential = conf.getBoolean("server.lock.potential");
        lock_boom = conf.getBoolean("server.lock.boom");
        lock_losing_upgrade_slot = conf.getBoolean("server.lock.losing_upgrade_slot");
        lock_losing_throwing = conf.getBoolean("server.lock.losing_throwing");
        lock_losing_stone = conf.getBoolean("server.lock.losing_stone");

        ContentState.CS_LOCK_HP_WASH.set(lock_hp_wash);
        ContentState.CS_LOCK_HAMMER.set(lock_hammer);
        ContentState.CS_LOCK_EE_SCROLL.set(lock_EE);
        ContentState.CS_LOCK_POTENTIAL.set(lock_potential);
        ContentState.CS_LOCK_BOOM.set(lock_boom);
        ContentState.CS_LOCK_LOSING_UPGRADE_SLOT.set(lock_losing_upgrade_slot);
        ContentState.CS_LOCK_LOSING_THRWOING.set(lock_losing_throwing);
        ContentState.CS_LOCK_LOSING_STONE.set(lock_losing_stone);

        // custom
        custom_content = conf.getBoolean("server.custom");
        drop_god_equip = conf.getBoolean("server.custom.god_equip");
        master_monster_timer = conf.getInt("server.custom.master_monster_timer");

        ContentCustom.CC_WZ_MAP_ADDED.set(custom_content);
        ContentCustom.CC_EQUIP_STAT_RANDOMIZER.set(drop_god_equip);
        ContentCustom.CC_MASTER_MONSTER_TIMER.setInt(master_monster_timer);
        return true;
    }
}
