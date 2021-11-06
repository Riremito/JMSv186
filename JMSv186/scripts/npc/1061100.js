var npc_talk_status = 0;
var enter_mapid = 105040401;
function action(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 3) {
			npc_talk_status = -1;
			var text = "当ホテルは別のサービスもございますので、ゆっくりお考えになってからご利用下さい。";
			return cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "いらっしゃいませ。スリーピーウッドホテルでございます。当ホテルは皆様に最高のサービスを提供するようにいつも心がけています。戦いに疲れたなら当ホテルを利用してはいかがでしょうか？\r\n";
				return cm.sendSimple(text);
			}
		case 2:
			{
				var text = "当ホテルには2つのルームがございます。どちらのルームを利用しますか？\r\n";
				text += "#L0##b一般サウナ室(1回：499メル)#k#l\r\n";
				text += "#L1##b高級サウナ室 (1回：999メル)#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 3:
			{
				if (selection == 0) {
					enter_mapid = 105040401;
					var text = "一般サウナ室をご利用ですね。HPとMPの回復ができて、色々なアイテムの売買も行えます。よろしいでしょうか？";
					return cm.sendYesNo(text);
				}
				enter_mapid = 105040402;
				var text = "高級サウナ室をご利用ですね。一般サウナ室よりも速くHPとMPの回復ができて、中では特別なアイテムを買うこともできます。よろしいでしょうか？";
				return cm.sendYesNo(text);
			}
		case 4: {
			cm.warp(enter_mapid, 0)
			return cm.dispose();
		}
		default:
			break;
	}

	return cm.dispose();
}