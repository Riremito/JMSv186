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
public class Reactor_Magatia_MagascaScript extends ReactorScriptMan {

    private final int anti_macro_monster_id = 6090004; // ルルモ

    @Override
    public void action() {
        if (checkMob(this.anti_macro_monster_id)) {
            weakenMacroMob(this.anti_macro_monster_id);
            // TODO : 弱体化時のメッセージ
            mapMessage("ルルモが弱体化されました。");
        }
    }
}
