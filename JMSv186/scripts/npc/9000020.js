// スピネル
// https://www.nicovideo.jp/watch/sm12852798

var spinel_map = Array(
	[740000000, "西門街(台湾)"],
	[701000000, "上海(中国)"],
	[800000000, "キノコ神社(日本)"],
	[500000000, "水上市場(タイランド)"],
	[702000000, "宋山里(中国)"]
);

var npc_talk_status = -1;
var to_map = 0;
var traveling = false;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var mapid = cm.getMapId();
				var spinel_select = "";

				for (var i = 0; i < spinel_map.length; i++) {
					// 現在のマップはスキップ
					if (mapid == spinel_map[i][0]) {
						traveling = true;
						continue;
					}
					spinel_select += "#L" + spinel_map[i][0] + "##b" + spinel_map[i][1] + "#k#l\r\n";
				}

				// 旅行中
				if (traveling) {
					var return_map = cm.getSavedLocation("WORLDTOUR");
					spinel_select += "#L" + return_map + "##b旅行を終え#m" + return_map + "#に戻りたいです。#k#l\r\n";
					var text = "どこに行ってみたいですか？\r\n";
					return cm.sendSimple(text + spinel_select);
				}

				var text = "#b国内のキノコ神社を含め、中国の上海、台湾の西門町#kに続き、#bタイランドの水上市場#kへのコースが用意出来ています。各旅行地でも私が皆様の楽しい旅行のために頑張ります。では、どこから行ってみたいですか？\r\n";
				return cm.sendSimple(text + spinel_select);
			}
		case 1:
			{
				to_map = selection;
				if (!traveling) {
					cm.saveLocation("WORLDTOUR");
				}
				cm.warp(to_map);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}
