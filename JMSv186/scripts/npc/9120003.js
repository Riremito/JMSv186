// ヒカリ
// 銭湯

var cost = 300;
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
				cm.sendYesNo("銭湯に入りたいですか？では、" + cost + "メルを払いなさい。");
				return;
			}
		case -1:
			{
				cm.sendOk("今度利用してください。");
				break;
			}
		case 1:
			{
				var gender = cm.getPlayerStat("GENDER");
				if (cm.getMeso() >= cost) {
					cm.gainMeso(-cost);
					if (gender == 0) {
						cm.warp(801000100);
					}
					else {
						cm.warp(801000200);
					}
				} else {
					cm.sendOk("入場料" + cost + "メルを持っているか確認してください。");
				}
				break;
			}
		default:
			//cm.sendYesNo("mode = " + mode + ", type " + type + ", selection = " + selection + ", status = " + status);
			break;
	}

	cm.dispose();
}