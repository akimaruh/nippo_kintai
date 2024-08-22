document.addEventListener('DOMContentLoaded', function () {
    const calendarBody = document.getElementById('calendar-body');
   

    let currentMonth = new Date().getMonth();
    let currentYear = new Date().getFullYear();

    function generateCalendar(month, year) {
        calendarBody.innerHTML = ''; // カレンダーをリセット
        let daysInMonth = new Date(2024, 7 + 1, 0).getDate();

        // カレンダーの行を生成
        for (let date = 1; date <= daysInMonth; date++) {
            let row = document.createElement('tr');

            // 日付セル
            let dateCell = document.createElement('td');
            dateCell.appendChild(document.createTextNode(`${year}-${month + 1}-${date}`));
            row.appendChild(dateCell);

            // 曜日セル
            let dayOfWeek = new Date(year, month, date).getDay();
            let dayOfWeekCell = document.createElement('td');
            dayOfWeekCell.appendChild(document.createTextNode(['日', '月', '火', '水', '木', '金', '土'][dayOfWeek]));
            row.appendChild(dayOfWeekCell);

            // 勤務状況セル
            let workStatusCell = document.createElement('td');
            let workStatusInput = document.createElement('input');
            workStatusInput.type = 'text';
            workStatusCell.appendChild(workStatusInput);
            row.appendChild(workStatusCell);

            // 出勤時間セル
            let startTimeCell = document.createElement('td');
            let startTimeInput = document.createElement('input');
            startTimeInput.type = 'time';
            startTimeCell.appendChild(startTimeInput);
            row.appendChild(startTimeCell);

            // 退勤時間セル
            let endTimeCell = document.createElement('td');
            let endTimeInput = document.createElement('input');
            endTimeInput.type = 'time';
            endTimeCell.appendChild(endTimeInput);
            row.appendChild(endTimeCell);

            // 備考セル
            let noteCell = document.createElement('td');
            let noteInput = document.createElement('input');
            noteInput.type = 'text';
            noteCell.appendChild(noteInput);
            row.appendChild(noteCell);

            calendarBody.appendChild(row);
        }
    }

    prevMonthButton.addEventListener('click', function () {
        if (currentMonth === 0) {
            currentMonth = 11;
            currentYear--;
        } else {
            currentMonth--;
        }
        generateCalendar(currentMonth, currentYear);
    });

    nextMonthButton.addEventListener('click', function () {
        if (currentMonth === 11) {
            currentMonth = 0;
            currentYear++;
        } else {
            currentMonth++;
        }
        generateCalendar(currentMonth, currentYear);
    });

    generateCalendar(currentMonth, currentYear);
});