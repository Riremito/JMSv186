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

import odin.server.MapleItemInformationProvider;
import tacos.client.TacosCharacter;
import tacos.debug.DebugMan;
import tacos.debug.IDebugMan;
import tacos.packet.ops.OpsMessage;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_Message;

/**
 *
 * @author Riremito
 */
public class NpcCorba extends DebugMan implements IDebugMan {

    @Override
    public boolean start(TacosCharacter chr) {
        super.start(this, chr);
        return true;
    }

    @Override
    public boolean end(TacosCharacter chr) {
        super.end(chr);
        return true;
    }

    private final int item_mini_dragon = 2210016; // ミニドラゴン変身
    private final int map_dragon_flight = 200090500; // 飛行中 : 時間の神殿への道

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcTag nt = new NpcTag();
                nt.add("羽があればそこに行けるじゃろう。じゃが、それだけでは足りんのじゃ。剣より鋭い風の狭間を飛ぶためには、丈夫な鱗も必要なんじゃ。戻ってくる方法まで知っておる半獣族は、もうワシ一人しかおらん…もしあそこに行きたいんじゃったら、ワシが変身させてやろう。お前の今の姿が何であれ、今、この瞬間だけは　#bドラゴン#kになるのじゃ…。");
                nt.addMenu(0, "ドラゴンに変身したいです。");
                super.askMenu(chr, nt);
                return true;
            }
            case 1 -> {
                if (answer != 0) {
                    return false;
                }
                MapleItemInformationProvider.getInstance().getItemEffect(this.item_mini_dragon).applyTo(chr);
                chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_GiveBuffMessage, PB_Message.builder().ItemID(this.item_mini_dragon).build()));
                chr.changeMapById(this.map_dragon_flight); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
