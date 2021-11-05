// エヴァン特殊マップ
// 臨時港

var status = -1;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status == 0) {
			cm.dispose();
		}
		status--;
	}
	if (status == 0) {
		// 適当
		cm.sendYesNo("港口に戻りますか？");
	} else if (status == 1) {
		cm.warp(104000000);
		cm.dispose();
	}
}