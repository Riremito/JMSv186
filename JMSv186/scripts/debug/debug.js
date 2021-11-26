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

// Javaから呼ばれる
function debug(c) {
	UpdateTama(c);
}