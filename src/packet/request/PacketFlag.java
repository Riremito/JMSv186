package packet.request;

import packet.ops.OpsNewCharacter;
import packet.ops.OpsMessage;
import packet.request.struct.CMovePath;
import packet.response.struct.SecondaryStat;
import packet.response.struct.GW_CharacterStat;

public class PacketFlag {

    public static void Update() {
        ReqCTrunkDlg.Init();
        ReqCNpcPool.Init();
        GW_CharacterStat.Init();
        SecondaryStat.Init();
        OpsMessage.Message_Init();
        CMovePath.setJumpDown();
        OpsNewCharacter.Init();
    }
}
