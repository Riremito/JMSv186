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
// Map.wz portal script : glpqPortal00, glpqPortal01, glpqPortal02, glpqPortal03, glpqPortal04 (JMS186), js の処理を利用
// 職業ごとのポータル
// TODO : JMS186 に存在しないマップ
public class Portal_Zipangu_GlpqPortal extends PortalScriptMan {

    @Override
    public boolean enter() {
        int job_type = getJob() % 1000 / 100;
        switch (getScriptName()) {
            case "glpqPortal00" -> {
                if (job_type == 1) {
                    return warp(610030510, 0);
                }
                // TODO : 文章 (js の英文を翻訳, 原文 : Only warriors may enter this portal.)
                playerMessage(5, "このポータルには戦士のみ入ることができます。");
            }
            case "glpqPortal01" -> {
                if (job_type == 3) {
                    return warp(610030540, 0);
                }
                // TODO : 文章 (js の英文を翻訳, 原文 : Only bowmen may enter this portal.)
                playerMessage(5, "このポータルには弓使いのみ入ることができます。");
            }
            case "glpqPortal02" -> {
                if (job_type == 2) {
                    return warp(610030521, 0);
                }
                // TODO : 文章 (js の英文を翻訳, 原文 : Only mages may enter this portal.)
                playerMessage(5, "このポータルには魔法使いのみ入ることができます。");
            }
            case "glpqPortal03" -> {
                if (job_type == 4) {
                    return warp(610030530, 0);
                }
                // TODO : 文章 (js の英文を翻訳, 原文 : Only thieves may enter this portal.)
                playerMessage(5, "このポータルには盗賊のみ入ることができます。");
            }
            case "glpqPortal04" -> {
                if (job_type == 5) {
                    return warp(610030550, 0);
                }
                // TODO : 文章 (js の英文を翻訳, 原文 : Only pirates may enter this portal.)
                playerMessage(5, "このポータルには海賊のみ入ることができます。");
            }
            default -> {
            }
        }
        return false;
    }
}
