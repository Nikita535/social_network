let friendMessage = document.getElementById('friendMessageScroll')

let page = 0
var isLoading = false;

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
                let profile = jsonResponse[0][i]
                let message = jsonResponse[1][i] != null ? jsonResponse[1][i]["content"]: "";
                let source = profile["user"]["image"] != null ? '/image/' +
                    profile["user"]["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'


                friendMessage.innerHTML +="<div class=\"list-group lg-alt\">\n" +
                    "                            <a class=\"list-group-item media\" style=\"text-decoration: \" href=\"/chat/" + profile["user"]["username"]+"\">\n" +
                    "                                <div class=\"lv-avatar pull-left\">\n" +
                    "                                    <img class=\"img-avatar mr-2\" src=\"" + source +"\" alt=\"\">\n" +
                    "                                </div>\n" +
                    "                                <div class=\"media-body\">\n" +
                    "                                    <div class=\"list-group-item-heading\">" + profile["surname"] + " " + profile["name"] + "</div>\n" +
                    "                                    <small class=\"list-group-item-text c-gray\">" + message + "</small>\n" +
                    "                                </div>\n" +
                    "                            </a>\n" +
                    "                        </div>"
            }
            console.log(page)
            isLoading = false;
            page += 1
        }
    }
    xhr.send()
}