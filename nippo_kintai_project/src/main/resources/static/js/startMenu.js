
function markAsRead(event, notificationId, userId, redirectUrl) {

	// デフォルトのリンク動作（GETリクエスト）を防ぐ
	event.preventDefault();
	// Ajaxリクエストを非同期で送信
	fetch('/notifications/markAsRead', {

		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			"notificationId": notificationId,
			"userId": userId
		}),


	})

		.then(response => {
			if (!response.ok) {
				throw new Error('既読処理に失敗しました');
			}
			console.log('既読処理が成功しました');
			window.location.href = redirectUrl;
		})
		.catch(error => {
			console.error('エラー:', error);

		});
}

function markAsReadForModal(event, notificationId, userId, index) {
	event.preventDefault(); // デフォルトのリンク遷移を防止

	// 通知の内容を取得（必要に応じてサーバーから取得する処理に変更）

	const message = document.getElementById("notificationsList[" + index + "].message").innerHTML;
	console.log(message);


	// モーダルのメッセージを設定
	document.getElementById('modalMessage').textContent = message;

	// モーダルを表示
	var myModal = new bootstrap.Modal(document.getElementById('myModal'));
	myModal.show();

	fetch('/notifications/markAsRead', {

		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			"notificationId": notificationId,
			"userId": userId
		}),


	})

		.then(response => {
			if (!response.ok) {
				throw new Error('既読処理に失敗しました');
			}
			console.log('既読処理が成功しました');
			window.location.href = '#';
		})
		.catch(error => {
			console.error('エラー:', error);

		});

}
////現在時刻を表示
function twoDigit(num) {
    return num < 10 ? "0" + num : num;
}

function showClock() {
    let nowTime = new Date();
    let nowHour = twoDigit(nowTime.getHours());
    let nowMin = twoDigit(nowTime.getMinutes());
    let msg = "現在時刻：" + nowHour + ":" + nowMin;
    document.getElementById("realtime").innerHTML = msg;
}
// 初期表示を行う
showClock();
// 1秒ごとに更新
setInterval(showClock, 1000);

// タイマー
const time = document.getElementById('timer');
const startButton = document.getElementById('start');
const stopButton = document.getElementById('stop');
const resetButton = document.getElementById('reset');
const submitButton = document.getElementById('submit');

// 開始時間
let startTime;
// 停止時間
let stopTime = 0;
// タイムアウトID
let timeoutID;
let currentTime;


// 時間を表示する関数
function displayTime() {
	  currentTime =new Date(Date.now() - startTime + stopTime);
//  console.log('displayTime().currentTime:'+currentTime);
  const h = String(currentTime.getUTCHours()).padStart(2, '0');
  const m = String(currentTime.getMinutes()).padStart(2, '0');
  const s = String(currentTime.getSeconds()).padStart(2, '0');

  time.textContent = `${h}:${m}:${s}`;
  timeoutID = setTimeout(displayTime, 1000);
}

// スタートボタンがクリックされたら時間を進める
startButton.addEventListener('click', () => {
//	sessionStorage['userId'+userId+'_action'] = 'userId5_action';
  startButton.disabled = true;
  stopButton.disabled = false;
  resetButton.disabled = true;
  submitButton.disabled = true;
  startTime = Date.now();
   sessionStorage[userId+'startTime'] = startTime;
  currentTime = new Date(Date.now() - startTime + stopTime);
//  console.log('スタートボタン押下.currentTime:'+currentTime);
 if(sessionStorage.getItem(userId+'action') == 'stop'){
	 currentTime = sessionStorage.getItem(userId+'currentTime');
 }
  sessionStorage[userId+'action'] = 'start';
  console.log(sessionStorage);
  
  displayTime();
});

// ストップボタンがクリックされたら時間を止める
stopButton.addEventListener('click', function() {
  startButton.disabled = false;
  stopButton.disabled = true;
  resetButton.disabled = false;
  submitButton.disabled = false;
  clearTimeout(timeoutID);
  stopTime += (Date.now() - startTime); 
  sessionStorage[userId+'stopTime'] = stopTime;
  sessionStorage[userId+'action'] = 'stop';

//  currentTime = new Date(Date.now() - startTime + stopTime);
//console.log('ストップボタン押下.currentTime:'+currentTime);
  sessionStorage[userId+'currentTime'] = currentTime.getTime(); // ミリ秒で保存
  console.log(sessionStorage);
});

