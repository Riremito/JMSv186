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
// Npc.wz script : begin_jp1 (JMS131), lua の処理を利用
public class Npc_MapleIsland_Sera extends NpcScriptMan {

    private final int map_training_entrance = 0; // メイプルアイランド : 冒険者講習所入口
    private final int map_training_2f = 1; // メイプルアイランド : 冒険者講習所2階
    private final int map_snail_park = 40000; // キノコ村 : デンデン広場Ⅰ
    private final int quest_training = 8141; // 見習いの訓練
    private boolean skip_training = false; // 訓練プログラムを受けずに旅を始める

    @Override
    public boolean action(int status, int answer) {
        if (getMapId() == this.map_training_2f) {
            if (hasPages()) {
                return nextPage();
            }
            // 翻訳
            return sayPages("ここは映像の部屋。最初の訓練プログラムはここから始まります。この部屋では、あなたが選べる職業を見ることができます。",
                    // 翻訳
                    "しっかり訓練を積めば、職業を選べるようになります。ヘネシスでは弓使い、エリニアでは魔法使い、ペリオンでは戦士、カニングでは盗賊になれます…");
        }
        if (getMapId() != this.map_training_entrance) {
            return false;
        }
        if (this.skip_training) {
            switch (status) {
                case 0 -> {
                    // 翻訳
                    say("訓練プログラムを受けずに旅を始めたいようですね。それでは、訓練の広場へお送りします。気をつけてね～", false, true);
                    return true;
                }
                case 1 -> {
                    warp(this.map_snail_park);
                    return false;
                }
                default -> {
                    // 翻訳
                    say("決めたら話しかけてください。");
                    return false;
                }
            }
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                askYesNo("メイプルストーリーの世界へ#Gようこそ:ようこそ#。この訓練場は、見習いの皆さんを手助けするための場所です。訓練場に入りますか？訓練プログラムを受けずに旅を始める人もいますが、まずは訓練プログラムを受けることをおすすめします。");
                return true;
            }
            case 1 -> {
                // 翻訳
                say("わかりました。では、訓練プログラムに入れてあげますね。教官の指示に従ってください。", false, true);
                return true;
            }
            case 2 -> {
                warp(this.map_training_2f);
                setQuestData(this.quest_training, "enter");
                return false;
            }
            case -1 -> {
                // 「いいえ」の後は status 0 から数え直す
                this.skip_training = true;
                // 翻訳
                askYesNo("本当にもう旅を始めますか？");
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
