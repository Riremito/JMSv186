// ビョル
// 7周年
// テスト用途

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "テスト\r\n";
				//text += "#L" + 0 + "##b#k#l\r\n";
				//text += "#L" + 1 + "##b#k#l\r\n";
				text += "#r赤#b青#d紫#g緑#k#e太字#n#k黒\r\n";

				text += "#rアイテム所持数 = #k#z2000005# #c2000005#\r\n";
				text += "#rマップ名 = #k#m910000000#\r\n";
				text += "#rNPC名 = #k#p9000083#\r\n";
				text += "#rMob名 = #k#o9400439#\r\n";
				text += "#rアイテム名 = #k#t2070016#\r\n";
				text += "#rアイテム情報 = #k#z2070016#\r\n";
				text += "#rアイテムアイコン1 = #k#v2070016#\r\n";
				text += "#rアイテムアイコン2 = #k#i2070016#\r\n";
				text += "#rスキルアイコン = #k#s4121007#\r\n"
				text += "#rスキル名 = #k#q4121007#\r\n"
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