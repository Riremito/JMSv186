package packet;

public class v131_0_CP {

    public static void Set() {
        ClientPacket.Header.CP_CheckPassword.Set(0x0001);
        ClientPacket.Header.CP_WorldInfoRequest.Set(0x0003);
        ClientPacket.Header.CP_SelectWorld.Set(0x0004);
        ClientPacket.Header.CP_SelectCharacter.Set(0x0006);
        ClientPacket.Header.CP_MigrateIn.Set(0x0007);
        ClientPacket.Header.CP_CheckDuplicatedID.Set(0x0008);
        ClientPacket.Header.CP_CreateNewCharacter.Set(0x0009);
        ClientPacket.Header.CP_DeleteCharacter.Set(0x000A);

        // GameGuardのUpdateが必要かどうか
        ClientPacket.Header.CP_T_UpdateGameGuard.Set(0x000F);

        ClientPacket.Header.CP_UserTransferFieldRequest.Set(0x0015);
        ClientPacket.Header.CP_UserPortalScriptRequest.Set(0x0048);
        ClientPacket.Header.CP_UserPortalTeleportRequest.Set(0x0049);

        ClientPacket.Header.CP_UserTransferChannelRequest.Set(0x0016);
        ClientPacket.Header.CP_UserMigrateToCashShopRequest.Set(0x0017);
        ClientPacket.Header.CP_UserChat.Set(0x001F);

        ClientPacket.Header.CP_UserSelectNpc.Set(0x0025);
        ClientPacket.Header.CP_UserScriptMessageAnswer.Set(0x0026);
        ClientPacket.Header.CP_UserShopRequest.Set(0x0027);
        ClientPacket.Header.CP_UserTrunkRequest.Set(0x0028);

    }
}
