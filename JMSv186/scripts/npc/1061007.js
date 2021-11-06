// 崩れている像
// スリーピーウッド忍耐

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
				var text = "石像に手を出してみると不思議な光に全身を包まれ、中に吸い込まれるような感覚に襲われました。このままスリーピーウッドに戻りますか？？\r\n";
				return cm.sendYesNo(text);
			}
		case 1:
			{
				cm.warp(105040300, 0)
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}