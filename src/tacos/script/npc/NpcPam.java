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
package tacos.script.npc;

/**
 *
 * @author Riremito
 */
public class NpcPam extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // Npc.wz script : babyfood (JMS164-302, GMS92-95, KMS95-114, CMS, EMS, TWMS122-125, THMS, MSEA)
                // Npc.wz script : loveProof_Leaf (JMS146-147, TWMS77)
                // 原文ママ (JMS186 String.wz の d0)
                say("ラララ～あら、まだここにいるの？僕の歌うのが下手だから聞かないでください。恥ずかしいですよ～。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
