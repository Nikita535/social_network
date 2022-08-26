let $name = document.getElementById("friendLists");
var currentLocation = document.location.protocol + "//" + document.location.host;
var jsonDataOfThePage;
var profilesOfStrangers;
var friendProfiles;
var isInviteSendStrangers;
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


// КНОПКА УДАЛЕНИЯ ДРУГА
function deleteButton($name) {
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("ol-md-3", "col-sm-3")
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "btn-danger", "pull-right");
    $friendButton.innerHTML += "Удалить из друзей";
    $friendButton.id = $name;
    $friendButton.onclick = function () {
        deleteFriend($name)
    };
    buttonContainer.appendChild($friendButton)
    return buttonContainer;
}

function deleteFriend($name) {
    createAjaxQuery('/user/' + $name + '/unfriend', successDeleteFriendHandler($name))
}

function successDeleteFriendHandler($username) {
    let obj = document.querySelector("." + $username).parentNode;
    document.getElementById($username).parentNode.parentNode.appendChild(sendButton($username));
    document.getElementById($username).parentNode.parentNode.removeChild(document.getElementById($username).parentNode);
    $name.removeChild(obj);
    $name.appendChild(obj);
}

// КНОПКА ОТПРАВКИ ЗАПРОСА В ДРУЗЬЯ
function sendButton($name) {
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("ol-md-3", "col-sm-3");
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "btn-primary", "pull-right");
    $friendButton.innerHTML += "Добавить в друзья";
    $friendButton.id = $name;
    $friendButton.onclick = function () {
        sendFriend($name)
    };
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}

function sendFriend($name) {
    createAjaxQuery('/user/' + $name + '/friend', successSendFriendHandler($name))
}

function successSendFriendHandler($name) {
    let obj = document.getElementById($name);
    let $mainObj = obj.parentNode;
    let parent = $mainObj.parentNode;
    parent.removeChild($mainObj);
    parent.appendChild(alreadySendButton($name));

}

// КНОПКА ЗАЯВКА ОТПРАВЛЕНА
function alreadySendButton($name) {
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("ol-md-3", "col-sm-3");
    const $friendButton = document.createElement('div');
    $friendButton.classList.add("btn", "btn-dark", "pull-right");
    $friendButton.innerHTML += "Заявка отправлена";
    $friendButton.id = $name;
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}


// ФУНКЦИЯ ДОБАВЛЕНИЯ 1 БЛОКА USER
function addPerson(person, isStranger = false, isSend = true, fromRight = false) {
    let source;
    if (fromRight === false)
        source = person["image"] != null ? '/image/' +
            person["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png';
    else
        source = person["images"][0];
    const personContainer = document.createElement('div');
    personContainer.classList.add("nearby-user")
    personContainer.innerHTML += "<div class=\"row " + person["username"] + "\">\n" +
        "                                        <div class=\"col-md-2 col-sm-2\">\n" +
        "                                            <img src=\"" + source + "\"\n" +
        "                                                 alt=\"user\" class=\"profile-photo-lg\">\n" +
        "                                        </div>\n" +
        "                                        <div class=\"col-md-7 col-sm-7\">\n" +
        "                                            <h5><a href=\" " + '/user/' + person["username"] + " \" class=\"profile-link\">" + person["profileInfo"]["surname"] + " " + person["profileInfo"]["name"] + "</a></h5>\n" +
        "                                            <p class=\"text-muted\">" + person["profileInfo"]["city"] + "</p>\n" +
        "                                        </div>\n" +
        "                                    </div>\n";

    if (isStranger === false) {
        personContainer.querySelector("." + person["username"]).appendChild(deleteButton(person["username"]));
        $name.appendChild(personContainer);
    } else if (isSend === false) {
        personContainer.querySelector("." + person["username"]).appendChild(sendButton(person["username"]));
        $name.appendChild(personContainer);
    } else {
        personContainer.querySelector("." + person["username"]).appendChild(alreadySendButton(person["username"]));
        $name.appendChild(personContainer);
    }
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
    console.log(data);
    friendProfiles = data["0"];
    profilesOfStrangers = data["1"];
    isInviteSendStrangers = data["2"];
    addAllPeople();
};

function addAllPeople() {

    if (document.getElementById("friendStatusText") === null && friendProfiles.length !== 0) {
        $name.appendChild(document.createElement('br'))
        $name.appendChild(createTextUnderPerson("Друзья", "friendStatusText"));
    }

    for (let i = 0; i < friendProfiles.length; i++)
        addPerson(friendProfiles[i]);


    if (profilesOfStrangers.length !== 0 && document.getElementById("strangeStatusText") === null)
        $name.appendChild(createTextUnderPerson("Возможные друзья", "strangeStatusText"));

    for (let i = 0; i < profilesOfStrangers.length; i++)
        addPerson(profilesOfStrangers[i], true, isInviteSendStrangers[i]);

    isLoading = false;
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
    console.log(value);
    jQuery.ajax({
        type: 'POST',
        url: currentLocation + "/user/" + userNameOfPage + "/reloadFriendList/" + page,
        contentType: 'application/json',
        data: JSON.stringify(request),
        success: successHandler
    });
}

$(function () {
    addSearchButton($name);
    reloadData(page);
})

$(window).scroll(function () {
    if ($(document).height() <= $(window).scrollTop() + $(window).height() + 100 &&
        friendProfiles.length + profilesOfStrangers.length !== 0 && !isLoading) {
        page++;
        isLoading = true;
        reloadData(page);
    }
});