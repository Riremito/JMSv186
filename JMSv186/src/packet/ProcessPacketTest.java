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

    // 宅配
    /*
        0x09	???
        0x0A	UIを開く
        0x0C	メルが足りません。
        0x0D	間違った要請です。
        0x0E	宛先の名前を再確認してください。
        0x0F	同じID内のキャラクターには送れません。
        0x10	宛先の宅配保管箱に空きがありません。
        0x11	宅配を受け取ることができないキャラクターです。
        0x12	1個しか持てないアイテムが宛先の宅配保管箱にあります。
        0x13	宅配物を発送しました。
        0x14	原因不明のエラーが発生しました。
        0x16	空きがあるか確認してください。
        0x17	1個しか持てないアイテムがありメルとアイテムを取り出すことができませんでした。
        0x18	宅配物を受け取りました。
        0x19	クラッシュ
        0x1A	宅配物到着! 通知
        0x1B	速達のUI (クイック配送利用券)
        0x1C	宅配物到着! 通知
        0x1D	原因不明のエラーが発生しました。
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

    public static MaplePacket Delivery_Send() {
        InPacket p = new InPacket(InPacket.Header.DUEY);
        p.Encode1((byte) 0x13);
        return p.Get();
    }
}
