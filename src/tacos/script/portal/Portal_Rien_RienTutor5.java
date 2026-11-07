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
// Map.wz portal script : rienTutor5 (JMS186), js の処理を利用
public class Portal_Rien_RienTutor5 extends PortalScriptMan {

    @Override
    public boolean enter() {
        // TODO : 文章 (js の英文を翻訳, 原文 : Just a little bit more, and you'll reach the town. I'll head over there first, since I also have some things to take care of. There's no need to rush, Aran. I'll see you there.)
        summonMsg("もう少しで村に着きます。私も用事があるので先に行っていますね。急がなくても大丈夫ですよ、アラン。村で会いましょう。");
        return false;
    }
}
