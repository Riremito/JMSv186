// パチンコ3
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
				cm.sendYesNo("パチンコを始めましょうか。");
				return;
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