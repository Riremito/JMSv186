// 危険地域弾丸タクシー

var taxi_map = Array(
	// エルナス
	[211000000, 211040200, 0],
	// ルディブリアム
	[220000000, 220050300, 0],
	// リプレ
	[240000000, 240030000, 0]
);

var npc_talk_status = -1;
var to_map = 910000000;
var cost = 0;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var mapid = cm.getMapId();
				for (var i = 0; i < taxi_map.length; i++) {
					if (mapid == taxi_map[i][0]) {
						to_map = taxi_map[i][1];
						cost = taxi_map[i][2];
						break;
					}
				}
				// 原文ママ
				var text = "こんにちは！ダンジョン行きの特急タクシーです。#m" + mapid + "#に乗って#b#m" + to_map + "##kに移動しますか。費用は#b" + cost + "メル#kです。\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				cm.warp(to_map);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}
