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
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : party3_minerva (JMS131-302), 会話文は lua_script_jp (party3_minerva) を利用
public class Npc_Orbis_Minerva extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_MEMBER = 1;
    private static final int NODE_DIARY_ASK = 2;
    private static final int NODE_DIARY_RESULT = 3;
    private static final int NODE_REWARD_SAY = 4;
    private static final int NODE_REWARD = 5;
    private final String field_set_name = "Party5";
    private final String field_set_bonus_name = "Party6";
    private final int map_center_tower = 920010100; // 女神の塔 : 中央塔
    private final int map_storage = 920011100; // 女神の塔 : 宝物倉庫
    private final int map_exit = 920011200; // 女神の塔 : 出口
    private final int map_blessing = 920011300; // 女神の塔 : 女神の祝福
    private final int map_jail1 = 920010910; // 女神の塔 : 監獄１
    private final int map_jail2 = 920010920; // 女神の塔 : 監獄2
    private final int map_jail3 = 920010930; // 女神の塔 : 監獄3
    private final int map_dark_room = 920011000; // 女神の塔 : 暗黒の部屋
    private final int item_diary = 4161014; // 女神の日記帳
    private final int item_diary_page_first = 4001064; // 日記帳のページ1 (4001064 から 4001073 まで)
    private final int item_diary_page_count = 10;
    private final int item_seed = 4001053; // 謎の種1
    private final int item_record_first = 4001056; // 荘厳な音楽のレコード (4001056 から 4001062 まで)
    private final int item_record_last = 4001062; // 鮮やかな音楽のレコード
    private final int mob_ruinel = 9300044; // 女神塔のルイネル
    private final int mob_junior_cellion = 9300051; // 女神塔のジュニアセリオン
    private final int mob_junior_lioner = 9300052; // 女神塔のジュニアライオナ
    private final int mob_junior_grupin = 9300053; // 女神塔のジュニアグリュピン
    private final int quest_clear = 7020; // パーティークエストのクリア
    private final int exp_clear = 23000;
    // パーティークエストで入手するアイテム (lua の party3_takeawayitem), 4001044 から 4001063 までと 4001074
    private final int item_quest_first = 4001044; // 女神像の一番目のかけら
    private final int item_quest_last = 4001063; // 雲のかけら
    private final int item_transparent = 4001074; // 透明アイテム
    // 報酬 (lua の random( 0, 250 ) の 0 から 95 まで), アイテムID, 個数
    private final int[][] rewards = {
        {2000004, 10}, {2000002, 100}, {2000003, 100}, {2000006, 50}, {2022000, 50}, {2022003, 50},
        {2040002, 1}, {2040402, 1}, {2040502, 1}, {2040505, 1}, {2040602, 1}, {2040802, 1},
        {4003000, 70}, {4010000, 20}, {4010001, 20}, {4010002, 20}, {4010003, 20}, {4010004, 20}, {4010005, 20}, {4010006, 15},
        {4020000, 20}, {4020001, 20}, {4020002, 20}, {4020003, 20}, {4020004, 20}, {4020005, 20}, {4020006, 20}, {4020007, 10}, {4020008, 10},
        {1032013, 1}, {1032011, 1}, {1032014, 1}, {1102021, 1}, {1102022, 1}, {1102023, 1}, {1102024, 1},
        {2040803, 1}, {2070011, 1}, {2043001, 1}, {2043101, 1}, {2043201, 1}, {2043301, 1}, {2043701, 1}, {2043801, 1},
        {2044001, 1}, {2044101, 1}, {2044201, 1}, {2044301, 1}, {2044401, 1}, {2044501, 1}, {2044601, 1}, {2044701, 1},
        {2000004, 35}, {2000002, 80}, {2000003, 80}, {2000006, 35}, {2022000, 35}, {2022003, 35},
        {4003000, 75}, {4010000, 18}, {4010001, 18}, {4010002, 18}, {4010003, 18}, {4010004, 18}, {4010005, 18}, {4010006, 12},
        {4020000, 18}, {4020001, 18}, {4020002, 18}, {4020003, 18}, {4020004, 18}, {4020005, 18}, {4020006, 18}, {4020007, 7}, {4020008, 7},
        {2040001, 1}, {2040004, 1}, {2040301, 1}, {2040401, 1}, {2040501, 1}, {2040504, 1}, {2040601, 1}, {2040601, 1},
        {2040701, 1}, {2040704, 1}, {2040707, 1}, {2040801, 1}, {2040901, 1}, {2041001, 1}, {2041004, 1}, {2041007, 1},
        {2041010, 1}, {2041013, 1}, {2041016, 1}, {2041019, 1}, {2041022, 1}
    };
    private int node = NODE_START;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        int map_id = getMapId();
        switch (this.node) {
            case NODE_START -> {
                if (map_id == this.map_center_tower) {
                    boolean leader = isPartyBoss();
                    // 翻訳
                    say("像を直してくれただけでなく、私を罠から救い出してくれてありがとう。女神の祝福が最後まであなたとともにありますように…", false, !leader);
                    if (!leader) {
                        this.node = NODE_MEMBER;
                        return true;
                    }
                    TacosFieldSet field_set = getFieldSet(this.field_set_name);
                    if (field_set == null) {
                        return false;
                    }
                    // TODO : lua の incExpAll の第2引数 (7020) の用途は不明
                    field_set.incExpAll(this.exp_clear);
                    // 期間限定のイベント (2007年, 休み特別経験値) の処理は削除
                    // ミッション経験値 (lua の cMission_reward)
                    String mission = field_set.getVar("cMission");
                    // 翻訳
                    switch (mission) {
                        case "1" -> {
                            if (field_set.getQuestTime() < 1504) {
                                field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "<ミッション完了> 25分以内にミッションをクリアした報酬として追加経験値。");
                                field_set.incExpAll(10000);
                            }
                        }
                        case "2" -> {
                            if (8 <= getItemCount(this.item_seed)) {
                                field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "<ミッション完了> 不思議な種を8個集めた報酬として追加経験値。");
                                field_set.incExpAll(5000);
                            }
                        }
                        case "3" -> {
                            int records = 0;
                            for (int i = this.item_record_first; i <= this.item_record_last; i++) {
                                records += getItemCount(i);
                            }
                            if (6 <= records) {
                                field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "<ミッション完了> レコードを6枚集めた報酬として追加経験値。");
                                field_set.incExpAll(6000);
                            }
                        }
                        case "4" -> {
                            if (getMobCount(this.map_jail1, this.mob_ruinel) + getMobCount(this.map_jail2, this.mob_ruinel) + getMobCount(this.map_jail3, this.mob_ruinel) == 0) {
                                field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "<ミッション完了> ルイネルを倒した報酬として追加経験値。");
                                field_set.incExpAll(8000);
                            }
                        }
                        case "5" -> {
                            if (getMobCount(this.map_dark_room, this.mob_junior_cellion) + getMobCount(this.map_dark_room, this.mob_junior_lioner) + getMobCount(this.map_dark_room, this.mob_junior_grupin) == 0) {
                                field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "<ミッション完了> ジュニアセルビオン、ジュニアグルピン、ジュニアライオナーを倒した報酬として追加経験値！");
                                field_set.incExpAll(5000);
                            }
                        }
                        default -> {
                        }
                    }
                    if (enterFieldSetParty(this.field_set_bonus_name, 0) == TacosFieldSet.ENTER_SUCCESS) {
                        field_set.transferFieldAll(this.map_storage, "st00");
                    }
                    return false;
                }
                if (map_id != this.map_blessing) {
                    return false;
                }
                boolean diary = true;
                for (int i = 0; i < this.item_diary_page_count; i++) {
                    if (getItemCount(this.item_diary_page_first + i) == 0) {
                        diary = false;
                        break;
                    }
                }
                if (diary) {
                    // 翻訳
                    say("あら、これは私の日記の破れたページではありませんか？見つけてくれて本当にありがとう！これを書いて、像に閉じ込められてしまったのです…何百年も前のことです。ほとんど覚えていません。", false, true);
                    this.node = NODE_DIARY_ASK;
                    return true;
                }
                // 翻訳
                say("救い出してくれて本当にありがとう。あなたの英雄的な行いのしるしに、ささやかな贈り物があります。どうか受け取ってください。でもその前に、装備インベントリ、ETCインベントリ、消費インベントリに空きスロットがあるか確認してください。", false, true);
                this.node = NODE_REWARD;
                return true;
            }
            case NODE_MEMBER -> {
                // 翻訳
                say("それでは、パーティーリーダーの案内で進んでください。", true, false);
                return false;
            }
            case NODE_DIARY_ASK -> {
                // 翻訳
                askYesNo("日記には、私が像に閉じ込められたいきさつが書かれています。いつも言っているように、同じ過ちは二度と繰り返しません。これを記録して、あなたに渡しましょう。");
                this.node = NODE_DIARY_RESULT;
                return true;
            }
            case NODE_DIARY_RESULT -> {
                this.node = NODE_REWARD_SAY;
                if (!ok) {
                    // 翻訳
                    say("記録があれば、多くのことがわかるかもしれません…うーん…", false, true);
                    return true;
                }
                if (0 < getItemCount(this.item_diary)) {
                    // 翻訳
                    say("あなたはすでに#b#t" + this.item_diary + "##kを持っています。", false, true);
                    return true;
                }
                int[] items = new int[this.item_diary_page_count * 2 + 2];
                for (int i = 0; i < this.item_diary_page_count; i++) {
                    items[i * 2] = this.item_diary_page_first + i;
                    items[i * 2 + 1] = -1;
                }
                items[this.item_diary_page_count * 2] = this.item_diary;
                items[this.item_diary_page_count * 2 + 1] = 1;
                if (!exchange(0, items)) {
                    // 翻訳
                    say("もしかして…あの日記のページをなくしてしまったのですか？");
                    return false;
                }
                // 翻訳
                say("私の日記を大切にしてくださいね。", false, true);
                return true;
            }
            case NODE_REWARD_SAY -> {
                // 翻訳
                say("救い出してくれて本当にありがとう。あなたの英雄的な行いのしるしに、ささやかな贈り物があります。どうか受け取ってください。でもその前に、装備インベントリ、ETCインベントリ、消費インベントリに空きスロットがあるか確認してください。", false, true);
                this.node = NODE_REWARD;
                return true;
            }
            case NODE_REWARD -> {
                if (!hasEmptySlot(MapleInventoryType.USE) || !hasEmptySlot(MapleInventoryType.ETC)) {
                    // 翻訳
                    say("消費インベントリかETCインベントリに本当に空きスロットがありますか？インベントリがいっぱいだと報酬をあげられません。");
                    return false;
                }
                int rnum = random(0, 250);
                int item_id;
                int item_count;
                if (rnum < this.rewards.length) {
                    item_id = this.rewards[rnum][0];
                    item_count = this.rewards[rnum][1];
                } else if (rnum <= 130) {
                    item_id = 2000004;
                    item_count = 20;
                } else if (rnum <= 150) {
                    item_id = 2000005;
                    item_count = 10;
                } else if (rnum <= 180) {
                    item_id = 2000002;
                    item_count = 100;
                } else if (rnum <= 200) {
                    item_id = 2000006;
                    item_count = 50;
                } else {
                    item_id = 2000003;
                    item_count = 100;
                }
                if (!exchange(0, item_id, item_count)) {
                    // 翻訳
                    say("消費インベントリかETCインベントリに本当に空きスロットがありますか？インベントリがいっぱいだと報酬をあげられません。");
                    return false;
                }
                for (int i = this.item_quest_first; i <= this.item_quest_last; i++) {
                    removeItemAll(i);
                }
                removeItemAll(this.item_transparent);
                setQuestData(this.quest_clear, "1");
                // 期間限定のイベント (2007年, クエスト 9663) の処理は削除
                warp(this.map_exit);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
