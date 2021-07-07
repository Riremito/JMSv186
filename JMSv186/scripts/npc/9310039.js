// 掛け軸 ボス

function EnterBossMap(mapid) {
	if (cm.getPlayerCount(mapid) == 0) {
		cm.resetMap(mapid);
	}
	cm.warp(mapid);
}

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
				cm.sendNext("グループを結成していますね。それではあなた達を修行の間に送ります。幸運を祈ります～");
				return;
			}
		case -1:
			{
				cm.sendOk("残念ですね…。後で時間ができましたら、ご利用ください。");
				break;
			}
		case 1:
			{
				EnterBossMap(702060000);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}