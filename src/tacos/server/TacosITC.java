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
package tacos.server;

import odin.server.MTSStorage;
import tacos.network.PacketHandler_ITC;
import tacos.property.Property_ITC;

/**
 *
 * @author Riremito
 */
public class TacosITC extends TacosServer {

    public static final int m_nRegisterFeeMeso = 5000;
    public static final int m_nCommissionRate = 10;
    public static final int m_nCommissionBase = 0;
    public static final int m_nAuctionDurationMin = 24;
    public static final int m_nAuctionDurationMax = 168;
    public static final int MIN_MTS = 110;

    private int world_id;
    private OnlinePlayers onlines;

    public TacosITC(String server_name) {
        super(server_name);
        setType(TacosServerType.ITC_SERVER);

        this.onlines = new OnlinePlayers();
    }

    @Override
    public void shutdown() {
        this.onlines.disconnectAll();
        MTSStorage.getInstance().saveBuyNow(true);
        super.shutdown();
    }

    public TacosWorld getWorld() {
        return TacosWorld.find(this.world_id);
    }

    public OnlinePlayers getOnlinePlayers() {
        return this.onlines;
    }

    public static boolean init() {
        TacosITC server_itc = new TacosITC("ITC");
        TacosServer.add(server_itc);
        server_itc.run(Property_ITC.getIP(), Property_ITC.getPort(), new PacketHandler_ITC(server_itc));
        server_itc.world_id = 0;
        TacosWorld.find(server_itc.world_id).setITC(server_itc);
        return true;
    }
}
