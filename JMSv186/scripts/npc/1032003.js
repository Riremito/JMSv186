// シェイン
// エリニア忍耐

var npc_talk_status = 0;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "#bサビトラマ#kから頼まれて薬草を取りに来ただって？　だが、ここは父から譲り受けた大切な場所なんだよ…。本当なら誰も入れる訳にはいけないんだが、#r6000#k メルさえ払えば話は違うぞ。どうだ、メルを払うかい？";
				return cm.sendYesNo(text);
			}
		case 2:
			{
				var text = "デバッグモード\r\n";
				// cm.getQuestStatus(2050) == 1
				text += "#L" + 101000100 + "##r#m" + 101000100 + "##k#l\r\n";
				text += "#L" + 101000101 + "##r#m" + 101000101 + "##k#l\r\n";
				// cm.getQuestStatus(2051) == 1
				text += "#L" + 101000102 + "##r#m" + 101000102 + "##k#l\r\n";
				text += "#L" + 101000103 + "##r#m" + 101000103 + "##k#l\r\n";
				text += "#L" + 101000104 + "##r#m" + 101000104 + "##k#l\r\n";
				return cm.sendSimple(text);

			}
		case 3:
			{
				var mapid = selection;
				cm.warp(mapid, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}