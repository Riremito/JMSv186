// パチンコ1
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
				cm.sendOk("パチンコの処理");
				break;
			}
		default:
			break;
	}

	cm.dispose();
}