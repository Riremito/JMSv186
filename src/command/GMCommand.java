// GMコマンド
package command;

import client.MapleClient;
import debug.Debug;
import tools.data.input.SeekableLittleEndianAccessor;

public class GMCommand {

    public static boolean Accept(final SeekableLittleEndianAccessor p, final MapleClient c) {
        byte command = p.readByte();

        Debug.AdminLog("[GM Command] " + String.format("%02X", command));

        switch (command) {
            // /create
            case 0x00: {
                // アイテム作成
                int itemid = p.readInt();
                return true;
            }
            // /ban test
            case 0x03: {
                // @0086 [03] 04 00 74 65 73 74
                return true;
            }
            // /norank test
            case 0x06: {
                // @0086 [06] 04 00 74 65 73 74
                return true;
            }
            // /unblock test
            case 0x05: {
                // @0086 [05] 04 00 74 65 73 74
                return true;
            }
            // /pton test
            // /ptoff test
            case 0x07: {
                // @0086 [07] 01 04 00 74 65 73 74
                // @0086 [07] 00 04 00 74 65 73 74
                return true;
            }
            // /hide 0, 1
            case 0x0F: {
                // @0086 [0F] 00
                return true;
            }
            // /questreset 111
            case 0x16: {
                // @0086 [16] 6F 00
                return true;
            }
            // /hackcheckcountreload
            case 0x19: {
                // @0086 [19]
                return true;
            }
            // /summon
            case 0x1A: {
                // Mob召喚
                int mobid = p.readInt();
                int count = p.readInt();
                return true;
            }
            // /levelset 111
            case 0x1C: {
                // @0086 [1C] 6F
                return true;
            }
            // /job 900
            case 0x1D: {
                // @0086 [1D] 84 03 00 00
                return true;
            }
            // /apget 111
            case 0x1F: {
                // @0086 [1F] 6F 00 00 00
                return true;
            }
            // /spget 111
            case 0x20: {
                // @0086 [20] 6F 00 00 00
                return true;
            }
            // /str
            case 0x21: {
                // AP割り当て
                int count = p.readInt();
                return true;
            }
            // /dex
            case 0x22: {
                // AP割り当て
                int count = p.readInt();
                return true;
            }
            // /int
            case 0x23: {
                // AP割り当て
                int count = p.readInt();
                return true;
            }
            // /luk
            case 0x24: {
                // AP割り当て
                int count = p.readInt();
                return true;
            }
            // /mmon test
            case 0x26: {
                // @0086 [26] 04 00 74 65 73 74
                return true;
            }
            // /mmoff test
            case 0x27: {
                // @0086 [24] 04 00 74 65 73 74
                return true;
            }
            // /refreshweatherevent
            case 0x30: {
                // @0086 [30]
                return true;
            }
            // /stagesystem test 7
            case 0x33: {
                // @0086 [33] 04 00 74 65 73 74 [07]
                return true;
            }
            // /activatestagesystem 1
            case 0x34: {
                // @0086 [34] [01]
                return true;
            }
            // /cubecomplete
            case 0x36: {
                // @0086 [36]
                return true;
            }
            // /createnpc
            case 0x3B: {
                // NPC召喚?
                //int npcid = p.readInt();
                return true;
            }
            default:
                break;
        }

        return false;
    }

    public static boolean AcceptMessage(final SeekableLittleEndianAccessor p, final MapleClient c) {
        short text_length = p.readShort();
        byte[] text_bytes = p.read(text_length);
        String text = new String(text_bytes);
        Debug.AdminLog("[GM Command Text] " + text);
        return true;
    }
}
