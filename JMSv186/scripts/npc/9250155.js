// OS4Œ¤‹†ˆõ ˆÚ“®æ‚©‚ç‘Şo

var status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	switch (status) {
		case 0:
			{
				cm.sendYesNo("‘Şo‚µ‚Ü‚·‚©H");
				return;
			}
		case 1:
			{
				cm.warp(502010000, "sp");
				break;
			}
		default:
			break;
	}

	cm.dispose();
}