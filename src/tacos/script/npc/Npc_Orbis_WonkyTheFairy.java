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
public class Npc_Orbis_WonkyTheFairy extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("お～、会えて嬉しいよ！女神の塔を冒険したいの？グループに戦士、魔法使い、盗賊、弓使いが各1名以上いるとウィンキの祝福をかけてあげるわ。");
                nt.addMenu(0, "入場を申し込む。");
                nt.addMenu(1, "女神の塔について聞く。");
                nt.addMenu(2, "ウィンキに食べ物を渡す。");
                nt.addMenu(3, "女神の羽について聞く。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : グループクエスト (OrbisPQ) の開始 (グループ長, 5人以上, レベル51以上), 女神の像のかけら (4001044 - 4001063) の回収
                // TODO : 女神の塔について聞く, ウィンキに食べ物を渡す, 女神の羽について聞く
                // TODO : 入場口 (920010000) での会話, ミネルバの腕輪 (1082232) の交換 (コメントアウトされた旧スクリプト)
                // 元のスクリプトはデバッグモードの選択肢で OrbisPQ を開始していた
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
