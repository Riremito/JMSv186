package packet;

import handling.MaplePacket;

// よくわからないパケットのテスト
public class ProcessPacketTest {

    // 0x005E @005E 00, ミニマップ点滅, 再読み込みかも?
    public static MaplePacket Test_ReloadMiniMap() {
        InPacket p = new InPacket(InPacket.Header.UNKNOWN_RELOAD_MINIMAP);
        p.Encode1((byte) 0x00);
        return p.Get();
    }

    // 0x0083 @0083, 画面の位置をキャラクターを中心とした場所に変更, 背景リロードしてるかも?
    public static MaplePacket Test_ReloadMap() {
        InPacket p = new InPacket(InPacket.Header.UNKNOWN_RELOAD_MAP);
        return p.Get();
    }

    // ベガの呪文書開始
    public static MaplePacket VegaScroll_Start() {
        InPacket p = new InPacket(InPacket.Header.VEGA_SCROLL);
        // 0x3E or 0x40
        p.Encode1((byte) 0x3E);
        return p.Get();
    }

    // ベガの呪文書の結果
    public static MaplePacket VegaScroll_Result(boolean isSuccess) {
        InPacket p = new InPacket(InPacket.Header.VEGA_SCROLL);
        // 0x3B or 0x40
        p.Encode1((byte) (isSuccess ? 0x3B : 0x40));
        return p.Get();
    }
}
