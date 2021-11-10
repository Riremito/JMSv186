// ƒ}ƒC
// ’Êí‚Í‰ï˜b•s‰Â

var mapid_list = Array(
	1010100,
	1010200,
	1010300,
	1010400
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
				// BBŒã, Œ´•¶’Ê‚è‚¾‚¯‚Çƒ}ƒbƒv–¼‚¿‚á‚ñ‚Æmapid‚©‚ç‚Æ‚Á‚½‚Ù‚¤‚ª—Ç‚¢‚©‚à?
				var text = "Šî‘b‚ğŒÅ‚ß‚½‚¢‚È‚çA‚±‚±‚ªŒü‚¢‚Ä‚¢‚é‚æ‚¤‚¾B‚Ç‚±‚ÅC—û‚·‚éH\r\n";
				// getQuestStatus(1041) == 1
				text += "#b#L0#–`Œ¯Ò‚ÌC—ûê1#l\r\n";
				// getQuestStatus(1042) == 1
				text += "#b#L1#–`Œ¯Ò‚ÌC—ûê2#l\r\n";
				// pi.getQuestStatus(1043) == 1
				text += "#b#L2#–`Œ¯Ò‚ÌC—ûê3#l\r\n";
				// pi.getQuestStatus(1044) == 1
				text += "#b#L3#–`Œ¯Ò‚ÌC—ûê4#l\r\n";
				return cm.sendSimple(text);
			}
		case 1: {
			cm.warp(mapid_list[selection], 4);
		}
		default:
			break;
	}

	return cm.dispose();
}