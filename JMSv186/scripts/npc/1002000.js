// ピル
// タクシー
// テキスト不足で説明内容が不明なので移動処理だけ実装

// 原文通りの並び
var taxi_map = Array(
	[101000000, 0],
	[102000000, 0],
	[104000000, 0],
	[100000000, 0],
	[103000000, 0],
	[120000000, 0]
);

var npc_talk_status = 0;
var to_map = 0;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// 原文ママ
				var text = "君は初心者ではないな？なら料金は規定どおりにいただくぜ？さあ、どの村へ行きたいんだい？\r\n";
				var mapid = cm.getMapId();
				for (var i = 0; i < taxi_map.length; i++) {
					// 現在のマップはスキップ
					if (mapid == taxi_map[i][0]) {
						continue;
					}
					text += "#L" + taxi_map[i][0] + "##b#m" + taxi_map[i][0] + "#(" + taxi_map[i][1] + "メル)#k#l\r\n";
				}
				return cm.sendSimple(text);
			}
		case 2:
			{
				to_map = selection;
				var text = "ここではもう用事がないようですね。本当に#m" + to_map + "#へ移動しますか？\r\n";
				return cm.sendYesNo(text);
			}
		case 3:
			{
				cm.warp(to_map);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}