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
public class NpcAlcaster extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 古代の本の封印クエストの完了確認, アイテムの購入 (購入数の入力, メルの支払い)
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("君のお陰で#b古代の本#kは無事に封印できた。だがそのせいで８００年間に修練してきたわしの力の半分を無くしたがね、もう死んでも惜しくない。そうだ、どころで君、珍しいアイテムは欲しくないか？今まで手伝ってくれたお礼だ。わしが持っているもので必要なものがあるなら安く売ってあげよう。");
                nt.addMenu(0, "聖水(値段：300 メル)");
                nt.addMenu(1, "万病治療薬(値段：400 メル)");
                nt.addMenu(2, "魔法の石(値段：5000 メル)");
                nt.addMenu(3, "召喚の石(値段：5000 メル)");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
