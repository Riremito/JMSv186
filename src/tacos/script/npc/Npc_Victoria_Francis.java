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
public class Npc_Victoria_Francis extends NpcScriptMan {

    private final int quest_francis = 21760; // クエスト名なし
    private final int map_evil_eye_nest = 105070300; // 中央ダンジョン : エビルアイの巣３

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (適当)
                say("私は黒い翼の人形師フランシス。よくも私の人形たちの邪魔をしてくれたね…。本当に腹立たしいけど、今回だけは見逃してあげるよ。でも、次にまた同じことをしたら、黒魔法師様の名にかけて、必ず償わせてやる。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (適当)
                sayUser("#b(黒い翼？何だそれは？それに黒魔法師とどう関係があるんだろう？この情報はトゥルーに報告した方がよさそうだ。)#k", true, true);
                return true;
            }
            case 2 -> {
                setQuestData(this.quest_francis, "0");
                // TODO : 移動先のポータル (jsでは3番)
                warp(this.map_evil_eye_nest);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
