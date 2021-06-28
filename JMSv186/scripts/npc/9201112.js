var status = -1;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	if (cm.getMapId() == 803000505  || cm.getMapId() == 910000000) {
		switch (status) {
			case 0:
				{
					var text = "さぁ…何をするんだ？\r\n";
					text += "#L" + 1 + "##b" + "ダンジョン入場（難易度：ノーマル・遠征隊用）" + "#l#k\r\n";
					text += "#L" + 2 + "##b" + "ダンジョン入場（難易度：ハード・遠征隊用）" + "#l#k\r\n";
					text += "#L" + 3 + "##b" + "ボスモンスター対戦（ソロ）" + "#l#k\r\n";
					text += "#L" + 4 + "##b" + "ボスモンスター対戦（グループ）" + "#l#k\r\n";
					text += "#L" + 5 + "##b" + "フリマ入口へ" + "#l#k\r\n";
					cm.sendSimple(text);
					return;
				}
			case 1:
				{
					if (selection == 3) {
						cm.sendYesNo("クリムゾンウッドの聖地に眠る敵と一人で戦ってみるか？");
						return;
					}
					else if (selection == 5) {
						cm.warp(910000000, "out00");
					}
					else {
						cm.sendOk("ボスモンスター対戦（ソロ）を選択してください");
					}
					break;
				}
			case 2:
				{
					var bossmap = cm.getMap(803100000);
					if (cm.getPlayerCount(803100000) <= 0) {
						bossmap.resetFully();
					}
					cm.warp(803100000, "sp");
					break;
				}
			default:
				break;
		}
	}
	else {
		var id = cm.getNpc();
		var mapid = cm.getMapId();
		var text = "デバッグ情報\r\n";
		text += "#p" + id + "#\r\n";
		text += "NPC ID = #b" + id + "#k\r\n";
		text += "#m" + mapid + "#\r\n";
		text += "Map ID = #b" + mapid + "#k\r\n";
	
		cm.sendOk(text);
	}

	cm.dispose();
}