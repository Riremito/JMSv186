// 素公パンダ 道場入口
// ベルト取得処理がおかしい

var status = -1;
var sel;
var mapid;

function start() {
	mapid = cm.getMapId();

	if (mapid == 925020001) {
		var text = "俺の師匠は武陵で一番強いんだ！　そんなお方にお前が挑むだぁ？　後で後悔すんなよ\r\n";
		text += "#L" + 0 + "##b一人で挑戦してみる#k#l\r\n"
		text += "#L" + 1 + "##b一緒に挑戦してみる#k#l\r\n\r\n"
		text += "#L" + 2 + "##bベルトが欲しい#k#l\r\n"
		text += "#L" + 3 + "##b修練点数をリセットしたい#k#l\r\n"
		// JMS オリジナル?
		text += "#L" + 4 + "##b勲章が欲しい#k#l\r\n"
		text += "#L" + 5 + "##b武陵道場ってなに？#k#l\r\n"
		cm.sendSimple(text);
	} else if (isRestingSpot(mapid)) {
		var text = "ここまで無事に来たなんてな…。でも、ここから先は簡単じゃねーぞ。どうだ？　続けて挑戦すんのか？\r\n";
		text += "#L" + 0 + "##b引き続き挑戦する#k#l\r\n"
		text += "#L" + 1 + "##b外へ出る#k#l\r\n"
		text += "#L" + 2 + "##bここまでの成績を記録する#k#l\r\n"
		cm.sendSimple(text);
	} else {
		cm.sendYesNo("What? You're ready to quit already? You just need to move on to the next level. Are you sure you want to quit?");
	}
}

