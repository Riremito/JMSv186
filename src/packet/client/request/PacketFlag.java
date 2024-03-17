package packet.client.request;

import packet.server.response.struct.SecondaryStat;
import packet.server.response.struct.GW_CharacterStat;

public class PacketFlag {

    public static void Update() {
        TrunkPacket.Init();
        NPCPacket.Init();
        GW_CharacterStat.Init();
        SecondaryStat.Init();
        ContextPacket.Message_Init();
    }
}
