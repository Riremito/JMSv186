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
package tacos.packet.response;

import tacos.packet.response.builder.PB_Messenger;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsMessenger;
import tacos.packet.response.data.RD_AvatarLook;

/**
 *
 * @author Riremito
 */
public class ResCUIMessenger {

    // CUIMessenger::OnPacket
    public static ServerPacket Messenger(OpsMessenger ops, PB_Messenger pd) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Messenger);

        sp.Encode1(ops.get());

        switch (ops) {
            case MSMP_Enter: {
                sp.Encode1(pd.player_index); // nIdx
                sp.EncodeBuffer(RD_AvatarLook.Encode(pd.player));
                sp.EncodeStr(pd.player.getName()); //sID
                sp.Encode1(pd.player.getChannelId() - 1); // nChannelID
                sp.Encode1(pd.is_new ? 1 : 0); // bNew
                break;
            }
            case MSMP_SelfEnterResult: {
                sp.Encode1(pd.player_index); // nIdx
                break;
            }
            case MSMP_Leave: {
                sp.Encode1(pd.player_index); // nIdx
                break;
            }
            case MSMP_Invite: {
                sp.EncodeStr(pd.inviter_name); // sCharacterName
                sp.Encode1(pd.inviter_channel_id); // m_nChannelID
                sp.Encode4(pd.messenger_id); // m_dwSN
                sp.Encode1(0);
                break;
            }
            case MSMP_InviteResult: {
                sp.EncodeStr(pd.invitee_name); // text
                sp.Encode1(pd.is_found ? 1 : 0); // found or not.
                break;
            }
            case MSMP_Blocked: {
                sp.EncodeStr(pd.invitee_name); // text
                sp.Encode1(pd.is_auto_blocked ? 1 : 0); // auto block or manual block.
                break;
            }
            case MSMP_Chat: {
                sp.EncodeStr(pd.message); // text
                break;
            }
            case MSMP_Avatar: {
                sp.Encode1(pd.player_index); // nIdx
                sp.EncodeBuffer(RD_AvatarLook.Encode(pd.player));
                break;
            }
            case MSMP_Migrated: {
                for (int i = 0; i < 3; i++) {
                    sp.Encode1(0); // 0 = clear, 1 = do nothing, others = enter player
                }
                break;
            }
            default: {
                break;
            }
        }

        return sp;
    }
}
