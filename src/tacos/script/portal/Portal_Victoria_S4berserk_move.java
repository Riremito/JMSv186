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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : s4berserk_move (JMS186), lua の処理を利用
public class Portal_Victoria_S4berserk_move extends PortalScriptMan {

    private final int mob_junior_balrog = 9300092; // 忘れられた神殿のジュニアバルログ
    private final int map_forgotten_temple = 910500200; // 忘れられた神殿

    @Override
    public boolean enter() {
        if (getMobCount(this.map_forgotten_temple, this.mob_junior_balrog) <= 0) {
            return transferField(this.map_forgotten_temple, "pt00");
        }
        // 翻訳
        message("ポータルは現在封印されています。");
        return false;
    }
}
