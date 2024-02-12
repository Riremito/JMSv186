package packet;

/**
 *
 * @author Riremito
 */
public class CMS_v86_1_SP {

    public static void Set() {
        ServerPacket.Header.LP_CheckPasswordResult.Set(0x0000);
        ServerPacket.Header.LP_WorldInformation.Set(0x0002);
        ServerPacket.Header.LP_SelectWorldResult.Set(0x0003);
        ServerPacket.Header.LP_SelectCharacterResult.Set(0x0004);
        ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0006);
        ServerPacket.Header.LP_DeleteCharacterResult.Set(0x0007);
        ServerPacket.Header.LP_MigrateCommand.Set(0x0008);
        ServerPacket.Header.LP_AliveReq.Set(0x0009);
        ServerPacket.Header.LP_AuthenCodeChanged.Set(0x000A);
        ServerPacket.Header.LP_AuthenMessage.Set(0x000B);
        ServerPacket.Header.LP_SecurityPacket.Set(0x000C);

        ServerPacket.Header.LP_T_UpdateGameGuard.Set(0x000F);
        ServerPacket.Header.LP_ViewAllCharResult.Set(0x0014);
        ServerPacket.Header.LP_LatestConnectedWorld.Set(0x0016);
        ServerPacket.Header.LP_RecommendWorldMessage.Set(0x0017);
        ServerPacket.Header.LOGIN_AUTH.Set(0x001A);

        ServerPacket.Header.LP_SetField.Set(0x0085);
    }
}
