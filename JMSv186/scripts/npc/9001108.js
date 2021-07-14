// 案内うさぎ
// 用途不明 3個のマップへランダム入場らしい?

var status = -1;
function EnterRabbit() {
	switch (status) {
		case 0:
			{
				cm.sendYesNo("入場しますか？（テスト）");
				return;
			}
		case 1:
			{
				// 同一マップが7個あるのでミニダンジョンタイプ?
				var r = Math.floor(Math.random() * 3);
				switch (r) {
					case 0:
						cm.warp(922230100, "sp");
						break
					case 1:
						cm.warp(922230200, "sp");
						break
					case 2:
						cm.warp(922230300, "sp");
					default:
						break
				}
				break;
			}
		default:
			break;
	}
	cm.dispose();
}
function LeaveRabbit() {
	switch (status) {
		case 0:
			{
				cm.sendYesNo("退出しますか？");
				return;
			}
		case 1:
			{
				cm.warp(922230000, "sp");
				break;
			}
		default:
			break;
	}
	cm.dispose();
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	var mapid = cm.getMapId();

	if (mapid == 922230000) {
		EnterRabbit();
	}
	else {
		LeaveRabbit();
	}
}