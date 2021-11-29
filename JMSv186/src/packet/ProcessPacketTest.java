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

    // ビシャスのハンマーの成功ダイアログで表示される残りアップグレード数を通知する
    public static MaplePacket ViciousHammer_Notify(int hammered) {
        InPacket p = new InPacket(InPacket.Header.VICIOUS_HAMMER);
        // ビシャスのハンマーの使用回数を通知するフラグ, 0x38,0x39以外なら何でもOK
        p.Encode1((byte) 0x3A);
        // 未使用
        p.Encode4(0);
        // 2 - 使用回数 = 残り回数
        p.Encode4(hammered);
        return p.Get();
    }

    // ビシャスのハンマーの成功ダイアログを表示
    public static MaplePacket ViciousHammer_Success() {
        InPacket p = new InPacket(InPacket.Header.VICIOUS_HAMMER);
        // 成功フラグ
        p.Encode1((byte) 0x38);
        /*
            0x00        アップグレード可能回数が1回増えました。あと(2 - hammered)回増やすことが出来ます。
            0x00以外    原因不明の不具合
         */
        p.Encode4(0);
        return p.Get();
    }

    // ビシャスのハンマーの失敗ダイアログを表示, クライアント側で弾かれるのでチート以外では表示されることがないメッセージ
    public static MaplePacket ViciousHammer_Failure(int error) {
        InPacket p = new InPacket(InPacket.Header.VICIOUS_HAMMER);
        // 失敗フラグ
        p.Encode1((byte) 0x39);
        /*
            0x01	このアイテムには使用できません。
            0x02	すでにアップグレード可能回数を超えました。これ以上使用することができません。
            0x03	ホーンテイルのネックレスには使用できません。
            上記以外	原因不明の不具合
         */
        p.Encode4(error);
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

    /*
        0x09    エラーメッセージ
        0x0A    UI
        0x1A	宅配物到着! 通知
        0x1B	速達のUI (クイック配送利用券)
        0x1C	宅配物到着! 通知
     */
    public static MaplePacket Delivery_Open(boolean isQuick, boolean isNPC) {
        InPacket p = new InPacket(InPacket.Header.DUEY);
        // 0x3B or 0x40
        if (isQuick) {
            // 速達のUI
            p.Encode1((byte) 0x1B);
            return p.Get();
        }

        // 通常のUI
        p.Encode1((byte) 0x0A);
        // NPC会話 or 速達の通知から開いたかの判定
        p.Encode1((byte) (isNPC ? 0x00 : 0x01));
        p.Encode1((byte) 0x00);
        p.Encode1((byte) 0x00);
        return p.Get();
    }
}
