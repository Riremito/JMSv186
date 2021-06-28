// プレドリック

function action(mode, type, selection) {
	var jobid = cm.getJob();
	var level = cm.getPlayer().getLevel();

	var text = "";
	text += "Job ID = " + jobid + "\r\n";
	text += "Lv = " + level + "\r\n";

	//cm.getPlayer().gainExp(500000000, true, false, true);
	cm.openShop(9030000);
	//cm.sendOk(text);
	cm.dispose();
}