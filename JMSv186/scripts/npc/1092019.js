// ジョナサン

function start() {
	if (cm.getJob() == 522 && cm.getPlayerStat("LVL") >= 120) {
		if (!cm.hasSkill(5221003)) {
			cm.teachSkill(5221003, 0, 10);
		}
	}
	// BB後
	cm.sendOk("エヘン！どうかしたのか？");
}

function action(mode, type, selection) {
	cm.dispose();
}
