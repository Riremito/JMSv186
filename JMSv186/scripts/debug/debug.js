// Packet Debugging Script

function test(c) {
	var p = c.getOutPacket();
	p.writeShort(0x00AD);
	p.writeInt(0x00007DBC); // リレミト
	p.writeInt(-1); // ダメージ
	p.writeZeroBytes(100);
	// ProcessPacket
	c.DebugPacket(p.getPacket());
}

// 個人商店のメッセージ
function ShopClose(c) {
	var p = c.getOutPacket();
	// 交換系処理
	p.writeShort(0x015F);
	// 閉じる処理
	p.write(0x0A);
	// 自分
	p.write(0);
	// メッセージ
	p.write(20);
	/*
		0	=	なし
		2	=	なし
		4	=	なし
		7-13=	なし
		15	=	時間経過で自動退場されました。再入場が不可能です。(フリマ入口へ自動的に飛ばされる機能)
		16-36	なし

		営業許可証
		1	=	ここではオープン出来ません。(出店時の場所)
		3	=	商店が閉じています(閉店)
		5	=	強制退場されました。(追放)
		6	=	制限時間が経過し、商店を開くことができませんした(場所取り防止の機能)
		14	=	品物は売れ切れです。(売り切れた場合の強制閉店)

		雇用商人
		17	=	イベントリに空きがないとアイテムはストアーバンクNPCのプレドリックのところで探すべきです。閉店しますか？
		18	=	営業時間が過ぎて閉店します。
		20	=	(メッセージダイアログなしで閉じる)

		雇用商人遠隔管理機
		19	=	マップが移動され、遠隔管理機使用が切断されました。しばらく、後にまた利用ください。
	*/
	c.DebugPacket(p.getPacket());
}

// 個人商店のメッセージ (他プレイヤー視点)
function ShoppingClose(c) {
	var p = c.getOutPacket();
	// 交換系処理
	p.writeShort(0x015F);
	// 閉じる処理
	p.write(0x0A);
	// 自分
	p.write(1);
	// メッセージ
	p.write(10);
	/*
		0	=	???
		4	=	???
		10
		11
		16
		21

		営業許可証
		1	=	ここではオープン出来ません。
		3	=	商店が閉じています。
		5	=	強制退場されました。
		6	=	制限時間が経過し、商店を開く事ができませんした。
		14	=	品物は売れ切れです。
		15	=	時間超過で自動退場されました。再入場が不可能です。
		~27		なし

		雇用商人
		6	=	制限時間が経過し、商店を開く事ができませんした。
		17	=	商店の主人が物品整理中でございます。もうしばらく後でご利用ください。
		18	=	営業時間が過ぎて閉店します。
		19	=	マップが移動され、遠隔管理機使用が切断されました。しばらく、後にまた利用ください。
		20	=	(メッセージダイアログなしで閉じる)
		~29		なし

		交換
		2	=	相手が交換を取り消しました。
		7	=	交換が終わりました。結果を確認してください。
		8	=	交換に失敗しました。
		9	=	１個のみ所持可能なアイテムがあって交換に失敗しました。
		12	=	相手が別のマップにいる為交換できません。
		13	=	ゲームファイルが損傷されアイテム取引ができません。ゲーム再設置後もう一度試してください。

		ゲーム(神経衰弱)
		0	=	部屋から退場しました。
		2	=	トーナメントが終わりました。10秒後に自動的にルームが閉ざされます。(チャット)
		3	=	部屋が閉じられました
		4	=	部屋から退場しました。
		5	=	強制退場されました。
	*/
	c.DebugPacket(p.getPacket());
}

// ゲーム
function Gaming(c) {
	var p = c.getOutPacket();
	// 交換系処理
	p.writeShort(0x015F);
	// なんらか?
	p.write(0x3D);
	p.writeInt(1);
	p.writeInt(1);
	p.writeInt(2);
	/*
		0x37	他プレイヤーがREADY!!状態
		0x38	他プレイヤーがREADY!!状態をキャンセル
		0x39
		0x3A	プレイヤーのターン(赤枠)
		0x3B	YOU WIN(0), DRAW(1)

	*/
	p.writeZeroBytes(100);
	c.DebugPacket(p.getPacket());
}

