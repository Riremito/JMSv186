// 釣り場管理人

var tips = Array(
	"釣り場に入る",
	"餌を買う",
	"釣り用の椅子を買う",
	"釣りの説明",
	"デバッグモード"
);

function EnterFishingLagoon(debug_mode) {
	// 釣竿または高級釣竿が必要
	if (!cm.haveItem(5340000) && !cm.haveItem(5340001)) {
		// 高級釣竿獲得
		if (!debug_mode) {
			return cm.sendSimple("[test]釣竿がありません");
		}
		cm.gainItem(5340001, 1);
	}
	// 釣り用の椅子が必要
	if (!cm.haveItem(3011000)) {
		// 釣り用椅子獲得
		if (!debug_mode) {
			return cm.sendSimple("[test]釣り用の椅子ががありません");
		}
		cm.gainItem(3011000, 1);
	}
	// 餌または高級餌
	if (!cm.haveItem(2300000) && !cm.haveItem(2300001)) {
		// 釣り用椅子獲得
		if (!debug_mode) {
			return cm.sendSimple("[test]餌ががありません");
		}
		cm.gainItem(2300001, 120);
	}
	cm.saveLocation("FISHING");
	cm.warp(741000200, "out00");
	return cm.dispose();
}

function MainMenu(selection) {
	switch (tips[selection]) {
		case "釣り場に入る":
			{
				return EnterFishingLagoon(false);
			}
		case "餌を買う":
			{
				if (cm.getMeso() < 3000) {
					var text = "[test]メルが足りません"
					return cm.sendSimple(text);
				}
				if (!cm.canHold(2300000, 120)) {
					var text = "[test]アイテム欄が不足しています"
					return cm.sendSimple(text);
				}
				cm.gainMeso(-3000);
				cm.gainItem(2300000, 120);
				return cm.dispose();
			}
		case "釣り用の椅子を買う":
			{
				if (cm.haveItem(3011000)) {
					var text = "[test]既に持っています"
					return cm.sendSimple(text);
				}
				if (cm.getMeso() < 50000) {
					var text = "[test]メルが足りません"
					return cm.sendSimple(text);
				}

				cm.gainMeso(-50000);
				cm.gainItem(3011000, 1);
				return cm.dispose();
			}
		case "釣りの説明":
			{
				var text = "釣竿、餌、釣り用の椅子を所持し、なおかつレベル11以上であれば釣り場に入場することができます、1分毎には何かを釣ることができるでしょう。釣り結果を知りたければ、釣り爺に話しかけてください。"
				return cm.sendOk(text);
			}
		case "デバッグモード":
			{
				return EnterFishingLagoon(true);
			}
		default:
			{
				var text = "未実装";
				return cm.sendSimple(text);
			}
	}
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
				var text = "こんな遠いところまで、何の用ですか。\r\n";
				for (var i = 0; i < tips.length; i++) {
					/*
					if (cm.getPlayer().isGM() && tips[i] == "デバッグモード") {
						continue;
					}
					*/
					text += "#L" + i + "##b" + tips[i] + "\r\n";
				}
				return cm.sendSimple(text);
			}
		case 1:
			{
				return MainMenu(selection);
			}
		default:
			break;
	}

	return cm.dispose();
}