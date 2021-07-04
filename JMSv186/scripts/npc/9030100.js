// スクルジ
var status = -1;

function selectJob(jobid, level) {
	text = "職業選択\r\n";
	text += "#L" + 0 + "##b" + "レベルアップ" + "#l#k\r\n";

	switch (jobid) {
		// 初心者
		case 0:
			{
				if (level >= 8) {
					text += "#L" + 200 + "##b" + "魔法使い" + "#l#k\r\n";

					if (level >= 10) {
						text += "#L" + 100 + "##b" + "戦士" + "#l#k\r\n";
						text += "#L" + 300 + "##b" + "弓使い" + "#l#k\r\n";
						text += "#L" + 400 + "##b" + "盗賊" + "#l#k\r\n";
						text += "#L" + 500 + "##b" + "海賊" + "#l#k\r\n";
					}
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		// シグナス
		case 1000:
			{
				if (level >= 10) {
					text += "#L" + 1100 + "##b" + "ソウルマスター" + "#l#k\r\n";
					text += "#L" + 1200 + "##b" + "フレイムウィザード" + "#l#k\r\n";
					text += "#L" + 1300 + "##b" + "ウィンドシューター" + "#l#k\r\n";
					text += "#L" + 1400 + "##b" + "ナイトウォーカー" + "#l#k\r\n";
					text += "#L" + 1500 + "##b" + "ストライカー" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		// アラン
		case 2000:
			{
				if (level >= 10) {
					text += "#L" + 2100 + "##b" + "アラン" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2100:
			{
				if (level >= 30) {
					text += "#L" + 2110 + "##b" + "アラン2次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2110:
			{
				if (level >= 70) {
					text += "#L" + 2111 + "##b" + "アラン3次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2111:
			{
				if (level >= 120) {
					text += "#L" + 2112 + "##b" + "アラン4次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		// エヴァン
		case 2001:
			{
				if (level >= 10) {
					text += "#L" + 2200 + "##b" + "エヴァン" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2200:
			{
				if (level >= 20) {
					text += "#L" + (jobid + 10) + "##b" + "エヴァン2次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2210:
			{
				if (level >= 30) {
					text += "#L" + (jobid + 1) + "##b" + "エヴァン3次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2211:
			{
				if (level >= 40) {
					text += "#L" + (jobid + 1) + "##b" + "エヴァン4次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2212:
			{
				if (level >= 60) {
					text += "#L" + (jobid + 1) + "##b" + "エヴァン5次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2213:
			{
				if (level >= 70) {
					text += "#L" + (jobid + 1) + "##b" + "エヴァン6次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2214:
			{
				if (level >= 80) {
					text += "#L" + (jobid + 1) + "##b" + "エヴァン7次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2215:
			{
				if (level >= 100) {
					text += "#L" + (jobid + 1) + "##b" + "エヴァン8次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2216:
			{
				if (level >= 120) {
					text += "#L" + (jobid + 1) + "##b" + "エヴァン9次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 2217:
			{
				if (level >= 160) {
					text += "#L" + (jobid + 1) + "##b" + "エヴァン10次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		// デュアルブレイド
		case 430:
			{
				if (level >= 30) {
					text += "#L" + (jobid + 1) + "##b" + "デュアルブレイド2次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 431:
			{
				if (level >= 55) {
					text += "#L" + (jobid + 1) + "##b" + "デュアルブレイド2.5次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 432:
			{
				if (level >= 70) {
					text += "#L" + (jobid + 1) + "##b" + "デュアルブレイド3次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 433:
			{
				if (level >= 120) {
					text += "#L" + (jobid + 1) + "##b" + "デュアルブレイド4次" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		// シグナス2次
		case 1100:
		case 1200:
		case 1300:
		case 1400:
		case 1500:
			{
				if (level >= 30) {
					text += "#L" + (jobid + 10) + "##b" + "転職" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		/*
		case 1111:
		case 1211:
		case 1311:
		case 1411:
		case 1511:
		*/
		case 1111:
			{
				if (level >= 120) {
					text += "#L" + (jobid + 1) + "##b" + "転職" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 100:
			{
				if (level >= 30) {
					text += "#L" + 110 + "##b" + "ソードマン" + "#l#k\r\n";
					text += "#L" + 120 + "##b" + "ページ" + "#l#k\r\n";
					text += "#L" + 130 + "##b" + "スピアマン" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		// 3次転職
		case 110:
		case 120:
		case 130:
		case 210:
		case 220:
		case 230:
		case 310:
		case 320:
		case 410:
		case 420:
		case 510:
		case 520:
		case 1110:
		case 1210:
		case 1310:
		case 1410:
		case 1510:
			{
				if (level >= 70) {
					text += "#L" + (jobid + 1) + "##b" + "3次転職" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		// 4次転職
		case 111:
		case 121:
		case 131:
		case 211:
		case 221:
		case 231:
		case 311:
		case 321:
		case 411:
		case 421:
		case 511:
		case 521:
			{
				if (level >= 120) {
					text += "#L" + (jobid + 1) + "##b" + "4次転職" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		// 2次転職
		case 200:
			{
				if (level >= 30) {
					text += "#L" + 210 + "##b" + "ウィザード(火、毒)" + "#l#k\r\n";
					text += "#L" + 220 + "##b" + "ウィザード(雷、氷)" + "#l#k\r\n";
					text += "#L" + 230 + "##b" + "クレリック" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}

		case 300:
			{
				if (level >= 30) {
					text += "#L" + 310 + "##b" + "ハンター" + "#l#k\r\n";
					text += "#L" + 320 + "##b" + "レンジャー" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 400:
			{
				if (level >= 20) {
					text += "#L" + 430 + "##b" + "セミデュアル" + "#l#k\r\n";

					if (level >= 30) {
						text += "#L" + 410 + "##b" + "アサシン" + "#l#k\r\n";
						text += "#L" + 420 + "##b" + "シーフ" + "#l#k\r\n";
					}
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		case 500:
			{
				if (level >= 30) {
					text += "#L" + 510 + "##b" + "インファイター" + "#l#k\r\n";
					text += "#L" + 520 + "##b" + "ガンスリンガー" + "#l#k\r\n";
					cm.sendSimple(text);
					return true;
				}
				break;
			}
		default:
			break;
	}

	text = "";

	return false;
}

function LearnSkills(jobid) {
	switch (jobid) {
		case 222:
			{
				cm.teachSkill(2221000, 0);
				cm.teachSkill(2221003, 0);
				cm.teachSkill(2221004, 0);
				cm.teachSkill(2221007, 0);
				cm.teachSkill(2221008, 0);
				break;
			}
		case 232:
			{
				cm.teachSkill(2321000, 0);
				cm.teachSkill(2321003, 0);
				cm.teachSkill(2321004, 0);
				cm.teachSkill(2321006, 0);
				cm.teachSkill(2321007, 0);
				cm.teachSkill(2321008, 0);
				cm.teachSkill(2321009, 0);
				break;
			}
		default:
			break;
	}
	return false;
}

function action(mode, type, selection) {
	var id = cm.getNpc();
	var mapid = cm.getMapId();
	var text = "デバッグ情報\r\n";
	text += "#p" + id + "#\r\n";
	text += "NPC ID = #b" + id + "#k\r\n";
	text += "#m" + mapid + "#\r\n";
	text += "Map ID = #b" + mapid + "#k\r\n";

	var jobid = cm.getJob();
	var level = cm.getPlayer().getLevel();

	if (mode == 1) {
		status++;
	}
	else {
		status--;
	}

	if (status == 0) {
		if (selectJob(jobid, level)) {
			return;
		}
	}

	if (status == 1 && selection > 0) {
		cm.getPlayer().changeJob(selection);
		//LearnSkills(selection);
	}
	else {
		if (status != 1 && level <= 200) {
			cm.getPlayer().gainExp(500000000, true, false, true);
		}
		else {
			cm.sendOk(text);
		}
	}

	cm.dispose();
	return;
}