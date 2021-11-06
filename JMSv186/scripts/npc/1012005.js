// クロイ
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
				var text = "君…もしや私の子供を育てているのではないか？私は生命の水と魔法を使って、人形に命を吹き込むことに成功した。人々はそのように命を得た私の子供達を#bペット#kと呼んでいるようだね。もしペットを持っているなら何でも私に聞いておくれ\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				// BB後
				var text = "何が知りたいのかね？\r\n";
				text += "#L" + 0 + "##bペットって何？#k#l\r\n";
				text += "#L" + 1 + "##bペットはどうやって育てるの？#k#l\r\n";
				text += "#L" + 2 + "##bペットも死ぬの？#k#l\r\n";
				text += "#L" + 3 + "##bアクションペットについて知りたい！#k#l\r\n";
				text += "#L" + 4 + "##bペットの能力値を移動させる方法を知りたい！#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 2:
			{
				// 説明とか記載する
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}