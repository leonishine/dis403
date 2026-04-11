<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>Тест</title>
</head>
<body>

<div id="login_section" style="display:block">
    <form id="form" method="post">
        <div><input name="username" type="text" placeholder="username"/></div>
        <div><input name="password" type="password" placeholder="password"/></div>
        <div><input type="button" value="OK" onclick="login()"/></div>
    </form>
</div>

<div id="content_section" style="display:none">
    <h3>Данные по бронированиям в гостинице:</h3>
    <div>
        <table id="booking_table"></table>
    </div>
</div>

<div id="booking_data"></div>

<#noparse>
    <script>
        let jwt_token;
        function login() {
            const formElement = document.getElementById('form');
            const jsonData = JSON.stringify(Object.fromEntries(new FormData(formElement)));

            fetch('http://localhost:8090/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                },
                body: jsonData
            })
                .then(r => {
                    if (!r.ok) throw new Error("Login failed");
                    return r.json();
                })
                .then(json => {
                    jwt_token = json.token;

                    document.getElementById("login_section").style.display = "none";
                    document.getElementById("content_section").style.display = "block";

                    show_content();
                })
                .catch(err => alert(err.message));
        }

        function show_content() {
            show_bookings();
        }

        function show_bookings() {
            fetch('http://localhost:8090/api/booking/allview', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + jwt_token
                }
            })
                .then(r => {
                    if (!r.ok) throw new Error("Unauthorized or server error");
                    return r.json();
                })
                .then(json => make_booking_table(json))
                .catch(err => alert(err.message));
        }

        function make_booking_table(data) {
            let tbl = document.getElementById("booking_table");
            tbl.innerHTML = "";

            if (!data || !data.bookings) return;

            for (let i = 0; i < data.bookings.length; i++) {
                let b = data.bookings[i] || {};

                let tr = document.createElement('tr');

                let td_room = document.createElement('td');
                let room = b.room || "—";

                td_room.innerHTML =
                    "<span onclick='open_booking(" + b.id + ")'>" +
                    room +
                    "</span>";

                tr.append(td_room);

                let td_arrivaldate = document.createElement('td');
                td_arrivaldate.innerHTML = formatDate(b.arrivaldate);
                tr.append(td_arrivaldate);

                let td_stayingdate = document.createElement('td');
                td_stayingdate.innerHTML = formatDate(b.stayingdate);
                tr.append(td_stayingdate);

                tbl.append(tr);
            }
        }

        function open_booking(booking_id) {
            fetch('http://localhost:8090/api/booking/get/' + booking_id, {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + jwt_token
                }
            })
                .then(r => {
                    if (!r.ok) throw new Error("Cannot load booking");
                    return r.json();
                })
                .then(json => fill_booking(json))
                .catch(err => alert(err.message));
        }

        function formatDate(dateStr) {
            if (!dateStr) return "";
            return dateStr.split("T")[0];
        }


        function fill_booking(data) {
            if (!data) return;

            let html = `
            <h3>Редактирование</h3>

            <input type="hidden" id="booking_id" value="${data.id || ''}"/>

            <div>
                Комната: <input type="text" id="room" value="${data.room || ''}"/>
            </div>

            <div>
                Дата заезда: <input type="date" id="arrivaldate" value="${formatDate(data.arrivaldate)}"/>
            </div>

            <div>
                Дата проживания: <input type="date" id="stayingdate" value="${formatDate(data.stayingdate)}"/>
            </div>

            <div>
                Дата выезда: <input type="date" id="departuredate" value="${formatDate(data.departuredate)}"/>
            </div>

            <div>
                Клиент: ${data.name || '—'}
            </div>

            <button onclick="save_booking()">Сохранить</button>
        `;

            document.getElementById("booking_data").innerHTML = html;
        }

        function save_booking() {
            let booking = {
                id: document.getElementById("booking_id").value,
                room: document.getElementById("room").value,
                arrivaldate: document.getElementById("arrivaldate").value,
                stayingdate: document.getElementById("stayingdate").value,
                departuredate: document.getElementById("departuredate").value
            };

            fetch('http://localhost:8090/api/booking/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json;charset=utf-8',
                    'Authorization': 'Bearer ' + jwt_token
                },
                body: JSON.stringify(booking)
            })
                .then(r => {
                    if (!r.ok) throw new Error("Ошибка сохранения");
                    return r.json();
                })
                .then(() => {
                    alert("Сохранено");
                    show_bookings();
                })
                .catch(err => alert(err.message));
        }
    </script>
</#noparse>

</body>
</html>