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
public class Npc_Orbis_Lenario extends NpcScriptMan {

    private static final int MENU_ABOUT = 0;
    private static final int MENU_HOW_TO = 1;
    private static final int MENU_CREATE = 2;
    private static final int MENU_INCREASE = 3;
    private static final int MENU_DISBAND = 4;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こんにちは！#bレナリウ#kといいます。");
                nt.addMenu(MENU_ABOUT, "ギルド連合が何か教えてください。");
                nt.addMenu(MENU_HOW_TO, "ギルド連合を結成しようとしたらどうしたらいいでしょうか？");
                nt.addMenu(MENU_CREATE, "ギルド連合を結成したいです。");
                nt.addMenu(MENU_INCREASE, "ギルド連合のギルド数を増やしたいです。");
                nt.addMenu(MENU_DISBAND, "ギルド連合を解体したいです。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_ABOUT -> {
                        say("いくつかのギルド同士が集まって生み出した集まりをギルド連合といいます。私はこう結成されたギルド連合を管理する仕事を担当しています。");
                    }
                    case MENU_HOW_TO -> {
                        // TODO : 続きの文章
                        // 2名のギルドマスターが集まったら500万メルが必要です。これはギルド連合に登録するのに必要な手数料です。
                        // そしてもう一つ！当然、他のギルド連合に加入されている場合は新たにギルド連合を結成することができませんよ！
                        say("ギルド連合を作るにはギルドマスター2名がグループを組まなければなりません。ここでグループリーダーがギルド連合リーダーになります。");
                    }
                    case MENU_CREATE -> {
                        // TODO : ギルド連合の結成
                        // グループリーダーかつグループ員が2名でない場合 : グループマスターのみギルド連合を登録することができます。 (グループ員が2名の場合のみ、ギルド連合を登録することができます。)
                        // ギルドマスターでない場合 : You cannot form a Guild Union until you own a guild
                        // グループ員がギルドマスターでない場合 : Your party member does not seem to own a guild.
                        // 既にギルド連合に加入している場合 : You cannot form a Guild Union if you are already affiliated with a different Union.
                        // グループ員が既にギルド連合に加入している場合 : Your party member is already affiliated with a guild union.
                        // グループ員が同じマップにいない場合 : Get your other party member on the same map please.
                        // 確認 : Oh, are you interested in forming a Guild Union?
                        // 名前の入力 : Now please enter the name of your new Guild Union. (max. 12 letters)
                        // 名前の確認 : Will #b" + guildName + "#k be the name of your Guild Union?
                        // 名前が使用できない場合 : This name is unavailable, please choose another one
                        // 結成 : You have successfully formed a Guild Union.
                    }
                    case MENU_INCREASE -> {
                        // TODO : ギルド連合のギルド数の増加
                        // ギルド連合長でない場合 : ギルド連合長のみギルド数を増やすことができます。
                        // 確認 : To increase the capacity, you will need to pay 10,000,000 mesos. Are you sure you wish to proceed?
                        // 成功 : You have added capacity to your alliance.
                        // 失敗 : Your guild union has too much capacity already. 5 is the maximum.
                    }
                    case MENU_DISBAND -> {
                        // TODO : ギルド連合の解体
                        // ギルド連合長でない場合 : ギルド連合長のみギルド連合を解体することができます。
                        // 確認 : Are you sure you want to disband your Guild Union?
                        // 成功 : Your Guild Union has been disbanded
                        // 失敗 : An error occured when disbanding the Guild Union
                    }
                    default -> {
                    }
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
