// イルカ

var spawn_portal = Array(
	[230000000, "market00"],
	[230030200, "east00"],
	[251000100, "out00"],
	[923020000, "sp"]
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
				/*
				var text = "世界の全ての海は繋がっています。歩いては遠いところでも海でいけば近くなるんです。#bイルカタクシー#kに乗って移動しますか。\r\n";
				text += "#L" + 0 + "##bイルカタクシーチケット#kを使用して#m230030200#に移動する#l"
				text += "#L" + 1 + "#10000 メルを払い、#m251000100#に移動する。#l"
				*/
				var mapid = cm.getMapId();
				var text = "世の中の全ての海は繋がっています。歩くと遠い所でも海ならすぐですよ。どうですか？#bイルカタクシー#kに乗って移動しますか？\r\n";
				if (mapid == 230000000) {
					text += "#L" + 230030200 + "##b#m230030200#に行く。#k#l\r\n"
					// 251000100
					text += "#L" + 251000100 + "##b#m251000000#に行く。#k#l\r\n"
					text += "#L" + 923020000 + "##b霧の海に行く。#k#l\r\n"
					return cm.sendSimple(text);
				}
				if (mapid == 251000100) {
					text += "#L" + 230000000 + "##b#m230000000#に行く。#k#l\r\n"
					// 251000100
					text += "#L" + 923020000 + "##b霧の海に行く。#k#l\r\n"
					return cm.sendSimple(text);
				}
				// デバッグモード
				text += "#L" + 230000000 + "##b#m230000000#に行く。#k#l\r\n"
				text += "#L" + 230030200 + "##b#m230030200#に行く。#k#l\r\n"
				text += "#L" + 251000100 + "##b#m251000000#に行く。#k#l\r\n"
				text += "#L" + 923020000 + "##b霧の海に行く。#k#l\r\n"

				return cm.sendSimple(text);
			}
		case 1:
			{
				var mapid = selection;
				for (var i = 0; i < spawn_portal.length; i++) {
					if (spawn_portal[i][0] == mapid) {
						if (mapid == 923020000) {
							// 霧の海
							cm.saveLocation("MULUNG_TC");
						}
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