// ラクダ
var cost = 1500;
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
				if (mapid != 260020700) {
					cm.sendYesNo("#bラクダ#kに乗って錬金術の村#bマガティア#kに移動しますか。費用は" + cost + "メルです。");
				}
				else {
					// テキスト不明
					cm.sendYesNo("#bラクダ#kに乗って#bアリアント#kに移動しますか。費用は" + cost + "メルです。");
				}
				return;
			}
		case -1:
			{
				//cm.sendOk("");
				break;
			}
		case 1:
			{
				if (cm.getMeso() >= cost) {
					cm.gainMeso(-cost);
					if (mapid != 260020700) {
						cm.warp(261000000, 0);
					}
					else {
						cm.warp(260000000, 0);
					}
				} else {
					//cm.sendNext("");
				}
				break;
			}
		default:
			break;
	}

	cm.dispose();
}