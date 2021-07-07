// 果物屋トレホレ

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
				cm.warp(741020100);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}