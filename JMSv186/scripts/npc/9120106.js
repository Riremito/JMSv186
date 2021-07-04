// パチンコ玉交換機
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
				cm.sendYesNo("パチンコ玉を購入するためにポイントショップへ行きますか？");
				return;
			}
		case -1:
			{
				cm.sendOk("またのご利用をお待ちしております。");
				break;
			}
		case 1:
			{
				cm.sendOk("ポイントショップへ入場する処理");
				break;
			}
		default:
			//cm.sendYesNo("mode = " + mode + ", type " + type + ", selection = " + selection + ", status = " + status);
			break;
	}

	cm.dispose();
}