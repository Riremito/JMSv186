// 番人ヴォルフ

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
				var text = "なんだ、人間…用がなかったらあっちへ行け。\r\n";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}