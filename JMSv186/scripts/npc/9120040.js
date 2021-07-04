// ポニチャル
// ベルガモット

function GetSpawnItem(itemid) {
	if (cm.haveItem(itemid)) {
		return false;
	}

	cm.gainItem(itemid, 1);
	return true;
}

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
				var text = "ベルガモットテスト\r\n";
				text += "#L" + 1 + "##b" + "入場" + "#l#k\r\n";
				text += "#L" + 2 + "##b" + "退出" + "#l#k\r\n";
				text += "#L" + 0 + "##b" + "フリマ" + "#l#k\r\n";
				cm.sendSimple(text);
				return;
			}
		case 1:
			{
				switch (selection) {
					case 0:
						{
							cm.warp(910000000);
							break;
						}
					case 1:
						{
							// spawn 9400263
							EnterBossMap(802000211);
							break;
						}
					case 2:
						{
							cm.warp(802000210);
							break;
						}
					default:
						break;
				}
				break;
			}
		default:
			break;
	}

	cm.dispose();
}