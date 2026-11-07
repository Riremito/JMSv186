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
// Map.wz portal script : jnr1_out, rnj1_out (JMS186), js の処理を利用
// モンスターを全て倒すとポータルが開く
public class Portal_Ariant_Jnr1_out extends PortalScriptMan {

    @Override
    public boolean enter() {
        int map_next;
        switch (getScriptName()) {
            case "jnr1_out" -> {
                map_next = 926110100; // 不快な実験室
            }
            case "rnj1_out" -> {
                map_next = 926100100; // 不快な実験室
            }
            default -> {
                return false;
            }
        }
        if (getMonsterCount() != 0) {
            // TODO : 文章 (js の英文を翻訳, 原文 : The portal has not opened yet.)
            playerMessage(5, "ポータルはまだ開いていません。");
            return false;
        }
        return warp(map_next, "sp");
    }
}
