// ケロベン
// 生命の洞窟入口

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "人間じゃないか。俺がここを守っている限り、人間は一歩も入れないぞ。痛い目に会いたくなければ早く帰りな！\r\n";
				// デバッグモード
				// 大きい巣の峰
				text += "#L" + 240040600 + "##r#m" + 240040600 + "##k#l\r\n"
				// 洞窟の入口
				text += "#L" + 240050000 + "##r#m" + 240050000 + "##k#l\r\n"
				return cm.sendSimple(text);
			}
		case 1:
			{
				var mapid = selection;
				cm.warp(mapid, 0);
				// 大きい巣の峰
				//cm.warp(240040600, "st00");
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}