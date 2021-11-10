// ダークロード
// 盗賊転職

var job_1st_level = 10;
var job_1st = 400;

var job_2nd_level = 30;
var job_2nd = Array(
	410,
	420
);

var job_3rd_level = 70;
var job_3rd = Array(
	411,
	421
);

var job_4th_level = 120;
var job_4th = Array(
	412,
	422
);

var npc_talk_status = 0;
// 転職中に条件が切り替わってしまうためNPC会話開始時に職業IDを保持する
var jobid = -1;

/*
	マイが言ってた人が君か？#h0#…ふむ。彼女に言われたどおりに、かなり資質があるような若造だな…おい、盗賊になりたいって？盗賊について分かってるか？
	盗賊というとちっぽけなこそ泥を思う人もいるが、実は違うんだ。メイプルワールドの盗賊は闇の中で鋭い短刀と手裏剣で戦う人たちだ。もしかしたら正々堂々としてない部分があるかもしれない。だが、それも盗賊の本質。否定する必要はないだろ。
	職業としての盗賊は素早くて強力なスキルで敵を攻撃するんだ。体力は弱いほうだが、その分動きが早いためモンスターを避けるのも難しくない。強力な運でクリティカルな攻撃もうまいしな。
	どうだ？盗賊の道を共に歩むか？君が盗賊の道に進むと決めたら転職官の特権で君をすぐに#bカニングシティー、盗賊のアジト#kに招待する…隠密な所だから光栄に思うんだな。#rだが、他の職業がもっと良かったら？それなら断ってくれ。盗賊じゃない他の道を推薦するから#k。
*/

function JobChange1(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 1) {
			// BB後
			var text = "まだ心の準備ができてないか？";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;

	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "盗賊のアジトへようこそ。こうでもしないと入口もなかなか分かり辛い所だ。ふふ、出る時に門をよく憶えておいてくれ。じゃ、盗賊になる準備はできたか？";
				return cm.sendYesNo(text);
			}
		case 2:
			{
				cm.resetStats(4, 25, 4, 4);
				cm.changeJob(job_1st);
				// BB後
				var text = "これで君は盗賊になった。盗賊のスキルが使用できるようになったからスキルウィンドウを開いてみてくれ。レベルを上げるとより多くのスキルを覚えることができるんだ。";
				return cm.sendSimple(text);
			}
		case 3:
			{
				// BB後
				var text = "スキルだけでは物足りないな？ステータスも盗賊らしくならないと本物の盗賊じゃないな。盗賊はLUKがメインステータスで、DEXが補助ステータスになる。ステータスの上げ方が分からなかったら#b自動配分#kを使用するといい。";
				return cm.sendSimple(text);
			}
		case 4:
			{
				// BB後
				var text = "そして、プレゼントがあるぞ。君の装備とETCアイテムの保管箱の個数を増やしてあげたんだ。インベントリが広いと旅が楽しくなるもの。ふふ…";
				return cm.sendSimple(text);
			}
		case 5:
			{
				// BB後
				var text = "さて！俺が君に教えられるのはここまでだ。君に役立つような武器もいくつか渡したから旅をしながら自身を鍛えてみてくれ。";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}

function JobChange2(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 1) {
			// BB後
			var text = "まだ心の準備ができてないか？";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;

	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "2次転職テスト\r\n";
				for (var i = 0; i < job_2nd.length; i++) {
					text += "#L" + job_2nd[i] + "##b" + job_2nd[i] + "#k#l\r\n";
				}
				return cm.sendYesNo(text);
			}
		case 2:
			{
				var next_job = selection;
				cm.changeJob(next_job);
				// BB後
				var text = "2次転職";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}
function JobChange3(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 1) {
			// BB後
			var text = "まだ心の準備ができてないか？";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;

	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "3次転職テスト\r\n";
				for (var i = 0; i < job_3rd.length; i++) {
					text += "#L" + job_3rd[i] + "##b" + job_3rd[i] + "#k#l\r\n";
				}
				return cm.sendYesNo(text);
			}
		case 2:
			{
				var next_job = selection;
				cm.changeJob(next_job);
				// BB後
				var text = "3次転職";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}
