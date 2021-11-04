// 改札口
// カニングシティー

//var itemid = new Array(4031036, 4031037, 4031038, 4031711);
//var mapid = new Array(103000900, 103000903, 103000906, 600010004);

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "行先を選択してください。\r\n";
				text += "#L" + 0 + "##b#e1号線#n#k#l\r\n\r\n";
				text += "#L" + 1 + "##bカニングスクエア<地下鉄搭乗>#k#l\r\n";
				text += "#L" + 2 + "##b工事場#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				if (selection == 0) {
					cm.warp(103000101, "out00");
					return cm.dispose();
				}
				if (selection == 1) {
					cm.warp(103000310, "out00");
					return cm.dispose();
				}
				return cm.dispose();
			}
		case 2:
			{
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}