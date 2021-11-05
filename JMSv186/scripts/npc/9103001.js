// ガイドモモ
// ルディブリアム迷路入場

var npc_talk_status = -1;

// 9103000, 809050015 ラスト
// 9103002, 809050016 報酬あり
// 9103003, 809050017 報酬なし

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// 原文ママ
				var text = "こちらはルディブリアム迷路の入口となります。是非楽しんでいって下さい。\r\n";
				text += "#L" + 0 + "##bルディブリアム迷路に入る#k#l\r\n";
				text += "#L" + 1 + "##bルディブリアム迷路とは？#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}
