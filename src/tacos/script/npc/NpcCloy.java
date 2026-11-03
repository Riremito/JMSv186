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
public class NpcCloy extends NpcScriptMan {

    private static final int MENU_ABOUT = 0;
    private static final int MENU_RAISE = 1;
    private static final int MENU_DIE = 2;
    private static final int MENU_ACTION_PET = 3;
    private static final int MENU_TRANSFER = 4;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("君…もしや私の子供を育てているのではないか？私は生命の水と魔法を使って、人形に命を吹き込むことに成功した。人々はそのように命を得た私の子供達を#bペット#kと呼んでいるようだね。もしペットを持っているなら何でも私に聞いておくれ");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("何が知りたいのかね？");
                nt.addMenu(MENU_ABOUT, "ペットって何？");
                nt.addMenu(MENU_RAISE, "ペットはどうやって育てるの？");
                nt.addMenu(MENU_DIE, "ペットも死ぬの？");
                nt.addMenu(MENU_ACTION_PET, "アクションペットについて知りたい！");
                nt.addMenu(MENU_TRANSFER, "ペットの能力値を移動させる方法を知りたい！");
                askMenu(nt);
                return true;
            }
            case 2 -> {
                // TODO : 各項目の説明 (文章が不明)
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
