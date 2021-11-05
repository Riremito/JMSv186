// モンロンジジ
// 友達リスト

var npc_talk_status = 0;

function action(mode, type, selection) {
	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// 原文ママ, BB後も同じっぽい
				var text = "今日もお客さんが沢山くるといいのう… おお！ちょっと待ちなされ。君の顔からして、友達をかなり多く持っているんじゃないかね？どうだ？…ワシに若干のメルさえ払えば、友達のリストを増やしてやろう。しかし、同じエントリーIDの他のキャラクターには反映されないから覚えておくんじゃぞ。さあ、やってみんか？";
				return cm.sendYesNo(text);
			}
		case 2:
			{
				if (mode == 0) {
					npc_talk_status = -1;
					// BB後
					var text = "そうか…君はワシが思ったほど友達がいないようじゃの。ヒョホホホ、冗談に決まっておるじゃろう。とにかく気が変わったら又来てくれよ。友達を増やしたいならな…ヒョホホ…";
					return cm.sendSimple(text);
				}
				// BB後
				var text = "よしよし、よく決断したのぅ。値段はあまり高くない。今は激安セール期間だから#b友達リスト5名の追加に5万メル#kが相場じゃな。もちろん、バラでは売らないぞ。一度増やしたら永遠にリストの上限は増えたままだ。どうだ？#b50000メル#kを払うのか？";
				return cm.sendYesNo(text);
			}
		case 3:
			{
				var friend_list = cm.getBuddyCapacity();
				if (mode == 0 || friend_list >= 96) {
					npc_talk_status = -1;
					// BB後
					var text = "そうか… 君はワシが思ったほど友達がいないようじゃの。それとも#b50000メル#kを持っていないのか？とにかく気が向いたら又来てくれよ。メルがあればな… ヒョホホ…";
					return cm.sendSimple(text);
				}
				// BB後
				friend_list += 5;
				cm.updateBuddyCapacity(friend_list);
				var text = "よし！完了じゃ。君の友達のリストが5個増やしたぞ。確認してみてくれ。もし、それでも友達のリストが足りないのならまたワシの所へ来てくれ。200個までなら増やしてあげるから。もちろんお金は頂くがのぅ。ではな、ヒョホホ～";
				return cm.sendOk(text);
			}
		default:
			break;
	}

	return cm.dispose();
}