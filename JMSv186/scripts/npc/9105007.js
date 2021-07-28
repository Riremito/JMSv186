var boss_map = Array(
	[9400337, 889210100],
	[9400449, 889210200],
	[9400339, 889210300],
	[9400340, 889210400],
	[9400341, 889210500],
	// 休憩1
	[9400342, 889210600],
	[9400343, 889210700],
	[9400344, 889210800],
	[9400346, 889210900],
	[9400450, 889211000],
	// 休憩2
	[9400349, 889211100],
	[9400350, 889211200],
	[9400351, 889211300],
	[9400352, 889211400],
	[9400353, 889211500],
	// 休憩3
	[9400354, 889211600],
	[9400357, 889211700],
	[9400361, 889211800],
	[9400452, 889211900],
	[9400454, 889212000],
	// 休憩4
	[9400367, 889212100],
	[9400368, 889212200],
	[9400369, 889212300],
	[9400455, 889212400],
	[9400456, 889212500]
)


function EnterBossMap(mapid) {
	if (cm.getPlayerCount(mapid) == 0) {
		cm.resetMap(mapid);
	}
	cm.warp(mapid, "sp");
}

var status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
		return;
	}

	status++;

	switch (status) {
		// 選択画面
		case 0:
			{
				text = "海外ボスモンスターレイド v180\r\n";
				for (var i = 0; i < boss_map.length; i++) {
					text += "#L" + i + "##b#o" + boss_map[i][0] + "# (#m" + boss_map[i][1] + "#)#l#k\r\n";
				}

				cm.sendSimple(text);
			}
			return;
		case 1:
			EnterBossMap(boss_map[selection][1]);
			break;
		default:
			break;
	}
	return cm.dispose();
}

/*
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
				cm.sendYesNo("ボスモンスターレイド開始");
				return;
			}
		case 1:
			{
				EnterBossMap(889210100);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}
*/