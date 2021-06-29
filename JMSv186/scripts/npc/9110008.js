// npc ペリー

// ～～
var cost = 1500;
function start() {
	cm.sendYesNo("いらっしゃーーいッ！オラはここジパングとビクトリアアイランドとを往復するペリカンさ、運賃さえ払ってくれればビクトリアアイランドまでひとっ飛び出行くよ。運賃（片道）" + cost + "メル払うかい？");
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.sendNext("エラーメッセージ");
	} else {
		if (cm.getMeso() >= cost) {
			cm.gainMeso(-cost);
			cm.warp(103000000, "gm02");
		} else {
			cm.sendNext("エラーメッセージ");
		}
	}
	cm.dispose();
}