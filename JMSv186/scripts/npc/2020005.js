// アイテム販売
// アルケスタ

var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "君のお陰で#b古代の本#kは無事に封印できた。だがそのせいで８００年間に修練してきたわしの力の半分を無くしたがね、もう死んでも惜しくない。そうだ、どころで君、珍しいアイテムは欲しくないか？今まで手伝ってくれたお礼だ。わしが持っているもので必要なものがあるなら安く売ってあげよう。\r\n";
				text += "#L" + 0 + "##b聖水(値段：300 メル)#k#l\r\n";
				text += "#L" + 1 + "##b万病治療薬(値段：400 メル)#k#l\r\n";
				text += "#L" + 2 + "##b魔法の石(値段：5000 メル)#k#l\r\n";
				text += "#L" + 3 + "##b召喚の石(値段：5000 メル)#k#l\r\n";
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