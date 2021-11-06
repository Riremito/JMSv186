// チコ
var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "こんにちは！俺はこの周辺のミニゲームを統括している#b#p2040014##kだよ。君、ミニゲームに関心があるようだな。 助けてあげようか。じゃ、やりたいことは？\r\n";
				text += "#L" + 0 + "##bミニゲームのアイテム作成#k#l\r\n";
				text += "#L" + 1 + "##bミニゲームに対する説明を聞く#k#l\r\n";
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