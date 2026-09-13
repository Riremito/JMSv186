/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.handling.channel;

import java.util.List;
import java.util.LinkedList;
import tacos.database.query.DQ_Guilds;

public class MapleGuildRanking {

    private static MapleGuildRanking instance = new MapleGuildRanking();
    private List<GuildRankingInfo> ranks = new LinkedList<>();

    public static MapleGuildRanking getInstance() {
        return instance;
    }

    public List<GuildRankingInfo> getRank() {
        if (ranks.isEmpty()) {
            reload();
        }
        return ranks;
    }

    private void reload() {
        ranks.clear();
        ranks.addAll(DQ_Guilds.getTopByGP(50));
    }

    public static class GuildRankingInfo {

        private String name;
        private int gp;
        private int logo;
        private int logocolor;
        private int logobg;
        private int logobgcolor;

        public GuildRankingInfo(String name, int gp, int logo, int logocolor, int logobg, int logobgcolor) {
            this.name = name;
            this.gp = gp;
            this.logo = logo;
            this.logocolor = logocolor;
            this.logobg = logobg;
            this.logobgcolor = logobgcolor;
        }

        public String getName() {
            return name;
        }

        public int getGP() {
            return gp;
        }

        public int getLogo() {
            return logo;
        }

        public int getLogoColor() {
            return logocolor;
        }

        public int getLogoBg() {
            return logobg;
        }

        public int getLogoBgColor() {
            return logobgcolor;
        }
    }
}
