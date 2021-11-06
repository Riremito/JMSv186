// 練習記録帳

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				cm.getMap().killMonster(5090001); // マクロ探知モンスター
				// BB後
				var text = "練習日誌を書く練習記録表である。真面目に練習日誌を書いた修練生のみ仙人人形の怒りを静めることができるという。\r\n";
				
				return cm.sendOk(text);
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