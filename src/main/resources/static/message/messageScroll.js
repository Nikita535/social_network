let friendMessage = document.getElementById('friendMessageScroll')

let page = 0
let isLoading = false;

$(friendMessage).scroll(function () {
    if ($(friendMessage).height() <= $(window).scrollTop() + $(window).height() && !isLoading) {
        loadFriendMessage()
        isLoading = true;
    }
});

$(function () {
    loadFriendMessage()
})

function loadFriendMessage() {
    let xhr = new XMLHttpRequest()
    let url = "http://localhost:8080/reloadMessageFriends/" + page
    xhr.open("GET", url)
    xhr.onload = function (ev) {
        let jsonResponse = JSON.parse(xhr.responseText);
        if (jsonResponse != null && jsonResponse[0].length > 0) {
            for (let i = 0; i < jsonResponse[0].length; i++) {
                let friend = jsonResponse[0][i]
                let profile = jsonResponse[0][i].profileInfo
                let message = jsonResponse[1][i] != null ? jsonResponse[1][i]["userFrom"]["id"] === userFrom["id"]
                    ? jsonResponse[1][i]["content"].length < 7 ? "Вы: " + jsonResponse[1][i]["content"]
                        : "Вы: " + jsonResponse[1][i]["content"].substr(0, 7) + "..."
                    : jsonResponse[1][i]["content"].length < 7 ? profile["name"] + ": " + jsonResponse[1][i]["content"]
                        : profile["name"] + ": " + jsonResponse[1][i]["content"].substr(0, 7) + "..." : "";

                let time = jsonResponse[1][i] != null ? jsonResponse[1][i].time.split(" ")[1].substring(0, 5) : "";

                let source = friend["image"] != null ? '/image/' + friend["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'


                friendMessage.innerHTML += "<div class=\"list-group lg-alt\">\n" +
                    "                            <a class=\"list-group-item media friendListener\" style=\"text-decoration: \" id=\""
                    + friend["username"] + "\">\n"
                    + "                                <div class=\"lv-avatar pull-left\">\n"
                    + "                                    <img class=\"img-avatar mr-2\" src=\""
                    + source + "\" alt=\"\">\n" + "                                </div>\n"
                    + "                                <div class=\"media-body\">\n"
                    + "                                    <div class=\"list-group-item-heading\">"
                    + profile["surname"] + " " + profile["name"] + "</div>\n"
                    + "<div class=\"mt-2 d-flex align-items-center justify-content-between\">"
                    + "<small class=\"list-group-item-text c-gray\">" + message + "</small> "
                    + "<small class=\"list-group-item-text c-gray\">" + time + "</small>"
                    + "</div>"
                    + "                                </div>\n"
                    + "                            </a>\n"
                    + "                        </div>"

            }

            // Можно изменить
            let allFriends = friendMessage.querySelectorAll(".friendListener")
            for (let j = 0; j < allFriends.length; j++)
                allFriends[j].addEventListener('click', getChatMessages, true)
            isLoading = false;
            page += 1
        }
    }
    xhr.send()
}
