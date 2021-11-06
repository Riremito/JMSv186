// カソン
var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "錬金術、そして錬金術師…どちらも重要だが、最も重要なのは錬金術の歴史を担ってきたこのマガティアという偉大な村だ。マガティアの歴史はこれからも守っていかなければならない。君にその力があるか？\r\n";
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