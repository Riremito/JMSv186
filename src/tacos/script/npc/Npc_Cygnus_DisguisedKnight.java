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

import java.awt.Point;

/**
 *
 * @author Riremito
 */
// Npc.wz script : blackWitch, desguiseSoul, desguiseFlame, desguiseWind, desguiseNight, desguiseStrike (JMS165-302), js の処理を利用
// 話しかけると NPC が消えて正体 (モンスター) を現す
public class Npc_Cygnus_DisguisedKnight extends NpcScriptMan {

    private final int npc_eleanor = 1104002; // エレオノール (blackWitch)
    private final int npc_fake_mihile = 1104100; // ミハエル(偽者) (desguiseSoul)
    private final int mob_black_witch = 9001010; // 暗黒の魔女
    private final int mob_transformer = 9001009; // 変化師
    private final Point fake_mihile_position = new Point(263, 88); // 108010620, 隠れ道 : ティムの森

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("おや…見つかってしまったか？ならば道はひとつ！#r黒い翼#kらしく戦ってやろう！", false, true);
                return true;
            }
            case 1 -> {
                int mob_id = (getNpcId() == this.npc_eleanor) ? this.mob_black_witch : this.mob_transformer;
                // js では ミハエル(偽者) のみ固定の位置, それ以外はキャラクターの位置に召喚する
                Point pos = (getNpcId() == this.npc_fake_mihile) ? this.fake_mihile_position : getPlayer().getPosition();
                removeNpc();
                summonMob(pos.x, pos.y, mob_id);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
