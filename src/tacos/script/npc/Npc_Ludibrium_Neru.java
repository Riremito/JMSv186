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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_InvOp;
import tacos.server.map.object.TacosPet;

/**
 *
 * @author Riremito
 */
public class Npc_Ludibrium_Neru extends NpcScriptMan {

    private final int item_weaver_letter = 4031128; // ウィーバーの手紙
    private final int pet_closeness = 4;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (!checkItem(this.item_weaver_letter)) {
                    // 翻訳
                    say("兄さんには障害物コースの管理を任されてるんだけど…兄さんから遠く離れてるから、つい遊んじゃうんだよね、へへ…兄さんは俺を見張れないから、ちょっとくらい息抜きしてもいいだろ。");
                    return false;
                }
                // 翻訳
                say("おっ！これは兄さんからの手紙だ！きっと俺が真面目に働いてないって叱ってるんだろうな…え？ああ…兄さんのアドバイス通りにペットを訓練して、ここまで来たのかい？すごいじゃないか！！ここまで頑張って来たんだから、ペットとの親密度を上げてあげよう。", false, true);
                return true;
            }
            case 1 -> {
                TacosPet pet = getPlayer().getPetByIndex(0);
                if (pet == null) {
                    // 翻訳
                    say("うーん…ペットを連れてここまで来たのかい！？この障害物はペット用なんだ。ペットもいないのにここで何をしてるんだい？出て行ってくれ！");
                    return false;
                }
                if (!checkItem(this.item_weaver_letter)) {
                    // 翻訳
                    say("おい！本当に兄さんの手紙を持ってるのかい？ETCインベントリに手紙があるか確認してくれ！");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_weaver_letter, 1, true, false);
                pet.setCloseness(pet.getCloseness() + this.pet_closeness);
                getPlayer().SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.CASH, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())).build()));
                // 翻訳
                say("どうだい？ペットとずいぶん仲良くなったと思わないかい？時間があったら、またこの障害物コースでペットを訓練してあげてくれ…もちろん、兄さんの許可を得てからね！");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
