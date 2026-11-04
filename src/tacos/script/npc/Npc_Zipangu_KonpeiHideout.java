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

/**
 *
 * @author Riremito
 */
public class Npc_Zipangu_KonpeiHideout extends NpcScriptMan {

    private static final int npc_konpei_hideout = 9120200; // コンペイ (アジト前)
    private static final int npc_konpei_boss = 9120202; // コンペイ (悪夢の果て)
    private static final int npc_konpei_clear = 9120203; // コンペイ (アジト前(天晴れ))
    private final int map_showa_town = 801000000; // ジパング : ショーワ町
    private final int map_hideout = 801040000; // ジパング : アジト前
    private final int map_hideout_clear = 801040101; // ジパング : アジト前(天晴れ)
    private final int item_boss_lantern = 4000141; // 大親分の提灯
    private final int item_elixir = 2000004; // エリクサー
    private final int elixir_count = 100;
    private int ask_status = -1;

    @Override
    public boolean action(int status, int answer) {
        // いいえを選択した
        if (0 <= this.ask_status && status == this.ask_status - 1) {
            switch (getNpcId()) {
                case npc_konpei_hideout -> {
                    // 翻訳
                    say("昭和村に戻りたいなら、俺に話しかけてくれ。");
                }
                case npc_konpei_boss -> {
                    // 翻訳
                    say("お前のタフさには本当に感心するよ！まあ、昭和村に戻ることにしたら教えてくれ～！");
                }
                default -> {
                    // 翻訳
                    say("そのタフさ！すごかったぜ！！まあ、昭和村に戻りたくなったら教えてくれ。");
                }
            }
            return false;
        }
        // はいを選択した
        if (0 <= this.ask_status && status == this.ask_status + 1) {
            warp((getNpcId() == npc_konpei_boss) ? this.map_hideout : this.map_showa_town);
            return false;
        }
        switch (getNpcId()) {
            case npc_konpei_hideout -> {
                this.ask_status = status;
                // 翻訳
                askYesNo("さあ、アジトの真ん前だ！何？昭和村に戻りたいって？");
                return true;
            }
            case npc_konpei_boss -> {
                if (checkItem(this.item_boss_lantern)) {
                    // 翻訳
                    say("そ…その懐中電灯！！本当にボスを倒したのか…？？お前…！すごい、俺には才能を見抜く目があるんだな。まったく信じられないぜ！さあ、ここから出よう！");
                    warp(this.map_hideout_clear);
                    return false;
                }
                this.ask_status = status;
                // 翻訳
                askYesNo("ボスを倒したら、証拠としてボスの懐中電灯を見せてもらうぞ。懐中電灯を見せてくれるまでは信じないからな！何？この部屋から出たいって？");
                return true;
            }
            case npc_konpei_clear -> {
                int count = getPlayer().getItemQuantity(this.item_boss_lantern, false);
                switch (status) {
                    case 0 -> {
                        // 翻訳
                        say("おお、すごい、やり遂げたんだな！あいつは本当にしぶとかった。これでここに待望の平和が訪れるといいんだが、どうも最悪の事態が心配でな。とにかく、あいつがいなくなってよかったよ。", false, true);
                        return true;
                    }
                    case 1 -> {
                        if (count > 0) {
                            // 翻訳
                            say("そうだ！ボスが落とす懐中電灯は、今後のために俺が預かっておく。あいつの正体がわかった今、平和な日々がやってくる気がするよ。正直に言うと、あの怪物が本当にあいつだったとわかったときは…不意を突かれたぜ。", false, true);
                            return true;
                        }
                        this.ask_status = status;
                        // 翻訳
                        askYesNo("昭和村に戻るか？");
                        return true;
                    }
                    case 2 -> {
                        if (count <= 0) {
                            return false;
                        }
                        if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_elixir, this.elixir_count, "")) {
                            // 翻訳
                            say("アイテムインベントリがいっぱいでないか確認してくれ。", false, true);
                            return true;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_boss_lantern, count, true, false);
                        MapleInventoryManipulator.addById(getPlayer(), this.item_elixir, (short) this.elixir_count);
                        this.ask_status = status;
                        // 翻訳
                        askYesNo("昭和村に戻るか？");
                        return true;
                    }
                    case 3 -> {
                        this.ask_status = status;
                        // 翻訳
                        askYesNo("昭和村に戻るか？");
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
