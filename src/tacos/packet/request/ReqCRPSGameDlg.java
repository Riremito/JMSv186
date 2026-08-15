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
package tacos.packet.request;

import java.util.Random;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;
import tacos.packet.ClientPacket;
import odin.server.maps.MapleMap;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsRPS;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCRPSGameDlg;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.builder.PB_UserEffect;

/**
 *
 * @author Riremito
 */
public class ReqCRPSGameDlg {

    public static boolean OnPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return true;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        switch (header) {
            case CP_RPSGame: {
                OnRPSGame(chr, cp);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnRPSGame(MapleCharacter chr, ClientPacket cp) {
        MapleClient client = chr.getClient();
        int tax = 1000;
        int refund = 500;

        byte nType = cp.Decode1();

        switch (OpsRPS.find(nType)) {
            case RPSReq_StartGame: {
                chr.setCntStraightVictories(0);

                if (chr.getMeso() < tax) {
                    chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_NotEnoughMoney));
                    return true;
                }
                if (chr.getInventory(MapleInventoryType.ETC).isFull()) {
                    chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_NoEmptySlotForReward));
                    return true;
                }

                chr.gainMeso(-tax, true, true, true);
                chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_StartGame));
                return true;
            }
            case RPSReq_UserSelection: {
                Random rand = new Random();
                boolean is_refund = false;

                byte nRPS = cp.Decode1();
                byte m_nNpcSelect = (byte) rand.nextInt(3);
                // Rock(0), Paper(1), Scissors(2)
                if (nRPS == m_nNpcSelect) {
                    // draw.
                }
                if ((nRPS == 0 && m_nNpcSelect == 1) || (nRPS == 1 && m_nNpcSelect == 2) || (nRPS == 2 && m_nNpcSelect == 0)) {
                    // lose.
                    if (chr.getCntStraightVictories() == 0) {
                        is_refund = true;
                    }
                    chr.setCntStraightVictories(-1);
                } else {
                    // win.   
                    chr.setCntStraightVictories(chr.getCntStraightVictories() + 1);
                }

                chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_NpcSelection, m_nNpcSelect, chr.getCntStraightVictories()));

                if (is_refund) {
                    chr.gainMeso(refund, true, true, true);
                }
                if (10 <= chr.getCntStraightVictories()) {
                    int item_id = 4031341;
                    MapleInventoryManipulator.addById(client, item_id, (short) 1, "", null, 0);

                    PB_UserEffect pb = PB_UserEffect.builder()
                            .item_id(item_id)
                            .item_quantity(1)
                            .build();
                    chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Quest, pb));
                }
                // reward 4031332
                return true;
            }
            case RPSReq_TimeOver: {
                chr.setCntStraightVictories(-1);
                chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_TimeOver));
                return true;
            }
            case RPSReq_Continue: {
                chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_Coninue));
                return true;
            }
            case RPSReq_Quit: {
                chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_Quit));
                if (1 <= chr.getCntStraightVictories()) {
                    int item_id = 4031332 + (chr.getCntStraightVictories() - 1);
                    MapleInventoryManipulator.addById(client, item_id, (short) 1, "", null, 0);

                    PB_UserEffect pb = PB_UserEffect.builder()
                            .item_id(item_id)
                            .item_quantity(1)
                            .build();
                    chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Quest, pb));
                    chr.setCntStraightVictories(0);
                }
                return true;
            }
            case RPSReq_Retry: {
                if (chr.getMeso() < tax) {
                    chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_NotEnoughMoney));
                    return true;
                }
                if (chr.getInventory(MapleInventoryType.ETC).isFull()) {
                    chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_NoEmptySlotForReward));
                    return true;
                }

                chr.SendPacket(ResCRPSGameDlg.RPSGame(OpsRPS.RPSRes_Retry));
                return true;
            }
        }

        return false;
    }
}
