package packet;

// 冒険家のみの最終バージョン
public class v164_0_SP {

    public static void Set() {
        // ===== Login Server 1 =====
        ServerPacket.Header.LP_CheckPasswordResult.Set(0x0000);
        ServerPacket.Header.LP_WorldInformation.Set(0x0002);
        ServerPacket.Header.LP_SelectWorldResult.Set(0x0003);
        ServerPacket.Header.LP_SelectCharacterResult.Set(0x0004);
        ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0006);
        ServerPacket.Header.LP_DeleteCharacterResult.Set(0x0007);
        // ===== Change Channel =====
        ServerPacket.Header.LP_MigrateCommand.Set(0x0008);
        // ===== Login Server 2 =====
        ServerPacket.Header.HELLO.Set(0x000E);
        ServerPacket.Header.LOGIN_AUTH.Set(0x0018);
        // ===== Game Server 1 =====
        ServerPacket.Header.LP_InventoryOperation.Set(0x0016);
        ServerPacket.Header.LP_InventoryGrow.Set(ServerPacket.Header.LP_InventoryOperation.Get() + 0x01);
        ServerPacket.Header.LP_StatChanged.Set(ServerPacket.Header.LP_InventoryOperation.Get() + 0x02);
        ServerPacket.Header.LP_BroadcastMsg.Set(0x0037);
        // ===== Cash Shop =====
        // 0x0066 + 1
        ServerPacket.Header.LP_SetField.Set(0x0067);
        ServerPacket.Header.LP_SetITC.Set(ServerPacket.Header.LP_SetField.Get() + 0x01); // 0x0068
        ServerPacket.Header.LP_SetCashShop.Set(ServerPacket.Header.LP_SetField.Get() + 0x02); // 0x0069
        // ===== Game Server 2 =====
        //Header.SERVER_BLOCKED.Set(0x0085);
        //Header.SHOW_EQUIP_EFFECT.Set(0x0086);
        ServerPacket.Header.LP_UserChat.Set(0x0083);
        // ===== Game Server 3 =====
        // 00A3 -> 0083
        //Header.SPAWN_PLAYER.Set(0x00A1);
        //Header.REMOVE_PLAYER_FROM_MAP.Set(0x00A2);
        ServerPacket.Header.LP_UserItemUpgradeEffect.Set(0x0087); // 00A8 -> 0087

        // ===== Mob =====
        ServerPacket.Header.LP_MobEnterField.Set(0x00C2);
        ServerPacket.Header.LP_MobLeaveField.Set(ServerPacket.Header.LP_MobEnterField.Get() + 0x01);
        ServerPacket.Header.LP_MobChangeController.Set(ServerPacket.Header.LP_MobEnterField.Get() + 0x02);
        // ===== Mob Movement ====
        ServerPacket.Header.LP_MobMove.Set(0x00C5);
        ServerPacket.Header.LP_MobCtrlAck.Set(ServerPacket.Header.LP_MobMove.Get() + 0x01);
        // Header.MOVE_MONSTER.Get() + 0x02 は存在しない
        ServerPacket.Header.LP_MobStatSet.Set(ServerPacket.Header.LP_MobMove.Get() + 0x03);
        ServerPacket.Header.LP_MobStatReset.Set(ServerPacket.Header.LP_MobMove.Get() + 0x04);
        // Header.MOVE_MONSTER.Get() + 0x05
        ServerPacket.Header.LP_MobAffected.Set(ServerPacket.Header.LP_MobMove.Get() + 0x06);
        ServerPacket.Header.LP_MobDamaged.Set(ServerPacket.Header.LP_MobMove.Get() + 0x07);
        // Header.MOVE_MONSTER.Get() + 0x08
        // Header.MOVE_MONSTER.Get() + 0x09
        // Header.MOVE_MONSTER.Get() + 0x0A
        ServerPacket.Header.LP_MobHPIndicator.Set(ServerPacket.Header.LP_MobMove.Get() + 0x0B);
        /*
        ServerPacket.Header.SHOW_MAGNET.Set(Header.MOVE_MONSTER.Get() + 0x0C);
        ServerPacket.Header.CATCH_MONSTER.Set(0x0114);
        ServerPacket.Header.MOB_SPEAKING.Set(0x0115);
        // 0x0116 @0116 int,int,int,int, 何らかの変数が更新されるが詳細不明
        ServerPacket.Header.MONSTER_PROPERTIES.Set(0x0117);
        ServerPacket.Header.REMOVE_TALK_MONSTER.Set(0x0118);
        ServerPacket.Header.TALK_MONSTER.Set(0x0119);
         */

        ServerPacket.Header.LP_NpcEnterField.Set(0x00D5);
        ServerPacket.Header.LP_NpcChangeController.Set(0x00D7);
        ServerPacket.Header.LP_ScriptMessage.Set(0x0100); // 00698C63
        ServerPacket.Header.LP_OpenShopDlg.Set(ServerPacket.Header.LP_ScriptMessage.Get() + 0x01);
        ServerPacket.Header.LP_ShopResult.Set(ServerPacket.Header.LP_ScriptMessage.Get() + 0x02);

        //Header.OPEN_STORAGE.Set(0x0104);
        //Header.MERCH_ITEM_MSG.Set(Header.NPC_TALK.Get() + 0x05);
        //Header.MERCH_ITEM_STORE.Set(Header.NPC_TALK.Get() + 0x06);
        //Header.RPS_GAME.Set(Header.NPC_TALK.Get() + 0x07);
        //Header.MESSENGER.Set(Header.NPC_TALK.Get() + 0x08);
        ServerPacket.Header.LP_MiniRoom.Set(ServerPacket.Header.LP_ScriptMessage.Get() + 0x09);
    }
}
