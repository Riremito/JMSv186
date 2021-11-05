// バイキン

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
				var text = "おいおい、早く宝の地図を探してこいよ。地図がなけりゃあ海には出られないぜ。";
				return cm.sendOk(text);
			}
		default:
			break;
	}

	return cm.dispose();
}