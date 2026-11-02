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
import odin.server.maps.SavedLocationType;
import tacos.client.TacosCharacter;
import tacos.debug.DebugMan;
import tacos.debug.IDebugMan;

/**
 *
 * @author Riremito
 */
public class NpcPason extends DebugMan implements IDebugMan {

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

    private final int item_free_pass = 4031134; // フリーパスカード
    private final int map_florina_beach = 110000000; // フロリナ : ビーチ

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                // デバッグモード
                if (!chr.haveItem(this.item_free_pass)) {
                    MapleInventoryManipulator.addById(chr, this.item_free_pass, 1); // TODO : item effect.
                }
                NpcTag nt = new NpcTag();
                nt.add("港口から少し離れたところに#bビーチ#kという幻想的な海岸があると聞いたことはあるか？#b2000メル#kを払うか#bフリーパスカード#kを持っているなら、いつでも俺がそこに運んでやるぜ。");
                nt.addMenu(0, "2000メル払う");
                nt.addMenu(1, "フリーパスカードを持っている。");
                nt.addMenu(2, "フリーパスカードとは？");
                super.askMenu(chr, nt);
                return true;
            }
            case 1 -> {
                // TODO : フリーパスカードを持っている, フリーパスカードとは？
                if (answer != 0) {
                    return false;
                }
                NpcTag nt = new NpcTag();
                // 原文ママ
                nt.add("#b2000メル#kを払ってビーチに行くんだな？でも、そこにもモンスターがいるらしいから油断するよ！では、早速出向準備をするが…今すぐビーチに行くのか？");
                super.askYesNo(chr, nt);
                return true;
            }
            case 2 -> {
                // TODO : 2000 メルの支払い
                if (chr.haveItem(this.item_free_pass)) {
                    MapleInventoryManipulator.removeById(chr, MapleInventoryType.ETC, this.item_free_pass, 1, true, false); // TODO : item effect.
                    chr.saveLocation(SavedLocationType.FLORINA);
                    chr.changeMapById(this.map_florina_beach); // TODO : portal name.
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
