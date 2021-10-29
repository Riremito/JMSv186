// オオカミ精霊のヴォルフ
// テスト

var tips = Array(
	"サーバー情報",
	"アップデート情報",
	"コマンド一覧",
	"不具合",
	"メモ",
	"テスト"
);

function MainMenu(selection) {
	var text = "#r" + tips[selection] + "#k\r\n"
	switch (tips[selection]) {
		case "サーバー情報":
			{
				text += "バージョン: JMS v186.1 (BB前最終バージョン)\r\n";
				text += "かえで: ???\r\n";
				text += "もみじ: JMSと同等にする(予定)\r\n";
				text += "Discord: #bhttps://discord.gg/72jpx7DfZH#k\r\n";
				text += "Windows Updateのせいでたまにサーバーが落ちます\r\n";
				break;
			}
		case "アップデート情報":
			{
				text += "2021/10/29 パチンコ実装\r\n";
				text += "2021/10/22 コレ\r\n";
				break;
			}
		case "コマンド一覧":
			{
				text += "#b@help#k\r\n";
				text += "\t@系コマンドのヘルプ (よくあるコマンド類)\r\n";
				text += "#b/help#k\r\n";
				text += "\tヘルプ\r\n";
				text += "#b/info#k\r\n";
				text += "\tスクリプトIDの表示\r\n";
				text += "#b/debug#k\r\n";
				text += "\tデバッグが必要な場合に利用\r\n";
				text += "#b/fm, /フリマ#k\r\n";
				text += "\tフリーマーケット入口へ移動\r\n";
				text += "#b/henesys, /ヘネシス#k\r\n";
				text += "\tヘネシスへ移動\r\n";
				text += "#b/leafre, /リプレ#k\r\n";
				text += "\tリプレへ移動\r\n";
				text += "#b/magatia, /マガティア#k\r\n";
				text += "\tマガティアへ移動\r\n";
				text += "#b/save#k\r\n";
				text += "\tロールバック防止のため現在の状態を保存\r\n";
				text += "#b/map2 [MapID], /map [マップ名 or MapID] (GM専用)#k\r\n";
				text += "\t指定マップへ移動\r\n";
				break;
			}
		case "不具合":
			{
				text += "ジャクム落ち等: 画質の切り替えで若干対策可能\r\n";
				text += "英語のメッセージ: 未実装的なアレ\r\n";
				text += "潜在能力: まともに能力が反映されていない可能性あり\r\n";
				text += "ビシャスのハンマー: 使うと固まるので黄金つちで代用してください\r\n";
				text += "GM+封印状態: スキルが何故か打ててしまい同一マップの人が全員落ちます\r\n";
				break;
			}
		case "メモ":
			{
				text += "サーバー自体はオープンソースなので誰でも同等のサーバー建てられる状態にする予定です\r\n";
				text += "\t#bhttps://github.com/Riremito/JMSv186#k\r\n";
				text += "サーバー自体はVPSに誰でもアクセス可能な状態にしておき、VPS経由で個人のPC上で実行されているサーバーに接続する形となっています\r\n";
				text += "VPS上には何も残らないので安心安全\r\n";
				text += "パスワードは暗号化されて扱われています\r\n";
				break;
			}
		default:
			text = "未実装";
			break;
	}
	return cm.sendSimple(text);
}

var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "#rメニュー#k\r\n";
				for (var i = 0; i < tips.length; i++) {
					text += "#L" + i + "##b" + tips[i] + "\r\n";
				}
				return cm.sendSimple(text);
			}
		case 1:
			{
				return MainMenu(selection);
			}
		default:
			break;
	}
	return cm.dispose();
}