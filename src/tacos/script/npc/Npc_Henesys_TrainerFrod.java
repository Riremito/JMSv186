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
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_InvOp;
import tacos.server.map.object.TacosPet;

/**
 *
 * @author Riremito
 */
public class Npc_Henesys_TrainerFrod extends NpcScriptMan {

    private final int item_bartos_letter = 4031035; // バルトスの手紙
    private final int pet_closeness = 2;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (!checkItem(this.item_bartos_letter)) {
                    // 翻訳
                    say("兄さんには障害物コースに気をつけろって言われてるけど…兄さんから遠く離れてるから、いたずらしたくてたまらないんだ…へへ。どうせ直接見られてないし、ちょっとくらいのんびりしてもいいよね。");
                    return false;
                }
                // 翻訳
                say("やった、兄さんからの手紙だ！どうせ僕がさぼってると思って叱ってるんだろうな…ん？ああ…兄さんのアドバイスどおりにペットを訓練して、ここまで登ってきたんだね？すごい！ここまで頑張って来たんだから、ペットとの親密度を上げてあげるよ。", false, true);
                return true;
            }
            case 1 -> {
                TacosPet pet = getPlayer().getPetByIndex(0);
                if (pet == null) {
                    // 翻訳
                    say("うーん…本当にペットと一緒に来たの？この障害物はペット用なんだよ。ペットなしでここで何してるの？冗談でしょ！！！");
                    return false;
                }
                if (!checkItem(this.item_bartos_letter)) {
                    // 翻訳
                    say("ねえ、本当に兄さんの手紙を持ってる？ETCインベントリを開いて、手紙が本当にあるか確認してよ！");
                    return false;
                }
                removeItem(this.item_bartos_letter, 1);
                pet.setCloseness(pet.getCloseness() + this.pet_closeness);
                getPlayer().SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.CASH, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())).build()));
                // 翻訳
                say("どう？ペットとすごく仲良くなったと思わない？時間があったら、またこの障害物コースでペットを訓練してね…もちろん、兄さんの許可をもらってからね。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
