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
// Reactor.wz action : syarenNPC0 (JMS186), js の処理を利用
public class Reactor_GuildQuest_SyarenNPC0 extends ReactorScriptMan {

    @Override
    public void action() {
        // TODO : 文章 (js の英文を翻訳, 原文 : A bright flash of light, then someone familiar appears in front of the blocked gate.)
        mapMessage("まばゆい光とともに、塞がれた門の前に見覚えのある姿が現れた。");
        summonNpc(9040003);
    }
}
