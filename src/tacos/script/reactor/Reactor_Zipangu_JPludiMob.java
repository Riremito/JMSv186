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

import odin.server.Randomizer;

/**
 *
 * @author Riremito
 */
// Reactor.wz action : JPludiMob0, JPludiMob1, JPludiMob2, JPludiMob3, JPludiMob4 (JMS186), js の処理を利用
// js では配置されているマップの確認をしているが、Reactor が配置されているマップと同じため省略
public class Reactor_Zipangu_JPludiMob extends ReactorScriptMan {

    private final int reactor_jp_ludi_mob0 = 8091000; // JPludiMob0
    private final int reactor_jp_ludi_mob4 = 8091004; // JPludiMob4
    private final int mob_base = 9400209; // メイプルの実3

    @Override
    public void action() {
        // JPludiMob0 = 9400209, 9400210, JPludiMob1 = 9400211, 9400212, ...
        int index = getReactorId() - this.reactor_jp_ludi_mob0;
        int mob1 = this.mob_base + index * 2;
        int mob2 = mob1 + 1;
        if (getReactorId() == this.reactor_jp_ludi_mob4) {
            summonMob(mob1, 3);
            summonMob(mob2, 3);
            return;
        }
        boolean swap = Randomizer.nextInt(2) == 0;
        summonMob(mob1, swap ? 4 : 3);
        summonMob(mob2, swap ? 3 : 4);
    }
}
