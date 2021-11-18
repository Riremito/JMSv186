// パチンコ2

var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		npc_talk_status++;
	} else {
		npc_talk_status--;
	}

	switch (npc_talk_status) {
		case 0:
			{
				// パチンコ玉を所持している場合
				if (cm.getPlayer().getTama() > 0) {
					cm.sendYesNo("パチンコを始めましょうか。");
					return;
				}

				cm.sendOk("パチンコ玉が足りないため、パチンコをすることができません。只今、ポイントショップのETCの経済活動欄でパチンコ玉を販売中ですので、ご利用ください。");
				break;
			}
		case -1:
			{
				cm.sendOk("残念ですね…。後で時間ができましたら、ご利用ください。");
				break;
			}
		case 1:
			{
				cm.getPlayer().StartPachinko(0);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}