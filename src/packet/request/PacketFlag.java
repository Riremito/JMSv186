package packet.request;

import packet.request.struct.CMovePath;
import packet.response.struct.SecondaryStat;
import packet.response.struct.GW_CharacterStat;

public class PacketFlag {

    public static void Update() {
        TrunkPacket.Init();
        NPCPacket.Init();
        GW_CharacterStat.Init();
        SecondaryStat.Init();
        ContextPacket.Message_Init();
        CMovePath.setJumpDown();
    }
}