function action(mode, type, selection) {
	if (mapid == 925020001) {
		if (mode == 1) {
			status++;
		} else {
			cm.dispose();
			return;
		}
		if (status == 0) {
			sel = selection;

			if (sel == 5) {
				cm.sendNext("My master is the most powerful individual in Mu Lung, and he is responsible for erecting this amazing Mu Lung Training Tower. Mu Lung Training Tower is a colossal training facility that consists of 38 floors. Each floor represents additional levels of difficulty. Of course, with your skills, reaching the top floor will be impossible...");
				cm.dispose();
			} else if (sel == 3) {
				cm.sendYesNo("You know if you reset your training points, then it'll return to 0, right? I can honestly say that it's not necessarily a bad thing. Once you reset your training points and start over again, then you'll be able to receive the belts once more. Do you want to reset your training points?");
			} else if (sel == 2) {
				cm.sendSimple("お前の修練点数は #b" + cm.getDojoPoints() + "#k 点だな。 師匠は才能のあるヤツが好きなんだ。修練点数が一定レベル以上溜まれば、点数によってベルトがもらえるぞ。\r\n\r\n#L0##i1132000:# #t1132000##l \n\r #L1##i1132001:# #t1132001##l \n\r #L2##i1132002:# #t1132002##l \n\r #L3##i1132003:# #t1132003##l \n\r #L4##i1132004:# #t1132004##l");
			} else if (sel == 1) {
				if (cm.getParty() != null) {
					if (cm.isLeader()) {
						cm.sendOk("Would you like to Enter now?");
					} else {
						cm.sendOk("Hey, you're not even a leader of your party. What are you doing trying to sneak in? Tell your party leader to talk to me if you want to enter the premise...");
					}
				}
			} else if (sel == 0) {
				if (cm.getParty() != null) {
					cm.sendOk("Please leave your party.");
					cm.dispose();
				}
				var record = cm.getQuestRecord(150000);
				var data = record.getCustomData();

				if (data != null) {
					cm.warp(get_restinFieldID(parseInt(data)), 0);
					record.setCustomData(null);
				} else {
					cm.start_DojoAgent(true, false);
				}
				cm.dispose();
				// cm.sendYesNo("The last time you took the challenge yourself, you were able to reach Floor #18. I can take you straight to that floor, if you want. Are you interested?");
			}
		} else if (status == 1) {
			if (sel == 3) {
				cm.setDojoRecord(true);
				cm.sendOk("I have resetted your training points to 0.");
			} else if (sel == 2) {
				var record = cm.getDojoRecord();
				var required = 0;

				switch (record) {
					case 0:
						required = 200;
						break;
					case 1:
						required = 1800;
						break;
					case 2:
						required = 4000;
						break;
					case 3:
						required = 9200;
						break;
					case 4:
						required = 17000;
						break;
				}

				if (record == selection && cm.getDojoPoints() >= required) {
					var item = 1132000 + record;
					if (cm.canHold(item)) {
						cm.gainItem(item, 1);
						cm.setDojoRecord(false);
					} else {
						cm.sendOk("Please check if you have any available slot in your inventory.");
					}
				} else {
					var text = "#i1132001:# #t1132001#をもらうためには #b35レベル以上#kじゃなきゃいけなくて、累積修練点数 #b1800点#kが必要だ。\r\n\r\n";
					text += "お前がこのベルトをもらうには、修練点数を #r" + (1800 - cm.getDojoPoints()) + "#k点溜めねーとダメだな。";
					cm.sendOk(text);
				}
				cm.dispose();
			} else if (sel == 1) {
				cm.start_DojoAgent(true, true);
				cm.dispose();
			}
		}
	} else if (isRestingSpot(mapid)) {
		if (mode == 1) {
			status++;
		} else {
			cm.dispose();
			return;
		}

		if (status == 0) {
			sel = selection;

			if (sel == 0) {
				cm.dojoAgent_NextMap(true, true);
				//cm.getQuestRecord(150000).setCustomData(null);
				cm.dispose();
			} else if (sel == 1) {
				cm.askAcceptDecline("Do you want to quit? You really want to leave here?");
			} else if (sel == 2) {
				if (cm.getParty() == null) {
					var stage = get_stageId(cm.getMapId());

					cm.getQuestRecord(150000).setCustomData(stage);
					cm.sendOk("I have just recorded your progress. The next time you get here, I'll sent you directly to this level.");
					cm.dispose();
				} else {
					cm.sendOk("Hey.. you can't record your progress with a team...");
					cm.dispose();
				}
			}
		} else if (status == 1) {
			if (sel == 1) {
				if (cm.isLeader()) {
					cm.warpParty(925020002);
				} else {
					cm.warp(925020002);
				}
			}
			cm.dispose();
		}
	} else {
		if (mode == 1) {
			if (cm.isLeader()) {
				cm.warpParty(925020002);
			} else {
				cm.warp(925020002);
			}
		}
		cm.dispose();
	}
}

function get_restinFieldID(id) {
	var idd = 925020002;
	switch (id) {
		case 1:
			idd = 925020600;
		case 2:
			idd = 925021200;
		case 3:
			idd = 925021800;
		case 4:
			idd = 925022400;
		case 5:
			idd = 925023000;
		case 6:
			idd = 925023600;
	}
	for (var i = 0; i < 15; i++) {
		var canenterr = true;
		for (var x = 1; x < 39; x++) {
			var map = cm.getMap(925020000 + 100 * x + i);
			if (map.getCharactersSize() > 0) {
				canenterr = false;
				break;
			}
		}
		if (canenterr) {
			idd += i;
			break;
		}
	}
	return idd;
}

function get_stageId(mapid) {
	if (mapid >= 925020600 && mapid <= 925020614) {
		return 1;
	} else if (mapid >= 925021200 && mapid <= 925021214) {
		return 2;
	} else if (mapid >= 925021800 && mapid <= 925021814) {
		return 3;
	} else if (mapid >= 925022400 && mapid <= 925022414) {
		return 4;
	} else if (mapid >= 925023000 && mapid <= 925023014) {
		return 5;
	} else if (mapid >= 925023600 && mapid <= 925023614) {
		return 6;
	}
	return 0;
}

function isRestingSpot(id) {
	return (get_stageId(id) > 0);
}