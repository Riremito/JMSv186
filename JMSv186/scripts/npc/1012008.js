// カイジ
var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// BB後
				var text = "俺にはわかる。君は狩に疲れ果てているな？人生、楽しみながら生きていくのが俺の座右の銘でな、ある意味…桃源郷、どうだ？いくつかのアイテムさえあればミニゲームが出来るアイテムと交換してやろう。当ゲームパークは誰でもウェルカムだ。さあ…何を手伝ってやろうか？\r\n";
				text += "#L" + 0 + "##bミニゲームアイテム作成#k#l\r\n";
				text += "#L" + 1 + "##bミニゲームに対する説明を聞く#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				// 説明とか記載する
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}