package packet;

/**
 *
 * @author Riremito
 */
public class TWMS_v122_1_CP {

    public static void Set() {
        ClientPacket.Header.CP_CheckPassword.Set(0x0001);
        ClientPacket.Header.CP_AccountInfoRequest.Set(0x0002);
        ClientPacket.Header.CP_WorldInfoRequest.Set(0x0003);
        ClientPacket.Header.CP_SelectWorld.Set(0x0004);
        ClientPacket.Header.CP_CheckUserLimit.Set(0x0005);
        ClientPacket.Header.CP_SelectCharacter.Set(0x0006);
        ClientPacket.Header.CP_MigrateIn.Set(0x0007);
        ClientPacket.Header.CP_CheckDuplicatedID.Set(0x0008);
        ClientPacket.Header.CP_ViewAllChar.Set(0x000A);
        ClientPacket.Header.CP_CreateNewCharacter.Set(0x000B);
        ClientPacket.Header.CP_CreateNewCharacterInCS.Set(0x000C);
        ClientPacket.Header.CP_DeleteCharacter.Set(0x000D);
        ClientPacket.Header.CP_AliveAck.Set(0x000E);
        ClientPacket.Header.CP_ExceptionLog.Set(0x000F);
        ClientPacket.Header.CP_SecurityPacket.Set(0x0010);
        ClientPacket.Header.CP_CheckPinCode.Set(0x0014);
        ClientPacket.Header.REACHED_LOGIN_SCREEN.Set(0x0017);
        ClientPacket.Header.CP_CreateSecurityHandle.Set(0x0019);
    }
}
