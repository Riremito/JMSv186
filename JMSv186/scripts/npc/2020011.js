/* Arec
	Thief 3rd job advancement
	El Nath: Chief's Residence (211000001)

	Custom Quest 100100, 100102
*/

var status = -1;
var job;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 1) {
	    cm.sendOk("Make up your mind and visit me again.");
	    cm.safeDispose();
	    return;
	}
	status--;
    }

    if (status == 0) {
	if (!(cm.getJob() == 410 || cm.getJob() == 420 || cm.getJob() == 432)) {
	    cm.sendOk("May #rOdin#k be with you!");
	    cm.safeDispose();
	    return;
	}
	if ((cm.getJob() == 410 || cm.getJob() == 420 || cm.getJob() == 432) && cm.getPlayerStat("LVL") >= 70 && (cm.getJob() == 432 || cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3)) {
	    cm.sendNext("You are indeed a strong one.");
	} else {
	    cm.sendOk("Please use all your SP from level 70 and under, and be level 70+...");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	    if (cm.getPlayerStat("LVL") >= 70 && (cm.getJob() == 432 || cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3)) {
	    	if (cm.getJob() == 410) { // ASSASIN
			cm.changeJob(411); // HERMIT
			cm.gainAp(5);
			cm.sendOk("You are now a #bHermit#k. May #rOdin#k be with you!");
			cm.safeDispose();
	    	} else if (cm.getJob() == 420) { // BANDIT
			cm.changeJob(421); // CDIT
			cm.gainAp(5);
			cm.sendOk("You are now a #bChief Bandit#k. May #rOdin#k be with you!");
			cm.safeDispose();
		} else if (cm.getJob() == 432) { // 
			cm.changeJob(433); // 
			cm.gainAp(5);
			cm.sendOk("You are now a #bBlade Lord#k. May #rOdin#k be with you!");
			cm.safeDispose();
	    	}
	    } else {
		cm.sendOk("Come back when you are level 70 and used all your SP accordingly.");
		cm.dispose();
	    }
    }
}
