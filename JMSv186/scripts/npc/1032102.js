// ペットの復活
// 妖精マル

var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "#b生命の水#kと#b生命の呪文書#kさえあればその人形を何とか以前のように動く動物にすることができるかもしれません、どうでしょう…アイテムを集めてきますか？アイテムさえ持って来てくださったら喜んでその人形を元どおりにしてあげますよ…\r\n";
				return cm.sendYesNo(text);
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