// 次元の扉
// 3次転職

/*
	次元の扉入場
	105070001	戦士 		アリの巣-広場
	100040106	魔法使い	邪気の森2
	105040305	弓使い		スリーピーダンジョン5
	107000402	盗賊		ルーパン沼2
	105070200	海賊		エビルアイの巣２

	職業別 次元の世界 (制限時間20分)
	108010100 弓使い			9001002
	108010200 魔法使い			9001001
	108010300 戦士				9001000
	108010400 盗賊				9001003
	108010500 海賊
	804000700 デュアルブレイド

*/

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "\r\n";
				text += "#L" + 0 + "##b#k#l\r\n";
				text += "#L" + 1 + "##b#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}
/*
function start() {
	//if (cm.getQuestStatus(100101) == 1 && !cm.haveItem(4031059)) {
		var em = cm.getEventManager("3rdjob");
		if (em == null) {
			cm.sendOk("Sorry, but everything is broken.");
		} else {
			em.newInstance(cm.getName()).registerPlayer(cm.getChar());
		}
	//} else {
		cm.sendOk("lul.");
	//}
	cm.dispose();
}

function action(mode, type, selection) {

}
*/