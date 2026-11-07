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
// Map.wz portal script : jnr3_in0, jnr3_in1, jnr_201_0, jnr_202, rnj3_in0, rnj3_in1, rnj_201_0, rnj_202 (JMS186), js の処理を利用
// リアクターが動作しているとポータルが開く
public class Portal_Ariant_Jnr3_in extends PortalScriptMan {

    @Override
    public boolean enter() {
        String reactor_name;
        int map_next;
        String portal_next;
        boolean in = true;
        switch (getScriptName()) {
            case "jnr3_in0" -> {
                reactor_name = "jnr3_out1";
                map_next = 926110201; // 暗い研究室1
                portal_next = "out00";
            }
            case "jnr3_in1" -> {
                reactor_name = "jnr3_out2";
                map_next = 926110202; // 暗い研究室2
                portal_next = "out00";
            }
            case "jnr_201_0" -> {
                reactor_name = "jnr31_out";
                map_next = 926110200; // 特殊な実験室
                portal_next = "in00";
                in = false;
            }
            case "jnr_202" -> {
                reactor_name = "jnr32_out";
                map_next = 926110200; // 特殊な実験室
                portal_next = "in01";
                in = false;
            }
            case "rnj3_in0" -> {
                reactor_name = "rnj3_out1";
                map_next = 926100201; // 暗い研究室1
                portal_next = "out00";
            }
            case "rnj3_in1" -> {
                reactor_name = "rnj3_out2";
                map_next = 926100202; // 暗い研究室2
                portal_next = "out00";
            }
            case "rnj_201_0" -> {
                reactor_name = "rnj31_out";
                map_next = 926100200; // 特殊な実験室
                portal_next = "in00";
                in = false;
            }
            case "rnj_202" -> {
                reactor_name = "rnj32_out";
                map_next = 926100200; // 特殊な実験室
                portal_next = "in01";
                in = false;
            }
            default -> {
                return false;
            }
        }
        if (getReactorState(reactor_name) <= 0) {
            if (in) {
                // TODO : 文章 (js の英文を翻訳, 原文 : The portal has not opened yet.)
                playerMessage(5, "ポータルはまだ開いていません。");
            } else {
                // TODO : 文章 (js の英文を翻訳, 原文 : The portal is not open.)
                playerMessage(5, "ポータルが開いていません。");
            }
            return false;
        }
        return warp(map_next, portal_next);
    }
}
