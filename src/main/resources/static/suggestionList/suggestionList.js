let $name = document.getElementById("friendLists");

var currentLocation = document.location.protocol + "//" + document.location.host;
var jsonDataOfThePage;

var profilesReceived;
var page = 0;
var value = ""
var isLoading = false;


// ПОИСК ПО ПОЛЬЗОВАТЕЛЯМ
function addSearchButton($mainObj) {
    $mainObj.innerHTML += "<div class=\"input-group\">\n" +
        "                                    <input id=\"searchLine\" value='" + value + "' class=\"form-control rounded\" placeholder=\"Search\" aria-label=\"Search\" aria-describedby=\"search-addon\" />\n" +
        "                                    <button onclick='toSearch()' class=\"btn btn-outline-primary\">Искать</button>\n" +
        "                                </div>";


}

function toSearch() {
    value = document.getElementById("searchLine").value;
    $name.innerHTML = "";
    page = 0;
    addSearchButton($name);
    reloadData(page);
}


// КНОПКА ОТПРАВКИ ЗАПРОСА В ДРУЗЬЯ
function addButton($name) {
    const buttonsContainer = document.createElement('div');
    buttonsContainer.classList.add("ol-md-4", "col-sm-4");
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("ol-md-2", "col-sm-2");
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "btn-primary", "pull-right");
    $friendButton.innerHTML += "Принять заявку";
    $friendButton.id = $name;
    $friendButton.onclick = function () {
        addFriend($name)
    };
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}

function denyButton($name) {
    const buttonsContainer = document.createElement('div');
    buttonsContainer.classList.add("ol-md-4", "col-sm-4");
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("ol-md-2", "col-sm-2");
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "btn-danger", "pull-right");
    $friendButton.innerHTML += "Отклонить заявку";
    $friendButton.id = $name;
    $friendButton.onclick = function () {
        denyFriend($name)
    };
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}

function addFriend($name) {
    createAjaxQuery('/user/' + $name + '/friend/1', successAddAndDenyButtonHandler($name))
}

function denyFriend($name) {
    createAjaxQuery('/user/' + $name + '/friend/2', successAddAndDenyButtonHandler($name))
}

function successAddAndDenyButtonHandler($name) {
    let obj = document.getElementById($name);
    let $mainObj = obj.parentNode;
    $mainObj.parentNode.parentNode.parentNode.removeChild(obj.parentNode.parentNode.parentNode);
}


// ФУНКЦИЯ ДОБАВЛЕНИЯ 1 БЛОКА USER
function addReceivedPerson(person) {
    let source = person["image"] != null ? '/image/' +
        person["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png';
    const personContainer = document.createElement('div');
    personContainer.classList.add("nearby-user")
    personContainer.innerHTML += "<div class=\"row " + person["username"] + "\">\n" +
        "                                        <div class=\"col-md-2 col-sm-2\">\n" +
        "                                            <img src=\"" + source + "\"\n" +
        "                                                 alt=\"user\" class=\"profile-photo-lg\">\n" +
        "                                        </div>\n" +
        "                                        <div class=\"col-md-5 col-sm-5\">\n" +
        "                                            <h5><a href=\" " + '/user/' + person["username"] + " \" class=\"profile-link\">" + person["profileInfo"]["surname"] + " " + person["profileInfo"]["name"] + "</a></h5>\n" +
        "                                            <p class=\"text-muted\">" + person["profileInfo"]["city"] + "</p>\n" +
        "                                        </div>\n" +
        "                                    </div>\n";

    personContainer.querySelector("." + person["username"]).appendChild(addButton(person["username"]));
    personContainer.querySelector("." + person["username"]).appendChild(denyButton(person["username"]));
    $name.appendChild(personContainer);
}

//Создание запросов
function createAjaxQuery(url, toFunction) {
    console.log(currentLocation + url);
    jQuery.ajax({
        type: 'GET',
        url: currentLocation + url,
        contentType: 'application/json',
        success: toFunction
    });
}


var successHandler = function (data) {
    jsonDataOfThePage = data;
    profilesReceived = data;
    addAllInvites()
};

function addAllInvites() {
    if (document.getElementById("receivedText") === null) {
        $name.appendChild(document.createElement('br'))
        $name.appendChild(createTextUnderPerson("Заявки в друзья", "receivedText"));
    }

    if (profilesReceived != null)
        for (let i = 0; i < profilesReceived.length; i++)
            addReceivedPerson(profilesReceived[i]);
}

function createTextUnderPerson(text, id) {
    const friendContainer = document.createElement('span');
    friendContainer.style.display = "flex";
    friendContainer.id = id;
    friendContainer.style.justifyContent = "center";
    friendContainer.innerText = text;
    return friendContainer;
}


function reloadData(page) {
    // Process your request
    var request = {};
    request.searchLine = value; // some data

    // Make the AJAX call
    jQuery.ajax({
        type: 'POST',
        url: currentLocation + "/user/" + userNameOfPage + "/reloadSuggestionList/" + page,
        contentType: 'application/json',
        data: JSON.stringify(request),
        success: successHandler
    });
}

$(function () {
    addSearchButton($name);
    isLoading = true;
    reloadData(page);

})

$(window).scroll(function () {
    if ($(document).height() <= $(window).scrollTop() + $(window).height() + 100 && !isLoading) {
        page++;
        isLoading = true;
        reloadData(page);
    }
});