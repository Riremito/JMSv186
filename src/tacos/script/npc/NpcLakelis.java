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
public class NpcLakelis extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#e<グループクエスト：一番目の同行>#n");
                nt.add("グループメンバーたちと共に力を合わせてクエストを解決してみませんか？その中にはお互いに力を合わせないと解決できない障害物がたくさんありますので…挑戦を希望しましたら#bあなたが属しているグループのグループ長#kを通し、私に声をかけてください。");
                nt.addMenu(0, "グループクエストがしたいです");
                nt.addMenu(1, "共にするグループメンバーを探したいです");
                nt.addMenu(2, "説明が聞きたいです");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : グループクエスト (KerningPQ) の開始 (グループ長, レベル21以上, 4人以上が同じマップにいる), 黒い玉 (4001007, 4001008) の回収
                // TODO : 共にするグループメンバーを探したいです, 説明が聞きたいです
                // 元のスクリプトはデバッグモードの選択肢で KerningPQ を開始していた
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
