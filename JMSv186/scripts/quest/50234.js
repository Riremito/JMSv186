
// オチャマルと親しくなろう！
var status = -1;

function start(mode, type, selection) {
	//qm.forfeitQuest();
	//qm.forceStartQuest();
	qm.dispose();
}

function end(mode, type, selection) {
	//qm.forceCompleteQuest();
	qm.forceStartQuest();
	qm.dispose();
}