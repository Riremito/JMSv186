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
package packet.ops;

import config.Region;
import config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsMiniRoomProtocol implements IPacketOps {
    MRP_Create(0),
    MRP_CreateResult(1),
    MRP_Invite(2),
    MRP_InviteResult(3),
    MRP_Enter(4),
    MRP_EnterResult(5),
    MRP_Chat(6),
    MRP_GameMessage(7),
    MRP_UserChat(8),
    MRP_Avatar(9),
    MRP_Leave(10),
    MRP_Balloon(11),
    MRP_NotAvailableField(12),
    MRP_FreeMarketClip(13),
    MRP_CheckSSN2,
    TRP_PutItem,
    TRP_PutMoney,
    TRP_Trade,
    TRP_UnTrade,
    TRP_MoveItemToInventory,
    TRP_ItemCRC,
    TRP_LimitFail,
    PSP_PutItem,
    PSP_BuyItem,
    PSP_BuyResult,
    PSP_Refresh,
    PSP_AddSoldItem,
    PSP_MoveItemToInventory,
    PSP_Ban,
    PSP_KickedTimeOver,
    PSP_DeliverBlackList,
    PSP_AddBlackList,
    PSP_DeleteBlackList,
    ESP_PutItem,
    ESP_BuyItem,
    ESP_BuyResult,
    ESP_Refresh,
    ESP_AddSoldItem,
    ESP_MoveItemToInventory,
    ESP_GoOut,
    ESP_ArrangeItem,
    ESP_WithdrawAll,
    ESP_WithdrawAllResult,
    ESP_WithdrawMoney,
    ESP_WithdrawMoneyResult,
    ESP_AdminChangeTitle,
    ESP_DeliverVisitList,
    ESP_DeliverBlackList,
    ESP_AddBlackList,
    ESP_DeleteBlackList,
    MGRP_TieRequest,
    MGRP_TieResult,
    MGRP_GiveUpRequest,
    MGRP_GiveUpResult,
    MGRP_RetreatRequest,
    MGRP_RetreatResult,
    MGRP_LeaveEngage,
    MGRP_LeaveEngageCancel,
    MGRP_Ready,
    MGRP_CancelReady,
    MGRP_Ban,
    MGRP_Start,
    MGRP_GameResult,
    MGRP_TimeOver,
    ORP_PutStoneChecker,
    ORP_InvalidStonePosition,
    ORP_InvalidStonePosition_Normal,
    ORP_InvalidStonePosition_By33,
    MGP_TurnUpCard,
    MGP_MatchCard,
    UNKNOWN(-1);

    private int value;

    OpsMiniRoomProtocol(int val) {
        this.value = val;
    }

    OpsMiniRoomProtocol() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static OpsMiniRoomProtocol find(int val) {
        for (final OpsMiniRoomProtocol ops : OpsMiniRoomProtocol.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.LessOrEqual(Region.JMS, 147)) {
            ESP_PutItem.set(29); // アイテム追加
            ESP_BuyItem.set(30);
            ESP_BuyResult.set(31);
            ESP_Refresh.set(32);
            ESP_AddSoldItem.set(33);
            ESP_MoveItemToInventory.set(34); // アイテム回収
            ESP_GoOut.set(35); // 商店から出る -> MRP_Leave
            ESP_ArrangeItem.set(36); // アイテム整理
            ESP_WithdrawAll.set(37); // 商店のクローズ
            ESP_WithdrawAllResult.set(38);
            ESP_WithdrawMoney.set(39); // メル回収
            ESP_WithdrawMoneyResult.set(40);
            ESP_AdminChangeTitle.set(-1);
            ESP_DeliverVisitList.set(-1);
            ESP_DeliverBlackList.set(-1);
            ESP_AddBlackList.set(-1);
            ESP_DeleteBlackList.set(-1);
            return;
        }
    }

}
