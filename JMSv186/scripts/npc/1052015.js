// ビリ@ネットカフェ
// パケットデバッグ

// ダメージエフェクト
function DoSomething_00AD() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0x00AD);

	// data
	p.writeInt(0x00007DBC); // リレミト
	p.writeInt(-1); // ダメージ
	p.writeZeroBytes(100);
	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

// test
function DoSomething_CID() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0x00B2);

	// data
	p.writeInt(0x00007DBC); // リレミト
	p.writeInt(0);
	//p.writeShort(1);
	//p.writeShort(0);
	p.writeZeroBytes(100);
	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

// do
function DoSomething() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0x003F);

	// data
	p.write(0x07);
	p.writeMapleAsciiString("リレミト" + " : " + "NAYN!");
	//p.writeMapleAsciiString("リレミト");
	//p.writeInt(1472117);
	p.writeInt(1);
	//p.writeInt(0x00007DBC);
	p.writeZeroBytes(100);
	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

function action(mode, type, selection) {
	DoSomething();
	cm.dispose();
}