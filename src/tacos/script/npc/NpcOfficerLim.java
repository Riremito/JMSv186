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
public class NpcOfficerLim extends NpcScriptMan {

    private final int map_black_sheep = 701010321; // 中国 : ブラックシープンの領域

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 前提クエスト 実力の証明, 資格証明書の確認
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こんにちは。私はポリンと申します。この中は誰もが入れるわけではありませんが、あたなは秘密任務の遂行中のようですね。中に送らせますので#b警察官ミャオ#kに会ってみてください。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                warp(this.map_black_sheep); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
