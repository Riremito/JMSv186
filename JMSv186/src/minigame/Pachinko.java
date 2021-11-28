package minigame;

import client.MapleCharacter;
import debug.Debug;
import handling.MaplePacket;
import packet.InPacket;

public class Pachinko {

    // パチンコ情報の更新
    public static MaplePacket UpdateTama(MapleCharacter chr) {
        InPacket p = new InPacket(InPacket.Header.MINIGAME_PACHINKO_UPDATE_TAMA);
        // クライアント上ではDecodeBufferで12バイト分Decodeされる
        // キャラクターID (実質不要)
        p.Encode4(chr.getId());
        // アイテム欄の玉の数に反映される値
        p.Encode4(chr.getTama());
        // 用途不明
        p.Encode4(0);
        return p.Get();
    }
}
