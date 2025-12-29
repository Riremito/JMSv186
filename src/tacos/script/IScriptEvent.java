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
package tacos.script;

import odin.client.MapleCharacter;
import odin.scripting.EventInstanceManager;

/**
 *
 * @author Riremito
 */
public interface IScriptEvent {

    public boolean init();

    public boolean monsterValue(EventInstanceManager eim, int mob_id);

    public boolean setup();

    public boolean playerEntry(EventInstanceManager eim, MapleCharacter player);

    public boolean playerDead(EventInstanceManager eim, MapleCharacter player);

    public boolean playerRevive(EventInstanceManager eim, MapleCharacter player);

    public boolean scheduledTimeout(EventInstanceManager eim);

    public boolean changedMap(EventInstanceManager eim, MapleCharacter player, int map_id);

    public boolean playerDisconnected(EventInstanceManager eim, MapleCharacter player);

    public boolean leftParty(EventInstanceManager eim, MapleCharacter player);

    public boolean disbandParty(EventInstanceManager eim, MapleCharacter player);

    public boolean playerExit(EventInstanceManager eim, MapleCharacter player);

    public boolean clearPQ(EventInstanceManager eim);

    public boolean allMonstersDead(EventInstanceManager eim);

    public boolean cancelSchedule();

}
