// OS4シャトル 移動先から退出

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
				cm.sendYesNo("退出しますか？");
				return;
			}
		case 1:
			{
				cm.warp(502010000, "sp");
				break;
			}
		default:
			break;
	}

	cm.dispose();
}