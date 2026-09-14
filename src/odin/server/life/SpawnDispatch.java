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
package odin.server.life;

import java.awt.Point;
import tacos.server.map.TacosMap;

/**
 * Spawnsを SpawnPoint / SpawnPointAreaBoss の2クラスへ統合(フラット化)したことに伴い、
 * 共通のスポーン操作をObject型経由でinstanceof分岐して行うためのディスパッチヘルパー。
 * 新しくスポーンポイントとして扱う型を追加した場合は、このクラスの各メソッドにも分岐を追加すること。
 *
 * @author Riremito
 */
public final class SpawnDispatch {

    private SpawnDispatch() {
    }

    public static MapleMonster getMonster(Object o) {
        if (o instanceof SpawnPoint) {
            return ((SpawnPoint) o).getMonster();
        } else if (o instanceof SpawnPointAreaBoss) {
            return ((SpawnPointAreaBoss) o).getMonster();
        }
        throw new IllegalArgumentException("getMonster: unknown spawn type: " + o);
    }

    public static byte getCarnivalTeam(Object o) {
        if (o instanceof SpawnPoint) {
            return ((SpawnPoint) o).getCarnivalTeam();
        } else if (o instanceof SpawnPointAreaBoss) {
            return ((SpawnPointAreaBoss) o).getCarnivalTeam();
        }
        throw new IllegalArgumentException("getCarnivalTeam: unknown spawn type: " + o);
    }

    public static boolean shouldSpawn(Object o) {
        if (o instanceof SpawnPoint) {
            return ((SpawnPoint) o).shouldSpawn();
        } else if (o instanceof SpawnPointAreaBoss) {
            return ((SpawnPointAreaBoss) o).shouldSpawn();
        }
        throw new IllegalArgumentException("shouldSpawn: unknown spawn type: " + o);
    }

    public static int getCarnivalId(Object o) {
        if (o instanceof SpawnPoint) {
            return ((SpawnPoint) o).getCarnivalId();
        } else if (o instanceof SpawnPointAreaBoss) {
            return ((SpawnPointAreaBoss) o).getCarnivalId();
        }
        throw new IllegalArgumentException("getCarnivalId: unknown spawn type: " + o);
    }

    public static MapleMonster spawnMonster(Object o, TacosMap map) {
        if (o instanceof SpawnPoint) {
            return ((SpawnPoint) o).spawnMonster(map);
        } else if (o instanceof SpawnPointAreaBoss) {
            return ((SpawnPointAreaBoss) o).spawnMonster(map);
        }
        throw new IllegalArgumentException("spawnMonster: unknown spawn type: " + o);
    }

    public static int getMobTime(Object o) {
        if (o instanceof SpawnPoint) {
            return ((SpawnPoint) o).getMobTime();
        } else if (o instanceof SpawnPointAreaBoss) {
            return ((SpawnPointAreaBoss) o).getMobTime();
        }
        throw new IllegalArgumentException("getMobTime: unknown spawn type: " + o);
    }

    public static Point getPosition(Object o) {
        if (o instanceof SpawnPoint) {
            return ((SpawnPoint) o).getPosition();
        } else if (o instanceof SpawnPointAreaBoss) {
            return ((SpawnPointAreaBoss) o).getPosition();
        }
        throw new IllegalArgumentException("getPosition: unknown spawn type: " + o);
    }
}
