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
// Map.wz portal script : rankRoom (JMS186), js の処理を利用
// 殿堂へ移動
public class Portal_Common_RankRoom extends PortalScriptMan {

    @Override
    public boolean enter() {
        switch (getMapId()) {
            case 130000000 -> {
                return warp(130000100, "east00"); // クィーンズロード : 騎士の殿堂 (or 130000101)
            }
            case 130000200 -> {
                return warp(130000100, "west00"); // クィーンズロード : 騎士の殿堂 (or 130000101)
            }
            case 140010100 -> {
                return warp(140010110, "out00"); // スノーアイランド : 英雄の殿堂 (or 140010111)
            }
            case 120000101 -> {
                return warp(120000105, "out00"); // ノーチラス : 訓練場
            }
            case 103000003 -> {
                return warp(103000008, "out00"); // ビクトリアロード : 盗賊の殿堂 (or 103000009)
            }
            case 100000201 -> {
                return warp(100000204, "out00"); // ビクトリアロード : 弓使いの殿堂 (or 100000205)
            }
            default -> {
                return warp(getMapId() + 1, 0); // or + 2
            }
        }
    }
}
