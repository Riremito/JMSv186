// タクシー
// 港口

var taxi_map = Array(
	[104000000, 0],
	[100000000, 0],
	[102000000, 0],
	[101000000, 0],
	[103000000, 0],
	[120000000, 0]
);

var npc_talk_status = -1;
var to_map = 0;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// 原文ママ
				var text = "こんにちは～! リス港口中型タクシーです。他の村で安全で早く移動したいのですか? それなら私どものタクシーを利用してみて下さい。 安い価格でお望みの所まで親切に迎えて差し上げています。\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				var text = "目的地をお選びください。村事に料金が異なります。\r\n";
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
				// テキスト適当 本来は誤字あり?
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