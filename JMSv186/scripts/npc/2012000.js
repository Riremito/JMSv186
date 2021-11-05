// イフ
// オルビスチケット売場

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// 原文ママ
				var text = "私は各地域に行く船のチケットを売っています。どのチケットを購入しますか？\r\n";
				text += "#L" + 0 + "##bビクトリアアイランドのエリニア#k#l\r\n";
				text += "#L" + 1 + "##bルディブリアム城#k#l\r\n";
				text += "#L" + 2 + "##bリプレ村#k#l\r\n";
				text += "#L" + 3 + "##bアリアント#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				// 購入処理が必要なら記載する
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}
