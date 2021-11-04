// 火狸金融 入場
// コンペイ

var main_menu = Array(
	"#bアジトの説明を聞く。#k",
	"#bアジトに連れて行ってくれ。#k",
	"#b用はない。#k"
);

function select_explain(npc_talk_status) {
	switch (npc_talk_status) {
		case 1:
			{
				var text = "オイラはアジトまで案内してやることができるぜ。しかしアジトはガラの悪い連中がウヨウヨいるからよ、力つけて腹くくってから行くことにしなぁ。アジトにはこの近辺の親分たちを取り仕切っている大親分がいる。アジトにはまあ簡単に行けるんだが、アジトの最上階にある部屋には1日1回しか行けねーぞ。大親分の部屋は恐ろしいところだ。あんまり長居しない方が良いから、短時間でケリつけてくれよ！あの大親分もただもんじゃねーが、大親分に会うまでにも腕っぷしの強いやつらがいるから、やっかいだぜ～。";
				cm.sendSimple(text);
				break;
			}
		default:
			break;
	}

	return cm.dispose();
}

function select_go(npc_talk_status) {
	switch (npc_talk_status) {
		case 1:
			{
				var text = "おお、勇者よ待っていたぞ。このまま奴等を放置すれば取り返しがつかなくなる。その前にお前さんの力で、５階にいる大親分を懲らしめてくれ。くれぐれも油断はすんなよ。大親分は幾人もの猛者が敵わなかった兵だ。だが、お前さんの目を見ているとやってくれると確信が持てたぜ。さあ行くぞ。";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}

function menu_select(npc_talk_status, selection) {
	switch (selection) {
		case 0:
			{
				return select_explain(npc_talk_status);
			}
		case 1:
			{
				return select_go(npc_talk_status);
			}
		default:
			break;
	}
	return cm.dispose();
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
				var mapid = cm.getMapId();
				var text = "用はなんだ。\r\n";
				for (var i = 0; i < main_menu.length; i++) {
					text += "#L" + i + "#" + main_menu[i] + "#l\r\n";
				}
				return cm.sendSimple(text);
			}
		case 1:
			{
				return menu_select(npc_talk_status, selection);
			}
		case 2:
			{
				cm.warp(801040000, 0);
			}
		default:
			break;
	}

	return cm.dispose();
}