// ゲーム
function AvaTrade(c) {
	var p = c.getOutPacket();
	p.writeShort(0x015F);
	p.write(2);
	p.write(6);
	/*
		3	=	交換
		6	=	ポイント交換
	*/
	p.writeMapleAsciiString("リレミトX");
	p.writeInt(1);
	p.writeZeroBytes(100);
	c.DebugPacket(p.getPacket());
}

function UpdateTama(c) {
	var p = c.getOutPacket();
	p.writeShort(0x0025);
	p.write(15);
	p.writeInt(7777);
	c.DebugPacket(p.getPacket());
}

// 任意のテキストの表示
function ChatMessage(c) {
	for (var i = 0; i <= 0x1A; i++) {
		var p = c.getOutPacket();
		p.writeShort(0x00FB);
		p.writeShort(i);
		p.writeMapleAsciiString("リレミト : メッセージ");
		p.writeZeroBytes(100);
		c.DebugPacket(p.getPacket());
	}
}


function AttackDamageMotion(c) {
	var p = c.getOutPacket();
	p.writeShort(0x00FE);
	p.writeInt(0);
	p.writeInt(0);
	p.writeInt(0);
	p.writeInt(1); // 吹っ飛び判定
	p.writeInt(9999); // 被ダメージ
	p.writeZeroBytes(100);
	c.DebugPacket(p.getPacket());
}

function ClosedPortal(c) {
	var p = c.getOutPacket();
	p.writeShort(0x0135);

	/*
		0	ポータルが開けませんでした。
		1	入場には[]が必要です。
		2	原因不明の理由で入場できません。
	*/
	p.write(1);
	p.writeInt(2000005); // 1 の場合にアイテム名が表示される
	p.writeZeroBytes(100);
	c.DebugPacket(p.getPacket());
}

// マップ退場メッセージ
function BossAfter(c) {
	var p = c.getOutPacket();
	p.writeShort(0x014F);
	/*
		0	マップが閉じられました。 or X分以内に出現中のモンスターを倒さないとマップから退場します。
		1	ボスモンスター退治後、マップ退場まで残りX分です。
	*/
	p.write(0);
	p.writeInt(33); // 残り時間
	p.writeZeroBytes(100);
	c.DebugPacket(p.getPacket());
}

// /MapleTV GMコマンドのexploit利用した場合にサーバーから返ってくるパケットと同等
function MapleTVErrorMessage(c) {
	var p = c.getOutPacket();
	p.writeShort(0x018F);
	/*
		0以外	エラーメッセージ処理
	*/
	p.write(1);
	/*
		1	Non-GM character tried to send GM message.
		2	You entered wrong user name.
		3	You have to wait for more than 1 Hour now. Please use it later.
	*/
	p.write(1);
	c.DebugPacket(p.getPacket());
}

// 強制的にUIを開く
function OpenUI(c) {
	var p = c.getOutPacket();
	p.writeShort(0x00EC);
	/*
		0x01	装備
		0x02	ステータス
		0x03	スキル
		0x05	キー設定
		0x06	クエスト
		0x09	モンスターブック
		0x0A	キャラクター情報
		0x11	クラッシュ
		0x15	グループ探し
		0x16	メーカー
		0x19	マイランキング Webブラウザ
		0x1A	ファミリースキル
		0x1A	ファミリー家系図
		0x1C	GM Story Board Webブラウザ
		0x1D	運用者から手紙が届きました。(右端にアイコン出現)
		0x1E	勲章
		0x1F	メイプルイベント @010D 00が送信される
		0x20	エヴァンのスキル
		0x22	チャット
		0x23	クラッシュ
	*/
	p.write(0x29);
	p.writeZeroBytes(100);
	c.DebugPacket(p.getPacket());
}

