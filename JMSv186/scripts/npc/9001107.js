// 案内うさぎ 月の国招待券 922231000
// 用途不明
var status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	var mapid = cm.getMapId();

	switch (status) {
		case 0:
			{
				cm.sendYesNo("退出しますか？");
				return;
			}
		case 1:
			{
				cm.warp(100000000);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}