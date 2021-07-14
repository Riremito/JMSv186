// OS4シャトル (名前ミス?) 退出? @OSSS秘密基地格納庫 502010000

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
				// 地下道入口
				cm.warp(502010010, "sp");
				break;
			}
		default:
			break;
	}

	cm.dispose();
}