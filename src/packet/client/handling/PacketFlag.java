package packet.client.handling;

import packet.struct.*;

public class PacketFlag {

    public static void Update() {
        MovementPacket.Init();
        TrunkPacket.Init();
        NPCPacket.Init();
        GW_CharacterStat.Init();
        SecondaryStat.Init();
        ContextPacket.Message_Init();
    }
}
