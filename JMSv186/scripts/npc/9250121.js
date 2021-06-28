// ゴミ箱おじさん
// !npc 9250121

function action(mode, type, selection) {
	//var jobid = cm.getJob();
	//var level = cm.getPlayer().getLevel();

	var text = "";
	//text += "Job ID = " + jobid + "\r\n";
	//text += "Lv = " + level + "\r\n";
	text += "テストメッセージ";

	//cm.changeMusic("Bgm09/TimeAttack");
	cm.changeMusic("Bgm03/Elfwood");
	cm.sendOk(text);
	cm.dispose();
	return;

	var p = cm.getOutPacket();

	p.writeShort(0x3F);
	p.write(3);
	p.writeMapleAsciiString("TSH");
	p.write(0);
	p.write(1);
	cm.SendPacket(p.getPacket());

	//cm.BroadcastPacket(cm.OutPacket().getPacket().getBytes());
	//DefeatedMessage(2);
	cm.dispose();
}

function DefeatedMessage(mobid) {
	switch (mobid) {
		case 0:
			{
				cm.WorldMessage("大変な挑戦の終わりにホーンテイルを撃破した遠征隊よ！貴方達が本当のリプレの英雄だ！");
				break;
			}

		case 1:
			{
				cm.WorldMessage("不屈の闘志でピンクビーンを退けた遠征隊の諸君！　君たちが真の時間の覇者だ！");
				break;
			}

		case 2:
			{
				cm.WorldMessage("びえんちゃん");
				break;
			}
		default:
			break;
	}
}