// リセットボタンがクリックされたら時間を0に戻す
resetButton.addEventListener('click', function() {
  startButton.disabled = false;
  stopButton.disabled = true;
  resetButton.disabled = true;
  submitButton.disabled = true;
  time.textContent = '00:00:00';
  stopTime = 0;
  sessionStorage.removeItem(userId+'startTime');
  sessionStorage.removeItem(userId+'stopTime');
  sessionStorage.removeItem(userId+'currentTime');
  sessionStorage.removeItem(userId+'action');
  console.log(sessionStorage);
});

// DOMが読み込まれた時の処理
document.addEventListener('DOMContentLoaded', () => {
	console.log(sessionStorage);
	
	//タイマー起動中に更新、画面遷移に対応する
  if (sessionStorage.getItem(userId+'action') == 'start') {
    startButton.disabled = true;
    stopButton.disabled = false;
    resetButton.disabled = true;
    submitButton.disabled = true;
    
    startTime = parseInt(sessionStorage[userId+'startTime'], 10);
    stopTime = parseInt(sessionStorage[userId+'stopTime'], 10) || 0; // stopTimeがない場合は0を設定
    displayTime();
  }
	//タイマーストップ後、再スタートできるようにストップ時のタイマーの値を引き継ぐ
  if (sessionStorage.getItem(userId+'action') == 'stop') {
    startButton.disabled = false;
    stopButton.disabled = true;
    resetButton.disabled = false;
    submitButton.disabled = false;
    
	startTime = parseInt(sessionStorage[userId+'startTime'], 10);
    stopTime = parseInt(sessionStorage[userId+'stopTime'], 10) || 0; // stopTimeがない場合は0を設定
     currentTime = new Date(parseInt(sessionStorage.getItem(userId+'currentTime'), 10));
  
    
    const h = String(currentTime.getUTCHours()).padStart(2, '0');
    const m = String(currentTime.getMinutes()).padStart(2, '0');
    const s = String(currentTime.getSeconds()).padStart(2, '0');
    
    time.textContent = `${h}:${m}:${s}`; // ストップ時の時間を表示
  }
  
});

//提出完了したらタイマーリセット
let submit =document.getElementById('submit-message')
if(submit){
  startButton.disabled = false;
  stopButton.disabled = true;
  resetButton.disabled = true;
  submitButton.disabled = true;
  time.textContent = '00:00:00';
  stopTime = 0;
  sessionStorage.removeItem(userId+'startTime');
  sessionStorage.removeItem(userId+'stopTime');
  sessionStorage.removeItem(userId+'currentTime');
  sessionStorage.removeItem(userId+'action');
  console.log(sessionStorage);
}

//プルダウンリストの作成
function createSelectOptions(workArray) {
	let options = '<option value=""></option>'; // 空のオプションを追加
	workArray.forEach(({ key, value }) => {
		options += `<option value="${value}">${key}</option>`;
	});
	return options; // 生成したオプションのHTMLを返す
}
/**
 * 日報提出フォームモーダル
 */
function modalForSubmit(event,dailyReportDetailForm) {
	event.preventDefault(); // デフォルトのリンク遷移を防止
	console.log(dailyReportDetailForm);


	// モーダルを表示
	var dailyReportModal = new bootstrap.Modal(document.getElementById('dailyReportModal'));
	dailyReportModal.show();
	
	//タイマーで計測した作業時間をフォームに反映させる
	console.log(currentTime);//Thu Jan 01 1970 09:00:06 GMT+0900 (日本標準時)の形
	let timeInput; 
	
	if(currentTime.getMinutes() < 30){
		timeInput = currentTime.getUTCHours();
	}else{
		timeInput = currentTime.getUTCHours()+1;
	}
	console.log(timeInput);
	
	// 作業時間の入力欄に値を設定
    let timeInputForm = document.getElementById("timeInputForm");
    timeInputForm.value = timeInput; // 値を直接設定

}
/**
 * 日報提出入力エラーの場合モーダル再表示
 */
