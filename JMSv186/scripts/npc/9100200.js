// パチンコ1

function PacketTest() {
	var p = cm.getOutPacket();
	// header
	p.writeShort(0x0168);

	// data
	p.writeInt(7777); // パチンコ玉の数
	p.write(0); // 台番号 0, 1, 2
	p.writeZeroBytes(100);

	// ProcessPacket
	cm.DebugPacket(p.getPacket());
}

var status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	switch (status) {
		case 0:
			{
				// パチンコ玉を所持している場合
				if (1) {
					cm.sendYesNo("パチンコを始めましょうか。");
					return;
				}

				cm.sendOk("パチンコ玉が足りないため、パチンコをすることができません。只今、ポイントショップのETCの経済活動欄でパチンコ玉を販売中ですので、ご利用ください。");
				break;
			}
		case -1:
			{
				cm.sendOk("残念ですね…。後で時間ができましたら、ご利用ください。");
				break;
			}
		case 1:
			{
				PacketTest();
				break;
			}
		default:
			break;
	}

	cm.dispose();
}