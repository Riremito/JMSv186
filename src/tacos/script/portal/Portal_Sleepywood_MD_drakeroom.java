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
// Map.wz portal script : MD_drakeroom (JMS186), js の処理を利用
public class Portal_Sleepywood_MD_drakeroom extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getMapId() == 105090311) {
            if (isPartyMember()) {
                if (isLeader()) {
                    warpParty(105090320);
                } else {
                    // TODO : 文章 (js の英文を翻訳, 原文 : You are not the leader of the party.)
                    playerMessage(5, "パーティーリーダーではありません。");
                }
            } else {
                warp(105090320, 0);
            }
            // TODO : 文章 (js の英文を翻訳, 原文 : All of the Mini-Dungeons are in use right now, please try again later.)
            playerMessage(5, "現在、全てのミニダンジョンが使用中です。後でもう一度お試しください。");
            return false;
        } else {
            return warp(105090311, "MD00");
        }
    }
}
