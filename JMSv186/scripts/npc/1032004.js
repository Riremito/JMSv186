// ルイス
// エリニア忍耐

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
				var text = "外へ出たいのか？　辛いのか…このくらいであきらめるなよ。一度ここから出ると、初めからやり直しだということは覚悟しているな。どうだ、出るのか？";
				return cm.sendYesNo(text);
			}
		case 1:
			{
				cm.warp(101000000, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}