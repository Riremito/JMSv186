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


// ビシャハン
function DoSomething() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0x018E);

	// data
	p.write(50);
	p.writeInt(0);
	p.writeInt(1);
	p.writeZeroBytes(100);

	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

function action(mode, type, selection) {
	DoSomething();
	cm.dispose();
}