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
package tacos.script.reactor;

/**
 *
 * @author Riremito
 */
public class Reactor_ElNath_SnowscaScript extends ReactorScriptMan {

    private final int anti_macro_monster_id = 6090001; // 雪山の亡霊

    @Override
    public void action() {
        if (checkMob(this.anti_macro_monster_id)) {
            weakenMacroMob(this.anti_macro_monster_id);
            // 原文ママ
            mapMessage("祭壇の光が雪山の魔女の憎悪を緩和した。魔女の力が弱くなった。");
        }
    }
}
