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
// Npc.wz script : PartyAmoria_enter (BMS8-24, EMS70-76, GMS92-95), 会話文は lua_script_jp (PartyAmoria_enter) を利用
// 結婚の確認は削除 (結婚システムが未実装のため)
public class Npc_Amoria_AmosEnter extends NpcScriptMan {

    private static final int MODE_TICKET = 0;
    private static final int MODE_KEY = 1;
    private static final int MODE_START = 2;
    private final int map_training = 670010000; // Hidden Street : Amos' Training Ground
    private final int map_entrance = 670010100; // Hidden Street : Entrance of Amorian Challenge
    private final int item_ticket = 4031592; // Entrance Ticket
    private final int item_key = 4031593; // Lip Lock Key
    private final int quest_key = 8883; // APQ : アモスの鍵
    private final int quest_time = 8884; // クエスト時間設定
    private final int key_count = 10;
    private final int level_min = 40;
    private int mode = MODE_START;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        if (getMapId() != this.map_training) {
            return false;
        }
        if (status == 0) {
            String val = getQuestData(this.quest_key);
            if (val.equals("end")) {
                this.mode = MODE_TICKET;
                // 翻訳
                say("チケットを持っているか？よし、それではアモリアパーティークエストの入口へ連れて行こう。パーティーの仲間たちがそこで待っているはずだ！", false, true);
                return true;
            }
            if (val.equals("ing")) {
                if (getPlayer().getLevel() < this.level_min) {
                    // 翻訳
                    say("君には立派な闘志が見えるぞ、友よ。残念ながら、まだ十分には育っていないがな。俺の狩り場に入るには、少なくともレベル40が必要だ！");
                    return false;
                }
                this.mode = MODE_KEY;
                // 翻訳
                say("入る資格があることを証明するために、リップロックキーを10個集めてほしい。インディゴアイを狩ってみるといい。あいつらはあれが好きみたいだからな。そうしたら、君の実力を見せてもらうために中に入れてやろう！", false, true);
                return true;
            }
            this.mode = MODE_START;
            // 翻訳
            askYesNo("俺は力持ちアモス！頼れる剣と知恵だけでバルログを倒した戦士だ！君たちのグループにその気があるなら、挑戦を用意しているぞ！どうだ？");
            return true;
        }
        switch (this.mode) {
            case MODE_TICKET -> {
                if (status != 1) {
                    return false;
                }
                setQuestData(this.quest_key, "");
                if (getItemCount(this.item_ticket) < 1) {
                    // 翻訳
                    say("入場チケットを持っていないのか？なんてこった。すまないが、もう一度リップロックキーを10個集めて俺に渡してくれ。そうすれば、もう一度チケットをやろう。");
                    return false;
                }
                if (!exchange(0, this.item_ticket, -1)) {
                    setQuestData(this.quest_key, "end");
                    // 翻訳
                    say("すまないが、インベントリにアイテムを入れるのに問題が起きているようだ。後でまた来てくれ。");
                    return false;
                }
                warp(this.map_entrance, "st00");
                return false;
            }
            case MODE_KEY -> {
                if (status != 1) {
                    return false;
                }
                if (getItemCount(this.item_key) < this.key_count) {
                    // 翻訳
                    say("どれどれ、1、2、3…10個ないな。兄は賢い方かもしれないが、俺だって抜けているわけじゃないぞ。アモリアの挑戦の入場チケットを渡すには、10個必要だ。");
                    return false;
                }
                if (!exchange(0, this.item_key, -this.key_count, this.item_ticket, 1)) {
                    // 翻訳
                    say("すまないが、インベントリにアイテムを入れるのに問題が起きているようだ。後でまた来てくれ。");
                    return false;
                }
                setQuestData(this.quest_key, "end");
                setQuestData(this.quest_time, currentTime());
                // 翻訳
                say("おお！立派な戦士とそのパーティーだ！チケットをどうぞ。幸運を祈る！");
                return false;
            }
            case MODE_START -> {
                if (!ok) {
                    if (status == -1) {
                        // 翻訳
                        say("無理もないさ、友よ。十分強くなったら戻ってきてくれ、待っているからな。");
                    }
                    return false;
                }
                if (status == 1) {
                    // 6時間以内に参加した場合は参加できない
                    boolean wait = compareTime(currentTime(), getQuestData(this.quest_time)) < 360;
                    if (!wait) {
                        setQuestData(this.quest_key, "ing");
                    }
                    // 翻訳
                    say("素晴らしい！警告しておくが、俺の挑戦は弱い武器とちっぽけな頭の持ち主向けじゃない！この狩り場は、愛する人を守る者たちの証として作ったんだ。そのためには強くなければならない！君たちを試させてもらおう！もう一度話しかけてくれ。", false, wait);
                    return wait;
                }
                if (status == 2) {
                    // 翻訳
                    say("おや、以前にパーティークエストに参加したことがあるようだな。何？もう一枚チケットがほしいって？おいおい、落ち着け。6時間後にまた来てくれ。", true, false);
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
