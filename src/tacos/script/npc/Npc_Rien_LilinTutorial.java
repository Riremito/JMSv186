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
// Npc.wz script : awake (JMS176-302), lua_script_jp の awake は中身がないため js の処理を利用
public class Npc_Rien_LilinTutorial extends NpcScriptMan {

    private final int quest_helper = 21019; // アランのチュートリアル (InfoQuest)
    private final String quest_helper_clear = "helper=clear";
    private final int map_ice_cave = 140090000; // スノーアイランド : 氷洞窟
    private final int map_cold_forest_1 = 140090100; // スノーアイランド : 冷たい森1
    private final String portal_name = "st00";
    private final String effect_face = "Effect/Direction1.img/aranTutorial/face";
    private final String effect_click_lilin = "Effect/Direction1.img/aranTutorial/ClickLirin";
    // チュートリアルの項目 (UserTutorMsg の番号)
    private final int[] tutor_indexes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    // TODO : 文章 (適当)
    private final String[] tutor_menus = {
        "ミニマップ",
        "クエストウィンドウ",
        "インベントリ",
        "通常攻撃",
        "アイテムの拾い方",
        "アイテムの装備",
        "スキルウィンドウ",
        "クイックスロットの使い方",
        "箱の壊し方",
        "椅子への座り方",
        "能力値の上げ方"
    };
    // 目覚めたとき (true : プレイヤーの台詞)
    private final boolean[] awake_users = {false, true, false, true, true};
    // TODO : 文章 (適当)
    private final String[] awake_texts = {
        "あなた…やっと目が覚めたのですね！",
        "…あなたは誰？",
        "ずっとお待ちしていました。黒魔法使いと戦った英雄が目覚めるのを…！",
        "ちょっと待って、何を言っているんだ？それに、あなたは誰…？",
        "待てよ…俺は誰なんだ…？昔のことが何も思い出せない。うっ…ひどい頭痛だ！"
    };
    // 目覚めた後 (true : プレイヤーの台詞)
    private final boolean[] explain_users = {false, true, false, false, false, false, false};
    // TODO : 文章 (適当)
    private final String[] explain_texts = {
        "大丈夫ですか？",
        "本当に何も覚えていないんだ…ここはどこ？それに、あなたは誰？",
        "落ち着いてください。記憶がないのは黒魔法使いの呪いのせいです。昔のことは心配しなくても大丈夫。詳しく説明しますね。",
        "あなたはこの世界の英雄なのです。数百年前、あなたは仲間たちと共に黒魔法使いと戦い、メイプルワールドを滅亡から救いました。でも最後の瞬間、黒魔法使いはあなたに呪いをかけ、あなたは長い間氷の中に閉じ込められ、記憶もすべて失ってしまったのです。",
        "ここはリエンという島で、黒魔法使いが数百年もの間あなたを閉じ込めておく場所として選んだ島です。その呪いのせいで、この島はいつも雪と氷に覆われています。あなたは洞窟の奥深くで見つかったのですよ。",
        "そして私の名前はリリン、リエン族の一人です。リエン族は何世紀もの間あなたの帰りを待ち望んでいました。そしてついに…その願いが叶ったのです。伝説の英雄が、今私の目の前にいるのですから。",
        "一度にたくさん話しすぎてしまったかもしれませんね。全部わからなくても大丈夫です。いずれわかるようになりますから。それよりも、#bまずは村へ向かってください#k。村に着くまでに何かわからないことがあれば、いつでも聞いてくださいね。"
    };

    @Override
    public boolean action(int status, int answer) {
        // 氷洞窟以外
        if (getMapId() != this.map_ice_cave) {
            if (status == 0) {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("待ってください！ここで説明することはレベル10までプレイすれば自然と身につくものなので、今すぐ覚える必要はありません。前もって知っておきたい方だけ聞いてくださいね。\r\nさて、どれについて知りたいですか？");
                for (int i = 0; i < this.tutor_indexes.length; i++) {
                    nt.addMenu(this.tutor_indexes[i], this.tutor_menus[i]);
                }
                askMenu(nt);
                return true;
            }
            for (int index : this.tutor_indexes) {
                if (index == answer) {
                    tutorMsg(index);
                    break;
                }
            }
            return false;
        }
        // 目覚めたとき
        if (getPlayer().getInfoQuest(this.quest_helper).isEmpty()) {
            if (status < 0) {
                return false;
            }
            if (status < this.awake_texts.length) {
                if (this.awake_users[status]) {
                    sayUser(this.awake_texts[status], 0 < status, true);
                } else {
                    say(this.awake_texts[status], 0 < status, true);
                }
                return true;
            }
            getPlayer().updateInfoQuest(this.quest_helper, this.quest_helper_clear);
            showReservedEffect(this.effect_face);
            showReservedEffect(this.effect_click_lilin);
            hireTutor(true);
            return false;
        }
        // 目覚めた後
        if (status < 0) {
            return false;
        }
        if (status < this.explain_texts.length) {
            if (this.explain_users[status]) {
                sayUser(this.explain_texts[status], 0 < status, true);
            } else {
                say(this.explain_texts[status], 0 < status, true);
            }
            return true;
        }
        hireTutor(true);
        warp(this.map_cold_forest_1, this.portal_name);
        return false;
    }
}
