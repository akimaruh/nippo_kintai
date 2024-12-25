// labelsとdataを取得するための配列
							const labels = [];
							const data = [];
							for (const [key, value] of Object.entries(workTimeByProcessMapList)) {
								labels.push(key); // 作業工程名をlabelsに追加
								data.push(value);  // 作業時間をdataに追加
							}
							var pieData = {
								labels: labels,
								datasets: [{
									label: '作業割合',
									data: data,
									hoverOffset: 4
								}]
							};
							window.onload = function () {
								var ctx = document.getElementById("myChart").getContext("2d");
								var myPieChart = new Chart(ctx, {
									type: 'pie',
									data: pieData,
									options: {
										responsive: true,
										plugins: {
											legend: {
												position: 'left',
											},
											tooltip: {
												callbacks: {
													label: function (context) {
														let label = context.label || '';
														if (label) {
															label += ': ';
														}
														label += context.raw; // 値を表示
														return label;
													}
												}
											}
										}
									}
								});
								const exportButton = document.getElementById('pdf-button');
								// 画像表示用のimgタグを作成
								exportButton.addEventListener('click', () => {
									let imageURL = myPieChart.toBase64Image();
									console.log(imageURL);
									document.getElementById('hiddenImageField').value = imageURL; // Base64データを隠しフィールドに設定	
								});
							};