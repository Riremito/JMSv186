// ピンクビーン遠征隊 退場
// 忘れられた神殿管理人

function start() {
	cm.sendYesNo("遠征隊をやめて退場しますか？");
}

function action(mode, type, selection) {
	if (mode == 1) {
		cm.warp(270050000, 0);
	}
	cm.dispose();
}