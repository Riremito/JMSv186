
var status = -1;
// !drop 4032156 100
// !drop 4032151

// !drop 4032154
// !drop 4032153
// !drop 4032152
function start(mode, type, selection) {
	qm.forceStartQuest();
	qm.dispose();
}

function end(mode, type, selection) {
	qm.forceCompleteQuest();
	qm.dispose();
}