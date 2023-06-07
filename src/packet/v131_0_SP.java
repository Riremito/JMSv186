package packet;

public class v131_0_SP {

    public static void Set() {
        ServerPacket.Header.LP_CheckPasswordResult.Set(0x0001);
        ServerPacket.Header.LP_WorldInformation.Set(0x0003);
        ServerPacket.Header.LP_SelectWorldResult.Set(0x0004);
        ServerPacket.Header.LP_SelectCharacterResult.Set(0x0005);
        ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x0006);
        ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0007);
        ServerPacket.Header.LP_DeleteCharacterResult.Set(0x0008);

        ServerPacket.Header.LP_SetField.Set(0x0047);
        ServerPacket.Header.LP_SetCashShop.Set(0x0048);
        ServerPacket.Header.LP_SetITC.Set(0x0049);

        ServerPacket.Header.LP_MigrateCommand.Set(0x0009); // CC
        ServerPacket.Header.LP_UserChat.Set(0x0063); // chat

        ServerPacket.Header.LP_NpcEnterField.Set(0x00AF);
        ServerPacket.Header.LP_NpcLeaveField.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 1);
        ServerPacket.Header.LP_NpcChangeController.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 2);
    }
}
