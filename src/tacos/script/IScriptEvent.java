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

import tacos.odin.OdinEventInstanceManager;
import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
public interface IScriptEvent {

    public boolean init();

    public boolean monsterValue(OdinEventInstanceManager eim, int mob_id);

    public boolean setup();

    public boolean playerEntry(OdinEventInstanceManager eim, TacosCharacter player);

    public boolean playerDead(OdinEventInstanceManager eim, TacosCharacter player);

    public boolean playerRevive(OdinEventInstanceManager eim, TacosCharacter player);

    public boolean scheduledTimeout(OdinEventInstanceManager eim);

    public boolean changedMap(OdinEventInstanceManager eim, TacosCharacter player, int map_id);

    public boolean playerDisconnected(OdinEventInstanceManager eim, TacosCharacter player);

    public boolean leftParty(OdinEventInstanceManager eim, TacosCharacter player);

    public boolean disbandParty(OdinEventInstanceManager eim, TacosCharacter player);

    public boolean playerExit(OdinEventInstanceManager eim, TacosCharacter player);

    public boolean clearPQ(OdinEventInstanceManager eim);

    public boolean allMonstersDead(OdinEventInstanceManager eim);

    public boolean cancelSchedule();

}
