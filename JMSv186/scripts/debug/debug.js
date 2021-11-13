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
	p.write(16);
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

// Javaから呼ばれる
function debug(c) {
	ShopClose(c);
}