window.addEventListener("DOMContentLoaded", (event) => {
	
    // サーバからのエラー情報を取得
	if(document.querySelector("[data-open-modal]")){
    const openModal = document.querySelector("[data-open-modal]").dataset.openModal;
    const modalError = document.querySelector("[data-modal-error]").dataset.modalError;
    console.log(errorMessages);
    document.getElementById("workIdError").innerHTML =errorMessages['workId'] ?? '';
    document.getElementById("timeError").innerHTML =errorMessages['time'] ?? '';
    document.getElementById("contentError").innerHTML =errorMessages['content'] ?? '';
    console.log(openModal);
    console.log(modalError);
    

    // エラーがある場合にモーダルを再表示し、エラーメッセージを表示
    if (openModal === "true") {
		
		//入力内容を設定
//					console.log(bindingResult);
					//workId
					let workIdInputForm = document.getElementById("workIdInputForm");
					workIdInputForm.value = dailyReportDetailForm.workId; // 値を直接設定
					//time
					let timeInputForm = document.getElementById("timeInputForm");
					timeInputForm.value = dailyReportDetailForm.time; // 値を直接設定
					//content
					let contentInputForm = document.getElementById("contentInputForm");
					contentInputForm.value = dailyReportDetailForm.content; // 値を直接設定
					
					
        const modal = new bootstrap.Modal(document.getElementById("dailyReportModal")); // Bootstrapを利用している場合
        modal.show();
        
        // エラーメッセージをモーダルに表示
        document.getElementById("modalErrorMessage").textContent = modalError;
    }
}});


////workId
//var workIdInputForm = document.getElementById("workIdInputForm");
////workIdInputForm.value = dailyReportDetailForm.workId; // 値を直接設定
////time
//let timeInputForm = document.getElementById("timeInputForm");
////timeInputForm.value = dailyReportDetailForm.time; // 値を直接設定
////content
//let contentInputForm = document.getElementById("contentInputForm");
////contentInputForm.value = dailyReportDetailForm.content; // 値を直接設定
//function modal(event) {
//	
//	// 要素が取得できているか確認
//	if (contentInputForm && timeInputForm && workIdInputForm) {
//		dailyReportDetailForm.content = contentInputForm.value || ""; // 空の場合は空文字を代入
//		dailyReportDetailForm.time = timeInputForm.value || ""; // 空の場合は空文字を代入
//		dailyReportDetailForm.workId = workIdInputForm.value || ""; // 空の場合は空文字を代入
//		dailyReportDetailForm.userId = userId;
//	} else {
//		console.error("必須の入力フォームが見つかりません");
//		return; // フォームが見つからない場合は処理を終了
//	}		
//
//	// デフォルトのリンク動作（GETリクエスト）を防ぐ
//	event.preventDefault();
//	// Ajaxリクエストを非同期で送信
//	fetch('/startMenu/dailyReport/submitComplete', {
//
//		method: 'POST',
//		headers: {
//			'Content-Type': 'application/json',
//		},
//		body: JSON.stringify({
//			"dailyReportDetailForm": dailyReportDetailForm
//			
//		}),
//
//
//	})
//		.then(response => {
//					if (!response.ok) {
//						throw new Error("Network response was not ok: " + response.status);
//					}
//					return response.json();
//				})
//		.then(data=> {
//			console.log(data);
//		})
//		.catch(error => {
//			console.error('エラー:', error);
//
//		});
//}

//
////タイマー
//function showTimer(){
//	let time = new showTimer();
//	let timerHour = twoDigit(setInterval());
//	let timerMin = twoDigit(setInterval());
//	let timerSec = twoDigit(time);
//	let timerMeg = timerHour + ":"+ timerMin + ":" + timerSec;
//	document.getElementById("timer").innerHTML = timerMeg;
//}
//
//showTimer();
//setInterval(function(){
//    timerSec++;
//    console.log(timerSec);
//    return timerSec;
//  },1000);
  
// 1秒ごとに更新


////現在時刻を表示
//function twoDigit(num) {
//	let ret;
//	if (num < 10)
//		ret = "0" + num;
//	else
//		ret = num;
//	return ret;
//}
//function showClock() {
//	let nowTime = new Date();
//	let nowHour = twoDigit(nowTime.getHours());
//	let nowMin = twoDigit(nowTime.getMinutes());
//	let msg = "現在時刻：" + nowHour + ":" + nowMin;
//	document.getElementById("realtime").innerHTML = msg;
//}
//setInterval('showClock()', 1000);
//document.addEventListener('DOMContentLoaded', () => {
//	if()
//	});