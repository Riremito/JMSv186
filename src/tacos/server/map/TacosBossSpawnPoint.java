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
package tacos.server.map;

import java.awt.Point;
import java.util.ArrayList;
import lombok.Data;
import odin.server.Randomizer;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleMap;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsMobAppear;
import tacos.server.TacosWorld;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
@Data
public class TacosBossSpawnPoint {

    private int id;
    private int mobTime;
    private String message = "";
    private MapleMonster monster = null;
    private long lastRegenTime = 0;
    private ArrayList<Integer> sharedMapIds = new ArrayList<>();

    private Point getRandomXY(MapleMap map) {
        int x = map.wall.getLeft() + Randomizer.nextInt(map.screen.getWidth());
        int y = map.wall.getTop() + Randomizer.nextInt(map.screen.getHeight());

        return new Point(x, y);
    }

    private int findFootholdId(MapleMap map, Point pos) {
        TacosFoothold foothold = map.findBelow(pos);

        return (foothold != null) ? foothold.getId() : 0;
    }

    public MapleMonster regen(MapleMap map) {
        if (this.monster != null) {
            return null;
        }
        this.monster = MapleLifeFactory.getMonster(this.id);

        int sp_count = map.getMonsterSpawnPoint().size();
        if (sp_count != 0) {
            TacosSpawnPoint target_sp = map.getMonsterSpawnPoint().get(Randomizer.nextInt(sp_count));
            this.monster.setPosition(target_sp.getX(), target_sp.getY());
            this.monster.setFootholdId(target_sp.getFh());
            this.monster.setHomeFoothold(target_sp.getFh());
        } else {
            Point pos = getRandomXY(map);
            int foothold_id = findFootholdId(map, pos);
            if (foothold_id == 0) {
                pos.setLocation(0, 0);
                foothold_id = findFootholdId(map, pos);
            }
            this.monster.setPosition(pos);
            this.monster.setFootholdId(foothold_id);
            this.monster.setHomeFoothold(foothold_id);
        }

        this.monster.setObjectId();
        this.monster.setMap(map); // TODO : remove from monster object.
        this.monster.setAT(OpsMobAppear.MOBAPPEAR_REGEN);
        this.monster.setATEx(OpsMobAppear.MOBAPPEAR_REGEN.get());
        this.lastRegenTime = System.currentTimeMillis();
        updateSharedMap(map, this.lastRegenTime);
        return this.monster;
    }

    public void removeMonster(TacosMap map) {
        this.monster = null;
        this.lastRegenTime = System.currentTimeMillis();
        updateSharedMap(map, this.lastRegenTime);
    }

    private void updateSharedMap(TacosMap map, long current_time) {
        for (int shared_map_id : this.sharedMapIds) {
            if (map.getId() != shared_map_id) {
                MapleMap shared_map = TacosWorld.find(0).getChannelServer(map.getChannel()).findMap(shared_map_id);
                if (shared_map != null && !shared_map.getBossSpawnPoint().isEmpty()) {
                    TacosBossSpawnPoint shared_bsp = shared_map.getBossSpawnPoint().get(0);
                    if (shared_bsp != null) {
                        shared_bsp.lastRegenTime = current_time;
                    }
                }
            }
        }
    }

    // master monster data.
    public static TacosBossSpawnPoint getMasterMonster(MapleMap map) {
        TacosBossSpawnPoint bsp = new TacosBossSpawnPoint();

        switch (map.getId()) {
            case 104000400 -> {
                bsp.id = 2220000;
                bsp.mobTime = 2700 * 1000;
                bsp.message = "涼しい気運が濃く立ち込めながらマノが現れました。";
            }
            case 101030404 -> {
                bsp.id = 3220000;
                bsp.mobTime = 2700 * 1000;
                bsp.message = "岩山を響く足音とともにスタンピが現れました。";
            }
            case 260010201 -> {
                bsp.id = 3220001;
                bsp.mobTime = 3600 * 1000;
                bsp.message = "デウが現れました。";
            }
            case 230020100 -> {
                bsp.id = 4220001;
                bsp.mobTime = 2700 * 1000;
                bsp.message = "セルフが現れました。";
            }
            case 110040000 -> {
                bsp.id = 5220001;
                bsp.mobTime = 1200 * 1000;
                bsp.message = "砂浜に怪しいキンクランが現れました。";
            }
            case 100040105, 100040106 -> {
                bsp.id = 5220002;
                bsp.mobTime = 1800 * 1000;
                bsp.message = "パウストが出ました。";
                bsp.sharedMapIds.add(100040105);
                bsp.sharedMapIds.add(100040106);
            }
            case 220050000, 220050100, 220050200 -> {
                bsp.id = 5220003;
                bsp.mobTime = 1500 * 1000;
                bsp.message = "タイマーが出ました。";
                bsp.sharedMapIds.add(220050000);
                bsp.sharedMapIds.add(220050100);
                bsp.sharedMapIds.add(220050200);
            }
            case 107000300, 107000400 -> {
                bsp.id = 6220000;
                bsp.mobTime = 1800 * 1000;
                bsp.message = "ダイルが出ました。";
                bsp.sharedMapIds.add(107000300);
                bsp.sharedMapIds.add(107000400);
            }
            case 221040301 -> {
                bsp.id = 6220001;
                bsp.mobTime = 2400 * 1000;
                bsp.message = "ジェノが現れました。";
            }
            case 250010304 -> {
                bsp.id = 7220000;
                bsp.mobTime = 2100 * 1000;
                bsp.message = "低音の口笛音とともにタイルンが現れました。";
            }
            case 222010310 -> {
                bsp.id = 7220001;
                bsp.mobTime = 2700 * 1000;
                bsp.message = "月の光が薄くなり、長い狐の鳴き声とともにおキツネ様の気運が感じられます。";
            }
            case 250010503, 250010504 -> {
                bsp.id = 7220002;
                bsp.mobTime = 1800 * 1000;
                bsp.message = "気持ち悪い猫の鳴き声が聞えます。";
                bsp.sharedMapIds.add(250010503);
                bsp.sharedMapIds.add(250010504);
            }
            case 200010300 -> {
                bsp.id = 8220000;
                bsp.mobTime = 1200 * 1000;
                bsp.message = "黒い旋風を巻き起こしながらエリジャーが現れました。";
            }
            case 261030000 -> {
                bsp.id = 8220002;
                bsp.mobTime = 2700 * 1000;
                bsp.message = "キメラが現れました。";
            }
            case 240040401 -> {
                bsp.id = 8220003;
                bsp.mobTime = 7200 * 1000;
                bsp.message = "レヴィアタンが現れました。";
            }
            default -> {
                return null;
            }
        }

        if (!WzDataStorage.MOB.check(bsp.id)) {
            DebugLogger.ErrorLog("getMasterMonster : invalid mob id, " + bsp.id);
            return null;
        }

        return bsp;
    }
}
