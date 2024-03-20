/*
 * Copyright (C) 2024 Riremito
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
package packet.client.request;

import client.MapleClient;
import packet.client.ClientPacket;

/**
 *
 * @author Riremito
 */
public class PetRequest {

    // CUserPool::OnUserCommonPacket
    public static boolean OnPetPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {

        // between CP_BEGIN_PET and CP_END_PET
        switch (header) {
            case CP_PetMove: {

                return true;
            }
            case CP_PetAction: {

                return true;
            }
            case CP_PetInteractionRequest: {

                return true;
            }
            case CP_PetDropPickUpRequest: {

                return true;
            }
            case CP_PetStatChangeItemUseRequest: {

                return true;
            }
            case CP_PetUpdateExceptionListRequest: {

                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }
}
