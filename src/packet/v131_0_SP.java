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
    }
}