function JobChange4(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 1) {
			// BB後
			var text = "まだ心の準備ができてないか？";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;

	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "4次転職テスト\r\n";
				for (var i = 0; i < job_4th.length; i++) {
					text += "#L" + job_4th[i] + "##b" + job_4th[i] + "#k#l\r\n";
				}
				return cm.sendYesNo(text);
			}
		case 2:
			{
				var next_job = selection;
				cm.changeJob(next_job);
				// BB後
				var text = "4次転職";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}

function JobCheck(jobid_list, jobid) {
	for (var i = 0; i < jobid_list.length; i++) {
		if (jobid_list[i] == jobid) {
			return true;
		}
	}

	return false;
}

function JobChange(mode, type, selection) {
	// 職業IDとレベルを取得
	if (jobid == -1) {
		jobid = cm.getJob();
	}
	var level = cm.getPlayer().getLevel();

	// 1次転職条件
	if (job_1st_level <= level && jobid == 0) {
		JobChange1(mode, type, selection);
		return true;
	}

	// 2次転職条件
	if (job_2nd_level <= level && job_1st == jobid) {
		JobChange2(mode, type, selection);
		return true;
	}

	// 3次転職条件
	if (job_3rd_level <= level && JobCheck(job_2nd, jobid)) {
		JobChange3(mode, type, selection);
		return true;
	}

	// 4次転職条件
	if (job_4th_level <= level && JobCheck(job_3rd, jobid)) {
		JobChange4(mode, type, selection);
		return true;
	}

	return false;
}

function action(mode, type, selection) {
	if (JobChange(mode, type, selection)) {
		return;
	}
	/*
	// 職業IDとレベルを取得
	var jobid = cm.getJob();
	var level = cm.getPlayer().getLevel();

	if (mode != 1) {
		if (npc_talk_status == 1) {
			// BB後
			var text = "まだ心の準備ができてないか？";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;

	switch (npc_talk_status) {
		case 1:
			{
				if (jobid == 0 && level >= 10) {
					// BB後
					var text = "盗賊のアジトへようこそ。こうでもしないと入口もなかなか分かり辛い所だ。ふふ、出る時に門をよく憶えておいてくれ。じゃ、盗賊になる準備はできたか？";
					return cm.sendYesNo(text);
				}
				// BB後
				var text = "隠密な会話…？隠密な行動が特技の盗賊だが、それは敵と戦う程度で充分だ。";
				// BB前?
				// 冒険も強くなることも良いが楽しみながら暮したくないか…？　我々と一緒に世の中を楽しみながら生きて行くのはどうだ？　本当に面白いぞ？
				cm.sendSimple(text);
				return cm.dispose();
			}
		case 2:
			{
				cm.resetStats(4, 25, 4, 4);
				//cm.expandInventory(1, 4);
				//cm.expandInventory(4, 4);
				// 盗賊s
				cm.changeJob(400);
				//if (cm.getQuestStatus(1048) == 1) {
				//	cm.forceCompleteQuest(1048);
				//}
				// BB後
				var text = "これで君は盗賊になった。盗賊のスキルが使用できるようになったからスキルウィンドウを開いてみてくれ。レベルを上げるとより多くのスキルを覚えることができるんだ。";
				return cm.sendSimple(text);
			}
		case 3:
			{
				// BB後
				var text = "スキルだけでは物足りないな？ステータスも盗賊らしくならないと本物の盗賊じゃないな。盗賊はLUKがメインステータスで、DEXが補助ステータスになる。ステータスの上げ方が分からなかったら#b自動配分#kを使用するといい。";
				return cm.sendSimple(text);
			}
		case 4:
			{
				// BB後
				var text = "そして、プレゼントがあるぞ。君の装備とETCアイテムの保管箱の個数を増やしてあげたんだ。インベントリが広いと旅が楽しくなるもの。ふふ…";
				return cm.sendSimple(text);
			}
		case 5:
			{
				// BB後
				var text = "さて！俺が君に教えられるのはここまでだ。君に役立つような武器もいくつか渡したから旅をしながら自身を鍛えてみてくれ。";
				return cm.sendSimple(text);
			}
		default:
			break;
	}
	*/
	var text = "隠密な会話…？隠密な行動が特技の盗賊だが、それは敵と戦う程度で充分だ。";
	cm.sendSimple(text);
	return cm.dispose();
}

