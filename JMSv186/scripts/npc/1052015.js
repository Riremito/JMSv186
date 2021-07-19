// ビリ@ネットカフェ
// パケットデバッグ

// test
function DoSomething_003F() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0x003F);

	// data
	p.write(0x05);
	p.writeMapleAsciiString("テスト");
	p.writeZeroBytes(100);

	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

function DoSomething_A8() {
	var p = cm.getOutPacket();
	var id = cm.getPlayer().getId();
	// header
	p.writeShort(0xA8);

	// data
	p.writeInt(id);
	p.write(1);
	p.write(1);
	//p.writeShort(1); // success
	p.writeShort(0);
	p.write(0);
	p.writeInt(0);

	p.writeZeroBytes(100);

	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

// ミラクルキューブ
function DoSomething_AC() {
	var p = cm.getOutPacket();
	var id = cm.getPlayer().getId();
	// header
	p.writeShort(0xAC);

	// data
	p.writeInt(id);
	p.writeInt(1);

	//p.writeZeroBytes(100);

	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

// メーカー
function DoSomething_DC() {
	var p = cm.getOutPacket();
	var id = cm.getPlayer().getId();
	// header
	p.writeShort(0xDC);

	// data
	p.write(17);
	p.writeInt(0); // 0 = success, 1 = fail
	cm.DebugPacket(p.getPacket());
}

// 道場
function DoSomething_DC() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0x00DC);

	// data
	p.write(08);
	p.writeZeroBytes(100);

	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}


// 獲得メッセージ
function DoSomething_25() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0x25);

	// data
	p.write(15);
	/*
		0	アイテムをこれ以上持つことができないです
		1	?
		2	?
		3	経験値
		4	SP (チャット)
		5	人気度 (チャット)
		6	メル
		7	ギルドポイント
		8	?
		9	?
		10	error code 38
		11
		12
		13	error code 38
		14	error code 38
		15 パチンコ玉
		16

	*/

	p.writeInt(1337);
	//p.writeZeroBytes(100);

	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

// ランダムメル袋
function DoSomething_E1() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0xE1);

	// type
	p.write(4);
	/*
		0	少量
		1	少量
		2	通常
		3	多量
		4	持ちきれない程
	*/


	p.writeInt(123456789);
	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

// パチンコ玉追加失敗
function DoSomething_F3() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0xF3);

	p.writeInt(123456789);
	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

// パチンコ玉追加失敗
function DoSomething_F4() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0xF4);
	cm.DebugPacket(p.getPacket());
}

// test
function DoSomething() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0xF3);

	p.writeInt(123456789);
	p.writeZeroBytes(100);
	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}


function action(mode, type, selection) {
	DoSomething();
	cm.dispose();
}