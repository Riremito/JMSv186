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
package tacos.packet.ops;

import tacos.config.Region;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsUI implements IPacketOps {
    UI_ITEM(0),
    UI_EQUIP(1),
    UI_STAT(2),
    UI_SKILL(3),
    UI_MINIMAP(4),
    UI_KEYCONFIG(5),
    UI_QUESTINFO(6),
    UI_USERLIST(7),
    UI_MESSENGER(8),
    UI_MONSTERBOOK(9),
    UI_USERINFO(10),
    UI_SHORTCUT(11),
    UI_MENU(12),
    UI_QUESTALARM(13),
    UI_PARTYHP(14),
    UI_QUESTTIMER(15),
    UI_QUESTTIMERACTION(16),
    UI_MONSTERCARNIVAL(17),
    UI_ITEMSEARCH(18),
    UI_ENERGYBAR(19),
    UI_GUILDBOARD(20),
    UI_PARTYSEARCH(21),
    UI_ITEMMAKE(22),
    UI_CONSULT(23),
    UI_CLASSCOMPETITION(24),
    UI_RANKING(25),
    UI_FAMILY(26),
    UI_FAMILYCHART(27),
    UI_OPERATORBOARD(28),
    UI_OPERATORBOARDSTATE(29),
    UI_MEDALQUESTINFO(30),
    UI_WEBEVENT(31),
    UI_SKILLEX(32),
    UI_REPAIRDURABILITY(33),
    UI_CHATWND(34),
    UI_BATTLERECORD(35),
    UI_GUILDMAKEMARK(36),
    UI_GUILDMAKE(37),
    UI_GUILDRANK(38),
    UI_GUILDBBS(39),
    UI_ACCOUNTMOREINFO(40),
    UI_FINDFRIEND(41),
    UI_DRAGONBOX(42),
    UI_WNDNO(43),
    UI_UNRELEASE(44),
    UNKNOWN;

    private int value;

    OpsUI(int val) {
        this.value = val;
    }

    OpsUI() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static void clear() {
        for (OpsUI o : OpsUI.values()) {
            o.set(-1);
        }
    }

    public static void init() {
        if (Version.GreaterOrEqual(Region.JMS, 187)) {
            clear();
            UI_REPAIRDURABILITY.set(33);
            return;
        }
    }
}
