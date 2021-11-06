// ƒ€ƒ‰ƒg

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
				var text = "‚¢‚Â‚Å‚à“ü‚ê‚é‚æ‚¤‚ÈŠ‚¶‚á‚È‚¢‚¼c";
				return cm.sendOk(text);
			}
		default:
			break;
	}

	return cm.dispose();
}