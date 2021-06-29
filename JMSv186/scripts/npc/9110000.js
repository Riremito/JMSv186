// npc ペリー

var cost = 1500;
function start() {
	cm.sendYesNo("いらっしゃーーいッ！オラはここジパングとビクトリアアイランドとを往復するペリカンさ、運賃さえ払ってくれればジパングまでひとっ飛び出行くよ。運賃（片道）" + cost + "メル払うかい？");
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.sendNext("エラーメッセージ");
	} else {
		if (cm.getMeso() >= cost) {
			cm.gainMeso(-cost);
			cm.warp(800000000, "st00");
		} else {
			cm.sendNext("エラーメッセージ");
		}
	}
	cm.dispose();
}