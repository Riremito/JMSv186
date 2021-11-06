// 未完成魔法陣
var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// でぃととろい
				cm.getMap().killMonster(8090000);
				// BB後
				var text = "幾何学な模様が描かれている魔方陣だ。完成には至っていないようだが…。\r\n";
				return cm.sendOk(text);
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