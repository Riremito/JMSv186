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
// Reactor.wz action : s4hitmanMap4, s4hitmanMap7 (JMS186), ReactorAction.img の処理を利用
public class Reactor_Victoria_S4hitmanMap4 extends ReactorScriptMan {

    @Override
    public void action() {
        // TODO : ReactorAction.img の 0 番目の値 (1) の意味が不明
        // TODO : 文章 (ReactorAction.img の英文を翻訳, 原文 : Você será movido para um mapa diferente)
        playerMessage(5, "別のマップへ移動します。");
        warp(910200000, "pt01");
    }
}
