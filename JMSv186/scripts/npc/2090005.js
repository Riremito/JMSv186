// ツノーレ
// オルビスからリプレなど

var spawn_portal = Array(
	[200000100, 0], // オルビスチケット売場
	[250000100, "sp"], // 武陵神社
	[251000000, 0] // 白草村
);

var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var mapid = cm.getMapId();
				var text = "メッセージ準備中\r\n";
				// 武陵
				if (mapid == 250000100) {
					text += "#L" + 200000100 + "##b#m200000100#に行く。#k#l\r\n"
					text += "#L" + 251000000 + "##b#m251000000#に行く。#k#l\r\n"
					return cm.sendSimple(text);
				}
				// 白草村
				if (mapid == 251000000) {
					text = "良く来た！　我輩は#b#m250000000##kへ往復している孤高の鶴であーる！今#m250000000#に出発したいであーるか？#b500 メル#kでいいあーる。";
					// Yes Noに修正予定
					text += "#L" + 250000100 + "##b#m250000100#に行く。#k#l\r\n"
					return cm.sendSimple(text);
				}
				// オルビス
				if (mapid == 200000141) {
					text = "良く来た！　我輩は#b#m250000000##kへ往復している孤高の鶴であーる！今#m250000000#に出発したいであーるか？\r\n";
					text += "#L" + 250000100 + "##b#m250000000#(1500 メル)#k#l\r\n"
					return cm.sendSimple(text);
				}
				// デバッグモード
				text = "デバッグモード\r\n";
				text += "#L" + 250000100 + "##b#m250000000#(1500 メル)#k#l\r\n"
				text += "#L" + 250000100 + "##b#m250000100#に行く。#k#l\r\n"

				return cm.sendSimple(text);
			}
		case 1:
			{
				var mapid = selection;
				for (var i = 0; i < spawn_portal.length; i++) {
					if (spawn_portal[i][0] == mapid) {
						cm.warp(spawn_portal[i][0], spawn_portal[i][1]);
						return cm.dispose();
					}
				}
				break;
			}
		default:
			break;
	}

	return cm.dispose();
}