// ƒvƒfƒBƒ“

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// BBŒã
				var text = "‚±‚±‚Í‰p—Y‚Ì“a“°‚¾B\r\n";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}