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
package tacos.packet.response.builder;

import java.util.List;
import lombok.Builder;
import odin.client.inventory.IItem;
import odin.server.MTSCart;
import odin.server.MTSStorage;
import tacos.packet.ops.OpsITC;

/**
 *
 * @author Riremito
 */
@Builder
public class PB_ITC {

    public OpsITC fail_reason;
    public IItem item;
    public List<IItem> items;
    public boolean unlock;
    public List<MTSStorage.MTSItemInfo> mts_items;
    public MTSCart mts_cart;
}
