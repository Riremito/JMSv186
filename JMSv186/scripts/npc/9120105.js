// キャサリン 各街
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
				cm.sendYesNo("パチンコ屋へ移動しますか？");
				return;
			}
		case 1:
			{
				cm.saveLocation("MULUNG_TC");
				cm.warp(809030000, "out00");
				break;
			}
		default:
			break;
	}

	cm.dispose();
}