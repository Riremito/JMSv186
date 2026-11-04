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
// Reactor.wz action : weddingItem0, weddingItem1 (JMS186), js の処理を利用
public class Reactor_Amoria_WeddingItem0 extends ReactorScriptMan {

    private final int reactor_wedding_item0 = 6802000; // weddingItem0
    private final int mob_wedding1 = 9400506; // キャンドル
    private final int mob_wedding2 = 9400507; // ケーキ

    @Override
    public void action() {
        if (getReactorId() == this.reactor_wedding_item0) {
            summonMob(this.mob_wedding1, 3);
            summonMob(this.mob_wedding2, 4);
            return;
        }
        summonMob(this.mob_wedding1, 5);
        summonMob(this.mob_wedding2, 7);
    }
}
