// ダリア

var medal_list = Array(
	[1142005, "伝説的な狩人", "レジェンドハンター"],
	[1142006, "メイプルアイドルスター", "メイプルアイドルスター"],
	[1142007, "ホーンテイルスレイヤー", "ホーンテイルスレイヤー"],
	[1142008, "ピンクビーン スレイヤー", "ピンクビーンスレイヤー"],
	[1142030, "街の寄付王", "リス港口 寄付王"]
);

function DebugGetMedal(index) {
	if (cm.haveItem(medal_list[index][0])) {
		return false;
	}
	cm.gainItem(medal_list[index][0], 1);
	var player_name = cm.getPlayer().getName();
	cm.worldMessage(6, player_name + "さんが " + medal_list[index][2] + "の称号を獲得しました。");
	return true;
}

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
				var text = "どの勲章のランキングを見たいのだ？\r\n";
				for (var i = 0; i < medal_list.length; i++) {
					text += "#L" + i + "##b" + medal_list[i][1] + "#k#l\r\n";
				}
				return cm.sendSimple(text);
			}
		case 1:
			{
				// とりあえず勲章をあげてメッセージを流しておく
				DebugGetMedal(selection);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}