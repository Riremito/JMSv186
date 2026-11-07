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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Reactor.wz action : shouwaBoss (JMS186), js の処理を利用
public class Reactor_Zipangu_ShouwaBoss extends ReactorScriptMan {

    @Override
    public void action() {
        // FieldSet (shouwaBoss) の action (リアクターの状態が 1 になった時に変数 shouwaBoss を yes にする) が未実装のため、ここで設定する
        TacosFieldSet field_set = getFieldSet("shouwaBoss");
        if (field_set != null) {
            field_set.setVar("shouwaBoss", "yes");
        }
        changeMusic("Bgm06/FinalFight");
        summonMob(9400112, 830, 160);
        // TODO : 文章 (js の文章をそのまま利用)
        mapMessage2("ボディガードAが召喚されます。");
    }
}