// マップ移動関連のエラーメッセージ
function MapMoveMessage(c) {
	/*
		0x01	只今、ポータルが閉じられています。
		0x02	他の大陸への瞬間移動は不可能です。
		0x03	地の気運に遮られて近づけることができません。
		0x04	テレポートできない場所です。(ダイアログ)
		0x05	地の気運に遮られて近づけることができません。
		0x06	グループメンバーのみ入場することができるマップです。
		0x07	遠征隊メンバーのみ入場できるマップです。
	*/
	for (var i = 1; i <= 7; i++) {
		var p = c.getOutPacket();
		p.writeShort(0x0084);
		p.write(i);
		c.DebugPacket(p.getPacket());
	}
}

// 灰色の文字列
function GrayMessage(c) {
	var p = c.getOutPacket();
	p.writeShort(0x007B);
	p.writeInt(0); // 不明
	p.writeMapleAsciiString("Maple"); // 文字列
	c.DebugPacket(p.getPacket());
}

// プレゼント
function GiftTest(c) {
	var p = c.getOutPacket();
	p.writeShort(0x004F);

	/*
		0x01	プレゼントが来ました。(通知)
		0x03	アイテムインベントリの消費欄に空きがないためプレゼントが渡せません。
	*/
	p.write(0x01);
	p.writeZeroBytes(100);
	c.DebugPacket(p.getPacket());
}

// パチンコ情報 (UIの玉も更新される)
function PachiUpdate(c) {
	var p = c.getOutPacket();
	p.writeShort(0x004C);
	p.writeInt(0);
	p.writeInt(8787); // 玉
	// bufferをdecodeしているのでフォーマット不明
	p.writeZeroBytes(100);
	c.DebugPacket(p.getPacket());
}

function Reported(c) {
	var p = c.InPacket(0x002A);
	/*
		0x02	受付成功
		0x03	申告されました。

		0x41	しばらく後もう一度行ってください。
		0x42	キャラクター名を確認してからもう一度行ってください。
		0x43	申告に必要な手数料のメルが足りません。
		0x44	サーバに接続できません。
		0x45	申告可能回数を超過しました。
		0x46	X時から X時まで申告可能です。
		0x47	虚偽申告で制裁され申告できません。
	*/
	p.Encode1(0x40);
	p.Encode4(0);
	p.Encode4(0);

	// 0埋め
	p.Encode4(0);
	p.Encode4(0);
	p.Encode4(0);
	p.Encode4(0);
	p.Encode4(0);
	p.Encode4(0);
	p.Encode4(0);
	p.Encode4(0);
	// 送信
	c.ProcessPacket(p.Get());
}

// test
// ベガ Encode1 (0x40, 0x41, 0x43)
function ViciousHammer(c) {
	var pp = c.InPacket(0x0192);
	pp.Encode1(0x3A);
	// 未使用?
	pp.Encode4(0);
	// 2-Xで残り回数
	pp.Encode4(2);
	c.ProcessPacket(pp.Get());

	var p = c.InPacket(0x0192);
	p.Encode1(0x38); // 0x38, 0x39, 0x3A
	p.Encode4(1);
	c.ProcessPacket(p.Get());
}

function VegaScroll(c) {
	var pp = c.InPacket(0x0196);
	// 3C or 3E
	pp.Encode1(0x3E);
	c.ProcessPacket(pp.Get());
	var p = c.InPacket(0x0196);
	// 3B = 成功? ,40 = 失敗?
	p.Encode1(0x3B);
	c.ProcessPacket(p.Get());
}

function TestPacket(c) {
	var p = c.InPacket(0x016C);
	/*
		0x09	エラー?
		0x0A	UIを開く
		0x19	D/C
		0x1A	宅配物到着! 通知
		0x1B	速達のUI (クイック配送利用券)
		0x1C	宅配物到着! 通知
	*/
	p.Encode1(0x0A);
	p.Encode1(0);
	p.Encode1(0);
	p.Encode1(0);
	/*
	p.Encode1(0);
	p.Encode4(0);
	p.Encode4(0);
	p.Encode4(0);
	p.Encode4(0);
	p.Encode4(0);
	*/
	c.ProcessPacket(p.Get());
}

/*
function TestPacket(c) {
	var p = c.InPacket(0x00F4);
	c.ProcessPacket(p.Get());
}
*/

// キャラID 0x00007DBC

// Javaから呼ばれる
function debug(c) {
	TestPacket(c);
}