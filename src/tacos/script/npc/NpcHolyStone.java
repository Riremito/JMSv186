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

import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcHolyStone extends NpcScriptMan {

    private final int item_necklace_of_wisdom = 4031058; // 知恵のネックレス
    private final int question_count = 5;
    private final String[] questions = {
        "次の中でメイプルストーリーを始めると一番最初に会えるNPCは誰なのか？",
        "レベル1からレベル2になるために、必要な必要経験値はいくつなのか？",
        "次の中でオシリア大陸　エルナスで会えないNPCは誰なのか？",
        "次の中でオシリア　オルビスで会えないNPCは誰なのか？",
        "次の中で何回も反復して、修行できる　クエストはどれだ？",
        "次の中で2次転職をすると、もらえる職業の名称でないのはどれだ？",
        "ペリオンのコブシを開いて立てがかぶっている帽子に付いている羽はいくつか？(SS)",
        "次の中ビクトリアアイランドのエリニアで会えないNPCは誰なのか？",
        "ビクトリアアイランドのヘネシスで会えないNPCは誰？",
        "次の中オシリア大陸で会えるモンスターでないのはどれだ？",
        "次の中一番レベルが高いモンスターはどれだ？",
        "次の中メイプルストーリーに登場する水薬と水薬の機能の正しい組み合わせでないのはどれだ？",
        "次の中ペットと関連されたNPCではないのは誰か？",
        "次の状態異常で正しい組み合わせでないものはどれか",
        "ノーチラスのカイリンがいる航海室のモニターに見えるNPC名は？",
        "次の中でオリシア大陸のあちこちに散らばっているアルファ小隊の隊員ではないNPCは誰なのか？",
        "次の中でペリオンのマンジからもらった古びたグラディウスを覚醒させるために必要なアイテムではないのはどれだ？",
        "次の中でビクトリアアイランドのカニングシティーで会えないNPCは誰なのか？",
        "次の中で、空中を飛び回るモンスターはどれだ？",
        "次の中でモンスターとそのモンスターがくれる戦利品の正しい組み合わせでないのはどれだ？",
        "ビクトリア大陸を構成していない町はどれ？",
        "次の中 ヘネシスのマヤが自分の病気を治すために、手に入れて来て欲しいと言った物はどれだ？",
        "該当クエストをするために必要な要求レベルが一番高いクエストはどれだ？",
        "次の中、何かを合成、精錬、製作してくれるNPCではないのは誰か。",
        "次の中、メイプルストーリーに登場する水薬と水薬の機能の正しい組み合わせはどれだ?",
        "次の中でモンスターとそのモンスターがくれる戦利品の正しい組み合わせはどれだ？",
        "メイプルアイランドで見られないモンスターはどれだ？",
        "次の中でビクトリアアイランドのペリオンで会えないNPCは誰なのか？",
        "次の中でカニングシティーで会えるアレックスのお父さんは誰なのか？",
        "次の中で2次転職のために黒い玉30個を集めると転職教官NPCからもらう物はどれだ？",
        "エリニアのハインズが持っている玉の色は？",
        "カニングシティーのダークロードがいるマップ名は？(SS)",
        "ヘネシスのヘレナの目の色は？",
        "次の中ビクトリアアイランドアリの巣を含めた中央ダンジョンの中で、見られないモンスターはどれだ？"
    };
    private final String[][] choices = {
        {"ローザー", "リナ", "ニナ", "セン", "ヒナ"},
        {"10", "15", "20", "25", "30"},
        {"ボゲン", "フォックス曹長", "メイドエルマ", "ゼイド", "スカドル"},
        {"チャーリー軍曹", "ソフィア", "ネーブ", "妖精ヌリ", "妖精エデル"},
        {"マヤと謎の薬", "家出少年アレックス", "ピアと青キノコ", "アルウェンのガラス靴", "アルファ小隊連絡網"},
        {"ページ", "アサシン", "シーフ", "クレリック", "バッカニア"},
        {"5本", "10本", "13本", "17本", "14本"},
        {"シェイン", "エトラン", "ハインズ", "妖精アルウェン", "ロエル"},
        {"マヤ", "テオ", "リナ", "へレナ", "ピア"},
        {"ホワイトパン", "クロコ", "イェティ", "ライカンスロープ", "サンフィクシ"},
        {"オクトパス", "リボンピグ", "緑キノコ", "ハイスタンプ", "バブルリング"},
        {"聖水-封印,呪い状態回復", "暁の露-MP3000回復", "ハンバーガー-HP400回復", "サラダ-MP200回復", "青い薬-MP100回復"},
        {"クロイ", "妖精マル", "プロド", "ビシャス", "キュト"},
        {"暗黒-命中率減少", "呪い-獲得経験値減少", "虚弱-移動速度減少", "封印-スキル使用不可", "中毒-徐々にHP減少"},
        {"アベル", "ポーター", "ドクター中村", "シュリンツ", "デイビーゾーン"},
        {"ピーター中士", "チャーリー軍曹", "フォックス曹長", "イージー兵長", "ブラボー伍長"},
        {"氷のかけら", "星の石", "炎の羽毛", "妖精の羽", "古代の呪文書"},
        {"裏通りのゼイエム", "シュミ", "モンロン", "ドンジオバネ", "ルーク"},
        {"ヘクタ", "ネペンデス", "アイアンピグ", "アムティ", "メロディ"},
        {"スターフィクシ-スターフィクシの星の欠片", "ネペンデス-ネペンデスの花びら", "ママシュ-ママシュの胞子", "ペペ-ペペの口ばし", "ルーパン-ルーパンのバナナ"},
        {"スリーピーウッド", "アムホスト", "ペリオン", "カニングシティー", "エリニア"},
        {"不思議な薬", "まずい薬", "万能治療薬", "漢方薬", "謎の薬"},
        {"マンジの古びたグラディウス", "警備兵のルークの冒険決心", "古代の本を探しに・・・", "アルケスターと暗黒のクリスタル", "アルファ小隊連絡網"},
        {"ネーブ", "サンダー", "シェイン", "エトラン", "ボゲン"},
        {"橙色の薬-HP200回復", "チューチューアイス-MP2000回復", "レモン-MP100回復", "チキンHP300回復", "ピザ-HP400回復"},
        {"ファイアボア-ファイアボアの鼻", "コールドアイ-コールドアイの目", "ピグ-ピグの耳", "スティジ-スティジの羽", "ゾンビキノコ-ゾンビキノコの傘"},
        {"青デンデン", "メイプルキノコ", "赤デンデン", "リボンピグ", "スポア"},
        {"グレゴリソン", "エトラン", "マンジ", "サンダー", "ソフィア"},
        {"クロイ", "ビシャス", "ルーカス", "ゾーン", "長老スタン"},
        {"英雄の証", "英雄のネックレス", "英雄のペンダント", "英雄のメダル", "英雄の標識"},
        {"黄色", "白", "青", "黒", "赤"},
        {"泥棒のアジト", "盗賊の休憩室", "盗賊のアジト", "盗賊の教育院", "泥棒の図書館"},
        {"黄色", "赤", "緑", "白", "青"},
        {"ツノキノコ", "エビルアイ", "ドレイク", "ゾンビキノコ", "ストーンボール"}
    };
    private final int[] answers = {
        4,
        1,
        2,
        1,
        3,
        4,
        2,
        4,
        1,
        1,
        3,
        1,
        3,
        2, // 原文 : 虚弱・移動速度減少
        2,
        0,
        3,
        4,
        4,
        1,
        1,
        4,
        3,
        2,
        4,
        3,
        3,
        1,
        4,
        0,
        2,
        2,
        2,
        4
    };
    private int answer_index = -1;

    @Override
    public boolean action(int status, int answer) {
        // TODO : 3次転職のクエストの確認
        if (2 <= status && status <= this.question_count + 1) {
            if (answer != this.answer_index) {
                say("間違い");
                return false;
            }
        }
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よかろう。これから汝の知恵をテストしよう。全ての質問に正しい答えを選ぶと合格だ。ただし途中で1つでも間違うと最初からやり直さねばならん。では始めるぞ。");
                say(nt, false, true);
                return true;
            }
            case 1, 2, 3, 4, 5 -> {
                int question_num = (int) (Math.random() * this.questions.length);
                NpcScriptTag nt = new NpcScriptTag();
                nt.add(status + "つ目の問題だ。" + this.questions[question_num]);
                for (int i = 0; i < this.choices[question_num].length; i++) {
                    nt.addMenu(i, this.choices[question_num][i]);
                }
                this.answer_index = this.answers[question_num];
                askMenu(nt);
                return true;
            }
            case 6 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("汝の答えに嘘がないのを認めよう。");
                nt.add("これで汝の知恵も証明された。");
                nt.add("このネックレスを持って帰るがいい。");
                MapleInventoryManipulator.addById(getPlayer(), this.item_necklace_of_wisdom, 1); // TODO : item effect.
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
