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
// Reactor.wz action : davyItem1, davyScript0, davyScript1, davyScript2, davyScript3 (JMS186), js の処理を利用
// lua (davyJohn) では NPC が Reactor の状態を確認するため、js の Pirate (EventManager) の進行状況の処理は未移行
public class Reactor_HerbTown_DavyItem1 extends ReactorScriptMan {

    private final int reactor_davy_item1 = 2512001; // davyItem1

    @Override
    public void action() {
        if (getReactorId() == this.reactor_davy_item1) {
            // TODO : 文章 (js の英文を翻訳, 原文 : One of the boxes have been activated.)
            mapMessage(6, "箱が1つ作動しました。");
            return;
        }
        // TODO : 4つの扉が作動した時の js のメッセージ (Proceed!) は未移行
        // TODO : 文章 (js の英文を翻訳, 原文 : One of the doors have been activated.)
        mapMessage(6, "扉が1つ作動しました。");
    }
}
