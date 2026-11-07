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
public class Npc_Common_Matilda extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        // TODO : ネットカフェ用の分岐, 武器の貸与
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#bネットカフェご利用のあなたへのサービス！#k");
                nt.add("現在ご使用中の武器に不満はありませんか？攻撃力が足りないと思っていませんか？#bネクソン公認ネットカフェ#kでのみ楽しめる武器貸与サービスを開始しました！");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("普通の武器であるとがっかりするにはまだ早いです！装備制限がぐっと下がっているのでワンランク上の強さが体験できます！#bネットカフェ#kでのみ楽しめる驚きの特典です！ ");
                nt.add("#r但し、ログアウトと同時に貸与した武器は自動に消えますのでご参考ください。#k");
                say(nt, true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
