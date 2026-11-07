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
public class Npc_Ludibrium_LibrarianWiz extends NpcScriptMan {

    private final int[] quest_ids = {
        3615, // 「キラクとトクゲツ」の昔話本の回収
        3616, // 「サクラコとシズク」の昔話本の回収
        3617, // 「仲良し兄弟」の昔話本の回収
        3618, // 「鬼」の昔話本の回収
        3630, // 「太陽と月になった兄弟」の昔話本の回収
        3633, // 「孝行娘 小町」の昔話本の回収
        3639 // 「金の斧と銀の斧」 の昔話本の回収
    };
    private final int[] item_ids = {
        4031235, // 「キラクとトクゲツ」の昔話本
        4031236, // 「サクラコとシズク」の昔話本
        4031237, // 「仲良し兄弟」の昔話本
        4031238, // 「鬼」の昔話本
        4031270, // 「太陽と月になった兄弟」の昔話本
        4031280, // 「孝行娘 小町」の昔話本
        4031298 // 「金の斧と銀の斧」の昔話本
    };

    @Override
    public boolean action(int status, int answer) {
        String name = getCharacterName();
        int count = 0;
        StringBuilder books = new StringBuilder();
        for (int i = 0; i < this.quest_ids.length; i++) {
            if (getQuestStatus(this.quest_ids[i]) == 2) {
                count++;
                books.append("#b#v").append(this.item_ids[i]).append("##t").append(this.item_ids[i]).append("##k\r\n");
            }
        }
        switch (status) {
            case 0 -> {
                if (count == 0) {
                    // 翻訳
                    say("#b" + name + "#kさんはまだ童話の本を1冊も返却していません…");
                    return false;
                }
                // 翻訳
                say("どれどれ…#b" + name + "#kさんが返却した本は全部で#b" + count + "#k冊です。\r\n返却された本のリストは次のとおりです:\r\n\r\n" + books, false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("図書館が片付いてきたのは、主にあなたのおかげです、#b" + name + "#kさん。またお話がごちゃまぜになったら、そのときもあなたに直してもらえると頼りにしていますね。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
