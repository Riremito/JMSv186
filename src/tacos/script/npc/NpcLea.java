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

import odin.handling.world.guild.MapleGuild;
import tacos.packet.ops.OpsGuild;
import tacos.packet.response.ResCWvsContext;

/**
 *
 * @author Riremito
 */
public class NpcLea extends NpcScriptMan {

    private static final int MENU_CREATE = 0;
    private static final int MENU_DELETE = 1;
    private final int guild_master_rank = 1;
    private final int create_cost = 15000000; // ReqCUser_Guild の SET_MARK_COST と同じ
    private final int delete_cost = 1000000;
    private int selected = -1;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        // いいえを選択した
        if (status == 0 && this.asked) {
            switch (this.selected) {
                case MENU_CREATE -> {
                    // 翻訳
                    say("あら…そう…マークがあればギルドの結束も強まるのに。ギルドマークを準備する時間がもっと必要なの？いつでもまた来てね。");
                }
                case MENU_DELETE -> {
                    // 翻訳
                    say("いつでもまた来てね。");
                }
                default -> {
                }
            }
            return false;
        }
        if (getPlayer().getGuildId() <= 0 || getPlayer().getGuildRank() != this.guild_master_rank) {
            // 翻訳
            say("あら…あなたはギルドマスターじゃないわね。ギルドマークを作ったり、削除したり、変更したりできるのは#rギルドマスター#kだけよ。");
            return false;
        }
        MapleGuild guild = getPlayer().getWorld().getGuild().getGuild(getPlayer().getGuildId());
        if (guild == null) {
            return false;
        }
        boolean mark_exist = guild.getLogo() != 0 || guild.getLogoBG() != 0;
        switch (status) {
            case 0 -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こんにちは？私の名前は#bレア#k。#bギルドマーク#kを担当しているの。");
                nt.addMenu(MENU_CREATE, "ギルドマークを登録したい。");
                nt.addMenu(MENU_DELETE, "ギルドマークを削除したい。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.selected = answer;
                switch (this.selected) {
                    case MENU_CREATE -> {
                        if (mark_exist) {
                            // 翻訳
                            say("ギルドマークはもう作られているわ。作り直すなら、まず今のギルドマークを削除してね。");
                            return false;
                        }
                        this.asked = true;
                        // 翻訳
                        askYesNo("ギルドマークを作るには#r" + String.format("%,d", this.create_cost) + "メル#kが必要よ。説明すると、ギルドマークはギルドごとに唯一のデザインなの。ゲーム内でギルド名の横に表示されるのよ。\r\nそれで、ギルドマークを作る？");
                        return true;
                    }
                    case MENU_DELETE -> {
                        if (!mark_exist) {
                            // 翻訳
                            say("あれ？おかしいわね…削除するギルドマークがないわよ。");
                            return false;
                        }
                        this.asked = true;
                        // 翻訳
                        askYesNo("今のギルドマークを削除すれば、新しいマークを作れるわ。ギルドマークを削除するには#r" + String.format("%,d", this.delete_cost) + "メル#kが必要よ。削除する？");
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 2 -> {
                switch (this.selected) {
                    case MENU_CREATE -> {
                        if (mark_exist) {
                            return false;
                        }
                        if (getPlayer().getMeso() < this.create_cost) {
                            // 翻訳
                            say("メルが足りないわ。#b" + String.format("%,d", this.create_cost) + "メル#kが必要よ。");
                            return false;
                        }
                        // ギルドマークの入力 (ReqCUser_Guild で登録される)
                        getPlayer().SendPacket(ResCWvsContext.GuildResult(OpsGuild.GuildReq_InputMark));
                        return false;
                    }
                    case MENU_DELETE -> {
                        if (!mark_exist) {
                            return false;
                        }
                        if (!payMoney(this.delete_cost)) {
                            // 翻訳
                            say("ギルドマークを削除するのに必要なメルが足りないわ。ギルドマークを削除するには#b" + String.format("%,d", this.delete_cost) + "メル#kが必要よ。");
                            return false;
                        }
                        getPlayer().getWorld().getGuild().setGuildEmblem(getPlayer().getGuildId(), (short) 0, (byte) 0, (short) 0, (byte) 0);
                        return false;
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
