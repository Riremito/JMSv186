// ジャンケンマスター
var npc_talk_status = -1;
function action(mode, type, selection) {
	// キャンセル不可
	npc_talk_status++;
	cm.dispose();
	cm.sendRPS();
	return cm.dispose();
}