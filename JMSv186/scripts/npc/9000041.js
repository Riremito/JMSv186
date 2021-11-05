// 寄付

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// BB後
				var text = "街の発展のために使われる寄付金を入れてください！善行を施すあなたに大きな祝福があるはずです！…と小さな字で書かれている。";
				return cm.sendOk(text);
				// 寄付王挑戦中の場合はメッセージを切り替える必要がある
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