function DoSomething() {
	var p = c.getOutPacket();
	// header
	p.writeShort(0x018E);

	// data
	p.write(49);
	p.writeInt(0);
	p.writeInt(1);
	p.writeZeroBytes(100);

	// ProcessPacket
	c.DebugPacket(p.getPacket());
}

function test() {
	DoSomething();
}