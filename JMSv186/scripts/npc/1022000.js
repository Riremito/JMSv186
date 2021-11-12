// コブシを開いて立て
// 戦士転職


var job_1st_level = 10;
var job_1st = 100;

var job_2nd_level = 30;
var job_2nd = Array(
	110,
	120,
	130
);

var job_3rd_level = 70;
var job_3rd = Array(
	111,
	121,
	131
);

var job_4th_level = 120;
var job_4th = Array(
	112,
	122,
	132
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
				// 2次転職可能状態
				if (cm.haveItem(4031012)) {
					// 魔法使い
					// var text = "";
					// 原文ママ
					var text = "おお…無事に帰ってきたね！　君ならそんな試験くらいは簡単にパスすると思ったね。君が本当に強い戦士ということを認めよう。さあ…君をもっと強くしてあげる。その前に…！　君は3つの道の中で一つを選択しなければならない。難しいことかも知れないが…分からないことがあったら聞きなさい。";
					return cm.sendSimple(text);
				}
				// 原文ママ
				var text = "ほう…君は見違えるように成長したな！　この前のひ弱な姿はどこかに行って今は戦士としての威厳が満ち溢れているではないか！　さあ…どうだ？　ここでさらに強くなりたくないかね？　簡単な試験さえパスしたら君をより一層強くしてあげるよ！　やってみないかね？";
				return cm.sendYesNo(text);
			}
		case 2:
			{
				// 2次転職可能状態
				if (cm.haveItem(4031012)) {
					// 原文ママ
					var text = "さあ…決定が終わったら…一番下にある[職業を選択します！]を選択しなさい。\r\n";
					text += "#L" + 1 + "##bソードマンについて説明して下さい。#k#l\r\n"
					text += "#L" + 2 + "##bページについて説明して下さい。#k#l\r\n"
					text += "#L" + 3 + "##bスピアマンについて説明して下さい。#k#l\r\n"
					text += "#L" + 0 + "##b職業を選択します！#k#l\r\n"
					return cm.sendSimple(text);
				}
				// 原文ママ
				var text = "よく考えたな。君は強そうに見えるがそれが本物なのか確認してみる必要がある。簡単なテストだから君なら充分にパスすることができるだろう。さあ…まずここで私の手紙を受け取ってくれ。忘れないように気を付けろよ。";
				return cm.sendSimple(text);
			}
		case 3:
			{
				// 2次転職可能状態
				if (cm.haveItem(4031012)) {
					// 原文ママ
					if (selection == 0) {
						var text = "さあ…心は決めたのか？　2次転職したい職業を選択しなさい。\r\n";
						text += "#L" + 0 + "##bソードマン(Swordman)#k#l\r\n"
						text += "#L" + 1 + "##bページ(Page)#k#l\r\n"
						text += "#L" + 2 + "##bスピアマン(Spearman)#k#l\r\n"
						return cm.sendSimple(text);
					}
					return cm.dispose();
				}
				cm.gainItem(4031008, 1);
				if (!cm.haveItem(4031008)) {
					// error
					var text = "インベントリを開けてください";
					return cm.sendSimple(text);
				}
				// 原文ママ
				var text = "この手紙をペリオン辺り#b#m102020300##kのどこかにいる#b#p1072000##kに伝えるように。忙しい私の代わりに教官の仕事をしてくれる有り難い人だ。手紙を伝えてくれれば君をワシの代わりにテストしてくれる。詳しいことは彼に直接聞けばわかる。それじゃ無事に帰って来てほしい。";
				// 実際はPrev Nextな選択肢
				return cm.sendSimple(text);
			}
		case 4:
			{
				// 2次転職可能状態
				if (cm.haveItem(4031012)) {
					// 原文ママ
					if (selection == 0) {
						var text = "#bソードマン#kで2次転職したいのだな？　一度決めれば他の2次転職職業には転職することができないぞ。その決心…間違いないかね？";
						return cm.sendYesNo(text);
					}
					return cm.dispose();
				}
				return cm.dispose();
			}
		case 5:
			{
				// 2次転職可能状態
				if (cm.haveItem(4031012)) {
					cm.changeJob(110);
					cm.gainItem(4031012, -1);
					// 原文ママ

					var text = "よし！　君はこれから#bソードマン#kだ！　ソードマンは強さを求めながら絶えず闘うもの…決してそのことを忘れずに前に進みなさい。さあ私の力で君をもっと強くしよう。";
					// prev next
					return cm.sendSimple(text);
				}
				return cm.dispose();
			}
		case 6:
			{
				// 原文ママ
				var text = "君に今からソードマンが学べるスキルが書かれている本を与えよう。その本にはさまざまなファイターと係わるスキルが書かれている。それに消費、ETCにアイテムを保管できる数も増えたぞ。それぞれ1ライン増えているだろう。最大HPも増えたしね…一回確認しなさい。";
				return cm.sendSimple(text);
			}
		case 7:
			{
				// sp
				// 原文ママ
				var text = "君に少しの#bSP#kをあげたから、#bスキルメニュー#kを開けてみろ。新たに得た2次スキルをあげることができる。ただしはじめから全てをあげることはできない。他のスキルをある程度上げてこそ学ぶことができるスキルもあるからな。覚えておくように。";
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
		cm.sendOk("Make up your mind and visit me again.");
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		if (cm.getJob() == 0) {
			if (cm.getPlayerStat("LVL") >= 10 && cm.getJob() == 0) {
				cm.sendNext("So you decided to become a #rWarrior#k?");
			} else {
				cm.sendOk("Train a bit more and I can show you the way of the #rWarrior#k.");
				cm.dispose();
			}
		} else {
			if (cm.getPlayerStat("LVL") >= 30 && cm.getJob() == 100) { // WARROPR
				if (cm.getQuestStatus(100003) >= 1) {
					cm.completeQuest(100005);
					if (cm.getQuestStatus(100005) == 2) {
						status = 20;
						cm.sendNext("I see you have done well. I will allow you to take the next step on your long road.");
					} else {
						if (!cm.haveItem(4031008)) {
							cm.gainItem(4031008, 1);
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
					cm.sendOk("Alright, now take this to #bTylus#k.");
				} else {
					cm.sendOk("Hey, #b#h0##k! I need a #bBlack Charm#k. Go and find the Door of Dimension.");
					cm.startQuest(100101);
				}
				cm.dispose();
			} else {
				cm.sendOk("You have chosen wisely.");
				cm.dispose();
			}
		}
	} else if (status == 1) {
		cm.sendNextPrev("It is an important and final choice. You will not be able to turn back.");
	} else if (status == 2) {
		cm.sendYesNo("Do you want to become a #rWarrior#k?");
	} else if (status == 3) {
		if (cm.getJob() == 0) {
			cm.resetStats(35, 4, 4, 4);
			cm.expandInventory(1, 4);
			cm.expandInventory(4, 4);
			cm.changeJob(100); // WARRIOR
		}
		cm.gainItem(1402001, 1);
		cm.sendOk("So be it! Now go, and go with pride.");
		cm.dispose();
	} else if (status == 11) {
		cm.sendNextPrev("You may be ready to take the next step as a #rFighter#k, #rPage#k or #rSpearman#k.")
	} else if (status == 12) {
		cm.askAcceptDecline("But first I must test your skills. Are you ready?");
	} else if (status == 13) {
		cm.gainItem(4031008, 1);
		cm.startQuest(100003);
		cm.sendOk("Go see the #bJob Instructor#k near Perion. He will show you the way.");
		cm.dispose();
	} else if (status == 21) {
		cm.sendSimple("What do you want to become?#b\r\n#L0#Fighter#l\r\n#L1#Page#l\r\n#L2#Spearman#l#k");
	} else if (status == 22) {
		var jobName;
		if (selection == 0) {
			jobName = "Fighter";
			job = 110; // FIGHTER
		} else if (selection == 1) {
			jobName = "Page";
			job = 120; // PAGE
		} else {
			jobName = "Spearman";
			job = 130; // SPEARMAN
		}
		cm.sendYesNo("Do you want to become a #r" + jobName + "#k?");
	} else if (status == 23) {
		cm.changeJob(job);
		cm.gainItem(4031012, -1);
		cm.sendOk("So be it! Now go, and go with pride.");
		cm.dispose();
	}
}
*/