/*
var status = 0;
var job;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0 && status == 2) {
		cm.sendOk("You know there is no other choice...");
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		if (cm.getJob() >= 400 && cm.getJob() <= 434 && cm.getQuestStatus(2351) == 1) {
			cm.forceCompleteQuest(2351);
			cm.gainItem(1032076, 1); //owl earring
		}
		if (cm.getJob() == 0) {
			if (cm.getPlayerStat("LVL") >= 10 && cm.getJob() == 0)
				cm.sendNext("So you decided to become a #rThief#k?");
			else {
				cm.sendOk("Train a bit more and I can show you the way of the #rThief#k.")
				cm.dispose();
			}
		} else {
			if (cm.getPlayerStat("LVL") >= 30 && cm.getJob() == 400) {
				if (cm.getQuestStatus(100009) >= 1) {
					cm.completeQuest(100011);
					if (cm.getQuestStatus(100011) == 2) {
						status = 20;
						cm.sendNext("I see you have done well. I will allow you to take the next step on your long road.");
					} else {
						if (!cm.haveItem(4031011)) {
							cm.gainItem(4031011, 1);
						}
						cm.sendOk("Go and see the #rJob Instructor#k.")
						cm.dispose();
					}
				} else {
					status = 10;
					cm.sendNext("The progress you have made is astonishing.");
				}
			} else if (cm.getQuestStatus(100100) == 1) {
				cm.completeQuest(100101);
				if (cm.getQuestStatus(100101) == 2) {
					cm.sendOk("Alright, now take this to #bArec#k.");
				} else {
					cm.sendOk("Hey, #b#h0##k! I need a #bBlack Charm#k. Go and find the Door of Dimension.");
					cm.startQuest(100101);
				}
				cm.dispose();
			} else {
				cm.sendNext("冒険も強くなることも良いが楽しみながら暮したくないか…？　我々と一緒に世の中を楽しみながら生きて行くのはどうだ？　本当に面白いぞ？");
				cm.dispose();
			}
		}
	} else if (status == 1) {
		cm.sendNextPrev("It is an important and final choice. You will not be able to turn back.");
	} else if (status == 2) {
		cm.sendYesNo("Do you want to become a #rThief#k?");
	} else if (status == 3) {
		if (cm.getJob() == 0) {
			cm.resetStats(4, 25, 4, 4);
			cm.expandInventory(1, 4);
			cm.expandInventory(4, 4);
			cm.changeJob(400); // THIEF
			if (cm.getQuestStatus(2351) == 1) {
				cm.forceCompleteQuest(2351);
				cm.gainItem(1032076, 1); //owl earring
			}
		}
		cm.gainItem(1332063, 1);
		cm.gainItem(1472000, 1);
		cm.gainItem(2070015, 500);
		cm.sendOk("So be it! Now go, and go with pride.");
		cm.dispose();
	} else if (status == 11) {
		cm.sendNextPrev("You may be ready to take the next step as a #rAssassin#k or #rBandit#k.");
	} else if (status == 12) {
		cm.askAcceptDecline("But first I must test your skills. Are you ready?");
	} else if (status == 13) {
		cm.startQuest(100009);
		cm.gainItem(4031011, 1);
		cm.sendOk("Go see the #bJob Instructor#k somewhere in the city. He will show you the way.");
		cm.dispose();
	} else if (status == 21) {
		cm.sendSimple("What do you want to become?#b\r\n#L0#Assassin#l\r\n#L1#Bandit#l#k");
	} else if (status == 22) {
		var jobName;
		if (selection == 0) {
			jobName = "Assassin";
			job = 410; // ASSASIN
		} else {
			jobName = "Bandit";
			job = 420; // BANDIT
		}
		cm.sendYesNo("Do you want to become a #r" + jobName + "#k?");
	} else if (status == 23) {
		cm.changeJob(job);
		cm.gainItem(4031012, -1);
		cm.sendOk("So be it! Now go, my servant.");
		cm.dispose();
	}
}
*/