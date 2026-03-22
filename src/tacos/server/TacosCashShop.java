/*
 * Copyright (C) 2025 Riremito
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

import tacos.property.Property_Shop;
import tacos.constants.TacosConstants;
import tacos.network.PacketHandler_CashShop;

/**
 *
 * @author Riremito
 */
public class TacosCashShop extends TacosServer {

    private int world_id;
    private OnlinePlayers onlines;

    public TacosCashShop(String server_name) {
        super(server_name);
        setType(TacosServerType.CASHSHOP_SERVER);
        this.onlines = new OnlinePlayers();
    }

    @Override
    public void shutdown() {
        this.onlines.disconnectAll();
        super.shutdown();
    }

    public TacosWorld getWorld() {
        return TacosWorld.find(this.world_id);
    }

    public OnlinePlayers getOnlinePlayers() {
        return this.onlines;
    }

    public static boolean init() {
        TacosCashShop server = new TacosCashShop("CashShop");
        TacosServer.add(server);
        server.run(TacosConstants.SERVER_GLOBAL_IP, Property_Shop.getPort(), new PacketHandler_CashShop(server));
        server.world_id = 0;
        TacosWorld.find(server.world_id).setCashShop(server);
        return true;
    }

}
