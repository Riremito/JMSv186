// ジャンケンマスター
var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		npc_talk_status--;
		return cm.dispose();
	}

	// キャンセル不可
	npc_talk_status++;
	cm.sendRPS();
	return cm.dispose();
}